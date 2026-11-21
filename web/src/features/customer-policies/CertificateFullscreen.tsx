import Close from '@mui/icons-material/Close';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { QRCodeSVG } from 'qrcode.react';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import type { PolicyDetailResponse } from '../../api/generated';
import { formatDate } from '../../i18n/format';

type WakeLockSentinelLike = { release(): Promise<void> };
type WakeLockLike = { request(type: 'screen'): Promise<WakeLockSentinelLike> };

/**
 * Mantém o ecrã ligado enquanto `active` (Screen Wake Lock API, quando existir); os erros
 * ignoram-se. Liberta-o ao fechar.
 */
function useScreenWakeLock(active: boolean) {
  useEffect(() => {
    if (!active) return;
    const wakeLock = (navigator as Navigator & { wakeLock?: WakeLockLike }).wakeLock;
    if (!wakeLock) return;
    let sentinel: WakeLockSentinelLike | undefined;
    let cancelled = false;
    wakeLock
      .request('screen')
      .then((s) => {
        if (cancelled) void s.release().catch(() => undefined);
        else sentinel = s;
      })
      .catch(() => undefined);
    return () => {
      cancelled = true;
      if (sentinel) void sentinel.release().catch(() => undefined);
    };
  }, [active]);
}

// Fundo branco e texto escuro também no modo escuro: o QR lê-se melhor.
const ink = '#142D4E';

/**
 * Certificado em ecrã inteiro (S6-06 §3) para o agente ler o QR: o maior QR possível, a
 * matrícula, a seguradora, a validade e o tipo de cobertura. Fecha com "Fechar" ou Esc.
 */
export function CertificateFullscreen({
  policy,
  open,
  onClose,
}: {
  policy: PolicyDetailResponse;
  open: boolean;
  onClose: () => void;
}) {
  const { t, i18n } = useTranslation();
  useScreenWakeLock(open);
  const certificate = policy.certificate;
  if (!certificate) return null;
  return (
    <Dialog
      fullScreen
      open={open}
      onClose={onClose}
      aria-label={t('certificate.title')}
      PaperProps={{ sx: { bgcolor: '#FFFFFF', color: ink, backgroundImage: 'none' } }}
    >
      <Stack direction="row" justifyContent="flex-end" sx={{ p: 1 }}>
        <Button onClick={onClose} startIcon={<Close />} sx={{ color: ink }}>
          {t('common.close')}
        </Button>
      </Stack>
      <Stack
        spacing={2}
        alignItems="center"
        sx={{ px: 2, pb: 4, textAlign: 'center', overflowY: 'auto' }}
      >
        <Box
          sx={{ width: 'min(90vw, 70vh)', aspectRatio: '1 / 1', lineHeight: 0 }}
          data-testid="certificate-fullscreen-qr"
          data-value={certificate.verificationUrl}
        >
          <QRCodeSVG
            value={certificate.verificationUrl}
            level="M"
            includeMargin
            bgColor="#FFFFFF"
            fgColor="#000000"
            style={{ width: '100%', height: '100%' }}
            role="img"
            aria-label={t('certificate.qrLabel')}
          />
        </Box>
        <Typography
          component="p"
          sx={{
            fontSize: { xs: '2rem', sm: '2.5rem' },
            fontWeight: 800,
            letterSpacing: 1,
            wordBreak: 'break-word',
          }}
        >
          {policy.vehicle.plate}
        </Typography>
        {policy.insurer.name && (
          <Typography sx={{ fontSize: '1.25rem', fontWeight: 600 }}>
            {policy.insurer.name}
          </Typography>
        )}
        <Typography sx={{ fontSize: '1.125rem' }}>
          {t('certificate.validUntil', { date: formatDate(certificate.validTo, i18n.language) })}
        </Typography>
        <Typography>{t(`coverageType.${policy.coverageType}`)}</Typography>
        <Typography variant="body2" sx={{ maxWidth: 420 }}>
          {t('certificate.agentHint')}
        </Typography>
      </Stack>
    </Dialog>
  );
}
