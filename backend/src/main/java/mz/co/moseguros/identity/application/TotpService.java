package mz.co.moseguros.identity.application;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.OptionalLong;
import java.util.regex.Pattern;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

/**
 * TOTP do staff (RFC 6238, ADR-003): SHA-1, 6 dígitos, passos de 30 s. Aceita o passo actual, o
 * anterior e o seguinte (tolerância de relógio), e nunca um passo já usado.
 *
 * <p>Os segredos têm 20 bytes aleatórios em Base32 (32 caracteres) e nunca são registados.
 */
@Service
public class TotpService {

    public static final String ISSUER = "MoSeguros";
    public static final int DIGITS = 6;
    public static final int PERIOD_SECONDS = 30;
    static final int SECRET_BYTES = 20;
    /** Passos aceites antes e depois do actual. */
    static final int ALLOWED_DRIFT = 1;

    private static final Pattern CODE = Pattern.compile("^\\d{" + DIGITS + "}$");

    private final CodeGenerator generator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, DIGITS);
    private final SecureRandom random = new SecureRandom();
    private final Clock clock;

    public TotpService(Clock clock) {
        this.clock = clock;
    }

    /** Segredo novo: 20 bytes aleatórios em Base32, sem {@code =}. */
    public String newSecret() {
        byte[] bytes = new byte[SECRET_BYTES];
        random.nextBytes(bytes);
        return new Base32().encodeToString(bytes).replace("=", "");
    }

    /**
     * {@code otpauth://totp/MoSeguros:{email}?secret=…&issuer=MoSeguros&algorithm=SHA1&digits=6&period=30},
     * para o QR code da aplicação de autenticação.
     */
    public String otpauthUri(String email, String secret) {
        return "otpauth://totp/" + ISSUER + ":" + label(email) + "?secret=" + secret + "&issuer=" + ISSUER
                + "&algorithm=SHA1&digits=" + DIGITS + "&period=" + PERIOD_SECONDS;
    }

    /** Passo actual ({@code segundos desde 1970 / 30}). */
    public long currentStep() {
        return Math.floorDiv(clock.instant().getEpochSecond(), PERIOD_SECONDS);
    }

    /**
     * Verifica o código nos passos −1, 0 e +1.
     *
     * @param lastUsedStep último passo aceite para esta conta (os anteriores ou iguais são recusados),
     *                     ou {@code null}
     * @return o passo do código, se for válido
     */
    public OptionalLong verify(String secret, String code, Long lastUsedStep) {
        if (secret == null || code == null || !CODE.matcher(code).matches()) {
            return OptionalLong.empty();
        }
        long current = currentStep();
        for (long step = current - ALLOWED_DRIFT; step <= current + ALLOWED_DRIFT; step++) {
            if (lastUsedStep != null && step <= lastUsedStep) {
                continue;
            }
            if (MessageDigest.isEqual(generate(secret, step).getBytes(StandardCharsets.US_ASCII),
                    code.getBytes(StandardCharsets.US_ASCII))) {
                return OptionalLong.of(step);
            }
        }
        return OptionalLong.empty();
    }

    /** Código do passo indicado (para a verificação e para os testes). */
    public String generate(String secret, long step) {
        try {
            return generator.generate(secret, step);
        } catch (CodeGenerationException e) {
            throw new IllegalStateException("Could not generate the TOTP code", e);
        }
    }

    /** O e-mail no rótulo, com o {@code @} legível e o resto codificado. */
    private static String label(String email) {
        return URLEncoder.encode(email, StandardCharsets.UTF_8).replace("+", "%20").replace("%40", "@");
    }
}
