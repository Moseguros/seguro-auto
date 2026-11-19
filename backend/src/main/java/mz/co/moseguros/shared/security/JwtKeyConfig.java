package mz.co.moseguros.shared.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * Chave de assinatura RS256 dos tokens (ADR-003) e o codificador e descodificador JWT.
 *
 * <p>A chave vem de {@code moseguros.security.jwt.private-key} (PEM PKCS#8, RSA 2048); a
 * pública deriva-se dela. Sem chave, nos perfis {@code local} e {@code test} gera-se um par
 * no arranque (os tokens deixam de valer a cada reinício); nos outros perfis a aplicação não
 * arranca.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
public class JwtKeyConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtKeyConfig.class);

    /** Tolerância entre relógios ao validar {@code exp}. */
    static final Duration CLOCK_SKEW = Duration.ofSeconds(30);
    static final int KEY_SIZE = 2048;
    static final String MISSING_KEY_MESSAGE = "moseguros.security.jwt.private-key is required outside the "
            + "'local' and 'test' profiles: set MOSEGUROS_JWT_PRIVATE_KEY to an RSA 2048 private key in PEM PKCS#8";

    /** Chave de assinatura (privada e pública) com {@code kid}, {@code alg: RS256} e {@code use: sig}. */
    @Bean
    RSAKey jwtSigningKey(JwtProperties properties, Environment environment) {
        KeyPair keyPair;
        String pem = properties.privateKey();
        if (pem == null || pem.isBlank()) {
            if (!environment.acceptsProfiles(Profiles.of("local", "test"))) {
                throw new IllegalStateException(MISSING_KEY_MESSAGE);
            }
            log.warn("No JWT signing key configured (MOSEGUROS_JWT_PRIVATE_KEY): generated a temporary RSA {} key; "
                    + "tokens stop being valid when the application restarts", KEY_SIZE);
            keyPair = generate();
        } else {
            keyPair = fromPem(pem);
        }
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(properties.keyId())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();
    }

    @Bean
    JwtEncoder jwtEncoder(RSAKey jwtSigningKey) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwtSigningKey)));
    }

    /** Valida a assinatura com a chave pública local, {@code iss}, {@code aud} e {@code exp} (30 s de tolerância). */
    @Bean
    JwtDecoder jwtDecoder(RSAKey jwtSigningKey, JwtProperties properties, Clock clock) throws JOSEException {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(jwtSigningKey.toRSAPublicKey())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
        decoder.setJwtValidator(validator(properties, clock));
        return decoder;
    }

    static OAuth2TokenValidator<Jwt> validator(JwtProperties properties, Clock clock) {
        return validator(properties.issuer(), properties.audience(), clock);
    }

    /** {@code exp} obrigatório e válido, {@code iss} e {@code aud} indicados (também os tokens de serviço, S5-09). */
    static OAuth2TokenValidator<Jwt> validator(String issuer, String audience, Clock clock) {
        JwtTimestampValidator timestamps = new JwtTimestampValidator(CLOCK_SKEW);
        timestamps.setClock(clock);
        return new DelegatingOAuth2TokenValidator<>(List.of(
                new JwtClaimValidator<Object>(JwtClaimNames.EXP, exp -> exp != null),
                timestamps,
                new JwtIssuerValidator(issuer),
                new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                        aud -> aud != null && aud.contains(audience))));
    }

    static KeyPair generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA is not available", e);
        }
    }

    /** Lê uma chave privada RSA em PEM PKCS#8 e deriva a pública. */
    static KeyPair fromPem(String pem) {
        String base64 = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        RSAPrivateCrtKey privateKey;
        RSAPublicKey publicKey;
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateCrtKey) factory.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64)));
            publicKey = (RSAPublicKey) factory.generatePublic(
                    new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
        } catch (IllegalArgumentException | ClassCastException | NoSuchAlgorithmException
                 | InvalidKeySpecException e) {
            throw new IllegalStateException("moseguros.security.jwt.private-key is not a valid RSA private key "
                    + "in PEM PKCS#8", e);
        }
        if (privateKey.getModulus().bitLength() < KEY_SIZE) {
            throw new IllegalStateException("moseguros.security.jwt.private-key must be an RSA key of at least "
                    + KEY_SIZE + " bits");
        }
        return new KeyPair(publicKey, privateKey);
    }
}
