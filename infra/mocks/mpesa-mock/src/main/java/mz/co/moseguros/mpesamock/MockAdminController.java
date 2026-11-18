package mz.co.moseguros.mpesamock;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de apoio (só do simulador, porta principal, sem autenticação): lista e limpeza das
 * transacções e atrasos em execução. A página {@code GET /} está em {@code static/index.html}.
 */
@RestController
@RequestMapping("/__mock")
class MockAdminController {

    private final TransactionStore store;
    private final MockProperties props;

    MockAdminController(TransactionStore store, MockProperties props) {
        this.store = store;
        this.props = props;
    }

    /** Mais recente primeiro, últimas 500. */
    @GetMapping("/transactions")
    List<MockTransaction> transactions() {
        return store.latest();
    }

    @PostMapping("/reset")
    ResponseEntity<Void> reset() {
        store.reset();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config")
    Map<String, Integer> config() {
        Map<String, Integer> config = new LinkedHashMap<>();
        config.put("defaultDelaySeconds", props.getDefaultDelaySeconds());
        config.put("delay000Seconds", props.getDelay000Seconds());
        config.put("slowDelaySeconds", props.getSlowDelaySeconds());
        config.put("failureDelaySeconds", props.getFailureDelaySeconds());
        return config;
    }

    /** Campos em falta ficam como estavam; valores negativos → 400. */
    @PutMapping("/config")
    ResponseEntity<Map<String, Integer>> updateConfig(@RequestBody DelayConfig update) {
        if (negative(update.defaultDelaySeconds()) || negative(update.delay000Seconds())
                || negative(update.slowDelaySeconds()) || negative(update.failureDelaySeconds())) {
            return ResponseEntity.badRequest().build();
        }
        if (update.defaultDelaySeconds() != null) {
            props.setDefaultDelaySeconds(update.defaultDelaySeconds());
        }
        if (update.delay000Seconds() != null) {
            props.setDelay000Seconds(update.delay000Seconds());
        }
        if (update.slowDelaySeconds() != null) {
            props.setSlowDelaySeconds(update.slowDelaySeconds());
        }
        if (update.failureDelaySeconds() != null) {
            props.setFailureDelaySeconds(update.failureDelaySeconds());
        }
        return ResponseEntity.ok(config());
    }

    private static boolean negative(Integer value) {
        return value != null && value < 0;
    }

    record DelayConfig(Integer defaultDelaySeconds, Integer delay000Seconds, Integer slowDelaySeconds,
            Integer failureDelaySeconds) {}
}
