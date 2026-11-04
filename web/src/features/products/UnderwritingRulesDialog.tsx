import { zodResolver } from '@hookform/resolvers/zod';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import InputAdornment from '@mui/material/InputAdornment';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import {
  getUnderwritingRules,
  updateUnderwritingRules,
  type UnderwritingRulesResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { formatAmount, parseMeticais } from '../../format/money';
import { productVersionQueryKey } from './productQueries';

/** Regras no formato `underwriting-v1` (dinheiro em centavos). */
export interface UnderwritingRulesV1 {
  schemaVersion: 1;
  autoAccept: {
    enabled: boolean;
    simplifiedKycMaxPremium: { amount: number; currency: 'MZN' };
    liability: { requireAllDeclarationsNegative: boolean };
    ownDamage: {
      maxVehicleValue: { amount: number; currency: 'MZN' };
      requireAllDeclarationsNegative: boolean;
    };
  };
}

type FormValues = {
  enabled: boolean;
  simplifiedKycMaxPremium: string;
  liabilityRequireAllNegative: boolean;
  ownDamageMaxVehicleValue: string;
  ownDamageRequireAllNegative: boolean;
};
type FormField = keyof FormValues;

/** Máximo do schema (`positiveMoney`). */
const maxAmount = 100_000_000_000;

/** Montante escrito → centavos, ou o código do erro (`validation.<código>`). */
export function amountOf(text: string): number | 'INVALID_MONEY' | 'POSITIVE' {
  const parsed = parseMeticais(text);
  if (parsed === 'invalid' || (parsed !== null && parsed > maxAmount)) return 'INVALID_MONEY';
  if (parsed === null || parsed <= 0) return 'POSITIVE';
  return parsed;
}

const schema = z
  .object({
    enabled: z.boolean(),
    simplifiedKycMaxPremium: z.string(),
    liabilityRequireAllNegative: z.boolean(),
    ownDamageMaxVehicleValue: z.string(),
    ownDamageRequireAllNegative: z.boolean(),
  })
  .superRefine((values, context) => {
    // Desligada, os montantes ficam escondidos: grava-se o que havia (ver `toRules`).
    if (!values.enabled) return;
    for (const field of ['simplifiedKycMaxPremium', 'ownDamageMaxVehicleValue'] as const) {
      const amount = amountOf(values[field]);
      if (typeof amount !== 'number') {
        context.addIssue({ code: z.ZodIssueCode.custom, path: [field], message: amount });
      }
    }
  });

function readRules(response: UnderwritingRulesResponse): UnderwritingRulesV1 {
  return response.rules as unknown as UnderwritingRulesV1;
}

function toForm(rules: UnderwritingRulesV1): FormValues {
  const auto = rules.autoAccept;
  return {
    enabled: auto.enabled,
    simplifiedKycMaxPremium: formatAmount(auto.simplifiedKycMaxPremium),
    liabilityRequireAllNegative: auto.liability.requireAllDeclarationsNegative,
    ownDamageMaxVehicleValue: formatAmount(auto.ownDamage.maxVehicleValue),
    ownDamageRequireAllNegative: auto.ownDamage.requireAllDeclarationsNegative,
  };
}

/** Formulário → regras; um montante inválido escondido (desligada) mantém o valor lido. */
export function toRules(values: FormValues, loaded: UnderwritingRulesV1): UnderwritingRulesV1 {
  const amount = (text: string, fallback: number) => {
    const parsed = amountOf(text);
    return typeof parsed === 'number' ? parsed : fallback;
  };
  return {
    schemaVersion: 1,
    autoAccept: {
      enabled: values.enabled,
      simplifiedKycMaxPremium: {
        amount: amount(
          values.simplifiedKycMaxPremium,
          loaded.autoAccept.simplifiedKycMaxPremium.amount,
        ),
        currency: 'MZN',
      },
      liability: { requireAllDeclarationsNegative: values.liabilityRequireAllNegative },
      ownDamage: {
        maxVehicleValue: {
          amount: amount(
            values.ownDamageMaxVehicleValue,
            loaded.autoAccept.ownDamage.maxVehicleValue.amount,
          ),
          currency: 'MZN',
        },
        requireAllDeclarationsNegative: values.ownDamageRequireAllNegative,
      },
    },
  };
}

/** Campo do pedido (`rules.autoAccept.…`) → campo do formulário. */
function formFieldOf(field: string): FormField | null {
  const path = field.replace(/^rules\.?/, '');
  if (path.startsWith('autoAccept.simplifiedKycMaxPremium')) return 'simplifiedKycMaxPremium';
  if (path.startsWith('autoAccept.ownDamage.maxVehicleValue')) return 'ownDamageMaxVehicleValue';
  if (path.startsWith('autoAccept.ownDamage.requireAllDeclarationsNegative')) {
    return 'ownDamageRequireAllNegative';
  }
  if (path.startsWith('autoAccept.liability')) return 'liabilityRequireAllNegative';
  if (path.startsWith('autoAccept.enabled')) return 'enabled';
  return null;
}

export const underwritingRulesKey = (versionId: string) =>
  ['underwriting-rules', versionId] as const;

export interface UnderwritingRulesDialogProps {
  versionId: string;
  /** Versão em rascunho (`editable` da versão do produto). */
  editable: boolean;
  onClose(): void;
}

/** Regras de aceitação de uma versão do produto, sem editar JSON. */
export function UnderwritingRulesDialog({
  versionId,
  editable,
  onClose,
}: UnderwritingRulesDialogProps) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.productManage);
  const readOnly = !editable || !canManage;
  const [message, setMessage] = useState<string | null>(null);
  const [conflict, setConflict] = useState<'notEditable' | 'conflict' | null>(null);
  const [saving, setSaving] = useState(false);

  const query = useQuery({
    queryKey: underwritingRulesKey(versionId),
    queryFn: ({ signal }) => getUnderwritingRules(versionId, undefined, signal),
  });
  const {
    control,
    handleSubmit,
    reset,
    setError,
    watch,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      enabled: true,
      simplifiedKycMaxPremium: '',
      liabilityRequireAllNegative: false,
      ownDamageMaxVehicleValue: '',
      ownDamageRequireAllNegative: true,
    },
  });
  const data = query.data;
  useEffect(() => {
    if (data) reset(toForm(readRules(data)));
  }, [data, reset]);
  const enabled = watch('enabled');

  const errorText = (field: FormField) => {
    const code = errors[field]?.message;
    if (!code) return undefined;
    return code === 'POSITIVE' ? t('underwritingRules.positive') : fieldMessage(t, code);
  };

  const save = handleSubmit(async (values) => {
    if (!data || readOnly) return;
    setSaving(true);
    setMessage(null);
    setConflict(null);
    try {
      const saved = await updateUnderwritingRules(versionId, {
        rules: toRules(values, readRules(data)) as unknown as UnderwritingRulesResponse['rules'],
        version: data.version,
      });
      queryClient.setQueryData(underwritingRulesKey(versionId), saved);
      // A versão do produto mudou (`version`): o editor tem de a ler de novo.
      void queryClient.invalidateQueries({ queryKey: productVersionQueryKey(versionId) });
      onClose();
    } catch (error) {
      const problem = getProblem(error);
      if (problem.status === 409) {
        setConflict(problem.code === 'CONCURRENT_MODIFICATION' ? 'conflict' : 'notEditable');
      } else {
        let applied = false;
        for (const fieldError of problem.errors) {
          const field = formFieldOf(fieldError.field);
          if (field) {
            setError(field, { type: 'server', message: fieldError.code });
            applied = true;
          }
        }
        if (!applied) setMessage(problemMessage(t, problem));
      }
    } finally {
      setSaving(false);
    }
  });

  const reload = async () => {
    setConflict(null);
    const fresh = await query.refetch();
    if (fresh.data) reset(toForm(readRules(fresh.data)));
  };

  const amountField = (
    field: 'simplifiedKycMaxPremium' | 'ownDamageMaxVehicleValue',
    label: string,
  ) => (
    <Controller
      control={control}
      name={field}
      render={({ field: input }) => (
        <TextField
          {...input}
          label={label}
          disabled={readOnly}
          error={!!errors[field]}
          helperText={errorText(field)}
          autoComplete="off"
          onBlur={() => {
            const amount = amountOf(input.value);
            if (typeof amount === 'number') input.onChange(formatAmount({ amount }));
            input.onBlur();
          }}
          slotProps={{
            htmlInput: { inputMode: 'decimal' },
            input: { endAdornment: <InputAdornment position="end">MT</InputAdornment> },
          }}
        />
      )}
    />
  );
  const switchField = (
    field: 'enabled' | 'liabilityRequireAllNegative' | 'ownDamageRequireAllNegative',
    label: string,
  ) => (
    <Controller
      control={control}
      name={field}
      render={({ field: input }) => (
        <FormControlLabel
          control={
            <Switch
              checked={input.value}
              onChange={(event) => input.onChange(event.target.checked)}
              disabled={readOnly}
            />
          }
          label={label}
        />
      )}
    />
  );

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="underwriting-title">
      <DialogTitle id="underwriting-title">{t('underwritingRules.title')}</DialogTitle>
      <DialogContent>
        {query.isLoading ? (
          <Stack spacing={1}>
            <Skeleton height={40} />
            <Skeleton height={40} />
            <Skeleton height={40} />
          </Stack>
        ) : !data ? (
          <Alert severity="error" role="alert">
            {problemMessage(t, getProblem(query.error))}
          </Alert>
        ) : (
          <Stack
            component="form"
            id="underwriting-form"
            noValidate
            spacing={2}
            sx={{ pt: 1 }}
            onSubmit={(event) => void save(event)}
          >
            {!editable && (
              <Alert severity="info" data-testid="underwriting-read-only">
                {t('underwritingRules.readOnly')}
              </Alert>
            )}
            {editable && !canManage && (
              <Alert severity="info" data-testid="underwriting-read-only">
                {t('underwritingRules.noPermission')}
              </Alert>
            )}
            {data.isDefault && <Alert severity="info">{t('underwritingRules.isDefault')}</Alert>}
            {conflict && (
              <Alert
                severity="warning"
                role="alert"
                action={
                  <Button color="inherit" size="small" onClick={() => void reload()}>
                    {t('common.reload')}
                  </Button>
                }
              >
                {t(`underwritingRules.${conflict}`)}
              </Alert>
            )}
            {switchField('enabled', t('underwritingRules.enabled'))}
            {enabled && (
              <>
                {amountField(
                  'simplifiedKycMaxPremium',
                  t('underwritingRules.simplifiedKycMaxPremium'),
                )}
                {switchField(
                  'liabilityRequireAllNegative',
                  t('underwritingRules.liabilityRequireAllNegative'),
                )}
                {amountField(
                  'ownDamageMaxVehicleValue',
                  t('underwritingRules.ownDamageMaxVehicleValue'),
                )}
                {switchField(
                  'ownDamageRequireAllNegative',
                  t('underwritingRules.ownDamageRequireAllNegative'),
                )}
              </>
            )}
            {message && (
              <Alert severity="error" role="alert">
                {message}
              </Alert>
            )}
          </Stack>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{readOnly ? t('common.close') : t('common.cancel')}</Button>
        {!readOnly && (
          <Button
            type="submit"
            form="underwriting-form"
            variant="contained"
            disabled={saving || !data}
          >
            {t('common.save')}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
