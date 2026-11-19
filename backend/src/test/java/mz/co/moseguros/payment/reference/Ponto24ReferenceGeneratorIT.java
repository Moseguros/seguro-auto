package mz.co.moseguros.payment.reference;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.payment.PaymentTestSupport;
import mz.co.moseguros.payment.application.reference.GeneratedReference;
import mz.co.moseguros.payment.application.reference.Ponto24CheckDigits;
import mz.co.moseguros.payment.application.reference.Ponto24ReferenceGenerator;
import mz.co.moseguros.payment.application.reference.ReferenceGenerator;
import mz.co.moseguros.payment.application.reference.ReferenceRequest;
import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Gerador real (S5-07 §4): é o único {@link ReferenceGenerator}, o mês é o de {@code Africa/Maputo} e duas
 * intenções com o mesmo montante na mesma entidade dão referências diferentes.
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class Ponto24ReferenceGeneratorIT {

    @Autowired
    ReferenceGenerator generator;

    @Autowired
    ApplicationContext context;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    IdentityFixtures fixtures;

    String freshEntity() {
        String entity = PaymentTestSupport.randomEntity();
        fixtures.owner().update("delete from payment.reference_invoice_sequence where entity = ?", entity);
        return entity;
    }

    GeneratedReference generate(String entity, long amount, String issuedAt) {
        return tx.execute(s -> generator.generate(new ReferenceRequest(UUID.randomUUID(), UUID.randomUUID(), entity,
                Money.ofCentavos(amount), Instant.parse(issuedAt))));
    }

    @Test
    void theRealGeneratorIsTheOnlyOne() {
        assertThat(context.getBeansOfType(ReferenceGenerator.class)).hasSize(1);
        assertThat(generator).isInstanceOf(Ponto24ReferenceGenerator.class);
    }

    @Test
    void theMonthIsTheOneInMaputo() {
        String entity = freshEntity();
        // 31/01 às 23:30 UTC = 01/02 às 01:30 em Maputo
        GeneratedReference february = generate(entity, 200_000, "2027-01-31T23:30:00Z");
        assertThat(february.month()).isEqualTo(2);
        assertThat(february.reference()).isEqualTo(Ponto24CheckDigits.reference(entity, 1, 2, 200_000));
        assertThat(february.reference().substring(7, 9)).isEqualTo("02");

        GeneratedReference january = generate(entity, 200_000, "2027-01-31T21:59:00Z");
        assertThat(january.month()).isEqualTo(1);
        assertThat(january.reference().substring(7, 9)).isEqualTo("01");

        GeneratedReference nextJanuary = generate(entity, 200_000, "2027-12-31T22:30:00Z");
        assertThat(nextJanuary.month()).isEqualTo(1);
    }

    @Test
    void twoIntentsWithTheSameAmountInTheSameEntityGetDifferentReferences() {
        String entity = freshEntity();
        GeneratedReference a = generate(entity, 299_900, "2027-03-10T10:00:00Z");
        GeneratedReference b = generate(entity, 299_900, "2027-03-10T10:00:00Z");
        assertThat(a.reference()).isNotEqualTo(b.reference());
        assertThat(b.invoiceNumber()).isEqualTo(a.invoiceNumber() + 1);
        assertThat(generator.isValid(entity, a.reference(), 299_900)).isTrue();
        assertThat(generator.isValid(entity, b.reference(), 299_900)).isTrue();
        // a entidade e o montante entram no controlo
        assertThat(generator.isValid(entity, a.reference(), 299_901)).isFalse();
        assertThat(generator.isValid("54321", "12122450702", 200_000)).isTrue();
    }
}
