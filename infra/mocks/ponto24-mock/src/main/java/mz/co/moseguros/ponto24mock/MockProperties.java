package mz.co.moseguros.ponto24mock;

import java.net.URI;
import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Configuração do simulador (S5-08 §6.1):
 *
 * <ul>
 *   <li>{@code PONTO24_MOCK_SOURCES}: {@code <entidade>=<formato>@sftp://<utilizador>@<host>:<porta>/<pasta>}
 *       separados por vírgulas; no BMEPS o formato leva a empresa e o perfil ({@code BMEPS:800:02});</li>
 *   <li>{@code PONTO24_MOCK_SFTP_PASSWORD_<entidade>}: palavra-passe SFTP de cada entidade (nunca nos logs);</li>
 *   <li>{@code PONTO24_MOCK_ZONE} e {@code PONTO24_MOCK_FILE_STYLE} ({@code EXAMPLE} ou {@code SPEC}).</li>
 * </ul>
 */
@Component
public class MockProperties {

    static final String PASSWORD_PREFIX = "PONTO24_MOCK_SFTP_PASSWORD_";

    private static final Pattern ENTITY = Pattern.compile("^[0-9]{5}$");
    private static final Pattern COMPANY = Pattern.compile("^[0-9]{3}$");
    private static final Pattern PROFILE = Pattern.compile("^[0-9]{2}$");

    /** Formato dos ficheiros de uma entidade. */
    public enum Format { MEPS, BMEPS }

    /** Estilo dos ficheiros gerados (S5-08 §6.1). */
    public enum FileStyle { EXAMPLE, SPEC }

    /**
     * Uma entidade configurada e o seu destino SFTP.
     *
     * @param dir pasta de entrega, relativa à pasta inicial do utilizador SFTP (ex.: {@code inbound})
     */
    public record Source(String entity, Format format, String companyId, String profileId, String host, int port,
                         String username, String dir) {

        /** {@code sftp://utilizador@host:porta/pasta} (sem a palavra-passe). */
        public String destination() {
            return "sftp://" + username + "@" + host + ":" + port + "/" + dir;
        }

        /** Formato como aparece na página: {@code MEPS} ou {@code BMEPS 800/02}. */
        public String formatLabel() {
            return format == Format.BMEPS ? "BMEPS " + companyId + "/" + profileId : "MEPS";
        }
    }

    private final Map<String, Source> sources;
    private final ZoneId zone;
    private final FileStyle fileStyle;
    private final Environment environment;

    public MockProperties(@Value("${ponto24mock.sources}") String sources,
                          @Value("${ponto24mock.zone}") String zone,
                          @Value("${ponto24mock.file-style}") String fileStyle,
                          Environment environment) {
        this.sources = parseSources(sources);
        this.zone = ZoneId.of(zone);
        this.fileStyle = FileStyle.valueOf(fileStyle.trim().toUpperCase());
        this.environment = environment;
    }

    static Map<String, Source> parseSources(String value) {
        Map<String, Source> out = new LinkedHashMap<>();
        if (value == null || value.isBlank()) {
            return out;
        }
        for (String item : value.split(",")) {
            String s = item.trim();
            if (s.isEmpty()) {
                continue;
            }
            int eq = s.indexOf('=');
            int at = s.indexOf('@');
            if (eq < 0 || at < eq) {
                throw new IllegalArgumentException("Invalid PONTO24_MOCK_SOURCES item: " + s);
            }
            String entity = s.substring(0, eq).trim();
            if (!ENTITY.matcher(entity).matches()) {
                throw new IllegalArgumentException("The entity must have 5 digits: " + entity);
            }
            String[] format = s.substring(eq + 1, at).trim().split(":");
            Format f = Format.valueOf(format[0].trim().toUpperCase());
            String company = null;
            String profile = null;
            if (f == Format.BMEPS) {
                if (format.length != 3 || !COMPANY.matcher(format[1]).matches()
                        || !PROFILE.matcher(format[2]).matches()) {
                    throw new IllegalArgumentException("BMEPS needs company (3 digits) and profile (2 digits): " + s);
                }
                company = format[1];
                profile = format[2];
            } else if (format.length != 1) {
                throw new IllegalArgumentException("Only BMEPS takes company and profile: " + s);
            }
            URI uri = URI.create(s.substring(at + 1).trim());
            if (!"sftp".equals(uri.getScheme()) || uri.getUserInfo() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("Destination must be sftp://user@host:port/dir: " + s);
            }
            String dir = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/+", "");
            out.put(entity, new Source(entity, f, company, profile, uri.getHost(), uri.getPort() < 0 ? 22 : uri.getPort(),
                    uri.getUserInfo(), dir));
        }
        return out;
    }

    public Optional<Source> source(String entity) {
        return Optional.ofNullable(entity == null ? null : sources.get(entity));
    }

    public Collection<Source> sources() {
        return sources.values();
    }

    /** Palavra-passe SFTP da entidade ({@code PONTO24_MOCK_SFTP_PASSWORD_<entidade>}), ou vazio. */
    public Optional<String> password(String entity) {
        return Optional.ofNullable(environment.getProperty(PASSWORD_PREFIX + entity)).filter(p -> !p.isEmpty());
    }

    public ZoneId zone() {
        return zone;
    }

    public FileStyle fileStyle() {
        return fileStyle;
    }
}
