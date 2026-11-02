package mz.co.moseguros.identity.application;

import java.time.Instant;
import java.util.List;

import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpSettings;
import mz.co.moseguros.identity.domain.OtpSettingsRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Definições do OTP do cliente ({@code identity.otp_settings}), geridas pela plataforma. Não há
 * cache: o {@link OtpService} lê-as em cada pedido, por isso uma alteração vale logo.
 */
@Service
public class OtpSettingsService {

    private static final Logger log = LoggerFactory.getLogger(OtpSettingsService.class);

    private final OtpSettingsRepository repository;
    private final OtpService otpService;
    private final AuditService audit;
    private final CurrentUser currentUser;

    public OtpSettingsService(OtpSettingsRepository repository, OtpService otpService, AuditService audit,
                              CurrentUser currentUser) {
        this.repository = repository;
        this.otpService = otpService;
        this.audit = audit;
        this.currentUser = currentUser;
    }

    @PreAuthorize("hasAuthority('otp-settings:read')")
    @Transactional(readOnly = true)
    public Snapshot get() {
        return Snapshot.of(settings());
    }

    /**
     * Substitui as definições (todos os campos).
     *
     * @throws DomainException {@code CONCURRENT_MODIFICATION} (409) se {@code version} não for a
     *                         gravada; {@code VALIDATION_ERROR} (400) com o erro do campo
     */
    @PreAuthorize("hasAuthority('otp-settings:manage')")
    @Transactional
    public Snapshot update(Update update) {
        OtpSettings settings = settings();
        if (update.version() != settings.getVersion()) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The OTP settings were changed by another request");
        }
        Snapshot before = Snapshot.of(settings);
        settings.update(update.enabledChannels(), update.defaultChannel(), update.customerCanChoose(),
                update.fallbackOrder(), update.codeLength(), update.validitySeconds(), update.maxAttempts(),
                update.resendAfterSeconds(), update.maxRequestsPerHour());
        repository.saveAndFlush(settings);
        Snapshot after = Snapshot.of(settings);
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), null, "OTP_SETTINGS.UPDATE",
                "OtpSettings", settings.getId().toString(), before, after));
        log.info("OTP settings updated to version {}", settings.getVersion());
        return after;
    }

    /** Envio de teste ({@link OtpService#sendTest}), só com a permissão de gerir as definições. */
    @PreAuthorize("hasAuthority('otp-settings:manage')")
    public OtpService.TestResult sendTest(String identifier, IdentifierType identifierType, Channel channel) {
        return otpService.sendTest(identifier, identifierType, channel);
    }

    private OtpSettings settings() {
        return repository.findFirstBy()
                .orElseThrow(() -> new IllegalStateException("identity.otp_settings has no row"));
    }

    /** Novas definições, com a {@code version} lida antes da alteração. */
    public record Update(List<Channel> enabledChannels, Channel defaultChannel, boolean customerCanChoose,
                         List<Channel> fallbackOrder, int codeLength, int validitySeconds, int maxAttempts,
                         int resendAfterSeconds, int maxRequestsPerHour, long version) {
    }

    /** Definições como são devolvidas e auditadas (sem dados pessoais). */
    public record Snapshot(List<Channel> enabledChannels, Channel defaultChannel, boolean customerCanChoose,
                           List<Channel> fallbackOrder, int codeLength, int validitySeconds, int maxAttempts,
                           int resendAfterSeconds, int maxRequestsPerHour, long version, Instant updatedAt,
                           String updatedBy) {

        static Snapshot of(OtpSettings s) {
            return new Snapshot(s.getEnabledChannels(), s.getDefaultChannel(), s.isCustomerCanChoose(),
                    s.getFallbackOrder(), s.getCodeLength(), s.getValiditySeconds(), s.getMaxAttempts(),
                    s.getResendAfterSeconds(), s.getMaxRequestsPerHour(), s.getVersion(), s.getUpdatedAt(),
                    s.getUpdatedBy());
        }
    }
}
