import { useQuery, type QueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import {
  getListActivePlateFormatsQueryKey,
  getListReferenceValuesQueryKey,
  listActivePlateFormats,
  listReferenceValues,
  type PublicReferenceValueResponse,
} from '../../api/generated';

type ReferenceDomain = Parameters<typeof listReferenceValues>[0];

const startsWith = (prefix: string) => (query: { queryKey: readonly unknown[] }) =>
  typeof query.queryKey[0] === 'string' && query.queryKey[0].startsWith(prefix);

/** Lista e detalhes dos veículos (`/api/v1/me/vehicles…`). */
export function invalidateVehicles(queryClient: QueryClient): Promise<void> {
  return queryClient.invalidateQueries({ predicate: startsWith('/api/v1/me/vehicles') });
}

/** Lista e detalhes dos condutores (`/api/v1/me/drivers…`). */
export function invalidateDrivers(queryClient: QueryClient): Promise<void> {
  return queryClient.invalidateQueries({ predicate: startsWith('/api/v1/me/drivers') });
}

/** Valores activos de uma tabela de referência (públicos, mudam pouco). */
export function useReferenceValues(
  domain: ReferenceDomain,
  parentCode?: string,
  enabled: boolean = true,
) {
  const params = parentCode ? { parentCode } : undefined;
  return useQuery({
    queryKey: getListReferenceValuesQueryKey(domain, params),
    queryFn: ({ signal }) => listReferenceValues(domain, params, undefined, signal),
    select: (data) => data.values,
    staleTime: 10 * 60_000,
    enabled,
  });
}

/** Formatos de matrícula activos (para a ajuda do campo). */
export function usePlateFormats() {
  return useQuery({
    queryKey: getListActivePlateFormatsQueryKey(),
    queryFn: ({ signal }) => listActivePlateFormats(undefined, signal),
    select: (data) => data.formats,
    staleTime: 10 * 60_000,
  });
}

/** Rótulo de um valor de referência na língua actual (o `labelPt` se não houver tradução). */
export function useReferenceLabel() {
  const { i18n } = useTranslation();
  return (value: PublicReferenceValueResponse) =>
    i18n.language === 'en' ? (value.labelEn ?? value.labelPt) : value.labelPt;
}
