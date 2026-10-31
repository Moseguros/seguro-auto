package mz.co.moseguros.shared.logging;

import com.fasterxml.jackson.core.JsonStreamContext;
import net.logstash.logback.mask.ValueMasker;

/**
 * Mascarador para os logs JSON (Logstash encoder): aplica-se a todos os valores
 * de texto, incluindo a mensagem e o stack trace.
 */
public class SensitiveValueMasker implements ValueMasker {

    @Override
    public Object mask(JsonStreamContext context, Object value) {
        if (value instanceof CharSequence text) {
            String masked = SensitiveDataMasker.mask(text.toString());
            return masked.contentEquals(text) ? null : masked;
        }
        return null;
    }
}
