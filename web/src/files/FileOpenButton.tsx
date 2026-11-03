import OpenInNew from '@mui/icons-material/OpenInNew';
import Button, { type ButtonProps } from '@mui/material/Button';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { downloadFile } from '../api/generated';
import { getProblem, problemMessage } from '../errors/problem';

export interface FileOpenButtonProps extends Omit<ButtonProps, 'onClick' | 'href' | 'onError'> {
  /** Ficheiro a ler da API no clique; ignorado quando já se tem o `url`. */
  fileId?: string;
  /** URL local já obtido com `useFileBlob` (evita descarregar duas vezes). */
  url?: string;
  /** Texto do botão (defeito `files.open`). */
  label?: string;
  /** Erro ao ler o ficheiro. */
  onError?(message: string): void;
}

/**
 * Abre o ficheiro num separador novo. Com `fileId`, o separador abre-se logo no clique (os
 * browsers bloqueiam janelas abertas depois de um pedido) e recebe o ficheiro quando chega.
 */
export function FileOpenButton({
  fileId,
  url,
  label,
  onError,
  disabled,
  size = 'small',
  ...props
}: FileOpenButtonProps) {
  const { t } = useTranslation();
  const [busy, setBusy] = useState(false);

  const open = async () => {
    if (url) {
      window.open(url, '_blank', 'noopener');
      return;
    }
    if (!fileId) return;
    const tab = window.open('', '_blank');
    setBusy(true);
    try {
      const blob = await downloadFile(fileId);
      const local = URL.createObjectURL(blob);
      if (tab) tab.location.href = local;
      else window.open(local, '_blank', 'noopener');
      setTimeout(() => URL.revokeObjectURL(local), 60_000);
    } catch (error) {
      tab?.close();
      onError?.(problemMessage(t, getProblem(error)));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Button
      size={size}
      startIcon={<OpenInNew />}
      {...props}
      onClick={() => void open()}
      disabled={disabled || busy || (!url && !fileId)}
    >
      {label ?? t('files.open')}
    </Button>
  );
}
