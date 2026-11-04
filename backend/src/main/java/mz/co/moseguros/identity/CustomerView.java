package mz.co.moseguros.identity;

import java.time.LocalDate;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Dados de um cliente para outros módulos ({@link IdentityApi#customer}; S4-01 §6). Não é um DTO
 * de API: nenhum controlador o devolve. {@code idDocNumber} e {@code nuit} vêm em claro: nunca se
 * serializam nem vão para um log; {@link #toString()} não os inclui (nem o telemóvel nem o e-mail
 * em claro).
 *
 * @param idDocType     {@code BI}, {@code PASSPORT}, {@code DIRE} ou {@code null}
 * @param msisdn        {@code null} se a conta só tem e-mail
 * @param accountActive {@code user_account.status = ACTIVE} (o S4-06 recusa contas {@code DISABLED})
 */
public record CustomerView(UUID userId, String fullName, LocalDate birthDate, String idDocType,
                           String idDocNumber,
                           String nuit,
                           String address, String province, Msisdn msisdn, boolean msisdnVerified,
                           String email, boolean emailVerified, String preferredLanguage,
                           boolean profileComplete,
                           boolean accountActive) {

    @Override
    public String toString() {
        return "CustomerView[userId=" + userId
                + ", idDocType=" + idDocType
                + ", province=" + province
                + ", msisdn=" + (msisdn == null ? null : msisdn.masked())
                + ", msisdnVerified=" + msisdnVerified
                + ", email=" + (email == null ? null : "***")
                + ", emailVerified=" + emailVerified
                + ", preferredLanguage=" + preferredLanguage
                + ", profileComplete=" + profileComplete
                + ", accountActive=" + accountActive + "]";
    }
}
