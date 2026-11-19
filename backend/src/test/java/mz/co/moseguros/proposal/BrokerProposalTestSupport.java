package mz.co.moseguros.proposal;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.proposal.ProposalTestSupport.PROPOSALS;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.rating.QuoteTestSupport;

/**
 * Apoio aos testes das propostas do mediador (S4-06): mediadores, cotações do mediador
 * ({@code POST /broker/quotes}, S3-02), os corpos do veículo, do condutor e do tomador coerentes com a
 * cotação e os pedidos REST de {@code /broker/proposals}.
 */
public final class BrokerProposalTestSupport {

    public static final String BROKER_PROPOSALS = "/api/v1/broker/proposals";
    public static final String BROKER_QUOTES = QuoteTestSupport.BROKER_QUOTES;
    /** Dados sensíveis do tomador e do condutor usados nos testes (procurados nos logs). */
    public static final String ID_DOC = "110100765432B";
    public static final String NUIT = "400123987";
    public static final String CHASSIS = "JTDBR32E720012345";

    private final IdentityFixtures fixtures;
    private final ProposalTestSupport support;
    private final FakeOtpDeliveryChannels channels;
    private final MutableClock clock;

    public BrokerProposalTestSupport(IdentityFixtures fixtures, ProposalTestSupport support,
                                     FakeOtpDeliveryChannels channels, MutableClock clock) {
        this.fixtures = fixtures;
        this.support = support;
        this.channels = channels;
        this.clock = clock;
    }

    // --- Mediadores ---

    /** Organização de corretagem ({@code BROKER}) ou agente ({@code AGENT}). */
    public UUID brokerOrg(OrganizationType type, String name) {
        return fixtures.organization(type, name).getId();
    }

    /** Utilizador de uma organização e o seu token. */
    public record Staff(UUID id, String token) {
    }

    /** Utilizador novo da organização indicada com o perfil indicado (BROKER ou AGENT nos mediadores). */
    public Staff staff(UUID orgId, String role) {
        mz.co.moseguros.identity.domain.UserAccount account = fixtures.staff("Mediador " + QuoteTestSupport.digits(4),
                orgId, role);
        return new Staff(account.getId(), support.token(account));
    }

    /** Token de um utilizador novo da organização do mediador, com o perfil BROKER ou AGENT. */
    public String broker(UUID orgId, String role) {
        return staff(orgId, role).token();
    }

    public static String newNational() {
        StringBuilder sb = new StringBuilder("84");
        for (int i = 0; i < 7; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    // --- Cotações do mediador ---

    /** Cotação do mediador e a conta do cliente a que ficou ligada. */
    public record BrokerQuote(UUID quoteId, UUID customerId, String national, JsonNode quote) {

        public String e164() {
            return "+258" + national;
        }
    }

    /** Veículo e condutor da cotação (só o que a tarifa usa). */
    public static Map<String, Object> quoteVehicle(String plate) {
        Map<String, Object> vehicle = new LinkedHashMap<>();
        vehicle.put("plate", plate);
        vehicle.put("make", "Toyota");
        vehicle.put("model", "Corolla");
        vehicle.put("year", 2024);
        vehicle.put("engineCc", 1600);
        vehicle.put("category", "LIGHT_PASSENGER");
        vehicle.put("usage", "PRIVATE");
        vehicle.put("seats", 5);
        vehicle.put("grossWeightKg", null);
        vehicle.put("provinceOfCirculation", "MAPUTO_CITY");
        vehicle.put("commercialValue", Map.of("amount", 120_000_000, "currency", "MZN"));
        vehicle.put("hasTracker", false);
        return vehicle;
    }

    /**
     * {@code POST /broker/quotes} para o telemóvel indicado e a cotação OFFERED da seguradora do teste
     * do tipo pedido ({@code MTPL_ONLY} ou {@code COMPREHENSIVE} com danos próprios).
     */
    public BrokerQuote quote(String brokerToken, String national, Insurer insurer, String productType, String plate)
            throws Exception {
        Map<String, Object> driver = new LinkedHashMap<>();
        driver.put("birthDate", "1991-09-09");
        driver.put("licenceIssueDate", "2011-04-04");
        driver.put("licenceCategories", List.of("B"));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("clientMsisdn", "+258" + national);
        body.put("vehicle", quoteVehicle(plate));
        body.put("driver", driver);
        body.put("productType", productType);
        body.put("coverages", QuoteTestSupport.coverages("COMPREHENSIVE".equals(productType)
                ? QuoteTestSupport.FULL_OD : List.of()));
        body.put("startDate", ProposalTestSupport.START.toString());
        body.put("durationDays", 365);
        Response r = given().auth().oauth2(brokerToken).contentType(ContentType.JSON).body(body).post(BROKER_QUOTES);
        r.then().statusCode(201);
        JsonNode quote = QuoteTestSupport.quotesOf(r, insurer.id()).stream()
                .filter(q -> q.get("status").asText().equals("OFFERED")
                        && q.get("product").get("type").asText().equals(productType))
                .findFirst().orElseThrow(() -> new AssertionError("No OFFERED quote of " + productType));
        UUID customerId = support.owner().queryForObject(
                "select customer_user_id from rating.quote_request where id = ?::uuid", UUID.class,
                (String) r.path("id"));
        return new BrokerQuote(UUID.fromString(quote.get("id").asText()), customerId, national, quote);
    }

    // --- Corpos ---

    /** Veículo completo, igual ao da cotação ({@link #quoteVehicle}). */
    public static Map<String, Object> vehicle(String plate, String chassis) {
        Map<String, Object> vehicle = quoteVehicle(plate);
        vehicle.put("chassisNumber", chassis);
        return vehicle;
    }

    /** Condutor completo, igual ao da cotação. */
    public static Map<String, Object> driver(String licenceNumber) {
        Map<String, Object> driver = new LinkedHashMap<>();
        driver.put("fullName", "João Carlos Sitoe");
        driver.put("birthDate", "1991-09-09");
        driver.put("licenceNumber", licenceNumber);
        driver.put("licenceCategories", List.of("B"));
        driver.put("licenceIssueDate", "2011-04-04");
        driver.put("isOwner", true);
        return driver;
    }

    /** Tomador preenchido pelo mediador ({@link #ID_DOC}, {@link #NUIT}). */
    public static Map<String, Object> policyholder() {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("fullName", "João Carlos Sitoe");
        p.put("birthDate", "1991-09-09");
        p.put("idDocType", "BI");
        p.put("idDocNumber", ID_DOC);
        p.put("nuit", NUIT);
        p.put("address", "Bairro da Machava, Q. 12");
        p.put("province", "MAPUTO");
        return p;
    }

    public static Map<String, Object> body(UUID quoteId, Map<String, Object> vehicle, Map<String, Object> driver,
                                           Map<String, Object> policyholder) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("quoteId", quoteId == null ? null : quoteId.toString());
        body.put("vehicle", vehicle);
        body.put("driver", driver);
        if (policyholder != null) {
            body.put("policyholder", policyholder);
        }
        return body;
    }

    // --- Pedidos ---

    public static Response create(String token, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).post(BROKER_PROPOSALS);
    }

    /** Proposta do mediador com o tomador preenchido por ele (perfil do cliente incompleto); devolve o id. */
    public static UUID created(String token, BrokerQuote quote, String plate) {
        return UUID.fromString(create(token, body(quote.quoteId(), vehicle(plate, CHASSIS),
                driver("MAP-" + QuoteTestSupport.digits(6)), policyholder())).then().statusCode(201).extract()
                .path("id"));
    }

    public static Response list(String token, Map<String, ?> params) {
        return given().auth().oauth2(token).queryParams(params).get(BROKER_PROPOSALS);
    }

    public static Response update(String token, UUID id, String startDate, Map<String, Object> policyholder,
                                  long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (startDate != null) {
            body.put("startDate", startDate);
        }
        if (policyholder != null) {
            body.put("policyholder", policyholder);
        }
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .put(BROKER_PROPOSALS + "/{id}", id);
    }

    // --- Passos com o token do mediador ---

    /** Declarações todas "não" e adequação SUITABLE (com ou sem danos próprios). */
    public static void answer(String token, UUID id, boolean ownDamage) {
        ProposalTestSupport.declarations(token, id, SubmissionTestSupport.allNegativeDeclarations(), true,
                ProposalTestSupport.version(token, id)).then().statusCode(200);
        ProposalTestSupport.suitability(token, id, ownDamage
                        ? ProposalTestSupport.suitabilityAnswers("OWN_DAMAGE", "NO", "YES", "NO", "YES")
                        : ProposalTestSupport.suitabilityAnswers("MANDATORY_ONLY", "NO", "YES", "NO", "UNSURE"),
                ProposalTestSupport.version(token, id)).then().statusCode(200);
    }

    public static Response requestSignature(String token, UUID id) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("documentsAcknowledged", true);
        body.put("version", ProposalTestSupport.version(token, id));
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(PROPOSALS + "/{id}/sign/request", id);
    }

    public static Response confirmSignature(String token, UUID id, String challengeId, String code) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("challengeId", challengeId);
        body.put("code", code);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(PROPOSALS + "/{id}/sign/confirm", id);
    }

    /** Assinatura assistida: o mediador pede, o código chega ao telemóvel do cliente e o mediador confirma. */
    public void assistedSign(String brokerToken, UUID id, String e164) {
        clock.advance(Duration.ofSeconds(61)); // 60 s entre códigos do mesmo telemóvel
        Response request = requestSignature(brokerToken, id);
        request.then().statusCode(202);
        String code = channels.lastCodeTo(e164).code();
        confirmSignature(brokerToken, id, request.path("challengeId"), code).then().statusCode(200);
    }

    /** Login do cliente por OTP com o telemóvel (como no S1-03); devolve o token de acesso. */
    public String customerLogin(String national) {
        clock.advance(Duration.ofSeconds(61));
        String challengeId = given().contentType(ContentType.JSON)
                .body(Map.of("identifier", national, "identifierType", "MSISDN"))
                .post("/api/v1/auth/otp/request").then().statusCode(202).extract().path("challengeId");
        String code = channels.lastCodeTo("+258" + national).code();
        return given().contentType(ContentType.JSON).body(Map.of("challengeId", challengeId, "code", code))
                .post("/api/v1/auth/otp/verify").then().statusCode(200).extract().path("accessToken");
    }

    /** Ids de todos os elementos {@code content[].id} de uma página. */
    public static List<String> ids(Response page) {
        return new ArrayList<>(page.then().statusCode(200).extract().jsonPath().getList("content.id", String.class));
    }
}
