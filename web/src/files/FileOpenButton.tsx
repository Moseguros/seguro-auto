import OpenInNew from '@mui/icons-material/OpenInNew';
import Button, { type ButtonProps } from '@mui/material/Button';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { downloadFile } from '../api/generated';
import { getProblem, problemMessage } from '../errors/problem';

export interface FileOpenButtonProps {
  fileId: string;
  label?: string;
  size?: ButtonProps['size'];
  /** Erro ao ler o ficheiro. */
  onError?(message: string): void;
}

/**
 * Abre o ficheiro num separador novo. O separador abre-se logo no clique (os browsers bloqueiam
 * janelas abertas depois de um pedido) e recebe o ficheiro quando chega.
 */
export function FileOpenButton({ fileId, label, size = 'small', onError }: FileOpenButtonProps) {
  const { t } = useTranslation();
  const [busy, setBusy] = useState(false);

  const open = async () => {
    const tab = window.open('', '_blank');
    setBusy(true);
    try {
      const blob = await downloadFile(fileId);
      const url = URL.createObjectURL(blob);
      if (tab) tab.location.href = url;
      else window.open(url, '_blank');
      setTimeout(() => URL.revokeObjectURL(url), 60_000);
    } catch (error) {
      tab?.close();
      onError?.(problemMessage(t, getProblem(error)));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Button size={size} startIcon={<OpenInNew />} onClick={() => void open()} disabled={busy}>
      {label ?? t('files.open')}
    </Button>
  );
}
