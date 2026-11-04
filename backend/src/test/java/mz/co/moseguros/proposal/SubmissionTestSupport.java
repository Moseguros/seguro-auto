package mz.co.moseguros.proposal;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.ProposalTestSupport.PROPOSALS;
import static mz.co.moseguros.proposal.ProposalTestSupport.declarations;
import static mz.co.moseguros.proposal.ProposalTestSupport.suitability;
import static mz.co.moseguros.proposal.ProposalTestSupport.suitabilityAnswers;
import static mz.co.moseguros.proposal.ProposalTestSupport.version;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.document.TestFiles;
import mz.co.moseguros.identity.KycTestSupport;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

/**
 * Apoio aos testes da submissão e da decisão (S4-05): proposta completa e assinada, KYC verificado,
 * vistoria submetida, os pedidos REST da submissão e da seguradora, e um registo dos eventos
 * publicados (os pedidos REST correm noutra thread, que o {@code PublishedEvents} não vê).
 */
public final class SubmissionTestSupport {

    public static final String INSURER_PROPOSALS = "/api/v1/insurer/proposals";

    private final ProposalTestSupport support;
    private final FakeOtpDeliveryChannels channels;
    private final MutableClock clock;

    public SubmissionTestSupport(ProposalTestSupport support, FakeOtpDeliveryChannels channels, MutableClock clock) {
        this.support = support;
        this.channels = channels;
        this.clock = clock;
    }

    /** Eventos públicos do {@code proposal}, por ordem de publicação. */
    public static final class Events {

        private final List<Object> published = new CopyOnWriteArrayList<>();

        @EventListener
        void on(mz.co.moseguros.proposal.ProposalSubmitted event) {
            published.add(event);
        }

        @EventListener
        void on(mz.co.moseguros.proposal.ProposalAccepted event) {
            published.add(event);
        }

        @EventListener
        void on(mz.co.moseguros.proposal.ProposalRejected event) {
            published.add(event);
        }

        @EventListener
        void on(mz.co.moseguros.proposal.ProposalInfoRequested event) {
            published.add(event);
        }

        public <T> List<T> of(Class<T> type) {
            List<T> list = new ArrayList<>();
            published.stream().filter(type::isInstance).map(type::cast).forEach(list::add);
            return list;
        }
    }

    /** Regista o {@link Events} no contexto dos testes do S4-05. */
    @TestConfiguration(proxyBeanMethods = false)
    public static class EventsConfig {

        @Bean
        Events proposalEventsRecorder() {
            return new Events();
        }
    }

    /**
     * Instante em texto a menos de 5 s de {@code expected}: o {@code MutableClock} dos testes continua a
     * andar com o relógio real.
     */
    public static org.hamcrest.Matcher<String> instantNear(java.time.Instant expected) {
        return new org.hamcrest.TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(String item) {
                java.time.Instant actual = java.time.Instant.parse(item);
                return java.time.Duration.between(expected, actual).abs().compareTo(java.time.Duration.ofSeconds(5)) < 0;
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("an instant within 5 s of ").appendValue(expected);
            }
        };
    }

    // --- Preparação ---

    /** As cinco declarações com resposta "não". */
    public static List<Map<String, Object>> allNegativeDeclarations() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String code : List.of("CLAIMS_LAST_3_YEARS", "PREVIOUS_REFUSAL", "USAGE_DIFFERENT", "VEHICLE_MODIFIED",
                "NOVICE_DRIVERS")) {
            list.add(ProposalTestSupport.answer(code, false, null, null));
        }
        return list;
    }

    /** Declarações todas "não" e adequação {@code SUITABLE} (sem danos próprios). */
    public void answerMtpl(Customer c, UUID id) {
        declarations(c.token(), id, allNegativeDeclarations(), true, version(c.token(), id)).then().statusCode(200);
        suitability(c.token(), id, suitabilityAnswers("MANDATORY_ONLY", "NO", "YES", "NO", "UNSURE"),
                version(c.token(), id)).then().statusCode(200);
    }

    /** Declarações todas "não", adequação {@code SUITABLE} com danos próprios e a vistoria submetida. */
    public void answerOwnDamage(Customer c, UUID id) throws IOException {
        declarations(c.token(), id, allNegativeDeclarations(), true, version(c.token(), id)).then().statusCode(200);
        suitability(c.token(), id, suitabilityAnswers("OWN_DAMAGE", "NO", "YES", "NO", "YES"),
                version(c.token(), id)).then().statusCode(200);
        submitInspection(c, id);
    }

    /** Os seis ângulos e a vistoria submetida. */
    public void submitInspection(Customer c, UUID id) throws IOException {
        InspectionTestSupport.uploadRequired(c.token(), id);
        InspectionTestSupport.submit(c.token(), id, 84_500, version(c.token(), id)).then().statusCode(200);
    }

    /** Assina a proposta com o código enviado ao telemóvel do cliente. */
    public void sign(Customer c, UUID id) {
        clock.advance(Duration.ofSeconds(61)); // 60 s entre códigos do mesmo telemóvel
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("documentsAcknowledged", true);
        body.put("version", version(c.token(), id));
        Response request = given().auth().oauth2(c.token()).contentType(ContentType.JSON).body(body)
                .post(PROPOSALS + "/{id}/sign/request", id);
        request.then().statusCode(202);
        String code = channels.lastTo(c.account().getMsisdn().e164()).code();
        Map<String, Object> confirm = new LinkedHashMap<>();
        confirm.put("challengeId", request.path("challengeId"));
        confirm.put("code", code);
        given().auth().oauth2(c.token()).contentType(ContentType.JSON).body(confirm)
                .post(PROPOSALS + "/{id}/sign/confirm", id).then().statusCode(200);
    }

    /** Proposta {@code MTPL_ONLY} completa e assinada (caso 1). */
    public UUID readyMtpl(Customer c, Insurer insurer) throws Exception {
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
        answerMtpl(c, id);
        sign(c, id);
        return id;
    }

    /** Proposta {@code COMPREHENSIVE} completa, com a vistoria submetida e assinada (caso 8). */
    public UUID readyComprehensive(Customer c, Insurer insurer) throws Exception {
        UUID id = ProposalTestSupport.created(c.token(), support.comprehensive(c, insurer).quoteId());
        answerOwnDamage(c, id);
        sign(c, id);
        return id;
    }

    /** KYC do cliente enviado à seguradora e verificado pelo subscritor (S4-01). */
    public void verifyKyc(Customer c, Insurer insurer, String underwriterToken) throws IOException {
        KycTestSupport.Part jpeg = new KycTestSupport.Part("frente.jpg", TestFiles.bytes("sample.jpg"), "image/jpeg");
        KycTestSupport.Part png = new KycTestSupport.Part("verso.png", TestFiles.bytes("sample.png"), "image/png");
        KycTestSupport.Part heic = new KycTestSupport.Part("selfie.heic", TestFiles.bytes("sample.heic"), "image/heic");
        String checkId = KycTestSupport.submit(c.token(), insurer.id(), jpeg, png, heic).then().statusCode(201)
                .extract().path("id");
        long v = ((Number) given().auth().oauth2(underwriterToken).get("/api/v1/insurer/kyc/{id}", checkId).then()
                .statusCode(200).extract().path("version")).longValue();
        given().auth().oauth2(underwriterToken).contentType(ContentType.JSON).body(Map.of("version", v))
                .post("/api/v1/insurer/kyc/{id}/verify", checkId).then().statusCode(200);
    }

    // --- Pedidos ---

    /** Pedido com o token, ou sem autenticação com {@code null}. */
    static io.restassured.specification.RequestSpecification as(String token) {
        return token == null ? given() : given().auth().oauth2(token);
    }

    public static Response submit(String token, UUID id, Long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(PROPOSALS + "/{id}/submit", id);
    }

    public static Response submit(String token, UUID id) {
        return submit(token, id, version(token, id));
    }

    public static Response insurerGet(String token, UUID id) {
        return as(token).get(INSURER_PROPOSALS + "/{id}", id);
    }

    public static long insurerVersion(String token, UUID id) {
        return ((Number) insurerGet(token, id).then().statusCode(200).extract().path("version")).longValue();
    }

    public static Response accept(String token, UUID id, String note, Long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (note != null) {
            body.put("note", note);
        }
        body.put("version", version);
        return as(token).contentType(ContentType.JSON).body(body)
                .post(INSURER_PROPOSALS + "/{id}/accept", id);
    }

    public static Response reject(String token, UUID id, String reasonCode, String reason, Long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("reasonCode", reasonCode);
        body.put("reason", reason);
        body.put("version", version);
        return as(token).contentType(ContentType.JSON).body(body)
                .post(INSURER_PROPOSALS + "/{id}/reject", id);
    }

    public static Response requestInfo(String token, UUID id, String message, List<String> items, Long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("items", items);
        body.put("version", version);
        return as(token).contentType(ContentType.JSON).body(body)
                .post(INSURER_PROPOSALS + "/{id}/request-info", id);
    }

    /** Verifica o e-mail do cliente na BD (como dono), para receber as mensagens da proposta. */
    public String verifiedEmail(Customer c) {
        String email = "cliente" + UUID.randomUUID().toString().substring(0, 8) + "@teste.co.mz";
        support.owner().update("update identity.user_account set email = ?, email_verified = true where id = ?",
                email, c.id());
        return email;
    }

    public int audits(String action, UUID proposalId) {
        Integer n = support.owner().queryForObject(
                "select count(*) from audit.audit_event where action = ? and entity_id = ?", Integer.class, action,
                proposalId.toString());
        return n == null ? 0 : n;
    }
}
