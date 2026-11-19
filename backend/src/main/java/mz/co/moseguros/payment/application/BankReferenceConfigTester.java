package mz.co.moseguros.payment.application;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.SettingsValidator.Mode;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.payment.domain.TestResult;
import mz.co.moseguros.shared.secrets.SecretStore;
import org.springframework.stereotype.Component;

/**
 * Teste de {@code BANK_REFERENCE} (S5-01 §5): valida {@code settings} com o schema completo e as
 * regras da secção 4 e confirma que os segredos SFTP obrigatórios existem no cofre. <b>Não liga ao
 * SFTP</b>: as credenciais só são usadas pelo {@code payment-file-worker} (ADR-004); a ligação real
 * aparece como "Última leitura" (S5-10, S5-12).
 *
 * <p>Mensagens: {@code CONFIG_VALID}, ou o primeiro problema ({@code SETTINGS_INVALID},
 * {@code SECRET_MISSING}).
 */
@Component
public class BankReferenceConfigTester implements PaymentMethodTester {

    public static final String CONFIG_VALID = "CONFIG_VALID";
    public static final String SETTINGS_INVALID = "SETTINGS_INVALID";
    public static final String SECRET_MISSING = "SECRET_MISSING";

    private final SettingsValidator validator;
    private final SecretStore secrets;

    public BankReferenceConfigTester(SettingsValidator validator, SecretStore secrets) {
        this.validator = validator;
        this.secrets = secrets;
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.BANK_REFERENCE;
    }

    @Override
    public TestResult test(UUID insurerId, JsonNode settings, Function<String, Optional<String>> secret) {
        if (!validator.validateSettings(PaymentMethod.BANK_REFERENCE, settings, insurerId, Mode.ENABLE).isEmpty()) {
            return TestResult.failed(SETTINGS_INVALID);
        }
        for (SecretField field : SecretField.of(PaymentMethod.BANK_REFERENCE)) {
            // Só a existência (SecretStore.exists): o tester não precisa do valor.
            if (field.required(settings) && !secrets.exists(field.path(insurerId))) {
                return TestResult.failed(SECRET_MISSING);
            }
        }
        return TestResult.ok(CONFIG_VALID);
    }
}
