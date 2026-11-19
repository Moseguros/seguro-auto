package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Formas de pagamento das seguradoras. Em âmbito {@code ORG} o RLS mostra só as da própria
 * seguradora; as leituras entre seguradoras passam pelo {@code PaymentSystemLookups} (SYSTEM), que
 * filtra sempre por {@code insurerId} no SQL.
 */
public interface InsurerPaymentMethodRepository extends JpaRepository<InsurerPaymentMethod, UUID> {

    Optional<InsurerPaymentMethod> findByInsurerIdAndMethod(UUID insurerId, PaymentMethod method);

    List<InsurerPaymentMethod> findByInsurerId(UUID insurerId);

    List<InsurerPaymentMethod> findByInsurerIdAndEnabledTrueOrderBySortOrderAscMethodAsc(UUID insurerId);

    Optional<InsurerPaymentMethod> findByInsurerIdAndMethodAndEnabledTrue(UUID insurerId, PaymentMethod method);

    /** Há {@code BANK_REFERENCE} de outra seguradora (activo ou não) com esta entidade. */
    @Query(value = """
            select exists (select 1 from payment.insurer_payment_method
                            where method = 'BANK_REFERENCE' and settings ->> 'entity' = :entity
                              and insurer_id <> :insurerId)""", nativeQuery = true)
    boolean entityUsedByAnotherInsurer(@Param("entity") String entity, @Param("insurerId") UUID insurerId);

    /**
     * Fontes de ficheiros de movimentos (S5-09 §1), de todas as seguradoras: os {@code BANK_REFERENCE}
     * activos e os inactivos cuja seguradora ainda tem referências {@code ACTIVE} (o banco continua a
     * aceitar pagamentos delas e os ficheiros têm de ser lidos). Só em {@code SYSTEM}.
     */
    @Query(value = """
            select m.* from payment.insurer_payment_method m
             where m.method = 'BANK_REFERENCE'
               and (m.enabled
                    or exists (select 1 from payment.reference_payment r
                                where r.insurer_id = m.insurer_id and r.status = 'ACTIVE'))
             order by m.insurer_id""", nativeQuery = true)
    List<InsurerPaymentMethod> findFileSources();

    /** Linhas das seguradoras indicadas (só as que têm segredos de desenvolvimento, S5-01 §8). */
    List<InsurerPaymentMethod> findByInsurerIdIn(List<UUID> insurerIds);

    // --- Leituras da fonte de ficheiros (S5-10): updates sem mudar a version ---

    /** Há uma linha {@code BANK_REFERENCE} com este id e esta seguradora. */
    @Query(value = """
            select exists (select 1 from payment.insurer_payment_method
                            where id = :id and insurer_id = :insurerId and method = 'BANK_REFERENCE')""",
            nativeQuery = true)
    boolean isFileSourceOf(@Param("id") UUID id, @Param("insurerId") UUID insurerId);

    /**
     * Resultado de uma leitura ({@code PaymentSourcePolled}), só se for mais recente do que a gravada
     * (mensagens fora de ordem são ignoradas). Não muda a {@code version}.
     *
     * @return linhas alteradas (0 = fonte desconhecida ou leitura mais antiga)
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            update payment.insurer_payment_method
               set last_poll_at = :startedAt,
                   last_poll_outcome = :outcome,
                   last_poll_error = cast(:error as varchar),
                   last_poll_success_at = case when :outcome = 'OK' then cast(:finishedAt as timestamptz)
                                               else last_poll_success_at end
             where id = :id and insurer_id = :insurerId and method = 'BANK_REFERENCE'
               and (last_poll_at is null or last_poll_at < :startedAt)""", nativeQuery = true)
    int recordPoll(@Param("id") UUID id, @Param("insurerId") UUID insurerId, @Param("startedAt") Instant startedAt,
                   @Param("finishedAt") Instant finishedAt, @Param("outcome") String outcome,
                   @Param("error") String error);

    /** Estado das fontes {@code BANK_REFERENCE} activas, para o {@code PaymentSourceHealthJob} (SYSTEM). */
    @Query("""
            select new mz.co.moseguros.payment.domain.FileSourceHealth(m.id, m.insurerId, m.enabledAt,
                   m.lastPollSuccessAt, m.staleSince)
              from InsurerPaymentMethod m
             where m.method = :method and m.enabled = true
             order by m.insurerId""")
    List<FileSourceHealth> enabledSourcesHealth(@Param("method") PaymentMethod method);

    /** Marca a fonte como parada desde {@code at}, se ainda não estava. Não muda a {@code version}. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            update payment.insurer_payment_method set stale_since = :at
             where id = :id and stale_since is null""", nativeQuery = true)
    int markStale(@Param("id") UUID id, @Param("at") Instant at);

    /** A fonte voltou a ler com sucesso. Não muda a {@code version}. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            update payment.insurer_payment_method set stale_since = null
             where id = :id and stale_since is not null""", nativeQuery = true)
    int clearStale(@Param("id") UUID id);

    /** Todas as fontes {@code BANK_REFERENCE} (activas e inactivas) visíveis no âmbito (PLATFORM: todas). */
    List<InsurerPaymentMethod> findByMethod(PaymentMethod method);

    /**
     * Último erro de configuração detectado num pagamento (alerta {@code PAYMENT_CONFIG_ERROR}, S5-04 §5).
     * Não muda a {@code version}: não é uma edição da configuração e não pode recusar a do administrador.
     */
    @Modifying
    @Query(value = """
            update payment.insurer_payment_method set last_error_code = :code, last_error_at = :at
             where id = :id and insurer_id = :insurerId""", nativeQuery = true)
    int recordLastError(@Param("id") UUID id, @Param("insurerId") UUID insurerId, @Param("code") String code,
                        @Param("at") Instant at);

    /** Limpa o último erro (primeiro {@code INS-0} seguinte); sem erro gravado não escreve nada. */
    @Modifying
    @Query(value = """
            update payment.insurer_payment_method set last_error_code = null, last_error_at = null
             where id = :id and insurer_id = :insurerId and last_error_code is not null""", nativeQuery = true)
    int clearLastError(@Param("id") UUID id, @Param("insurerId") UUID insurerId);
}
