import type { MeOrganizationType, MeResponse } from '../api/generated';
import type { Area } from '../layout/menus';

export interface AreaTarget {
  /** Área do utilizador, ou `null` quando não tem área (polícia: só a verificação). */
  area: Area | null;
  /** Página inicial. */
  path: string;
}

const byOrganizationType: Record<MeOrganizationType, Area | null> = {
  INSURER: 'seguradora',
  BROKER: 'mediador',
  AGENT: 'mediador',
  ASSESSOR: 'perito',
  WORKSHOP: 'oficina',
  ASSISTANCE: 'assistencia',
  OMBUDSMAN: 'provedor',
  REGULATOR: 'supervisao',
  CORPORATE_CLIENT: 'frota',
  POLICE: null,
};

/** Área e página inicial de um utilizador autenticado. */
export function areaFor(me: Pick<MeResponse, 'type' | 'roles' | 'organization'>): AreaTarget {
  if (me.type === 'CUSTOMER') return { area: 'cliente', path: '/cliente' };
  if (me.roles.includes('PLATFORM_ADMIN')) return { area: 'admin', path: '/admin' };
  const orgType = me.organization?.type;
  if (orgType === 'POLICE') return { area: null, path: '/verificar' };
  const area = orgType ? byOrganizationType[orgType] : null;
  // Staff sem organização nem PLATFORM_ADMIN não tem área: fica na página inicial pública.
  return area ? { area, path: `/${area}` } : { area: null, path: '/' };
}
