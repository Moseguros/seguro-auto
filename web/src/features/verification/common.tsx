import Box from '@mui/material/Box';
import type { ReactNode } from 'react';
import { hasPermission, Permissions } from '../../auth/permissions';
import { useAuth } from '../../auth/useAuth';

/** Coluna estreita das páginas da verificação, pensada para o telemóvel (360 px). */
export function VerifyColumn({ children }: { children: ReactNode }) {
  return <Box sx={{ maxWidth: 560, mx: 'auto', width: '100%', minWidth: 0 }}>{children}</Box>;
}

/** Sessão de staff com `verification:police` (a polícia usa os endpoints próprios)? */
export function usePoliceSession(): { loading: boolean; police: boolean } {
  const { status, session, me } = useAuth();
  if (status === 'loading') return { loading: true, police: false };
  return {
    loading: false,
    police: session?.type === 'STAFF' && hasPermission(me, Permissions.verificationPolice),
  };
}
