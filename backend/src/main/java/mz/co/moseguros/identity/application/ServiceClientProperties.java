package mz.co.moseguros.identity.application;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * Clientes de serviço ({@code moseguros.security.service-clients}, S5-09): os executáveis internos
 * (o {@code payment-file-worker}) que pedem um token com {@code client_credentials} em
 * {@code POST /internal/v1/auth/token}. Não há tabela: a configuração é a fonte.
 *
 * <p>Fora dos perfis {@code local} e {@code test}: segredo vazio = cliente desactivado (aviso no
 * arranque); segredo com menos de {@value #MIN_SECRET_LENGTH} caracteres = a aplicação não arranca.
 *
 * @param serviceClients id do cliente → segredo e âmbitos permitidos
 */
@ConfigurationProperties(prefix = "moseguros.security")
public record ServiceClientProperties(Map<String, Client> serviceClients) {

    public static final int MIN_SECRET_LENGTH = 32;

    public ServiceClientProperties {
        serviceClients = serviceClients == null ? Map.of() : Map.copyOf(serviceClients);
    }

    /**
     * Um cliente de serviço.
     *
     * @param secret segredo partilhado (vazio = desactivado)
     * @param scopes âmbitos que o cliente pode pedir; também são as permissões do token
     */
    public record Client(String secret, List<String> scopes) {

        public Client {
            scopes = scopes == null ? List.of() : List.copyOf(scopes);
        }

        public boolean enabled() {
            return secret != null && !secret.isBlank();
        }

        @Override
        public String toString() {
            return "Client[secret=***, scopes=" + scopes + "]";
        }
    }

    /** Segredo curto fora de {@code local}/{@code test} → excepção (o arranque falha). */
    void check(Environment environment) {
        if (environment.acceptsProfiles(Profiles.of("local", "test"))) {
            return;
        }
        serviceClients.forEach((id, client) -> {
            if (client.enabled() && client.secret().length() < MIN_SECRET_LENGTH) {
                throw new IllegalStateException("moseguros.security.service-clients." + id + ".secret must have at "
                        + "least " + MIN_SECRET_LENGTH + " characters outside the 'local' and 'test' profiles");
            }
        });
    }

    @Override
    public String toString() {
        return "ServiceClientProperties" + serviceClients;
    }
}
