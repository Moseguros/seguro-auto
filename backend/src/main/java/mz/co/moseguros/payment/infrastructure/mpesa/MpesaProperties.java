package mz.co.moseguros.payment.infrastructure.mpesa;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Parâmetros técnicos do adaptador M-Pesa ({@code moseguros.payment.mpesa}, S5-04 §1 e §9). Os dados
 * de cada seguradora (código de serviço, anfitrião, portas, {@code Origin}, prazo do C2B) estão na
 * configuração dela ({@code insurer_payment_method.settings}); as chaves no cofre.
 *
 * <p>{@code timeout-cap} e {@code adapter: test-stub} só existem para os testes: fora dos perfis
 * {@code local} e {@code test} a aplicação não arranca com eles ({@link MpesaConfig}).
 */
@ConfigurationProperties("moseguros.payment.mpesa")
public class MpesaProperties {

    /** Adaptador do método {@code MPESA}. */
    public enum Adapter {
        /** API oficial da Vodacom (o {@code mpesa-mock} em desenvolvimento e testes). */
        VODACOM,
        /** Só nos testes do S5-02 que não falam com nenhum simulador ({@code TestGateway}, perfil {@code test}). */
        TEST_STUB
    }

    private Adapter adapter = Adapter.VODACOM;

    /** Tempo para estabelecer a ligação (todas as operações). */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /** Tempo de leitura da consulta de estado e da reversão (o do C2B é {@code c2bTimeoutSeconds}). */
    private Duration readTimeout = Duration.ofSeconds(15);

    /**
     * Só em testes: limita o tempo de leitura do C2B (o schema exige {@code c2bTimeoutSeconds ≥ 30}).
     * {@code null} = sem limite.
     */
    private Duration timeoutCap;

    /** Circuit breaker por seguradora ({@code mpesa-<insurerId>}): janela, limiar e tempo aberto. */
    private int circuitBreakerWindow = 20;
    private float circuitBreakerFailureRate = 50f;
    private Duration circuitBreakerOpenFor = Duration.ofSeconds(60);

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter == null ? Adapter.VODACOM : adapter;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = positive(connectTimeout, "connect-timeout");
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = positive(readTimeout, "read-timeout");
    }

    public Duration getTimeoutCap() {
        return timeoutCap;
    }

    public void setTimeoutCap(Duration timeoutCap) {
        this.timeoutCap = timeoutCap == null ? null : positive(timeoutCap, "timeout-cap");
    }

    public int getCircuitBreakerWindow() {
        return circuitBreakerWindow;
    }

    public void setCircuitBreakerWindow(int circuitBreakerWindow) {
        if (circuitBreakerWindow < 1) {
            throw new IllegalArgumentException("moseguros.payment.mpesa.circuit-breaker-window must be at least 1");
        }
        this.circuitBreakerWindow = circuitBreakerWindow;
    }

    public float getCircuitBreakerFailureRate() {
        return circuitBreakerFailureRate;
    }

    public void setCircuitBreakerFailureRate(float circuitBreakerFailureRate) {
        if (circuitBreakerFailureRate <= 0 || circuitBreakerFailureRate > 100) {
            throw new IllegalArgumentException("moseguros.payment.mpesa.circuit-breaker-failure-rate must be in ]0, 100]");
        }
        this.circuitBreakerFailureRate = circuitBreakerFailureRate;
    }

    public Duration getCircuitBreakerOpenFor() {
        return circuitBreakerOpenFor;
    }

    public void setCircuitBreakerOpenFor(Duration circuitBreakerOpenFor) {
        this.circuitBreakerOpenFor = positive(circuitBreakerOpenFor, "circuit-breaker-open-for");
    }

    /** Tempo de leitura efectivo do C2B: {@code c2bTimeoutSeconds}, limitado por {@code timeout-cap}. */
    public Duration c2bReadTimeout(int c2bTimeoutSeconds) {
        Duration configured = Duration.ofSeconds(c2bTimeoutSeconds);
        return timeoutCap != null && timeoutCap.compareTo(configured) < 0 ? timeoutCap : configured;
    }

    /** Valida as propriedades só de testes (chamado no arranque). */
    void validate(boolean localOrTest, boolean test) {
        if (timeoutCap != null && !localOrTest) {
            throw new IllegalStateException("moseguros.payment.mpesa.timeout-cap is only allowed in tests");
        }
        if (adapter == Adapter.TEST_STUB && !test) {
            throw new IllegalStateException("moseguros.payment.mpesa.adapter=test-stub is only allowed in tests");
        }
    }

    private static Duration positive(Duration value, String name) {
        if (value == null || value.isNegative() || value.isZero()) {
            throw new IllegalArgumentException("moseguros.payment.mpesa." + name + " must be positive");
        }
        return value;
    }
}
