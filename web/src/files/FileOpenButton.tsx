import OpenInNew from '@mui/icons-material/OpenInNew';
import Button, { type ButtonProps } from '@mui/material/Button';
import { useFileBlob } from './useFileBlob';

export interface FileOpenButtonProps extends Omit<ButtonProps, 'onClick' | 'href'> {
  /** Ficheiro a descarregar; ignorado quando já se tem o `url`. */
  fileId?: string;
  /** URL local já obtido com `useFileBlob` (evita descarregar duas vezes). */
  url?: string;
  label: string;
}

/** Abre o ficheiro num separador novo (desactivado enquanto descarrega). */
export function FileOpenButton({ fileId, url, label, disabled, ...props }: FileOpenButtonProps) {
  const blob = useFileBlob(url ? null : fileId);
  const target = url ?? blob.url;
  return (
    <Button
      startIcon={<OpenInNew />}
      {...props}
      disabled={disabled || !target}
      onClick={() => {
        if (target) window.open(target, '_blank', 'noopener');
      }}
    >
      {label}
    </Button>
  );
}
