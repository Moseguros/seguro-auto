package mz.co.moseguros.payment.application.reference;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gerador real das referências Ponto24 (S5-07 §4; ADR-007): número de factura sequencial da entidade
 * ({@link ReferenceInvoiceSequence}), mês da emissão em {@code Africa/Maputo} e dígitos de controlo de
 * {@link Ponto24CheckDigits}. O mesmo bean em todos os ambientes, incluindo {@code prod}: não há gerador
 * mock.
 *
 * <p>A unicidade vem da numeração (a entidade nunca repete uma factura, logo nunca repete uma referência);
 * os índices únicos de {@code payment.reference_payment} são a rede de segurança. A referência completa
 * nunca vai para os logs em {@code INFO}.
 */
@Component
public class Ponto24ReferenceGenerator implements ReferenceGenerator {

    /** Fuso em que se conta o mês da emissão. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");

    private static final Logger log = LoggerFactory.getLogger(Ponto24ReferenceGenerator.class);

    private final ReferenceInvoiceSequence sequence;

    public Ponto24ReferenceGenerator(ReferenceInvoiceSequence sequence) {
        this.sequence = sequence;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public GeneratedReference generate(ReferenceRequest request) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(request.amount(), "amount");
        Objects.requireNonNull(request.issuedAt(), "issuedAt");
        int invoiceNumber = sequence.next(request.entity());
        int month = ZonedDateTime.ofInstant(request.issuedAt(), ZONE).getMonthValue();
        String reference = Ponto24CheckDigits.reference(request.entity(), invoiceNumber, month,
                request.amount().amount());
        log.debug("Reference generated intentId={} entity={} invoiceNumber={}", request.intentId(), request.entity(),
                invoiceNumber);
        return new GeneratedReference(reference, invoiceNumber, month);
    }

    @Override
    public boolean isValid(String entity, String reference, long amountCentavos) {
        return Ponto24CheckDigits.isValid(entity, reference, amountCentavos);
    }
}
