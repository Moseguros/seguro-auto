import type { ReferenceValueResponseDomain } from '../../api/generated';

export type ReferenceDomain = ReferenceValueResponseDomain;

/** Separadores de `/admin/referencias` (valores de `?separador=`). */
export const referenceTabs = ['marcas', 'modelos', 'cartas', 'matriculas', 'parametros'] as const;
export type ReferenceTab = (typeof referenceTabs)[number];

export function isReferenceTab(value: unknown): value is ReferenceTab {
  return typeof value === 'string' && (referenceTabs as readonly string[]).includes(value);
}

/** Domínio de cada separador de valores. */
export const tabDomains: Partial<Record<ReferenceTab, ReferenceDomain>> = {
  marcas: 'VEHICLE_MAKE',
  modelos: 'VEHICLE_MODEL',
  cartas: 'LICENCE_CATEGORY',
};

/** Chave das consultas das tabelas de referência (para invalidar depois de escrever). */
export const referencesQueryKey = ['references'] as const;
