package mz.co.moseguros.policy.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mz.co.moseguros.policy.PaymentPlan;

/**
 * Calendário das prestações (S6-01 regra 2). Sem custo de fraccionamento neste sprint.
 */
public final class InstalmentSchedule {

    private InstalmentSchedule() {
    }

    /** Uma prestação do calendário. */
    public record Line(int seq, LocalDate dueDate, long amount) {
    }

    /**
     * {@code n} prestações ({@link PaymentPlan#instalments()}): {@code base = floor(total / n)} em
     * centavos; a prestação 1 vale {@code base + resto} e as outras {@code base}. O vencimento da
     * prestação {@code k} é {@code startDate + (k − 1) × 12 / n} meses, sempre a partir de
     * {@code startDate} (não acumulado; 31/01 + 1 mês = 28/02).
     *
     * @param total     prémio total do período, em centavos (&gt; 0)
     * @param startDate data de {@code start_at} em {@code Africa/Maputo}
     */
    public static List<Line> build(long total, PaymentPlan plan, LocalDate startDate) {
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(startDate, "startDate");
        int n = plan.instalments();
        if (total < n) {
            throw new IllegalArgumentException("The total must be at least one centavo per instalment");
        }
        long base = total / n;
        long remainder = total - base * n;
        int monthsBetween = 12 / n;
        List<Line> lines = new ArrayList<>(n);
        for (int k = 1; k <= n; k++) {
            long amount = k == 1 ? base + remainder : base;
            lines.add(new Line(k, startDate.plusMonths((long) (k - 1) * monthsBetween), amount));
        }
        return List.copyOf(lines);
    }
}
