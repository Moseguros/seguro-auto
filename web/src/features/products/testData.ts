import type { ProductResponse, ProductVersionResponse } from '../../api/generated';

/** Versão em preparação, vazia, para os testes. */
export const draftVersion: ProductVersionResponse = {
  id: 'v1',
  productId: 'p1',
  productCode: 'RC-TESTE',
  productName: 'RC Teste',
  productType: 'MTPL_ONLY',
  versionNo: 1,
  status: 'DRAFT',
  vehicleCategories: [],
  usages: [],
  coverages: [],
  documents: [],
  editable: true,
  version: 0,
  createdAt: '2026-11-03T08:00:00Z',
  updatedAt: '2026-11-03T08:00:00Z',
};

/** Versão publicada, com uma cobertura e os dois documentos. */
export const publishedVersion: ProductVersionResponse = {
  ...draftVersion,
  status: 'PUBLISHED',
  editable: false,
  version: 7,
  vehicleCategories: ['LIGHT_PASSENGER'],
  usages: ['PRIVATE'],
  validFrom: '2026-11-03',
  publishedAt: '2026-11-03T08:30:00Z',
  publishedBy: 'u1',
  coverages: [
    {
      code: 'MTPL',
      mandatory: true,
      capitalType: 'FIXED',
      capital: { amount: 300000000, currency: 'MZN' },
      deductibleType: 'NONE',
      territorialScope: ['MZ'],
    },
  ],
  documents: [
    { id: 'd1', docType: 'GENERAL_CONDITIONS', language: 'pt', fileId: 'f1' },
    { id: 'd2', docType: 'PRE_CONTRACT_INFO', language: 'pt', fileId: 'f2' },
  ],
};

export function productWith(version: ProductVersionResponse): ProductResponse {
  const ref = {
    id: version.id,
    versionNo: version.versionNo,
    status: version.status,
    validFrom: version.validFrom,
    publishedAt: version.publishedAt,
  };
  return {
    id: version.productId,
    code: version.productCode,
    name: version.productName,
    type: version.productType,
    status: version.status === 'DRAFT' ? 'DRAFT' : 'PUBLISHED',
    currentVersion: version.status === 'PUBLISHED' ? ref : undefined,
    draftVersion: version.status === 'DRAFT' ? ref : undefined,
    versions: [ref],
    version: 1,
    createdAt: version.createdAt,
    updatedAt: version.updatedAt,
  };
}
