import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useEffect, useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import {
  createPlateFormat,
  testPlateFormat,
  updatePlateFormat,
  type PlateFormatResponse,
  type TestPlateFormatResponse,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { fieldText } from '../organizations/OrganizationForm';

type Field = 'code' | 'label' | 'pattern' | 'displayTemplate' | 'example' | 'sortOrder';
const fields: Field[] = ['code', 'label', 'pattern', 'displayTemplate', 'example', 'sortOrder'];
const codePattern = /^[A-Z0-9_]{2,20}$/;
/** Espera depois da última tecla antes de testar. */
export const testDelayMs = 400;

type TestResult =
  { kind: 'result'; value: TestPlateFormatResponse } | { kind: 'error'; text: string };

/** Criar ou editar um formato de matrícula, com teste do padrão. */
export function PlateFormatDialog({
  format,
  onClose,
  onSaved,
  onConflict,
}: {
  format?: PlateFormatResponse;
  onClose(): void;
  onSaved(format: PlateFormatResponse): void;
  onConflict?(): void;
}) {
  const { t } = useTranslation();
  const creating = !format;
  const [values, setValues] = useState({
    code: format?.code ?? '',
    label: format?.label ?? '',
    pattern: format?.pattern ?? '',
    displayTemplate: format?.displayTemplate ?? '',
    example: format?.example ?? '',
    sortOrder: String(format?.sortOrder ?? 100),
  });
  const [errors, setErrors] = useState<Partial<Record<Field, string>>>({});
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [sample, setSample] = useState('');
  const [test, setTest] = useState<TestResult | null>(null);

  // Testa o padrão 400 ms depois da última alteração (ao valor, ao padrão ou ao modelo).
  useEffect(() => {
    const value = sample.trim();
    if (!value || !values.pattern) {
      setTest(null);
      return;
    }
    const controller = new AbortController();
    const timer = setTimeout(() => {
      testPlateFormat(
        { pattern: values.pattern, displayTemplate: values.displayTemplate, value },
        undefined,
        controller.signal,
      )
        .then((result) => setTest({ kind: 'result', value: result }))
        .catch((error: unknown) => {
          if (controller.signal.aborted) return;
          const problem = getProblem(error);
          const first = problem.errors[0];
          setTest({
            kind: 'error',
            text: first ? fieldText(t, first.code) : problemMessage(t, problem),
          });
        });
    }, testDelayMs);
    return () => {
      clearTimeout(timer);
      controller.abort();
    };
  }, [sample, values.pattern, values.displayTemplate, t]);

  const set = (field: Field, value: string) => setValues((v) => ({ ...v, [field]: value }));

  const validate = () => {
    const found: Partial<Record<Field, string>> = {};
    if (creating) {
      if (!values.code) found.code = 'REQUIRED';
      else if (!codePattern.test(values.code)) found.code = 'INVALID_CODE';
    }
    const label = values.label.trim();
    if (!label) found.label = 'REQUIRED';
    else if (label.length < 2 || label.length > 80) found.label = 'SIZE';
    if (!values.pattern) found.pattern = 'REQUIRED';
    else if (values.pattern.length > 200) found.pattern = 'SIZE';
    if (!values.displayTemplate.trim()) found.displayTemplate = 'REQUIRED';
    if (!values.example.trim()) found.example = 'REQUIRED';
    if (!/^\d{1,3}$/.test(values.sortOrder)) found.sortOrder = 'OUT_OF_RANGE';
    return found;
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const found = validate();
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    setSaving(true);
    const common = {
      label: values.label.trim(),
      pattern: values.pattern,
      displayTemplate: values.displayTemplate.trim(),
      example: values.example.trim(),
      sortOrder: Number(values.sortOrder),
    };
    try {
      onSaved(
        format
          ? await updatePlateFormat(format.id, { ...common, version: format.version })
          : await createPlateFormat({ ...common, code: values.code }),
      );
    } catch (error) {
      const problem = getProblem(error);
      setSaving(false);
      if (problem.code === 'PLATE_FORMAT_CODE_ALREADY_EXISTS') {
        setErrors({ code: problem.code });
        return;
      }
      if (problem.code === 'CONCURRENT_MODIFICATION') onConflict?.();
      const server: Partial<Record<Field, string>> = {};
      for (const e of problem.errors) {
        if ((fields as string[]).includes(e.field)) server[e.field as Field] = e.code;
      }
      if (Object.keys(server).length > 0) setErrors(server);
      else setMessage(problemMessage(t, problem));
    }
  };

  const field = (
    name: Field,
    options: { disabled?: boolean; required?: boolean; mono?: boolean; helper?: string } = {},
  ) => (
    <TextField
      label={t(`references.plateFields.${name}`)}
      value={values[name]}
      onChange={(event) =>
        set(name, name === 'code' ? event.target.value.toUpperCase() : event.target.value)
      }
      disabled={options.disabled}
      required={options.required}
      error={!!errors[name]}
      helperText={errors[name] ? fieldText(t, errors[name]!) : options.helper}
      autoComplete="off"
      fullWidth
      slotProps={{
        htmlInput: {
          spellCheck: false,
          style: options.mono ? { fontFamily: 'monospace' } : undefined,
        },
      }}
    />
  );

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="plate-title">
      <DialogTitle id="plate-title">
        {creating ? t('references.plates.newTitle') : t('references.plates.editTitle')}
      </DialogTitle>
      <DialogContent>
        <form id="plate-form" onSubmit={submit} noValidate>
          <Stack spacing={2} sx={{ pt: 1 }}>
            {field('code', { disabled: !creating, required: creating })}
            {field('label', { required: true })}
            {field('pattern', { required: true, mono: true })}
            {field('displayTemplate', { required: true, mono: true })}
            {field('example', { required: true })}
            {field('sortOrder', { helper: t('references.plates.sortOrderHelp') })}
            <Typography variant="body2" color="text.secondary">
              {t('references.plates.help')}
            </Typography>
            <Paper
              variant="outlined"
              sx={{ p: 2 }}
              component="section"
              aria-label={t('references.plates.test')}
            >
              <Stack spacing={1}>
                <Typography variant="subtitle2" component="h3">
                  {t('references.plates.test')}
                </Typography>
                <TextField
                  label={t('references.plates.testValue')}
                  value={sample}
                  onChange={(event) => setSample(event.target.value)}
                  autoComplete="off"
                  fullWidth
                  size="small"
                />
                <div aria-live="polite" data-testid="plate-test-result">
                  {test?.kind === 'result' &&
                    (test.value.matches ? (
                      <Typography color="success.main">
                        {t('references.plates.matches', { formatted: test.value.formatted ?? '' })}
                      </Typography>
                    ) : (
                      <Typography color="error">{t('references.plates.noMatch')}</Typography>
                    ))}
                  {test?.kind === 'error' && <Typography color="error">{test.text}</Typography>}
                </div>
              </Stack>
            </Paper>
          </Stack>
        </form>
        {message && (
          <Alert severity="error" role="alert" sx={{ mt: 2 }}>
            {message}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('common.cancel')}</Button>
        <Button type="submit" form="plate-form" variant="contained" disabled={saving}>
          {t('common.save')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
