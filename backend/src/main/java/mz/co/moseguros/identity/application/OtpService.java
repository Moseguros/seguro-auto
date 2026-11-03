package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.OtpChallenge;
import mz.co.moseguros.identity.domain.OtpChallenge.DeliveryStatus;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpChallenge.Purpose;
import mz.co.moseguros.identity.domain.OtpChallengeRepository;
import mz.co.moseguros.identity.domain.OtpIdentifier;
import mz.co.moseguros.identity.domain.OtpSettings;
import mz.co.moseguros.identity.domain.OtpSettingsRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.identity.domain.UserRoleRepository;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * OTP do cliente (RF-01, ADR-003): opções, pedido de código com alternativa automática de canal,
 * consulta do desafio, validação com login (e registo) e envio de teste pelo administrador.
 *
 * <p>Todos os métodos públicos correm em {@code SYSTEM} ({@code otp_challenge} só é visível
 * nesse âmbito) e são chamados a partir de outros beans. As definições lêem-se da BD em cada
 * pedido (sem cache). O envio fica fora de transacção: o desafio é gravado antes e o resultado
 * depois.
 *
 * <p>Os logs têm o {@code challengeId}, o canal, o estado e o identificador mascarado; nunca o
 * código, o hash nem o identificador completo.
 */
@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    /** Um recibo de falha só gera alternativa se o código foi enviado há no máximo 30 s. */
    static final Duration RECEIPT_FALLBACK_WINDOW = Duration.ofSeconds(30);
    static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(1);
    /** Finalidades que contam para os limites de pedidos. */
    static final Set<Purpose> RATE_LIMITED_PURPOSES = EnumSet.of(Purpose.LOGIN, Purpose.TEST, Purpose.VERIFY_CONTACT);
    static final String CUSTOMER_ROLE = "CUSTOMER";
    static final String DEFAULT_LANGUAGE = UserAccount.DEFAULT_LANGUAGE;

    private final OtpSettingsRepository settingsRepository;
    private final OtpChallengeRepository challenges;
    private final UserAccountRepository accounts;
    private final CustomerProfileRepository profiles;
    private final UserRoleRepository userRoles;
    private final TokenService tokenService;
    private final AuditService audit;
    private final NotificationApi notifications;
    private final OtpCodeHasher hasher;
    private final OtpProperties properties;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final CustomerAccountFactory accountFactory;

    public OtpService(OtpSettingsRepository settingsRepository, OtpChallengeRepository challenges,
                      UserAccountRepository accounts, CustomerProfileRepository profiles,
                      UserRoleRepository userRoles, TokenService tokenService, AuditService audit,
                      NotificationApi notifications, OtpCodeHasher hasher, OtpProperties properties,
                      TransactionTemplate tx, Clock clock) {
        this(settingsRepository, challenges, accounts, profiles, userRoles, tokenService, audit, notifications, hasher,
                properties, tx, clock, new CustomerAccountFactory(accounts, profiles, userRoles, audit));
    }

    /** As contas novas criam-se pela {@link CustomerAccountFactory} (a mesma do S3-02 §6.1). */
    @Autowired
    public OtpService(OtpSettingsRepository settingsRepository, OtpChallengeRepository challenges,
                      UserAccountRepository accounts, CustomerProfileRepository profiles,
                      UserRoleRepository userRoles, TokenService tokenService, AuditService audit,
                      NotificationApi notifications, OtpCodeHasher hasher, OtpProperties properties,
                      TransactionTemplate tx, Clock clock, CustomerAccountFactory accountFactory) {
        this.accountFactory = accountFactory;
        this.settingsRepository = settingsRepository;
        this.challenges = challenges;
        this.accounts = accounts;
        this.profiles = profiles;
        this.userRoles = userRoles;
        this.tokenService = tokenService;
        this.audit = audit;
        this.notifications = notifications;
        this.hasher = hasher;
        this.properties = properties;
        this.tx = tx;
        this.clock = clock;
    }

    /** Tipos de identificador e canais disponíveis para o cliente, conforme as definições actuais. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Options options() {
        OtpSettings settings = settings();
        Map<IdentifierType, List<Channel>> channels = new LinkedHashMap<>();
        Map<IdentifierType, Channel> defaults = new LinkedHashMap<>();
        for (IdentifierType type : IdentifierType.values()) {
            List<Channel> available = settings.channelsFor(type);
            if (!available.isEmpty()) {
                channels.put(type, available);
                defaults.put(type, settings.defaultChannelFor(type).orElseThrow());
            }
        }
        return new Options(List.copyOf(channels.keySet()), channels, defaults, settings.isCustomerCanChoose(),
                settings.getCodeLength(), settings.getResendAfterSeconds());
    }

    /**
     * Pede um código de login: escolhe o canal, aplica os limites, substitui os códigos anteriores,
     * envia e, se o envio falhar, tenta os outros canais compatíveis pela ordem de
     * {@code fallbackOrder}. A resposta é igual exista ou não a conta.
     *
     * @param channel canal escolhido pelo cliente, ou {@code null} para o canal por defeito
     * @throws DomainException {@code VALIDATION_ERROR}, {@code OTP_CHANNEL_CHOICE_DISABLED},
     *                         {@code OTP_CHANNEL_NOT_AVAILABLE}, {@code OTP_RATE_LIMITED},
     *                         {@code OTP_RESEND_TOO_SOON} ou {@code OTP_DELIVERY_FAILED}
     */
    @RunAsSystem
    public RequestResult request(String rawIdentifier, IdentifierType type, Channel channel) {
        OtpIdentifier identifier = OtpIdentifier.parse(rawIdentifier, type, "identifier");
        return send(identifier, channel, Purpose.LOGIN, null);
    }

    /**
     * Pede um código para confirmar um contacto novo do cliente {@code userId} (S1-06): desafio
     * {@code VERIFY_CONTACT} com o {@code user_id} do cliente, com as mesmas regras de canal,
     * alternativa e limites do login. As verificações de posse do contacto são de quem chama.
     *
     * @param identifier contacto já normalizado
     * @param channel    canal escolhido pelo cliente, ou {@code null} para o canal por defeito
     * @throws DomainException como {@link #request}
     */
    @RunAsSystem
    public RequestResult requestForContact(OtpIdentifier identifier, UUID userId, Channel channel) {
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(userId, "userId");
        return send(identifier, channel, Purpose.VERIFY_CONTACT, userId);
    }

    /**
     * Escolhe o canal, aplica os limites, substitui os códigos anteriores da mesma finalidade,
     * grava o desafio e envia (com alternativa de canal).
     *
     * @param owner conta que pede ({@code VERIFY_CONTACT}); no login a conta vem do identificador
     */
    private RequestResult send(OtpIdentifier identifier, Channel channel, Purpose purpose, UUID owner) {
        OtpSettings settings = tx.execute(status -> settings());
        Channel chosen = chooseChannel(settings, identifier.type(), channel);

        Start start = tx.execute(status -> {
            Instant now = clock.instant();
            checkLimits(identifier, settings, now, true);
            supersedeActive(identifier, purpose, now);
            UUID userId;
            String language;
            if (purpose == Purpose.LOGIN) {
                Optional<UserAccount> account = findCustomer(identifier);
                userId = account.map(UserAccount::getId).orElse(null);
                language = account.map(UserAccount::getPreferredLanguage).orElse(DEFAULT_LANGUAGE);
            } else {
                userId = owner;
                language = languageOf(owner);
            }
            Issued issued = issue(identifier, chosen, purpose, userId, settings, now, null);
            return new Start(issued, language);
        });
        UUID rootId = start.issued().challenge().getId();
        log.info("OTP challenge {} ({}) requested for {} via {}", rootId, purpose, identifier, chosen);

        Delivery delivery = deliver(identifier, start.issued(), settings, start.language(), true);
        if (delivery.failed()) {
            log.warn("OTP challenge {} for {}: no channel delivered the code", rootId, identifier);
            throw DomainException.of(ErrorCodes.OTP_DELIVERY_FAILED, "The code could not be delivered on any channel");
        }
        OtpChallenge effective = delivery.issued().challenge();
        return new RequestResult(rootId, effective.getChannel(), settings.getResendAfterSeconds(),
                effective.getExpiresAt(), effective.getChannel() != chosen,
                properties.debugHeader() ? delivery.issued().code() : null);
    }

    /**
     * Estado do desafio activo da cadeia (o último canal tentado), para o ecrã saber se o canal
     * mudou. Não devolve o identificador.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public ChallengeView challenge(UUID challengeId) {
        UUID rootId = challenges.findRootId(challengeId).orElseThrow(OtpService::challengeNotFound);
        OtpChallenge active = tail(challenges.findByRootChallengeId(rootId));
        return new ChallengeView(active.getChannel(), active.getDeliveryStatus(), active.isFallback(),
                active.getExpiresAt());
    }

    /**
     * Valida o código do desafio activo da cadeia e abre uma sessão; cria a conta de cliente se
     * ainda não existir. As tentativas erradas ficam gravadas mesmo com a resposta de erro.
     *
     * @param deviceInfo o {@code User-Agent} do pedido
     */
    @RunAsSystem
    @Transactional(noRollbackFor = DomainException.class)
    public LoginResult verify(UUID challengeId, String code, String deviceInfo) {
        Instant now = clock.instant();
        UUID rootId = challenges.findRootId(challengeId).orElseThrow(OtpService::challengeNotFound);
        OtpChallenge challenge = tail(challenges.findChainForUpdate(rootId));
        if (challenge.getPurpose() != Purpose.LOGIN) {
            throw challengeNotFound();
        }
        consumeIfValid(challenge, code, now);

        OtpIdentifier identifier = new OtpIdentifier(challenge.getIdentifierType(), challenge.getIdentifier());
        Optional<UserAccount> existing = findCustomer(identifier);
        boolean newUser = existing.isEmpty();
        UserAccount account;
        if (newUser) {
            account = register(identifier, challenge.getChannel());
        } else {
            account = existing.get();
            if (account.isDisabled()) {
                log.info("OTP challenge {}: login refused, account {} is disabled", challenge.getId(), account.getId());
                throw DomainException.of(ErrorCodes.ACCOUNT_DISABLED, "The account is disabled");
            }
            markVerified(account, identifier);
        }
        account.recordLogin(now);
        TokenPair tokens = tokenService.issue(account, deviceInfo);
        log.info("OTP challenge {}: customer {} signed in (new account: {})", challenge.getId(), account.getId(),
                newUser);
        return new LoginResult(tokens, newUser);
    }

    /**
     * Valida o código de confirmação de um contacto (S1-06), com as mesmas regras do login. O
     * desafio tem de ser {@code VERIFY_CONTACT} e do cliente {@code userId}; senão
     * {@code OTP_CHALLENGE_NOT_FOUND}. As tentativas erradas ficam gravadas mesmo com a resposta de
     * erro. Não altera a conta: devolve o contacto confirmado.
     */
    @RunAsSystem
    @Transactional(noRollbackFor = DomainException.class)
    public OtpIdentifier verifyContact(UUID challengeId, String code, UUID userId) {
        Instant now = clock.instant();
        UUID rootId = challenges.findRootId(challengeId).orElseThrow(OtpService::challengeNotFound);
        OtpChallenge challenge = tail(challenges.findChainForUpdate(rootId));
        if (challenge.getPurpose() != Purpose.VERIFY_CONTACT || !Objects.equals(challenge.getUserId(), userId)) {
            throw challengeNotFound();
        }
        consumeIfValid(challenge, code, now);
        log.info("OTP challenge {}: contact confirmed for customer {}", challenge.getId(), userId);
        return new OtpIdentifier(challenge.getIdentifierType(), challenge.getIdentifier());
    }

    /**
     * Envio de teste do administrador: só pelo canal pedido (sem alternativa), mesmo que esteja
     * desactivado; o desafio ({@code TEST}) não serve para login. Conta para o limite por hora.
     * A permissão é verificada antes, pelo {@code OtpSettingsService}.
     *
     * @throws DomainException {@code OTP_CHANNEL_NOT_AVAILABLE} se o canal não servir o identificador
     */
    @RunAsSystem
    public TestResult sendTest(String rawIdentifier, IdentifierType type, Channel channel) {
        OtpIdentifier identifier = OtpIdentifier.parse(rawIdentifier, type, "identifier");
        if (channel == null || !identifier.accepts(channel)) {
            throw channelNotAvailable();
        }
        OtpSettings settings = tx.execute(status -> settings());
        Issued issued = tx.execute(status -> {
            Instant now = clock.instant();
            checkLimits(identifier, settings, now, false);
            supersedeActive(identifier, Purpose.TEST, now);
            return issue(identifier, channel, Purpose.TEST, null, settings, now, null);
        });
        log.info("OTP test challenge {} for {} via {}", issued.challenge().getId(), identifier, channel);
        Delivery delivery = deliver(identifier, issued, settings, DEFAULT_LANGUAGE, false);
        return new TestResult(channel, delivery.issued().challenge().getDeliveryStatus(), delivery.messageId());
    }

    /**
     * Recibo de falha da entrega do código do desafio indicado: se ele ainda está activo e foi
     * enviado há no máximo 30 s, tenta o canal seguinte (como no pedido); senão só regista a falha.
     */
    @RunAsSystem
    public void fallbackAfterDeliveryFailure(UUID challengeId) {
        if (challengeId == null) {
            return;
        }
        Fallback fallback = tx.execute(status -> {
            Instant now = clock.instant();
            OtpChallenge failed = challenges.findById(challengeId).orElse(null);
            if (failed == null) {
                return null;
            }
            failed.markFailed();
            Instant sentAt = failed.getSentAt() != null ? failed.getSentAt() : failed.getCreatedAt();
            if (!failed.isActive(now) || failed.getPurpose() == Purpose.TEST
                    || sentAt.plus(RECEIPT_FALLBACK_WINDOW).isBefore(now)) {
                log.info("OTP challenge {}: delivery failed on {} (receipt); no fallback (inactive, test or late)",
                        failed.getId(), failed.getChannel());
                return null;
            }
            OtpSettings settings = settings();
            OtpIdentifier identifier = new OtpIdentifier(failed.getIdentifierType(), failed.getIdentifier());
            Optional<Channel> next = nextChannel(settings, failed);
            if (next.isEmpty()) {
                log.warn("OTP challenge {} for {}: delivery failed on {} (receipt) and no other channel is available",
                        failed.getId(), identifier, failed.getChannel());
                return null;
            }
            failed.supersede(now);
            Issued issued = issue(identifier, next.get(), failed.getPurpose(), failed.getUserId(), settings, now,
                    failed);
            log.info("OTP challenge {}: delivery failed on {} (receipt), falling back to {} (challenge {})",
                    failed.getId(), failed.getChannel(), next.get(), issued.challenge().getId());
            return new Fallback(identifier, issued, settings, languageOf(failed.getUserId()));
        });
        if (fallback != null) {
            deliver(fallback.identifier(), fallback.issued(), fallback.settings(), fallback.language(), true);
        }
    }

    // --- passos internos (já em SYSTEM) ---

    /**
     * Valida o código do desafio (consumido, substituído, expirado, tentativas, código errado) e
     * consome-o. Um código errado conta uma tentativa.
     */
    private void consumeIfValid(OtpChallenge challenge, String code, Instant now) {
        OtpSettings settings = settings();
        if (challenge.getConsumedAt() != null) {
            throw DomainException.of(ErrorCodes.OTP_ALREADY_USED, "The code was already used");
        }
        if (challenge.getSupersededAt() != null) {
            throw DomainException.of(ErrorCodes.OTP_SUPERSEDED, "A newer code was requested");
        }
        if (challenge.isExpired(now)) {
            throw DomainException.of(ErrorCodes.OTP_EXPIRED, "The code has expired");
        }
        if (challenge.getAttempts() >= settings.getMaxAttempts()) {
            throw attemptsExceeded();
        }
        if (!hasher.matches(challenge.getId(), code, challenge.getCodeHash())) {
            challenge.registerFailedAttempt();
            int remaining = settings.getMaxAttempts() - challenge.getAttempts();
            log.info("OTP challenge {}: wrong code ({} attempts remaining)", challenge.getId(), Math.max(remaining, 0));
            if (remaining <= 0) {
                throw attemptsExceeded();
            }
            throw DomainException.of(ErrorCodes.OTP_INVALID, "Wrong code").withProperty("attemptsRemaining", remaining);
        }
        challenge.consume(now);
    }

    private Channel chooseChannel(OtpSettings settings, IdentifierType type, Channel requested) {
        if (requested != null) {
            if (!settings.isCustomerCanChoose()) {
                throw DomainException.of(ErrorCodes.OTP_CHANNEL_CHOICE_DISABLED, "Choosing the channel is disabled");
            }
            if (!settings.channelsFor(type).contains(requested)) {
                throw channelNotAvailable();
            }
            return requested;
        }
        return settings.defaultChannelFor(type).orElseThrow(OtpService::channelNotAvailable);
    }

    /** Limite por hora e, se {@code checkResend}, tempo mínimo entre pedidos (429 com {@code Retry-After}). */
    private void checkLimits(OtpIdentifier identifier, OtpSettings settings, Instant now, boolean checkResend) {
        List<Instant> recent = challenges.findRequestTimesSince(identifier.value(), RATE_LIMITED_PURPOSES,
                now.minus(RATE_LIMIT_WINDOW));
        if (recent.size() >= settings.getMaxRequestsPerHour()) {
            long retryAfter = secondsUntil(recent.get(0).plus(RATE_LIMIT_WINDOW), now);
            log.info("OTP request for {} refused: hourly limit reached", identifier);
            throw DomainException.of(ErrorCodes.OTP_RATE_LIMITED, "Too many codes requested in the last hour")
                    .withProperty(DomainException.RETRY_AFTER_SECONDS, retryAfter);
        }
        if (checkResend) {
            Instant last = challenges.findLastRequestTime(identifier.value(), RATE_LIMITED_PURPOSES);
            Instant allowedAt = last == null ? null : last.plusSeconds(settings.getResendAfterSeconds());
            if (allowedAt != null && allowedAt.isAfter(now)) {
                throw DomainException.of(ErrorCodes.OTP_RESEND_TOO_SOON, "A new code can only be requested later")
                        .withProperty(DomainException.RETRY_AFTER_SECONDS, secondsUntil(allowedAt, now));
            }
        }
    }

    /** Só o último código é válido: os anteriores não consumidos ficam substituídos. */
    private void supersedeActive(OtpIdentifier identifier, Purpose purpose, Instant now) {
        challenges.findByIdentifierAndPurposeAndConsumedAtIsNullAndSupersededAtIsNull(identifier.value(), purpose)
                .forEach(previous -> previous.supersede(now));
    }

    /** Grava um desafio novo com um código novo ({@code from} = desafio que este substitui, ou {@code null}). */
    private Issued issue(OtpIdentifier identifier, Channel channel, Purpose purpose, UUID userId, OtpSettings settings,
                         Instant now, OtpChallenge from) {
        String code = hasher.generate(settings.getCodeLength());
        OtpChallenge challenge = OtpChallenge.issue(identifier.value(), identifier.type(), channel, purpose, userId,
                id -> hasher.hash(id, code), now.plusSeconds(settings.getValiditySeconds()), from);
        return new Issued(challenges.save(challenge), code);
    }

    /**
     * Envia o código e grava o resultado. Se falhar e {@code fallback}, marca o desafio como
     * {@code FAILED} e substituído e tenta o canal seguinte com um código novo, até um envio não
     * falhar ou acabarem os canais compatíveis.
     */
    private Delivery deliver(OtpIdentifier identifier, Issued first, OtpSettings settings, String language,
                             boolean fallback) {
        Issued current = first;
        while (true) {
            OtpChallenge challenge = current.challenge();
            DeliveryResult result = notifications.sendOtp(challenge.getChannel(), identifier.destination(),
                    current.code(), validityMinutes(settings), language, challenge.getId());
            Instant now = clock.instant();
            if (!result.failed()) {
                OtpChallenge sent = tx.execute(status -> {
                    OtpChallenge reloaded = challenges.findById(challenge.getId()).orElseThrow();
                    reloaded.markSent(now);
                    return reloaded;
                });
                log.info("OTP challenge {} sent via {}", challenge.getId(), challenge.getChannel());
                return new Delivery(new Issued(sent, current.code()), result.messageId(), false);
            }
            log.warn("OTP challenge {}: delivery via {} failed ({})", challenge.getId(), challenge.getChannel(),
                    result.failureReason());
            Issued next = tx.execute(status -> {
                OtpChallenge failed = challenges.findById(challenge.getId()).orElseThrow();
                failed.markFailed();
                if (!fallback) {
                    return new Issued(failed, null);
                }
                Optional<Channel> nextChannel = nextChannel(settings, failed);
                if (nextChannel.isEmpty()) {
                    return new Issued(failed, null);
                }
                failed.supersede(now);
                Issued issued = issue(identifier, nextChannel.get(), failed.getPurpose(), failed.getUserId(), settings,
                        now, failed);
                log.info("OTP challenge {}: falling back from {} to {} (challenge {})", failed.getId(),
                        failed.getChannel(), nextChannel.get(), issued.challenge().getId());
                return issued;
            });
            if (next.code() == null) {
                return new Delivery(next, result.messageId(), true);
            }
            current = next;
        }
    }

    /** Primeiro canal activo e compatível, pela ordem de {@code fallbackOrder}, ainda não tentado na cadeia. */
    private Optional<Channel> nextChannel(OtpSettings settings, OtpChallenge failed) {
        Set<Channel> tried = challenges.findByRootChallengeId(failed.getRootChallengeId()).stream()
                .map(OtpChallenge::getChannel)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Channel.class)));
        tried.add(failed.getChannel());
        return settings.channelsFor(failed.getIdentifierType()).stream()
                .filter(channel -> !tried.contains(channel))
                .findFirst();
    }

    private UserAccount register(OtpIdentifier identifier, Channel channel) {
        return accountFactory.createVerified(identifier, channel);
    }

    private static void markVerified(UserAccount account, OtpIdentifier identifier) {
        if (identifier.type() == IdentifierType.MSISDN) {
            account.markMsisdnVerified();
        } else {
            account.markEmailVerified();
        }
    }

    private Optional<UserAccount> findCustomer(OtpIdentifier identifier) {
        return identifier.type() == IdentifierType.MSISDN
                ? accounts.findByTypeAndMsisdn(AccountType.CUSTOMER, identifier.msisdn())
                : accounts.findByTypeAndEmail(AccountType.CUSTOMER, identifier.email());
    }

    private String languageOf(UUID userId) {
        return userId == null ? DEFAULT_LANGUAGE
                : accounts.findById(userId).map(UserAccount::getPreferredLanguage).orElse(DEFAULT_LANGUAGE);
    }

    private OtpSettings settings() {
        return settingsRepository.findFirstBy()
                .orElseThrow(() -> new IllegalStateException("identity.otp_settings has no row"));
    }

    /** Desafio activo de uma cadeia: o que nenhum outro substituiu como alternativa de canal. */
    static OtpChallenge tail(List<OtpChallenge> chain) {
        Set<UUID> replaced = chain.stream()
                .map(OtpChallenge::getFallbackFromChallengeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return chain.stream()
                .filter(challenge -> !replaced.contains(challenge.getId()))
                .max(Comparator.comparing(OtpChallenge::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow(OtpService::challengeNotFound);
    }

    static int validityMinutes(OtpSettings settings) {
        return (int) Math.ceil(settings.getValiditySeconds() / 60.0);
    }

    static long secondsUntil(Instant at, Instant now) {
        long millis = Duration.between(now, at).toMillis();
        return Math.max(1, (millis + 999) / 1000);
    }

    private static DomainException challengeNotFound() {
        return DomainException.of(ErrorCodes.OTP_CHALLENGE_NOT_FOUND, "Unknown OTP challenge");
    }

    private static DomainException channelNotAvailable() {
        return DomainException.of(ErrorCodes.OTP_CHANNEL_NOT_AVAILABLE,
                "The channel is not available for this identifier");
    }

    private static DomainException attemptsExceeded() {
        return DomainException.of(ErrorCodes.OTP_ATTEMPTS_EXCEEDED, "Too many wrong attempts");
    }

    // --- resultados ---

    /** Opções do ecrã de login. */
    public record Options(List<IdentifierType> identifierTypes, Map<IdentifierType, List<Channel>> channels,
                          Map<IdentifierType, Channel> defaultChannels, boolean customerCanChoose, int codeLength,
                          int resendAfterSeconds) {
    }

    /**
     * Resultado de um pedido.
     *
     * @param challengeId desafio raiz (o que o cliente usa para validar e consultar)
     * @param channel     canal efectivamente usado
     * @param debugCode   o código, só com {@code moseguros.security.otp.debug-header} (perfil local)
     */
    public record RequestResult(UUID challengeId, Channel channel, int resendAfterSeconds, Instant expiresAt,
                                boolean fallbackUsed, String debugCode) {

        @Override
        public String toString() {
            return "RequestResult[challengeId=" + challengeId + ", channel=" + channel + ", fallbackUsed="
                    + fallbackUsed + ", debugCode=***]";
        }
    }

    /** Estado do desafio activo de uma cadeia. */
    public record ChallengeView(Channel channel, DeliveryStatus deliveryStatus, boolean fallbackUsed,
                                Instant expiresAt) {
    }

    /** Sessão aberta por um código válido. */
    public record LoginResult(TokenPair tokens, boolean newUser) {
    }

    /** Resultado de um envio de teste. */
    public record TestResult(Channel channel, DeliveryStatus deliveryStatus, UUID messageId) {
    }

    /** Desafio acabado de gravar e o seu código (nunca registado). */
    record Issued(OtpChallenge challenge, String code) {

        @Override
        public String toString() {
            return "Issued[challenge=" + challenge.getId() + ", code=***]";
        }
    }

    private record Start(Issued issued, String language) {
    }

    private record Fallback(OtpIdentifier identifier, Issued issued, OtpSettings settings, String language) {
    }

    /** Resultado do envio: o último desafio tentado e se todos falharam. */
    private record Delivery(Issued issued, UUID messageId, boolean failed) {
    }
}
