package mz.co.moseguros.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Calendário das consultas de estado (S5-04 §4): 30 s, 60 s, 120 s, 300 s, depois a cada 15 min, 24 h. */
class StatusQueryScheduleTest {

    static final Instant SINCE = Instant.parse("2027-01-10T10:00:00Z");

    final PaymentProperties.StatusQuery schedule = new PaymentProperties().getStatusQuery();

    @Test
    void firstQueriesFollowTheDelaysFromUnknownSinceThenTheRepeatInterval() {
        assertThat(schedule.firstQueryAt(SINCE)).isEqualTo(SINCE.plusSeconds(30));
        assertThat(schedule.nextQueryAt(SINCE, 1, SINCE.plusSeconds(30))).isEqualTo(SINCE.plusSeconds(60));
        assertThat(schedule.nextQueryAt(SINCE, 2, SINCE.plusSeconds(61))).isEqualTo(SINCE.plusSeconds(120));
        assertThat(schedule.nextQueryAt(SINCE, 3, SINCE.plusSeconds(121))).isEqualTo(SINCE.plusSeconds(300));
        Instant fourth = SINCE.plusSeconds(301);
        assertThat(schedule.nextQueryAt(SINCE, 4, fourth)).isEqualTo(fourth.plus(Duration.ofMinutes(15)));
        Instant later = SINCE.plus(Duration.ofHours(3));
        assertThat(schedule.nextQueryAt(SINCE, 15, later)).isEqualTo(later.plus(Duration.ofMinutes(15)));
    }

    @Test
    void lastQueryIsAtTheDeadlineAndThenItGivesUp() {
        Instant almost = SINCE.plus(Duration.ofHours(24)).minusSeconds(60);
        assertThat(schedule.nextQueryAt(SINCE, 99, almost)).isEqualTo(SINCE.plus(Duration.ofHours(24)));
        assertThat(schedule.nextQueryAt(SINCE, 100, SINCE.plus(Duration.ofHours(24)))).isNull();
        assertThat(schedule.nextQueryAt(SINCE, 100, SINCE.plus(Duration.ofHours(25)))).isNull();
    }

    @Test
    void aLateQueryIsFollowedByTheNextOneInTheNextCycle() {
        Instant late = SINCE.plusSeconds(200);
        assertThat(schedule.nextQueryAt(SINCE, 1, late)).isEqualTo(late);
    }

    @Test
    void delaysMustBePositiveAndIncreasing() {
        assertThatThrownBy(() -> schedule.setDelays(List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> schedule.setDelays(List.of(Duration.ofSeconds(60), Duration.ofSeconds(30))))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> schedule.setRepeatInterval(Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> schedule.setGiveUpAfter(Duration.ofSeconds(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
