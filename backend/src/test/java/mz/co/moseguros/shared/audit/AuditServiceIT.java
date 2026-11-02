package mz.co.moseguros.shared.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * {@link AuditService} contra um Postgres real com a migração aplicada e
 * {@code ddl-auto: validate} (a aplicação arranca, logo as entidades batem com o schema).
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, AuditServiceIT.FixedClock.class})
@ActiveProfiles("test")
class AuditServiceIT {

    static final Instant NOW = Instant.parse("2026-11-02T08:30:00Z");

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClock {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(NOW, ZoneOffset.UTC);
        }
    }

    /** DTO de auditoria com o telemóvel já mascarado, como os módulos devem fazer. */
    record PolicySnapshot(String status, Money premium, String holderMsisdn) {
    }

    @Autowired
    AuditService auditService;

    @Autowired
    AuditEventRepository repository;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostgreSQLContainer<?> postgres;

    @AfterEach
    void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
        MDC.clear();
    }

    @Test
    void recordsAndReadsAnEventWithJsonbStates() throws Exception {
        UUID insurerId = UUID.randomUUID();
        String masked = Msisdn.of("841234567").masked();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "41.220.1.10, 10.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        MDC.put("traceId", "4bf92f3577b34da6a3ce929d0e0e4736");

        UUID id = tx.execute(status -> auditService.record(new AuditRecord(
                ActorType.STAFF, "user-42", insurerId, "POLICY.ISSUE", "Policy", "AP-2026-000123",
                new PolicySnapshot("DRAFT", Money.ofCentavos(299900), masked),
                new PolicySnapshot("ACTIVE", Money.ofCentavos(299900), masked))));

        Map<String, Object> row = jdbc.queryForMap("""
                select id, occurred_at, actor_id, actor_type, insurer_id, action, entity_type, entity_id,
                       before_state->>'status' as before_status,
                       after_state->'premium'->>'amount' as after_amount,
                       jsonb_typeof(after_state) as after_type,
                       pg_typeof(after_state)::text as after_column_type,
                       ip, trace_id
                  from audit.audit_event where id = ?""", id);

        assertThat(id.version()).isEqualTo(7);
        assertThat(row.get("actor_id")).isEqualTo("user-42");
        assertThat(row.get("actor_type")).isEqualTo("STAFF");
        assertThat(row.get("insurer_id")).isEqualTo(insurerId);
        assertThat(row.get("action")).isEqualTo("POLICY.ISSUE");
        assertThat(row.get("entity_type")).isEqualTo("Policy");
        assertThat(row.get("entity_id")).isEqualTo("AP-2026-000123");
        assertThat(row.get("before_status")).isEqualTo("DRAFT");
        assertThat(row.get("after_amount")).isEqualTo("299900");
        assertThat(row.get("after_type")).isEqualTo("object");
        assertThat(row.get("after_column_type")).isEqualTo("jsonb");
        assertThat(row.get("ip")).isEqualTo("41.220.1.10");
        assertThat(row.get("trace_id")).isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");

        AuditEvent event = tx.execute(status -> repository.findById(id).orElseThrow());
        assertThat(event.getOccurredAt()).isEqualTo(NOW);
        assertThat(event.getBeforeState().get("status").asText()).isEqualTo("DRAFT");
        assertThat(event.getAfterState().get("holderMsisdn").asText()).isEqualTo("+258*****4567");
        assertThat(event.getAfterState().get("premium").toString())
                .isEqualTo(objectMapper.writeValueAsString(Money.ofCentavos(299900)));
        assertThat(repository.findByEntityTypeAndEntityIdOrderByOccurredAtDesc("Policy", "AP-2026-000123"))
                .extracting(AuditEvent::getId).contains(id);
    }

    @Test
    void creationWithoutBeforeStateAndOutsideHttpRequest() {
        // O SpringBootTest (MOCK) deixa um pedido simulado no contexto; aqui não há pedido HTTP.
        RequestContextHolder.resetRequestAttributes();
        UUID id = tx.execute(status -> auditService.record(new AuditRecord(
                ActorType.SYSTEM, "renewal-job", null, "POLICY.RENEW", "Policy", "AP-2026-000124",
                null, Map.of("status", "ACTIVE"))));

        Map<String, Object> row = jdbc.queryForMap(
                "select before_state, insurer_id, ip, trace_id from audit.audit_event where id = ?", id);
        assertThat(row.get("before_state")).isNull();
        assertThat(row.get("insurer_id")).isNull();
        assertThat(row.get("ip")).isNull();
        assertThat(row.get("trace_id")).isNull();
    }

    @Test
    void remoteAddressIsUsedWithoutForwardedHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("196.28.1.2");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UUID id = tx.execute(status -> auditService.record(sample("AP-2026-000125")));

        assertThat(jdbc.queryForObject("select ip from audit.audit_event where id = ?", String.class, id))
                .isEqualTo("196.28.1.2");
    }

    @Test
    void failsWithoutActiveTransaction() {
        assertThatThrownBy(() -> auditService.record(sample("AP-2026-000126")))
                .isInstanceOf(IllegalTransactionStateException.class);
        assertThat(count("AP-2026-000126")).isZero();
    }

    @Test
    void rolledBackOperationLeavesNoAuditEvent() {
        tx.executeWithoutResult(status -> {
            auditService.record(sample("AP-2026-000127"));
            status.setRollbackOnly();
        });
        assertThat(count("AP-2026-000127")).isZero();
    }

    @Test
    void migrationCreatesEveryModuleSchema() {
        // Como dono: o utilizador da aplicação só vê os schemas a que tem acesso.
        JdbcTemplate owner = TestcontainersConfiguration.ownerJdbc(postgres);
        List<String> schemas = owner.queryForList(
                "select schema_name from information_schema.schemata", String.class);
        assertThat(schemas).contains("audit", "identity", "organization", "vehicle", "catalog", "rating",
                "proposal", "payment", "policy", "document", "notification", "verification", "claim",
                "complaint", "assistance", "reporting", "fraud", "integration");
        assertThat(owner.queryForObject(
                "select count(*) from public.flyway_schema_history where version = '202610311200' and success",
                Integer.class)).isEqualTo(1);
    }

    @Test
    void actorTypeIsCheckedByTheDatabase() {
        assertThatThrownBy(() -> jdbc.update("""
                insert into audit.audit_event (id, occurred_at, actor_id, actor_type, action, entity_type, entity_id)
                values (gen_random_uuid(), now(), 'x', 'ROBOT', 'X.Y', 'X', '1')"""))
                .hasMessageContaining("actor_type");
    }

    private static AuditRecord sample(String entityId) {
        return new AuditRecord(ActorType.CUSTOMER, "customer-1", null, "POLICY.CANCEL", "Policy", entityId,
                Map.of("status", "ACTIVE"), Map.of("status", "CANCELLED"));
    }

    private int count(String entityId) {
        return jdbc.queryForObject("select count(*) from audit.audit_event where entity_id = ?",
                Integer.class, entityId);
    }
}
