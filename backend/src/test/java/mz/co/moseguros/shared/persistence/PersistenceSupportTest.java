package mz.co.moseguros.shared.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.persistence.converters.MatriculaConverter;
import mz.co.moseguros.shared.persistence.converters.MsisdnConverter;
import mz.co.moseguros.shared.persistence.converters.NuitConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class PersistenceSupportTest {

    static class Sample extends BaseEntity {
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void baseEntityAssignsUuidV7AndComparesById() {
        Sample a = new Sample();
        Sample b = new Sample();
        assertThat(a.getId()).isNotNull();
        assertThat(a.getId().version()).isEqualTo(7);
        assertThat(a).isNotEqualTo(b).isEqualTo(a);
        assertThat(a.hashCode()).isEqualTo(a.getId().hashCode());
        assertThat(a.isNew()).isTrue();
        assertThat(a.getVersion()).isZero();
    }

    @Test
    void auditorIsSystemWithoutAuthentication() {
        assertThat(new SecurityAuditorAware().getCurrentAuditor()).contains("system");
    }

    @Test
    void auditorIsSystemForAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        assertThat(new SecurityAuditorAware().getCurrentAuditor()).contains("system");
    }

    @Test
    void auditorIsJwtSubject() {
        Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("user-123")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
        assertThat(new SecurityAuditorAware().getCurrentAuditor()).contains("user-123");
    }

    @Test
    void auditorFallsBackToAuthenticationName() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("job-x", null, List.of()));
        assertThat(new SecurityAuditorAware().getCurrentAuditor()).contains("job-x");
    }

    @Test
    void convertersRoundTrip() {
        MsisdnConverter msisdn = new MsisdnConverter();
        assertThat(msisdn.convertToDatabaseColumn(Msisdn.of("841234567"))).isEqualTo("258841234567");
        assertThat(msisdn.convertToEntityAttribute("258841234567")).isEqualTo(Msisdn.of("841234567"));
        assertThat(msisdn.convertToDatabaseColumn(null)).isNull();
        assertThat(msisdn.convertToEntityAttribute(null)).isNull();

        NuitConverter nuit = new NuitConverter();
        assertThat(nuit.convertToDatabaseColumn(Nuit.of("123 456 789"))).isEqualTo("123456789");
        assertThat(nuit.convertToEntityAttribute("123456789")).isEqualTo(Nuit.of("123456789"));
        assertThat(nuit.convertToEntityAttribute(null)).isNull();

        MatriculaConverter plate = new MatriculaConverter();
        assertThat(plate.convertToDatabaseColumn(Matricula.restore("ABC-123-MC", "STANDARD"))).isEqualTo("ABC-123-MC");
        assertThat(plate.convertToEntityAttribute("OLD-1").value()).isEqualTo("OLD-1");
        assertThat(plate.convertToEntityAttribute(null)).isNull();
    }
}
