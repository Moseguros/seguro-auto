import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import type { Area } from '../layout/menus';
import { areaFor } from './areas';
import { loginPathFor } from './AuthProvider';
import { getLastEndReason, type SessionType } from './session';
import { useAuth } from './useAuth';

export const noAccessPath = '/sem-acesso';

export function LoadingScreen() {
  const { t } = useTranslation();
  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
      <CircularProgress aria-label={t('auth.loading')} />
    </Box>
  );
}

/**
 * Guarda de rota: sem sessão vai para a entrada (com `voltar`); sessão de outro tipo ou de
 * outra área vai para `/sem-acesso`.
 */
export function RequireAuth({
  type,
  area,
  children,
}: {
  type: SessionType;
  area?: Area;
  children?: ReactNode;
}) {
  const { status, session, me } = useAuth();
  const location = useLocation();

  if (status === 'loading') return <LoadingScreen />;
  if (!session) {
    const login = loginPathFor(type);
    // Depois de "Terminar sessão" não faz sentido voltar à página onde se estava.
    if (getLastEndReason() === 'logout') return <Navigate to={login} replace />;
    const back = encodeURIComponent(`${location.pathname}${location.search}`);
    return <Navigate to={`${login}?voltar=${back}`} replace />;
  }
  if (session.type !== type || (area && (!me || areaFor(me).area !== area))) {
    return <Navigate to={noAccessPath} replace />;
  }
  return children ?? <Outlet />;
}
