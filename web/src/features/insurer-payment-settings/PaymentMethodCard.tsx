import ExpandMore from '@mui/icons-material/ExpandMore';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useMutation } from '@tanstack/react-query';
import { useId, useMemo, useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import {
  disableInsurerPaymentMethod,
  enableInsurerPaymentMethod,
  testInsurerPaymentMethod,
  updateInsurerPaymentMethod,
  type LastTestResponse,
  type Money,
  type PaymentMethodResponse,
  type UpdatePaymentMethodRequest,
} from '../../api/generated';
import { MoneyField } from '../../components/MoneyField';
import { fieldMessage, getProblem, problemMessage, type Problem } from '../../errors/problem';
import { formatStamp } from '../insurer-payments/format';
import { ConfirmDialog } from './ConfirmDialog';
import { FileSourceStatusPanel } from './FileSourceStatusPanel';
import {
  isHiddenField,
  referenceKeyChanged,
  secretApplies,
  secretFieldsByMethod,
  secretRequired,
} from './methodRules';
import { FieldGrid, SchemaForm } from './SchemaForm';
import {
  applyDefaults,
  cleanSettings,
  errorTarget,
  type JsonSchema,
  type Settings,
} from './schemaPaths';
import { SecretFields, typedSecrets } from './SecretFields';
import { TestResultAlert } from './TestResultAlert';

interface GeneralForm {
  displayName: string;
  environment: string;
  minAmount: Money | null;
  maxAmount: Money | null;
  intentValidityMinutes: string;
  sortOrder: string;
}

type Confirm = 'enable' | 'disable' | 'save' | null;

export interface PaymentMethodCardProps {
  item: PaymentMethodResponse;
  /** A menos de 900 px o cartão é um `Accordion` (só um aberto). */
  narrow: boolean;
  expanded: boolean;
  onToggle(): void;
  /** Aviso curto da página ("Guardado"). */
  onNotice(text: string): void;
  /** O servidor devolveu o método actualizado. */
  onReplace(item: PaymentMethodResponse): void;
  /** Voltar a ler os métodos (depois de um teste ou de "Recarregar"). */
  onReload(): void;
}

function generalOf(item: PaymentMethodResponse): GeneralForm {
  return {
    displayName: item.displayName ?? '',
    environment: item.environment,
    minAmount: item.minAmount ?? null,
    maxAmount: item.maxAmount ?? null,
    intentValidityMinutes: String(item.intentValidityMinutes),
    sortOrder: String(item.sortOrder),
  };
}

function integerIn(text: string, min: number, max: number): boolean {
  const value = Number(text.trim());
  return /^\d+$/.test(text.trim()) && value >= min && value <= max;
}

/**
 * Configuração de uma forma de pagamento da seguradora (S5-12 §2): dados gerais, dados do método
 * (gerados pelo JSON Schema), segredos, "Guardar", "Testar ligação" e "Activar"/"Desactivar".
 * O componente é recriado quando muda a `version` do método (os segredos escritos ficam vazios).
 */
export function PaymentMethodCard({
  item,
  narrow,
  expanded,
  onToggle,
  onNotice,
  onReplace,
  onReload,
}: PaymentMethodCardProps) {
  const { t } = useTranslation();
  const method = item.method;
  const schema = item.settingsSchema as JsonSchema;
  const headingId = useId();

  const initialGeneral = useMemo(() => generalOf(item), [item]);
  const initialSettings = useMemo(
    () =>
      item.configured
        ? ((item.settings ?? {}) as Settings)
        : applyDefaults(schema, (item.settings ?? {}) as Settings),
    [item, schema],
  );
  const [general, setGeneral] = useState<GeneralForm>(initialGeneral);
  const [settings, setSettings] = useState<Settings>(initialSettings);
  const [secretValues, setSecretValues] = useState<Record<string, string>>({});
  const [moneyInvalid, setMoneyInvalid] = useState({ min: false, max: false });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [alert, setAlert] = useState<{ text: string; reload?: boolean } | null>(null);
  const [confirm, setConfirm] = useState<Confirm>(null);
  const [localTest, setLocalTest] = useState<LastTestResponse | undefined>(undefined);

  const hidden = (path: string) => isHiddenField(method, path, settings);
  const visibleSecrets = (secretFieldsByMethod[method] ?? Object.keys(item.secrets)).filter(
    (field) => secretApplies(method, field, settings),
  );
  const secretsToSend = typedSecrets(visibleSecrets, secretValues);
  const dirty =
    JSON.stringify(general) !== JSON.stringify(initialGeneral) ||
    JSON.stringify(settings) !== JSON.stringify(initialSettings) ||
    secretsToSend !== undefined;

  // Erros por destino: dados gerais, dados do método e segredos.
  const generalErrors: Record<string, string> = {};
  const settingsErrors: Record<string, string> = {};
  const secretErrors: Record<string, string> = {};
  for (const [field, code] of Object.entries(errors)) {
    const target = errorTarget(field);
    if (target.scope === 'settings') settingsErrors[target.path] = code;
    else if (target.scope === 'secrets') secretErrors[target.path] = code;
    else generalErrors[target.path] = code;
  }

  const handleProblem = (problem: Problem, fallback?: string) => {
    setErrors(Object.fromEntries(problem.errors.map((e) => [e.field, e.code])));
    switch (problem.code) {
      case 'VALIDATION_ERROR':
        setAlert({ text: t('paymentSettingsPage.errors.validation') });
        break;
      case 'CONCURRENT_MODIFICATION':
        setAlert({ text: t('paymentSettingsPage.errors.concurrent'), reload: true });
        break;
      case 'SECRET_STORE_UNAVAILABLE':
        setAlert({ text: t('paymentSettingsPage.errors.secretStore') });
        break;
      case 'PAYMENT_METHOD_INCOMPLETE':
        setAlert({ text: t('paymentSettingsPage.errors.incomplete') });
        break;
      default:
        setAlert({ text: fallback ?? problemMessage(t, problem) });
    }
  };

  const validate = (): Record<string, string> => {
    const found: Record<string, string> = {};
    if (general.displayName.trim().length > 60) found.displayName = 'TOO_LONG';
    if (
      general.minAmount &&
      general.maxAmount &&
      general.minAmount.amount > general.maxAmount.amount
    ) {
      found.maxAmount = 'MIN_GREATER_THAN_MAX';
    }
    if (method === 'MPESA' && !integerIn(general.intentValidityMinutes, 5, 1440)) {
      found.intentValidityMinutes = 'OUT_OF_RANGE';
    }
    if (!integerIn(general.sortOrder, 0, 99)) found.sortOrder = 'OUT_OF_RANGE';
    return found;
  };

  const save = useMutation({
    mutationFn: () => {
      const body: UpdatePaymentMethodRequest = {
        displayName: general.displayName.trim() || null,
        environment: general.environment as UpdatePaymentMethodRequest['environment'],
        minAmount: general.minAmount ?? undefined,
        maxAmount: general.maxAmount ?? undefined,
        intentValidityMinutes:
          method === 'MPESA' ? Number(general.intentValidityMinutes) : item.intentValidityMinutes,
        sortOrder: Number(general.sortOrder),
        settings: cleanSettings(schema, settings, hidden),
        ...(secretsToSend ? { secrets: secretsToSend } : {}),
        version: item.version ?? null,
      };
      return updateInsurerPaymentMethod(method, body);
    },
    onSuccess: (updated) => {
      onNotice(t('paymentSettingsPage.saved'));
      onReplace(updated);
    },
    onError: (error) => handleProblem(getProblem(error)),
  });

  const test = useMutation({
    mutationFn: () => testInsurerPaymentMethod(method),
    onSuccess: (result) => {
      setLocalTest({ at: result.testedAt, result: result.result, message: result.message });
      onReload();
    },
    onError: (error) => handleProblem(getProblem(error)),
  });

  const toggle = useMutation({
    mutationFn: (action: 'enable' | 'disable') =>
      action === 'enable'
        ? enableInsurerPaymentMethod(method, { version: item.version ?? 0 })
        : disableInsurerPaymentMethod(method, { version: item.version ?? 0 }),
    onSuccess: (updated, action) => {
      setConfirm(null);
      onNotice(
        t(action === 'enable' ? 'paymentSettingsPage.enabled' : 'paymentSettingsPage.disabled'),
      );
      onReplace(updated);
    },
    onError: (error) => {
      setConfirm(null);
      handleProblem(getProblem(error));
    },
  });

  const busy = save.isPending || test.isPending || toggle.isPending;

  const startSave = () => {
    setAlert(null);
    const found = validate();
    setErrors(found);
    if (Object.keys(found).length > 0 || moneyInvalid.min || moneyInvalid.max) {
      setAlert({ text: t('paymentSettingsPage.errors.validation') });
      return;
    }
    if (item.enabled && referenceKeyChanged(method, initialSettings, settings)) {
      setConfirm('save');
      return;
    }
    save.mutate();
  };

  const changeGeneral = (changes: Partial<GeneralForm>) =>
    setGeneral((current) => ({ ...current, ...changes }));

  const generalError = (field: keyof GeneralForm) =>
    generalErrors[field] ? fieldMessage(t, generalErrors[field]) : undefined;

  const state = !item.configured ? 'NOT_CONFIGURED' : item.enabled ? 'ACTIVE' : 'INACTIVE';
  const header = (
    <Stack spacing={1} sx={{ minWidth: 0, width: '100%' }}>
      <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
        <Typography id={headingId} variant="h6" component="h2" sx={{ mr: 1 }}>
          {t(`paymentMethod.${method}`)}
        </Typography>
        <Chip
          size="small"
          color={state === 'ACTIVE' ? 'success' : 'default'}
          variant={state === 'ACTIVE' ? 'filled' : 'outlined'}
          label={t(`paymentSettingsPage.state.${state}`)}
          data-testid="method-state"
        />
        <Chip
          size="small"
          variant="outlined"
          label={t(`paymentSettingsPage.environment.${item.environment}`)}
        />
      </Stack>
    </Stack>
  );

  const section = (title: string, children: ReactNode) => (
    <Stack spacing={2} component="section" aria-label={title}>
      <Typography variant="subtitle1" component="h3" fontWeight={600}>
        {title}
      </Typography>
      {children}
    </Stack>
  );

  const test_ = localTest ?? item.lastTest ?? undefined;
  const testDisabledReason = !item.configured
    ? undefined
    : dirty
      ? t('paymentSettingsPage.actions.testHint')
      : undefined;

  const body = (
    <Stack spacing={3} component="form" noValidate onSubmit={(e) => e.preventDefault()}>
      {item.lastError && (
        <Alert severity="error">
          {t('paymentSettingsPage.lastError', { date: formatStamp(item.lastError.at) })}
        </Alert>
      )}
      {section(
        t('paymentSettingsPage.general'),
        <FieldGrid>
          <TextField
            id={`${method}-displayName`}
            label={t('paymentSettingsPage.fields.displayName')}
            value={general.displayName}
            onChange={(e) => changeGeneral({ displayName: e.target.value })}
            error={!!generalErrors.displayName}
            helperText={generalError('displayName') ?? `${general.displayName.length}/60`}
            slotProps={{ htmlInput: { maxLength: 60 } }}
            disabled={busy}
            fullWidth
          />
          <TextField
            id={`${method}-environment`}
            select
            required
            label={t('paymentSettingsPage.fields.environment')}
            value={general.environment}
            onChange={(e) => changeGeneral({ environment: e.target.value })}
            error={!!generalErrors.environment}
            helperText={generalError('environment')}
            disabled={busy}
            fullWidth
          >
            {['SANDBOX', 'PRODUCTION'].map((env) => (
              <MenuItem key={env} value={env}>
                {t(`paymentSettingsPage.environment.${env}`)}
              </MenuItem>
            ))}
          </TextField>
          <MoneyField
            id={`${method}-minAmount`}
            label={t('paymentSettingsPage.fields.minAmount')}
            value={general.minAmount}
            onChange={(minAmount) => changeGeneral({ minAmount })}
            onValidityChange={(valid) => setMoneyInvalid((m) => ({ ...m, min: !valid }))}
            error={!!generalErrors.minAmount}
            helperText={generalError('minAmount')}
            disabled={busy}
          />
          <MoneyField
            id={`${method}-maxAmount`}
            label={t('paymentSettingsPage.fields.maxAmount')}
            value={general.maxAmount}
            onChange={(maxAmount) => changeGeneral({ maxAmount })}
            onValidityChange={(valid) => setMoneyInvalid((m) => ({ ...m, max: !valid }))}
            error={!!generalErrors.maxAmount}
            helperText={generalError('maxAmount')}
            disabled={busy}
          />
          {method === 'MPESA' && (
            <TextField
              id={`${method}-intentValidityMinutes`}
              type="number"
              label={t('paymentSettingsPage.fields.intentValidityMinutes')}
              value={general.intentValidityMinutes}
              onChange={(e) => changeGeneral({ intentValidityMinutes: e.target.value })}
              error={!!generalErrors.intentValidityMinutes}
              helperText={generalError('intentValidityMinutes')}
              slotProps={{ htmlInput: { min: 5, max: 1440, step: 1 } }}
              disabled={busy}
              fullWidth
            />
          )}
          <TextField
            id={`${method}-sortOrder`}
            type="number"
            label={t('paymentSettingsPage.fields.sortOrder')}
            value={general.sortOrder}
            onChange={(e) => changeGeneral({ sortOrder: e.target.value })}
            error={!!generalErrors.sortOrder}
            helperText={generalError('sortOrder')}
            slotProps={{ htmlInput: { min: 0, max: 99, step: 1 } }}
            disabled={busy}
            fullWidth
          />
        </FieldGrid>,
      )}
      <Divider />
      {section(
        t('paymentSettingsPage.methodData'),
        <SchemaForm
          method={method}
          schema={schema}
          value={settings}
          onChange={setSettings}
          errors={settingsErrors}
          isHidden={hidden}
          disabled={busy}
        />,
      )}
      {visibleSecrets.length > 0 && (
        <>
          <Divider />
          {section(
            t('paymentSettingsPage.secrets'),
            <SecretFields
              method={method}
              fields={visibleSecrets}
              status={item.secrets}
              required={(field) =>
                secretRequired(method, field, settings, item.secrets[field]?.required ?? false)
              }
              values={secretValues}
              onChange={(field, value) =>
                setSecretValues((current) => ({ ...current, [field]: value }))
              }
              errors={secretErrors}
              disabled={busy}
            />,
          )}
        </>
      )}
      {alert && (
        <Alert
          severity="error"
          role="alert"
          action={
            alert.reload ? (
              <Button color="inherit" size="small" onClick={onReload}>
                {t('paymentSettingsPage.actions.reload')}
              </Button>
            ) : undefined
          }
        >
          {alert.text}
        </Alert>
      )}
      <TestResultAlert test={test_} />
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} useFlexGap flexWrap="wrap">
        <Button variant="contained" onClick={startSave} disabled={busy}>
          {t('paymentSettingsPage.actions.save')}
        </Button>
        <Box component="span" sx={{ display: 'inline-flex', flexDirection: 'column' }}>
          <Button
            variant="outlined"
            onClick={() => {
              setAlert(null);
              test.mutate();
            }}
            disabled={busy || !item.configured || dirty}
            aria-describedby={testDisabledReason ? `${method}-test-hint` : undefined}
          >
            {t('paymentSettingsPage.actions.test')}
          </Button>
          {testDisabledReason && (
            <Typography id={`${method}-test-hint`} variant="caption" color="text.secondary">
              {testDisabledReason}
            </Typography>
          )}
        </Box>
        <Button
          variant="outlined"
          onClick={() => {
            setAlert(null);
            setConfirm(item.enabled ? 'disable' : 'enable');
          }}
          disabled={busy || !item.configured || item.version == null || dirty}
        >
          {t(
            item.enabled
              ? 'paymentSettingsPage.actions.disable'
              : 'paymentSettingsPage.actions.enable',
          )}
        </Button>
      </Stack>
      {method === 'BANK_REFERENCE' && item.configured && (
        <FileSourceStatusPanel placement="settings" />
      )}
      <ConfirmDialog
        open={confirm === 'enable' || confirm === 'disable'}
        title={t(
          confirm === 'disable'
            ? 'paymentSettingsPage.confirmDisable.title'
            : 'paymentSettingsPage.confirmEnable.title',
          { method: t(`paymentMethod.${method}`) },
        )}
        text={t(
          confirm === 'disable'
            ? 'paymentSettingsPage.confirmDisable.text'
            : 'paymentSettingsPage.confirmEnable.text',
        )}
        confirmLabel={t(
          confirm === 'disable'
            ? 'paymentSettingsPage.actions.disable'
            : 'paymentSettingsPage.actions.enable',
        )}
        busy={toggle.isPending}
        onConfirm={() => toggle.mutate(confirm === 'disable' ? 'disable' : 'enable')}
        onClose={() => setConfirm(null)}
      />
      <ConfirmDialog
        open={confirm === 'save'}
        title={t('paymentSettingsPage.confirmEntity.title')}
        text={t('paymentSettingsPage.confirmEntity.text')}
        confirmLabel={t('paymentSettingsPage.actions.save')}
        busy={save.isPending}
        onConfirm={() => {
          setConfirm(null);
          save.mutate();
        }}
        onClose={() => setConfirm(null)}
      />
    </Stack>
  );

  if (narrow) {
    return (
      <Accordion
        expanded={expanded}
        onChange={onToggle}
        disableGutters
        variant="outlined"
        data-testid={`payment-method-${method}`}
      >
        <AccordionSummary expandIcon={<ExpandMore />} aria-controls={`${method}-content`}>
          {header}
        </AccordionSummary>
        <AccordionDetails id={`${method}-content`}>{body}</AccordionDetails>
      </Accordion>
    );
  }
  return (
    <Paper
      variant="outlined"
      component="article"
      aria-labelledby={headingId}
      sx={{ p: { xs: 2, sm: 3 }, minWidth: 0 }}
      data-testid={`payment-method-${method}`}
    >
      <Stack spacing={3}>
        {header}
        {body}
      </Stack>
    </Paper>
  );
}
