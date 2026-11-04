import { useQuery } from '@tanstack/react-query';
import {
  getListCatalogProductsQueryKey,
  listCatalogProducts,
  type CatalogProductResponse,
  type CoverageResponseCapitalType,
  type ListCatalogProductsParams,
  type Money,
  type VehicleCategory,
  type VehicleUsage,
} from '../../api/generated';
import { mzn } from '../../format/money';
import { theftRequires, type CoverageChoice } from './quoteRules';

/** Uma cobertura no passo "Coberturas", juntando os produtos do tipo escolhido. */
export interface CoverageOption {
  code: string;
  /** Obrigatória em todos os produtos que a têm (aparece marcada e desactivada). */
  mandatory: boolean;
  /** Produtos (do tipo escolhido) que a oferecem. */
  availableIn: number;
  capitalType: CoverageResponseCapitalType;
  /** `OPTIONS`: a união dos capitais, por ordem crescente. */
  capitalOptions: Money[];
  /** `PER_PERSON`/`FIXED`: o capital (o menor, se os produtos diferirem). */
  capital?: Money;
}

export interface CoverageOptions {
  /** Produtos do tipo escolhido. */
  total: number;
  mandatory: CoverageOption[];
  optional: CoverageOption[];
}

/** Ordem fixa dos códigos (a do catálogo). */
const order = [
  'MTPL',
  'VTPL',
  'OD_COLLISION',
  'OD_GLASS',
  'OD_FIRE',
  'OD_NATURE',
  'OD_THEFT',
  'OCCUPANTS',
  'ASSISTANCE',
  'LEGAL',
  'SRCC',
  'CROSS_BORDER',
];

const byCode = (a: CoverageOption, b: CoverageOption) =>
  order.indexOf(a.code) - order.indexOf(b.code);

/**
 * Junta as coberturas dos produtos do tipo escolhido: as obrigatórias de todos e as opcionais (a
 * união dos códigos, com "disponível em n de total"); em `OPTIONS`, a união dos capitais.
 */
export function buildCoverageOptions(
  products: readonly CatalogProductResponse[],
  productType: string,
): CoverageOptions {
  const ofType = products.filter((product) => product.type === productType);
  const options = new Map<string, CoverageOption & { optionalSomewhere: boolean }>();
  for (const product of ofType) {
    for (const coverage of product.coverages) {
      const current = options.get(coverage.code) ?? {
        code: coverage.code,
        mandatory: true,
        optionalSomewhere: false,
        availableIn: 0,
        capitalType: coverage.capitalType,
        capitalOptions: [],
        capital: undefined,
      };
      current.availableIn += 1;
      if (!coverage.mandatory) current.optionalSomewhere = true;
      for (const option of coverage.capitalOptions ?? []) {
        if (!current.capitalOptions.some((m) => m.amount === option.amount)) {
          current.capitalOptions.push(mzn(option.amount));
        }
      }
      if (
        coverage.capital &&
        (!current.capital || coverage.capital.amount < current.capital.amount)
      ) {
        current.capital = mzn(coverage.capital.amount);
      }
      options.set(coverage.code, current);
    }
  }
  const all = [...options.values()].map(({ optionalSomewhere, ...option }) => ({
    ...option,
    mandatory: !optionalSomewhere,
    capitalOptions: [...option.capitalOptions].sort((a, b) => a.amount - b.amount),
  }));
  return {
    total: ofType.length,
    mandatory: all.filter((option) => option.mandatory).sort(byCode),
    optional: all.filter((option) => !option.mandatory).sort(byCode),
  };
}

const catalogPageSize = 100;

/** Catálogo público filtrado pela categoria e pelo uso do veículo e pelo tipo. */
export function useCoverageOptions(
  category: VehicleCategory | undefined,
  usage: VehicleUsage | undefined,
  productType: string,
) {
  const params: ListCatalogProductsParams = {
    vehicleCategory: category,
    usage,
    type: productType as ListCatalogProductsParams['type'],
    size: catalogPageSize,
  };
  return useQuery({
    queryKey: getListCatalogProductsQueryKey(params),
    queryFn: ({ signal }) => listCatalogProducts(params, undefined, signal),
    select: (data) => buildCoverageOptions(data.content, productType),
    enabled: Boolean(category && usage && productType),
    staleTime: 5 * 60_000,
  });
}

// --- Regras automáticas (S3-02 §3.5) ---

/**
 * Marca ou desmarca uma cobertura opcional. Marcar "Furto ou roubo" marca também choque, incêndio
 * e fenómenos da natureza (as que existirem); desmarcar uma destas desmarca "Furto ou roubo".
 * Devolve a nova escolha e se o aviso do furto se deve mostrar.
 */
export function toggleCoverage(
  selected: readonly CoverageChoice[],
  option: CoverageOption,
  checked: boolean,
  available: readonly CoverageOption[],
): { selected: CoverageChoice[]; theftNotice: boolean } {
  const without = (list: CoverageChoice[], codes: readonly string[]) =>
    list.filter((choice) => !codes.includes(choice.code));
  let next = [...selected];
  if (!checked) {
    next = without(next, [option.code]);
    if ((theftRequires as readonly string[]).includes(option.code)) {
      next = without(next, ['OD_THEFT']);
    }
    return { selected: next, theftNotice: false };
  }
  const add = (code: string) => {
    if (next.some((choice) => choice.code === code)) return;
    const found = available.find((o) => o.code === code);
    if (!found) return;
    next.push(defaultChoice(found));
  };
  add(option.code);
  if (option.code === 'OD_THEFT') {
    theftRequires.forEach(add);
    return { selected: next, theftNotice: true };
  }
  return { selected: next, theftNotice: false };
}

/** Escolha com o capital por defeito (o menor de `OPTIONS`). */
export function defaultChoice(option: CoverageOption): CoverageChoice {
  if (option.capitalType === 'OPTIONS' && option.capitalOptions.length > 0) {
    return { code: option.code, capital: option.capitalOptions[0] };
  }
  return { code: option.code };
}

/** Em `COMPREHENSIVE` começam marcadas choque, incêndio e fenómenos da natureza (se existirem). */
export function defaultCoverages(
  productType: string,
  optional: readonly CoverageOption[],
): CoverageChoice[] {
  if (productType !== 'COMPREHENSIVE') return [];
  return optional
    .filter((option) => (theftRequires as readonly string[]).includes(option.code))
    .map(defaultChoice);
}
