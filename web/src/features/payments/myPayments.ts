import type { PageResponseIntentView } from '../../api/generated';

export const PAGE_SIZE_PAYMENTS = 20;

/** A página seguinte da lista de pagamentos, ou `undefined` na última. */
export function nextPaymentsPage(last: PageResponseIntentView): number | undefined {
  return (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined;
}
