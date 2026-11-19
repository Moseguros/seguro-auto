package mz.co.moseguros.shared.security;

import java.io.IOException;
import java.time.Clock;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.servlet.http.HttpServletResponse;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Cadeia de segurança dos endpoints internos {@code /internal/**} (S5-09 §1), antes da cadeia da API
 * ({@link SecurityConfig}):
 *
 * <ul>
 *   <li>{@code POST /internal/v1/auth/token} sem JWT: a autenticação Basic do cliente de serviço é
 *       verificada no controlador;</li>
 *   <li>o resto exige um token de serviço: assinatura com a chave do S1-02, {@code iss}, {@code exp},
 *       {@code aud = moseguros-internal} e {@code type = SERVICE}. Um token de utilizador
 *       ({@code aud = moseguros-api}) dá 401 aqui, e um token de serviço dá 401 em {@code /api/**}.</li>
 * </ul>
 *
 * <p>O descodificador não é um bean: a cadeia da API continua a usar o único {@code JwtDecoder}.
 * O {@link TenantContextFilter} põe {@code NONE} para {@code type = SERVICE}; quem precisa de ler
 * dados de várias seguradoras usa um {@code <Modulo>SystemLookups}. O nginx da web só encaminha
 * {@code /api/}: {@code /internal/**} nunca é exposto.
 */
@Configuration(proxyBeanMethods = false)
public class InternalSecurityConfig {

    static final String INTERNAL_PATHS = "/internal/**";
    static final RequestMatcher TOKEN_ENDPOINT =
            AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/internal/v1/auth/token");

    @Bean
    @Order(1)
    SecurityFilterChain internalSecurityFilterChain(HttpSecurity http, RSAKey jwtSigningKey, JwtProperties properties,
                                                    Clock clock, ObjectMapper objectMapper) throws Exception {
        AuthenticationEntryPoint unauthorized = (req, res, ex) -> write(res, objectMapper,
                Problems.of(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED, "Authentication required"));
        AccessDeniedHandler forbidden = (req, res, ex) -> write(res, objectMapper,
                Problems.of(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN, "Access denied"));
        NimbusJwtDecoder decoder = internalDecoder(jwtSigningKey, properties, clock);
        return http
                .securityMatcher(INTERNAL_PATHS)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(TOKEN_ENDPOINT).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(bearerTokenResolver())
                        .authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler(forbidden)
                        .jwt(jwt -> jwt
                                .decoder(decoder)
                                .jwtAuthenticationConverter(new JwtClaimsConverter())))
                .addFilterAfter(new TenantContextFilter(), BearerTokenAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler(forbidden))
                .build();
    }

    /** Mesma chave pública, {@code aud = internal-audience} e {@code type = SERVICE}. */
    static NimbusJwtDecoder internalDecoder(RSAKey jwtSigningKey, JwtProperties properties, Clock clock)
            throws JOSEException {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(jwtSigningKey.toRSAPublicKey())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
        decoder.setJwtValidator(internalValidator(properties, clock));
        return decoder;
    }

    static OAuth2TokenValidator<Jwt> internalValidator(JwtProperties properties, Clock clock) {
        return new DelegatingOAuth2TokenValidator<>(List.of(
                JwtKeyConfig.validator(properties.issuer(), properties.internalAudience(), clock),
                new JwtClaimValidator<String>(JwtClaims.TYPE, JwtClaims.TYPE_SERVICE::equals)));
    }

    /** O pedido do token leva {@code Authorization: Basic}: nunca se lê como Bearer. */
    static BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
        return request -> TOKEN_ENDPOINT.matches(request) ? null : delegate.resolve(request);
    }

    private static void write(HttpServletResponse response, ObjectMapper mapper, ProblemDetail problem)
            throws IOException {
        response.setStatus(problem.getStatus());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), problem);
    }
}
