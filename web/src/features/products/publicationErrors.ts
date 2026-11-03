import type { RuleError } from '../../api/generated';

/** Passos do editor da versão, pela ordem (valores de `?passo=`). */
export const editorSteps = ['dados', 'coberturas', 'franquias', 'documentos', 'revisao'] as const;
export type EditorStep = (typeof editorSteps)[number];

export function isEditorStep(value: unknown): value is EditorStep {
  return typeof value === 'string' && (editorSteps as readonly string[]).includes(value);
}

/** Passo onde se corrige o erro de publicação ou de gravação com este `field`. */
export function stepForField(field: string): EditorStep {
  if (field.startsWith('documents')) return 'documentos';
  if (field === 'vehicleCategories' || field === 'usages' || field === 'changeNotes') {
    return 'dados';
  }
  if (/^coverages\[\d+\]\.deductible/.test(field)) return 'franquias';
  if (field.startsWith('coverages')) return 'coberturas';
  return 'dados';
}

/** Erros agrupados por passo, pela ordem dos passos (só os passos com erros). */
export function groupByStep(errors: readonly RuleError[]): [EditorStep, RuleError[]][] {
  const groups = new Map<EditorStep, RuleError[]>();
  for (const error of errors) {
    const step = stepForField(error.field);
    groups.set(step, [...(groups.get(step) ?? []), error]);
  }
  return editorSteps.filter((step) => groups.has(step)).map((step) => [step, groups.get(step)!]);
}

/** Índice da cobertura em `coverages[i]…`, ou `undefined`. */
export function coverageIndex(field: string): number | undefined {
  const match = /^coverages\[(\d+)\]/.exec(field);
  return match ? Number(match[1]) : undefined;
}
