import Download from '@mui/icons-material/Download';
import Fullscreen from '@mui/icons-material/Fullscreen';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CircularProgress from '@mui/material/CircularProgress';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { QRCodeSVG } from 'qrcode.react';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { PolicyDetailResponse, PolicyDocument } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { downloadFile } from '../../files/downloadFile';
import { formatDate } from '../../i18n/format';
import { documentFileName } from '../policies/common/DocumentsList';

/** O documento do certificado, se já estiver pronto para descarregar. */
export function readyCertificateDocument(
  policy: Pick<PolicyDetailResponse, 'documents'>,
): PolicyDocument | undefined {
  const document = policy.documents.find((d) => d.type === 'CERTIFICATE');
  return document?.status === 'READY' && document.fileId ? document : undefined;
}

/**
 * Certificado da apólice (S6-06 §3): QR de verificação, número e validade, ecrã inteiro e
 * download do PDF (ou "A preparar o certificado…" enquanto o documento estiver `PENDING`).
 */
export function CertificateCard({
  policy,
  onShowFullscreen,
}: {
  policy: PolicyDetailResponse;
  onShowFullscreen: () => void;
}) {
  const { t, i18n } = useTranslation();
  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const certificate = policy.certificate;
  const document = readyCertificateDocument(policy);

  async function download(doc: PolicyDocument) {
    if (!doc.fileId) return;
    setDownloading(true);
    setError(null);
    try {
      await downloadFile(doc.fileId, documentFileName(doc));
    } catch (e) {
      setError(problemMessage(t, getProblem(e)));
    } finally {
      setDownloading(false);
    }
  }

  let body;
  if (certificate?.status === 'REVOKED') {
    body = <Alert severity="warning">{t('certificate.revoked')}</Alert>;
  } else if (!certificate) {
    body = (
      <Stack direction="row" spacing={1} alignItems="center">
        <CircularProgress size={16} />
        <Typography color="text.secondary">{t('certificate.preparing')}</Typography>
      </Stack>
    );
  } else {
    body = (
      <Stack spacing={2} alignItems="center">
        <Box
          sx={{ bgcolor: '#FFFFFF', borderRadius: 1, lineHeight: 0 }}
          data-testid="certificate-qr"
          data-value={certificate.verificationUrl}
        >
          <QRCodeSVG
            value={certificate.verificationUrl}
            level="M"
            size={160}
            includeMargin
            role="img"
            aria-label={t('certificate.qrLabel')}
          />
        </Box>
        <Stack spacing={0.5} sx={{ textAlign: 'center' }}>
          <Typography fontWeight={600}>
            {t('certificate.number', { number: certificate.number })}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {t('certificate.validity', {
              validFrom: formatDate(certificate.validFrom, i18n.language),
              validTo: formatDate(certificate.validTo, i18n.language),
            })}
          </Typography>
        </Stack>
        <Stack spacing={1} sx={{ width: '100%' }}>
          <Button variant="contained" startIcon={<Fullscreen />} onClick={onShowFullscreen}>
            {t('certificate.showFullscreen')}
          </Button>
          {document ? (
            <Button
              variant="outlined"
              startIcon={downloading ? <CircularProgress size={16} /> : <Download />}
              disabled={downloading}
              onClick={() => void download(document)}
            >
              {t('certificate.download')}
            </Button>
          ) : (
            <Stack direction="row" spacing={1} alignItems="center" justifyContent="center">
              <CircularProgress size={16} />
              <Typography variant="body2" color="text.secondary">
                {t('certificate.preparing')}
              </Typography>
            </Stack>
          )}
          {error && (
            <Alert severity="error" role="alert" onClose={() => setError(null)}>
              {error}
            </Alert>
          )}
        </Stack>
      </Stack>
    );
  }

  return (
    <Card variant="outlined" elevation={0} data-testid="certificate-card">
      <CardContent>
        <Typography variant="h6" component="h2" sx={{ mb: 2 }}>
          {t('certificate.title')}
        </Typography>
        {body}
      </CardContent>
    </Card>
  );
}
