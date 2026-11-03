package mz.co.moseguros.shared.security;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Segurança da API (ADR-003): resource server que valida os JWT emitidos pelo próprio backend
 * ({@link JwtKeyConfig}), autorização por permissão com {@code @PreAuthorize} no serviço, e
 * {@link TenantContextFilter} depois da autenticação.
 *
 * <p>Nos caminhos públicos o cabeçalho {@code Authorization} é ignorado: sem isso um token de
 * acesso expirado daria 401 mesmo em {@code permitAll}, e a renovação (chamada precisamente
 * quando o token expirou) falharia sempre. Nos caminhos de {@link #OPTIONAL_AUTH_PATHS} (download de
 * ficheiros) o pedido pode ser anónimo, mas um token presente é validado.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    static final String[] PUBLIC_PATHS = {
        "/api/v1/health/**",
        "/api/v1/meta/**",
        "/api/v1/auth/**",
        "/api/v1/integrations/delivery-receipts/**",
        // listas de referência para os ecrãs (S2-02); a administração fica em /api/v1/admin/**
        "/api/v1/reference-domains/*/values",
        "/api/v1/platform-settings",
        "/api/v1/plate-formats",
        "/.well-known/jwks.json",
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/error"
    };

    static final RequestMatcher PUBLIC = new OrRequestMatcher(Arrays.stream(PUBLIC_PATHS)
            .map(path -> (RequestMatcher) AntPathRequestMatcher.antMatcher(path))
            .toList());

    /**
     * Caminhos públicos <b>ou</b> autenticados (S2-01): sem token o pedido é anónimo; com token
     * válido é autenticado; com token inválido ou expirado dá 401 (o cliente renova e repete). Por
     * isso ficam fora de {@link #PUBLIC_PATHS}, onde o cabeçalho seria ignorado.
     */
    static final String[] OPTIONAL_AUTH_PATHS = {
        "/api/v1/files/**"
    };

    static final RequestMatcher OPTIONAL_AUTH = new OrRequestMatcher(Arrays.stream(OPTIONAL_AUTH_PATHS)
            .map(path -> (RequestMatcher) AntPathRequestMatcher.antMatcher(path))
            .toList());

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        AuthenticationEntryPoint unauthorized = (req, res, ex) -> write(res, objectMapper,
                Problems.of(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED, "Authentication required"));
        AccessDeniedHandler forbidden = (req, res, ex) -> write(res, objectMapper,
                Problems.of(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN, "Access denied"));
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC).permitAll()
                        .requestMatchers(OPTIONAL_AUTH).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(bearerTokenResolver())
                        .authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler(forbidden)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtClaimsConverter())))
                .addFilterAfter(new TenantContextFilter(), BearerTokenAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler(forbidden))
                .build();
    }

    /** Ignora o {@code Authorization} nos caminhos públicos; nos outros usa o resolvedor normal. */
    static BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
        return request -> PUBLIC.matches(request) ? null : delegate.resolve(request);
    }

    private static void write(HttpServletResponse response, ObjectMapper mapper, ProblemDetail problem)
            throws IOException {
        response.setStatus(problem.getStatus());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), problem);
    }
}
