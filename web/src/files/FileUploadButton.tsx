import Upload from '@mui/icons-material/Upload';
import Button, { type ButtonProps } from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { useRef, useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';

/** Limite do servidor para os ficheiros (S2-01). */
export const maxFileBytes = 10 * 1024 * 1024;

export type FileRejection = 'FILE_TOO_LARGE' | 'FILE_TYPE_NOT_ALLOWED' | 'FILE_EMPTY';

const extensionTypes: Record<string, string> = {
  heic: 'image/heic',
  heif: 'image/heic',
  jpg: 'image/jpeg',
  jpeg: 'image/jpeg',
  png: 'image/png',
  pdf: 'application/pdf',
};

/** Tipo do ficheiro; alguns browsers não sabem o de HEIC e deixam-no vazio. */
export function fileType(file: File): string {
  if (file.type) return file.type.toLowerCase();
  const extension = file.name.split('.').pop()?.toLowerCase() ?? '';
  return extensionTypes[extension] ?? '';
}

function matches(type: string, allowed: string): boolean {
  const pattern = allowed.trim().toLowerCase();
  if (!pattern) return false;
  if (pattern.endsWith('/*')) return type.startsWith(pattern.slice(0, -1));
  return type === pattern;
}

/** Código do erro (`errors.<código>`) de um ficheiro que não pode ser enviado, ou `null`. */
export function rejectFile(
  file: File,
  allowedTypes: readonly string[],
  maxBytes: number = maxFileBytes,
): FileRejection | null {
  const type = fileType(file);
  if (!allowedTypes.some((allowed) => matches(type, allowed))) return 'FILE_TYPE_NOT_ALLOWED';
  if (file.size === 0) return 'FILE_EMPTY';
  if (file.size > maxBytes) return 'FILE_TOO_LARGE';
  return null;
}

export interface FileUploadButtonProps extends Omit<ButtonProps, 'onClick' | 'component'> {
  /** Texto do botão e nome acessível do `<input>` (em alternativa a `children`). */
  label?: string;
  /** Tipos aceites, como no `<input accept>` (`application/pdf`, `image/png,image/jpeg`). */
  accept: string;
  /** Tipos aceites na verificação do browser (por defeito os de `accept`). */
  allowedTypes?: readonly string[];
  /** Abre a câmara do telemóvel (`capture`). */
  capture?: 'environment' | 'user';
  /** Tamanho máximo em bytes (defeito 10 MB). */
  maxBytes?: number;
  /** Mensagem para um ficheiro grande de mais (defeito `errors.FILE_TOO_LARGE`). */
  tooLargeMessage?: string;
  onFile(file: File): void;
  /**
   * Ficheiro recusado no browser (tipo ou tamanho), sem chamar a API. Sem esta função o botão
   * mostra ele próprio a mensagem por baixo.
   */
  onReject?(code: FileRejection, file: File): void;
  /** Identifica o `<input>` nos testes (defeito `file-input`). */
  inputTestId?: string;
  children?: ReactNode;
}

/** Escolha de um ficheiro, verificado no browser (tipo e tamanho) antes de ir para a API. */
export function FileUploadButton({
  label,
  accept,
  allowedTypes,
  capture,
  maxBytes = maxFileBytes,
  tooLargeMessage,
  onFile,
  onReject,
  inputTestId = 'file-input',
  children,
  variant = 'outlined',
  size = 'small',
  startIcon = <Upload />,
  ...props
}: FileUploadButtonProps) {
  const { t } = useTranslation();
  const input = useRef<HTMLInputElement>(null);
  const [error, setError] = useState<string | null>(null);
  const allowed = allowedTypes ?? accept.split(',');

  const reject = (code: FileRejection, file: File) => {
    if (onReject) {
      onReject(code, file);
      return;
    }
    setError(code === 'FILE_TOO_LARGE' && tooLargeMessage ? tooLargeMessage : t(`errors.${code}`));
  };

  return (
    <>
      <Button
        variant={variant}
        size={size}
        startIcon={startIcon}
        {...props}
        onClick={() => input.current?.click()}
      >
        {children ?? label}
      </Button>
      <input
        ref={input}
        type="file"
        hidden
        accept={accept}
        capture={capture}
        aria-label={label}
        data-testid={inputTestId}
        onChange={(event) => {
          const file = event.target.files?.[0];
          // Limpa para que escolher o mesmo ficheiro outra vez volte a disparar o evento.
          event.target.value = '';
          if (!file) return;
          const rejection = rejectFile(file, allowed, maxBytes);
          if (rejection) {
            reject(rejection, file);
            return;
          }
          setError(null);
          onFile(file);
        }}
      />
      {error && (
        <Typography role="alert" variant="body2" color="error" sx={{ width: '100%' }}>
          {error}
        </Typography>
      )}
    </>
  );
}
