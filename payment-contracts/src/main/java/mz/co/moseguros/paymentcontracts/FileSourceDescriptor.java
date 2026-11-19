package mz.co.moseguros.paymentcontracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import java.util.UUID;

/**
 * Uma fonte de ficheiros de movimentos, como o backend a entrega ao worker (contrato REST interno
 * {@code GET /internal/v1/payment-file-sources}, S5-09). Nunca leva segredos: só as referências
 * do cofre. Schema: {@code schemas/file-source-list.v1.json}.
 *
 * @param sourceId identificador da fonte
 * @param insurerId seguradora dona da fonte
 * @param version versão da configuração (muda sempre que a configuração muda)
 * @param enabled se a fonte deve ser lida
 * @param entity entidade da seguradora na rede Ponto24 (5 dígitos)
 * @param fileFormat {@value #FORMAT_MEPS} ou {@value #FORMAT_BMEPS}
 * @param bmepsCompanyId ID da empresa no BMEPS (3 dígitos); só BMEPS
 * @param bmepsProfileId ID do perfil no BMEPS (2 dígitos); só BMEPS
 * @param sftp servidor SFTP onde o banco deposita os ficheiros
 * @param secretRefs campo do segredo → referência no cofre (ex.: {@code "sftp.password"} → {@code
 *     "secret/insurers/…/payments/BANK_REFERENCE/sftp.password"})
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FileSourceDescriptor(
        UUID sourceId,
        UUID insurerId,
        long version,
        boolean enabled,
        String entity,
        String fileFormat,
        String bmepsCompanyId,
        String bmepsProfileId,
        Sftp sftp,
        Map<String, String> secretRefs) {

    public static final String FORMAT_MEPS = "MEPS";
    public static final String FORMAT_BMEPS = "BMEPS";

    /**
     * Ligação SFTP da fonte.
     *
     * @param authType {@value #AUTH_PASSWORD} ou {@value #AUTH_KEY}
     * @param processedDir {@code null} sem mudança de pasta ({@code moveAfterProcessing = false})
     * @param errorDir idem
     * @param filePattern glob dos ficheiros a ler (defeito {@value #DEFAULT_FILE_PATTERN})
     * @param hostKeyFingerprint {@code SHA256:…} da chave do servidor; pode ser {@code null} em
     *     {@code local} e {@code test}
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Sftp(
            String host,
            int port,
            String username,
            String authType,
            String inboundDir,
            boolean moveAfterProcessing,
            String processedDir,
            String errorDir,
            String filePattern,
            String hostKeyFingerprint) {

        public static final String AUTH_PASSWORD = "PASSWORD";
        public static final String AUTH_KEY = "KEY";
        public static final String DEFAULT_FILE_PATTERN = "*";
    }
}
