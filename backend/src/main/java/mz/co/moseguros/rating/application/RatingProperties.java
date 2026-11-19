package mz.co.moseguros.rating.application;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração das cotações ({@code moseguros.rating}, S3-02 §2). Os tempos lêem-se em cada uso:
 * os testes podem alterá-los (ex.: {@code read-timeout}) sem reiniciar a aplicação.
 */
@ConfigurationProperties("moseguros.rating")
public class RatingProperties {

    /** Validade de uma cotação oferecida. */
    private Duration quoteValidity = Duration.ofDays(15);
    /** Prazo da fase de avaliação (todas as seguradoras em paralelo). */
    private Duration globalTimeout = Duration.ofSeconds(3);
    /** Janela da data de início (provisória, ver Questões). */
    private int maxStartDaysAhead = 60;
    /** Pedidos de cotação por utilizador por hora (provisório, ver Questões). */
    private int maxRequestsPerHour = 30;
    /** Dias antes do fim da validade em que o cliente é avisado (S6-04 §9.1, decisão D16). */
    private int quoteExpiryNoticeDays = 2;
    private final Executor executor = new Executor();
    private final External external = new External();
    /** Chave → segredo do HMAC das seguradoras externas (referência {@code config:<chave>}). */
    private Map<String, String> secrets = new LinkedHashMap<>();

    public static class Executor {
        private int coreSize = 8;
        private int maxSize = 32;
        private int queueCapacity = 100;

        public int getCoreSize() {
            return coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }

    public static class External {
        private Duration connectTimeout = Duration.ofSeconds(1);
        private Duration readTimeout = Duration.ofSeconds(2);
        private int maxResponseBytes = 65_536;
        private boolean allowHttp = false;
        private final CircuitBreaker circuitBreaker = new CircuitBreaker();
        private final Bulkhead bulkhead = new Bulkhead();

        public Duration getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Duration getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
        }

        public int getMaxResponseBytes() {
            return maxResponseBytes;
        }

        public void setMaxResponseBytes(int maxResponseBytes) {
            this.maxResponseBytes = maxResponseBytes;
        }

        public boolean isAllowHttp() {
            return allowHttp;
        }

        public void setAllowHttp(boolean allowHttp) {
            this.allowHttp = allowHttp;
        }

        public CircuitBreaker getCircuitBreaker() {
            return circuitBreaker;
        }

        public Bulkhead getBulkhead() {
            return bulkhead;
        }
    }

    public static class CircuitBreaker {
        private int slidingWindowSize = 20;
        private int minimumNumberOfCalls = 10;
        private float failureRateThreshold = 50;
        private Duration waitDurationInOpenState = Duration.ofSeconds(30);
        private int permittedCallsInHalfOpenState = 3;

        public int getSlidingWindowSize() {
            return slidingWindowSize;
        }

        public void setSlidingWindowSize(int slidingWindowSize) {
            this.slidingWindowSize = slidingWindowSize;
        }

        public int getMinimumNumberOfCalls() {
            return minimumNumberOfCalls;
        }

        public void setMinimumNumberOfCalls(int minimumNumberOfCalls) {
            this.minimumNumberOfCalls = minimumNumberOfCalls;
        }

        public float getFailureRateThreshold() {
            return failureRateThreshold;
        }

        public void setFailureRateThreshold(float failureRateThreshold) {
            this.failureRateThreshold = failureRateThreshold;
        }

        public Duration getWaitDurationInOpenState() {
            return waitDurationInOpenState;
        }

        public void setWaitDurationInOpenState(Duration waitDurationInOpenState) {
            this.waitDurationInOpenState = waitDurationInOpenState;
        }

        public int getPermittedCallsInHalfOpenState() {
            return permittedCallsInHalfOpenState;
        }

        public void setPermittedCallsInHalfOpenState(int permittedCallsInHalfOpenState) {
            this.permittedCallsInHalfOpenState = permittedCallsInHalfOpenState;
        }
    }

    public static class Bulkhead {
        private int maxConcurrentCalls = 20;

        public int getMaxConcurrentCalls() {
            return maxConcurrentCalls;
        }

        public void setMaxConcurrentCalls(int maxConcurrentCalls) {
            this.maxConcurrentCalls = maxConcurrentCalls;
        }
    }

    public Duration getQuoteValidity() {
        return quoteValidity;
    }

    public void setQuoteValidity(Duration quoteValidity) {
        this.quoteValidity = quoteValidity;
    }

    public Duration getGlobalTimeout() {
        return globalTimeout;
    }

    public void setGlobalTimeout(Duration globalTimeout) {
        this.globalTimeout = globalTimeout;
    }

    public int getMaxStartDaysAhead() {
        return maxStartDaysAhead;
    }

    public void setMaxStartDaysAhead(int maxStartDaysAhead) {
        this.maxStartDaysAhead = maxStartDaysAhead;
    }

    public int getQuoteExpiryNoticeDays() {
        return quoteExpiryNoticeDays;
    }

    public void setQuoteExpiryNoticeDays(int quoteExpiryNoticeDays) {
        this.quoteExpiryNoticeDays = quoteExpiryNoticeDays;
    }

    public int getMaxRequestsPerHour() {
        return maxRequestsPerHour;
    }

    public void setMaxRequestsPerHour(int maxRequestsPerHour) {
        this.maxRequestsPerHour = maxRequestsPerHour;
    }

    public Executor getExecutor() {
        return executor;
    }

    public External getExternal() {
        return external;
    }

    public Map<String, String> getSecrets() {
        return secrets;
    }

    public void setSecrets(Map<String, String> secrets) {
        this.secrets = secrets == null ? new LinkedHashMap<>() : new LinkedHashMap<>(secrets);
    }
}
