import CheckCircle from '@mui/icons-material/CheckCircle';
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import FormHelperText from '@mui/material/FormHelperText';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState, type ChangeEvent, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { submitMyKyc, type KycCheckResponse } from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import {
  acceptedImageTypes,
  isTooLarge,
  prepareImage,
  type ImageCodec,
} from '../proposal-wizard/imageCompression';
import { CameraCaptureDialog, type GetUserMedia } from './CameraCaptureDialog';

type Part = 'docFront' | 'docBack' | 'selfie';

export interface KycFormProps {
  /** Seguradora a quem se envia a verificação. */
  insurerId: string;
  /** Tipo do documento do perfil; no passaporte não há verso. */
  idDocType?: string;
  onSubmitted(check: KycCheckResponse): void;
  /** Acções extra ao lado de "Enviar" (ex.: "Agora não"). */
  extraActions?: ReactNode;
  getUserMedia?: GetUserMedia;
  codec?: ImageCodec;
}

function FileInputButton({
  label,
  capture,
  onFile,
}: {
  label: string;
  capture: 'user' | 'environment';
  onFile(file: File): void;
}) {
  const onChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (file) onFile(file);
  };
  return (
    <Button variant="outlined" component="label" startIcon={<PhotoCamera />}>
      {label}
      <input
        type="file"
        hidden
        accept={acceptedImageTypes}
        capture={capture}
        aria-label={label}
        data-testid={`kyc-file-${capture}`}
        onChange={onChange}
      />
    </Button>
  );
}

/** Documento (frente e verso) e selfie para a verificação de identidade (S4-01). */
export function KycForm({
  insurerId,
  idDocType,
  onSubmitted,
  extraActions,
  getUserMedia,
  codec,
}: KycFormProps) {
  const { t } = useTranslation();
  const [files, setFiles] = useState<Partial<Record<Part, File>>>({});
  const [errors, setErrors] = useState<Partial<Record<Part, string>>>({});
  const [cameraOpen, setCameraOpen] = useState(false);
  const [cameraFailed, setCameraFailed] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const passport = idDocType === 'PASSPORT';
  const parts: Part[] = passport ? ['docFront', 'selfie'] : ['docFront', 'docBack', 'selfie'];

  function choose(part: Part, file: File) {
    setFiles((current) => ({ ...current, [part]: file }));
    setErrors((current) => ({ ...current, [part]: undefined }));
  }

  async function submit() {
    setMessage(null);
    const missing: Partial<Record<Part, string>> = {};
    for (const part of parts) if (!files[part]) missing[part] = 'REQUIRED';
    setErrors(missing);
    if (Object.keys(missing).length > 0) return;

    setBusy(true);
    try {
      const prepared: Partial<Record<Part, File>> = {};
      for (const part of parts) {
        const file = await prepareImage(files[part]!, codec);
        if (isTooLarge(file)) {
          setErrors({ [part]: 'FILE_TOO_LARGE' });
          return;
        }
        prepared[part] = file;
      }
      const check = await submitMyKyc({
        insurerId,
        docFront: prepared.docFront,
        ...(prepared.docBack ? { docBack: prepared.docBack } : {}),
        selfie: prepared.selfie,
      });
      onSubmitted(check);
    } catch (error) {
      const problem = getProblem(error);
      const fieldErrors: Partial<Record<Part, string>> = {};
      for (const fieldError of problem.errors) {
        if (parts.includes(fieldError.field as Part)) {
          fieldErrors[fieldError.field as Part] = fieldError.code;
        }
      }
      if (Object.keys(fieldErrors).length > 0) setErrors(fieldErrors);
      else setMessage(problemMessage(t, problem));
    } finally {
      setBusy(false);
    }
  }

  const errorText = (code?: string) =>
    code ? (code === 'FILE_TOO_LARGE' ? t('errors.FILE_TOO_LARGE') : fieldMessage(t, code)) : null;

  const chosen = (part: Part) =>
    files[part] ? (
      <Stack direction="row" spacing={0.5} alignItems="center" sx={{ minWidth: 0 }}>
        <CheckCircle color="success" fontSize="small" />
        <Typography variant="body2" noWrap sx={{ minWidth: 0 }}>
          {t('kyc.form.chosen', { name: files[part]!.name })}
        </Typography>
      </Stack>
    ) : null;

  const block = (part: Part, title: string, hint: string, picker: ReactNode) => (
    <Paper variant="outlined" sx={{ p: 2 }} data-testid={`kyc-${part}`}>
      <Stack spacing={1}>
        <Typography variant="subtitle1" component="h3" fontWeight={600}>
          {title}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {hint}
        </Typography>
        {chosen(part)}
        <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
          {picker}
        </Stack>
        {errors[part] && <FormHelperText error>{errorText(errors[part])}</FormHelperText>}
      </Stack>
    </Paper>
  );

  return (
    <Stack spacing={2}>
      {block(
        'docFront',
        t('kyc.form.docFront'),
        t('kyc.form.docHint'),
        <FileInputButton
          label={files.docFront ? t('kyc.form.replace') : t('kyc.form.choose')}
          capture="environment"
          onFile={(file) => choose('docFront', file)}
        />,
      )}
      {!passport &&
        block(
          'docBack',
          t('kyc.form.docBack'),
          t('kyc.form.docHint'),
          <FileInputButton
            label={files.docBack ? t('kyc.form.replace') : t('kyc.form.choose')}
            capture="environment"
            onFile={(file) => choose('docBack', file)}
          />,
        )}
      {block(
        'selfie',
        t('kyc.form.selfie'),
        t('kyc.form.selfieInstruction'),
        cameraFailed ? (
          <FileInputButton
            label={files.selfie ? t('kyc.form.replace') : t('kyc.form.choose')}
            capture="user"
            onFile={(file) => choose('selfie', file)}
          />
        ) : (
          <Button
            variant="outlined"
            startIcon={<PhotoCamera />}
            onClick={() => setCameraOpen(true)}
          >
            {files.selfie ? t('kyc.form.retakeSelfie') : t('kyc.form.takeSelfie')}
          </Button>
        ),
      )}
      {cameraFailed && <Alert severity="info">{t('kyc.form.cameraUnavailable')}</Alert>}

      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}

      <Stack direction="row" spacing={1.5} useFlexGap flexWrap="wrap" alignItems="center">
        <Button variant="contained" disabled={busy} onClick={() => void submit()}>
          {t('kyc.form.submit')}
        </Button>
        {extraActions}
      </Stack>

      <CameraCaptureDialog
        open={cameraOpen}
        onClose={() => setCameraOpen(false)}
        onCapture={(file) => choose('selfie', file)}
        onUnavailable={() => {
          setCameraOpen(false);
          setCameraFailed(true);
        }}
        getUserMedia={getUserMedia}
      />
    </Stack>
  );
}
