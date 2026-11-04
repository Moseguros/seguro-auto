import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import {
  updateBrokerProposal,
  updateProposal,
  type BrokerPolicyholderRequest,
  type FieldError,
  type ProposalResponse,
} from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { todayInMaputo } from '../../format/dates';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { Field } from '../insurer-proposals/common';
import { endDateOf, startDateRange } from '../quotes/period';
import { BrokerPolicyholderForm } from './BrokerPolicyholderForm';
import { StepActions } from './StepActions';
import { useWizard } from './useProposalMutations';
import { stepPath } from './wizardSteps';

/** Data de início (`AAAA-MM-DD` em Maputo) de uma proposta. */
export function proposalStartDate(proposal: Pick<ProposalResponse, 'startAt'>): string {
  return todayInMaputo(new Date(proposal.startAt));
}

/** Data `AAAA-MM-DD` mostrada na língua (sem mudar de dia com o fuso). */
export function formatLocalDate(value: string, language: string): string {
  return value ? formatDate(`${value}T12:00:00Z`, language) : '';
}

/** Resumo curto: seguradora, produto, coberturas e total. */
export function ProposalShortSummary({ proposal }: { proposal: ProposalResponse }) {
  const { t, i18n } = useTranslation();
  return (
    <Card variant="outlined" elevation={0}>
      <CardContent>
        <Box
          component="dl"
          sx={{
            m: 0,
            display: 'grid',
            gap: 1.5,
            gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))' },
          }}
        >
          <Field label={t('proposal.summary.insurer')}>{proposal.insurer?.displayName}</Field>
          <Field label={t('proposal.summary.product')}>{proposal.product.name}</Field>
          <Field label={t('proposal.summary.coverages')}>
            {proposal.coverages.map((c) => t(`coverage.${c.code}`)).join(', ')}
          </Field>
          <Field label={t('proposal.summary.total')}>
            {formatMoney(proposal.premium.total, i18n.language)}
          </Field>
          <Field label={t('proposal.summary.vehicle')}>
            {`${proposal.vehicle.plate} · ${proposal.vehicle.make} ${proposal.vehicle.model}`}
          </Field>
        </Box>
      </CardContent>
    </Card>
  );
}

/** Dados do tomador (documento e NUIT mascarados). */
export function PolicyholderData({ proposal }: { proposal: ProposalResponse }) {
  const { t, i18n } = useTranslation();
  const holder = proposal.policyholder;
  return (
    <Box
      component="dl"
      sx={{
        m: 0,
        display: 'grid',
        gap: 1.5,
        gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))' },
      }}
    >
      <Field label={t('profile.fields.fullName')}>{holder.fullName}</Field>
      <Field label={t('profile.fields.birthDate')}>
        {formatLocalDate(holder.birthDate, i18n.language)}
      </Field>
      <Field label={t('profile.fields.idDocNumber')}>
        {`${t(`idDocType.${holder.idDocType}`, { defaultValue: holder.idDocType })} ${holder.idDocNumberMasked}`}
      </Field>
      <Field label={t('profile.fields.nuit')}>{holder.nuitMasked}</Field>
      <Field label={t('profile.fields.province')}>
        {t(`province.${holder.province}`, { defaultValue: holder.province })}
      </Field>
      {holder.address && <Field label={t('profile.fields.address')}>{holder.address}</Field>}
      {holder.msisdnMasked && (
        <Field label={t('proposal.summary.msisdn')}>{holder.msisdnMasked}</Field>
      )}
      {holder.emailMasked && (
        <Field label={t('proposal.summary.email')}>{holder.emailMasked}</Field>
      )}
    </Box>
  );
}

const formId = 'broker-policyholder-step';

/** Passo `tomador`: revisão dos dados do tomador e data de início. */
export function PolicyholderStep() {
  const { t, i18n } = useTranslation();
  const wizard = useWizard();
  const { proposal, mode } = wizard;
  const initialDate = proposalStartDate(proposal);
  const [startDate, setStartDate] = useState(initialDate);
  const [dateError, setDateError] = useState<string | null>(null);
  const [message, setMessage] = useState<{ text: string; profileLink?: boolean } | null>(null);
  const [serverErrors, setServerErrors] = useState<FieldError[]>([]);
  const [busy, setBusy] = useState(false);
  const range = startDateRange();
  const endDate = endDateOf(startDate, proposal.durationDays);

  const brokerForm = mode === 'broker' && proposal.policyholderSource === 'BROKER';
  const profileSource = proposal.policyholderSource === 'PROFILE';
  const returnTo = encodeURIComponent(stepPath('customer', proposal.id, 'tomador'));

  function validDate(): boolean {
    if (!startDate || startDate < range.min || startDate > range.max) {
      setDateError('START_DATE_OUT_OF_RANGE');
      return false;
    }
    setDateError(null);
    return true;
  }

  async function save(request: () => Promise<ProposalResponse>, advance: boolean): Promise<void> {
    setBusy(true);
    setMessage(null);
    try {
      const saved = await request();
      wizard.update(saved);
      if (advance) wizard.next();
    } catch (error) {
      const problem = getProblem(error);
      if (wizard.handleCommon(problem)) return;
      const startError = problem.errors.find((e) => e.field === 'startDate');
      if (startError) {
        setDateError(startError.code);
        return;
      }
      const holderErrors = problem.errors.filter((e) => e.field.startsWith('policyholder'));
      if (holderErrors.length > 0) {
        setServerErrors(holderErrors);
        return;
      }
      setMessage({
        text: problemMessage(t, problem),
        profileLink: problem.code === 'PROFILE_INCOMPLETE',
      });
    } finally {
      setBusy(false);
    }
  }

  const dateChanged = startDate !== initialDate;

  function saveDate(advance: boolean) {
    if (!validDate()) return;
    if (!dateChanged && advance) {
      wizard.next();
      return;
    }
    void save(
      () =>
        mode === 'broker'
          ? updateBrokerProposal(proposal.id, { startDate, version: proposal.version })
          : updateProposal(proposal.id, { startDate, version: proposal.version }),
      advance,
    );
  }

  function onBrokerSubmit(policyholder: BrokerPolicyholderRequest, dirty: boolean) {
    if (!validDate()) return;
    if (!dirty && !dateChanged) {
      wizard.next();
      return;
    }
    return save(
      () =>
        updateBrokerProposal(proposal.id, {
          ...(dateChanged ? { startDate } : {}),
          ...(dirty ? { policyholder } : {}),
          version: proposal.version,
        }),
      true,
    );
  }

  return (
    <Stack spacing={3}>
      <ProposalShortSummary proposal={proposal} />

      <Stack spacing={1.5}>
        <Typography variant="h6" component="h2">
          {t('proposal.policyholder.title')}
        </Typography>
        {mode === 'customer' && profileSource && (
          <Alert
            severity="info"
            action={
              <Button
                color="inherit"
                size="small"
                variant="outlined"
                disabled={busy}
                onClick={() =>
                  void save(
                    () => updateProposal(proposal.id, { startDate, version: proposal.version }),
                    false,
                  )
                }
              >
                {t('proposal.policyholder.refresh')}
              </Button>
            }
          >
            {t('proposal.policyholder.fromProfile')}{' '}
            <Link component={RouterLink} to={`/cliente/perfil?voltar=${returnTo}`}>
              {t('proposal.policyholder.editProfile')}
            </Link>
          </Alert>
        )}
        {mode === 'broker' && profileSource && (
          <Alert severity="info">{t('proposal.policyholder.fromClientProfile')}</Alert>
        )}
        {brokerForm ? (
          <>
            <Alert severity="info">{t('proposal.policyholder.filledByBroker')}</Alert>
            <BrokerPolicyholderForm
              formId={formId}
              current={proposal.policyholder}
              serverErrors={serverErrors}
              onSubmit={onBrokerSubmit}
            />
          </>
        ) : (
          <PolicyholderData proposal={proposal} />
        )}
      </Stack>

      <Stack spacing={1}>
        <Typography variant="h6" component="h2">
          {t('proposal.policyholder.startTitle')}
        </Typography>
        <TextField
          label={t('proposal.policyholder.startDate')}
          type="date"
          value={startDate}
          onChange={(event) => {
            setStartDate(event.target.value);
            setDateError(null);
          }}
          error={!!dateError}
          helperText={dateError ? fieldMessage(t, dateError) : undefined}
          fullWidth
          slotProps={{
            inputLabel: { shrink: true },
            htmlInput: { min: range.min, max: range.max },
          }}
        />
        {endDate && (
          <Typography color="text.secondary">
            {t('proposal.policyholder.end', { date: formatLocalDate(endDate, i18n.language) })}
          </Typography>
        )}
      </Stack>

      {message && (
        <Alert
          severity="error"
          role="alert"
          action={
            message.profileLink && mode === 'customer' ? (
              <Button
                color="inherit"
                size="small"
                component={RouterLink}
                to={`/cliente/perfil?completar=1&voltar=${returnTo}`}
              >
                {t('proposal.policyholder.editProfile')}
              </Button>
            ) : undefined
          }
        >
          {message.text}
        </Alert>
      )}

      <StepActions onBack={wizard.back}>
        {brokerForm ? (
          <Button type="submit" form={formId} variant="contained" disabled={busy}>
            {t('proposal.wizard.continue')}
          </Button>
        ) : (
          <Button variant="contained" disabled={busy} onClick={() => saveDate(true)}>
            {t('proposal.wizard.continue')}
          </Button>
        )}
      </StepActions>
    </Stack>
  );
}
