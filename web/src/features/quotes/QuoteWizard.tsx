import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import MobileStepper from '@mui/material/MobileStepper';
import Stack from '@mui/material/Stack';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Stepper from '@mui/material/Stepper';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  getGetQuoteRequestQueryKey,
  getListMyDriversQueryKey,
  getListMyVehiclesQueryKey,
  getQuoteRequest,
  listMyDrivers,
  listMyVehicles,
  type QuoteRequestResponse,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { todayInMaputo } from '../../format/dates';
import { quotePaths, type QuoteMode } from './config';
import type { BrokerVehicleValues } from './quoteRules';
import { BrokerClientStep } from './steps/BrokerClientStep';
import { BrokerDriverStep } from './steps/BrokerDriverStep';
import { BrokerVehicleStep } from './steps/BrokerVehicleStep';
import { CoveragesStep } from './steps/CoveragesStep';
import { PeriodStep } from './steps/PeriodStep';
import type { DraftUpdate, RiskVehicle, StepProps } from './steps/stepTypes';
import { SummaryStep, type StepKey } from './steps/SummaryStep';
import { TypeStep } from './steps/TypeStep';
import { VehicleStep } from './steps/VehicleStep';
import { emptyDraft, useQuoteDraft, type QuoteDraft } from './useQuoteDraft';

export const wizardSteps: Record<QuoteMode, StepKey[]> = {
  customer: ['vehicle', 'type', 'coverages', 'period', 'summary'],
  broker: ['client', 'vehicle', 'driver', 'type', 'coverages', 'period', 'summary'],
};

const listParams = { size: 50 };

const text = (value: number | null | undefined) =>
  value === null || value === undefined ? '' : String(value);

/** Rascunho com os dados de um pedido (`?refazer=`); a data de início passada passa a hoje. */
export function draftFromRequest(
  mode: QuoteMode,
  request: QuoteRequestResponse,
  today: string = todayInMaputo(),
): { draft: QuoteDraft; startUpdated: boolean } {
  const v = request.vehicle;
  const vehicle: BrokerVehicleValues = {
    plate: v.plate ?? '',
    make: v.make ?? '',
    model: v.model ?? '',
    year: String(v.year),
    engineCc: text(v.engineCc),
    category: v.category,
    usage: v.usage,
    seats: text(v.seats),
    grossWeightKg: text(v.grossWeightKg),
    provinceOfCirculation: v.provinceOfCirculation,
    commercialValue: v.commercialValue ?? null,
    hasTracker: v.hasTracker,
  };
  const startUpdated = request.startDate < today;
  const steps = wizardSteps[mode];
  return {
    startUpdated,
    draft: {
      ...emptyDraft(),
      step: steps.length - 1,
      vehicleId: v.vehicleId ?? '',
      driverId: request.driver.driverId ?? '',
      clientMsisdn: (request.clientMsisdn ?? '').replace(/^\+?258/, ''),
      vehicle,
      driver: {
        birthDate: request.driver.birthDate ?? '',
        licenceIssueDate: request.driver.licenceIssueDate ?? '',
        licenceCategories: request.driver.licenceCategories,
      },
      productType: request.productType,
      coverages: request.requestedCoverages
        .filter((coverage) => coverage.code !== 'MTPL')
        .map((coverage) => ({ code: coverage.code, capital: coverage.capital ?? null })),
      coveragesFor: request.productType,
      startDate: startUpdated ? today : request.startDate,
      durationDays: request.durationDays,
    },
  };
}

/** Passo onde está o campo de um `errors[].field` do servidor. */
export function stepOfField(mode: QuoteMode, field: string): StepKey {
  if (field === 'clientMsisdn') return 'client';
  if (field.startsWith('vehicle') || (mode === 'customer' && field.startsWith('driver'))) {
    return 'vehicle';
  }
  if (field.startsWith('driver')) return 'driver';
  if (field === 'productType') return 'type';
  if (field.startsWith('coverages')) return 'coverages';
  return 'period';
}

/** Assistente de simulação (cliente) ou de cotação (mediador). */
export function QuoteWizard({ mode }: { mode: QuoteMode }) {
  const { t } = useTranslation();
  const theme = useTheme();
  const wide = useMediaQuery(theme.breakpoints.up('sm'));
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params, setParams] = useSearchParams();
  const redo = params.get('refazer');
  const { draft, update: rawUpdate, replace, clear } = useQuoteDraft(mode);
  const [valid, setValid] = useState(false);
  const [serverErrors, setServerErrors] = useState<Record<string, string>>({});
  const [notice, setNotice] = useState<{ severity: 'info' | 'error'; message: string } | null>(
    null,
  );
  const steps = wizardSteps[mode];
  const stepIndex = Math.min(Math.max(draft.step, 0), steps.length - 1);
  const step = steps[stepIndex] ?? 'summary';
  const paths = quotePaths[mode];

  const customer = mode === 'customer';
  const vehicles = useQuery({
    queryKey: getListMyVehiclesQueryKey(listParams),
    queryFn: ({ signal }) => listMyVehicles(listParams, undefined, signal),
    enabled: customer,
  });
  const drivers = useQuery({
    queryKey: getListMyDriversQueryKey(listParams),
    queryFn: ({ signal }) => listMyDrivers(listParams, undefined, signal),
    enabled: customer,
  });
  const previous = useQuery({
    queryKey: getGetQuoteRequestQueryKey(redo ?? ''),
    queryFn: ({ signal }) => getQuoteRequest(redo ?? '', undefined, signal),
    enabled: !!redo,
  });

  // `?refazer=`: preenche o rascunho uma vez e tira o parâmetro do endereço.
  const applied = useRef<string | null>(null);
  useEffect(() => {
    if (!redo || !previous.data || applied.current === redo) return;
    applied.current = redo;
    const result = draftFromRequest(mode, previous.data);
    replace(result.draft);
    setNotice(
      result.startUpdated ? { severity: 'info', message: t('quoteWizard.startUpdated') } : null,
    );
    setParams({}, { replace: true });
  }, [redo, previous.data, mode, replace, setParams, t]);

  const update: DraftUpdate = useCallback(
    (patch) => {
      setServerErrors((current) => (Object.keys(current).length ? {} : current));
      rawUpdate(patch);
    },
    [rawUpdate],
  );

  const goTo = useCallback(
    (key: StepKey) => {
      const index = steps.indexOf(key);
      if (index >= 0) rawUpdate({ step: index });
      setValid(false);
    },
    [steps, rawUpdate],
  );

  const onValidity = useCallback((value: boolean) => setValid(value), []);

  const vehicleList = useMemo(() => vehicles.data?.content ?? [], [vehicles.data]);
  const driverList = useMemo(() => drivers.data?.content ?? [], [drivers.data]);

  const risk: RiskVehicle = useMemo(() => {
    if (customer) {
      const vehicle = vehicleList.find((v) => v.id === draft.vehicleId);
      return {
        vehicleId: vehicle?.id,
        category: vehicle?.category,
        usage: vehicle?.usage,
        commercialValue: vehicle?.commercialValue ?? null,
        plate: vehicle?.plate,
        make: vehicle?.make,
        model: vehicle?.model,
        year: vehicle?.year,
      };
    }
    const v = draft.vehicle;
    return {
      category: (v.category || undefined) as RiskVehicle['category'],
      usage: (v.usage || undefined) as RiskVehicle['usage'],
      commercialValue: v.commercialValue,
      plate: v.plate || undefined,
      make: v.make || undefined,
      model: v.model || undefined,
    };
  }, [customer, vehicleList, draft.vehicleId, draft.vehicle]);

  const driverInfo = useMemo(() => {
    if (customer) {
      const driver = driverList.find((d) => d.id === draft.driverId);
      return {
        name: driver?.fullName,
        birthDate: driver?.birthDate,
        licenceIssueDate: driver?.licenceIssueDate,
      };
    }
    return {
      birthDate: draft.driver.birthDate || null,
      licenceIssueDate: draft.driver.licenceIssueDate || null,
    };
  }, [customer, driverList, draft.driverId, draft.driver]);

  const onProblem = (problem: Problem): boolean => {
    if (
      ['VEHICLE_VALUE_REQUIRED', 'VEHICLE_NOT_FOUND', 'DRIVER_NOT_FOUND'].includes(problem.code)
    ) {
      setNotice({ severity: 'error', message: problemMessage(t, problem) });
      if (problem.code === 'VEHICLE_NOT_FOUND' || problem.code === 'DRIVER_NOT_FOUND') {
        void queryClient.invalidateQueries({ queryKey: getListMyVehiclesQueryKey(listParams) });
        void queryClient.invalidateQueries({ queryKey: getListMyDriversQueryKey(listParams) });
      }
      goTo(problem.code === 'DRIVER_NOT_FOUND' && !customer ? 'driver' : 'vehicle');
      return true;
    }
    if (problem.code === 'VALIDATION_ERROR' && problem.errors.length > 0) {
      const errors: Record<string, string> = {};
      for (const error of problem.errors) errors[error.field] ??= error.code;
      const first = Math.min(
        ...problem.errors.map((error) => steps.indexOf(stepOfField(mode, error.field))),
      );
      setNotice({ severity: 'error', message: problemMessage(t, problem) });
      goTo(steps[first] ?? steps[0] ?? 'summary');
      setServerErrors(errors);
      return true;
    }
    return false;
  };

  const onCreated = (requestId: string) => {
    clear();
    void queryClient.invalidateQueries({
      predicate: (query) =>
        typeof query.queryKey[0] === 'string' &&
        (query.queryKey[0].startsWith('/api/v1/me/quotes') ||
          query.queryKey[0].startsWith('/api/v1/broker/quotes')),
    });
    navigate(`${paths.base}/${requestId}`);
  };

  const title = t(`quoteWizard.title.${mode}`);
  const stepTitle = t(`quoteWizard.steps.${step}`);

  let content;
  if ((redo && previous.isPending) || (customer && (vehicles.isPending || drivers.isPending))) {
    content = <LoadingScreen />;
  } else if (redo && previous.isError) {
    content = <Alert severity="error">{problemMessage(t, getProblem(previous.error))}</Alert>;
  } else if (customer && (vehicles.isError || drivers.isError)) {
    content = (
      <Alert severity="error">
        {problemMessage(t, getProblem(vehicles.error ?? drivers.error))}
      </Alert>
    );
  } else {
    const common: StepProps = { mode, draft, update, onValidity, serverErrors };
    const body = {
      client: <BrokerClientStep {...common} />,
      vehicle: customer ? (
        <VehicleStep {...common} vehicles={vehicleList} drivers={driverList} />
      ) : (
        <BrokerVehicleStep {...common} />
      ),
      driver: <BrokerDriverStep {...common} />,
      type: <TypeStep {...common} vehicle={risk} />,
      coverages: <CoveragesStep {...common} vehicle={risk} />,
      period: <PeriodStep {...common} />,
      summary: (
        <SummaryStep
          mode={mode}
          draft={draft}
          vehicle={risk}
          driver={driverInfo}
          goTo={goTo}
          onCreated={onCreated}
          onProblem={onProblem}
          onBack={() => goTo(steps[stepIndex - 1] ?? 'summary')}
        />
      ),
    }[step];

    content = (
      <Card>
        <CardContent>
          <Stack spacing={3}>
            <Typography variant="h6" component="h2">
              {stepTitle}
            </Typography>
            {body}
            {step !== 'summary' && (
              <Stack direction="row" spacing={2} justifyContent="space-between">
                <Button
                  variant="text"
                  disabled={stepIndex === 0}
                  onClick={() => goTo(steps[stepIndex - 1] ?? steps[0] ?? 'summary')}
                >
                  {t('quoteWizard.back')}
                </Button>
                <Button
                  variant="contained"
                  disabled={!valid}
                  onClick={() => {
                    setNotice(null);
                    goTo(steps[stepIndex + 1] ?? 'summary');
                  }}
                >
                  {t('quoteWizard.next')}
                </Button>
              </Stack>
            )}
          </Stack>
        </CardContent>
      </Card>
    );
  }

  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {title}
        </Typography>
        {wide ? (
          <Stepper activeStep={stepIndex} alternativeLabel>
            {steps.map((key) => (
              <Step key={key}>
                <StepLabel>{t(`quoteWizard.steps.${key}`)}</StepLabel>
              </Step>
            ))}
          </Stepper>
        ) : (
          <Stack spacing={0.5}>
            <Typography variant="body2" color="text.secondary" data-testid="quote-step-of">
              {t('quoteWizard.stepOf', { n: stepIndex + 1, total: steps.length })} · {stepTitle}
            </Typography>
            <MobileStepper
              variant="progress"
              steps={steps.length}
              position="static"
              activeStep={stepIndex}
              backButton={null}
              nextButton={null}
              sx={{
                p: 0,
                bgcolor: 'transparent',
                '& .MuiMobileStepper-progress': { width: '100%' },
              }}
            />
          </Stack>
        )}
        {notice && (
          <Alert severity={notice.severity} onClose={() => setNotice(null)}>
            {notice.message}
          </Alert>
        )}
        {content}
      </Stack>
    </Container>
  );
}
