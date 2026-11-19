package mz.co.moseguros.shared.secrets;

import java.util.Optional;

/**
 * Cofre de segredos (S5-01 §2; modelo transversal §7): chaves M-Pesa, credenciais SFTP e outros
 * segredos das seguradoras. A BD guarda só o caminho ({@link SecretRef}), nunca o valor
 * (AGENTS.md §6).
 *
 * <p>Caminhos {@code <mount>/<resto>}: o primeiro segmento é o <i>mount</i> KV v2 configurado
 * ({@code secret}), ex.: {@code secret/insurers/<uuid>/payments/MPESA/apiKey}.
 *
 * <p>Os valores nunca vão para logs, {@code toString()}, respostas ou auditoria; o caminho pode
 * aparecer nos logs. Cofre indisponível → {@code DomainException} {@code SECRET_STORE_UNAVAILABLE}
 * (503).
 */
public interface SecretStore {

    /** Cria ou substitui o segredo. */
    void put(String path, String value);

    /** Valor actual, ou vazio se não existir. */
    Optional<String> get(String path);

    /** Apaga o segredo (todas as versões); não falha se não existir. */
    void delete(String path);

    boolean exists(String path);
}
