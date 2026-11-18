package mz.co.moseguros.payment;

import static mz.co.moseguros.payment.PaymentTestSupport.action;
import static mz.co.moseguros.payment.PaymentTestSupport.body;
import static mz.co.moseguros.payment.PaymentTestSupport.put;
import static mz.co.moseguros.payment.PaymentTestSupport.version;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Apoio aos testes do adaptador M-Pesa (S5-04): a configuração M-Pesa da seguradora gravada pela API
 * (S5-01) a apontar para o {@link MpesaMockContainer}, e intenções M-Pesa inseridas directamente (como
 * dono) num estado escolhido, para os jobs.
 */
public final class MpesaTestSupport {

    private MpesaTestSupport() {
    }

    /** Grava e activa o M-Pesa da seguradora pela API, com os {@code settings} e os segredos indicados. */
    public static void configure(Seg insurer, Map<String, Object> settings, Map<String, String> secrets) {
        Long current = version(insurer.admin(), "MPESA");
        put(insurer.admin(), "MPESA", body(settings, secrets, current)).then().statusCode(200);
        Long saved = version(insurer.admin(), "MPESA");
        if (current == null) {
            action(insurer.admin(), "MPESA", "enable", saved).then().statusCode(200);
        }
    }

    public static UUID configId(JdbcTemplate owner, UUID insurerId) {
        return owner.queryForObject("select id from payment.insurer_payment_method where insurer_id = ? "
                + "and method = 'MPESA'", UUID.class, insurerId);
    }

    public static String lastErrorCode(JdbcTemplate owner, UUID insurerId) {
        return owner.queryForObject("select last_error_code from payment.insurer_payment_method where insurer_id = ? "
                + "and method = 'MPESA'", String.class, insurerId);
    }

    /** Dados de uma intenção inserida à mão. */
    public record Row(String status, Instant createdAt, Instant dispatchedAt, String thirdPartyReference,
                      Instant unknownSince, Instant nextQueryAt) {
    }

    /**
     * Insere uma intenção M-Pesa (como dono) para a proposta, no estado indicado.
     *
     * @param national número nacional ({@code 84…})
     */
    public static UUID insertIntent(JdbcTemplate owner, Seg insurer, UUID payerId, UUID proposalId, String national,
                                    Row row) {
        UUID id = UUID.randomUUID();
        Instant created = row.createdAt();
        owner.update("""
                insert into payment.payment_intent
                  (id, created_at, created_by, updated_at, updated_by, version, reference, insurer_id, payer_user_id,
                   purpose, target_type, target_id, target_label, amount, method, method_config_id, payer_msisdn,
                   status, third_party_reference, idempotency_key, expires_at, initiated_by_user_id, initiated_via,
                   dispatched_at, unknown_since, next_query_at)
                values (?, ?, 'test', ?, 'test', 0, ?, ?, ?, 'NEW_POLICY', 'PROPOSAL', ?, 'PR-TESTE', ?, 'MPESA', ?, ?,
                        ?, ?, ?, ?, ?, 'APP', ?, ?, ?)""",
                id, Timestamp.from(created), Timestamp.from(created),
                "PG-9" + PaymentFlowSupport.digits(3) + "-" + PaymentFlowSupport.digits(7),
                insurer.id(), payerId, proposalId, PaymentFlowSupport.AMOUNT, configId(owner, insurer.id()),
                "258" + national, row.status(), row.thirdPartyReference(), UUID.randomUUID(),
                Timestamp.from(created.plusSeconds(1800)), payerId, ts(row.dispatchedAt()), ts(row.unknownSince()),
                ts(row.nextQueryAt()));
        return id;
    }

    /** Tipos de {@code payment_event} da intenção, por ordem, com o estado de destino nas transições. */
    public static List<String> events(JdbcTemplate owner, UUID intentId) {
        return owner.queryForList("""
                select type || coalesce(':' || to_status, '') from payment.payment_event
                 where intent_id = ? order by received_at, created_at""", String.class, intentId);
    }

    private static Timestamp ts(Instant at) {
        return at == null ? null : Timestamp.from(at);
    }
}
