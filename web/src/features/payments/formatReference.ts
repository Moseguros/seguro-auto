import type { Money } from '../../api/generated';

/**
 * Referência Ponto24 (11 dígitos) em grupos de 3 a partir da esquerda: `00548781108` →
 * `005 487 811 08`. Só para mostrar: o que se copia é {@link referenceCopyValue}.
 */
export function formatReference(reference: string): string {
  return referenceCopyValue(reference).replace(/(\d{3})(?=\d)/g, '$1 ');
}

/** A referência sem espaços nem outros separadores (o que se copia). */
export function referenceCopyValue(reference: string): string {
  return reference.replace(/\D/g, '');
}

/** O montante a copiar: só os algarismos e a vírgula (`{ amount: 299900 }` → `2999,00`). */
export function amountCopyValue(money: Pick<Money, 'amount'>): string {
  const cents = Math.abs(Math.trunc(money.amount));
  const units = Math.floor(cents / 100);
  const rest = String(cents % 100).padStart(2, '0');
  return `${units},${rest}`;
}
