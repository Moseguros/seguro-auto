package mz.co.moseguros.payment;

import java.util.Locale;
import java.util.Optional;

/**
 * Formas de pagamento da MoSeguros (ADR-007, D36): M-Pesa (API oficial da Vodacom) e entidade e
 * referência na rede Ponto24. O e-Mola e o cartão estão fora do âmbito.
 */
public enum PaymentMethod {
    MPESA,
    BANK_REFERENCE;

    /** Nome em minúsculas, usado no ficheiro do JSON Schema ({@code payment-method-mpesa.json}). */
    public String lowerName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /** O método com este nome exacto ({@code MPESA}, {@code BANK_REFERENCE}), ou vazio. */
    public static Optional<PaymentMethod> parse(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        for (PaymentMethod m : values()) {
            if (m.name().equals(raw)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }
}
