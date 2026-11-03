package mz.co.moseguros.catalog.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.PatternSyntaxException;

import mz.co.moseguros.catalog.domain.PlateFormatEntity;
import mz.co.moseguros.catalog.domain.PlateFormatRepository;
import mz.co.moseguros.shared.domain.PlateFormat;
import mz.co.moseguros.shared.domain.PlateFormatProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Formatos de matrícula activos lidos de {@code catalog.plate_format} (S2-02 §4), por
 * {@code sort_order}, com os padrões já compilados.
 *
 * <p>Guarda uma cópia em memória que recarrega quando tem mais de {@link #MAX_AGE} e logo a seguir
 * a qualquer alteração feita nesta instância ({@link PlateFormatsChanged}, depois do commit). Outras
 * instâncias vêem a alteração em {@link #MAX_AGE} no máximo.
 *
 * <p>Os formatos são validados ao gravar ({@link PlateFormatValidator}); um padrão que mesmo
 * assim não compile é ignorado (com aviso no log) em vez de impedir todos os pedidos.
 */
@Component
public class DbPlateFormatProvider implements PlateFormatProvider {

    static final Duration MAX_AGE = Duration.ofSeconds(30);

    private static final Logger log = LoggerFactory.getLogger(DbPlateFormatProvider.class);

    private final PlateFormatRepository repository;
    private final Clock clock;

    private volatile Snapshot snapshot;
    /** Aumenta a cada invalidação: uma leitura começada antes dela não fica em memória. */
    private final AtomicLong generation = new AtomicLong();

    public DbPlateFormatProvider(PlateFormatRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public List<PlateFormat> activeFormats() {
        Snapshot current = snapshot;
        Instant now = clock.instant();
        if (current == null || !now.isBefore(current.loadedAt().plus(MAX_AGE)) || now.isBefore(current.loadedAt())) {
            current = reload(now);
        }
        return current.formats();
    }

    /** Descarta a cópia em memória: a próxima leitura vai à base de dados. */
    public void invalidate() {
        generation.incrementAndGet();
        snapshot = null;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPlateFormatsChanged(PlateFormatsChanged event) {
        log.debug("Plate format {} changed: reloading formats", event.code());
        invalidate();
    }

    private Snapshot reload(Instant now) {
        long startedAt = generation.get();
        List<PlateFormat> formats = new ArrayList<>();
        for (PlateFormatEntity entity : repository.findByActiveTrueOrderBySortOrderAscCodeAsc()) {
            try {
                formats.add(entity.toPlateFormat());
            } catch (PatternSyntaxException e) {
                log.warn("Plate format {} ignored: its pattern does not compile", entity.getCode());
            }
        }
        Snapshot loaded = new Snapshot(List.copyOf(formats), now);
        synchronized (this) {
            if (generation.get() == startedAt) {
                snapshot = loaded;
            }
        }
        return loaded;
    }

    private record Snapshot(List<PlateFormat> formats, Instant loadedAt) {
    }
}
