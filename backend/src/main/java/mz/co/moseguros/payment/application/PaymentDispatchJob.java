package mz.co.moseguros.payment.application;

import java.util.UUID;

import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.application.gateway.AsyncDispatcher;
import mz.co.moseguros.payment.application.gateway.GatewayRegistry;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.PaymentGateway;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Envio de uma intenção assíncrona (M-Pesa), numa thread do {@link AsyncDispatcher}, em {@code SYSTEM}
 * (S5-02 §4, passo 11): compare-and-set {@code CREATED → PENDING} ({@code dispatched_at}), chamada ao
 * adaptador fora de transacção e aplicação do resultado. Uma excepção do adaptador deixa a intenção em
 * {@code PENDING}: o S5-04 trata as consultas de estado e os prazos.
 */
public class PaymentDispatchJob {

    static final String ACTOR = "PaymentDispatchJob";
    private static final Logger log = LoggerFactory.getLogger(PaymentDispatchJob.class);

    private final PaymentIntentRepository intents;
    private final IntentStateService states;
    private final GatewayRegistry gateways;
    private final PaymentSystemLookups lookups;
    private final TransactionTemplate tx;

    public PaymentDispatchJob(PaymentIntentRepository intents, IntentStateService states, GatewayRegistry gateways,
                              PaymentSystemLookups lookups, PlatformTransactionManager transactionManager) {
        this.intents = intents;
        this.states = states;
        this.gateways = gateways;
        this.lookups = lookups;
        this.tx = new TransactionTemplate(transactionManager);
    }

    @RunAsSystem
    public void dispatch(UUID intentId) {
        Boolean started = tx.execute(status -> states.startDispatch(intentId, ACTOR));
        if (!Boolean.TRUE.equals(started)) {
            return;
        }
        PaymentIntent intent = tx.execute(status -> intents.findById(intentId).orElseThrow());
        PaymentGateway gateway = gateways.find(intent.getMethod()).orElse(null);
        MethodConfig config = lookups.configById(intent.getMethodConfigId()).orElse(null);
        if (gateway == null || config == null) {
            log.warn("Payment dispatch without gateway or configuration intentId={} method={}", intentId,
                    intent.getMethod());
            tx.executeWithoutResult(status -> states.markFailed(intentId, FailureCode.PROVIDER_CONFIG_ERROR, ACTOR));
            return;
        }
        InitiationResult result;
        try {
            result = gateway.initiate(intent, config);
        } catch (RuntimeException e) {
            log.warn("Payment gateway error intentId={} method={} ({})", intentId, intent.getMethod(),
                    e.getClass().getSimpleName());
            return;
        }
        tx.executeWithoutResult(status -> states.applyInitiation(intentId, result, ACTOR));
    }

    /** Beans do envio assíncrono. */
    @Configuration(proxyBeanMethods = false)
    static class Config {

        @Bean
        PaymentDispatchJob paymentDispatchJob(PaymentIntentRepository intents, IntentStateService states,
                                              GatewayRegistry gateways, PaymentSystemLookups lookups,
                                              PlatformTransactionManager transactionManager) {
            return new PaymentDispatchJob(intents, states, gateways, lookups, transactionManager);
        }

        @Bean
        AsyncDispatcher paymentAsyncDispatcher(PaymentDispatchJob job,
                                               @Value("${moseguros.payment.dispatch-threads:4}") int threads) {
            return new AsyncDispatcher(job::dispatch, threads);
        }
    }
}
