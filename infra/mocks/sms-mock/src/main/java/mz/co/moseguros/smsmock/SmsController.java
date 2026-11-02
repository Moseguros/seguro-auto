package mz.co.moseguros.smsmock;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mz.co.moseguros.smsmock.DeliveryModes.Mode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * API do simulador: recebe SMS ({@code POST /sms}) e mensagens de WhatsApp com modelo
 * ({@code POST /whatsapp}), lista-as e limpa-as ({@code /sms}, os dois canais), e muda o modo de
 * cada canal ({@code /config}). A página {@code GET /} é o {@code static/index.html}.
 */
@RestController
public class SmsController {

    static final String SMS_PROVIDER = "mock-sms";
    static final String WHATSAPP_PROVIDER = "mock-whatsapp";

    private final SmsStore store;
    private final DeliveryModes modes;
    private final ReceiptSender receipts;

    public SmsController(SmsStore store, DeliveryModes modes, ReceiptSender receipts) {
        this.store = store;
        this.modes = modes;
        this.receipts = receipts;
    }

    @PostMapping("/sms")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SendResponse send(@Valid @RequestBody SendRequest request) {
        Mode mode = accept(modes.sms());
        SmsMessage message = store.add(request.to(), request.text());
        receipts.schedule(SMS_PROVIDER, message.id(), receiptStatus(mode));
        return new SendResponse(message.id());
    }

    @PostMapping("/whatsapp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SendResponse sendWhatsApp(@Valid @RequestBody WhatsAppRequest request) {
        Mode mode = accept(modes.whatsapp());
        SmsMessage message = store.addWhatsApp(request.to(), request.template(), request.params());
        receipts.schedule(WHATSAPP_PROVIDER, message.id(), receiptStatus(mode));
        return new SendResponse(message.id());
    }

    @GetMapping("/sms")
    public List<SmsMessage> list() {
        return store.list();
    }

    @DeleteMapping("/sms")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear() {
        store.clear();
    }

    @GetMapping("/config")
    public DeliveryModes.Config config() {
        return modes.get();
    }

    @PutMapping("/config")
    public DeliveryModes.Config updateConfig(@RequestBody DeliveryModes.Config config) {
        return modes.update(config);
    }

    /** {@code FAIL} → 503 (como um fornecedor indisponível). */
    private static Mode accept(Mode mode) {
        if (mode == Mode.FAIL) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Simulated provider failure");
        }
        return mode;
    }

    private static String receiptStatus(Mode mode) {
        return mode == Mode.FAIL_RECEIPT ? "FAILED" : "DELIVERED";
    }

    public record SendRequest(@NotBlank String to, @NotBlank String text) {
    }

    public record WhatsAppRequest(@NotBlank String to, @NotBlank String template, Map<String, String> params) {
    }

    public record SendResponse(String id) {
    }
}
