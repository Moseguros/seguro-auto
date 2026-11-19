package mz.co.moseguros.payment.application;

import java.util.UUID;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.GatewayRegistry;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.PaymentGateway;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Envio do C2B de uma intenção M-Pesa (S5-04 §2), numa thread {@code mpesa-c2b-*} do
 * {@code ExecutorAsyncDispatcher}, em {@code SYSTEM}:
 * <ol>
 *   <li><b>Transacção 1:</b> compare-and-set {@code CREATED → PENDING} ({@code dispatched_at}, a
 *       {@code third_party_reference} nova, evento {@code DISPATCHED}) só se a intenção ainda estiver
 *       {@code CREATED} e dentro do prazo; senão termina sem fazer nada (já foi enviada, ou expirou).</li>
 *   <li><b>Sem transacção:</b> lê a configuração activa e chama o adaptador
 *       ({@code MpesaGateway.initiate}: segredos, token, C2B). Configuração desactivada → {@code FAILED}
 *       {@code PROVIDER_CONFIG_ERROR}; um erro inesperado do adaptador → {@code UNKNOWN} (o pedido pode ter
 *       saído).</li>
 *   <li><b>Transacção 2:</b> aplica o resultado com o {@link IntentStateService} (e o
 *       {@code PROVIDER_RESPONSE} mascarado).</li>
 * </ol>
 * <b>Nunca se repete o C2B de uma intenção</b>: é este o único caminho até ao C2B e só depois do
 * compare-and-set; duas chamadas em paralelo para a mesma intenção dão um só envio.
 */
@Component
public class MpesaC2bJob {

    static final String ACTOR = "MpesaC2bJob";
    private static final Logger log = LoggerFactory.getLogger(MpesaC2bJob.class);

    private final PaymentIntentRepository intents;
    private final IntentStateService states;
    private final GatewayRegistry gateways;
    private final PaymentSystemLookups lookups;
    private final TransactionTemplate tx;

    public MpesaC2bJob(PaymentIntentRepository intents, IntentStateService states, GatewayRegistry gateways,
                       PaymentSystemLookups lookups, PlatformTransactionManager transactionManager) {
        this.intents = intents;
        this.states = states;
        this.gateways = gateways;
        this.lookups = lookups;
        this.tx = new TransactionTemplate(transactionManager);
    }

    @RunAsSystem
    public void run(UUID intentId) {
        PaymentIntent snapshot = tx.execute(status -> intents.findById(intentId).orElse(null));
        if (snapshot == null) {
            log.warn("M-Pesa dispatch of an unknown intent intentId={}", intentId);
            return;
        }
        PaymentGateway gateway = gateways.find(snapshot.getMethod()).orElse(null);
        String thirdPartyReference = gateway == null ? null : gateway.newThirdPartyReference();

        // Transacção 1: compare-and-set CREATED → PENDING.
        Boolean started = tx.execute(status -> states.startDispatch(intentId, thirdPartyReference, ACTOR));
        if (!Boolean.TRUE.equals(started)) {
            log.debug("Payment intent already dispatched or expired intentId={}", intentId);
            return;
        }
        PaymentIntent intent = tx.execute(status -> intents.findById(intentId).orElseThrow());

        // Sem transacção: configuração, segredos, token e C2B.
        InitiationResult result;
        MethodConfig config = lookups.enabledConfig(intent.getInsurerId(), intent.getMethod()).orElse(null);
        if (gateway == null || config == null) {
            log.warn("Payment dispatch without gateway or active configuration intentId={} method={}", intentId,
                    intent.getMethod());
            result = InitiationResult.failed(FailureCode.PROVIDER_CONFIG_ERROR,
                    JsonNodeFactory.instance.objectNode().put("error", "METHOD_NOT_CONFIGURED"));
        } else {
            try {
                result = gateway.initiate(intent, config);
            } catch (RuntimeException e) {
                log.warn("Payment gateway error intentId={} method={} ({})", intentId, intent.getMethod(),
                        e.getClass().getSimpleName());
                result = new InitiationResult(InitiationResult.Outcome.UNKNOWN, null, null, null, null, null,
                        JsonNodeFactory.instance.objectNode().put("error", e.getClass().getSimpleName()));
            }
        }

        // Transacção 2: aplica o resultado.
        InitiationResult applied = result;
        tx.executeWithoutResult(status -> states.applyInitiation(intentId, applied, ACTOR));
    }
}
