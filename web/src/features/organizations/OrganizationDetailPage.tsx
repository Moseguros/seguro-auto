import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation, useParams } from 'react-router-dom';
import {
  getOrganization,
  listAssignableRoles,
  listOrganizationUsers,
  reactivateOrganization,
  updateOrganization,
  type OrganizationResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { LoadingScreen } from '../../auth/RequireAuth';
import { StatusChip } from '../../components/StatusChip';
import { getProblem, problemMessage } from '../../errors/problem';
import { UsersPanel } from '../users/UsersPanel';
import { assignableRolesQueryKey, usersQueryKey, type UsersScope } from '../users/usersApi';
import type { OrganizationDetailState } from './OrganizationCreatePage';
import { OrganizationForm } from './OrganizationForm';
import { toUpdateRequest, valuesFrom } from './organizationSchema';
import { SuspendDialog } from './SuspendDialog';

type TabKey = 'data' | 'users';

/** `/admin/organizacoes/:id`: dados, utilizadores e suspensão da organização. */
export function OrganizationDetailPage() {
  const { t } = useTranslation();
  const { id = '' } = useParams();
  const location = useLocation();
  const state = (location.state ?? {}) as OrganizationDetailState;
  const [tab, setTab] = useState<TabKey>(state.tab ?? 'data');
  const query = useQuery({
    queryKey: ['organization', id],
    queryFn: ({ signal }) => getOrganization(id, undefined, signal),
  });

  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    return <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>;
  }
  const organization = query.data;

  return (
    <Stack spacing={3} sx={{ maxWidth: 1100, minWidth: 0 }}>
      <Header organization={organization} />
      <ManagerWarning organizationId={organization.id} />
      <Tabs
        value={tab}
        onChange={(_, value: TabKey) => setTab(value)}
        variant="scrollable"
        allowScrollButtonsMobile
      >
        <Tab value="data" label={t('organizations.tabs.data')} />
        <Tab value="users" label={t('organizations.tabs.users')} />
      </Tabs>
      {tab === 'data' ? (
        <DataTab organization={organization} onReload={() => void query.refetch()} />
      ) : (
        <UsersPanel
          scope={{ kind: 'organization', organizationId: organization.id }}
          pendingInvite={state.pendingInvite}
        />
      )}
    </Stack>
  );
}

function Header({ organization }: { organization: OrganizationResponse }) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.organizationManage);
  const [suspending, setSuspending] = useState(false);
  const [confirmReactivate, setConfirmReactivate] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [done, setDone] = useState<string | null>(null);

  const updated = (next: OrganizationResponse, text: string) => {
    queryClient.setQueryData(['organization', next.id], next);
    void queryClient.invalidateQueries({ queryKey: ['organizations'] });
    setDone(text);
  };

  const reactivate = async () => {
    setConfirmReactivate(false);
    setMessage(null);
    try {
      updated(await reactivateOrganization(organization.id), t('organizations.reactivated'));
    } catch (e) {
      setMessage(problemMessage(t, getProblem(e)));
    }
  };

  return (
    <Stack spacing={2}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ sm: 'center' }}
      >
        <Stack spacing={1} sx={{ minWidth: 0 }}>
          <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
            {organization.displayName}
          </Typography>
          <Stack direction="row" spacing={1}>
            <Chip size="small" label={t(`orgType.${organization.type}`)} />
            <StatusChip status={organization.status} />
          </Stack>
        </Stack>
        {canManage &&
          (organization.status === 'ACTIVE' ? (
            <Button variant="outlined" color="error" onClick={() => setSuspending(true)}>
              {t('organizations.suspend')}
            </Button>
          ) : (
            <Button variant="outlined" onClick={() => setConfirmReactivate(true)}>
              {t('organizations.reactivate')}
            </Button>
          ))}
      </Stack>
      {message && (
        <Alert severity="error" role="alert" onClose={() => setMessage(null)}>
          {message}
        </Alert>
      )}
      {suspending && (
        <SuspendDialog
          organization={organization}
          onClose={() => setSuspending(false)}
          onSuspended={(next) => {
            setSuspending(false);
            updated(next, t('organizations.suspended'));
          }}
        />
      )}
      <Dialog
        open={confirmReactivate}
        onClose={() => setConfirmReactivate(false)}
        aria-labelledby="reactivate-title"
      >
        <DialogTitle id="reactivate-title">{t('organizations.reactivateDialog.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('organizations.reactivateDialog.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmReactivate(false)}>
            {t('organizations.reactivateDialog.cancel')}
          </Button>
          <Button variant="contained" onClick={() => void reactivate()}>
            {t('organizations.reactivateDialog.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={done !== null}
        autoHideDuration={4000}
        onClose={() => setDone(null)}
        message={done}
      />
    </Stack>
  );
}

/**
 * Aviso quando nenhum utilizador `ACTIVE` tem um dos `managerRoles` (os convites pendentes não
 * contam: o gestor só existe depois de activar a conta).
 */
function ManagerWarning({ organizationId }: { organizationId: string }) {
  const { t } = useTranslation();
  const scope: UsersScope = { kind: 'organization', organizationId };
  const roles = useQuery({
    queryKey: assignableRolesQueryKey(scope),
    queryFn: ({ signal }) => listAssignableRoles({ organizationId }, undefined, signal),
    staleTime: 5 * 60_000,
  });
  const active = useQuery({
    queryKey: [...usersQueryKey(scope), 'active'],
    queryFn: ({ signal }) =>
      listOrganizationUsers(organizationId, { status: 'ACTIVE', size: 100 }, undefined, signal),
  });
  if (!roles.data || !active.data) return null;
  const managerRoles = roles.data.managerRoles;
  const hasManager = active.data.content.some(
    (user) => user.status === 'ACTIVE' && user.roles.some((role) => managerRoles.includes(role)),
  );
  if (hasManager) return null;
  return (
    <Alert severity="warning" data-testid="no-manager">
      {t('organizations.noManager')}
    </Alert>
  );
}

function DataTab({
  organization,
  onReload,
}: {
  organization: OrganizationResponse;
  onReload(): void;
}) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.organizationManage);
  const [saved, setSaved] = useState(false);
  const initial = useMemo(() => valuesFrom(organization), [organization]);

  return (
    <>
      <OrganizationForm
        mode="edit"
        initial={initial}
        readOnly={!canManage}
        submitLabel={t('organizations.save')}
        onSubmit={async (values) => {
          try {
            const next = await updateOrganization(
              organization.id,
              toUpdateRequest(values, organization.version),
            );
            queryClient.setQueryData(['organization', next.id], next);
            void queryClient.invalidateQueries({ queryKey: ['organizations'] });
            setSaved(true);
          } catch (error) {
            if (getProblem(error).code === 'CONCURRENT_MODIFICATION') onReload();
            throw error;
          }
        }}
      />
      <Snackbar
        open={saved}
        autoHideDuration={4000}
        onClose={() => setSaved(false)}
        message={t('organizations.saved')}
      />
    </>
  );
}
