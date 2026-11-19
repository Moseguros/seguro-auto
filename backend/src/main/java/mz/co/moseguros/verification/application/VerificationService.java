package mz.co.moseguros.verification.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import mz.co.moseguros.policy.PolicyCoverView;
import mz.co.moseguros.shared.captcha.CaptchaMetrics;
import mz.co.moseguros.shared.captcha.CaptchaProvider;
import mz.co.moseguros.shared.captcha.CaptchaResult;
import mz.co.moseguros.shared.captcha.CaptchaVerifier;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.verification.domain.CheckLog;
import mz.co.moseguros.verification.domain.CheckLogRepository;
import mz.co.moseguros.verification.domain.CheckerType;
import mz.co.moseguros.verification.domain.SubjectType;
import mz.co.moseguros.verification.domain.VerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verificação de seguro por código do certificado ou por matrícula (S6-03 §2 a §5), pública ou por um
 * agente da polícia ({@code verification:police}). O resultado calcula-se em cada pedido com o instante
 * actual; cada consulta fica em {@code verification.check_log} (numa transacção própria, sem a matrícula
 * nem o código em claro) e conta para {@code moseguros_verification_checks_total}.
 *
 * <p>Ordem de um pedido: limite de pedidos → CAPTCHA (só na matrícula pública) → validação da matrícula →
 * consulta. Um pedido recusado pelo limite, pelo CAPTCHA ou por uma matrícula inválida não grava registo.
 * Os logs levam só a matrícula mascarada, o tipo, o resultado e o hash truncado.
 *
 * <p>Não tem {@code @RunAsSystem}: as consultas à apólice e ao nome da seguradora correm em {@code SYSTEM}
 * dentro dos módulos donos, e o RLS do {@code check_log} aceita inserções em qualquer âmbito.
 */
@Service
public class VerificationService {

    /** Formato dos códigos do QR (S6-01 regra 6): 22 caracteres Base64url (128 bits). */
    static final Pattern TOKEN_FORMAT = Pattern.compile("^[A-Za-z0-9_-]{22}$");
    static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    static final int DEFAULT_HISTORY_DAYS = 30;
    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);

    private final PolicyLookupCache lookups;
    private final CheckLogRepository checkLogs;
    private final SubjectHasher hasher;
    private final VerificationRateLimiter limiter;
    private final VerificationMetrics metrics;
    private final CaptchaVerifier captcha;
    private final CaptchaMetrics captchaMetrics;
    private final PlateParser plates;
    private final CurrentUser currentUser;
    private final Clock clock;

    public VerificationService(PolicyLookupCache lookups, CheckLogRepository checkLogs, SubjectHasher hasher,
                               VerificationRateLimiter limiter, VerificationMetrics metrics, CaptchaVerifier captcha,
                               CaptchaMetrics captchaMetrics, PlateParser plates, CurrentUser currentUser,
                               Clock clock) {
        this.lookups = lookups;
        this.checkLogs = checkLogs;
        this.hasher = hasher;
        this.limiter = limiter;
        this.metrics = metrics;
        this.captcha = captcha;
        this.captchaMetrics = captchaMetrics;
        this.plates = plates;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    /** Resultado de uma consulta, para os controladores. */
    public record Outcome(VerificationResult status, SubjectType subjectType, String plateMasked,
                          PolicyCoverView cover, String insurerName, Instant checkedAt) {

        /** A apólice encontrada; vazio em {@code NOT_FOUND}. */
        public Optional<PolicyCoverView> policy() {
            return Optional.ofNullable(cover);
        }
    }

    /** Fornecedor de CAPTCHA activo e a chave do site (vazia com {@code NONE}). */
    public record CaptchaInfo(CaptchaProvider provider, String siteKey) {
    }

    /** Quem consulta. */
    record Checker(CheckerType type, UUID userId, UUID orgId, String clientIp) {
    }

    public CaptchaInfo captchaInfo() {
        return new CaptchaInfo(captcha.provider(), captcha.siteKey().orElse(null));
    }

    // --- Público ---

    /** Consulta pública pelo código do QR (sem CAPTCHA). @throws DomainException 429 {@code RATE_LIMITED} */
    public Outcome verifyTokenPublic(String token, String clientIp) {
        limiter.consumePublic(clientIp);
        return byToken(token, new Checker(CheckerType.PUBLIC, null, null, clientIp));
    }

    /**
     * Consulta pública pela matrícula, com CAPTCHA (quando o fornecedor não é {@code NONE}).
     *
     * @throws DomainException 429 {@code RATE_LIMITED}; 400 {@code CAPTCHA_REQUIRED}/{@code CAPTCHA_INVALID};
     *                         503 {@code CAPTCHA_UNAVAILABLE}; 400 {@code VALIDATION_ERROR} ({@code plate},
     *                         {@code INVALID_PLATE})
     */
    public Outcome verifyPlatePublic(String plate, String captchaToken, String clientIp) {
        limiter.consumePublic(clientIp);
        requireCaptcha(captchaToken, clientIp);
        return byPlate(parse(plate), new Checker(CheckerType.PUBLIC, null, null, clientIp));
    }

    // --- Polícia ---

    @PreAuthorize("hasAuthority('verification:police')")
    public Outcome verifyTokenAsPolice(String token, String clientIp) {
        Checker checker = police(clientIp);
        limiter.consumePolice(checker.userId());
        return byToken(token, checker);
    }

    @PreAuthorize("hasAuthority('verification:police')")
    public Outcome verifyPlateAsPolice(String plate, String clientIp) {
        Checker checker = police(clientIp);
        limiter.consumePolice(checker.userId());
        return byPlate(parse(plate), checker);
    }

    /**
     * Consultas do próprio agente entre {@code from} e {@code to} (datas de {@code Africa/Maputo},
     * inclusivas; por defeito os últimos 30 dias), as mais recentes primeiro. RLS e filtro pelo utilizador.
     *
     * @throws FieldValidationException {@code from} depois de {@code to} ({@code OUT_OF_RANGE})
     */
    @PreAuthorize("hasAuthority('verification:police')")
    @Transactional(readOnly = true)
    public Page<CheckLog> myChecks(LocalDate from, LocalDate to, Pageable pageable) {
        UUID userId = currentUser.id();
        limiter.consumePolice(userId);
        LocalDate last = to != null ? to : LocalDate.now(clock.withZone(ZONE));
        LocalDate first = from != null ? from : last.minusDays(DEFAULT_HISTORY_DAYS);
        if (first.isAfter(last)) {
            throw new FieldValidationException("from", FieldErrorCodes.OUT_OF_RANGE, "from must not be after to");
        }
        return checkLogs.findByCheckerUserIdAndCheckedAtGreaterThanEqualAndCheckedAtLessThan(userId,
                first.atStartOfDay(ZONE).toInstant(), last.plusDays(1).atStartOfDay(ZONE).toInstant(), pageable);
    }

    // --- Comum ---

    private Checker police(String clientIp) {
        return new Checker(CheckerType.POLICE, currentUser.id(), currentUser.orgId(), clientIp);
    }

    private void requireCaptcha(String token, String clientIp) {
        if (captcha.provider() == CaptchaProvider.NONE) {
            return;
        }
        if (token == null || token.isBlank()) {
            throw DomainException.of(ErrorCodes.CAPTCHA_REQUIRED, "X-Captcha-Token header is required");
        }
        CaptchaResult result = captcha.verify(token, clientIp);
        captchaMetrics.record(result);
        switch (result) {
            case VALID -> {
            }
            case INVALID -> throw DomainException.of(ErrorCodes.CAPTCHA_INVALID, "CAPTCHA token rejected");
            case UNAVAILABLE -> throw DomainException.of(ErrorCodes.CAPTCHA_UNAVAILABLE,
                    "CAPTCHA provider unavailable");
        }
    }

    private Matricula parse(String plate) {
        if (plate == null || plate.isBlank()) {
            throw new FieldValidationException("plate", FieldErrorCodes.INVALID_PLATE, "plate is required");
        }
        try {
            return plates.parse(plate);
        } catch (DomainException e) {
            throw FieldValidationException.of("plate", e);
        }
    }

    private Outcome byToken(String token, Checker checker) {
        String value = token == null ? "" : token;
        Instant now = clock.instant();
        PolicyCoverView cover = TOKEN_FORMAT.matcher(value).matches() ? lookups.byToken(value).orElse(null) : null;
        String masked = cover == null ? null : Matricula.restore(cover.plate(), null).masked();
        return finish(SubjectType.TOKEN, hasher.hash(SubjectType.TOKEN, value), masked, cover, now, checker);
    }

    private Outcome byPlate(Matricula plate, Checker checker) {
        Instant now = clock.instant();
        PolicyCoverView cover = lookups.byPlate(plate.value(), now).orElse(null);
        String masked = cover == null ? plate.masked() : Matricula.restore(cover.plate(), null).masked();
        return finish(SubjectType.PLATE, hasher.hash(SubjectType.PLATE, plate.value()), masked, cover, now, checker);
    }

    private Outcome finish(SubjectType subject, String hash, String masked, PolicyCoverView cover, Instant now,
                           Checker checker) {
        VerificationResult status = VerificationResult.of(cover, now);
        String insurerName = cover == null ? null : lookups.insurerName(cover.insurerId()).orElse(null);
        checkLogs.save(CheckLog.record(subject, hash, masked, status, cover == null ? null : cover.policyId(),
                cover == null ? null : cover.insurerId(), checker.type(), checker.userId(), checker.orgId(),
                IpPrefix.of(checker.clientIp()), now));
        metrics.checked(checker.type(), subject, status);
        log.info("Verification checker={} subjectType={} plateMasked={} result={} subject={}", checker.type(),
                subject, masked, status, SubjectHasher.forLog(hash));
        return new Outcome(status, subject, masked, cover, insurerName, now);
    }
}
