/** Configuração das cotações na web (S3-04). */

/** Igual ao `moseguros.rating.max-start-days-ahead` do backend (S3-02 §2). */
export const QUOTE_MAX_START_DAYS_AHEAD = 60;

/** Máximo de propostas lado a lado. */
export const QUOTE_MAX_COMPARE = 3;

/** Simulações mostradas no assistente e no início do cliente. */
export const RECENT_QUOTES_SIZE = 3;

/** Tamanho das páginas das listas de cotações. */
export const QUOTE_LIST_PAGE_SIZE = 20;

/** Durações aceites (em dias); 365 = 1 ano. */
export const temporaryDurations = [7, 14, 30, 60, 365] as const;
export const ONE_YEAR_DAYS = 365;

export type QuoteMode = 'customer' | 'broker';

export interface QuotePaths {
  /** Caminho base dos resultados (`<base>/:requestId`). */
  base: string;
  /** Assistente. */
  wizard: string;
  /** Lista das cotações. */
  list: string;
}

export const quotePaths: Record<QuoteMode, QuotePaths> = {
  customer: { base: '/cliente/simular', wizard: '/cliente/simular', list: '/cliente/cotacoes' },
  broker: {
    base: '/mediador/cotacoes',
    wizard: '/mediador/cotacoes/nova',
    list: '/mediador/cotacoes',
  },
};

/** Rascunho do assistente em `sessionStorage`. */
export const draftStorageKeys: Record<QuoteMode, string> = {
  customer: 'moseguros.quoteDraft.customer',
  broker: 'moseguros.quoteDraft.broker',
};

/** `?voltar=` dos formulários de veículo e condutor quando se vem do assistente. */
export const backToWizard = encodeURIComponent(quotePaths.customer.wizard);

/**
 * Destino do `?voltar=` depois de gravar um veículo ou um condutor: só caminhos da área do
 * cliente (`/cliente/…`); qualquer outro valor é ignorado (evita redirecções abertas).
 */
export function customerReturnPath(value: string | null): string | null {
  if (!value || !value.startsWith('/cliente/') || value.includes('\\')) return null;
  return value;
}
