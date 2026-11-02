package mz.co.moseguros.smsmock;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API do simulador: recebe, lista e limpa SMS. A página {@code GET /} é o
 * {@code static/index.html}, que lê {@code GET /sms}.
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsStore store;

    public SmsController(SmsStore store) {
        this.store = store;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SendResponse send(@Valid @RequestBody SendRequest request) {
        return new SendResponse(store.add(request.to(), request.text()).id());
    }

    @GetMapping
    public List<SmsMessage> list() {
        return store.list();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear() {
        store.clear();
    }

    public record SendRequest(@NotBlank String to, @NotBlank String text) {
    }

    public record SendResponse(String id) {
    }
}
