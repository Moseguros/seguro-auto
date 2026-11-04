import { useCallback, useMemo, useState } from 'react';
import type { Rating } from './ratingTypes';

/** Passos guardados para "Anular". */
export const maxHistory = 50;

interface History {
  past: Rating[];
  present: Rating | null;
  future: Rating[];
  /** O que está gravado no servidor (JSON), para saber se há alterações por guardar. */
  saved: string;
}

/** Rascunho da tarifa em estado local, com "Anular" e "Refazer" (até 50 passos). */
export function useTariffDraft() {
  const [history, setHistory] = useState<History>({
    past: [],
    present: null,
    future: [],
    saved: '',
  });

  /** Substitui tudo (tarifa lida do servidor): sem histórico, sem alterações. */
  const reset = useCallback((rating: Rating) => {
    setHistory({ past: [], present: rating, future: [], saved: JSON.stringify(rating) });
  }, []);

  /** Nova versão do rascunho (um passo no histórico). */
  const change = useCallback((update: Rating | ((current: Rating) => Rating)) => {
    setHistory((h) => {
      if (!h.present) return h;
      const next = typeof update === 'function' ? update(h.present) : update;
      if (next === h.present) return h;
      return {
        ...h,
        past: [...h.past, h.present].slice(-maxHistory),
        present: next,
        future: [],
      };
    });
  }, []);

  const undo = useCallback(() => {
    setHistory((h) => {
      const previous = h.past[h.past.length - 1];
      if (!previous || !h.present) return h;
      return {
        ...h,
        past: h.past.slice(0, -1),
        present: previous,
        future: [h.present, ...h.future],
      };
    });
  }, []);

  const redo = useCallback(() => {
    setHistory((h) => {
      const [next, ...rest] = h.future;
      if (!next || !h.present) return h;
      return { ...h, past: [...h.past, h.present], present: next, future: rest };
    });
  }, []);

  /** O rascunho actual passou a ser o gravado. */
  const markSaved = useCallback((rating: Rating) => {
    setHistory((h) => ({ ...h, saved: JSON.stringify(rating) }));
  }, []);

  const dirty = useMemo(
    () => history.present !== null && JSON.stringify(history.present) !== history.saved,
    [history.present, history.saved],
  );

  return {
    draft: history.present,
    change,
    reset,
    undo,
    redo,
    markSaved,
    canUndo: history.past.length > 0,
    canRedo: history.future.length > 0,
    dirty,
  };
}
