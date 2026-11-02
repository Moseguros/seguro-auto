package mz.co.moseguros.shared.security;

import java.util.Map;

import io.swagger.v3.oas.annotations.Hidden;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints só para testes (perfil {@code test}, fora do contrato): o {@link TenantSnapshot}
 * do pedido e um endpoint protegido por permissão.
 */
@Hidden
@Profile("test")
@RestController
@RequestMapping("/api/v1/test-probes")
public class SecurityProbeController {

    @GetMapping("/tenant")
    public TenantSnapshot tenant() {
        return TenantContext.current();
    }

    @GetMapping("/organization-manage")
    @PreAuthorize("hasAuthority('organization:manage')")
    public Map<String, String> organizationManage() {
        return Map.of("result", "ok");
    }
}
