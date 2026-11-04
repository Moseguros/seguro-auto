import type {
  InsurerRatingSettingsResponse,
  ProductSummaryResponse,
  RatingRulesResponse,
} from '../../api/generated';
import { staffMe } from '../../test/staff';
import { draftVersion, productWith, publishedVersion } from '../products/testData';

export const tariffManager = staffMe({
  roles: ['PRODUCT_MANAGER'],
  permissions: ['product:read', 'product:manage', 'rating:manage'],
  orgType: 'INSURER',
});

export const tariffReader = staffMe({
  roles: ['UNDERWRITER'],
  permissions: ['product:read'],
  orgType: 'INSURER',
});

const mtpl = {
  code: 'MTPL',
  mandatory: true,
  capitalType: 'FIXED',
  capital: { amount: 300000000, currency: 'MZN' },
} as const;

/** Versão em preparação sem tarifa (só a MTPL). */
export const draftRules: RatingRulesResponse = {
  productVersionId: draftVersion.id,
  productType: 'MTPL_ONLY',
  editable: true,
  configured: false,
  coverages: [mtpl],
  version: 3,
};

/** Versão publicada com tarifa. */
export const publishedRules: RatingRulesResponse = {
  productVersionId: publishedVersion.id,
  productType: 'MTPL_ONLY',
  editable: false,
  configured: true,
  rating: {
    schemaVersion: 'rating-v1',
    coverages: [
      {
        coverageCode: 'MTPL',
        base: { type: 'AMOUNT', rows: [{ label: 'Todos', when: {}, value: 450000 }] },
      },
    ],
  },
  coverages: [mtpl],
  version: 7,
};

export function settings(
  overrides: Partial<InsurerRatingSettingsResponse> = {},
): InsurerRatingSettingsResponse {
  return { ratingMode: 'INTERNAL', charges: [], version: 1, ...overrides };
}

export const productSummary: ProductSummaryResponse = {
  id: draftVersion.productId,
  code: draftVersion.productCode,
  name: draftVersion.productName,
  type: draftVersion.productType,
  status: 'DRAFT',
  hasDraft: true,
  updatedAt: draftVersion.updatedAt,
};

export { draftVersion, productWith, publishedVersion };
