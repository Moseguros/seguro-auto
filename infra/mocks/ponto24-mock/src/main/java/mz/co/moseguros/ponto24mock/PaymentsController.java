package mz.co.moseguros.ponto24mock;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import mz.co.moseguros.ponto24mock.MockProperties.Format;
import mz.co.moseguros.ponto24mock.MockProperties.Source;
import mz.co.moseguros.ponto24mock.SettlementService.Corruption;
import mz.co.moseguros.ponto24mock.SettlementService.MockException;
import mz.co.moseguros.ponto24mock.SettlementService.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * API do simulador (S5-08 §6.2). Erros como {@code { "error": "<CÓDIGO>" }}.
 *
 * <p>Sem {@code force}, recusa como a rede: entidade sem destino → 404; referência sem 11 dígitos ou cujos dígitos
 * de controlo não batem com a entidade e o montante → 422 {@code INVALID_REFERENCE}; montante ≤ 0 → 422. Não
 * recusa referências já pagas, canceladas ou expiradas (a rede não sabe).
 */
@RestController
public class PaymentsController {

    private static final Pattern REFERENCE = Pattern.compile("^[0-9]{11}$");
    private static final Pattern CHANNEL = Pattern.compile("^[A-Z]{2}$");

    /** Pedido de pagamento; {@code paidAt} e {@code channel} opcionais. */
    public record PaymentRequest(String entity, String reference, Long amountCentavos, Instant paidAt, String channel) {
    }

    private final SettlementService settlements;
    private final MockProperties properties;

    public PaymentsController(SettlementService settlements, MockProperties properties) {
        this.settlements = settlements;
        this.properties = properties;
    }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> pay(@RequestBody PaymentRequest request,
                                   @RequestParam(defaultValue = "false") boolean force) {
        if (request.entity() == null || properties.source(request.entity()).isEmpty()) {
            throw new MockException(404, "UNKNOWN_ENTITY", "No destination for this entity");
        }
        if (request.amountCentavos() == null || request.amountCentavos() <= 0) {
            throw new MockException(422, "INVALID_AMOUNT", "The amount must be greater than zero");
        }
        if (request.reference() == null || !REFERENCE.matcher(request.reference()).matches()) {
            throw new MockException(422, "INVALID_REFERENCE", "The reference must have 11 digits");
        }
        if (!force && !Ponto24CheckDigits.isValid(request.entity(), request.reference(), request.amountCentavos())) {
            throw new MockException(422, "INVALID_REFERENCE",
                    "The check digits do not match the entity and the amount");
        }
        if (request.channel() != null && !request.channel().isEmpty() && !CHANNEL.matcher(request.channel()).matches()) {
            throw new MockException(422, "INVALID_CHANNEL", "The terminal type has 2 capital letters");
        }
        String channel = request.channel() == null || request.channel().isEmpty() ? null : request.channel();
        Payment p = settlements.pay(request.entity(), request.reference(), request.amountCentavos(), request.paidAt(),
                channel, force);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", p.id);
        body.put("status", p.status.name());
        return body;
    }

    @GetMapping("/payments")
    public List<Map<String, Object>> payments() {
        return settlements.payments();
    }

    @PostMapping("/settlements/run")
    public Map<String, Object> run(@RequestParam(required = false) String entity,
                                   @RequestParam(required = false) Corruption corrupt) {
        return settlements.run(emptyToNull(entity), corrupt);
    }

    @PostMapping("/settlements/empty")
    public Map<String, Object> empty(@RequestParam String entity) {
        return Map.of("files", List.of(settlements.empty(entity)));
    }

    @PostMapping("/settlements/resend")
    public Map<String, Object> resend(@RequestParam String file) {
        return Map.of("files", List.of(settlements.resend(file)));
    }

    @PostMapping("/settlements/official-example")
    public Map<String, Object> officialExample(@RequestParam Format format) {
        return Map.of("files", List.of(settlements.officialExample(format)));
    }

    @GetMapping("/settlements")
    public List<Map<String, Object>> settlements() {
        return settlements.files();
    }

    /** Entidades configuradas, para a página (sem palavras-passe). */
    @GetMapping("/sources")
    public List<Map<String, Object>> sources() {
        return properties.sources().stream().map(PaymentsController::source).toList();
    }

    @PostMapping("/__mock/reset")
    public ResponseEntity<Void> reset() {
        settlements.reset();
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MockException.class)
    ResponseEntity<Map<String, Object>> mockError(MockException e) {
        return ResponseEntity.status(e.status()).body(Map.of("error", e.code(), "message", e.getMessage()));
    }

    @ExceptionHandler(SftpDelivery.DeliveryException.class)
    ResponseEntity<Map<String, Object>> deliveryError(SftpDelivery.DeliveryException e) {
        return ResponseEntity.status(502).body(Map.of("error", "SFTP_DELIVERY_FAILED", "message", e.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<Map<String, Object>> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", "BAD_REQUEST"));
    }

    private static Map<String, Object> source(Source s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("entity", s.entity());
        m.put("format", s.format().name());
        m.put("formatLabel", s.formatLabel());
        m.put("destination", s.destination());
        return m;
    }

    private static String emptyToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
