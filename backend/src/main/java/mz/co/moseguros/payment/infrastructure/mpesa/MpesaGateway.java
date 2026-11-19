package mz.co.moseguros.payment.infrastructure.mpesa;

import java.security.PublicKey;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.PaymentAlerts;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.DispatchMode;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.PaymentGateway;
import mz.co.moseguros.payment.application.gateway.RefundRequest;
import mz.co.moseguros.payment.application.gateway.RefundResult;
import mz.co.moseguros.payment.application.gateway.StatusResult;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaClient.Endpoint;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaClient.Failure;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaClient.MpesaCallException;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaResponseMapper.Signal;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaTokenFactory.InvalidPublicKeyException;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.secrets.SecretStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adaptador M-Pesa (S5-04): implementação da API Vodacom Moçambique (C2B, consulta de estado,
 * reversão) com as credenciais <b>da seguradora</b> (código de serviço dela: o dinheiro vai para a conta
 * dela). Em desenvolvimento e testes aponta para o {@code mpesa-mock}.
 *
 * <p>Assíncrono ({@link DispatchMode#ASYNC_AFTER_COMMIT}): o {@code MpesaC2bJob} grava a
 * {@code third_party_reference} ({@link #newThirdPartyReference()}) e passa a intenção a {@code PENDING}
 * antes de chamar {@link #initiate}; o C2B nunca se repete para a mesma intenção. Um erro antes de o
 * pedido sair (configuração, segredo, chave pública, circuito aberto) dá {@code FAILED}; depois de começar
 * o envio (timeout, rede, corpo ilegível) dá {@code UNKNOWN}, resolvido pela consulta.
 *
 * <p>O {@code payment_event} recebe os {@code input_*} do pedido (telemóvel mascarado) e os
 * {@code output_*} da resposta; nunca o token, a {@code apiKey}, a {@code securityCredential} nem a chave
 * pública.
 */
@Component
@ConditionalOnProperty(name = "moseguros.payment.mpesa.adapter", havingValue = "vodacom", matchIfMissing = true)
public class MpesaGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MpesaGateway.class);

    private final MpesaClient client;
    private final MpesaTokenFactory tokens;
    private final SecretStore secrets;
    private final PaymentAlerts alerts;

    public MpesaGateway(MpesaClient client, MpesaTokenFactory tokens, SecretStore secrets, PaymentAlerts alerts) {
        this.client = client;
        this.tokens = tokens;
        this.secrets = secrets;
        this.alerts = alerts;
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.MPESA;
    }

    @Override
    public DispatchMode dispatchMode() {
        return DispatchMode.ASYNC_AFTER_COMMIT;
    }

    /** 12 caracteres {@code [A-Z0-9]} aleatórios ({@code SecureRandom}). */
    @Override
    public String newThirdPartyReference() {
        return MpesaRequests.newThirdPartyReference();
    }

    // --- C2B ---

    @Override
    public InitiationResult initiate(PaymentIntent intent, MethodConfig config) {
        String thirdPartyReference = intent.getThirdPartyReference();
        if (thirdPartyReference == null || intent.getPayerMsisdn() == null) {
            throw new IllegalStateException("M-Pesa intent " + intent.getId() + " was not prepared for dispatch");
        }
        Prepared prepared;
        try {
            prepared = prepare(config, Port.C2B);
        } catch (ConfigProblem problem) {
            return preSendFailure(intent, config, problem);
        }
        MpesaRequests.C2b body = new MpesaRequests.C2b(intent.getReference().replace("-", ""),
                intent.getPayerMsisdn().value(), amount(intent.amount()), thirdPartyReference,
                prepared.serviceProviderCode());
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("operation", "C2B");
        payload.set("request", body.masked());
        long start = System.nanoTime();
        MpesaResponse response;
        try {
            response = client.c2b(config.insurerId(), prepared.endpoint(), prepared.token(), body,
                    prepared.c2bTimeoutSeconds());
        } catch (MpesaCallException e) {
            payload.put("error", e.failure().name());
            logCall("C2B", intent, null, start, e.failure().name());
            if (e.failure() == Failure.CIRCUIT_OPEN) {
                return InitiationResult.failed(FailureCode.PROVIDER_UNAVAILABLE, payload);
            }
            return new InitiationResult(InitiationResult.Outcome.UNKNOWN, null, null, null, thirdPartyReference,
                    null, payload);
        }
        payload.set("response", response.toJson());
        logCall("C2B", intent, response.responseCode(), start, null);
        MpesaResponseMapper.C2bMapping mapping = MpesaResponseMapper.c2b(response.responseCode());
        signal(mapping.signal(), "C2B", intent, config, response.responseCode());
        if (mapping.outcome() == InitiationResult.Outcome.SUCCEEDED) {
            alerts.clearConfigError(config);
        }
        return new InitiationResult(mapping.outcome(), mapping.failureCode(), response.transactionIdOrNull(),
                response.conversationIdOrNull(), thirdPartyReference, null, payload);
    }

    // --- Consulta ---

    @Override
    public StatusResult queryStatus(PaymentIntent intent, MethodConfig config) {
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("operation", "QUERY");
        Prepared prepared;
        try {
            prepared = prepare(config, Port.QUERY);
        } catch (ConfigProblem problem) {
            payload.put("error", problem.reason());
            if (problem.alert()) {
                alerts.configError(config, problem.reason());
            }
            log.warn("M-Pesa status query not sent intentId={} reference={} reason={}", intent.getId(),
                    intent.getReference(), problem.reason());
            return StatusResult.stillUnknown(payload);
        }
        String queryReference = intent.getProviderTxId() != null ? intent.getProviderTxId()
                : intent.getThirdPartyReference();
        if (queryReference == null) {
            payload.put("error", "NO_QUERY_REFERENCE");
            log.error("M-Pesa intent without a query reference intentId={}", intent.getId());
            return StatusResult.stillUnknown(payload);
        }
        MpesaRequests.Query query = new MpesaRequests.Query(queryReference, prepared.serviceProviderCode(),
                newThirdPartyReference());
        payload.set("request", query.masked());
        long start = System.nanoTime();
        MpesaResponse response;
        try {
            response = client.query(config.insurerId(), prepared.endpoint(), prepared.token(), query);
        } catch (MpesaCallException e) {
            payload.put("error", e.failure().name());
            logCall("QUERY", intent, null, start, e.failure().name());
            return StatusResult.stillUnknown(payload);
        }
        payload.set("response", response.toJson());
        logCall("QUERY", intent, response.responseCode(), start, null);
        MpesaResponseMapper.QueryMapping mapping = MpesaResponseMapper.query(response.responseCode(),
                response.transactionStatus());
        signal(mapping.signal(), "QUERY", intent, config, response.responseCode());
        return new StatusResult(mapping.outcome(), mapping.failureCode(), response.transactionIdOrNull(), payload);
    }

    // --- Reversão ---

    /**
     * Reversão (S5-04 §7) de uma transacção {@code SUCCEEDED}. Nenhum endpoint nem job a usa nesta
     * tarefa: as devoluções fazem-se fora da plataforma (S7).
     */
    @Override
    public RefundResult refund(RefundRequest request, MethodConfig config) {
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("operation", "REVERSAL");
        Prepared prepared;
        String credential;
        String initiator;
        try {
            prepared = prepare(config, Port.REVERSAL);
            initiator = text(config.settings(), "initiatorIdentifier")
                    .orElseThrow(() -> new ConfigProblem("INITIATOR_MISSING", true));
            credential = secret(config, SecretField.MPESA_SECURITY_CREDENTIAL)
                    .orElseThrow(() -> new ConfigProblem("SECRET_MISSING", true));
        } catch (ConfigProblem problem) {
            payload.put("error", problem.reason());
            if (problem.alert()) {
                alerts.configError(config, problem.reason());
            }
            return new RefundResult(RefundResult.Outcome.FAILED, null, null, payload);
        }
        MpesaRequests.Reversal body = new MpesaRequests.Reversal(request.providerTxId(), credential, initiator,
                newThirdPartyReference(), prepared.serviceProviderCode(), amount(request.amount()));
        payload.set("request", body.masked());
        long start = System.nanoTime();
        MpesaResponse response;
        try {
            response = client.reversal(config.insurerId(), prepared.endpoint(), prepared.token(), body);
        } catch (MpesaCallException e) {
            payload.put("error", e.failure().name());
            log.info("M-Pesa REVERSAL intentId={} result={} durationMs={}", request.intentId(), e.failure(),
                    millis(start));
            RefundResult.Outcome outcome = e.failure().attempted() ? RefundResult.Outcome.UNKNOWN
                    : RefundResult.Outcome.FAILED;
            return new RefundResult(outcome, null, null, payload);
        }
        payload.set("response", response.toJson());
        log.info("M-Pesa REVERSAL intentId={} code={} durationMs={}", request.intentId(), response.responseCode(),
                millis(start));
        MpesaResponseMapper.ReversalMapping mapping = MpesaResponseMapper.reversal(response.responseCode());
        if (mapping.signal() == Signal.CONFIG_ERROR) {
            alerts.configError(config, response.responseCode());
        }
        return new RefundResult(mapping.outcome(),
                mapping.outcome() == RefundResult.Outcome.SUCCEEDED ? response.transactionIdOrNull() : null,
                response.responseCode(), payload);
    }

    // --- Apoio ---

    /** Montante em meticais: {@code "2999"} se os centavos forem {@code 00}, senão {@code "2999.50"}. */
    static String amount(Money money) {
        long centavos = money.amount();
        long meticais = centavos / 100;
        long rest = centavos % 100;
        return rest == 0 ? Long.toString(meticais) : meticais + "." + (rest < 10 ? "0" : "") + rest;
    }

    private enum Port {
        C2B("c2b"), QUERY("query"), REVERSAL("reversal");

        private final String field;

        Port(String field) {
            this.field = field;
        }
    }

    /** Dados prontos para um pedido (o token é deste pedido). */
    private record Prepared(Endpoint endpoint, String serviceProviderCode, int c2bTimeoutSeconds, String token) {
    }

    /** Problema antes de o pedido sair: nada foi enviado. */
    private static final class ConfigProblem extends Exception {

        private final String reason;
        private final boolean alert;
        private final FailureCode failureCode;

        ConfigProblem(String reason, boolean alert) {
            this(reason, alert, FailureCode.PROVIDER_CONFIG_ERROR);
        }

        ConfigProblem(String reason, boolean alert, FailureCode failureCode) {
            super(reason, null, false, false);
            this.reason = reason;
            this.alert = alert;
            this.failureCode = failureCode;
        }

        String reason() {
            return reason;
        }

        boolean alert() {
            return alert;
        }

        FailureCode failureCode() {
            return failureCode;
        }
    }

    private Prepared prepare(MethodConfig config, Port port) throws ConfigProblem {
        JsonNode settings = config.settings();
        String host = text(settings, "host").orElseThrow(() -> new ConfigProblem("SETTINGS_INVALID", true));
        String origin = text(settings, "origin").orElseThrow(() -> new ConfigProblem("SETTINGS_INVALID", true));
        String serviceProviderCode = text(settings, "serviceProviderCode")
                .orElseThrow(() -> new ConfigProblem("SETTINGS_INVALID", true));
        JsonNode portNode = settings == null ? null : settings.path("ports").path(port.field);
        if (portNode == null || !portNode.canConvertToInt() || portNode.asInt() < 1 || portNode.asInt() > 65535) {
            throw new ConfigProblem("SETTINGS_INVALID", true);
        }
        int timeout = settings.path("c2bTimeoutSeconds").canConvertToInt() ? settings.path("c2bTimeoutSeconds").asInt()
                : 90;
        String apiKey;
        String publicKeyText;
        try {
            apiKey = secret(config, SecretField.MPESA_API_KEY).orElseThrow(() -> new ConfigProblem("SECRET_MISSING",
                    true));
            publicKeyText = secret(config, SecretField.MPESA_PUBLIC_KEY)
                    .orElseThrow(() -> new ConfigProblem("SECRET_MISSING", true));
        } catch (RuntimeException e) {
            // Cofre indisponível: problema da plataforma, não da seguradora.
            throw new ConfigProblem("SECRET_STORE_UNAVAILABLE", false, FailureCode.PROVIDER_UNAVAILABLE);
        }
        PublicKey publicKey;
        try {
            publicKey = tokens.publicKey(config.id(), config.version(), publicKeyText);
        } catch (InvalidPublicKeyException e) {
            throw new ConfigProblem("PUBLIC_KEY_INVALID", true);
        }
        String token = tokens.token(publicKey, apiKey);
        return new Prepared(new Endpoint(host, portNode.asInt(), origin), serviceProviderCode, timeout, token);
    }

    private Optional<String> secret(MethodConfig config, SecretField field) {
        return config.secretRef(field).flatMap(ref -> secrets.get(ref.path())).filter(v -> !v.isBlank());
    }

    private static Optional<String> text(JsonNode settings, String field) {
        if (settings == null) {
            return Optional.empty();
        }
        JsonNode node = settings.path(field);
        return node.isTextual() && !node.asText().isBlank() ? Optional.of(node.asText()) : Optional.empty();
    }

    private InitiationResult preSendFailure(PaymentIntent intent, MethodConfig config, ConfigProblem problem) {
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("operation", "C2B");
        payload.put("error", problem.reason());
        if (problem.alert()) {
            alerts.configError(config, problem.reason());
        }
        log.warn("M-Pesa C2B not sent intentId={} reference={} reason={}", intent.getId(), intent.getReference(),
                problem.reason());
        return InitiationResult.failed(problem.failureCode(), payload);
    }

    private void signal(Signal signal, String operation, PaymentIntent intent, MethodConfig config, String code) {
        switch (signal) {
            case CONFIG_ERROR -> alerts.configError(config, code);
            case REQUEST_ERROR -> log.error("M-Pesa {} rejected our request intentId={} reference={} code={}",
                    operation, intent.getId(), intent.getReference(), code);
            case UNEXPECTED_CODE -> log.warn("M-Pesa {} unexpected response code intentId={} reference={} code={}",
                    operation, intent.getId(), intent.getReference(), code);
            case NONE -> {
                // nada
            }
        }
    }

    private static void logCall(String operation, PaymentIntent intent, String code, long start, String failure) {
        log.info("M-Pesa {} intentId={} reference={} msisdn={} code={} failure={} durationMs={}", operation,
                intent.getId(), intent.getReference(),
                intent.getPayerMsisdn() == null ? "-" : intent.getPayerMsisdn().masked(), code, failure,
                millis(start));
    }

    private static long millis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
