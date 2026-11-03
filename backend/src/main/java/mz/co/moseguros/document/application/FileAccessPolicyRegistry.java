package mz.co.moseguros.document.application;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import mz.co.moseguros.document.FileAccessPolicy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Todas as {@link FileAccessPolicy} da aplicação, por tipo de dono. Recolhidas no arranque: uma
 * política com {@code ownerTypes()} vazio, ou dois beans para o mesmo tipo, impedem o arranque.
 */
@Component
public class FileAccessPolicyRegistry {

    private final Map<String, FileAccessPolicy> byOwnerType;

    @Autowired
    public FileAccessPolicyRegistry(ObjectProvider<FileAccessPolicy> policies) {
        this(policies.orderedStream().toList());
    }

    FileAccessPolicyRegistry(List<FileAccessPolicy> policies) {
        Map<String, FileAccessPolicy> map = new TreeMap<>();
        for (FileAccessPolicy policy : policies) {
            Set<String> types = policy.ownerTypes();
            if (types == null || types.isEmpty()) {
                throw new IllegalStateException("FileAccessPolicy " + policy.getClass().getName()
                        + " declares no owner types");
            }
            for (String type : types) {
                FileAccessPolicy previous = map.putIfAbsent(type, policy);
                if (previous != null) {
                    throw new IllegalStateException("Owner type " + type + " has two FileAccessPolicy beans: "
                            + previous.getClass().getName() + " and " + policy.getClass().getName());
                }
            }
        }
        this.byOwnerType = Collections.unmodifiableMap(map);
    }

    public Optional<FileAccessPolicy> find(String ownerType) {
        return Optional.ofNullable(ownerType == null ? null : byOwnerType.get(ownerType));
    }

    /** Política do tipo; sem política → {@link IllegalArgumentException} (erro de programação). */
    public FileAccessPolicy require(String ownerType) {
        return find(ownerType).orElseThrow(() ->
                new IllegalArgumentException("No FileAccessPolicy registered for owner type " + ownerType));
    }

    /** Tipos com política registada. */
    public Set<String> ownerTypes() {
        return byOwnerType.keySet();
    }
}
