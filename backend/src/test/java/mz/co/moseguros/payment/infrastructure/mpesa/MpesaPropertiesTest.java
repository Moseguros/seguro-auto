package mz.co.moseguros.payment.infrastructure.mpesa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/** {@code timeout-cap} e {@code adapter: test-stub} só existem nos testes (S5-04 §9). */
class MpesaPropertiesTest {

    final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(MpesaConfig.class);

    @Test
    void timeoutCapWithTheProdProfileDoesNotStart() {
        runner.withPropertyValues("spring.profiles.active=prod", "moseguros.payment.mpesa.timeout-cap=PT3S")
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().rootCause().hasMessageContaining("timeout-cap"));
        runner.withPropertyValues("spring.profiles.active=prod")
                .run(context -> assertThat(context).hasNotFailed());
        runner.withPropertyValues("spring.profiles.active=test", "moseguros.payment.mpesa.timeout-cap=PT3S")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void testStubOnlyInTheTestProfile() {
        runner.withPropertyValues("spring.profiles.active=local", "moseguros.payment.mpesa.adapter=test-stub")
                .run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues("spring.profiles.active=test", "moseguros.payment.mpesa.adapter=test-stub")
                .run(context -> assertThat(context.getBean(MpesaProperties.class).getAdapter())
                        .isEqualTo(MpesaProperties.Adapter.TEST_STUB));
    }

    @Test
    void timeoutCapLimitsOnlyTheC2bReadTimeout() {
        MpesaProperties p = new MpesaProperties();
        assertThat(p.c2bReadTimeout(90)).isEqualTo(Duration.ofSeconds(90));
        p.setTimeoutCap(Duration.ofSeconds(3));
        assertThat(p.c2bReadTimeout(90)).isEqualTo(Duration.ofSeconds(3));
        assertThat(p.getReadTimeout()).isEqualTo(Duration.ofSeconds(15));
        assertThatThrownBy(() -> p.setTimeoutCap(Duration.ZERO)).isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> p.setTimeoutCap(null)).doesNotThrowAnyException();
    }
}
