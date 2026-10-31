package mz.co.moseguros.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configuração de segurança provisória (Sprint 0): só os endpoints técnicos são
 * públicos; tudo o resto devolve 401. A autenticação real chega no Sprint 1.
 */
@Configuration
public class SecurityConfig {

    static final String[] PUBLIC_PATHS = {
        "/api/v1/health/**",
        "/api/v1/meta/**",
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/error"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> write(res, objectMapper,
                                Problems.of(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED, "Authentication required")))
                        .accessDeniedHandler((req, res, ex) -> write(res, objectMapper,
                                Problems.of(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN, "Access denied"))))
                .build();
    }

    private static void write(HttpServletResponse response, ObjectMapper mapper, ProblemDetail problem)
            throws IOException {
        response.setStatus(problem.getStatus());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), problem);
    }
}
