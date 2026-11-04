import { useEffect } from 'react';
import type { Money, VehicleCategory, VehicleUsage } from '../../../api/generated';
import type { QuoteMode } from '../config';
import type { QuoteDraft } from '../useQuoteDraft';

export type DraftUpdate = (
  patch: Partial<QuoteDraft> | ((current: QuoteDraft) => Partial<QuoteDraft>),
) => void;

/** O que todos os passos recebem do assistente. */
export interface StepProps {
  mode: QuoteMode;
  draft: QuoteDraft;
  update: DraftUpdate;
  /** O passo diz se "Continuar" pode ficar activo. */
  onValidity(valid: boolean): void;
  /** Erros de campo do servidor (`errors[].field` → código), depois de um 400. */
  serverErrors: Record<string, string>;
}

/** Dados do veículo que os passos seguintes usam (do veículo do cliente ou do mediador). */
export interface RiskVehicle {
  vehicleId?: string;
  category?: VehicleCategory;
  usage?: VehicleUsage;
  commercialValue: Money | null;
  plate?: string;
  make?: string;
  model?: string;
  year?: number;
}

/** Comunica a validade do passo ao assistente. */
export function useStepValidity(valid: boolean, onValidity: (valid: boolean) => void) {
  useEffect(() => onValidity(valid), [valid, onValidity]);
}
