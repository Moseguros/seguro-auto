package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Primeiro administrador da plataforma (S1-04 §5): com a propriedade, o arranque cria um convite
 * {@code PLATFORM_ADMIN} e envia o e-mail; ao arrancar de novo não cria outro.
 */
@SpringBootTest(properties = {
        "moseguros.bootstrap.platform-admin-email=Admin@MoSeguros.local",
        "moseguros.bootstrap.platform-admin-name=Administradora Inicial"})
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class PlatformAdminBootstrapIT {

    static final String EMAIL = "admin@moseguros.local";

    @Autowired
    PlatformAdminBootstrap bootstrap;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Test
    void createsTheInvitationOnceAndNotAgainOnTheNextStart(CapturedOutput output) {
        JdbcTemplate owner = fixtures.owner();
        Map<String, Object> invitation = owner.queryForMap("""
                select id, full_name, organization_id, role_codes::text as roles, invited_by_user_id
                  from identity.invitation where email = ?""", EMAIL);
        assertThat(invitation.get("full_name")).isEqualTo("Administradora Inicial");
        assertThat(invitation.get("organization_id")).isNull();
        assertThat(invitation.get("invited_by_user_id")).isNull();
        assertThat(invitation.get("roles")).isEqualTo("{PLATFORM_ADMIN}");
        assertThat(channels.lastTo(EMAIL).text())
                .contains("Olá Administradora Inicial,")
                .contains("Foi convidado para usar a MoSeguros. Para activar")
                .contains("http://localhost:5173/convite/");
        assertThat(owner.queryForObject("""
                select count(*) from audit.audit_event
                 where action = 'USER.INVITE' and actor_type = 'SYSTEM' and entity_id = ?""",
                Integer.class, invitation.get("id").toString())).isEqualTo(1);

        // novo arranque: o convite pendente já existe
        bootstrap.run(new DefaultApplicationArguments());
        assertThat(count(owner)).isEqualTo(1);
        assertThat(channels.sentTo(EMAIL)).hasSize(1);
        assertThat(output.getAll()).contains("a***@moseguros.local").doesNotContain(EMAIL)
                .doesNotContain("/convite/");

        // sem convite pendente, mas com um administrador activo: também não cria
        owner.update("update identity.invitation set revoked_at = now() where email = ?", EMAIL);
        fixtures.staff("Outro Admin", null, "PLATFORM_ADMIN");
        bootstrap.run(new DefaultApplicationArguments());
        assertThat(count(owner)).isEqualTo(1);
    }

    private static int count(JdbcTemplate owner) {
        return owner.queryForObject("select count(*) from identity.invitation where email = ?", Integer.class, EMAIL);
    }
}
