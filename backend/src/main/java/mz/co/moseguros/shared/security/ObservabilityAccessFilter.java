package mz.co.moseguros.shared.security;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@code /api/v1/prometheus} só na rede interna (S6-08 §1): o pedido passa quando o <b>endereço remoto</b>
 * ({@link HttpServletRequest#getRemoteAddr()}, nunca o {@code X-Forwarded-For}) está numa das redes de
 * {@code moseguros.observability.allowed-cidrs}; nos outros casos responde 404, como um caminho que não
 * existe. Não é um bean: só corre dentro da cadeia do {@link SecurityConfig}.
 */
public class ObservabilityAccessFilter extends OncePerRequestFilter {

    static final String PROMETHEUS_PATH = "/api/v1/prometheus";

    static final RequestMatcher PROMETHEUS = AntPathRequestMatcher.antMatcher(PROMETHEUS_PATH);

    private final List<IpAddressMatcher> allowed;
    private final ObjectMapper mapper;

    public ObservabilityAccessFilter(List<String> allowedCidrs, ObjectMapper mapper) {
        this.allowed = (allowedCidrs == null ? List.<String>of() : allowedCidrs).stream()
                .filter(c -> c != null && !c.isBlank())
                .map(String::strip)
                .map(IpAddressMatcher::new)
                .toList();
        this.mapper = mapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !PROMETHEUS.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (isAllowed(request.getRemoteAddr())) {
            chain.doFilter(request, response);
            return;
        }
        ProblemDetail problem = Problems.of(HttpStatus.NOT_FOUND, ErrorCodes.NOT_FOUND, "Not found");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), problem);
    }

    boolean isAllowed(String remoteAddress) {
        if (remoteAddress == null || remoteAddress.isBlank()) {
            return false;
        }
        String address = remoteAddress.strip();
        if (address.startsWith("[") && address.endsWith("]")) {
            address = address.substring(1, address.length() - 1);
        }
        // Só literais IPv4/IPv6: um nome nunca é resolvido por DNS.
        if (!address.matches("[0-9A-Fa-f:.]+")) {
            return false;
        }
        for (IpAddressMatcher matcher : allowed) {
            try {
                if (matcher.matches(address)) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }
}
