package mz.co.moseguros.catalog.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import mz.co.moseguros.catalog.domain.PlatformSetting;
import mz.co.moseguros.catalog.domain.PlatformSettingRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Parâmetros da plataforma (S2-02 §5 e §6.6). A lista é pública (nunca há segredos nesta tabela);
 * só o valor se altera, com {@code reference:manage}. Sem cache: uma alteração vê-se logo a seguir.
 */
@Service
public class PlatformSettingService {

    private final PlatformSettingRepository settings;
    private final AuditService audit;
    private final CurrentUser currentUser;

    public PlatformSettingService(PlatformSettingRepository settings, AuditService audit, CurrentUser currentUser) {
        this.settings = settings;
        this.audit = audit;
        this.currentUser = currentUser;
    }

    /** Todos os parâmetros, por chave (público). */
    @Transactional(readOnly = true)
    public List<PlatformSetting> listPublic() {
        return settings.findAllByOrderByKeyAsc();
    }

    /** Todos os parâmetros, por chave, com versão e autor da última alteração. */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional(readOnly = true)
    public List<PlatformSetting> list() {
        return settings.findAllByOrderByKeyAsc();
    }

    /** Valor de um parâmetro, sem verificar permissões (para outros módulos, via {@code CatalogApi}). */
    @Transactional(readOnly = true)
    public Optional<String> value(String key) {
        return settings.findByKey(key).map(PlatformSetting::getValue);
    }

    /**
     * Altera o valor.
     *
     * @param version versão lida; diferente → {@code CONCURRENT_MODIFICATION} (409)
     * @throws DomainException {@code PLATFORM_SETTING_NOT_FOUND} (404) ou {@code VALIDATION_ERROR}
     *                         com {@code INVALID_VALUE} no campo {@code value}
     */
    @PreAuthorize("hasAuthority('reference:manage')")
    @Transactional
    public PlatformSetting update(String key, String value, long version) {
        PlatformSetting setting = settings.findByKey(key).orElseThrow(() -> DomainException.of(
                ErrorCodes.PLATFORM_SETTING_NOT_FOUND, "Platform setting not found"));
        if (setting.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The platform setting was changed by another request");
        }
        Snapshot before = Snapshot.of(setting);
        setting.changeValue(value);
        settings.flush();
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), null, "PLATFORM_SETTING.UPDATE",
                "PlatformSetting", setting.getId().toString(), before, Snapshot.of(setting)));
        return setting;
    }

    /** Estado auditado (antes e depois). */
    public record Snapshot(String key, String valueType, String value, long version, Instant updatedAt) {

        static Snapshot of(PlatformSetting s) {
            return new Snapshot(s.getKey(), s.getValueType().name(), s.getValue(), s.getVersion(), s.getUpdatedAt());
        }
    }
}
