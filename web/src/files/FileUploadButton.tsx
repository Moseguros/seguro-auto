import Button, { type ButtonProps } from '@mui/material/Button';
import { useRef } from 'react';

/** Limite do servidor (S2-01). */
export const maxUploadBytes = 10 * 1024 * 1024;

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
  if (pattern.endsWith('/*')) return type.startsWith(pattern.slice(0, -1));
  return type === pattern;
}

/** Código do erro (`errors.<código>`) de um ficheiro que não pode ser enviado, ou `null`. */
export function rejectFile(
  file: File,
  allowedTypes: readonly string[],
  maxBytes: number = maxUploadBytes,
): FileRejection | null {
  const type = fileType(file);
  if (!allowedTypes.some((allowed) => matches(type, allowed))) return 'FILE_TYPE_NOT_ALLOWED';
  if (file.size === 0) return 'FILE_EMPTY';
  if (file.size > maxBytes) return 'FILE_TOO_LARGE';
  return null;
}

export interface FileUploadButtonProps extends Omit<ButtonProps, 'onClick' | 'component'> {
  /** Atributo `accept` do `<input type="file">`. */
  accept: string;
  /** Tipos aceites na verificação do browser (por defeito os de `accept`). */
  allowedTypes?: readonly string[];
  /** Abre a câmara do telemóvel (`capture`). */
  capture?: 'environment' | 'user';
  maxBytes?: number;
  onFile(file: File): void;
  /** Ficheiro recusado no browser (tipo ou tamanho), sem chamar a API. */
  onReject(code: FileRejection, file: File): void;
  /** Identifica o `<input>` nos testes. */
  inputTestId?: string;
}

/** Botão que abre a escolha de ficheiro e verifica o tipo e o tamanho antes de o devolver. */
export function FileUploadButton({
  accept,
  allowedTypes,
  capture,
  maxBytes = maxUploadBytes,
  onFile,
  onReject,
  inputTestId,
  children,
  ...props
}: FileUploadButtonProps) {
  const input = useRef<HTMLInputElement>(null);
  const allowed = allowedTypes ?? accept.split(',');
  return (
    <>
      <Button {...props} onClick={() => input.current?.click()}>
        {children}
      </Button>
      <input
        ref={input}
        type="file"
        hidden
        accept={accept}
        capture={capture}
        data-testid={inputTestId}
        onChange={(event) => {
          const file = event.target.files?.[0];
          // Limpa para que escolher o mesmo ficheiro outra vez volte a disparar o evento.
          event.target.value = '';
          if (!file) return;
          const rejection = rejectFile(file, allowed, maxBytes);
          if (rejection) onReject(rejection, file);
          else onFile(file);
        }}
      />
    </>
  );
}
