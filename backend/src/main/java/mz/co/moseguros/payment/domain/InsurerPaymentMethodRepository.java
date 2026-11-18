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

    /** Linhas das seguradoras indicadas (só as que têm segredos de desenvolvimento, S5-01 §8). */
    List<InsurerPaymentMethod> findByInsurerIdIn(List<UUID> insurerIds);

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
