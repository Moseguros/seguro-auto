package mz.co.moseguros.shared.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Conversor Logback para logs em texto: {@code %maskedMsg}.
 */
public class MaskedMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return SensitiveDataMasker.mask(super.convert(event));
    }
}
