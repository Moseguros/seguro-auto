package mz.co.moseguros.identity.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Definições do OTP do cliente ({@code identity.otp_settings}); uma única linha, criada pela
 * migração e alterada pela plataforma (S1-03).
 */
@Entity
@Table(schema = "identity", name = "otp_settings")
public class OtpSettings extends BaseEntity {

    @Column(name = "singleton", nullable = false, updatable = false)
    private boolean singleton = true;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "enabled_channels", nullable = false, columnDefinition = "text[]")
    private List<String> enabledChannels;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_channel", nullable = false, length = 10)
    private Channel defaultChannel;

    @Column(name = "customer_can_choose", nullable = false)
    private boolean customerCanChoose;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "fallback_order", nullable = false, columnDefinition = "text[]")
    private List<String> fallbackOrder;

    @Column(name = "code_length", nullable = false)
    private int codeLength;

    @Column(name = "validity_seconds", nullable = false)
    private int validitySeconds;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Column(name = "resend_after_seconds", nullable = false)
    private int resendAfterSeconds;

    @Column(name = "max_requests_per_hour", nullable = false)
    private int maxRequestsPerHour;

    protected OtpSettings() {
    }

    /**
     * Substitui as definições, validando as regras da tabela (erros de campo, 400
     * {@code VALIDATION_ERROR}, {@link FieldValidationException}):
     * canais vazios ({@code CHANNELS_EMPTY}) ou repetidos ({@code CHANNELS_DUPLICATED}), canal por
     * defeito fora dos activos ({@code DEFAULT_CHANNEL_NOT_ENABLED}), ordem alternativa diferente
     * dos canais activos ({@code FALLBACK_ORDER_MISMATCH}) e números fora dos limites
     * ({@code OUT_OF_RANGE}).
     */
    public void update(List<Channel> enabledChannels, Channel defaultChannel, boolean customerCanChoose,
                       List<Channel> fallbackOrder, int codeLength, int validitySeconds, int maxAttempts,
                       int resendAfterSeconds, int maxRequestsPerHour) {
        if (enabledChannels == null || enabledChannels.isEmpty()) {
            throw invalid("enabledChannels", FieldErrorCodes.CHANNELS_EMPTY, "enabledChannels must not be empty");
        }
        Set<Channel> enabled = new HashSet<>(enabledChannels);
        if (enabled.contains(null)) {
            throw invalid("enabledChannels", FieldErrorCodes.CHANNELS_EMPTY, "enabledChannels must not contain null");
        }
        if (enabled.size() != enabledChannels.size()) {
            throw invalid("enabledChannels", FieldErrorCodes.CHANNELS_DUPLICATED, "enabledChannels must not repeat channels");
        }
        if (defaultChannel == null || !enabled.contains(defaultChannel)) {
            throw invalid("defaultChannel", FieldErrorCodes.DEFAULT_CHANNEL_NOT_ENABLED, "defaultChannel must be one of enabledChannels");
        }
        if (fallbackOrder == null || fallbackOrder.size() != enabledChannels.size()
                || !new HashSet<>(fallbackOrder).equals(enabled)) {
            throw invalid("fallbackOrder", FieldErrorCodes.FALLBACK_ORDER_MISMATCH,
                    "fallbackOrder must list exactly the enabled channels, once each");
        }
        checkRange("codeLength", codeLength, 4, 8);
        checkRange("validitySeconds", validitySeconds, 60, 1800);
        checkRange("maxAttempts", maxAttempts, 1, 10);
        checkRange("resendAfterSeconds", resendAfterSeconds, 15, 600);
        checkRange("maxRequestsPerHour", maxRequestsPerHour, 1, 50);

        this.enabledChannels = names(enabledChannels);
        this.defaultChannel = defaultChannel;
        this.customerCanChoose = customerCanChoose;
        this.fallbackOrder = names(fallbackOrder);
        this.codeLength = codeLength;
        this.validitySeconds = validitySeconds;
        this.maxAttempts = maxAttempts;
        this.resendAfterSeconds = resendAfterSeconds;
        this.maxRequestsPerHour = maxRequestsPerHour;
    }

    public boolean isEnabled(Channel channel) {
        return enabledChannels.contains(channel.name());
    }

    /**
     * Canais activos compatíveis com o tipo de identificador, pela ordem de {@code fallbackOrder}
     * (vazio se nenhum).
     */
    public List<Channel> channelsFor(OtpChallenge.IdentifierType type) {
        return getFallbackOrder().stream()
                .filter(this::isEnabled)
                .filter(channel -> OtpIdentifier.compatible(type, channel))
                .toList();
    }

    /**
     * Canal por defeito para o tipo: {@code defaultChannel} se for compatível; senão o primeiro de
     * {@link #channelsFor}; vazio se o tipo não tiver canais.
     */
    public Optional<Channel> defaultChannelFor(OtpChallenge.IdentifierType type) {
        List<Channel> channels = channelsFor(type);
        if (channels.contains(defaultChannel)) {
            return Optional.of(defaultChannel);
        }
        return channels.stream().findFirst();
    }

    public List<Channel> getEnabledChannels() {
        return channels(enabledChannels);
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public boolean isCustomerCanChoose() {
        return customerCanChoose;
    }

    public List<Channel> getFallbackOrder() {
        return channels(fallbackOrder);
    }

    public int getCodeLength() {
        return codeLength;
    }

    public int getValiditySeconds() {
        return validitySeconds;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getResendAfterSeconds() {
        return resendAfterSeconds;
    }

    public int getMaxRequestsPerHour() {
        return maxRequestsPerHour;
    }

    private static void checkRange(String field, int value, int min, int max) {
        if (value < min || value > max) {
            throw invalid(field, FieldErrorCodes.OUT_OF_RANGE, field + " must be between " + min + " and " + max);
        }
    }

    private static FieldValidationException invalid(String field, String code, String detail) {
        return new FieldValidationException(field, code, detail);
    }

    private static List<String> names(List<Channel> channels) {
        return channels.stream().map(Channel::name).toList();
    }

    private static List<Channel> channels(List<String> names) {
        return Objects.requireNonNull(names).stream().map(Channel::valueOf).toList();
    }
}
