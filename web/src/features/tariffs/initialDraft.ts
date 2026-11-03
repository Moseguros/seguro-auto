import type { RatingCoverage, RatingRulesResponse } from '../../api/generated';
import type { BaseType, CoverageTariff, Rating } from './ratingTypes';

/** Tipos de base permitidos por tipo de capital (S3-01 §7.3). */
export function allowedBaseTypes(capitalType: RatingCoverage['capitalType']): BaseType[] {
  switch (capitalType) {
    case 'VEHICLE_VALUE':
      return ['AMOUNT', 'RATE_OF_VEHICLE_VALUE'];
    default:
      return ['AMOUNT', 'RATE_OF_CAPITAL'];
  }
}

/** Tarifa vazia de uma cobertura: uma linha "sempre" sem valor, sem factores. */
export function emptyCoverageTariff(coverage: RatingCoverage): CoverageTariff {
  return {
    coverageCode: coverage.code,
    base: {
      type: coverage.capitalType === 'VEHICLE_VALUE' ? 'RATE_OF_VEHICLE_VALUE' : 'AMOUNT',
      rows: [{ when: {}, value: null }],
    },
  };
}

/** Rascunho inicial de uma versão sem tarifa (S3-03 §4). */
export function initialDraft(response: Pick<RatingRulesResponse, 'coverages' | 'productType'>) {
  const rating: Rating = {
    schemaVersion: 'rating-v1',
    coverages: response.coverages.map(emptyCoverageTariff),
  };
  if (response.productType === 'TEMPORARY') {
    rating.durationFactors = [{ days: 365, factor: '1' }];
  }
  return rating;
}

/** Rascunho a partir da resposta do servidor: a tarifa gravada ou a inicial. */
export function draftFrom(response: RatingRulesResponse): Rating {
  return response.rating ? (response.rating as unknown as Rating) : initialDraft(response);
}
