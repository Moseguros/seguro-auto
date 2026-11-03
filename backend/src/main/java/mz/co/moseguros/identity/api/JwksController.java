package mz.co.moseguros.identity.api;

import java.util.List;

import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Chaves públicas com que se validam os tokens de acesso (ADR-003). Público.
 */
@RestController
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class JwksController {

    private final JwksResponse jwks;

    /** Só a parte pública da chave de assinatura ({@code toPublicJWK}). */
    public JwksController(RSAKey jwtSigningKey) {
        RSAKey key = jwtSigningKey.toPublicJWK();
        this.jwks = new JwksResponse(List.of(new JwksResponse.Jwk(
                key.getKeyID(),
                key.getKeyType().getValue(),
                key.getAlgorithm().getName(),
                key.getKeyUse().identifier(),
                key.getModulus().toString(),
                key.getPublicExponent().toString())));
    }

    @GetMapping("/.well-known/jwks.json")
    @SecurityRequirements
    @Operation(operationId = "getJwks", summary = "Chaves públicas dos tokens (JWKS)")
    public JwksResponse getJwks() {
        return jwks;
    }
}
