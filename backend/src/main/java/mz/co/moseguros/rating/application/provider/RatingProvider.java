package mz.co.moseguros.rating.application.provider;

import mz.co.moseguros.organization.RatingMode;

/**
 * Quem calcula o preço de uma versão de produto (S3-02 §4): a tarifa interna
 * ({@link InternalRatingProvider}) ou o sistema da seguradora ({@link HttpRatingProvider}),
 * escolhido pelo {@code ratingMode} da seguradora. Corre na fase 2: sem transacção, sem
 * {@code TenantContext} e sem acesso à BD.
 */
public interface RatingProvider {

    boolean supports(RatingMode mode);

    ProviderResult rate(RatingRequest request);
}
