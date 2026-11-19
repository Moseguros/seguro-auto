package mz.co.moseguros.payment.application.reference;

import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Números de factura das referências Ponto24, um contador por entidade em
 * {@code payment.reference_invoice_sequence} (S5-07 §3): o primeiro é {@code 1} ({@code 0000001}), nunca
 * reutilizado e sem ano.
 *
 * <p>Corre na transacção de quem chama ({@link Propagation#MANDATORY}, a da criação da intenção): o
 * {@code insert … on conflict do update} bloqueia a linha da entidade até ao fim dessa transacção, por isso
 * dois pedidos em paralelo nunca recebem o mesmo número; um rollback desfaz o incremento (o número nunca
 * chegou a uma referência gravada). A sequência só cresce, mesmo que a referência expire ou seja cancelada.
 */
@Component
public class ReferenceInvoiceSequence {

    /** A partir deste valor cada geração regista {@code alert=REFERENCE_SEQUENCE_NEAR_LIMIT}. */
    public static final int NEAR_LIMIT = 9_000_000;

    private static final Logger log = LoggerFactory.getLogger(ReferenceInvoiceSequence.class);
    private static final Pattern ENTITY = Pattern.compile("^[0-9]{5}$");
    private static final String NEXT = """
            insert into payment.reference_invoice_sequence (entity, last_value) values (?, 1)
            on conflict (entity) do update set last_value = payment.reference_invoice_sequence.last_value + 1
            returning last_value""";

    private final JdbcTemplate jdbc;

    public ReferenceInvoiceSequence(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Próximo número de factura da entidade (1–9 999 999).
     *
     * @throws IllegalArgumentException entidade sem 5 dígitos (erro de programação)
     * @throws DomainException          503 {@code REFERENCE_GENERATION_FAILED} se a entidade esgotou os números
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int next(String entity) {
        if (entity == null || !ENTITY.matcher(entity).matches()) {
            throw new IllegalArgumentException("The entity must have 5 digits");
        }
        Integer value;
        try {
            value = jdbc.queryForObject(NEXT, Integer.class, entity);
        } catch (DataIntegrityViolationException e) {
            // last_value já era 9 999 999: o check da coluna recusa o incremento.
            log.error("Reference invoice numbers exhausted alert=REFERENCE_SEQUENCE_EXHAUSTED entity={}", entity);
            throw DomainException.of(ErrorCodes.REFERENCE_GENERATION_FAILED,
                    "The payment reference could not be generated");
        }
        if (value == null) {
            throw new IllegalStateException("No invoice number returned");
        }
        if (value > NEAR_LIMIT) {
            log.warn("Reference invoice numbers near the limit alert=REFERENCE_SEQUENCE_NEAR_LIMIT entity={} "
                    + "invoiceNumber={}", entity, value);
        }
        return value;
    }
}
