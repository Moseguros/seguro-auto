package mz.co.moseguros.identity;

import java.time.LocalDate;

import mz.co.moseguros.shared.domain.Province;

/**
 * Dados pessoais de uma pessoa singular com as regras do perfil do cliente (S1-06 §2), validados por
 * {@link IdentityApi#validatePersonalData}. Usado pelo {@code proposal} para o tomador preenchido pelo
 * mediador (S4-06 §5). {@code idDocNumber} e {@code nuit} vêm em claro: {@link #toString()} não os
 * inclui.
 *
 * @param idDocType   {@code BI}, {@code PASSPORT} ou {@code DIRE}
 * @param idDocNumber {@code null} mantém o actual (se existir e o tipo não mudar)
 * @param nuit        {@code null} mantém o actual (se existir)
 */
public record PersonalData(String fullName, LocalDate birthDate, String idDocType, String idDocNumber, String nuit,
                           String address, Province province) {

    @Override
    public String toString() {
        return "PersonalData[idDocType=" + idDocType + ", province=" + province + "]";
    }
}
