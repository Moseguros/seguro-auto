package mz.co.moseguros.identity.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import mz.co.moseguros.identity.application.ServiceTokenService;
import mz.co.moseguros.identity.application.ServiceTokenService.ServiceToken;
import mz.co.moseguros.identity.application.ServiceTokenService.ServiceTokenException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token de serviço dos executáveis internos (S5-09 §1): {@code client_credentials} do RFC 6749 §4.4,
 * para o worker poder usar um cliente OAuth2 normal. Por isso os erros seguem o RFC 6749 §5.2
 * ({@code {"error": "…"}}) e não {@code application/problem+json} (excepção ao {@code AGENTS.md}
 * §4, só neste endpoint). Fora do {@code api/openapi.yaml} ({@code springdoc.paths-to-exclude}).
 *
 * <p>A autenticação Basic verifica-se aqui: a cadeia de {@code /internal/**} deixa este caminho sem
 * JWT ({@code InternalSecurityConfig}).
 */
@RestController
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class ServiceTokenController {

    static final String PATH = "/internal/v1/auth/token";
    static final String REALM = "Basic realm=\"moseguros-internal\"";

    private final ServiceTokenService tokens;

    public ServiceTokenController(ServiceTokenService tokens) {
        this.tokens = tokens;
    }

    @PostMapping(PATH)
    @SecurityRequirements
    @Operation(operationId = "issueServiceToken", summary = "Token de serviço (client credentials, interno)")
    public ResponseEntity<Map<String, Object>> issueServiceToken(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || !MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(
                parse(contentType))) {
            return error(HttpStatus.BAD_REQUEST, ServiceTokenService.INVALID_REQUEST);
        }
        String[] credentials = basic(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (credentials == null) {
            return error(HttpStatus.UNAUTHORIZED, ServiceTokenService.INVALID_CLIENT);
        }
        String[] grantTypes = request.getParameterValues("grant_type");
        String[] scopes = request.getParameterValues("scope");
        if (grantTypes == null || grantTypes.length != 1 || grantTypes[0].isBlank()
                || (scopes != null && scopes.length != 1)) {
            return error(HttpStatus.BAD_REQUEST, ServiceTokenService.INVALID_REQUEST);
        }
        if (!ServiceTokenService.GRANT_CLIENT_CREDENTIALS.equals(grantTypes[0])) {
            return error(HttpStatus.BAD_REQUEST, ServiceTokenService.UNSUPPORTED_GRANT_TYPE);
        }
        try {
            ServiceToken token = tokens.issue(credentials[0], credentials[1], scopes == null ? null : scopes[0]);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("access_token", token.accessToken());
            body.put("token_type", "Bearer");
            body.put("expires_in", token.expiresIn());
            body.put("scope", token.scope());
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        } catch (ServiceTokenException e) {
            HttpStatus status = ServiceTokenService.INVALID_CLIENT.equals(e.error())
                    ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST;
            return error(status, e.error());
        }
    }

    private static MediaType parse(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    /** {@code [clientId, clientSecret]} do cabeçalho Basic (RFC 7617; RFC 6749 §2.3.1), ou {@code null}. */
    static String[] basic(String header) {
        if (header == null || !header.regionMatches(true, 0, "Basic ", 0, 6)) {
            return null;
        }
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(header.substring(6).trim()), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
        int colon = decoded.indexOf(':');
        if (colon <= 0) {
            return null;
        }
        return new String[] {decoded.substring(0, colon), decoded.substring(colon + 1)};
    }

    private static ResponseEntity<Map<String, Object>> error(HttpStatus status, String error) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_JSON);
        if (status == HttpStatus.UNAUTHORIZED) {
            builder.header(HttpHeaders.WWW_AUTHENTICATE, REALM);
        }
        return builder.body(Map.of("error", error));
    }
}
