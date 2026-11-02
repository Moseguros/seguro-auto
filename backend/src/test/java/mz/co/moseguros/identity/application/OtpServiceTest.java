package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.OtpChallenge;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpChallenge.Purpose;
import mz.co.moseguros.identity.domain.OtpChallengeRepository;
import mz.co.moseguros.identity.domain.OtpSettings;
import mz.co.moseguros.identity.domain.OtpSettingsRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.identity.domain.UserRoleRepository;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.FieldValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Regras do {@link OtpService} sem base de dados: escolha do canal, limites, alternativa de canal,
 * validação do código e criação da conta.
 */
class OtpServiceTest {

    static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");
    static final String MSISDN = "841234567";

    final OtpSettingsRepository settingsRepository = mock(OtpSettingsRepository.class);
    final OtpChallengeRepository challenges = mock(OtpChallengeRepository.class);
    final UserAccountRepository accounts = mock(UserAccountRepository.class);
    final CustomerProfileRepository profiles = mock(CustomerProfileRepository.class);
    final UserRoleRepository userRoles = mock(UserRoleRepository.class);
    final TokenService tokenService = mock(TokenService.class);
    final AuditService audit = mock(AuditService.class);
    final NotificationApi notifications = mock(NotificationApi.class);
    final OtpCodeHasher hasher = new OtpCodeHasher("unit-test-pepper");

    /** Desafios "gravados" pelo repositório falso. */
    final List<OtpChallenge> saved = new ArrayList<>();
    OtpSettings settings;
    OtpService service;

    @BeforeEach
    void setUp() throws Exception {
        settings = newSettings();
        settings.update(List.of(Channel.SMS, Channel.WHATSAPP, Channel.EMAIL), Channel.SMS, true,
                List.of(Channel.SMS, Channel.WHATSAPP, Channel.EMAIL), 6, 300, 3, 60, 5);
        when(settingsRepository.findFirstBy()).thenReturn(Optional.of(settings));
        when(challenges.save(any(OtpChallenge.class))).thenAnswer(invocation -> {
            OtpChallenge challenge = invocation.getArgument(0);
            setField(challenge, "createdAt", NOW.plusMillis(saved.size()));
            saved.add(challenge);
            return challenge;
        });
        when(challenges.findById(any())).thenAnswer(invocation -> saved.stream()
                .filter(c -> c.getId().equals(invocation.getArgument(0))).findFirst());
        when(challenges.findByRootChallengeId(any())).thenAnswer(invocation -> saved.stream()
                .filter(c -> c.getRootChallengeId().equals(invocation.getArgument(0))).toList());
        when(challenges.findChainForUpdate(any())).thenAnswer(invocation -> saved.stream()
                .filter(c -> c.getRootChallengeId().equals(invocation.getArgument(0))).toList());
        when(challenges.findRootId(any())).thenAnswer(invocation -> saved.stream()
                .filter(c -> c.getId().equals(invocation.getArgument(0))).findFirst()
                .map(OtpChallenge::getRootChallengeId));
        when(challenges.findRequestTimesSince(anyString(), any(), any())).thenReturn(List.of());
        when(accounts.findByTypeAndMsisdn(any(), any())).thenReturn(Optional.empty());
        when(accounts.findByTypeAndEmail(any(), any())).thenReturn(Optional.empty());
        when(tokenService.issue(any(), any())).thenReturn(new TokenPair("access", "refresh", 900));
        service = service(false);
    }

    OtpService service(boolean debugHeader) {
        TransactionTemplate tx = new TransactionTemplate(mock(PlatformTransactionManager.class));
        return new OtpService(settingsRepository, challenges, accounts, profiles, userRoles, tokenService, audit,
                notifications, hasher, new OtpProperties("unit-test-pepper", debugHeader), tx,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void requestUsesTheDefaultChannelAndHashesTheCode() {
        Map<Channel, String> codes = deliverAll();

        OtpService.RequestResult result = service.request(MSISDN, IdentifierType.MSISDN, null);

        assertThat(result.channel()).isEqualTo(Channel.SMS);
        assertThat(result.fallbackUsed()).isFalse();
        assertThat(result.debugCode()).isNull();
        assertThat(result.expiresAt()).isEqualTo(NOW.plusSeconds(300));
        OtpChallenge challenge = saved.get(0);
        assertThat(challenge.getIdentifier()).isEqualTo("258841234567");
        assertThat(challenge.getDeliveryStatus()).isEqualTo(OtpChallenge.DeliveryStatus.SENT);
        assertThat(challenge.getCodeHash()).hasSize(64).isNotEqualTo(codes.get(Channel.SMS));
        assertThat(hasher.matches(challenge.getId(), codes.get(Channel.SMS), challenge.getCodeHash())).isTrue();
        verify(notifications).sendOtp(eq(Channel.SMS), eq("+258841234567"), anyString(), eq(5), eq("pt"),
                eq(challenge.getId()));
    }

    @Test
    void debugHeaderReturnsTheCodeOnlyWhenEnabled() {
        Map<Channel, String> codes = deliverAll();

        OtpService.RequestResult result = service(true).request(MSISDN, IdentifierType.MSISDN, null);

        assertThat(result.debugCode()).isEqualTo(codes.get(Channel.SMS));
        assertThat(result.toString()).doesNotContain(codes.get(Channel.SMS));
    }

    @Test
    void synchronousFailureFallsBackToTheNextCompatibleChannel() {
        Map<Channel, String> codes = deliverAll(Channel.SMS);

        OtpService.RequestResult result = service.request(MSISDN, IdentifierType.MSISDN, null);

        assertThat(result.channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(result.fallbackUsed()).isTrue();
        assertThat(result.challengeId()).isEqualTo(saved.get(0).getId());
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getDeliveryStatus()).isEqualTo(OtpChallenge.DeliveryStatus.FAILED);
        assertThat(saved.get(0).getSupersededAt()).isEqualTo(NOW);
        assertThat(saved.get(1).getFallbackFromChallengeId()).isEqualTo(saved.get(0).getId());
        assertThat(saved.get(1).getRootChallengeId()).isEqualTo(saved.get(0).getId());
        assertThat(codes).containsKey(Channel.WHATSAPP);
    }

    @Test
    void allMobileChannelsFailingIsADeliveryFailureAndEmailIsNeverTried() {
        deliverAll(Channel.SMS, Channel.WHATSAPP);

        assertCode(() -> service.request(MSISDN, IdentifierType.MSISDN, null), "OTP_DELIVERY_FAILED", 502);
        verify(notifications, never()).sendOtp(eq(Channel.EMAIL), any(), any(), anyInt(), any(), any());
    }

    @Test
    void channelRules() {
        deliverAll();
        assertCode(() -> service.request(MSISDN, IdentifierType.MSISDN, Channel.EMAIL),
                "OTP_CHANNEL_NOT_AVAILABLE", 400);
        settings.update(List.of(Channel.SMS, Channel.EMAIL), Channel.SMS, false, List.of(Channel.SMS, Channel.EMAIL),
                6, 300, 3, 60, 5);
        assertCode(() -> service.request(MSISDN, IdentifierType.MSISDN, Channel.SMS),
                "OTP_CHANNEL_CHOICE_DISABLED", 400);
        settings.update(List.of(Channel.EMAIL), Channel.EMAIL, true, List.of(Channel.EMAIL), 6, 300, 3, 60, 5);
        assertCode(() -> service.request(MSISDN, IdentifierType.MSISDN, null), "OTP_CHANNEL_NOT_AVAILABLE", 400);
    }

    @Test
    void invalidIdentifierIsAFieldError() {
        assertThatThrownBy(() -> service.request("123", IdentifierType.MSISDN, null))
                .isInstanceOfSatisfying(FieldValidationException.class, e -> {
                    assertThat(e.getField()).isEqualTo("identifier");
                    assertThat(e.getFieldCode()).isEqualTo("INVALID_MSISDN");
                });
    }

    @Test
    void limitsAddRetryAfter() {
        when(challenges.findRequestTimesSince(anyString(), any(), any()))
                .thenReturn(List.of(NOW.minusSeconds(3000), NOW.minusSeconds(2000), NOW.minusSeconds(1000),
                        NOW.minusSeconds(500), NOW.minusSeconds(100)));
        assertThatThrownBy(() -> service.request(MSISDN, IdentifierType.MSISDN, null))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("OTP_RATE_LIMITED");
                    assertThat(e.getHttpStatus()).isEqualTo(429);
                    assertThat(e.getProperties()).containsEntry("retryAfterSeconds", 600L);
                });

        when(challenges.findRequestTimesSince(anyString(), any(), any())).thenReturn(List.of(NOW.minusSeconds(45)));
        when(challenges.findLastRequestTime(anyString(), any())).thenReturn(NOW.minusSeconds(45));
        assertThatThrownBy(() -> service.request(MSISDN, IdentifierType.MSISDN, null))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("OTP_RESEND_TOO_SOON");
                    assertThat(e.getProperties()).containsEntry("retryAfterSeconds", 15L);
                });
    }

    @Test
    void verifyCreatesTheCustomerAccountOnFirstLogin() {
        Map<Channel, String> codes = deliverAll();
        UUID challengeId = service.request(MSISDN, IdentifierType.MSISDN, null).challengeId();

        OtpService.LoginResult result = service.verify(challengeId, codes.get(Channel.SMS), "JUnit");

        assertThat(result.newUser()).isTrue();
        assertThat(result.tokens().accessToken()).isEqualTo("access");
        assertThat(saved.get(0).getConsumedAt()).isEqualTo(NOW);
        ArgumentCaptor<UserAccount> account = ArgumentCaptor.forClass(UserAccount.class);
        verify(accounts).save(account.capture());
        assertThat(account.getValue().getMsisdn()).isEqualTo(Msisdn.of(MSISDN));
        assertThat(account.getValue().isMsisdnVerified()).isTrue();
        assertThat(account.getValue().getLastLoginAt()).isEqualTo(NOW);
        ArgumentCaptor<AuditRecord> record = ArgumentCaptor.forClass(AuditRecord.class);
        verify(audit).record(record.capture());
        assertThat(record.getValue().action()).isEqualTo("CUSTOMER.REGISTER");
        assertThat(record.getValue().afterState().toString()).doesNotContain("841234567");
        verify(tokenService).issue(account.getValue(), "JUnit");
    }

    @Test
    void verifyWithExistingAccount() {
        Map<Channel, String> codes = deliverAll();
        UserAccount existing = UserAccount.newCustomer(Msisdn.of(MSISDN));
        when(accounts.findByTypeAndMsisdn(AccountType.CUSTOMER, Msisdn.of(MSISDN))).thenReturn(Optional.of(existing));
        UUID challengeId = service.request(MSISDN, IdentifierType.MSISDN, null).challengeId();
        assertThat(saved.get(0).getUserId()).isEqualTo(existing.getId());

        OtpService.LoginResult result = service.verify(challengeId, codes.get(Channel.SMS), null);

        assertThat(result.newUser()).isFalse();
        verify(accounts, never()).save(any());
        assertThat(existing.getLastLoginAt()).isEqualTo(NOW);
    }

    @Test
    void wrongCodesCountAttempts() {
        Map<Channel, String> codes = deliverAll();
        UUID challengeId = service.request(MSISDN, IdentifierType.MSISDN, null).challengeId();
        String wrong = codes.get(Channel.SMS).equals("000000") ? "000001" : "000000";

        assertThatThrownBy(() -> service.verify(challengeId, wrong, null))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("OTP_INVALID");
                    assertThat(e.getProperties()).containsEntry("attemptsRemaining", 2);
                });
        assertCode(() -> service.verify(challengeId, wrong, null), "OTP_INVALID", 400);
        assertCode(() -> service.verify(challengeId, wrong, null), "OTP_ATTEMPTS_EXCEEDED", 400);
        assertCode(() -> service.verify(challengeId, codes.get(Channel.SMS), null), "OTP_ATTEMPTS_EXCEEDED", 400);
        assertThat(saved.get(0).getAttempts()).isEqualTo(3);
    }

    @Test
    void verifyRejectsUsedSupersededAndTestChallenges() {
        Map<Channel, String> codes = deliverAll();
        UUID challengeId = service.request(MSISDN, IdentifierType.MSISDN, null).challengeId();
        service.verify(challengeId, codes.get(Channel.SMS), null);
        assertCode(() -> service.verify(challengeId, codes.get(Channel.SMS), null), "OTP_ALREADY_USED", 400);

        UUID second = service.request("841234568", IdentifierType.MSISDN, null).challengeId();
        saved.get(1).supersede(NOW);
        assertCode(() -> service.verify(second, codes.get(Channel.SMS), null), "OTP_SUPERSEDED", 400);

        OtpService.TestResult test = service.sendTest("841234569", IdentifierType.MSISDN, Channel.WHATSAPP);
        assertThat(test.deliveryStatus()).isEqualTo(OtpChallenge.DeliveryStatus.SENT);
        assertThat(saved.get(2).getPurpose()).isEqualTo(Purpose.TEST);
        assertCode(() -> service.verify(saved.get(2).getId(), codes.get(Channel.WHATSAPP), null),
                "OTP_CHALLENGE_NOT_FOUND", 404);
        assertCode(() -> service.verify(UUID.randomUUID(), "123456", null), "OTP_CHALLENGE_NOT_FOUND", 404);
    }

    @Test
    void sendTestRejectsAnIncompatibleChannelAndNeverFallsBack() {
        deliverAll(Channel.WHATSAPP);
        assertCode(() -> service.sendTest(MSISDN, IdentifierType.MSISDN, Channel.EMAIL),
                "OTP_CHANNEL_NOT_AVAILABLE", 400);

        OtpService.TestResult result = service.sendTest(MSISDN, IdentifierType.MSISDN, Channel.WHATSAPP);

        assertThat(result.deliveryStatus()).isEqualTo(OtpChallenge.DeliveryStatus.FAILED);
        assertThat(saved).hasSize(1);
    }

    @Test
    void receiptFailureFallsBackOnlyWithinThirtySeconds() throws Exception {
        Map<Channel, String> codes = deliverAll();
        UUID challengeId = service.request(MSISDN, IdentifierType.MSISDN, null).challengeId();

        service.fallbackAfterDeliveryFailure(challengeId);

        assertThat(saved).hasSize(2);
        assertThat(saved.get(1).getChannel()).isEqualTo(Channel.WHATSAPP);
        assertThat(service.challenge(challengeId).channel()).isEqualTo(Channel.WHATSAPP);
        assertThat(service.challenge(challengeId).fallbackUsed()).isTrue();
        assertThat(codes).containsKey(Channel.WHATSAPP);

        UUID late = service.request("841234560", IdentifierType.MSISDN, null).challengeId();
        setField(saved.get(2), "sentAt", NOW.minusSeconds(31));
        service.fallbackAfterDeliveryFailure(late);
        assertThat(saved).hasSize(3);
        assertThat(saved.get(2).getDeliveryStatus()).isEqualTo(OtpChallenge.DeliveryStatus.FAILED);
        assertThat(service.challenge(late).channel()).isEqualTo(Channel.SMS);

        service.fallbackAfterDeliveryFailure(null);
        service.fallbackAfterDeliveryFailure(UUID.randomUUID());
        assertThat(saved).hasSize(3);
    }

    @Test
    void optionsFollowTheSettings() {
        OtpService.Options options = service.options();
        assertThat(options.identifierTypes()).containsExactly(IdentifierType.MSISDN, IdentifierType.EMAIL);
        assertThat(options.channels().get(IdentifierType.MSISDN)).containsExactly(Channel.SMS, Channel.WHATSAPP);
        assertThat(options.defaultChannels()).containsEntry(IdentifierType.EMAIL, Channel.EMAIL);

        settings.update(List.of(Channel.WHATSAPP, Channel.EMAIL), Channel.EMAIL, true,
                List.of(Channel.EMAIL, Channel.WHATSAPP), 6, 300, 3, 60, 5);
        options = service.options();
        assertThat(options.channels().get(IdentifierType.MSISDN)).containsExactly(Channel.WHATSAPP);
        assertThat(options.defaultChannels()).containsEntry(IdentifierType.MSISDN, Channel.WHATSAPP);
    }

    @Test
    void helpers() {
        assertThat(OtpService.secondsUntil(NOW.plusMillis(1500), NOW)).isEqualTo(2);
        assertThat(OtpService.secondsUntil(NOW.minusSeconds(5), NOW)).isEqualTo(1);
        assertThat(OtpService.validityMinutes(settings)).isEqualTo(5);
    }

    /** O envio pelos canais indicados falha; os outros têm sucesso. Devolve o último código por canal. */
    private Map<Channel, String> deliverAll(Channel... failing) {
        Map<Channel, String> codes = new java.util.EnumMap<>(Channel.class);
        List<Channel> failingChannels = List.of(failing);
        when(notifications.sendOtp(any(), anyString(), anyString(), anyInt(), anyString(), any()))
                .thenAnswer(invocation -> {
                    Channel channel = invocation.getArgument(0);
                    if (failingChannels.contains(channel)) {
                        return new DeliveryResult(UUID.randomUUID(), DeliveryStatus.FAILED, "HTTP 503");
                    }
                    codes.put(channel, invocation.getArgument(2));
                    return new DeliveryResult(UUID.randomUUID(), DeliveryStatus.SENT, null);
                });
        return codes;
    }

    private static void assertCode(Runnable action, String code, int status) {
        assertThatThrownBy(action::run).isInstanceOfSatisfying(DomainException.class, e -> {
            assertThat(e.getCode()).isEqualTo(code);
            assertThat(e.getHttpStatus()).isEqualTo(status);
        });
    }

    private static OtpSettings newSettings() throws Exception {
        var constructor = OtpSettings.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
