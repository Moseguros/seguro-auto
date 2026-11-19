package mz.co.moseguros.rating.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.notification.NotificationTestSupport;
import mz.co.moseguros.notification.NotificationTestSupport.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Aviso de simulação a expirar (S6-04 §9.1, D16): um {@code QUOTE_EXPIRING} com o menor total para os pedidos do
 * cliente com uma cotação oferecida que expira daqui a 2 dias (Maputo); nunca duas vezes; nada com cotação usada,
 * pedido do mediador, validade a 3 dias ou data de início passada.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class QuoteExpiryNotifierJobIT {

    static final ZoneId MAPUTO = ZoneId.of("Africa/Maputo");

    @Autowired
    QuoteExpiryNotifierJob job;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    FakeOtpDeliveryChannels channels;

    NotificationTestSupport support;

    @BeforeEach
    void setUp() {
        support = new NotificationTestSupport(fixtures, identityApi);
        clock.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
    }

    @Test
    void notifiesOnceWithTheLowestTotal() {
        Client c = support.customer();
        Instant now = clock.instant();
        UUID request = request(c.id(), "CUSTOMER", today().plusDays(5));
        quote(request, c.id(), "OFFERED", 500_000, now.plus(Duration.ofDays(2)));
        quote(request, c.id(), "OFFERED", 466_300, now.plus(Duration.ofDays(2)).plusSeconds(60));
        quote(request, c.id(), "OFFERED", 100_000, now.minus(Duration.ofHours(1))); // já não efectiva
        quote(request, c.id(), "NOT_AVAILABLE", null, null);

        job.run();

        List<Map<String, Object>> rows = support.byUser(c.id(), "QUOTE_EXPIRING");
        assertThat(rows).extracting(r -> r.get("channel")).containsExactlyInAnyOrder("INBOX", "WHATSAPP");
        Map<String, Object> inbox = rows.stream().filter(r -> "INBOX".equals(r.get("channel"))).findFirst()
                .orElseThrow();
        assertThat(inbox).containsEntry("category", "QUOTE");
        String validUntil = mz.co.moseguros.shared.format.DateFormatter.dateTime(now.plus(Duration.ofDays(2))
                .plusSeconds(60));
        assertThat((String) inbox.get("body")).isEqualTo("A sua simulação, desde 4 663,00 MT, é válida até "
                + validUntil + ". Toque para escolher uma oferta.");
        assertThat(inbox.get("data").toString()).contains("moseguros://quote/" + request)
                .contains("/cliente/simular/" + request);
        assertThat(support.owner().queryForObject("select expiry_notified_at from rating.quote_request where id = ?",
                Timestamp.class, request)).isNotNull();

        job.run();
        assertThat(support.byUser(c.id(), "QUOTE_EXPIRING")).hasSize(2);
    }

    @Test
    void skipsUsedBrokerLaterAndPastStartRequests() {
        Client c = support.customer();
        Instant now = clock.instant();
        Instant inTwoDays = now.plus(Duration.ofDays(2));

        UUID used = request(c.id(), "CUSTOMER", today().plusDays(5));
        quote(used, c.id(), "OFFERED", 400_000, inTwoDays);
        quote(used, c.id(), "USED", 450_000, inTwoDays);

        UUID broker = request(c.id(), "BROKER", today().plusDays(5));
        quote(broker, c.id(), "OFFERED", 400_000, inTwoDays);

        UUID later = request(c.id(), "CUSTOMER", today().plusDays(5));
        quote(later, c.id(), "OFFERED", 400_000, now.plus(Duration.ofDays(3)));

        UUID pastStart = request(c.id(), "CUSTOMER", today().minusDays(1));
        quote(pastStart, c.id(), "OFFERED", 400_000, inTwoDays);

        job.run();
        assertThat(support.byUser(c.id(), "QUOTE_EXPIRING")).isEmpty();
        for (UUID id : List.of(used, broker, later, pastStart)) {
            assertThat(support.owner().queryForObject("select expiry_notified_at from rating.quote_request where id = ?",
                    Timestamp.class, id)).isNull();
        }

        // no dia seguinte, o de 3 dias passa a 2
        clock.advance(Duration.ofDays(1));
        job.run();
        assertThat(support.byUser(c.id(), "QUOTE_EXPIRING")).extracting(r -> r.get("channel"))
                .containsExactlyInAnyOrder("INBOX", "WHATSAPP");
        assertThat(support.owner().queryForObject("select expiry_notified_at from rating.quote_request where id = ?",
                Timestamp.class, later)).isNotNull();
    }

    private LocalDate today() {
        return LocalDate.ofInstant(clock.instant(), MAPUTO);
    }

    private UUID request(UUID customerId, String origin, LocalDate startDate) {
        UUID id = UUID.randomUUID();
        boolean broker = "BROKER".equals(origin);
        support.owner().update("""
                insert into rating.quote_request (id, created_at, created_by, updated_at, updated_by, version, origin,
                       customer_user_id, client_msisdn, requested_by_user_id, requested_by_org_id, product_type,
                       vehicle_snapshot, driver_snapshot, requested_coverages, start_date, duration_days, end_date,
                       evaluation_ms)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, ?, ?, ?, 'MTPL_ONLY', '{}', '{}', '[]', ?, 365, ?, 1)""",
                id, origin, customerId, broker ? "258841234567" : null, customerId, broker ? UUID.randomUUID() : null,
                Date.valueOf(startDate), Date.valueOf(startDate.plusDays(364)));
        return id;
    }

    private void quote(UUID requestId, UUID customerId, String status, Integer total, Instant validUntil) {
        boolean priced = !"NOT_AVAILABLE".equals(status);
        support.owner().update("""
                insert into rating.quote (id, created_at, created_by, updated_at, updated_by, version, quote_request_id,
                       insurer_id, customer_user_id, product_id, product_version_id, provider, status,
                       unavailable_reason, premium_breakdown, total_amount, valid_until, used_at, rating_ms)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, ?, ?, ?, 'INTERNAL', ?, ?, ?::jsonb, ?, ?, ?, 1)""",
                UUID.randomUUID(), requestId, UUID.randomUUID(), customerId, UUID.randomUUID(), UUID.randomUUID(),
                status, priced ? null : "TARIFF_MISSING", priced ? "{}" : null, total == null ? null : total.longValue(),
                validUntil == null ? null : Timestamp.from(validUntil),
                "USED".equals(status) ? Timestamp.from(clock.instant()) : null);
    }
}
