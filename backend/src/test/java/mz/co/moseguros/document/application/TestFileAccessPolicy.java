package mz.co.moseguros.document.application;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Política de teste (perfil {@code test}) para o tipo de dono {@value #OWNER_TYPE}. Por defeito
 * recusa; cada teste diz, por {@code ownerId}, quem pode ler.
 */
@Component
@Profile("test")
public class TestFileAccessPolicy implements FileAccessPolicy {

    public static final String OWNER_TYPE = "TEST";

    private final Map<UUID, Rule> rules = new ConcurrentHashMap<>();

    /** Qualquer pedido, mesmo anónimo, lê os ficheiros do dono. */
    public void allowAnyone(UUID ownerId) {
        rules.put(ownerId, new Rule(true, null));
    }

    /** Só o utilizador indicado (autenticado) lê os ficheiros do dono. */
    public void allowUser(UUID ownerId, UUID userId) {
        rules.put(ownerId, new Rule(false, userId));
    }

    @Override
    public Set<String> ownerTypes() {
        return Set.of(OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        Rule rule = rules.get(ownerId);
        if (rule == null) {
            return false;
        }
        if (rule.anyone()) {
            return true;
        }
        return user.isAuthenticated() && user.id().equals(rule.userId());
    }

    private record Rule(boolean anyone, UUID userId) {
    }
}
