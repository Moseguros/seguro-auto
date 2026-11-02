package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;

/** Bloqueio, recuperação de palavra-passe e transições de estado do staff (S1-05). */
class StaffAccountLifecycleTest {

    static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");
    static final Duration LOCK = Duration.ofMinutes(15);

    @Test
    void fifthFailureLocksAnActiveAccountForFifteenMinutes() {
        UserAccount account = active();
        for (int attempt = 1; attempt <= 4; attempt++) {
            assertThat(account.registerFailedLogin(NOW, 5, LOCK)).isFalse();
        }
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        assertThat(account.registerFailedLogin(NOW, 5, LOCK)).isTrue();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.LOCKED);
        assertThat(account.getLockedUntil()).isEqualTo(NOW.plus(LOCK));
        assertThat(account.getFailedAttempts()).isEqualTo(5);
        assertThat(account.isLockedAt(NOW.plus(LOCK).minusSeconds(1))).isTrue();
        assertThat(account.effectiveStatus(NOW)).isEqualTo(AccountStatus.LOCKED);
        assertThat(account.effectiveStatus(NOW.plus(LOCK))).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.isLockedAt(NOW.plus(LOCK))).isFalse();
    }

    @Test
    void expiredLockIsReleasedOnlyAfterTheDeadline() {
        UserAccount account = locked();

        assertThat(account.releaseExpiredLock(NOW.plusSeconds(60))).isFalse();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.LOCKED);

        assertThat(account.releaseExpiredLock(NOW.plus(LOCK))).isTrue();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getFailedAttempts()).isZero();
        assertThat(account.getLockedUntil()).isNull();
        assertThat(account.releaseExpiredLock(NOW.plus(LOCK))).isFalse();
    }

    @Test
    void disabledAccountCountsFailuresButNeverLocks() {
        UserAccount account = active();
        account.disable();
        for (int attempt = 1; attempt <= 6; attempt++) {
            assertThat(account.registerFailedLogin(NOW, 5, LOCK)).isFalse();
        }
        assertThat(account.getStatus()).isEqualTo(AccountStatus.DISABLED);
        assertThat(account.getFailedAttempts()).isEqualTo(6);
    }

    @Test
    void successfulLoginResetsFailuresAndRecordsTheStep() {
        UserAccount account = active();
        account.registerFailedLogin(NOW, 5, LOCK);

        account.recordSuccessfulLogin(42L, NOW);

        assertThat(account.getFailedAttempts()).isZero();
        assertThat(account.getLastTotpStep()).isEqualTo(42L);
        assertThat(account.getLastLoginAt()).isEqualTo(NOW);

        UserAccount disabled = active();
        disabled.disable();
        assertThatThrownBy(() -> disabled.recordSuccessfulLogin(43L, NOW)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void passwordResetUnlocksAndKeepsTheTotp() {
        UserAccount account = locked();
        String secret = account.getTotpSecret();

        account.resetPassword("$argon2id$new");

        assertThat(account.getPasswordHash()).isEqualTo("$argon2id$new");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getFailedAttempts()).isZero();
        assertThat(account.getLockedUntil()).isNull();
        assertThat(account.getTotpSecret()).isEqualTo(secret);

        UserAccount disabled = active();
        disabled.disable();
        assertThatThrownBy(() -> disabled.resetPassword("x")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void disableEnableAndUnlockFollowTheAllowedTransitions() {
        UserAccount account = locked();
        account.disable();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.DISABLED);
        assertThat(account.getLockedUntil()).isNull();
        assertTransitionRefused(account::disable);
        assertTransitionRefused(account::unlock);

        account.enable();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getFailedAttempts()).isZero();
        assertTransitionRefused(account::enable);
        assertTransitionRefused(account::unlock);

        UserAccount other = locked();
        other.unlock();
        assertThat(other.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(other.getFailedAttempts()).isZero();
        assertThat(other.getLockedUntil()).isNull();
    }

    @Test
    void resetAccessClearsTheCredentials() {
        UserAccount account = locked();

        account.resetAccess();

        assertThat(account.getStatus()).isEqualTo(AccountStatus.PENDING_ACTIVATION);
        assertThat(account.getPasswordHash()).isNull();
        assertThat(account.getTotpSecret()).isNull();
        assertThat(account.getLastTotpStep()).isNull();
        assertThat(account.getFailedAttempts()).isZero();
        assertThat(account.hasCredentials()).isFalse();
        assertTransitionRefused(account::resetAccess);
        assertTransitionRefused(account::disable);
    }

    @Test
    void customersHaveNoneOfTheseTransitions() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        assertThatThrownBy(() -> customer.registerFailedLogin(NOW, 5, LOCK)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(customer::disable).isInstanceOf(IllegalStateException.class);
        assertThat(customer.effectiveStatus(NOW)).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void passwordResetRequestIsUsableOnlyOnceAndWithinItsValidity() {
        PasswordReset reset = PasswordReset.create(UUID.randomUUID(), "hash", NOW.plus(Duration.ofMinutes(30)));
        assertThat(reset.isUsable(NOW)).isTrue();
        assertThat(reset.isUsable(NOW.plus(Duration.ofMinutes(30)))).isFalse();

        reset.markUsed(NOW);
        assertThat(reset.isUsable(NOW)).isFalse();

        PasswordReset replaced = PasswordReset.create(UUID.randomUUID(), "hash2", NOW.plus(Duration.ofMinutes(30)));
        replaced.invalidate(NOW);
        replaced.invalidate(NOW.plusSeconds(5));
        assertThat(replaced.getInvalidatedAt()).isEqualTo(NOW);
        assertThat(replaced.isUsable(NOW)).isFalse();
    }

    private static UserAccount active() {
        UserAccount account = UserAccount.newStaff("staff@exemplo.co.mz", "Staff", UUID.randomUUID());
        account.setCredentials("Staff", "$argon2id$hash", "SECRET");
        account.activate(1L);
        return account;
    }

    private static UserAccount locked() {
        UserAccount account = active();
        for (int attempt = 1; attempt <= 5; attempt++) {
            account.registerFailedLogin(NOW, 5, LOCK);
        }
        return account;
    }

    private static void assertTransitionRefused(Runnable transition) {
        assertThatThrownBy(transition::run).isInstanceOf(DomainException.class)
                .extracting("code").isEqualTo("INVALID_STATE_TRANSITION");
    }
}
