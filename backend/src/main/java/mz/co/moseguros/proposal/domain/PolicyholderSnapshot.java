package mz.co.moseguros.proposal.domain;

import java.time.LocalDate;

import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Tomador do seguro copiado do perfil do cliente ({@code proposal.policyholder}, S4-02 §5). Só com
 * o documento e o NUIT mascarados: os valores em claro ficam cifrados nas colunas
 * {@code policyholder_id_doc_number_enc} e {@code policyholder_nuit_enc}.
 *
 * @param idDocType         {@code BI}, {@code PASSPORT} ou {@code DIRE}
 * @param idDocNumberMasked {@code *********345A}
 * @param nuitMasked        {@code *****6789}
 * @param province          código {@code Province}
 * @param msisdn            E.164 ({@code +258841234567}); {@code null} se a conta só tiver e-mail
 * @param email             {@code null} se a conta não tiver e-mail
 */
public record PolicyholderSnapshot(String fullName, LocalDate birthDate, String idDocType, String idDocNumberMasked,
                                   String nuitMasked, String address, String province, String msisdn, String email,
                                   String preferredLanguage) {

    /** {@code +258*****4567}; {@code null} sem telemóvel. */
    public String msisdnMasked() {
        return msisdn == null ? null : Msisdn.of(msisdn).masked();
    }

    /** {@code a***@exemplo.co.mz} (primeira letra e domínio); {@code null} sem e-mail. */
    public String emailMasked() {
        if (email == null) {
            return null;
        }
        int at = email.indexOf('@');
        if (at < 0) {
            return "***";
        }
        String local = email.substring(0, at);
        return (local.isEmpty() ? "" : local.substring(0, 1)) + "***" + email.substring(at);
    }
}
