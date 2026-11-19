package mz.co.moseguros.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Modelos carregados no arranque (S6-04 §3 e §10): 9 códigos × 4 canais × 2 línguas, nenhuma migração com
 * textos (D7), todos válidos (SMS ≤ 160 depois de normalizados) e actualização só das linhas não personalizadas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class TemplateSeedIT {

    static final List<String> CODES = List.of("PROPOSAL_ACCEPTED", "PROPOSAL_REJECTED", "PROPOSAL_INFO_REQUESTED",
            "PROPOSAL_EXPIRED", "PAYMENT_SUCCEEDED", "PAYMENT_FAILED", "PAYMENT_REFERENCE_GENERATED", "POLICY_ISSUED",
            "QUOTE_EXPIRING");

    @Autowired
    TemplateSeeder seeder;

    @Autowired
    TemplateRenderer renderer;

    @Autowired
    IdentityFixtures fixtures;

    @Test
    void startupLoadsTheSeventyTwoTemplates() {
        assertThat(fixtures.owner().queryForObject("select count(*) from notification.template where active",
                Integer.class)).isEqualTo(72);
        assertThat(fixtures.owner().queryForList("select distinct code from notification.template order by code",
                String.class)).containsExactlyInAnyOrderElementsOf(CODES);
        for (String code : CODES) {
            assertThat(fixtures.owner().queryForObject(
                    "select count(*) from notification.template where code = ? and active", Integer.class, code))
                    .as(code).isEqualTo(8);
        }
        assertThat(fixtures.owner().queryForObject("""
                select count(*) from notification.template where channel = 'WHATSAPP'
                   and (whatsapp_template_name is null or whatsapp_template_name not like 'moseguros\\_%')""",
                Integer.class)).isZero();
    }

    @Test
    void noMigrationInsertsTemplateTexts() throws IOException {
        Path migrations = Path.of("src/main/resources/db/migration");
        try (Stream<Path> files = Files.list(migrations)) {
            for (Path file : files.toList()) {
                String sql = Files.readString(file, StandardCharsets.UTF_8).toLowerCase();
                assertThat(sql).as(file.getFileName().toString()).doesNotContain("insert into notification.template");
            }
        }
    }

    @Test
    void everyDefaultTemplatePassesTheValidation() {
        TemplateCatalog catalog = TemplateCatalog.load();
        assertThat(catalog.codes().keySet()).containsExactlyInAnyOrderElementsOf(CODES);
        List<String> problems = new ArrayList<>();
        catalog.codes().values().forEach(code -> code.texts().forEach((key, text) -> {
            List<FieldError> errors = renderer.validate(key.channel(), text.subject(), text.body(),
                    text.whatsappTemplateName(), code.variables(), code.samples());
            errors.forEach(e -> problems.add(code.code() + "/" + key + ": " + e.field() + " " + e.code()));
            if (key.channel() == MessageChannel.SMS) {
                String rendered = renderer.render(text.body(), code.samples());
                SmsText.Measured sms = SmsText.measure(rendered, true);
                if (!sms.fits() || sms.limit() != SmsText.GSM_LIMIT) {
                    problems.add(code.code() + "/" + key + ": SMS " + sms.length() + "/" + sms.limit());
                }
            }
        }));
        assertThat(problems).isEmpty();
        assertThat(catalog.codes().values()).allSatisfy(code -> {
            assertThat(code.texts()).hasSize(8);
            assertThat(code.variables()).contains("link", "siteUrl");
            assertThat(code.samples().keySet()).containsAll(code.variables());
        });
    }

    @Test
    void aChangedFileTextUpdatesOnlyNonCustomizedRows() {
        Map<String, Properties> files = TemplateCatalog.readFiles();
        files.get("pt").setProperty("notify.PROPOSAL_EXPIRED.sms.body", "MoSeguros: proposta {{proposalNumber}} "
                + "expirada. {{siteUrl}}");
        files.get("pt").setProperty("notify.PROPOSAL_EXPIRED.push.body", "Proposta {{proposalNumber}} expirada.");
        String customizedBody = "Texto do administrador {{proposalNumber}}";
        fixtures.owner().update("""
                update notification.template set body = ?, customized = true
                 where code = 'PROPOSAL_EXPIRED' and channel = 'PUSH' and locale = 'pt'""", customizedBody);
        try {
            Map<String, Integer> counts = seeder.seed(TemplateCatalog.of(files));
            assertThat(counts.get("inserted")).isZero();
            assertThat(counts.get("updated")).isEqualTo(2);
            assertThat(body("SMS")).isEqualTo("MoSeguros: proposta {{proposalNumber}} expirada. {{siteUrl}}");
            assertThat(body("PUSH")).isEqualTo(customizedBody);
            assertThat(fixtures.owner().queryForObject("""
                    select default_hash from notification.template
                     where code = 'PROPOSAL_EXPIRED' and channel = 'PUSH' and locale = 'pt'""", String.class))
                    .isEqualTo(TemplateCatalog.hash(TemplateCatalog.of(files)
                            .text("PROPOSAL_EXPIRED", MessageChannel.PUSH, "pt").orElseThrow()));

            // uma linha que sai do ficheiro fica inactiva
            files.get("pt").remove("notify.PROPOSAL_EXPIRED.whatsapp.body");
            assertThat(seeder.seed(TemplateCatalog.of(files)).get("deactivated")).isEqualTo(1);
            assertThat(fixtures.owner().queryForObject("""
                    select active from notification.template
                     where code = 'PROPOSAL_EXPIRED' and channel = 'WHATSAPP' and locale = 'pt'""", Boolean.class))
                    .isFalse();
        } finally {
            fixtures.owner().update("""
                    update notification.template set customized = false
                     where code = 'PROPOSAL_EXPIRED' and channel = 'PUSH' and locale = 'pt'""");
            // o hash da linha personalizada já é o do ficheiro alterado: repor o original como dono
            fixtures.owner().update("update notification.template set default_hash = 'x' where code = 'PROPOSAL_EXPIRED'");
            seeder.seed(TemplateCatalog.load());
        }
        assertThat(body("SMS")).startsWith("MoSeguros: a proposta {{proposalNumber}} expirou.");
        assertThat(body("PUSH")).isEqualTo("A proposta {{proposalNumber}} expirou. Pode fazer uma nova simulação.");
        assertThat(fixtures.owner().queryForObject("select count(*) from notification.template where active",
                Integer.class)).isEqualTo(72);
    }

    private String body(String channel) {
        return fixtures.owner().queryForObject("""
                select body from notification.template where code = 'PROPOSAL_EXPIRED' and channel = ? and locale = 'pt'""",
                String.class, channel);
    }
}
