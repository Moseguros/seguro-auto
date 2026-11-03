import Search from '@mui/icons-material/Search';
import Button from '@mui/material/Button';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { lookupVehicleRegistry, type RegistryLookupResponse } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { normalizePlate } from './vehicleSchema';

export interface LookupNotice {
  severity: 'info' | 'warning' | 'error';
  message: string;
}

export interface RegistryLookupButtonProps {
  plate: string;
  /** 200: dados do registo para preencher o formulário. */
  onFound(data: RegistryLookupResponse): void;
  /** Aviso a mostrar por baixo da matrícula (`null` apaga o anterior). */
  onNotice(notice: LookupNotice | null): void;
  /** Erro no campo matrícula (código `validation.*`). */
  onPlateError(code: string): void;
}

/** "Procurar no registo": consulta a matrícula no registo automóvel e devolve os dados. */
export function RegistryLookupButton({
  plate,
  onFound,
  onNotice,
  onPlateError,
}: RegistryLookupButtonProps) {
  const { t } = useTranslation();
  const [busy, setBusy] = useState(false);

  const lookup = async () => {
    onNotice(null);
    if (!normalizePlate(plate)) {
      onPlateError('REQUIRED');
      return;
    }
    setBusy(true);
    try {
      const data = await lookupVehicleRegistry({ plate: plate.trim() });
      onFound(data);
      onNotice({ severity: 'info', message: t('vehicles.lookup.filled') });
    } catch (error) {
      const problem = getProblem(error);
      if (problem.status === 400) {
        onPlateError(problem.errors.find((e) => e.field === 'plate')?.code ?? 'INVALID_PLATE');
      } else {
        const severity = problem.status === 404 || problem.status === 503 ? 'warning' : 'error';
        onNotice({ severity, message: problemMessage(t, problem) });
      }
    } finally {
      setBusy(false);
    }
  };

  return (
    <Button
      variant="outlined"
      startIcon={<Search />}
      disabled={busy}
      onClick={() => void lookup()}
      sx={{ flexShrink: 0, alignSelf: { xs: 'stretch', sm: 'flex-start' }, minHeight: 56 }}
    >
      {t('vehicles.lookup.button')}
    </Button>
  );
}
