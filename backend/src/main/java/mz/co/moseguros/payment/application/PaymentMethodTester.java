package mz.co.moseguros.payment.application;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.TestResult;

/**
 * Teste de ligação de uma forma de pagamento (S5-01 §5), com os dados gravados. Um bean por método:
 * {@code BANK_REFERENCE} no S5-01 ({@link BankReferenceConfigTester}), {@code MPESA} no S5-04. Sem
 * tester para o método, o resultado é {@code NOT_SUPPORTED}.
 *
 * <p>O teste tem 15 s no total; ultrapassado, o resultado é {@code FAILED} / {@code TIMEOUT}. A
 * mensagem é um código curto, nunca dados secretos.
 */
public interface PaymentMethodTester {

    PaymentMethod method();

    /**
     * @param settings dados não secretos gravados
     * @param secret   {@code secret(campo)} lê o valor do cofre ({@code campo} como em
     *                 {@code SecretField.fieldName()}); vazio se não estiver configurado
     */
    TestResult test(UUID insurerId, JsonNode settings, Function<String, Optional<String>> secret);
}
