package mz.co.moseguros.rating.api;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import mz.co.moseguros.rating.domain.tariff.RatingRulesValidator;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

/**
 * Limite do corpo dos pedidos com uma tarifa (S3-01 §5; também o simulador, S3-02 §12): mais de 262 144 bytes → 400
 * {@code RATING_RULES_TOO_LARGE}, verificado pelo {@code Content-Length} (antes de ler) e pelo
 * número de bytes lidos (pedidos sem {@code Content-Length}).
 */
@ControllerAdvice(assignableTypes = {RatingRulesController.class, RatingSimulationController.class})
class RatingRulesBodyLimit extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        if (inputMessage.getHeaders().getContentLength() > RatingRulesValidator.MAX_BYTES) {
            throw tooLarge();
        }
        InputStream limited = new LimitedInputStream(inputMessage.getBody(), RatingRulesValidator.MAX_BYTES);
        HttpHeaders headers = inputMessage.getHeaders();
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return limited;
            }

            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }
        };
    }

    static DomainException tooLarge() {
        return DomainException.of(ErrorCodes.RATING_RULES_TOO_LARGE, "The request body is larger than 256 KB");
    }

    /** Lança {@code RATING_RULES_TOO_LARGE} ao passar de {@code max} bytes lidos. */
    static final class LimitedInputStream extends FilterInputStream {

        private final long max;
        private long count;

        LimitedInputStream(InputStream in, long max) {
            super(in);
            this.max = max;
        }

        @Override
        public int read() throws IOException {
            int b = super.read();
            if (b >= 0) {
                add(1);
            }
            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int n = super.read(b, off, len);
            if (n > 0) {
                add(n);
            }
            return n;
        }

        @Override
        public long skip(long n) throws IOException {
            long skipped = super.skip(n);
            add(skipped);
            return skipped;
        }

        private void add(long n) {
            count += n;
            if (count > max) {
                throw tooLarge();
            }
        }
    }
}
