import AccountCircle from '@mui/icons-material/AccountCircle';
import Logout from '@mui/icons-material/Logout';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import Menu from '@mui/material/Menu';
import MenuItemComponent from '@mui/material/MenuItem';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../auth/useAuth';

/** Menu do utilizador na barra superior: nome e "Terminar sessão" (só com sessão). */
export function UserMenu() {
  const { t } = useTranslation();
  const { session, me, logout } = useAuth();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  if (!session) return null;
  const name = me?.displayName || me?.msisdn || me?.email || t('auth.userMenu.account');
  return (
    <>
      <IconButton
        color="inherit"
        edge="end"
        aria-label={t('auth.userMenu.open')}
        aria-haspopup="menu"
        aria-controls={anchor ? 'user-menu' : undefined}
        aria-expanded={anchor ? true : undefined}
        onClick={(event) => setAnchor(event.currentTarget)}
      >
        <AccountCircle />
      </IconButton>
      <Menu
        id="user-menu"
        anchorEl={anchor}
        open={anchor !== null}
        onClose={() => setAnchor(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <Box sx={{ px: 2, py: 1, maxWidth: 260 }}>
          <Typography variant="subtitle2" noWrap data-testid="user-menu-name">
            {name}
          </Typography>
        </Box>
        <MenuItemComponent
          onClick={() => {
            setAnchor(null);
            void logout();
          }}
        >
          <ListItemIcon>
            <Logout fontSize="small" />
          </ListItemIcon>
          {t('auth.userMenu.logout')}
        </MenuItemComponent>
      </Menu>
    </>
  );
}
