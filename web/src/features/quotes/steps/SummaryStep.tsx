import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Divider from '@mui/material/Divider';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import {
  createBrokerQuoteRequest,
  createQuoteRequest,
  type CreateBrokerQuoteRequest,
  type CreateQuoteRequest,
  type RequestedCoverageRequest,
} from '../../../api/generated';
import { getProblem, problemMessage, type Problem } from '../../../errors/problem';
import { formatMoney } from '../../../format/money';
import { formatDate } from '../../../i18n/format';
import { msisdnPrefix } from '../../customer-auth/otp';
import { isTrailer } from '../../vehicles/vehicleSchema';
import { endDateOf, yearsBetween } from '../period';
import { durationText, formatMsisdn, vehicleLabel } from '../quoteFormat';
import type { CoverageChoice } from '../quoteRules';
import type { QuoteDraft } from '../useQuoteDraft';
import type { RiskVehicle } from './stepTypes';

export type StepKey = 'client' | 'vehicle' | 'driver' | 'type' | 'coverages' | 'period' | 'summary';

function coveragesRequest(choices: readonly CoverageChoice[]): RequestedCoverageRequest[] {
  return choices.map((choice) => ({
    code: choice.code as RequestedCoverageRequest['code'],
    ...(choice.capital ? { capital: choice.capital } : {}),
  }));
}

/** Pedido do cliente (`createQuoteRequest`). */
export function toCustomerRequest(draft: QuoteDraft): CreateQuoteRequest {
  return {
    vehicleId: draft.vehicleId,
    driverId: draft.driverId,
    productType: draft.productType as CreateQuoteRequest['productType'],
    coverages: coveragesRequest(draft.coverages),
    startDate: draft.startDate,
    durationDays: draft.durationDays,
  };
}

const optionalNumber = (value: string) => (value.trim() ? Number(value.trim()) : undefined);
const optionalText = (value: string) => (value.trim() ? value.trim() : undefined);

/** Pedido do mediador (`createBrokerQuoteRequest`); os campos vazios vão ausentes. */
export function toBrokerRequest(draft: QuoteDraft): CreateBrokerQuoteRequest {
  const v = draft.vehicle;
  const d = draft.driver;
  const trailer = isTrailer(v.category);
  return {
    clientMsisdn: `${msisdnPrefix}${draft.clientMsisdn}`,
    vehicle: {
      plate: optionalText(v.plate.toUpperCase()),
      make: optionalText(v.make),
      model: optionalText(v.model),
      year: Number(v.year.trim()),
      engineCc: trailer ? undefined : optionalNumber(v.engineCc),
      category: v.category as CreateBrokerQuoteRequest['vehicle']['category'],
      usage: v.usage as CreateBrokerQuoteRequest['vehicle']['usage'],
      seats: trailer ? 0 : Number(v.seats.trim()),
      grossWeightKg: optionalNumber(v.grossWeightKg),
      provinceOfCirculation:
        v.provinceOfCirculation as CreateBrokerQuoteRequest['vehicle']['provinceOfCirculation'],
      ...(v.commercialValue ? { commercialValue: v.commercialValue } : {}),
      hasTracker: v.hasTracker,
    },
    driver: {
      birthDate: d.birthDate,
      ...(d.licenceIssueDate ? { licenceIssueDate: d.licenceIssueDate } : {}),
      licenceCategories: d.licenceCategories,
    },
    productType: draft.productType as CreateBrokerQuoteRequest['productType'],
    coverages: coveragesRequest(draft.coverages),
    startDate: draft.startDate,
    durationDays: draft.durationDays,
  };
}

/** Mensagem de um erro ao pedir as cotações (com os minutos de espera em `QUOTE_RATE_LIMITED`). */
export function quoteRequestErrorMessage(
  t: ReturnType<typeof useTranslation>['t'],
  problem: Problem,
): string {
  const message = problemMessage(t, problem);
  if (problem.code === 'QUOTE_RATE_LIMITED' && problem.retryAfterSeconds !== undefined) {
    const count = Math.max(1, Math.ceil(problem.retryAfterSeconds / 60));
    return `${message} ${t('quoteWizard.summary.retryIn', { count })}`;
  }
  return message;
}

export interface SummaryStepProps {
  mode: 'customer' | 'broker';
  draft: QuoteDraft;
  vehicle: RiskVehicle;
  driver: { name?: string; birthDate?: string | null; licenceIssueDate?: string | null };
  goTo(step: StepKey): void;
  onCreated(requestId: string): void;
  /** O assistente trata o erro (volta a um passo)? */
  onProblem(problem: Problem): boolean;
  onBack(): void;
}

/** Passo "Resumo" e o botão laranja "Simular seguro". */
export function SummaryStep({
  mode,
  draft,
  vehicle,
  driver,
  goTo,
  onCreated,
  onProblem,
  onBack,
}: SummaryStepProps) {
  const { t, i18n } = useTranslation();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const language = i18n.language;

  const age = driver.birthDate ? yearsBetween(driver.birthDate, draft.startDate) : null;
  const licenceYears = driver.licenceIssueDate
    ? yearsBetween(driver.licenceIssueDate, draft.startDate)
    : null;
  const driverText = [
    driver.name,
    age === null
      ? null
      : licenceYears === null
        ? t('quoteWizard.summary.driverAge', { age })
        : t('quoteWizard.summary.driverAgeLicence', { age, years: Math.max(0, licenceYears) }),
  ]
    .filter(Boolean)
    .join(' · ');

  const coverageLines = [
    t('coverage.MTPL'),
    ...draft.coverages
      .filter((choice) => choice.code !== 'MTPL')
      .map((choice) =>
        choice.capital
          ? `${t(`coverage.${choice.code}`)} (${formatMoney(choice.capital)})`
          : t(`coverage.${choice.code}`),
      ),
  ];

  const end = endDateOf(draft.startDate, draft.durationDays);

  const submit = async () => {
    setBusy(true);
    setError(null);
    try {
      const response =
        mode === 'customer'
          ? await createQuoteRequest(toCustomerRequest(draft))
          : await createBrokerQuoteRequest(toBrokerRequest(draft));
      onCreated(response.id);
    } catch (caught) {
      const problem = getProblem(caught);
      setBusy(false);
      if (onProblem(problem)) return;
      setError(quoteRequestErrorMessage(t, problem));
    }
  };

  const blocks: { key: StepKey; title: string; content: ReactNode }[] = [
    ...(mode === 'broker'
      ? [
          {
            key: 'client' as const,
            title: t('quoteWizard.summary.client'),
            content: formatMsisdn(`${msisdnPrefix}${draft.clientMsisdn}`),
          },
        ]
      : []),
    {
      key: 'vehicle',
      title: t('quoteWizard.summary.vehicle'),
      content: [
        vehicleLabel(t, vehicle),
        vehicle.category && vehicle.usage
          ? `${t(`vehicleCategory.${vehicle.category}`)} · ${t(`vehicleUsage.${vehicle.usage}`)}`
          : null,
        vehicle.commercialValue ? formatMoney(vehicle.commercialValue) : null,
      ]
        .filter(Boolean)
        .join(' · '),
    },
    {
      key: mode === 'broker' ? 'driver' : 'vehicle',
      title: t('quoteWizard.summary.driver'),
      content: driverText,
    },
    {
      key: 'type',
      title: t('quoteWizard.summary.type'),
      content: draft.productType ? t(`quote.types.${draft.productType}.title`) : '',
    },
    {
      key: 'coverages',
      title: t('quoteWizard.summary.coverages'),
      content: (
        <Box component="ul" sx={{ m: 0, pl: 2.5 }}>
          {coverageLines.map((line) => (
            <li key={line}>{line}</li>
          ))}
        </Box>
      ),
    },
    {
      key: 'period',
      title: t('quoteWizard.summary.period'),
      content: `${t('quote.periodRange', {
        start: formatDate(draft.startDate, language),
        end: formatDate(end, language),
      })} (${durationText(t, draft.durationDays)})`,
    },
  ];

  return (
    <Stack spacing={2.5}>
      <Stack spacing={1.5} divider={<Divider flexItem />}>
        {blocks.map((block) => (
          <Stack
            key={`${block.key}-${block.title}`}
            direction="row"
            spacing={2}
            justifyContent="space-between"
            alignItems="flex-start"
          >
            <Box sx={{ minWidth: 0 }}>
              <Typography variant="subtitle2" color="text.secondary">
                {block.title}
              </Typography>
              <Box sx={{ wordBreak: 'break-word' }}>{block.content}</Box>
            </Box>
            <Button
              size="small"
              onClick={() => goTo(block.key)}
              aria-label={`${t('quoteWizard.summary.change')}: ${block.title}`}
            >
              {t('quoteWizard.summary.change')}
            </Button>
          </Stack>
        ))}
      </Stack>

      {error && (
        <Alert severity="error" role="alert">
          {error}
        </Alert>
      )}
      {busy && (
        <Typography color="text.secondary" role="status">
          {t('quoteWizard.summary.waiting')}
        </Typography>
      )}
      <Stack direction="row" spacing={2} justifyContent="space-between">
        <Button variant="text" onClick={onBack} disabled={busy}>
          {t('quoteWizard.back')}
        </Button>
        <Button
          variant="cta"
          size="large"
          onClick={() => void submit()}
          disabled={busy}
          startIcon={busy ? <CircularProgress size={18} color="inherit" /> : undefined}
        >
          {t('quote.simulate')}
        </Button>
      </Stack>
    </Stack>
  );
}
