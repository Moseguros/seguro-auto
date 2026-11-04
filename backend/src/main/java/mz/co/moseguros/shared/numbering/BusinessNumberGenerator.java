package mz.co.moseguros.shared.numbering;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Números de negócio ({@code PR-2027-000001}, {@code PG-2027-0000001}…; modelo transversal §4 e
 * S4-02 §1), contados por prefixo e por ano de {@code Africa/Maputo} em
 * {@code shared.business_sequence}.
 *
 * <p>Corre na transacção de quem chama ({@link Propagation#MANDATORY}): o {@code insert … on conflict
 * do update} bloqueia a linha do contador até ao fim dessa transacção, por isso duas transacções em
 * paralelo nunca recebem o mesmo número, e um rollback desfaz o incremento (não há números perdidos
 * por erros). Sem transacção lança {@code IllegalTransactionStateException}.
 */
@Service
public class BusinessNumberGenerator {

    /** Fuso em que se conta o ano. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");

    private static final Pattern PREFIX = Pattern.compile("^[A-Z]{2,4}$");
    private static final String NEXT = """
            insert into shared.business_sequence (prefix, year, last_value) values (:prefix, :year, 1)
            on conflict (prefix, year) do update set last_value = shared.business_sequence.last_value + 1
            returning last_value""";

    @PersistenceContext
    private EntityManager entityManager;

    private final Clock clock;

    public BusinessNumberGenerator(Clock clock) {
        this.clock = clock;
    }

    /**
     * Próximo número do prefixo no ano actual: {@code {prefixo}-{ano}-{valor}}, com o valor com zeros
     * à esquerda até ao número de dígitos do prefixo ({@link BusinessPrefix#digits}); acima do
     * máximo cresce sem cortar ({@code PR-2027-1000000}).
     *
     * @param prefix {@code ^[A-Z]{2,4}$} (ex.: {@link BusinessPrefix#PROPOSAL})
     * @throws IllegalArgumentException se o prefixo não tiver o formato
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public String next(String prefix) {
        if (prefix == null || !PREFIX.matcher(prefix).matches()) {
            throw new IllegalArgumentException("Invalid business number prefix: " + prefix);
        }
        // LocalDate.ofInstant em vez de LocalDate.now(clock.withZone(...)): o relógio dos testes não muda de fuso.
        int year = LocalDate.ofInstant(clock.instant(), ZONE).getYear();
        Number value = (Number) entityManager.createNativeQuery(NEXT)
                .setParameter("prefix", prefix)
                .setParameter("year", year)
                .getSingleResult();
        return format(prefix, year, value.longValue());
    }

    /** {@code PR-2027-000001}. */
    static String format(String prefix, int year, long value) {
        return prefix + "-" + year + "-" + String.format("%0" + BusinessPrefix.digits(prefix) + "d", value);
    }
}
