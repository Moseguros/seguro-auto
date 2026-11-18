package mz.co.moseguros.payment.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
