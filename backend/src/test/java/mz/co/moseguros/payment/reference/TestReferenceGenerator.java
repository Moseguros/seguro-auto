package mz.co.moseguros.payment.reference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import mz.co.moseguros.payment.application.reference.GeneratedReference;
import mz.co.moseguros.payment.application.reference.ReferenceGenerator;
import mz.co.moseguros.payment.application.reference.ReferenceRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Gerador de referências só para os testes do S5-02 (perfil {@code test}): uma sequência em memória,
 * com o formato factura (7) + mês (2) + "controlo" (2) mas <b>sem</b> o algoritmo MOD 97 da
 * especificação. O S5-07 apaga-o e passa a usar o {@code Ponto24ReferenceGenerator} real.
 */
@Component
@Profile("test")
public class TestReferenceGenerator implements ReferenceGenerator {

    private static final ZoneId ZONE = ZoneId.of("Africa/Maputo");

    /** Começa num valor aleatório: várias execuções na mesma BD não repetem números. */
    private final AtomicInteger invoices = new AtomicInteger(ThreadLocalRandom.current().nextInt(1, 5_000_000));

    @Override
    public GeneratedReference generate(ReferenceRequest request) {
        int invoice = invoices.incrementAndGet();
        int month = LocalDate.ofInstant(request.issuedAt(), ZONE).getMonthValue();
        String reference = String.format("%07d%02d%02d", invoice, month, invoice % 97);
        return new GeneratedReference(reference, invoice, month);
    }

    @Override
    public boolean isValid(String entity, String reference, long amountCentavos) {
        return reference != null && reference.matches("^[0-9]{11}$");
    }
}
