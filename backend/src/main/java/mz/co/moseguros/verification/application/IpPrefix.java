package mz.co.moseguros.verification.application;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP truncado do registo das verificações (S6-03 §4 regra 5): IPv4 → os três primeiros octetos +
 * {@code .0/24} ({@code 41.220.15.0/24}); IPv6 → os primeiros 48 bits + {@code ::/48}. O IP completo só
 * se usa em memória, para o limite de pedidos.
 */
public final class IpPrefix {

    private IpPrefix() {
    }

    /** Prefixo do endereço literal; {@code null} se não for um IPv4 ou IPv6 literal (nunca resolve nomes). */
    public static String of(String ip) {
        if (ip == null || ip.isBlank()) {
            return null;
        }
        String value = ip.strip();
        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1);
        }
        int zone = value.indexOf('%');
        if (zone >= 0) {
            value = value.substring(0, zone);
        }
        boolean ipv4 = value.matches("\\d{1,3}(\\.\\d{1,3}){3}");
        boolean ipv6 = value.contains(":") && value.matches("[0-9A-Fa-f:.]+");
        if (!ipv4 && !ipv6) {
            return null;
        }
        byte[] bytes;
        try {
            bytes = InetAddress.getByName(value).getAddress();
        } catch (UnknownHostException e) {
            return null;
        }
        if (bytes.length == 4) {
            return (bytes[0] & 0xFF) + "." + (bytes[1] & 0xFF) + "." + (bytes[2] & 0xFF) + ".0/24";
        }
        if (isIpv4Mapped(bytes)) {
            return (bytes[12] & 0xFF) + "." + (bytes[13] & 0xFF) + "." + (bytes[14] & 0xFF) + ".0/24";
        }
        return String.format("%x:%x:%x::/48", word(bytes, 0), word(bytes, 2), word(bytes, 4));
    }

    private static int word(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 8) | (bytes[offset + 1] & 0xFF);
    }

    private static boolean isIpv4Mapped(byte[] bytes) {
        for (int i = 0; i < 10; i++) {
            if (bytes[i] != 0) {
                return false;
            }
        }
        return (bytes[10] & 0xFF) == 0xFF && (bytes[11] & 0xFF) == 0xFF;
    }
}
