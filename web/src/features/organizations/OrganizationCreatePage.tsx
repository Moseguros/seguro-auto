import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  createOrganization,
  inviteOrganizationUser,
  listAssignableRoles,
} from '../../api/generated';
import { getProblem } from '../../errors/problem';
import type { PendingInvite } from '../users/usersApi';
import { OrganizationForm } from './OrganizationForm';
import { toCreateRequest, type OrganizationFormValues } from './organizationSchema';

/** Estado passado ao detalhe depois de criar. */
export interface OrganizationDetailState {
  tab?: 'data' | 'users';
  /** O convite do primeiro gestor falhou: reabre-se preenchido, com o erro. */
  pendingInvite?: PendingInvite;
}

/**
 * `/admin/organizacoes/nova`: cria a organização e convida o primeiro gestor com os perfis
 * `managerInviteRoles`. Se o convite falhar, a organização fica criada e o detalhe abre no
 * separador "Utilizadores" com o diálogo de convite preenchido.
 */
export function OrganizationCreatePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const submit = async (values: OrganizationFormValues) => {
    // Um erro aqui fica no formulário (a organização não foi criada).
    const organization = await createOrganization(toCreateRequest(values));
    queryClient.setQueryData(['organization', organization.id], organization);
    void queryClient.invalidateQueries({ queryKey: ['organizations'] });

    const invite = {
      fullName: values.managerName.trim().replace(/\s+/g, ' '),
      email: values.managerEmail.trim(),
      roleCodes: [] as string[],
    };
    let state: OrganizationDetailState = { tab: 'users' };
    try {
      const roles = await listAssignableRoles({ organizationId: organization.id });
      invite.roleCodes = roles.managerInviteRoles;
      await inviteOrganizationUser(organization.id, invite);
    } catch (error) {
      state = {
        tab: 'users',
        pendingInvite: { values: invite, errorCode: getProblem(error).code },
      };
    }
    navigate(`/admin/organizacoes/${organization.id}`, { state });
  };

  return (
    <Stack spacing={3} sx={{ maxWidth: 960 }}>
      <Typography variant="h4" component="h1">
        {t('organizations.new')}
      </Typography>
      <OrganizationForm mode="create" submitLabel={t('organizations.create')} onSubmit={submit} />
    </Stack>
  );
}
