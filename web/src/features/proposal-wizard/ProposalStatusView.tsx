import Download from '@mui/icons-material/Download';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Step from '@mui/material/Step';
import StepContent from '@mui/material/StepContent';
import StepLabel from '@mui/material/StepLabel';
import Stepper from '@mui/material/Stepper';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { ProposalResponse } from '../../api/generated';
import { downloadProtectedFile } from '../../files/downloadProtectedFile';
import { formatMoney } from '../../format/money';
import { formatDateTime } from '../../i18n/format';
import { ProposalFullSummary } from './ReviewStep';
import { proposalPaths, type ProposalMode } from './wizardSteps';

/** Estado de uma proposta fora dos passos (S4-08 §5). */
export function ProposalStatusView({
  proposal,
  mode,
}: {
  proposal: ProposalResponse;
  mode: ProposalMode;
}) {
  const { t, i18n } = useTranslation();
  const lang = i18n.language;
  const [fileError, setFileError] = useState(false);
  const insurer = proposal.insurer?.displayName ?? '';
  const decision = proposal.decision ?? null;
  const simulate = (
    <Button
      variant="cta"
      component={RouterLink}
      to={proposalPaths[mode].quotes}
      sx={{ alignSelf: 'flex-start' }}
    >
      {t('proposal.status.simulate')}
    </Button>
  );

  const timeline = [
    { key: 'created', date: proposal.createdAt },
    ...(proposal.submittedAt ? [{ key: 'submitted', date: proposal.submittedAt }] : []),
    ...(decision?.decidedAt
      ? [
          {
            key:
              proposal.status === 'REJECTED'
                ? 'rejected'
                : ['ACCEPTED', 'PAID', 'CONVERTED'].includes(proposal.status)
                  ? 'accepted'
                  : 'decided',
            date: decision.decidedAt,
          },
        ]
      : []),
  ];

  let body;
  switch (proposal.status) {
    case 'UNDER_REVIEW':
      body = (
        <Alert severity="info">
          {decision?.insurerDueAt
            ? t('proposal.status.underReview', {
                insurer,
                date: formatDateTime(decision.insurerDueAt, lang),
              })
            : t('proposal.status.underReviewNoDate', { insurer })}
        </Alert>
      );
      break;
    case 'ACCEPTED':
      body = (
        <Stack spacing={1}>
          <Alert severity="success">
            <Typography fontWeight={600}>{t('proposal.status.accepted')}</Typography>
            <Typography variant="body2">{t('proposal.status.paymentSoon')}</Typography>
          </Alert>
          {proposal.expiresAt && (
            <Typography color="text.secondary">
              {t('proposal.status.expiresAt', { date: formatDateTime(proposal.expiresAt, lang) })}
            </Typography>
          )}
        </Stack>
      );
      break;
    case 'REJECTED':
      body = (
        <Stack spacing={2}>
          <Alert severity="error">
            <Typography fontWeight={600}>{t('proposal.status.rejected')}</Typography>
            {decision?.rejectionReasonCode && (
              <Typography variant="body2">
                {t(`rejectionReason.${decision.rejectionReasonCode}`)}
              </Typography>
            )}
            {decision?.reason && (
              <Typography variant="body2" sx={{ whiteSpace: 'pre-line', wordBreak: 'break-word' }}>
                {decision.reason}
              </Typography>
            )}
          </Alert>
          {simulate}
        </Stack>
      );
      break;
    case 'EXPIRED':
      body = (
        <Stack spacing={2}>
          <Alert severity="warning">{t('proposal.status.expired')}</Alert>
          {simulate}
        </Stack>
      );
      break;
    default:
      body = <Alert severity="info">{t(`proposalStatus.${proposal.status}`)}</Alert>;
  }

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Stack spacing={0.5}>
        <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
          {t('proposal.wizard.title', { number: proposal.number })}
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
          <Chip
            size="small"
            color={
              proposal.status === 'ACCEPTED'
                ? 'success'
                : proposal.status === 'REJECTED'
                  ? 'error'
                  : 'default'
            }
            label={t(`proposalStatus.${proposal.status}`)}
          />
          <Typography color="text.secondary">
            {insurer} · {formatMoney(proposal.premium.total, lang)}
          </Typography>
        </Stack>
      </Stack>

      {body}

      <Stepper
        orientation="vertical"
        activeStep={timeline.length - 1}
        aria-label={t('proposal.status.timeline')}
      >
        {timeline.map((item) => (
          <Step key={item.key} completed>
            <StepLabel>{t(`proposal.status.event.${item.key}`)}</StepLabel>
            <StepContent TransitionProps={{ in: true }}>
              <Typography variant="body2" color="text.secondary">
                {formatDateTime(item.date, lang)}
              </Typography>
            </StepContent>
          </Step>
        ))}
      </Stepper>

      {proposal.signature && (
        <Button
          variant="outlined"
          startIcon={<Download />}
          sx={{ alignSelf: 'flex-start' }}
          onClick={() => {
            setFileError(false);
            downloadProtectedFile(
              proposal.signature!.documentFileId,
              `${proposal.number}.pdf`,
            ).catch(() => setFileError(true));
          }}
        >
          {t('proposal.status.downloadSigned')}
        </Button>
      )}
      {fileError && <Alert severity="error">{t('files.loadError')}</Alert>}

      <Card variant="outlined" elevation={0}>
        <CardContent>
          <ProposalFullSummary proposal={proposal} />
        </CardContent>
      </Card>

      <Button
        variant="text"
        component={RouterLink}
        to={proposalPaths[mode].list}
        sx={{ alignSelf: 'flex-start' }}
      >
        {t('proposal.status.backToList')}
      </Button>
    </Stack>
  );
}
