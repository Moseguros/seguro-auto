package mz.co.moseguros.shared.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * IP do cliente de um pedido HTTP (S0-03, decisão D15). Usa o primeiro valor de
 * {@code X-Forwarded-For} <strong>só</strong> quando o endereço remoto do pedido está na lista
 * {@code moseguros.web.trusted-proxies} (IPs ou CIDR, vazia por defeito); nos outros casos usa o
 * endereço remoto, para o cabeçalho não poder ser forjado por quem chama directamente o backend.
 *
 * <p>Usado pelo {@code AuditService}, pela assinatura das propostas (S4-04) e pelos limites de pedidos
 * (S6-03). O proxy de confiança tem de substituir o cabeçalho que vem do cliente
 * ({@code proxy_set_header X-Forwarded-For $remote_addr}), não acrescentar-lhe.
 */
@Component
@EnableConfigurationProperties(ClientIpResolver.Properties.class)
public class ClientIpResolver {

    static final String FORWARDED_FOR = "X-Forwarded-For";

    private final List<Cidr> trustedProxies;

    public ClientIpResolver(Properties properties) {
        List<Cidr> parsed = new ArrayList<>();
        for (String entry : properties.trustedProxies() == null ? List.<String>of() : properties.trustedProxies()) {
            if (entry != null && !entry.isBlank()) {
                parsed.add(Cidr.parse(entry.strip()));
            }
        }
        this.trustedProxies = List.copyOf(parsed);
    }

    /**
     * {@code moseguros.web.trusted-proxies}: endereços (ex.: {@code 10.0.0.2}) ou redes CIDR (ex.:
     * {@code 172.18.0.0/16}) dos proxies cujo {@code X-Forwarded-For} é aceite.
     */
    @ConfigurationProperties(prefix = "moseguros.web")
    public record Properties(List<String> trustedProxies) {
    }

    /** IP do cliente: o primeiro {@code X-Forwarded-For} de um proxy de confiança, ou o endereço remoto. */
    public String resolve(HttpServletRequest request) {
        Objects.requireNonNull(request, "request");
        String remote = request.getRemoteAddr();
        if (remote != null && isTrusted(remote)) {
            String forwarded = request.getHeader(FORWARDED_FOR);
            if (forwarded != null && !forwarded.isBlank()) {
                String first = forwarded.split(",", 2)[0].strip();
                if (!first.isEmpty()) {
                    return first;
                }
            }
        }
        return remote;
    }

    boolean isTrusted(String address) {
        if (trustedProxies.isEmpty()) {
            return false;
        }
        byte[] bytes = literal(address);
        return bytes != null && trustedProxies.stream().anyMatch(cidr -> cidr.contains(bytes));
    }

    /**
     * Bytes de um endereço IPv4 ou IPv6 literal; {@code null} se não for um literal (nunca resolve
     * nomes por DNS).
     */
    static byte[] literal(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }
        String value = address.strip();
        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1);
        }
        boolean ipv4 = value.matches("\\d{1,3}(\\.\\d{1,3}){3}");
        boolean ipv6 = value.contains(":") && value.matches("[0-9A-Fa-f:.%a-zA-Z0-9]+");
        if (!ipv4 && !ipv6) {
            return null;
        }
        int zone = value.indexOf('%');
        if (zone >= 0) {
            value = value.substring(0, zone);
        }
        try {
            return InetAddress.getByName(value).getAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /** Rede {@code endereço/prefixo}; um endereço sem prefixo vale só por si. */
    record Cidr(byte[] network, int prefix) {

        static Cidr parse(String text) {
            int slash = text.indexOf('/');
            String address = slash < 0 ? text : text.substring(0, slash);
            byte[] bytes = literal(address);
            if (bytes == null) {
                throw new IllegalArgumentException("moseguros.web.trusted-proxies: invalid address '" + text + "'");
            }
            int prefix = bytes.length * 8;
            if (slash >= 0) {
                try {
                    prefix = Integer.parseInt(text.substring(slash + 1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("moseguros.web.trusted-proxies: invalid prefix '" + text + "'");
                }
                if (prefix < 0 || prefix > bytes.length * 8) {
                    throw new IllegalArgumentException("moseguros.web.trusted-proxies: invalid prefix '" + text + "'");
                }
            }
            return new Cidr(bytes, prefix);
        }

        boolean contains(byte[] address) {
            if (address.length != network.length) {
                return false;
            }
            int full = prefix / 8;
            for (int i = 0; i < full; i++) {
                if (address[i] != network[i]) {
                    return false;
                }
            }
            int rest = prefix % 8;
            if (rest == 0) {
                return true;
            }
            int mask = (0xFF << (8 - rest)) & 0xFF;
            return (address[full] & mask) == (network[full] & mask);
        }
    }
}
