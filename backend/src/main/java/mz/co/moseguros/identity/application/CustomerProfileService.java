package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.CustomerProfileRules;
import mz.co.moseguros.identity.domain.CustomerProfileRules.Changes;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Perfil do cliente autenticado ({@code /me/profile}, S1-06 §2). Corre em âmbito
 * {@code CUSTOMER}: o RLS só mostra o perfil do próprio cliente. O número do documento e o NUIT
 * nunca saem em claro, nem na resposta nem na auditoria.
 */
@Service
public class CustomerProfileService {

    private static final Logger log = LoggerFactory.getLogger(CustomerProfileService.class);

    private final CurrentUser currentUser;
    private final CustomerProfileRepository profiles;
    private final AuditService audit;
    private final Clock clock;

    public CustomerProfileService(CurrentUser currentUser, CustomerProfileRepository profiles, AuditService audit,
                                  Clock clock) {
        this.currentUser = currentUser;
        this.profiles = profiles;
        this.audit = audit;
        this.clock = clock;
    }

    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional(readOnly = true)
    public ProfileView getMyProfile() {
        return ProfileView.of(load(currentUser.id()));
    }

    /**
     * Valida e grava o perfil; auditoria {@code CUSTOMER.PROFILE_UPDATE} com os valores mascarados.
     *
     * @param version versão lida antes da alteração; diferente → {@code CONCURRENT_MODIFICATION} (409)
     * @throws DomainException {@code VALIDATION_ERROR} com {@code errors[]}
     */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional
    public ProfileView updateMyProfile(Changes changes, long version) {
        UUID userId = currentUser.id();
        CustomerProfile profile = load(userId);
        if (profile.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The profile was changed by another request (version " + profile.getVersion() + ")");
        }
        AuditState before = AuditState.of(profile);
        profile.apply(changes, LocalDate.ofInstant(clock.instant(), CustomerProfileRules.ZONE));
        profiles.saveAndFlush(profile);
        audit.record(new AuditRecord(ActorType.CUSTOMER, userId.toString(), null, "CUSTOMER.PROFILE_UPDATE",
                "CustomerProfile", profile.getId().toString(), before, AuditState.of(profile)));
        log.info("Customer {} updated the profile (complete: {})", userId, profile.isComplete());
        return ProfileView.of(profile);
    }

    private CustomerProfile load(UUID userId) {
        return profiles.findByUserId(userId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.NOT_FOUND, "Customer profile not found"));
    }

    /** Perfil como é mostrado: documento e NUIT mascarados. */
    public record ProfileView(String fullName, LocalDate birthDate, IdDocType idDocType, String idDocNumberMasked,
                              String nuitMasked, String address, Province province, boolean complete,
                              long version) {

        static ProfileView of(CustomerProfile profile) {
            return new ProfileView(profile.getFullName(), profile.getBirthDate(), profile.getIdDocType(),
                    profile.idDocNumberMasked(), profile.nuitMasked(), profile.getAddress(), profile.getProvince(),
                    profile.isComplete(), profile.getVersion());
        }
    }

    /** Estado gravado na auditoria: só valores mascarados. */
    public record AuditState(String fullName, LocalDate birthDate, IdDocType idDocType, String idDocNumberMasked,
                      String nuitMasked, String address, Province province) {

        static AuditState of(CustomerProfile profile) {
            return new AuditState(profile.getFullName(), profile.getBirthDate(), profile.getIdDocType(),
                    profile.idDocNumberMasked(), profile.nuitMasked(), profile.getAddress(), profile.getProvince());
        }
    }
}
