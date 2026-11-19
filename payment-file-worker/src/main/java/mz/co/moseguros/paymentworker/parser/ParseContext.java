package mz.co.moseguros.paymentworker.parser;

import java.time.ZoneId;

/**
 * Dados da fonte que o leitor precisa (S5-08 §1).
 *
 * @param sourceEntity   entidade da seguradora na fonte (5 dígitos)
 * @param bmepsCompanyId ID da empresa no BMEPS; {@code null} nos outros formatos
 * @param bmepsProfileId ID do perfil no BMEPS; {@code null} nos outros formatos
 * @param zone           fuso das datas e horas do ficheiro ({@code Africa/Maputo})
 * @param fileName       nome do ficheiro no SFTP (só para mensagens)
 */
public record ParseContext(String sourceEntity, String bmepsCompanyId, String bmepsProfileId, ZoneId zone,
                           String fileName) {

    public static final ZoneId MAPUTO = ZoneId.of("Africa/Maputo");
}
