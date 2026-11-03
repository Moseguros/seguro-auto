import FolderOpen from '@mui/icons-material/FolderOpen';
import Image from '@mui/icons-material/Image';
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import PictureAsPdf from '@mui/icons-material/PictureAsPdf';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import LinearProgress from '@mui/material/LinearProgress';
import Skeleton from '@mui/material/Skeleton';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  uploadMyVehicleRegistrationDoc,
  type RegistrationDocResponse,
  type VehicleResponse,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { FileOpenButton } from '../../files/FileOpenButton';
import { FileUploadButton, type FileRejection } from '../../files/FileUploadButton';
import { useFileBlob } from '../../files/useFileBlob';
import { formatDate } from '../../i18n/format';

/** Tipos aceites pelo servidor para o livrete (S2-01). */
export const registrationDocTypes = [
  'image/jpeg',
  'image/png',
  'image/heic',
  'application/pdf',
] as const;

/** Imagens que os browsers mostram (HEIC não). */
const previewTypes = ['image/jpeg', 'image/png'];

/** `812 KB`, `1,2 MB`. */
export function formatBytes(bytes: number, language: string): string {
  const number = (value: number, digits: number) =>
    new Intl.NumberFormat(language === 'en' ? 'en' : 'pt-MZ', {
      maximumFractionDigits: digits,
    }).format(value);
  if (bytes < 1024 * 1024) return `${number(Math.max(1, Math.round(bytes / 1024)), 0)} KB`;
  return `${number(bytes / (1024 * 1024), 1)} MB`;
}

export interface RegistrationDocSectionProps {
  vehicle: VehicleResponse;
  onUpdated(vehicle: VehicleResponse): void;
}

/** Livrete do veículo: miniatura ou ícone, "Abrir", e envio por foto ou ficheiro. */
export function RegistrationDocSection({ vehicle, onUpdated }: RegistrationDocSectionProps) {
  const { t } = useTranslation();
  const [progress, setProgress] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);
  const doc = vehicle.registrationDoc;

  const reject = (code: FileRejection) => setError(t(`errors.${code}`));

  const upload = async (file: File) => {
    setError(null);
    setProgress(0);
    try {
      const next = await uploadMyVehicleRegistrationDoc(
        vehicle.id,
        { file },
        {
          onUploadProgress: (event) => {
            if (event.total) setProgress(Math.round((event.loaded / event.total) * 100));
          },
        },
      );
      onUpdated(next);
      setSaved(true);
    } catch (e) {
      setError(problemMessage(t, getProblem(e)));
    } finally {
      setProgress(null);
    }
  };

  const uploading = progress !== null;
  const common = {
    accept: '',
    allowedTypes: registrationDocTypes,
    onFile: (file: File) => void upload(file),
    onReject: reject,
    disabled: uploading,
    variant: 'outlined' as const,
  };

  return (
    <Card component="section" aria-labelledby="registration-doc-title">
      <CardContent>
        <Stack spacing={2}>
          <Typography variant="h6" component="h2" id="registration-doc-title">
            {t('vehicles.doc.title')}
          </Typography>

          {doc ? (
            <DocumentPreview doc={doc} />
          ) : (
            <Typography color="text.secondary">{t('vehicles.doc.none')}</Typography>
          )}

          {uploading && (
            <Stack spacing={0.5}>
              <Typography variant="body2">{t('vehicles.doc.uploading')}</Typography>
              <LinearProgress
                variant="determinate"
                value={progress}
                aria-label={t('vehicles.doc.uploading')}
              />
            </Stack>
          )}
          {error && (
            <Alert severity="error" role="alert" onClose={() => setError(null)}>
              {error}
            </Alert>
          )}

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
            <FileUploadButton
              {...common}
              accept="image/*"
              capture="environment"
              startIcon={<PhotoCamera />}
              inputTestId="registration-doc-camera"
              aria-label={doc ? t('vehicles.doc.replacePhoto') : undefined}
            >
              {doc ? t('vehicles.doc.replace') : t('vehicles.doc.takePhoto')}
            </FileUploadButton>
            <FileUploadButton
              {...common}
              accept={registrationDocTypes.join(',')}
              startIcon={<FolderOpen />}
              inputTestId="registration-doc-file"
              aria-label={doc ? t('vehicles.doc.replaceFile') : undefined}
            >
              {doc ? t('vehicles.doc.replace') : t('vehicles.doc.chooseFile')}
            </FileUploadButton>
          </Stack>
        </Stack>
      </CardContent>
      <Snackbar
        open={saved}
        autoHideDuration={4000}
        onClose={() => setSaved(false)}
        message={t('vehicles.doc.saved')}
      />
    </Card>
  );
}

function DocumentPreview({ doc }: { doc: RegistrationDocResponse }) {
  const { t, i18n } = useTranslation();
  const image = previewTypes.includes(doc.contentType);
  const blob = useFileBlob(image ? doc.fileId : null);
  const details = `${formatBytes(doc.sizeBytes, i18n.language)} · ${t('vehicles.doc.uploadedAt', {
    date: formatDate(doc.uploadedAt, i18n.language),
  })}`;

  if (image) {
    return (
      <Stack spacing={1} alignItems="flex-start">
        {blob.url ? (
          <Box
            component="img"
            src={blob.url}
            alt={t('vehicles.doc.alt')}
            sx={{ maxWidth: '100%', maxHeight: 360, borderRadius: 1, display: 'block' }}
          />
        ) : blob.error ? (
          <Alert severity="error">{problemMessage(t, getProblem(blob.error))}</Alert>
        ) : (
          <Skeleton variant="rectangular" width={240} height={160} sx={{ maxWidth: '100%' }} />
        )}
        <Typography variant="body2" color="text.secondary">
          {details}
        </Typography>
        <FileOpenButton url={blob.url} label={t('vehicles.doc.open')} disabled={!blob.url} />
      </Stack>
    );
  }

  const Icon = doc.contentType === 'application/pdf' ? PictureAsPdf : Image;
  return (
    <Stack direction="row" spacing={2} alignItems="center" useFlexGap flexWrap="wrap">
      <Icon fontSize="large" color="action" data-testid="registration-doc-icon" />
      <Stack sx={{ minWidth: 0, flexGrow: 1 }}>
        <Typography>
          {t(`vehicles.doc.type.${doc.contentType === 'application/pdf' ? 'pdf' : 'heic'}`)}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {details}
        </Typography>
      </Stack>
      <FileOpenButton fileId={doc.fileId} label={t('vehicles.doc.open')} />
    </Stack>
  );
}
