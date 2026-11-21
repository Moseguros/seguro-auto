package mz.co.moseguros.policy.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;

import mz.co.moseguros.policy.PaymentPlan;
import mz.co.moseguros.policy.domain.InstalmentSchedule.Line;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

/** Casos de referência da regra 2 do S6-01. */
class InstalmentScheduleTest {

    @Test
    void fullIsASingleInstalmentOnTheStartDate() {
        assertThat(InstalmentSchedule.build(299_900, PaymentPlan.FULL, LocalDate.of(2027, 2, 10)))
                .containsExactly(new Line(1, LocalDate.of(2027, 2, 10), 299_900));
    }

    @Test
    void quarterlySplitsEvenly() {
        assertThat(InstalmentSchedule.build(299_900, PaymentPlan.QUARTERLY, LocalDate.of(2027, 2, 10)))
                .containsExactly(
                        new Line(1, LocalDate.of(2027, 2, 10), 74_975),
                        new Line(2, LocalDate.of(2027, 5, 10), 74_975),
                        new Line(3, LocalDate.of(2027, 8, 10), 74_975),
                        new Line(4, LocalDate.of(2027, 11, 10), 74_975));
    }

    @Test
    void semiannualPutsTheRemainderOnTheFirstInstalment() {
        assertThat(InstalmentSchedule.build(299_901, PaymentPlan.SEMIANNUAL, LocalDate.of(2027, 2, 10)))
                .containsExactly(
                        new Line(1, LocalDate.of(2027, 2, 10), 149_951),
                        new Line(2, LocalDate.of(2027, 8, 10), 149_950));
    }

    @Test
    void monthlyFromTheLastDayOfJanuaryIsNotAccumulated() {
        List<Line> lines = InstalmentSchedule.build(100_001, PaymentPlan.MONTHLY, LocalDate.of(2027, 1, 31));
        assertThat(lines).hasSize(12);
        assertThat(lines.get(0)).isEqualTo(new Line(1, LocalDate.of(2027, 1, 31), 8_338));
        assertThat(lines.subList(1, 12)).allSatisfy(l -> assertThat(l.amount()).isEqualTo(8_333));
        assertThat(lines.stream().map(Line::dueDate)).containsExactly(
                LocalDate.of(2027, 1, 31), LocalDate.of(2027, 2, 28), LocalDate.of(2027, 3, 31),
                LocalDate.of(2027, 4, 30), LocalDate.of(2027, 5, 31), LocalDate.of(2027, 6, 30),
                LocalDate.of(2027, 7, 31), LocalDate.of(2027, 8, 31), LocalDate.of(2027, 9, 30),
                LocalDate.of(2027, 10, 31), LocalDate.of(2027, 11, 30), LocalDate.of(2027, 12, 31));
        assertThat(lines.stream().map(Line::seq)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }

    @ParameterizedTest
    @EnumSource(PaymentPlan.class)
    void theSumIsAlwaysTheTotal(PaymentPlan plan) {
        LongStream.of(12, 99, 100_001, 299_900, 299_901, 1_234_567, 9_999_999_999L).forEach(total -> {
            List<Line> lines = InstalmentSchedule.build(total, plan, LocalDate.of(2027, 3, 15));
            assertThat(lines).hasSize(plan.instalments());
            assertThat(lines.stream().mapToLong(Line::amount).sum()).as("%s %d", plan, total).isEqualTo(total);
            assertThat(lines).allSatisfy(l -> assertThat(l.amount()).isPositive());
        });
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 11})
    void rejectsATotalSmallerThanOneCentavoPerInstalment(long total) {
        assertThatThrownBy(() -> InstalmentSchedule.build(total, PaymentPlan.MONTHLY, LocalDate.of(2027, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
