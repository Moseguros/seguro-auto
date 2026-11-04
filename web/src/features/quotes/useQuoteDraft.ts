import { useCallback, useState } from 'react';
import { todayInMaputo } from '../../format/dates';
import { draftStorageKeys, ONE_YEAR_DAYS, type QuoteMode } from './config';
import {
  emptyBrokerDriver,
  emptyBrokerVehicle,
  type BrokerDriverValues,
  type BrokerVehicleValues,
  type CoverageChoice,
} from './quoteRules';

/** Rascunho do assistente (guardado em `sessionStorage`). */
export interface QuoteDraft {
  step: number;
  /** Cliente: veículo e condutor escolhidos. */
  vehicleId: string;
  driverId: string;
  /** Mediador: os 9 dígitos depois do `+258`, o veículo e o condutor escritos à mão. */
  clientMsisdn: string;
  vehicle: BrokerVehicleValues;
  driver: BrokerDriverValues;
  productType: string;
  coverages: CoverageChoice[];
  /** Tipo para o qual as coberturas já foram iniciadas (evita repor as escolhas). */
  coveragesFor: string;
  startDate: string;
  durationDays: number;
}

export function emptyDraft(): QuoteDraft {
  return {
    step: 0,
    vehicleId: '',
    driverId: '',
    clientMsisdn: '',
    vehicle: emptyBrokerVehicle,
    driver: emptyBrokerDriver,
    productType: '',
    coverages: [],
    coveragesFor: '',
    startDate: todayInMaputo(),
    durationDays: ONE_YEAR_DAYS,
  };
}

export function readDraft(mode: QuoteMode): QuoteDraft {
  try {
    const raw = globalThis.sessionStorage?.getItem(draftStorageKeys[mode]);
    if (!raw) return emptyDraft();
    const parsed = JSON.parse(raw) as Partial<QuoteDraft>;
    const empty = emptyDraft();
    return {
      ...empty,
      ...parsed,
      vehicle: { ...empty.vehicle, ...parsed.vehicle },
      driver: { ...empty.driver, ...parsed.driver },
    };
  } catch {
    return emptyDraft();
  }
}

function writeDraft(mode: QuoteMode, draft: QuoteDraft | null) {
  try {
    if (draft) globalThis.sessionStorage?.setItem(draftStorageKeys[mode], JSON.stringify(draft));
    else globalThis.sessionStorage?.removeItem(draftStorageKeys[mode]);
  } catch {
    // Sem armazenamento: o rascunho vale só enquanto a página está aberta.
  }
}

/** Rascunho do assistente, lido ao abrir e gravado a cada mudança. */
export function useQuoteDraft(mode: QuoteMode) {
  const [draft, setDraft] = useState<QuoteDraft>(() => readDraft(mode));

  const update = useCallback(
    (patch: Partial<QuoteDraft> | ((current: QuoteDraft) => Partial<QuoteDraft>)) => {
      setDraft((current) => {
        const next = { ...current, ...(typeof patch === 'function' ? patch(current) : patch) };
        writeDraft(mode, next);
        return next;
      });
    },
    [mode],
  );

  const replace = useCallback(
    (next: QuoteDraft) => {
      writeDraft(mode, next);
      setDraft(next);
    },
    [mode],
  );

  const clear = useCallback(() => writeDraft(mode, null), [mode]);

  return { draft, update, replace, clear };
}
