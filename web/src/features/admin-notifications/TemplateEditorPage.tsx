import ArrowBack from '@mui/icons-material/ArrowBack';
import ArrowDownward from '@mui/icons-material/ArrowDownward';
import ArrowUpward from '@mui/icons-material/ArrowUpward';
import Alert from '@mui/material/Alert';
import Autocomplete from '@mui/material/Autocomplete';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Paper from '@mui/material/Paper';
import Skeleton from '@mui/material/Skeleton';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useBlocker, useNavigate, useParams } from 'react-router-dom';
import {
  getNotificationTemplate,
  listNotificationTemplates,
  previewNotificationTemplate,
  resetNotificationTemplate,
  updateNotificationTemplate,
  type NotificationTemplateDetail,
  type NotificationTemplatePreview,
} from '../../api/generated';
import {
  applyFieldErrors,
  fieldMessage,
  getProblem,
  problemMessage,
  type Problem,
} from '../../errors/problem';
import { formatDateTime } from '../../i18n/format';
import { QueueError } from '../insurer-proposals/common';
import { TemplatePreview } from './TemplatePreview';
import { TestSendDialog } from './TestSendDialog';
import { insertAt, VariableChips } from './VariableChips';

export const templatesPath = '/admin/notificacoes';
export const templateKey = (id: string) => ['notification-template', id] as const;
export const templatesKey = ['notification-templates'] as const;

/** Atraso da pré-visualização depois da última alteração. */
export const previewDelayMs = 500;
const maxSubject = 200;

type Field = 'subject' | 'body' | 'whatsappTemplateName' | 'whatsappParams';
const fields: readonly Field[] = ['subject', 'body', 'whatsappTemplateName', 'whatsappParams'];
type TextKey = 'subject' | 'body';

/** Valores editáveis do modelo. */
export interface TemplateValues {
  subject: string;
  body: string;
  whatsappTemplateName: string;
  whatsappParams: string[];
  active: boolean;
}

export function valuesOf(template: NotificationTemplateDetail): TemplateValues {
  return {
    subject: template.subject ?? '',
    body: template.body,
    whatsappTemplateName: template.whatsappTemplateName ?? '',
    whatsappParams: template.whatsappParams ?? [],
    active: template.active,
  };
}

function sameValues(a: TemplateValues, b: TemplateValues): boolean {
  return (
    a.subject === b.subject &&
    a.body === b.body &&
    a.whatsappTemplateName === b.whatsappTemplateName &&
    a.active === b.active &&
    a.whatsappParams.join('\u0000') === b.whatsappParams.join('\u0000')
  );
}

function useFieldText() {
  const { t } = useTranslation();
  return (code: string | undefined) =>
    !code
      ? undefined
      : code === 'TOO_LONG'
        ? t('notificationTemplates.editor.tooLong')
        : fieldMessage(t, code);
}

/** Etiquetas para os outros canais e línguas do mesmo código. */
function Variants({
  template,
  onOpen,
}: {
  template: NotificationTemplateDetail;
  onOpen(id: string): void;
}) {
  const { t } = useTranslation();
  const query = useQuery({
    queryKey: [...templatesKey, 'variants', template.code],
    queryFn: ({ signal }) =>
      listNotificationTemplates({ code: template.code, size: 50 }, undefined, signal),
  });
  // O filtro `code` é por prefixo: ficam só as linhas do mesmo código.
  const rows = (query.data?.content ?? []).filter((row) => row.code === template.code);
  if (rows.length <= 1) return null;
  return (
    <Stack spacing={0.5}>
      <Typography variant="caption" color="text.secondary">
        {t('notificationTemplates.variants')}
      </Typography>
      <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap" data-testid="template-variants">
        {rows.map((row) => {
          const label = `${t(`channel.${row.channel}`)} · ${t(`notificationTemplates.locale.${row.locale}`, { defaultValue: row.locale })}`;
          const current = row.id === template.id;
          return (
            <Chip
              key={row.id}
              label={label}
              color={current ? 'primary' : 'default'}
              variant={current ? 'filled' : 'outlined'}
              onClick={current ? undefined : () => onOpen(row.id)}
              aria-current={current ? 'page' : undefined}
            />
          );
        })}
      </Stack>
    </Stack>
  );
}

/** Parâmetros do modelo WhatsApp, por ordem: selecção múltipla e botões para subir e descer. */
function WhatsappParams({
  variables,
  value,
  onChange,
  error,
}: {
  variables: string[];
  value: string[];
  onChange(value: string[]): void;
  error?: string;
}) {
  const { t } = useTranslation();
  const move = (index: number, delta: number) => {
    const next = [...value];
    const target = index + delta;
    if (target < 0 || target >= next.length) return;
    [next[index], next[target]] = [next[target] as string, next[index] as string];
    onChange(next);
  };
  return (
    <Stack spacing={1}>
      <Autocomplete
        multiple
        options={variables}
        value={value}
        onChange={(_, next) => onChange(next)}
        filterSelectedOptions
        renderInput={(params) => (
          <TextField
            {...params}
            label={t('notificationTemplates.editor.whatsappParams')}
            error={!!error}
            helperText={error}
          />
        )}
      />
      {value.length > 1 && (
        <List dense disablePadding>
          {value.map((name, index) => (
            <ListItem
              key={name}
              disableGutters
              secondaryAction={
                <Stack direction="row">
                  <IconButton
                    size="small"
                    disabled={index === 0}
                    onClick={() => move(index, -1)}
                    aria-label={t('notificationTemplates.editor.moveUp', { name })}
                  >
                    <ArrowUpward fontSize="small" />
                  </IconButton>
                  <IconButton
                    size="small"
                    disabled={index === value.length - 1}
                    onClick={() => move(index, 1)}
                    aria-label={t('notificationTemplates.editor.moveDown', { name })}
                  >
                    <ArrowDownward fontSize="small" />
                  </IconButton>
                </Stack>
              }
            >
              <ListItemText primary={`${index + 1}. ${name}`} />
            </ListItem>
          ))}
        </List>
      )}
    </Stack>
  );
}

function EditorForm({
  template,
  onSaved,
  onReload,
}: {
  template: NotificationTemplateDetail;
  onSaved(saved: NotificationTemplateDetail, notice: string): void;
  onReload(): void;
}) {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const fieldText = useFieldText();
  const initial = useRef(valuesOf(template)).current;
  const [values, setValues] = useState<TemplateValues>(initial);
  const [errors, setErrors] = useState<Partial<Record<Field, string>>>({});
  const [conflict, setConflict] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [preview, setPreview] = useState<NotificationTemplatePreview | null>(null);
  const [previewing, setPreviewing] = useState(false);
  const [confirmReset, setConfirmReset] = useState(false);
  const [testing, setTesting] = useState(false);
  const inputs = useRef<Partial<Record<TextKey, HTMLInputElement | HTMLTextAreaElement>>>({});
  const lastFocused = useRef<TextKey>('body');
  const hasSubject = template.channel === 'EMAIL' || template.channel === 'PUSH';
  const whatsapp = template.channel === 'WHATSAPP';
  const dirty = !sameValues(values, initial);

  const setError = (field: Field, error: { message: string }) =>
    setErrors((current) => ({ ...current, [field]: error.message }));
  const change = <K extends keyof TemplateValues>(key: K, value: TemplateValues[K]) => {
    setValues((current) => ({ ...current, [key]: value }));
    if (key !== 'active') setErrors((current) => ({ ...current, [key]: undefined }));
  };

  // Pré-visualização 500 ms depois da última alteração do assunto ou do texto.
  useEffect(() => {
    const controller = new AbortController();
    const timer = setTimeout(() => {
      setPreviewing(true);
      previewNotificationTemplate(
        template.id,
        { subject: hasSubject ? values.subject : undefined, body: values.body },
        undefined,
        controller.signal,
      )
        .then((result) => {
          setPreview(result);
          setErrors((current) => ({ ...current, subject: undefined, body: undefined }));
        })
        .catch((error: unknown) => {
          if (controller.signal.aborted) return;
          const problem = getProblem(error);
          applyFieldErrors(problem, ['subject', 'body'] as const, setError);
        })
        .finally(() => {
          if (!controller.signal.aborted) setPreviewing(false);
        });
    }, previewDelayMs);
    return () => {
      clearTimeout(timer);
      controller.abort();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [template.id, values.subject, values.body]);

  const fail = (problem: Problem) => {
    if (problem.code === 'CONCURRENT_MODIFICATION') {
      setConflict(true);
      return;
    }
    if (applyFieldErrors(problem, fields, setError)) return;
    setMessage(problemMessage(t, problem));
  };

  const save = useMutation({
    mutationFn: () =>
      updateNotificationTemplate(template.id, {
        subject: hasSubject ? values.subject : undefined,
        body: values.body,
        active: values.active,
        whatsappTemplateName: whatsapp ? values.whatsappTemplateName : undefined,
        whatsappParams: whatsapp ? values.whatsappParams : undefined,
        version: template.version,
      }),
    onSuccess: (saved) => onSaved(saved, t('notificationTemplates.editor.saved')),
    onError: (error) => fail(getProblem(error)),
  });
  const reset = useMutation({
    mutationFn: () => resetNotificationTemplate(template.id),
    onSuccess: (saved) => onSaved(saved, t('notificationTemplates.editor.resetDone')),
    onError: (error) => fail(getProblem(error)),
  });
  const busy = save.isPending || reset.isPending;

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      dirty && !busy && currentLocation.pathname !== nextLocation.pathname,
  );

  const submit = () => {
    setMessage(null);
    setConflict(false);
    const local: Partial<Record<Field, string>> = {};
    if (hasSubject) {
      if (!values.subject.trim()) local.subject = 'REQUIRED';
      else if (values.subject.length > maxSubject) local.subject = 'TOO_LONG';
    }
    if (!values.body.trim()) local.body = 'REQUIRED';
    if (whatsapp && !values.whatsappTemplateName.trim()) local.whatsappTemplateName = 'REQUIRED';
    if (Object.keys(local).length > 0) {
      setErrors((current) => ({ ...current, ...local }));
      return;
    }
    save.mutate();
  };

  const insert = (name: string) => {
    const field = hasSubject ? lastFocused.current : 'body';
    const element = inputs.current[field];
    const { text, caret } = insertAt(
      values[field],
      name,
      element?.selectionStart,
      element?.selectionEnd,
    );
    change(field, text);
    requestAnimationFrame(() => {
      if (!element) return;
      element.focus();
      element.setSelectionRange(caret, caret);
    });
  };

  const textInput = (field: TextKey) => ({
    inputRef: (element: HTMLInputElement | HTMLTextAreaElement | null) => {
      if (element) inputs.current[field] = element;
    },
    onFocus: () => {
      lastFocused.current = field;
    },
  });

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <div>
        <Button component={RouterLink} to={templatesPath} startIcon={<ArrowBack />}>
          {t('notificationTemplates.backToList')}
        </Button>
      </div>
      <Stack spacing={0.5}>
        <Typography variant="h5" component="h1" sx={{ wordBreak: 'break-all' }}>
          {template.code}
        </Typography>
        <Typography color="text.secondary">
          {t(`channel.${template.channel}`)} ·{' '}
          {t(`notificationTemplates.locale.${template.locale}`, { defaultValue: template.locale })}
          {template.customized && ` · ${t('notificationTemplates.customized')}`}
        </Typography>
        <Typography variant="caption" color="text.secondary">
          {formatDateTime(template.updatedAt, i18n.language)} · {template.updatedBy}
        </Typography>
      </Stack>
      <Variants template={template} onOpen={(id) => navigate(`${templatesPath}/${id}`)} />
      {conflict && (
        <Alert
          severity="warning"
          role="alert"
          action={
            <Button color="inherit" size="small" onClick={onReload}>
              {t('common.reload')}
            </Button>
          }
        >
          {t('notificationTemplates.editor.conflict')}
        </Alert>
      )}
      {message && (
        <Alert severity="error" role="alert" onClose={() => setMessage(null)}>
          {message}
        </Alert>
      )}
      <Box
        sx={{
          display: 'grid',
          gap: 2,
          gridTemplateColumns: { xs: 'minmax(0, 1fr)', md: 'minmax(0, 3fr) minmax(0, 2fr)' },
          alignItems: 'start',
        }}
      >
        <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 }, minWidth: 0 }}>
          <Stack spacing={2}>
            {hasSubject && (
              <TextField
                label={
                  template.channel === 'PUSH'
                    ? t('notificationTemplates.editor.pushTitle')
                    : t('notificationTemplates.editor.subject')
                }
                value={values.subject}
                onChange={(event) => change('subject', event.target.value)}
                required
                fullWidth
                error={!!errors.subject}
                helperText={fieldText(errors.subject)}
                {...textInput('subject')}
              />
            )}
            <TextField
              label={t('notificationTemplates.editor.body')}
              value={values.body}
              onChange={(event) => change('body', event.target.value)}
              required
              fullWidth
              multiline
              minRows={6}
              error={!!errors.body}
              helperText={fieldText(errors.body)}
              slotProps={{ htmlInput: { sx: { fontFamily: 'monospace' } } }}
              {...textInput('body')}
            />
            {whatsapp && (
              <>
                <TextField
                  label={t('notificationTemplates.editor.whatsappTemplateName')}
                  value={values.whatsappTemplateName}
                  onChange={(event) => change('whatsappTemplateName', event.target.value)}
                  required
                  fullWidth
                  error={!!errors.whatsappTemplateName}
                  helperText={fieldText(errors.whatsappTemplateName)}
                />
                <WhatsappParams
                  variables={template.variables}
                  value={values.whatsappParams}
                  onChange={(next) => change('whatsappParams', next)}
                  error={fieldText(errors.whatsappParams)}
                />
              </>
            )}
            <VariableChips variables={template.variables} onInsert={insert} />
            <FormControlLabel
              control={
                <Switch
                  checked={values.active}
                  onChange={(event) => change('active', event.target.checked)}
                />
              }
              label={t('notificationTemplates.editor.active')}
            />
            <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
              <Button variant="contained" onClick={submit} disabled={!dirty || busy}>
                {t('common.save')}
              </Button>
              {template.customized && (
                <Button variant="outlined" onClick={() => setConfirmReset(true)} disabled={busy}>
                  {t('notificationTemplates.editor.reset')}
                </Button>
              )}
              <Button variant="outlined" onClick={() => setTesting(true)}>
                {t('notificationTemplates.editor.test')}
              </Button>
            </Stack>
          </Stack>
        </Paper>
        <TemplatePreview channel={template.channel} preview={preview} loading={previewing} />
      </Box>

      <Dialog open={confirmReset} onClose={() => setConfirmReset(false)}>
        <DialogTitle>{t('notificationTemplates.editor.resetConfirmTitle')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('notificationTemplates.editor.resetConfirm')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmReset(false)}>{t('common.cancel')}</Button>
          <Button
            variant="contained"
            onClick={() => {
              setConfirmReset(false);
              setMessage(null);
              setConflict(false);
              reset.mutate();
            }}
          >
            {t('notificationTemplates.editor.reset')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={blocker.state === 'blocked'} onClose={() => blocker.reset?.()}>
        <DialogTitle>{t('notificationTemplates.editor.leaveTitle')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('notificationTemplates.editor.leaveText')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => blocker.reset?.()}>
            {t('notificationTemplates.editor.stay')}
          </Button>
          <Button variant="contained" onClick={() => blocker.proceed?.()}>
            {t('notificationTemplates.editor.leave')}
          </Button>
        </DialogActions>
      </Dialog>

      <TestSendDialog
        open={testing}
        templateId={template.id}
        channel={template.channel}
        onClose={() => setTesting(false)}
      />
    </Stack>
  );
}

/** `/admin/notificacoes/:id` (S6-05 §7): edição, pré-visualização e teste de um modelo. */
export function TemplateEditorPage() {
  const { t } = useTranslation();
  const { id = '' } = useParams();
  const queryClient = useQueryClient();
  const [notice, setNotice] = useState<string | null>(null);
  const query = useQuery({
    queryKey: templateKey(id),
    queryFn: ({ signal }) => getNotificationTemplate(id, undefined, signal),
  });

  if (query.isLoading) {
    return (
      <Stack spacing={2} data-testid="template-loading">
        <Skeleton variant="text" width="40%" height={40} />
        <Skeleton variant="rectangular" height={240} sx={{ borderRadius: 1 }} />
      </Stack>
    );
  }
  if (query.isError && !query.data) {
    const problem = getProblem(query.error);
    if (problem.status === 404 || problem.code === 'TEMPLATE_NOT_FOUND') {
      return (
        <Alert severity="warning" role="alert">
          <Stack spacing={1} alignItems="flex-start">
            <span>{t('notificationTemplates.notFound')}</span>
            <Link component={RouterLink} to={templatesPath}>
              {t('notificationTemplates.backToList')}
            </Link>
          </Stack>
        </Alert>
      );
    }
    return <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  }
  const template = query.data;
  if (!template) return null;

  return (
    <>
      <EditorForm
        key={`${template.id}-${template.version}`}
        template={template}
        onSaved={(saved, text) => {
          queryClient.setQueryData(templateKey(id), saved);
          void queryClient.invalidateQueries({ queryKey: templatesKey });
          setNotice(text);
        }}
        onReload={() => void query.refetch()}
      />
      <Snackbar
        open={notice !== null}
        autoHideDuration={6000}
        onClose={() => setNotice(null)}
        message={notice}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />
    </>
  );
}
