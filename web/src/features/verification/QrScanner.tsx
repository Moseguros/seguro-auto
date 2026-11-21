import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { BrowserQRCodeReader, type IScannerControls } from '@zxing/browser';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { parseVerificationUrl } from './parseVerificationUrl';

/**
 * Leitor do QR do certificado com a câmara traseira (S6-05 §5). Carregado à parte (`@zxing` no
 * bloco `zxing`), só quando a polícia abre o separador "Ler QR". Pára a câmara ao ler um código
 * válido e ao sair (desmontar).
 */
export function QrScanner({
  onToken,
  onCameraError,
}: {
  onToken(token: string): void;
  onCameraError(): void;
}) {
  const { t } = useTranslation();
  const video = useRef<HTMLVideoElement>(null);
  const [notCertificate, setNotCertificate] = useState(false);
  const [starting, setStarting] = useState(true);
  const callbacks = useRef({ onToken, onCameraError });
  callbacks.current = { onToken, onCameraError };

  useEffect(() => {
    let controls: IScannerControls | undefined;
    let stopped = false;
    const stop = () => {
      stopped = true;
      controls?.stop();
    };
    const reader = new BrowserQRCodeReader();
    const element = video.current;
    if (!element) return undefined;
    reader
      .decodeFromConstraints(
        { video: { facingMode: 'environment' } },
        element,
        (result, _error, scanner) => {
          if (!result || stopped) return;
          const token = parseVerificationUrl(result.getText());
          if (!token) {
            setNotCertificate(true);
            return;
          }
          stopped = true;
          scanner.stop();
          callbacks.current.onToken(token);
        },
      )
      .then((started) => {
        controls = started;
        setStarting(false);
        if (stopped) started.stop();
      })
      .catch(() => {
        if (!stopped) callbacks.current.onCameraError();
      });
    return stop;
  }, []);

  return (
    <Stack spacing={1.5}>
      <Typography variant="body2">{t('police.qr.hint')}</Typography>
      <Box sx={{ position: 'relative', width: '100%', bgcolor: 'common.black', borderRadius: 1 }}>
        <Box
          component="video"
          ref={video}
          muted
          playsInline
          aria-label={t('police.qr.video')}
          sx={{ display: 'block', width: '100%', borderRadius: 1 }}
        />
        {/* Moldura quadrada desenhada por cima do vídeo. */}
        <Box
          aria-hidden
          sx={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            width: '60%',
            aspectRatio: '1 / 1',
            maxHeight: '80%',
            transform: 'translate(-50%, -50%)',
            border: '3px solid',
            borderColor: 'common.white',
            borderRadius: 2,
            boxShadow: '0 0 0 9999px rgba(0, 0, 0, 0.35)',
            pointerEvents: 'none',
          }}
        />
      </Box>
      {starting && (
        <Typography variant="caption" color="text.secondary">
          {t('police.qr.starting')}
        </Typography>
      )}
      {notCertificate && (
        <Alert severity="warning" role="alert">
          {t('police.qr.notCertificate')}
        </Alert>
      )}
    </Stack>
  );
}
