import type { ReactNode } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import type { MeResponse } from '../api/generated';
import { noAccessPath } from './RequireAuth';
import { useAuth } from './useAuth';

/** Permissões usadas pela web. O backend decide sempre; a web só esconde o que não se pode usar. */
export const Permissions = {
  otpSettingsRead: 'otp-settings:read',
  otpSettingsManage: 'otp-settings:manage',
  organizationRead: 'organization:read',
  organizationManage: 'organization:manage',
  orgUserRead: 'org-user:read',
  orgUserManage: 'org-user:manage',
  productRead: 'product:read',
  productManage: 'product:manage',
  ratingManage: 'rating:manage',
  referenceManage: 'reference:manage',
  quoteCreateForClient: 'quote:create-for-client',
} as const;

export type Permission = (typeof Permissions)[keyof typeof Permissions];

export function hasPermission(
  me: Pick<MeResponse, 'permissions'> | undefined,
  permission: string,
): boolean {
  return !!me?.permissions.includes(permission);
}

/** O utilizador autenticado tem a permissão? */
export function usePermission(permission: Permission): boolean {
  const { me } = useAuth();
  return hasPermission(me, permission);
}

/** Guarda de página: sem a permissão vai para `/sem-acesso` (usar dentro de `RequireAuth`). */
export function RequirePermission({
  permission,
  children,
}: {
  permission: Permission;
  children?: ReactNode;
}) {
  const allowed = usePermission(permission);
  if (!allowed) return <Navigate to={noAccessPath} replace />;
  return children ?? <Outlet />;
}
