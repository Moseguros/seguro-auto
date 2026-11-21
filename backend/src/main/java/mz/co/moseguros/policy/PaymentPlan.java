package mz.co.moseguros.policy;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Plano de pagamento do prémio (S6-01 regra 2). Neste sprint as apólices emitidas a partir de propostas
 * são sempre {@link #FULL} (Questões do S6-01: a proposta ainda não tem onde escolher o plano).
 *
 * <p>Está no pacote público (e não em {@code domain}) porque faz parte do {@link PolicyView}.
 */
@Schema(enumAsRef = true)
public enum PaymentPlan {
    FULL(1),
    SEMIANNUAL(2),
    QUARTERLY(4),
    MONTHLY(12);

    private final int instalments;

    PaymentPlan(int instalments) {
        this.instalments = instalments;
    }

    /** Número de prestações por período (1, 2, 4 ou 12). */
    public int instalments() {
        return instalments;
    }
}
