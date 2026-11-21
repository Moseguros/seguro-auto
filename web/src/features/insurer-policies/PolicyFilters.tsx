import { useTranslation } from 'react-i18next';
import { PolicyStatus, type ListInsurerPoliciesParams } from '../../api/generated';
import {
  DateFilter,
  FiltersBar,
  MultiSelectFilter,
  SearchFilter,
  SingleSelectFilter,
} from '../insurer-payments/PaymentFilters';

const statuses = Object.values(PolicyStatus);
export const policyProductTypes = ['MTPL_ONLY', 'COMPREHENSIVE', 'TEMPORARY', 'BORDER', 'FLEET'];
export const policiesDefaultSort = 'issuedAt,desc';
const sortableFields = ['issuedAt', 'startAt', 'endAt'];
const pageSizes = [10, 20, 50];

/** Filtros da lista das apólices, lidos da query string (S6-05 §6). */
export interface PolicyListFilters {
  status: string[];
  productType: string;
  issuedFrom: string;
  issuedTo: string;
  q: string;
  page: number;
  size: number;
  sort: string;
}

function number(params: URLSearchParams, key: string, fallback: number): number {
  const raw = params.get(key);
  const value = Number(raw);
  return raw !== null && Number.isInteger(value) && value >= 0 ? value : fallback;
}

export function readPolicyFilters(params: URLSearchParams): PolicyListFilters {
  const sort = params.get('sort') ?? policiesDefaultSort;
  const size = number(params, 'size', 20);
  const productType = params.get('productType') ?? '';
  return {
    status: params.getAll('status').filter((s) => statuses.includes(s as PolicyStatus)),
    productType: policyProductTypes.includes(productType) ? productType : '',
    issuedFrom: params.get('issuedFrom') ?? '',
    issuedTo: params.get('issuedTo') ?? '',
    q: params.get('q') ?? '',
    page: number(params, 'page', 0),
    size: pageSizes.includes(size) ? size : 20,
    sort: sortableFields.includes(sort.split(',')[0] ?? '') ? sort : policiesDefaultSort,
  };
}

export function writePolicyFilters(filters: PolicyListFilters): URLSearchParams {
  const params = new URLSearchParams();
  filters.status.forEach((s) => params.append('status', s));
  if (filters.productType) params.set('productType', filters.productType);
  if (filters.issuedFrom) params.set('issuedFrom', filters.issuedFrom);
  if (filters.issuedTo) params.set('issuedTo', filters.issuedTo);
  if (filters.q) params.set('q', filters.q);
  if (filters.page) params.set('page', String(filters.page));
  if (filters.size !== 20) params.set('size', String(filters.size));
  if (filters.sort !== policiesDefaultSort) params.set('sort', filters.sort);
  return params;
}

/** Parâmetros do pedido `listInsurerPolicies`. */
export function policyQueryParams(filters: PolicyListFilters): ListInsurerPoliciesParams {
  return {
    status: filters.status.length ? (filters.status as PolicyStatus[]) : undefined,
    productType: (filters.productType || undefined) as ListInsurerPoliciesParams['productType'],
    issuedFrom: filters.issuedFrom || undefined,
    issuedTo: filters.issuedTo || undefined,
    q: filters.q || undefined,
    page: filters.page,
    size: filters.size,
    sort: filters.sort,
  };
}

/** Estado, tipo de produto, emitida de/até e pesquisa (com *debounce* de 400 ms). */
export function PolicyFilters({
  filters,
  narrow,
  onChange,
}: {
  filters: PolicyListFilters;
  narrow: boolean;
  onChange(changes: Partial<PolicyListFilters>): void;
}) {
  const { t } = useTranslation();
  return (
    <FiltersBar narrow={narrow}>
      <MultiSelectFilter
        id="policy-filter-status"
        label={t('insurerPolicies.filters.status')}
        value={filters.status}
        options={statuses.map((s) => ({ value: s, label: t(`policyStatus.${s}`) }))}
        onChange={(status) => onChange({ status })}
        fullWidth={narrow}
      />
      <SingleSelectFilter
        id="policy-filter-product-type"
        label={t('insurerPolicies.filters.productType')}
        value={filters.productType}
        options={policyProductTypes.map((p) => ({ value: p, label: t(`productType.${p}`) }))}
        onChange={(productType) => onChange({ productType })}
        allLabel={t('insurerPolicies.filters.all')}
        fullWidth={narrow}
      />
      <DateFilter
        label={t('insurerPolicies.filters.issuedFrom')}
        value={filters.issuedFrom}
        onChange={(issuedFrom) => onChange({ issuedFrom })}
        fullWidth={narrow}
      />
      <DateFilter
        label={t('insurerPolicies.filters.issuedTo')}
        value={filters.issuedTo}
        onChange={(issuedTo) => onChange({ issuedTo })}
        fullWidth={narrow}
      />
      <SearchFilter
        label={t('insurerPolicies.filters.search')}
        value={filters.q}
        onChange={(q) => onChange({ q })}
        fullWidth={narrow}
        delayMs={400}
      />
    </FiltersBar>
  );
}
