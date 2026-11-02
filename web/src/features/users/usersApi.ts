import {
  invitePlatformUser,
  inviteOrganizationUser,
  listAssignableRoles,
  listOrganizationInvitations,
  listOrganizationUsers,
  listPlatformInvitations,
  listPlatformUsers,
  type InviteUserRequest,
  type StaffUserResponse,
} from '../../api/generated';

/** Utilizadores da plataforma ou de uma organização. */
export type UsersScope = { kind: 'platform' } | { kind: 'organization'; organizationId: string };

export function scopeKey(scope: UsersScope): string {
  return scope.kind === 'platform' ? 'platform' : scope.organizationId;
}

export const usersQueryKey = (scope: UsersScope) => ['users', scopeKey(scope)] as const;
export const invitationsQueryKey = (scope: UsersScope) => ['invitations', scopeKey(scope)] as const;
export const assignableRolesQueryKey = (scope: UsersScope) =>
  ['assignableRoles', scopeKey(scope)] as const;

const sort = 'createdAt,desc';

export function fetchUsers(scope: UsersScope, page: number, size: number, signal?: AbortSignal) {
  const params = { page, size, sort };
  return scope.kind === 'platform'
    ? listPlatformUsers(params, undefined, signal)
    : listOrganizationUsers(scope.organizationId, params, undefined, signal);
}

export function fetchInvitations(
  scope: UsersScope,
  page: number,
  size: number,
  signal?: AbortSignal,
) {
  const params = { page, size, sort };
  return scope.kind === 'platform'
    ? listPlatformInvitations(params, undefined, signal)
    : listOrganizationInvitations(scope.organizationId, params, undefined, signal);
}

export function fetchAssignableRoles(scope: UsersScope, signal?: AbortSignal) {
  return listAssignableRoles(
    scope.kind === 'platform' ? { scope: 'PLATFORM' } : { organizationId: scope.organizationId },
    undefined,
    signal,
  );
}

export function inviteUser(scope: UsersScope, request: InviteUserRequest) {
  return scope.kind === 'platform'
    ? invitePlatformUser(request)
    : inviteOrganizationUser(scope.organizationId, request);
}

export type UserAction = 'roles' | 'disable' | 'enable' | 'unlock' | 'resetAccess';

/** Acções possíveis numa linha, conforme o estado (as mesmas transições do backend). */
export function availableActions(
  user: Pick<StaffUserResponse, 'id' | 'status'>,
  currentUserId: string | undefined,
): UserAction[] {
  if (user.id === currentUserId) return [];
  switch (user.status) {
    case 'ACTIVE':
      return ['roles', 'disable', 'resetAccess'];
    case 'LOCKED':
      return ['roles', 'unlock', 'disable', 'resetAccess'];
    case 'DISABLED':
      return ['enable'];
    default:
      return [];
  }
}

export type InvitationAction = 'resend' | 'revoke';

export function invitationActions(status: string): InvitationAction[] {
  return status === 'PENDING' || status === 'EXPIRED' ? ['resend', 'revoke'] : [];
}

export interface InviteValues {
  fullName: string;
  email: string;
  roleCodes: string[];
}

/** Convite que falhou noutro ecrã (criação da organização): reabre-se preenchido, com o erro. */
export interface PendingInvite {
  values: InviteValues;
  errorCode: string;
}
