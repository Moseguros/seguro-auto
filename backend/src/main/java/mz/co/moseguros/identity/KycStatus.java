package mz.co.moseguros.identity;

import java.time.Instant;
import java.util.UUID;

/**
 * Estado KYC de um cliente ({@link IdentityApi#kycStatus}). A verificação vale para todas as
 * seguradoras (ver "Questões" do S4-01) e não expira por tempo nesta fase.
 *
 * @param checkId             verificação mais recente; {@code null} em {@link KycState#NONE}
 * @param verifiedAt          só em {@link KycState#VERIFIED} e {@link KycState#OUTDATED}
 * @param verifiedByInsurerId só em {@link KycState#VERIFIED} e {@link KycState#OUTDATED}
 */
public record KycStatus(KycState state, UUID checkId, Instant verifiedAt, UUID verifiedByInsurerId) {

    /** Sem nenhuma verificação. */
    public static KycStatus none() {
        return new KycStatus(KycState.NONE, null, null, null);
    }
}
