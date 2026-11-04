import { isAxiosError } from 'axios';

/**
 * Perguntas das declarações e da adequação, iguais a `api/proposal-questions.json` (S4-02; o
 * teste `wizardSteps.test.ts` confirma que são iguais). Os textos vêm do i18n
 * (`declaration.<CODE>.*`, `suitability.<CODE>.*`).
 */
export const declarationQuestions = [
  { code: 'CLAIMS_LAST_3_YEARS', followUp: ['claimsCount', 'details'] },
  { code: 'PREVIOUS_REFUSAL', followUp: ['details'] },
  { code: 'USAGE_DIFFERENT', followUp: ['details'] },
  { code: 'VEHICLE_MODIFIED', followUp: ['details'] },
  { code: 'NOVICE_DRIVERS', followUp: ['details'] },
] as const;

export const suitabilityQuestions = [
  { code: 'SUIT_NEED', options: ['MANDATORY_ONLY', 'OWN_DAMAGE', 'FULL', 'UNSURE'] },
  { code: 'SUIT_FINANCED', options: ['YES', 'NO'] },
  { code: 'SUIT_CAN_AFFORD_REPAIR', options: ['YES', 'NO', 'UNSURE'] },
  { code: 'SUIT_CROSS_BORDER', options: ['YES', 'NO'] },
  { code: 'SUIT_DEDUCTIBLE', options: ['YES', 'NO', 'UNSURE'] },
] as const;

/** Limites dos seguimentos das declarações (S4-02). */
export const DETAILS_MIN = 10;
export const DETAILS_MAX = 500;
export const CLAIMS_COUNT_MIN = 1;
export const CLAIMS_COUNT_MAX = 20;

/**
 * Propriedades extra do ProblemDetail (`proposalId`, `missing`, `missingSlots`), que o
 * `getProblem` não lê.
 */
export function problemData(error: unknown): Record<string, unknown> {
  if (!isAxiosError(error)) return {};
  const data: unknown = error.response?.data;
  return data && typeof data === 'object' ? (data as Record<string, unknown>) : {};
}

/** Lista de textos de uma propriedade do ProblemDetail (`[]` sem ela). */
export function problemList(error: unknown, name: string): string[] {
  const value = problemData(error)[name];
  return Array.isArray(value) ? value.filter((v): v is string => typeof v === 'string') : [];
}
