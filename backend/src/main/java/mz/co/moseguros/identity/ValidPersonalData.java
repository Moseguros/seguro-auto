package mz.co.moseguros.identity;

import java.time.LocalDate;

import mz.co.moseguros.shared.domain.Province;

/**
 * {@link PersonalData} válidos e normalizados (nome com um só espaço entre palavras, documento em
 * maiúsculas sem espaços, NUIT só com os dígitos, morada sem espaços nas pontas ou {@code null}).
 *
 * @param idDocNumber {@code null} = manter o actual
 * @param nuit        {@code null} = manter o actual
 */
public record ValidPersonalData(String fullName, LocalDate birthDate, String idDocType, String idDocNumber,
                                String nuit, String address, Province province) {

    @Override
    public String toString() {
        return "ValidPersonalData[idDocType=" + idDocType + ", province=" + province + "]";
    }
}
