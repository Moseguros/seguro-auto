package mz.co.moseguros.proposal;

import static io.restassured.RestAssured.given;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.KycTestSupport;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.rating.QuoteTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.rating.domain.tariff.ReferenceData;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Apoio aos testes de integração das propostas (S4-02): seguradoras com os produtos de demonstração
 * publicados com tarifa (S3), clientes com o perfil completo, veículo, condutor e cotação, e os
 * pedidos REST das propostas.
 */
public final class ProposalTestSupport {

    public static final String PROPOSALS = "/api/v1/proposals";
    public static final String MY_PROPOSALS = "/api/v1/me/proposals";
    /** "Agora" dos testes (o do S3-02): 10/01/2027, 08:15 UTC. */
    public static final Instant NOW = QuoteTestSupport.NOW;
    public static final LocalDate START = QuoteTestSupport.START;
    public static final String NUIT = "123456789";

    private final IdentityFixtures fixtures;
    private final QuoteTestSupport quotes;

    public ProposalTestSupport(IdentityFixtures fixtures, IdentityApi identityApi, MutableClock clock) {
        this.fixtures = fixtures;
        this.quotes = new QuoteTestSupport(fixtures, identityApi, clock);
    }

    public QuoteTestSupport quotes() {
        return quotes;
    }

    public JdbcTemplate owner() {
        return fixtures.owner();
    }

    public void atNow() {
        quotes.atNow();
    }

    public void advance(Duration duration) {
        quotes.advance(duration);
    }

    public String token(UserAccount account) {
        return quotes.token(account);
    }

    // --- Seguradoras ---

    /** Seguradora activa com DEMO-AUTO (danos próprios), DEMO-RC (só RC) e DEMO-TEMP (temporário). */
    public Insurer insurerWithProducts(String name) {
        Insurer insurer = quotes.insurerWithCharges(name);
        quotes.publishedDemoAuto(insurer.manager(), ReferenceData.tariff("DEMO-AUTO"));
        quotes.published(insurer.manager(), "MTPL_ONLY", List.of("LIGHT_PASSENGER"), List.of("PRIVATE"),
                QuoteTestSupport.demoRcCoverages(), ReferenceData.tariff("DEMO-RC"));
        quotes.published(insurer.manager(), "TEMPORARY", List.of("LIGHT_PASSENGER"), List.of("PRIVATE"),
                QuoteTestSupport.demoTempCoverages(), ReferenceData.tariff("DEMO-TEMP"));
        return insurer;
    }

    // --- Clientes e cotações ---

    /** Cliente com o perfil completo (documento {@link KycTestSupport#DOC}, NUIT {@link #NUIT}). */
    public Customer customerWithProfile() {
        Customer c = quotes.customer();
        KycTestSupport.completeProfile(c.token(), "BI", KycTestSupport.DOC, 0);
        return c;
    }

    /** Cliente com veículo, condutor e uma cotação. */
    public record Quoted(Customer customer, UUID vehicleId, UUID driverId, UUID quoteId, JsonNode quote) {

        public String token() {
            return customer.token();
        }
    }

    /** Cotação {@code COMPREHENSIVE} de 365 dias com danos próprios completos (o caso C05). */
    public Quoted comprehensive(Customer c, Insurer insurer) throws Exception {
        return quoted(c, insurer, "COMPREHENSIVE", 365, QuoteTestSupport.FULL_OD);
    }

    /** Cotação {@code MTPL_ONLY} de 365 dias. */
    public Quoted mtplOnly(Customer c, Insurer insurer) throws Exception {
        return quoted(c, insurer, "MTPL_ONLY", 365, List.of());
    }

    public Quoted quoted(Customer c, Insurer insurer, String productType, int days, List<String> coverages)
            throws Exception {
        UUID vehicleId = QuoteTestSupport.vehicle(c.token(), QuoteTestSupport.c05Vehicle());
        UUID driverId = QuoteTestSupport.c05Driver(c.token());
        return quote(c, insurer, vehicleId, driverId, productType, days, coverages);
    }

    public Quoted quote(Customer c, Insurer insurer, UUID vehicleId, UUID driverId, String productType, int days,
                        List<String> coverages) throws Exception {
        Response r = QuoteTestSupport.postQuote(c.token(),
                QuoteTestSupport.quoteBody(vehicleId, driverId, productType, START, days, coverages));
        r.then().statusCode(201);
        List<JsonNode> mine = QuoteTestSupport.quotesOf(r, insurer.id());
        JsonNode quote = mine.stream().filter(q -> q.get("status").asText().equals("OFFERED")
                        && q.get("product").get("type").asText().equals(productType))
                .findFirst().orElseThrow(() -> new AssertionError("No OFFERED quote of " + productType));
        return new Quoted(c, vehicleId, driverId, UUID.fromString(quote.get("id").asText()), quote);
    }

    // --- Pedidos ---

    public static Response create(String token, UUID quoteId) {
        return create(token, quoteId, "APP");
    }

    public static Response create(String token, UUID quoteId, String channel) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("quoteId", quoteId == null ? null : quoteId.toString());
        body.put("channel", channel);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post(PROPOSALS);
    }

    /** Cria a proposta (201) e devolve o id. */
    public static UUID created(String token, UUID quoteId) {
        return UUID.fromString(create(token, quoteId).then().statusCode(201).extract().path("id"));
    }

    public static Response get(String token, UUID proposalId) {
        return given().auth().oauth2(token).get(PROPOSALS + "/{id}", proposalId);
    }

    public static long version(String token, UUID proposalId) {
        return ((Number) get(token, proposalId).then().statusCode(200).extract().path("version")).longValue();
    }

    public static Response update(String token, UUID proposalId, String startDate, long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("startDate", startDate);
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .put(PROPOSALS + "/{id}", proposalId);
    }

    public static Response declarations(String token, UUID proposalId, List<Map<String, Object>> answers,
                                        Boolean truthfulnessConfirmed, long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("answers", answers);
        body.put("truthfulnessConfirmed", truthfulnessConfirmed);
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(PROPOSALS + "/{id}/declarations", proposalId);
    }

    public static Response suitability(String token, UUID proposalId, Map<String, String> answers, long version) {
        List<Map<String, Object>> list = new ArrayList<>();
        answers.forEach((code, answer) -> {
            Map<String, Object> a = new LinkedHashMap<>();
            a.put("questionCode", code);
            a.put("answer", answer);
            list.add(a);
        });
        return suitabilityList(token, proposalId, list, version);
    }

    public static Response suitabilityList(String token, UUID proposalId, List<Map<String, Object>> answers,
                                           long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("answers", answers);
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(PROPOSALS + "/{id}/suitability", proposalId);
    }

    public static Response acknowledge(String token, UUID proposalId, long version) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(Map.of("version", version))
                .post(PROPOSALS + "/{id}/suitability/acknowledge", proposalId);
    }

    // --- Corpos ---

    /** Uma resposta de declaração ({@code null} = campo omitido). */
    public static Map<String, Object> answer(String code, Boolean answer, Integer claimsCount, String details) {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("questionCode", code);
        a.put("answer", answer);
        if (claimsCount != null) {
            a.put("claimsCount", claimsCount);
        }
        if (details != null) {
            a.put("details", details);
        }
        return a;
    }

    /** As cinco declarações válidas: sinistro nos últimos 3 anos e as outras "não". */
    public static List<Map<String, Object>> validDeclarations() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(answer("CLAIMS_LAST_3_YEARS", true, 1, "Colisão traseira em Maputo, Março de 2025, sem feridos."));
        list.add(answer("PREVIOUS_REFUSAL", false, null, null));
        list.add(answer("USAGE_DIFFERENT", false, null, null));
        list.add(answer("VEHICLE_MODIFIED", false, null, null));
        list.add(answer("NOVICE_DRIVERS", false, null, null));
        return list;
    }

    /** Respostas de adequação pela ordem das perguntas. */
    public static Map<String, String> suitabilityAnswers(String need, String financed, String canAfford,
                                                         String crossBorder, String deductible) {
        Map<String, String> answers = new LinkedHashMap<>();
        answers.put("SUIT_NEED", need);
        answers.put("SUIT_FINANCED", financed);
        answers.put("SUIT_CAN_AFFORD_REPAIR", canAfford);
        answers.put("SUIT_CROSS_BORDER", crossBorder);
        answers.put("SUIT_DEDUCTIBLE", deductible);
        return answers;
    }

    /** Muda o estado directamente na BD (como dono, sem RLS). */
    public void setStatus(UUID proposalId, String status) {
        owner().update("update proposal.proposal set status = ? where id = ?", status, proposalId);
    }
}
