import ArrowBack from '@mui/icons-material/ArrowBack';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import ButtonBase from '@mui/material/ButtonBase';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Link from '@mui/material/Link';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation, useNavigate, useParams } from 'react-router-dom';
import { getInsurerKycCheck, verifyKycCheck } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { ProtectedImage } from '../../files/ProtectedImage';
import { todayInMaputo } from '../../format/dates';
import { formatDate, formatDateTime } from '../../i18n/format';
import { PhotoLightbox, type LightboxPhoto } from '../insurer-inspections/PhotoLightbox';
import {
  Field,
  insurerKycKey,
  insurerProposalsKey,
  QueueError,
  type QueueNavigationState,
} from '../insurer-proposals/common';
import { kycPath } from '../insurer-proposals/ProposalsTabs';
import { KycRejectDialog } from './KycRejectDialog';

/** Idade em anos completos, numa data (`AAAA-MM-DD`) de Maputo. */
export function ageOn(birthDate: string, today: string = todayInMaputo()): number {
  const [by, bm, bd] = birthDate.split('-').map(Number) as [number, number, number];
  const [ty, tm, td] = today.split('-').map(Number) as [number, number, number];
  return ty - by - (tm < bm || (tm === bm && td < bd) ? 1 : 0);
}

/** Revisão de uma verificação de identidade: dados, as três imagens e a decisão. */
export function KycReviewPage() {
  const { t, i18n } = useTranslation();
  const { id = '' } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const queryClient = useQueryClient();
  const state = (location.state as QueueNavigationState | null) ?? {};
  const [dialog, setDialog] = useState<'verify' | 'reject' | null>(null);
  const [lightbox, setLightbox] = useState<number | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [verifyError, setVerifyError] = useState<string | null>(null);

  const query = useQuery({
    queryKey: [...insurerKycKey, 'check', id],
    queryFn: ({ signal }) => getInsurerKycCheck(id, undefined, signal),
  });
  const check = query.data;

  if (query.isLoading) return <LoadingScreen />;
  if (!check) return <QueueError error={query.error} onRetry={() => void query.refetch()} />;

  const backTo = state.returnTo ?? kycPath;
  const done = (notice: string) => {
    void queryClient.invalidateQueries({ queryKey: insurerKycKey });
    void queryClient.invalidateQueries({ queryKey: insurerProposalsKey });
    void queryClient.invalidateQueries({ queryKey: ['insurer-proposal'] });
    navigate(backTo, {
      state: { notice, queueSearch: state.queueSearch } satisfies QueueNavigationState,
    });
  };
  const conflict = (message: string) => {
    setDialog(null);
    setNotice(message);
    void query.refetch();
  };
  const verify = async () => {
    setSaving(true);
    setVerifyError(null);
    try {
      await verifyKycCheck(check.id, { version: check.version });
      done(t('insurerKyc.review.verified'));
    } catch (error) {
      setSaving(false);
      const problem = getProblem(error);
      if (problem.status === 409) conflict(problemMessage(t, problem));
      else setVerifyError(problemMessage(t, problem));
    }
  };

  const customer = check.customer;
  const images: LightboxPhoto[] = [
    { fileId: check.files.docFrontFileId, label: t('insurerKyc.review.front') },
    ...(check.files.docBackFileId
      ? [{ fileId: check.files.docBackFileId, label: t('insurerKyc.review.back') }]
      : []),
    { fileId: check.files.selfieFileId, label: t('insurerKyc.review.selfie') },
  ];
  const pending = check.status === 'PENDING';

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <div>
        <Link
          component={RouterLink}
          to={backTo}
          state={{ queueSearch: state.queueSearch } satisfies QueueNavigationState}
          sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
        >
          <ArrowBack fontSize="small" />
          {state.returnTo ? t('insurerProposals.detail.back') : t('insurerKyc.review.back')}
        </Link>
      </div>
      <Stack direction="row" spacing={2} alignItems="center" flexWrap="wrap" useFlexGap>
        <Typography variant="h4" component="h1">
          {t('insurerKyc.review.title')}
        </Typography>
        <Chip
          size="small"
          variant="outlined"
          color={
            check.status === 'VERIFIED'
              ? 'success'
              : check.status === 'REJECTED'
                ? 'error'
                : 'default'
          }
          label={t(`kyc.checkStatus.${check.status}`)}
        />
      </Stack>
      {notice && (
        <Alert severity="warning" role="alert" onClose={() => setNotice(null)}>
          {notice}
        </Alert>
      )}
      <Box
        sx={{
          display: 'grid',
          gap: 3,
          gridTemplateColumns: { xs: 'minmax(0, 1fr)', md: 'minmax(0, 1fr) minmax(0, 1.4fr)' },
          alignItems: 'start',
        }}
      >
        <Paper variant="outlined" sx={{ p: 2 }}>
          <Stack spacing={2}>
            <Alert severity="info">{t('insurerKyc.review.compare')}</Alert>
            <Box component="dl" sx={{ m: 0, display: 'grid', gap: 1.5 }}>
              <Field label={t('insurerKyc.review.fullName')}>{customer.fullName}</Field>
              <Field label={t('insurerKyc.review.birthDate')}>
                {t('insurerKyc.review.age', {
                  date: formatDate(`${customer.birthDate}T12:00:00Z`, i18n.language),
                  age: ageOn(customer.birthDate),
                })}
              </Field>
              <Field label={t('insurerKyc.review.idDocType')}>
                {t(`idDocType.${customer.idDocType}`)}
              </Field>
              <Field label={t('insurerKyc.review.idDocNumber')}>
                <Box component="span" sx={{ fontFamily: 'monospace', fontSize: '1.05em' }}>
                  {customer.idDocNumber}
                </Box>
              </Field>
              <Field label={t('insurerKyc.review.msisdn')}>{customer.msisdnMasked ?? ''}</Field>
              <Field label={t('insurerKyc.review.submittedAt')}>
                {formatDateTime(check.submittedAt, i18n.language)}
              </Field>
              {check.rejectionReasonCode && (
                <Field label={t('insurerKyc.review.rejection')}>
                  {[t(`kyc.rejectionReason.${check.rejectionReasonCode}`), check.rejectionReason]
                    .filter(Boolean)
                    .join(' — ')}
                </Field>
              )}
            </Box>
          </Stack>
        </Paper>
        <Box
          component="ul"
          sx={{
            listStyle: 'none',
            p: 0,
            m: 0,
            display: 'grid',
            gap: 1.5,
            gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))',
          }}
        >
          {images.map((image, index) => (
            <Box component="li" key={image.fileId} sx={{ minWidth: 0 }}>
              <ButtonBase
                onClick={() => setLightbox(index)}
                aria-label={t('insurerInspections.gallery.open', { label: image.label })}
                sx={{ display: 'block', width: '100%', borderRadius: 1 }}
              >
                <ProtectedImage fileId={image.fileId} alt={image.label} height={200} />
              </ButtonBase>
              <Typography variant="caption" fontWeight={600}>
                {image.label}
              </Typography>
            </Box>
          ))}
        </Box>
      </Box>

      <Stack spacing={1}>
        <Typography variant="h6" component="h2">
          {t('insurerKyc.review.previous')}
        </Typography>
        {check.previousChecks.length === 0 ? (
          <Typography color="text.secondary">{t('insurerKyc.review.noPrevious')}</Typography>
        ) : (
          <List dense disablePadding>
            {check.previousChecks.map((previous) => (
              <ListItem key={previous.id} disableGutters>
                <ListItemText
                  primary={`${formatDateTime(previous.submittedAt, i18n.language)} · ${t(`kyc.checkStatus.${previous.status}`)}`}
                  secondary={
                    previous.rejectionReasonCode
                      ? t(`kyc.rejectionReason.${previous.rejectionReasonCode}`)
                      : undefined
                  }
                />
              </ListItem>
            ))}
          </List>
        )}
      </Stack>

      {pending && (
        <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
          <Button variant="contained" onClick={() => setDialog('verify')}>
            {t('insurerKyc.review.verify')}
          </Button>
          <Button variant="outlined" onClick={() => setDialog('reject')}>
            {t('insurerKyc.review.reject')}
          </Button>
        </Stack>
      )}

      {dialog === 'verify' && (
        <Dialog open onClose={() => setDialog(null)} aria-labelledby="kyc-verify-title">
          <DialogTitle id="kyc-verify-title">{t('insurerKyc.review.verifyTitle')}</DialogTitle>
          <DialogContent>
            <DialogContentText>{t('insurerKyc.review.verifyText')}</DialogContentText>
            {verifyError && (
              <Alert severity="error" role="alert" sx={{ mt: 2 }}>
                {verifyError}
              </Alert>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDialog(null)}>{t('common.cancel')}</Button>
            <Button variant="contained" onClick={() => void verify()} disabled={saving}>
              {t('insurerKyc.review.verify')}
            </Button>
          </DialogActions>
        </Dialog>
      )}
      {dialog === 'reject' && (
        <KycRejectDialog
          check={check}
          onClose={() => setDialog(null)}
          onDone={() => done(t('insurerKyc.review.rejected'))}
          onConflict={conflict}
        />
      )}
      {lightbox !== null && (
        <PhotoLightbox
          photos={images}
          index={lightbox}
          onIndexChange={setLightbox}
          onClose={() => setLightbox(null)}
        />
      )}
    </Stack>
  );
}
