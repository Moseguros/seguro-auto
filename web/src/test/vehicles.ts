import type { InternalAxiosRequestConfig } from 'axios';
import type {
  DriverResponse,
  PublicReferenceValueResponse,
  RegistryLookupResponse,
  VehicleResponse,
} from '../api/generated';
import type { MockResult } from './staff';

/** Dados de teste dos veículos e condutores (S2-06). */

export const vehicleFixture: VehicleResponse = {
  id: 'v1',
  plate: 'AAA-100-MC',
  chassisNumber: 'JTDBR32E720012345',
  make: 'Toyota',
  model: 'Corolla',
  year: 2018,
  engineCc: 1598,
  category: 'LIGHT_PASSENGER',
  seats: 5,
  usage: 'PRIVATE',
  provinceOfCirculation: 'MAPUTO_CITY',
  commercialValue: { amount: 85000000, currency: 'MZN' },
  hasTracker: false,
  inatroVerified: true,
  drivers: [],
  version: 2,
  createdAt: '2026-11-03T08:00:00Z',
  updatedAt: '2026-11-03T08:00:00Z',
};

export function driverFixture(overrides: Partial<DriverResponse> = {}): DriverResponse {
  return {
    id: 'd1',
    fullName: 'Ana Maria Mussa',
    birthDate: '1990-05-14',
    licenceNumberMasked: '****4567',
    licenceCategories: ['B'],
    licenceIssueDate: '2010-06-01',
    isOwner: false,
    vehicleIds: [],
    version: 0,
    createdAt: '2026-11-03T08:00:00Z',
    updatedAt: '2026-11-03T08:00:00Z',
    ...overrides,
  };
}

export const registryFixture: RegistryLookupResponse = {
  plate: 'AAA-100-MC',
  chassisNumber: 'JTDBR32E720012345',
  make: 'Toyota',
  model: 'Corolla',
  year: 2018,
  engineCc: 1598,
  category: 'LIGHT_PASSENGER',
  seats: 5,
  source: 'MOCK',
};

export const makesFixture: PublicReferenceValueResponse[] = [
  { code: 'TOYOTA', labelPt: 'Toyota' },
  { code: 'NISSAN', labelPt: 'Nissan' },
];

export const modelsFixture: PublicReferenceValueResponse[] = [
  { code: 'TOYOTA_COROLLA', labelPt: 'Corolla', parentCode: 'TOYOTA' },
  { code: 'TOYOTA_HILUX', labelPt: 'Hilux', parentCode: 'TOYOTA' },
];

export const licenceCategoriesFixture: PublicReferenceValueResponse[] = [
  { code: 'A', labelPt: 'A — Motociclos', labelEn: 'A — Motorcycles' },
  { code: 'B', labelPt: 'B — Ligeiros', labelEn: 'B — Light vehicles' },
  { code: 'C', labelPt: 'C — Pesados', labelEn: 'C — Heavy vehicles' },
];

/** Respostas das listas públicas (formatos de matrícula e tabelas de referência). */
export function referenceHandler(config: InternalAxiosRequestConfig): MockResult | undefined {
  if (config.method?.toUpperCase() !== 'GET') return undefined;
  if (config.url === '/api/v1/plate-formats') {
    return {
      status: 200,
      data: {
        formats: [
          { code: 'STANDARD', label: 'Actual', example: 'AAA-100-MC' },
          { code: 'LEGACY', label: 'Antiga', example: 'MLA-12-34' },
        ],
      },
    };
  }
  if (config.url === '/api/v1/reference-domains/VEHICLE_MAKE/values') {
    return { status: 200, data: { values: makesFixture } };
  }
  if (config.url === '/api/v1/reference-domains/VEHICLE_MODEL/values') {
    const parent = (config.params as { parentCode?: string } | undefined)?.parentCode;
    return {
      status: 200,
      data: { values: modelsFixture.filter((model) => model.parentCode === parent) },
    };
  }
  if (config.url === '/api/v1/reference-domains/LICENCE_CATEGORY/values') {
    return { status: 200, data: { values: licenceCategoriesFixture } };
  }
  return undefined;
}

export function page<T>(content: T[]) {
  return { content, page: 0, size: 50, totalElements: content.length };
}
