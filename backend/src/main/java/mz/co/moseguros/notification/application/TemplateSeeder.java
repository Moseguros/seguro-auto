package mz.co.moseguros.notification.application;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mz.co.moseguros.notification.application.TemplateCatalog.CodeDefaults;
import mz.co.moseguros.notification.application.TemplateCatalog.Key;
import mz.co.moseguros.notification.domain.NotificationTemplate;
import mz.co.moseguros.notification.domain.NotificationTemplate.TemplateText;
import mz.co.moseguros.notification.domain.NotificationTemplateRepository;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Carrega os modelos de {@code messages/notifications_{pt,en}.properties} em {@code notification.template} em
 * cada arranque (S6-04 §3; nenhuma migração insere textos, decisão D7). Para cada (código, canal, língua):
 * <ul>
 *   <li>não existe → insere ({@code customized = false});</li>
 *   <li>existe, não personalizado e o {@code default_hash} mudou → actualiza o texto;</li>
 *   <li>existe e personalizado → mantém o texto do administrador; actualiza variáveis, exemplos e hash;</li>
 *   <li>linhas que deixaram de estar no ficheiro → {@code active = false} e {@code WARN}.</li>
 * </ul>
 */
@Component
public class TemplateSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TemplateSeeder.class);

    private final NotificationTemplateRepository templates;
    private final TemplateRenderer renderer;
    private final TransactionTemplate tx;

    public TemplateSeeder(NotificationTemplateRepository templates, TemplateRenderer renderer, TransactionTemplate tx) {
        this.templates = templates;
        this.renderer = renderer;
        this.tx = tx;
    }

    @Override
    @RunAsSystem
    public void run(ApplicationArguments args) {
        seed(TemplateCatalog.load());
    }

    /** @return contagens {@code inserted}, {@code updated}, {@code deactivated} */
    @RunAsSystem
    public Map<String, Integer> seed(TemplateCatalog catalog) {
        int[] counts = new int[3];
        tx.executeWithoutResult(status -> {
            Set<java.util.UUID> seen = new HashSet<>();
            for (CodeDefaults code : catalog.codes().values()) {
                for (Map.Entry<Key, TemplateText> entry : code.texts().entrySet()) {
                    Key key = entry.getKey();
                    TemplateText text = entry.getValue();
                    String hash = TemplateCatalog.hash(text);
                    warnIfInvalid(code, key, text);
                    NotificationTemplate existing = templates.findByCodeAndChannelAndLocale(code.code(), key.channel(),
                            key.locale()).orElse(null);
                    if (existing == null) {
                        existing = templates.save(NotificationTemplate.fromDefaults(code.code(), key.channel(),
                                key.locale(), text, code.variables(), code.samples(), hash));
                        counts[0]++;
                    } else if (!existing.getDefaultHash().equals(hash)) {
                        existing.refreshDefaults(text, code.variables(), code.samples(), hash);
                        counts[1]++;
                    } else {
                        if (!existing.getVariables().equals(code.variables())
                                || !existing.getSampleParams().equals(code.samples())) {
                            existing.refreshMetadata(code.variables(), code.samples(), hash);
                            counts[1]++;
                        }
                        existing.reactivateDefault();
                    }
                    seen.add(existing.getId());
                }
            }
            for (NotificationTemplate t : templates.findAll()) {
                if (!seen.contains(t.getId()) && t.isActive()) {
                    t.deactivateRemoved();
                    counts[2]++;
                    log.warn("Notification template {}/{}/{} is no longer in the messages file: deactivated",
                            t.getCode(), t.getChannel(), t.getLocale());
                }
            }
        });
        log.info("Notification templates seeded: inserted={} updated={} deactivated={}", counts[0], counts[1],
                counts[2]);
        return Map.of("inserted", counts[0], "updated", counts[1], "deactivated", counts[2]);
    }

    private void warnIfInvalid(CodeDefaults code, Key key, TemplateText text) {
        List<FieldError> errors = renderer.validate(key.channel(), text.subject(), text.body(),
                text.whatsappTemplateName(), code.variables(), code.samples());
        if (!errors.isEmpty()) {
            log.warn("Default notification template {}/{}/{} does not pass validation: {}", code.code(),
                    key.channel(), key.locale(), errors.stream().map(FieldError::code).toList());
        }
    }
}
