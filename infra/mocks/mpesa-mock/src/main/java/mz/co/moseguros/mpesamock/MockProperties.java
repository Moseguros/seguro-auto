package mz.co.moseguros.mpesamock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Configuração do simulador ({@code mock.*}, variáveis {@code MOCK_*}). Os atrasos podem mudar
 * em execução por {@code PUT /__mock/config}; por isso são {@code volatile}.
 */
@ConfigurationProperties("mock")
public class MockProperties {

    private final Ports ports = new Ports();
    private String apiKey = "dev-mpesa-api-key";
    private String allowedOrigin = "developer.mpesa.vm.co.mz";
    private String serviceProviderCode = "171717";
    private String initiatorIdentifier = "dev-initiator";
    private String securityCredential = "dev-security-credential";
    private Resource privateKey;
    private volatile int defaultDelaySeconds = 2;
    private volatile int delay000Seconds = 5;
    private volatile int slowDelaySeconds = 120;
    private volatile int failureDelaySeconds = 2;

    public static class Ports {
        private int query = 18353;
        private int reversal = 18354;

        public int getQuery() {
            return query;
        }

        public void setQuery(int query) {
            this.query = query;
        }

        public int getReversal() {
            return reversal;
        }

        public void setReversal(int reversal) {
            this.reversal = reversal;
        }
    }

    public Ports getPorts() {
        return ports;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    public String getServiceProviderCode() {
        return serviceProviderCode;
    }

    public void setServiceProviderCode(String serviceProviderCode) {
        this.serviceProviderCode = serviceProviderCode;
    }

    public String getInitiatorIdentifier() {
        return initiatorIdentifier;
    }

    public void setInitiatorIdentifier(String initiatorIdentifier) {
        this.initiatorIdentifier = initiatorIdentifier;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public void setSecurityCredential(String securityCredential) {
        this.securityCredential = securityCredential;
    }

    public Resource getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }

    public int getDefaultDelaySeconds() {
        return defaultDelaySeconds;
    }

    public void setDefaultDelaySeconds(int defaultDelaySeconds) {
        this.defaultDelaySeconds = defaultDelaySeconds;
    }

    public int getDelay000Seconds() {
        return delay000Seconds;
    }

    public void setDelay000Seconds(int delay000Seconds) {
        this.delay000Seconds = delay000Seconds;
    }

    public int getSlowDelaySeconds() {
        return slowDelaySeconds;
    }

    public void setSlowDelaySeconds(int slowDelaySeconds) {
        this.slowDelaySeconds = slowDelaySeconds;
    }

    public int getFailureDelaySeconds() {
        return failureDelaySeconds;
    }

    public void setFailureDelaySeconds(int failureDelaySeconds) {
        this.failureDelaySeconds = failureDelaySeconds;
    }
}
