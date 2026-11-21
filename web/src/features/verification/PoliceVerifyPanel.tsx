import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useMutation } from '@tanstack/react-query';
import { lazy, Suspense, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { verifyCertificateTokenAsPolice } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { PublicVerifyForm } from './PublicVerifyForm';
import { VerificationResultCard } from './VerificationResultCard';

/** O leitor (e o `@zxing`) só se carrega quando se abre o separador "Ler QR". */
const QrScanner = lazy(() => import('./QrScanner').then((m) => ({ default: m.QrScanner })));

type PoliceTab = 'plate' | 'qr' | 'history';

/** Área da polícia em `/verificar` (S6-05 §5): matrícula sem CAPTCHA, leitura do QR e histórico. */
export function PoliceVerifyPanel() {
  const { t } = useTranslation();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const [tab, setTab] = useState<PoliceTab>('plate');
  const [cameraError, setCameraError] = useState(false);
  // A chave recria o leitor ("Nova verificação" volta a ligar a câmara).
  const [scan, setScan] = useState(0);

  const byToken = useMutation({
    mutationFn: (token: string) => verifyCertificateTokenAsPolice(token),
  });

  const changeTab = (next: PoliceTab) => {
    setTab(next);
    if (next === 'qr') {
      setCameraError(false);
      byToken.reset();
      setScan((n) => n + 1);
    }
  };

  let qrContent;
  if (byToken.isPending) {
    qrContent = (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <CircularProgress aria-label={t('verify.loading')} />
      </Box>
    );
  } else if (byToken.data) {
    qrContent = (
      <VerificationResultCard
        result={byToken.data}
        police
        onNew={() => {
          byToken.reset();
          setScan((n) => n + 1);
        }}
      />
    );
  } else if (byToken.error) {
    qrContent = (
      <Alert severity="error" role="alert">
        {problemMessage(t, getProblem(byToken.error))}
      </Alert>
    );
  } else {
    qrContent = (
      <Suspense
        fallback={
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress aria-label={t('police.qr.starting')} />
          </Box>
        }
      >
        <QrScanner
          key={scan}
          onToken={(token) => byToken.mutate(token)}
          onCameraError={() => {
            setCameraError(true);
            setTab('plate');
          }}
        />
      </Suspense>
    );
  }

  return (
    <Stack spacing={2}>
      <Tabs
        value={tab}
        onChange={(_, value: PoliceTab) => changeTab(value)}
        variant={narrow ? 'fullWidth' : 'standard'}
        aria-label={t('police.title')}
      >
        <Tab value="plate" label={t('police.tabs.plate')} />
        <Tab value="qr" label={t('police.tabs.qr')} />
        <Tab value="history" label={t('police.tabs.history')} />
      </Tabs>
      {cameraError && tab === 'plate' && (
        <Alert severity="warning" role="alert" onClose={() => setCameraError(false)}>
          {t('police.qr.cameraError')}
        </Alert>
      )}
      {tab === 'plate' && <PublicVerifyForm mode="police" />}
      {tab === 'qr' && qrContent}
      {tab === 'history' && (
        <Link component={RouterLink} to="/verificar/historico">
          {t('police.historyLink')}
        </Link>
      )}
    </Stack>
  );
}
