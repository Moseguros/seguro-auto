package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpIdentifier;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Regras do {@link ContactService} difíceis de provocar com a BD real: o índice único como última
 * defesa e as verificações antes do envio.
 */
class ContactServiceTest {

    private final CurrentUser currentUser = mock(CurrentUser.class);
    private final UserAccountRepository accounts = mock(UserAccountRepository.class);
    private final SystemLookups lookups = mock(SystemLookups.class);
    private final OtpService otpService = mock(OtpService.class);
    private final AuditService audit = mock(AuditService.class);
    private final TransactionTemplate tx = mock(TransactionTemplate.class);
    private final ContactService service = new ContactService(currentUser, accounts, lookups, otpService, audit, tx);

    private final UserAccount account = UserAccount.newCustomer(Msisdn.of("841234567"));
    private final OtpIdentifier email = new OtpIdentifier(IdentifierType.EMAIL, "ana@exemplo.co.mz");

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(currentUser.id()).thenReturn(account.getId());
        when(accounts.findById(account.getId())).thenReturn(Optional.of(account));
        when(tx.execute(any())).thenAnswer(invocation ->
                ((TransactionCallback<Object>) invocation.getArgument(0)).doInTransaction(null));
    }

    @Test
    void uniqueIndexViolationIsAConflictNotAServerError() {
        UUID challengeId = UUID.randomUUID();
        when(otpService.verifyContact(challengeId, "123456", account.getId())).thenReturn(email);
        when(lookups.customerContactTaken(IdentifierType.EMAIL, email.value(), account.getId())).thenReturn(false);
        when(accounts.saveAndFlush(account)).thenThrow(new DataIntegrityViolationException("ux_user_customer_email"));

        assertThatThrownBy(() -> service.verify(challengeId, "123456"))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("CONTACT_ALREADY_IN_USE");
                    assertThat(e.getHttpStatus()).isEqualTo(409);
                });
    }

    @Test
    void contactTakenAfterTheCodeIsNotSaved() {
        UUID challengeId = UUID.randomUUID();
        when(otpService.verifyContact(challengeId, "123456", account.getId())).thenReturn(email);
        when(lookups.customerContactTaken(IdentifierType.EMAIL, email.value(), account.getId())).thenReturn(true);

        assertThatThrownBy(() -> service.verify(challengeId, "123456"))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("CONTACT_ALREADY_IN_USE"));
        verify(accounts, never()).saveAndFlush(any());
        assertThat(account.getEmail()).isNull();
    }

    @Test
    void noCodeIsSentForAContactOfAnotherAccount() {
        when(lookups.customerContactTaken(IdentifierType.EMAIL, email.value(), account.getId())).thenReturn(true);

        assertThatThrownBy(() -> service.startVerification(IdentifierType.EMAIL, "Ana@Exemplo.co.mz", null))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("CONTACT_ALREADY_IN_USE"));
        verify(otpService, never()).requestForContact(any(), any(), any());
    }

    @Test
    void theCurrentVerifiedContactIsNotSentAgain() {
        assertThatThrownBy(() -> service.startVerification(IdentifierType.MSISDN, "+258 84 123 4567", null))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("CONTACT_ALREADY_VERIFIED"));
        verify(lookups, never()).customerContactTaken(any(), any(), eq(account.getId()));
    }
}
