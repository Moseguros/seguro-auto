import { useCallback, useState, type ReactElement } from 'react';
import { emptyDraft, type QuoteDraft } from '../useQuoteDraft';
import type { DraftUpdate, StepProps } from './stepTypes';

/** Estado observado pelos testes dos passos. */
export interface HarnessState {
  draft: QuoteDraft;
  valid: boolean;
}

/** Mostra um passo com um rascunho em memória (só para testes). */
export function StepHarness({
  initial,
  state,
  render,
}: {
  initial: Partial<QuoteDraft>;
  state: HarnessState;
  render(props: StepProps): ReactElement;
}) {
  const [draft, setDraft] = useState<QuoteDraft>(() => ({ ...emptyDraft(), ...initial }));
  state.draft = draft;
  const update: DraftUpdate = useCallback((patch) => {
    setDraft((current) => ({
      ...current,
      ...(typeof patch === 'function' ? patch(current) : patch),
    }));
  }, []);
  const onValidity = useCallback(
    (valid: boolean) => {
      state.valid = valid;
    },
    [state],
  );
  return render({ mode: 'customer', draft, update, onValidity, serverErrors: {} });
}

export function harnessState(): HarnessState {
  return { draft: emptyDraft(), valid: false };
}
