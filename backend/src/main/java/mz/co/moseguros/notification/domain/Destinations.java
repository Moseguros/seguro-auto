package mz.co.moseguros.notification.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;

/**
 * Destinos de mensagens: máscara para guardar e registar, e hash para pesquisar sem guardar o
 * valor em claro.
 */
public final class Destinations {

    private Destinations() {
    }

    /** {@code +258*****4567} para telemóveis, {@code a***@exemplo.co.mz} para e-mails. */
    public static String mask(String destination) {
        if (destination == null || destination.isBlank()) {
            return "***";
        }
        int at = destination.indexOf('@');
        if (at >= 0) {
            String local = destination.substring(0, at);
            return (local.isEmpty() ? "" : local.substring(0, 1)) + "***" + destination.substring(at);
        }
        try {
            return Msisdn.of(destination).masked();
        } catch (DomainException e) {
            String digits = destination.replaceAll("[^0-9]", "");
            return "***" + (digits.length() > 4 ? digits.substring(digits.length() - 4) : "");
        }
    }

    /**
     * SHA-256 (hexadecimal) do destino normalizado: telemóveis como {@code 258XXXXXXXXX},
     * e-mails em minúsculas.
     */
    public static String hash(String destination) {
        String normalized = normalize(destination);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(normalized.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    static String normalize(String destination) {
        String value = destination == null ? "" : destination.strip();
        if (value.indexOf('@') >= 0) {
            return value.toLowerCase(Locale.ROOT);
        }
        try {
            return Msisdn.of(value).value();
        } catch (DomainException e) {
            return value;
        }
    }
}
