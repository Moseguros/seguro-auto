import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useEffect, useMemo, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';

/** `navigator.mediaDevices.getUserMedia` (os testes passam um falso). */
export type GetUserMedia = (constraints: MediaStreamConstraints) => Promise<MediaStream>;

/** A câmara do browser, se existir. */
export function browserGetUserMedia(): GetUserMedia | undefined {
  const devices = typeof navigator === 'undefined' ? undefined : navigator.mediaDevices;
  return devices?.getUserMedia ? (c) => devices.getUserMedia(c) : undefined;
}

export interface CameraCaptureDialogProps {
  open: boolean;
  onClose(): void;
  /** Fotografia aceite ("Usar"). */
  onCapture(file: File): void;
  /** Sem câmara ou sem permissão: o formulário passa ao ficheiro. */
  onUnavailable(): void;
  getUserMedia?: GetUserMedia;
}

function stop(stream: MediaStream | null) {
  stream?.getTracks().forEach((track) => track.stop());
}

/** Selfie pela câmara frontal: pré-visualização, "Tirar fotografia", "Repetir", "Usar". */
export function CameraCaptureDialog({
  open,
  onClose,
  onCapture,
  onUnavailable,
  getUserMedia,
}: CameraCaptureDialogProps) {
  const { t } = useTranslation();
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const [starting, setStarting] = useState(false);
  const [shot, setShot] = useState<{ file: File; preview: string } | null>(null);
  const media = useMemo(() => getUserMedia ?? browserGetUserMedia(), [getUserMedia]);
  const unavailableRef = useRef(onUnavailable);
  unavailableRef.current = onUnavailable;

  useEffect(() => {
    if (!open || shot) return;
    if (!media) {
      unavailableRef.current();
      return;
    }
    let active = true;
    setStarting(true);
    media({ video: { facingMode: 'user' }, audio: false })
      .then((stream) => {
        if (!active) {
          stop(stream);
          return;
        }
        streamRef.current = stream;
        const video = videoRef.current;
        if (video) {
          video.srcObject = stream;
          void video.play?.()?.catch?.(() => undefined);
        }
      })
      .catch(() => {
        if (active) unavailableRef.current();
      })
      .finally(() => {
        if (active) setStarting(false);
      });
    return () => {
      active = false;
      stop(streamRef.current);
      streamRef.current = null;
    };
  }, [open, shot, media]);

  function close() {
    stop(streamRef.current);
    streamRef.current = null;
    setShot(null);
    onClose();
  }

  function take() {
    const video = videoRef.current;
    if (!video || !video.videoWidth) return;
    const canvas = document.createElement('canvas');
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext('2d')?.drawImage(video, 0, 0);
    const preview = canvas.toDataURL('image/jpeg', 0.85);
    canvas.toBlob(
      (blob) => {
        if (!blob) return;
        setShot({ file: new File([blob], 'selfie.jpg', { type: 'image/jpeg' }), preview });
      },
      'image/jpeg',
      0.85,
    );
  }

  return (
    <Dialog open={open} onClose={close} fullScreen={fullScreen} fullWidth maxWidth="sm">
      <DialogTitle>{t('kyc.form.selfieTitle')}</DialogTitle>
      <DialogContent>
        <Typography sx={{ mb: 2 }}>{t('kyc.form.selfieInstruction')}</Typography>
        <Box
          sx={{
            position: 'relative',
            width: '100%',
            aspectRatio: '3 / 4',
            maxHeight: '60vh',
            bgcolor: 'grey.900',
            borderRadius: 1,
            overflow: 'hidden',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          {shot ? (
            <Box
              component="img"
              src={shot.preview}
              alt={t('kyc.form.selfie')}
              sx={{ width: '100%', height: '100%', objectFit: 'cover' }}
            />
          ) : (
            <Box
              component="video"
              ref={videoRef}
              autoPlay
              playsInline
              muted
              aria-label={t('kyc.form.cameraPreview')}
              sx={{ width: '100%', height: '100%', objectFit: 'cover', transform: 'scaleX(-1)' }}
            />
          )}
          {starting && (
            <CircularProgress sx={{ position: 'absolute' }} aria-label={t('auth.loading')} />
          )}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={close}>{t('common.cancel')}</Button>
        {shot ? (
          <>
            <Button onClick={() => setShot(null)}>{t('kyc.form.retake')}</Button>
            <Button
              variant="contained"
              onClick={() => {
                const file = shot.file;
                close();
                onCapture(file);
              }}
            >
              {t('kyc.form.use')}
            </Button>
          </>
        ) : (
          <Button variant="contained" onClick={take} disabled={starting}>
            {t('kyc.form.take')}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
