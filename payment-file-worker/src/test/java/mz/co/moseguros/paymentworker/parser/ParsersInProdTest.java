package mz.co.moseguros.paymentworker.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Com o perfil {@code prod}, o worker tem os dois leitores reais (S5-08, critério de aceitação): não há {@code @Profile}. */
class ParsersInProdTest {

    @Test
    void prodProfileHasTheMepsAndBmepsParsers() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("prod");
            context.scan(PaymentFileParser.class.getPackageName());
            context.refresh();

            ParserRegistry registry = context.getBean(ParserRegistry.class);
            assertThat(registry.formats()).containsExactlyInAnyOrder("MEPS", "BMEPS");
            assertThat(registry.find("MEPS")).containsInstanceOf(MepsFileParser.class);
            assertThat(registry.find("BMEPS")).containsInstanceOf(BmepsFileParser.class);
        }
    }
}
