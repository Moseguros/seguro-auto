import PersonAdd from '@mui/icons-material/PersonAdd';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Paper from '@mui/material/Paper';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import {
  disableUser,
  enableUser,
  resendInvitation,
  resetUserAccess,
  revokeInvitation,
  unlockUser,
  type InvitationResponse,
  type StaffUserResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { LoadingScreen } from '../../auth/RequireAuth';
import { useAuth } from '../../auth/useAuth';
import { StatusChip } from '../../components/StatusChip';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatDateTime } from '../../i18n/format';
import { InviteDialog } from './InviteDialog';
import { RolesDialog } from './RolesDialog';
import { UserActionsMenu } from './UserActionsMenu';
import {
  assignableRolesQueryKey,
  availableActions,
  fetchAssignableRoles,
  fetchInvitations,
  fetchUsers,
  invitationActions,
  invitationsQueryKey,
  inviteUser,
  usersQueryKey,
  type InvitationAction,
  type PendingInvite,
  type UserAction,
  type UsersScope,
} from './usersApi';

const pageSize = 20;

export interface UsersPanelProps {
  scope: UsersScope;
  /** Convite que falhou noutro ecrã: o diálogo abre preenchido, com o erro. */
  pendingInvite?: PendingInvite;
}

/** Utilizadores e convites de staff (plataforma ou organização), com as acções de gestão. */
export function UsersPanel({ scope, pendingInvite }: UsersPanelProps) {
  const { t, i18n } = useTranslation();
  const { me } = useAuth();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.orgUserManage);
  const [usersPage, setUsersPage] = useState(0);
  const [invitationsPage, setInvitationsPage] = useState(0);
  const [inviting, setInviting] = useState<PendingInvite | 'new' | null>(pendingInvite ?? null);
  const [editingRoles, setEditingRoles] = useState<StaffUserResponse | null>(null);
  const [confirmReset, setConfirmReset] = useState<StaffUserResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const users = useQuery({
    queryKey: [...usersQueryKey(scope), usersPage],
    queryFn: ({ signal }) => fetchUsers(scope, usersPage, pageSize, signal),
  });
  const invitations = useQuery({
    queryKey: [...invitationsQueryKey(scope), invitationsPage],
    queryFn: ({ signal }) => fetchInvitations(scope, invitationsPage, pageSize, signal),
  });
  const roles = useQuery({
    queryKey: assignableRolesQueryKey(scope),
    queryFn: ({ signal }) => fetchAssignableRoles(scope, signal),
    enabled: canManage,
    staleTime: 5 * 60_000,
  });

  const refresh = async () => {
    await Promise.all([
      queryClient.invalidateQueries({ queryKey: usersQueryKey(scope) }),
      queryClient.invalidateQueries({ queryKey: invitationsQueryKey(scope) }),
    ]);
  };

  const run = async (key: string, action: () => Promise<unknown>) => {
    setError(null);
    setBusy(true);
    try {
      await action();
      setDone(t(`users.done.${key}`));
      await refresh();
    } catch (e) {
      setError(problemMessage(t, getProblem(e)));
    } finally {
      setBusy(false);
    }
  };

  const onUserAction = (user: StaffUserResponse, action: UserAction) => {
    if (action === 'roles') setEditingRoles(user);
    else if (action === 'resetAccess') setConfirmReset(user);
    else {
      const call = { disable: disableUser, enable: enableUser, unlock: unlockUser }[action];
      void run(action, () => call(user.id));
    }
  };

  const onInvitationAction = (invitation: InvitationResponse, action: InvitationAction) => {
    const call = action === 'resend' ? resendInvitation : revokeInvitation;
    void run(action, () => call(invitation.id));
  };

  const roleChips = (codes: string[]) => (
    <Stack direction="row" spacing={0.5} useFlexGap flexWrap="wrap">
      {codes.map((code) => (
        <Chip key={code} size="small" label={t(`role.${code}`, { defaultValue: code })} />
      ))}
    </Stack>
  );

  const date = (value: string | undefined) => formatDateTime(value, i18n.language);

  return (
    <Stack spacing={3}>
      {canManage && (
        <Box>
          <Button
            variant="contained"
            startIcon={<PersonAdd />}
            onClick={() => setInviting('new')}
            disabled={!roles.data}
          >
            {t('users.invite')}
          </Button>
        </Box>
      )}
      {error && (
        <Alert severity="error" role="alert" onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Section
        title={t('users.title')}
        query={users}
        empty={t('users.noUsers')}
        testId="users-table"
        headers={['name', 'email', 'roles', 'status', 'lastLogin', 'actions']}
        page={usersPage}
        onPage={setUsersPage}
      >
        {users.data?.content.map((user) => {
          const self = user.id === me?.id;
          const name = user.displayName || user.email;
          return (
            <TableRow key={user.id} data-testid={`user-row-${user.email}`}>
              <TableCell>
                {user.displayName ?? '—'}{' '}
                {self && (
                  <Typography component="span" variant="body2" color="text.secondary">
                    {t('users.you')}
                  </Typography>
                )}
              </TableCell>
              <TableCell sx={{ wordBreak: 'break-all' }}>{user.email}</TableCell>
              <TableCell>{roleChips(user.roles)}</TableCell>
              <TableCell>
                <StatusChip status={user.status} />
              </TableCell>
              <TableCell>{user.lastLoginAt ? date(user.lastLoginAt) : t('users.never')}</TableCell>
              <TableCell align="right">
                {canManage && (
                  <UserActionsMenu
                    name={name}
                    actions={availableActions(user, me?.id)}
                    disabled={busy || (!roles.data && user.status !== 'DISABLED')}
                    onAction={(action) => onUserAction(user, action)}
                  />
                )}
              </TableCell>
            </TableRow>
          );
        })}
      </Section>

      <Section
        title={t('users.invitations')}
        query={invitations}
        empty={t('users.noInvitations')}
        testId="invitations-table"
        headers={['name', 'email', 'roles', 'status', 'expiresAt', 'actions']}
        page={invitationsPage}
        onPage={setInvitationsPage}
      >
        {invitations.data?.content.map((invitation) => (
          <TableRow key={invitation.id} data-testid={`invitation-row-${invitation.email}`}>
            <TableCell>{invitation.fullName}</TableCell>
            <TableCell sx={{ wordBreak: 'break-all' }}>{invitation.email}</TableCell>
            <TableCell>{roleChips(invitation.roleCodes)}</TableCell>
            <TableCell>
              <StatusChip status={invitation.status} />
            </TableCell>
            <TableCell>{date(invitation.expiresAt)}</TableCell>
            <TableCell align="right">
              {canManage && (
                <UserActionsMenu
                  name={invitation.fullName}
                  actions={invitationActions(invitation.status)}
                  disabled={busy}
                  onAction={(action) => onInvitationAction(invitation, action)}
                />
              )}
            </TableCell>
          </TableRow>
        ))}
      </Section>

      {inviting && roles.data && (
        <InviteDialog
          roles={roles.data.roles}
          initial={inviting === 'new' ? undefined : inviting.values}
          initialErrorCode={inviting === 'new' ? undefined : inviting.errorCode}
          onClose={() => setInviting(null)}
          onInvite={async (values) => {
            await inviteUser(scope, values);
            setInviting(null);
            setDone(t('users.done.invite'));
            await refresh();
          }}
        />
      )}
      {editingRoles && roles.data && (
        <RolesDialog
          user={editingRoles}
          roles={roles.data.roles}
          onClose={() => setEditingRoles(null)}
          onSaved={() => {
            setEditingRoles(null);
            setDone(t('users.done.roles'));
            void refresh();
          }}
        />
      )}
      {confirmReset && (
        <Dialog open onClose={() => setConfirmReset(null)} aria-labelledby="reset-title">
          <DialogTitle id="reset-title">{t('users.resetConfirm.title')}</DialogTitle>
          <DialogContent>
            <DialogContentText>{t('users.resetConfirm.text')}</DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setConfirmReset(null)}>{t('users.resetConfirm.cancel')}</Button>
            <Button
              variant="contained"
              onClick={() => {
                const user = confirmReset;
                setConfirmReset(null);
                void run('resetAccess', () => resetUserAccess(user.id));
              }}
            >
              {t('users.resetConfirm.confirm')}
            </Button>
          </DialogActions>
        </Dialog>
      )}
      <Snackbar
        open={done !== null}
        autoHideDuration={4000}
        onClose={() => setDone(null)}
        message={done}
      />
    </Stack>
  );
}

function Section({
  title,
  query,
  empty,
  testId,
  headers,
  page,
  onPage,
  children,
}: {
  title: string;
  query: {
    isPending: boolean;
    isError: boolean;
    error: unknown;
    data?: { content: unknown[]; totalElements: number };
  };
  empty: string;
  testId: string;
  headers: string[];
  page: number;
  onPage(page: number): void;
  children: ReactNode;
}) {
  const { t } = useTranslation();
  let body;
  if (query.isPending) body = <LoadingScreen />;
  else if (query.isError) {
    body = <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>;
  } else if (!query.data || query.data.content.length === 0) {
    body = <Typography color="text.secondary">{empty}</Typography>;
  } else {
    body = (
      <Paper variant="outlined">
        <TableContainer>
          <Table size="small" aria-label={title} data-testid={testId}>
            <TableHead>
              <TableRow>
                {headers.map((header) => (
                  <TableCell key={header} align={header === 'actions' ? 'right' : undefined}>
                    {header === 'actions' ? (
                      <Box component="span" sx={visuallyHidden}>
                        {t('users.columns.actions')}
                      </Box>
                    ) : (
                      t(`users.columns.${header}`)
                    )}
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>{children}</TableBody>
          </Table>
        </TableContainer>
        {query.data.totalElements > pageSize && (
          <TablePagination
            component="div"
            count={query.data.totalElements}
            page={page}
            rowsPerPage={pageSize}
            rowsPerPageOptions={[pageSize]}
            onPageChange={(_, next) => onPage(next)}
          />
        )}
      </Paper>
    );
  }
  return (
    <Stack spacing={1} component="section">
      <Typography variant="h6" component="h2">
        {title}
      </Typography>
      {body}
    </Stack>
  );
}

const visuallyHidden = {
  position: 'absolute',
  width: 1,
  height: 1,
  overflow: 'hidden',
  clip: 'rect(0 0 0 0)',
  whiteSpace: 'nowrap',
} as const;
