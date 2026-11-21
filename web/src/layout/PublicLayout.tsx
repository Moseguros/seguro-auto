import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import { Outlet } from 'react-router-dom';
import { Brand } from '../components/Brand';
import { LanguageSelector } from '../components/LanguageSelector';
import { UserMenu } from './UserMenu';

/**
 * Layout sem menu lateral: página inicial, verificação de documentos e 404. Com sessão (de
 * polícia ou outra) mostra o menu do utilizador ao lado da língua (S6-05).
 */
export function PublicLayout() {
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <AppBar position="static" elevation={0}>
        <Toolbar sx={{ gap: 1 }}>
          <Brand />
          <Box sx={{ flexGrow: 1 }} />
          <LanguageSelector />
          <UserMenu />
        </Toolbar>
      </AppBar>
      <Box component="main" sx={{ flexGrow: 1, p: { xs: 2, sm: 3 }, minWidth: 0 }}>
        <Outlet />
      </Box>
    </Box>
  );
}
