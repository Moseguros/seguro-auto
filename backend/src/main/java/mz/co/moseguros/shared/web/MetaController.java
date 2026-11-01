package mz.co.moseguros.shared.web;

import java.time.Instant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Informação técnica da aplicação, pública.
 */
@RestController
@RequestMapping("/api/v1/meta")
@Tag(name = "Meta", description = "Informação técnica da aplicação")
public class MetaController {

    static final String APP_NAME = "moseguros-backend";

    private final ObjectProvider<BuildProperties> build;
    private final ObjectProvider<GitProperties> git;

    public MetaController(ObjectProvider<BuildProperties> build, ObjectProvider<GitProperties> git) {
        this.build = build;
        this.git = git;
    }

    @GetMapping("/version")
    @SecurityRequirements
    @Operation(operationId = "getVersion", summary = "Versão da API")
    public VersionResponse version() {
        BuildProperties b = build.getIfAvailable();
        GitProperties g = git.getIfAvailable();
        String version = b != null ? b.getVersion() : "dev";
        Instant builtAt = b != null ? b.getTime() : null;
        String commit = g != null && g.getShortCommitId() != null ? g.getShortCommitId() : "local";
        return new VersionResponse(APP_NAME, version, commit, builtAt);
    }

    /** Versão da aplicação. */
    public record VersionResponse(String name, String version, String commit, Instant builtAt) {
    }
}
