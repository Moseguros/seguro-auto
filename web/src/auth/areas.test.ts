import { describe, expect, it } from 'vitest';
import type { MeOrganizationType, MeResponse } from '../api/generated';
import { areaFor } from './areas';

function staff(orgType?: MeOrganizationType, roles: string[] = []): MeResponse {
  return {
    id: 'u',
    type: 'STAFF',
    roles,
    permissions: [],
    preferredLanguage: 'pt',
    emailVerified: true,
    msisdnVerified: false,
    organization: orgType ? { id: 'o', displayName: 'Org', type: orgType } : undefined,
  };
}

describe('areaFor', () => {
  it('cliente → /cliente', () => {
    expect(areaFor({ ...staff(), type: 'CUSTOMER' })).toEqual({
      area: 'cliente',
      path: '/cliente',
    });
  });

  it('staff com PLATFORM_ADMIN → /admin', () => {
    expect(areaFor(staff(undefined, ['PLATFORM_ADMIN']))).toEqual({
      area: 'admin',
      path: '/admin',
    });
  });

  it.each([
    ['INSURER', 'seguradora'],
    ['BROKER', 'mediador'],
    ['AGENT', 'mediador'],
    ['ASSESSOR', 'perito'],
    ['WORKSHOP', 'oficina'],
    ['ASSISTANCE', 'assistencia'],
    ['OMBUDSMAN', 'provedor'],
    ['REGULATOR', 'supervisao'],
    ['CORPORATE_CLIENT', 'frota'],
  ] as const)('%s → /%s', (orgType, area) => {
    expect(areaFor(staff(orgType, ['ORG_MANAGER']))).toEqual({ area, path: `/${area}` });
  });

  it('POLICE → /verificar, sem área', () => {
    expect(areaFor(staff('POLICE', ['POLICE_OFFICER']))).toEqual({
      area: null,
      path: '/verificar',
    });
  });

  it('staff sem organização nem PLATFORM_ADMIN não tem área', () => {
    expect(areaFor(staff())).toEqual({ area: null, path: '/' });
  });
});
