import { z } from 'zod';

/** As mesmas regras do backend (`CustomerProfileRules`, S1-06). Mensagens = códigos `validation.*`. */

export const idDocTypes = ['BI', 'PASSPORT', 'DIRE'] as const;
export const provinces = [
  'MAPUTO_CITY',
  'MAPUTO',
  'GAZA',
  'INHAMBANE',
  'SOFALA',
  'MANICA',
  'TETE',
  'ZAMBEZIA',
  'NAMPULA',
  'CABO_DELGADO',
  'NIASSA',
] as const;

export const profileTimeZone = 'Africa/Maputo';
const minAge = 18;
const maxAge = 120;

const namePattern = /^[\p{L}\p{M}' -]+$/u;
const letterPattern = /\p{L}/u;
const documentPattern = /^[A-Z0-9]{5,20}$/;
const nuitPattern = /^\d{9}$/;
const datePattern = /^(\d{4})-(\d{2})-(\d{2})$/;

/** Sem espaços nas pontas e um só espaço entre palavras. */
export function normalizeName(value: string): string {
  return value.trim().replace(/\s+/g, ' ');
}

/** Maiúsculas e sem espaços. */
export function normalizeDocumentNumber(value: string): string {
  return value.replace(/\s+/g, '').toUpperCase();
}

export function normalizeNuit(value: string): string {
  return value.replace(/\s+/g, '');
}

export function isValidName(value: string): boolean {
  const name = normalizeName(value);
  if (name.length < 2 || name.length > 120 || !namePattern.test(name)) return false;
  const words = name.split(' ');
  return words.length >= 2 && words.every((word) => letterPattern.test(word));
}

/** Data de hoje (`YYYY-MM-DD`) em `Africa/Maputo`. */
export function todayInMaputo(now: Date = new Date()): string {
  return new Intl.DateTimeFormat('en-CA', {
    timeZone: profileTimeZone,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(now);
}

function parseDate(value: string): [number, number, number] | null {
  const match = datePattern.exec(value);
  if (!match) return null;
  const [y, m, d] = [Number(match[1]), Number(match[2]), Number(match[3])];
  const date = new Date(Date.UTC(y, m - 1, d));
  if (date.getUTCFullYear() !== y || date.getUTCMonth() !== m - 1 || date.getUTCDate() !== d) {
    return null;
  }
  return [y, m, d];
}

/** Anos completos entre duas datas `YYYY-MM-DD` (como `Period.between(...).getYears()`). */
export function ageInYears(birth: [number, number, number], today: [number, number, number]) {
  let years = today[0] - birth[0];
  if (today[1] < birth[1] || (today[1] === birth[1] && today[2] < birth[2])) years -= 1;
  return years;
}

/** Código do erro da data de nascimento, ou `null` se for válida. */
export function birthDateError(value: string, today: string = todayInMaputo()): string | null {
  if (!value) return 'REQUIRED';
  const birth = parseDate(value);
  const now = parseDate(today);
  if (!birth || !now) return 'INVALID_DATE';
  if (value > today) return 'UNDERAGE';
  const age = ageInYears(birth, now);
  if (age < minAge) return 'UNDERAGE';
  if (age > maxAge) return 'INVALID_DATE';
  return null;
}

export interface ProfileSchemaOptions {
  /** O número do documento é obrigatório (ainda não existe ou o tipo mudou). */
  documentRequired: boolean;
  /** O NUIT é obrigatório (ainda não existe). */
  nuitRequired: boolean;
  today?: string;
}

export function profileSchema({ documentRequired, nuitRequired, today }: ProfileSchemaOptions) {
  return z
    .object({
      fullName: z.string(),
      birthDate: z.string(),
      idDocType: z.string(),
      idDocNumber: z.string(),
      nuit: z.string(),
      address: z.string(),
      province: z.string(),
    })
    .superRefine((values, ctx) => {
      const issue = (path: string, message: string) =>
        ctx.addIssue({ code: z.ZodIssueCode.custom, path: [path], message });

      if (!normalizeName(values.fullName)) issue('fullName', 'REQUIRED');
      else if (!isValidName(values.fullName)) issue('fullName', 'INVALID_NAME');

      const birth = birthDateError(values.birthDate, today);
      if (birth) issue('birthDate', birth);

      if (!(idDocTypes as readonly string[]).includes(values.idDocType)) {
        issue('idDocType', 'REQUIRED');
      }

      const document = normalizeDocumentNumber(values.idDocNumber);
      if (!document) {
        if (documentRequired) issue('idDocNumber', 'REQUIRED');
      } else if (!documentPattern.test(document)) {
        issue('idDocNumber', 'INVALID_DOCUMENT_NUMBER');
      }

      const nuit = normalizeNuit(values.nuit);
      if (!nuit) {
        if (nuitRequired) issue('nuit', 'REQUIRED');
      } else if (!nuitPattern.test(nuit)) {
        issue('nuit', 'INVALID_NUIT');
      }

      if (values.address.trim().length > 300) issue('address', 'SIZE');

      if (!(provinces as readonly string[]).includes(values.province)) {
        issue('province', 'REQUIRED');
      }
    });
}

export type ProfileFormValues = z.infer<ReturnType<typeof profileSchema>>;

export const profileFields = [
  'fullName',
  'birthDate',
  'idDocType',
  'idDocNumber',
  'nuit',
  'address',
  'province',
] as const satisfies readonly (keyof ProfileFormValues)[];
