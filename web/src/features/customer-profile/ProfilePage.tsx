import Alert from '@mui/material/Alert';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  getGetMyProfileQueryKey,
  getMyProfile,
  type CustomerProfileResponse,
} from '../../api/generated';
import { meQueryKey } from '../../auth/AuthProvider';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { ContactsSection } from './ContactsSection';
import { ProfileForm } from './ProfileForm';

/** `/cliente/perfil` (com `?completar=1` depois do primeiro login). */
export function ProfilePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params] = useSearchParams();
  const completing = params.get('completar') === '1';
  const [saved, setSaved] = useState(false);
  const queryKey = getGetMyProfileQueryKey();
  const profile = useQuery({
    queryKey,
    queryFn: ({ signal }) => getMyProfile(undefined, signal),
  });

  async function onSaved(value: CustomerProfileResponse) {
    queryClient.setQueryData(queryKey, value);
    // O nome e `profileComplete` do utilizador mudaram.
    await queryClient.invalidateQueries({ queryKey: meQueryKey });
    if (completing && value.complete) {
      navigate('/cliente', { replace: true, state: { profileSaved: true } });
    } else {
      setSaved(true);
    }
  }

  let form;
  if (profile.isPending) form = <LoadingScreen />;
  else if (profile.isError) {
    form = <Alert severity="error">{problemMessage(t, getProblem(profile.error))}</Alert>;
  } else {
    form = (
      <ProfileForm
        profile={profile.data}
        onSaved={onSaved}
        onConflict={() => void profile.refetch()}
      />
    );
  }

  return (
    <Container maxWidth="sm" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {t('profile.title')}
        </Typography>
        {completing && <Alert severity="info">{t('profile.completeNotice')}</Alert>}
        <Card>
          <CardContent>
            <Typography variant="h6" component="h2" sx={{ mb: 2 }}>
              {t('profile.personalData')}
            </Typography>
            {form}
          </CardContent>
        </Card>
        <ContactsSection />
      </Stack>
      <Snackbar
        open={saved}
        autoHideDuration={4000}
        onClose={() => setSaved(false)}
        message={t('profile.saved')}
      />
    </Container>
  );
}
