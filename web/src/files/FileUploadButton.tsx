import Upload from '@mui/icons-material/Upload';
import Button, { type ButtonProps } from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { useRef, useState, type ChangeEvent } from 'react';
import { useTranslation } from 'react-i18next';

/** Limite do servidor para os ficheiros (S2-01). */
export const maxFileBytes = 10 * 1024 * 1024;

export interface FileUploadButtonProps {
  label: string;
  /** Tipos aceites, como no `<input accept>` (`application/pdf`, `image/png,image/jpeg`). */
  accept: string;
  /** Tamanho máximo em bytes (defeito 10 MB). */
  maxBytes?: number;
  /** Mensagem para um ficheiro grande de mais (defeito `errors.FILE_TOO_LARGE`). */
  tooLargeMessage?: string;
  onFile(file: File): void;
  disabled?: boolean;
  variant?: ButtonProps['variant'];
  size?: ButtonProps['size'];
}

function accepted(file: File, accept: string): boolean {
  const type = file.type.toLowerCase();
  return accept
    .split(',')
    .map((item) => item.trim().toLowerCase())
    .filter(Boolean)
    .some((item) => (item.endsWith('/*') ? type.startsWith(item.slice(0, -1)) : type === item));
}

/** Escolha de um ficheiro, verificado no browser (tipo e tamanho) antes de ir para a API. */
export function FileUploadButton({
  label,
  accept,
  maxBytes = maxFileBytes,
  tooLargeMessage,
  onFile,
  disabled,
  variant = 'outlined',
  size = 'small',
}: FileUploadButtonProps) {
  const { t } = useTranslation();
  const input = useRef<HTMLInputElement>(null);
  const [error, setError] = useState<string | null>(null);

  const choose = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) return;
    if (!accepted(file, accept)) {
      setError(t('errors.FILE_TYPE_NOT_ALLOWED'));
      return;
    }
    if (file.size > maxBytes) {
      setError(tooLargeMessage ?? t('errors.FILE_TOO_LARGE'));
      return;
    }
    setError(null);
    onFile(file);
  };

  return (
    <>
      <Button
        variant={variant}
        size={size}
        startIcon={<Upload />}
        disabled={disabled}
        onClick={() => input.current?.click()}
      >
        {label}
      </Button>
      <input
        ref={input}
        type="file"
        accept={accept}
        hidden
        aria-label={label}
        onChange={choose}
        data-testid="file-input"
      />
      {error && (
        <Typography role="alert" variant="body2" color="error" sx={{ width: '100%' }}>
          {error}
        </Typography>
      )}
    </>
  );
}
