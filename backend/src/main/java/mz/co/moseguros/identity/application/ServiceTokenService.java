package mz.co.moseguros.identity.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.security.JwtClaims;
import mz.co.moseguros.shared.security.JwtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Tokens de serviço (S5-09 §1, RFC 6749 §4.4 {@code client_credentials}) para os executáveis
 * internos. JWT RS256 com a chave do S1-02, {@code aud = moseguros-internal}, {@code type = SERVICE}
 * e permissões = âmbitos concedidos; vale 15 min. Só servem em {@code /internal/**}
 * ({@code InternalSecurityConfig}).
 *
 * <p>O segredo compara-se em tempo constante ({@link MessageDigest#isEqual} sobre o SHA-256 de cada
 * lado). As falhas registam o {@code clientId}, nunca o segredo.
 */
@Service
@EnableConfigurationProperties(ServiceClientProperties.class)
public class ServiceTokenService {

    private static final Logger log = LoggerFactory.getLogger(ServiceTokenService.class);

    /** Validade do token de serviço. */
    public static final Duration TOKEN_TTL = Duration.ofMinutes(15);

    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_SCOPE = "invalid_scope";
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    public static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";

    private final ServiceClientProperties properties;
    private final JwtProperties jwt;
    private final JwtEncoder encoder;
    private final Clock clock;

    public ServiceTokenService(ServiceClientProperties properties, JwtProperties jwt, JwtEncoder encoder, Clock clock,
                               Environment environment) {
        this.properties = properties;
        this.jwt = jwt;
        this.encoder = encoder;
        this.clock = clock;
        properties.check(environment);
        properties.serviceClients().forEach((id, client) -> {
            if (!client.enabled()) {
                log.warn("Service client '{}' has no secret: it is disabled", id);
            }
        });
    }

    /** Token emitido. */
    public record ServiceToken(String accessToken, long expiresIn, String scope) {

        @Override
        public String toString() {
            return "ServiceToken[accessToken=***, expiresIn=" + expiresIn + ", scope=" + scope + "]";
        }
    }

    /** Erro do RFC 6749 §5.2 ({@code invalid_client}, {@code invalid_scope}…). */
    public static class ServiceTokenException extends RuntimeException {

        private final String error;

        public ServiceTokenException(String error) {
            super(error);
            this.error = error;
        }

        public String error() {
            return error;
        }
    }

    /**
     * Autentica o cliente e emite o token.
     *
     * @param requestedScope âmbitos separados por espaços; {@code null} ou vazio = todos os do cliente
     * @throws ServiceTokenException {@code invalid_client} ou {@code invalid_scope}
     */
    public ServiceToken issue(String clientId, String clientSecret, String requestedScope) {
        ServiceClientProperties.Client client = clientId == null ? null : properties.serviceClients().get(clientId);
        if (client == null || !client.enabled() || clientSecret == null
                || !MessageDigest.isEqual(sha256(client.secret()), sha256(clientSecret))) {
            log.warn("Service token refused: invalid client credentials (clientId={})", printable(clientId));
            throw new ServiceTokenException(INVALID_CLIENT);
        }
        List<String> scopes;
        if (requestedScope == null || requestedScope.isBlank()) {
            scopes = client.scopes();
        } else {
            Set<String> requested = new LinkedHashSet<>(Arrays.asList(requestedScope.trim().split("\\s+")));
            if (!client.scopes().containsAll(requested)) {
                log.warn("Service token refused: scope not allowed (clientId={})", clientId);
                throw new ServiceTokenException(INVALID_SCOPE);
            }
            scopes = List.copyOf(requested);
        }
        Instant now = clock.instant();
        String scope = String.join(" ", scopes);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwt.issuer())
                .audience(List.of(jwt.internalAudience()))
                .subject(clientId)
                .issuedAt(now)
                .expiresAt(now.plus(TOKEN_TTL))
                .id(Ids.newId().toString())
                .claim(JwtClaims.TYPE, JwtClaims.TYPE_SERVICE)
                .claim(JwtClaims.SCOPE, scope)
                .claim(JwtClaims.PERMISSIONS, scopes)
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).keyId(jwt.keyId()).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        log.info("Service token issued (clientId={}, scope={})", clientId, scope);
        return new ServiceToken(token, TOKEN_TTL.toSeconds(), scope);
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    /** O {@code clientId} vem do pedido: só caracteres seguros e no máximo 64 no log. */
    private static String printable(String clientId) {
        if (clientId == null) {
            return null;
        }
        String safe = clientId.replaceAll("[^A-Za-z0-9._-]", "?");
        return safe.length() > 64 ? safe.substring(0, 64) + "..." : safe;
    }
}
