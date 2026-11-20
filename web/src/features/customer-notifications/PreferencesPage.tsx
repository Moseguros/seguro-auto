import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import Container from '@mui/material/Container';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import Link from '@mui/material/Link';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import Typography from '@mui/material/Typography';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useBlocker } from 'react-router-dom';
import {
  getGetMyNotificationPreferencesQueryKey,
  getMyNotificationPreferences,
  updateMyNotificationPreferences,
  type NotificationCategory,
  type NotificationCategoryPreferences,
  type NotificationCategoryPreferencesChange,
  type NotificationChannel,
  type NotificationChannelPreference,
  type NotificationPreferencesResponse,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import {
  changedCategories,
  draftFrom,
  serverCategoryErrors,
  validatePreferences,
  type PreferencesDraft,
} from './preferencesForm';

export const profilePath = '/cliente/perfil';

type CategoryErrors = Partial<Record<NotificationCategory, string>>;

/** Ajuda de um canal indisponível (sem contacto verificado ou sem a app). */
function UnavailableHelp({ channel }: { channel: NotificationChannel }) {
  const { t } = useTranslation();
  if (channel === 'PUSH') {
    return <FormHelperText sx={{ mt: 0, ml: 6 }}>{t('preferences.needsApp')}</FormHelperText>;
  }
  return (
    <FormHelperText sx={{ mt: 0, ml: 6 }}>
      {t(channel === 'EMAIL' ? 'preferences.needsEmail' : 'preferences.needsMsisdn')}{' '}
      <Link component={RouterLink} to={profilePath}>
        {t('preferences.addInProfile')}
      </Link>
    </FormHelperText>
  );
}

function ChannelSwitch({
  channel,
  checked,
  onChange,
}: {
  channel: NotificationChannelPreference;
  checked: boolean;
  onChange: (enabled: boolean) => void;
}) {
  const { t } = useTranslation();
  return (
    <Stack data-testid={`channel-${channel.channel}`}>
      <FormControlLabel
        control={
          <Switch
            checked={checked}
            disabled={!channel.available}
            inputProps={{ role: 'switch' }}
            onChange={(event) => onChange(event.target.checked)}
          />
        }
        label={t(`channel.${channel.channel}`)}
      />
      {!channel.available && <UnavailableHelp channel={channel.channel} />}
    </Stack>
  );
}

function CategorySection({
  category,
  draft,
  error,
  onChange,
}: {
  category: NotificationCategoryPreferences;
  draft: PreferencesDraft;
  error?: string;
  onChange: (channel: NotificationChannel, enabled: boolean) => void;
}) {
  const { t } = useTranslation();
  const headingId = `preferences-${category.category}`;
  return (
    <Card
      component="section"
      variant="outlined"
      elevation={0}
      aria-labelledby={headingId}
      data-testid={`category-${category.category}`}
    >
      <CardContent>
        <Stack spacing={1}>
          <Stack direction="row" alignItems="center" gap={1} flexWrap="wrap">
            <Typography variant="h6" component="h2" id={headingId}>
              {t(`notificationCategory.${category.category}`)}
            </Typography>
            {category.mandatory && (
              <Chip size="small" color="primary" label={t('preferences.mandatory')} />
            )}
          </Stack>
          {category.mandatory && (
            <Typography variant="body2" color="text.secondary">
              {t('preferences.mandatoryHelp')}
            </Typography>
          )}
          <Stack>
            {category.channels.map((channel) => (
              <ChannelSwitch
                key={channel.channel}
                channel={channel}
                checked={draft[category.category]?.[channel.channel] ?? channel.enabled}
                onChange={(enabled) => onChange(channel.channel, enabled)}
              />
            ))}
          </Stack>
          {error && (
            <Alert severity="error" role="alert">
              {fieldMessage(t, error)}
            </Alert>
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}

function PreferencesForm({ preferences }: { preferences: NotificationPreferencesResponse }) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [draft, setDraft] = useState<PreferencesDraft>(() => draftFrom(preferences));
  const [serverErrors, setServerErrors] = useState<CategoryErrors>({});
  const [saved, setSaved] = useState(false);

  const changes = useMemo(() => changedCategories(preferences, draft), [preferences, draft]);
  const localErrors = useMemo(() => validatePreferences(preferences, draft), [preferences, draft]);
  const dirty = changes.length > 0;
  const invalid = Object.keys(localErrors).length > 0;

  const save = useMutation({
    mutationFn: (categories: NotificationCategoryPreferencesChange[]) =>
      updateMyNotificationPreferences({ categories }),
    onSuccess: (response) => {
      queryClient.setQueryData(getGetMyNotificationPreferencesQueryKey(), response);
      setDraft(draftFrom(response));
      setServerErrors({});
      setSaved(true);
    },
    onError: (error, sent) => setServerErrors(serverCategoryErrors(getProblem(error), sent)),
  });

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      dirty && !save.isPending && currentLocation.pathname !== nextLocation.pathname,
  );
  useEffect(() => {
    if (!dirty) return;
    const handler = (event: BeforeUnloadEvent) => {
      event.preventDefault();
      event.returnValue = '';
    };
    window.addEventListener('beforeunload', handler);
    return () => window.removeEventListener('beforeunload', handler);
  }, [dirty]);

  function change(category: NotificationCategory, channel: NotificationChannel, enabled: boolean) {
    setDraft((current) => ({
      ...current,
      [category]: { ...current[category], [channel]: enabled },
    }));
    setServerErrors((current) => {
      if (!current[category]) return current;
      const next = { ...current };
      delete next[category];
      return next;
    });
  }

  const generalError =
    save.isError && Object.keys(serverErrors).length === 0
      ? problemMessage(t, getProblem(save.error))
      : null;

  return (
    <Stack spacing={2}>
      {preferences.categories.map((category) => (
        <CategorySection
          key={category.category}
          category={category}
          draft={draft}
          error={localErrors[category.category] ?? serverErrors[category.category]}
          onChange={(channel, enabled) => change(category.category, channel, enabled)}
        />
      ))}
      {generalError && (
        <Alert severity="error" role="alert">
          {generalError}
        </Alert>
      )}
      <Button
        variant="contained"
        disabled={!dirty || invalid || save.isPending}
        onClick={() => save.mutate(changes)}
        startIcon={save.isPending ? <CircularProgress size={16} /> : undefined}
        sx={{ alignSelf: { xs: 'stretch', sm: 'flex-start' } }}
      >
        {t('common.save')}
      </Button>
      <Snackbar
        open={saved}
        autoHideDuration={4000}
        onClose={() => setSaved(false)}
        message={t('preferences.saved')}
      />
      <Dialog open={blocker.state === 'blocked'} onClose={() => blocker.reset?.()}>
        <DialogTitle>{t('preferences.unsaved.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('preferences.unsaved.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => blocker.reset?.()}>{t('preferences.unsaved.stay')}</Button>
          <Button color="error" onClick={() => blocker.proceed?.()}>
            {t('preferences.unsaved.leave')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}

/** `/cliente/notificacoes/preferencias` (S6-06 §5): por que canais o cliente recebe cada aviso. */
export function PreferencesPage() {
  const { t } = useTranslation();
  const query = useQuery({
    queryKey: getGetMyNotificationPreferencesQueryKey(),
    queryFn: ({ signal }) => getMyNotificationPreferences(undefined, signal),
    refetchOnWindowFocus: false,
  });

  let content;
  if (query.isPending) content = <LoadingScreen />;
  else if (query.isError) {
    content = (
      <Alert
        severity="error"
        role="alert"
        action={
          <Button color="inherit" size="small" onClick={() => void query.refetch()}>
            {t('common.retry')}
          </Button>
        }
      >
        {problemMessage(t, getProblem(query.error))}
      </Alert>
    );
  } else {
    content = <PreferencesForm preferences={query.data} />;
  }

  return (
    <Container maxWidth="sm" disableGutters>
      <Stack spacing={2}>
        <Typography variant="h4" component="h1">
          {t('preferences.title')}
        </Typography>
        {content}
      </Stack>
    </Container>
  );
}
