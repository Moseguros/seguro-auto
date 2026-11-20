import type {
  NotificationCategory,
  NotificationCategoryPreferencesChange,
  NotificationChannel,
  NotificationPreferencesResponse,
} from '../../api/generated';
import type { Problem } from '../../errors/problem';

/** Categoria → canal → ligado. */
export type PreferencesDraft = Partial<
  Record<NotificationCategory, Partial<Record<NotificationChannel, boolean>>>
>;

/** Canais que contam para as categorias obrigatórias (o push não conta). */
export const externalChannels: readonly NotificationChannel[] = ['SMS', 'WHATSAPP', 'EMAIL'];

export function draftFrom(response: NotificationPreferencesResponse): PreferencesDraft {
  const draft: PreferencesDraft = {};
  for (const category of response.categories) {
    draft[category.category] = Object.fromEntries(
      category.channels.map((c) => [c.channel, c.enabled]),
    );
  }
  return draft;
}

/**
 * As categorias alteradas, cada uma só com os canais que mudaram, pela ordem da resposta (é o
 * corpo do `updateMyNotificationPreferences`).
 */
export function changedCategories(
  response: NotificationPreferencesResponse,
  draft: PreferencesDraft,
): NotificationCategoryPreferencesChange[] {
  const changes: NotificationCategoryPreferencesChange[] = [];
  for (const category of response.categories) {
    const channels = category.channels
      .filter((c) => (draft[category.category]?.[c.channel] ?? c.enabled) !== c.enabled)
      .map((c) => ({ channel: c.channel, enabled: !c.enabled }));
    if (channels.length > 0) changes.push({ category: category.category, channels });
  }
  return changes;
}

/**
 * Validação igual à do backend (S6-04): numa categoria obrigatória alterada, SMS, WhatsApp e
 * e-mail não podem ficar todos desligados. Devolve as categorias com erro e o código.
 */
export function validatePreferences(
  response: NotificationPreferencesResponse,
  draft: PreferencesDraft,
): Partial<Record<NotificationCategory, string>> {
  const errors: Partial<Record<NotificationCategory, string>> = {};
  const changed = new Set(changedCategories(response, draft).map((c) => c.category));
  for (const category of response.categories) {
    if (!category.mandatory || !changed.has(category.category)) continue;
    const enabled = (channel: NotificationChannel) =>
      draft[category.category]?.[channel] ??
      category.channels.find((c) => c.channel === channel)?.enabled ??
      true;
    if (!externalChannels.some(enabled)) {
      errors[category.category] = 'MANDATORY_CATEGORY_NEEDS_CHANNEL';
    }
  }
  return errors;
}

const fieldPattern = /^categories\[(\d+)\]/;

/**
 * Erros de campo do servidor por categoria: `errors[].field = categories[i].channels`, com `i` o
 * índice no pedido enviado (não na resposta).
 */
export function serverCategoryErrors(
  problem: Problem,
  sent: NotificationCategoryPreferencesChange[],
): Partial<Record<NotificationCategory, string>> {
  const errors: Partial<Record<NotificationCategory, string>> = {};
  for (const error of problem.errors) {
    const match = fieldPattern.exec(error.field);
    const category = match ? sent[Number(match[1])]?.category : undefined;
    if (category) errors[category] = error.code;
  }
  return errors;
}
