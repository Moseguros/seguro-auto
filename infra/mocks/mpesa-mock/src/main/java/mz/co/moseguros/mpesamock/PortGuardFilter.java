package mz.co.moseguros.mpesamock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Cada caminho só responde na sua porta: C2B e apoio ({@code /__mock/**}, {@code /}) na porta
 * principal, consulta na 18353, reversão na 18354. Noutra porta → 404 sem corpo.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class PortGuardFilter extends OncePerRequestFilter {

    static final String C2B_PATH = "/ipg/v1x/c2bPayment/singleStage";
    static final String QUERY_PATH = "/ipg/v1x/queryTransactionStatus";
    static final String REVERSAL_PATH = "/ipg/v1x/reversal";

    private final MockProperties props;
    private final ServletWebServerApplicationContext context;

    PortGuardFilter(MockProperties props, ServletWebServerApplicationContext context) {
        this.props = props;
        this.context = context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!allowed(request.getLocalPort(), normalise(request.getRequestURI()))) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean allowed(int port, String path) {
        if (port == props.getPorts().getQuery()) {
            return QUERY_PATH.equals(path);
        }
        if (port == props.getPorts().getReversal()) {
            return REVERSAL_PATH.equals(path);
        }
        if (port == mainPort()) {
            return !QUERY_PATH.equals(path) && !REVERSAL_PATH.equals(path);
        }
        return false;
    }

    private int mainPort() {
        // A porta real do conector principal (o Tomcat escolhe-a quando server.port é 0).
        return context.getWebServer().getPort();
    }

    static String normalise(String uri) {
        String path = uri == null ? "" : uri;
        while (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
