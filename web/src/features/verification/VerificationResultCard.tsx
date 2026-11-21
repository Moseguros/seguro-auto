import type { SvgIconComponent } from '@mui/icons-material';
import Cancel from '@mui/icons-material/Cancel';
import EventBusy from '@mui/icons-material/EventBusy';
import HelpOutline from '@mui/icons-material/HelpOutline';
import PauseCircle from '@mui/icons-material/PauseCircle';
import Schedule from '@mui/icons-material/Schedule';
import VerifiedUser from '@mui/icons-material/VerifiedUser';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import type {
  PoliceVerificationResponse,
  PublicVerificationResponse,
  VerificationResult,
} from '../../api/generated';
import { formatDateTimeNumeric } from '../../i18n/format';

type Tone = 'success' | 'info' | 'error' | 'warning';

/** Cor (do tema) e ícone de cada estado (S6-05 §3): a cor nunca é a única pista. */
export const statusLook: Record<VerificationResult, { tone: Tone; icon: SvgIconComponent }> = {
  VALID: { tone: 'success', icon: VerifiedUser },
  NOT_YET_VALID: { tone: 'info', icon: Schedule },
  EXPIRED: { tone: 'error', icon: EventBusy },
  SUSPENDED: { tone: 'error', icon: PauseCircle },
  CANCELLED: { tone: 'error', icon: Cancel },
  NOT_FOUND: { tone: 'warning', icon: HelpOutline },
};

/** Resposta pública ou da polícia (a da polícia tem campos a mais). */
export type VerificationView = PublicVerificationResponse & Partial<PoliceVerificationResponse>;

/** Ícone e título do estado (versão compacta, usada no histórico da polícia). */
export function VerificationStatusLabel({
  status,
  variant = 'body2',
}: {
  status: VerificationResult;
  variant?: 'body2' | 'h6';
}) {
  const { t } = useTranslation();
  const { tone, icon: Icon } = statusLook[status];
  return (
    <Stack
      direction="row"
      spacing={1}
      alignItems="center"
      data-testid="verification-status"
      data-status={status}
    >
      {/* Os ícones do MUI têm `data-testid` próprio (ex.: `VerifiedUserIcon`). */}
      <Icon color={tone} />
      <Typography
        variant={variant}
        component={variant === 'h6' ? 'h2' : 'span'}
        fontWeight={600}
        sx={{ color: `${tone}.main` }}
      >
        {t(`verificationStatus.${status}`)}
      </Typography>
    </Stack>
  );
}

function Row({ label, children }: { label: string; children: ReactNode }) {
  return (
    <Stack spacing={0} sx={{ minWidth: 0 }}>
      <Typography variant="caption" color="text.secondary" component="dt">
        {label}
      </Typography>
      <Typography variant="body1" component="dd" sx={{ m: 0, wordBreak: 'break-word' }}>
        {children}
      </Typography>
    </Stack>
  );
}

/**
 * Resultado de uma verificação (S6-05 §3): ícone, cor e título por estado e as linhas que existem.
 * `police` acrescenta os números da apólice e do certificado e o veículo; `compact` mostra só o
 * ícone e o título.
 */
export function VerificationResultCard({
  result,
  police = false,
  compact = false,
  onNew,
}: {
  result: VerificationView;
  police?: boolean;
  compact?: boolean;
  onNew?: () => void;
}) {
  const { t } = useTranslation();
  if (compact) return <VerificationStatusLabel status={result.status} />;
  const { tone } = statusLook[result.status];
  const vehicle = [result.vehicleMake, result.vehicleModel].filter(Boolean).join(' ');
  return (
    <Card
      variant="outlined"
      elevation={0}
      data-testid="verification-result"
      sx={{ borderLeftWidth: 6, borderLeftColor: `${tone}.main`, minWidth: 0 }}
    >
      <CardContent>
        <Stack spacing={2} role="status" aria-live="polite">
          <VerificationStatusLabel status={result.status} variant="h6" />
          {result.status === 'NOT_FOUND' && (
            <Typography variant="body2">{t('verify.notFoundHint')}</Typography>
          )}
          <Stack component="dl" spacing={1.5} sx={{ m: 0 }}>
            {result.plateMasked && <Row label={t('verify.rowPlate')}>{result.plateMasked}</Row>}
            {result.insurerName && <Row label={t('verify.rowInsurer')}>{result.insurerName}</Row>}
            {result.validFrom && result.validTo && (
              <Row label={t('verify.rowValidity')}>
                {t('verify.validity', {
                  validFrom: formatDateTimeNumeric(result.validFrom),
                  validTo: formatDateTimeNumeric(result.validTo),
                })}
              </Row>
            )}
            {result.coverageType && (
              <Row label={t('verify.rowCoverage')}>{t(`coverageType.${result.coverageType}`)}</Row>
            )}
            {police && result.policyNumber && (
              <Row label={t('police.policyNumber')}>{result.policyNumber}</Row>
            )}
            {police && result.certificateNumber && (
              <Row label={t('police.certificateNumber')}>{result.certificateNumber}</Row>
            )}
            {police && vehicle && <Row label={t('police.vehicle')}>{vehicle}</Row>}
          </Stack>
          {police && result.status !== 'NOT_FOUND' && (
            <Alert severity="info">{t('police.confirmVehicle')}</Alert>
          )}
          {result.certificateStatus === 'REVOKED' && (
            <Alert severity="warning">{t('verify.revoked')}</Alert>
          )}
          <Typography variant="caption" color="text.secondary">
            {t('verify.checkedAt', { checkedAt: formatDateTimeNumeric(result.checkedAt) })}
          </Typography>
          {onNew && (
            <Button variant="outlined" onClick={onNew} sx={{ alignSelf: 'flex-start' }}>
              {t('verify.newCheck')}
            </Button>
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
