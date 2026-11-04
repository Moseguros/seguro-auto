import PhotoCamera from '@mui/icons-material/PhotoCamera';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import LinearProgress from '@mui/material/LinearProgress';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useRef, useState, type ChangeEvent } from 'react';
import { useTranslation } from 'react-i18next';
import type { InspectionPhotoResponse, PhotoSlot } from '../../api/generated';
import { ProtectedImage } from '../../files/ProtectedImage';
import { acceptedImageTypes } from './imageCompression';

/** Envio de uma fotografia: `clientPhotoId` repete-se em "Tentar de novo" (idempotente). */
export type PhotoUpload = (
  file: File,
  clientPhotoId: string,
  onProgress: (percent: number) => void,
) => Promise<void>;

/** Erro de envio com a mensagem já traduzida. */
export class PhotoUploadError extends Error {}

export interface PhotoSlotCardProps {
  slot: PhotoSlot;
  photo?: InspectionPhotoResponse;
  /** Ângulo obrigatório sem fotografia (marcado). */
  missing?: boolean;
  /** Ângulo a repetir (vistoria recusada). */
  retake?: boolean;
  /** Sem alterações (vistoria enviada ou aprovada). */
  readOnly?: boolean;
  upload: PhotoUpload;
  /** Só nas fotografias extra: "Retirar". */
  onRemove?: () => void;
  /** Título (por defeito o nome do ângulo). */
  title?: string;
}

function newId(): string {
  return crypto.randomUUID();
}

/** Botão que abre a câmara traseira ou a galeria. */
export function PhotoPickerButton({
  label,
  disabled,
  onFile,
  variant = 'outlined',
}: {
  label: string;
  disabled?: boolean;
  onFile(file: File): void;
  variant?: 'outlined' | 'text' | 'contained';
}) {
  const inputRef = useRef<HTMLInputElement>(null);
  const onChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (file) onFile(file);
  };
  return (
    <Button variant={variant} component="label" startIcon={<PhotoCamera />} disabled={disabled}>
      {label}
      <input
        ref={inputRef}
        type="file"
        hidden
        accept={acceptedImageTypes}
        capture="environment"
        aria-label={label}
        onChange={onChange}
      />
    </Button>
  );
}

/** Um ângulo da vistoria: instrução, miniatura e envio com progresso. */
export function PhotoSlotCard({
  slot,
  photo,
  missing,
  retake,
  readOnly,
  upload,
  onRemove,
  title,
}: PhotoSlotCardProps) {
  const { t } = useTranslation();
  const [progress, setProgress] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState<{ file: File; id: string } | null>(null);
  const name = title ?? t(`inspection.slot.${slot}`);

  async function send(file: File, id: string) {
    setPending({ file, id });
    setError(null);
    setProgress(0);
    try {
      await upload(file, id, (percent) => setProgress(percent));
      setPending(null);
    } catch (failure) {
      setError(
        failure instanceof PhotoUploadError
          ? failure.message
          : t('proposal.inspection.uploadFailed'),
      );
    } finally {
      setProgress(null);
    }
  }

  const busy = progress !== null;
  const highlighted = !!missing || !!retake;

  return (
    <Card
      variant="outlined"
      elevation={0}
      data-testid={`photo-slot-${slot}`}
      data-missing={missing ? 'true' : undefined}
      sx={{ borderColor: highlighted ? 'error.main' : undefined, borderWidth: highlighted ? 2 : 1 }}
    >
      <CardContent>
        <Stack spacing={1.5}>
          <Stack spacing={0.25}>
            <Typography variant="subtitle1" component="h3" fontWeight={600}>
              {name}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t(`inspection.instruction.${slot}`)}
            </Typography>
          </Stack>
          {photo ? (
            <ProtectedImage
              fileId={photo.fileId}
              alt={name}
              height="auto"
              sx={{ aspectRatio: '4 / 3', width: '100%' }}
            />
          ) : (
            <Box
              sx={{
                aspectRatio: '4 / 3',
                width: '100%',
                borderRadius: 1,
                bgcolor: 'action.hover',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: 'text.secondary',
              }}
            >
              <PhotoCamera fontSize="large" />
            </Box>
          )}
          {missing && (
            <Typography variant="body2" color="error">
              {t('proposal.inspection.missing')}
            </Typography>
          )}
          {retake && (
            <Typography variant="body2" color="error">
              {t('proposal.inspection.retake')}
            </Typography>
          )}
          {busy && (
            <LinearProgress
              variant="determinate"
              value={progress ?? 0}
              aria-label={t('proposal.inspection.uploading', { slot: name })}
            />
          )}
          {error && (
            <Alert
              severity="error"
              role="alert"
              action={
                pending ? (
                  <Button
                    color="inherit"
                    size="small"
                    onClick={() => void send(pending.file, pending.id)}
                  >
                    {t('common.retry')}
                  </Button>
                ) : undefined
              }
            >
              {error}
            </Alert>
          )}
          {!readOnly && (
            <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
              {onRemove ? (
                <Button variant="text" color="error" onClick={onRemove} disabled={busy}>
                  {t('proposal.inspection.remove')}
                </Button>
              ) : (
                <PhotoPickerButton
                  label={photo ? t('proposal.inspection.replace') : t('proposal.inspection.choose')}
                  disabled={busy}
                  onFile={(file) => void send(file, newId())}
                />
              )}
            </Stack>
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
