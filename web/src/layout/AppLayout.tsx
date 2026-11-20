import MenuIcon from '@mui/icons-material/Menu';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import ListSubheader from '@mui/material/ListSubheader';
import Toolbar from '@mui/material/Toolbar';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, Outlet, useLocation } from 'react-router-dom';
import { hasPermission } from '../auth/permissions';
import { useAuth } from '../auth/useAuth';
import { Brand } from '../components/Brand';
import { LanguageSelector } from '../components/LanguageSelector';
import { aliasedMenuKey, menus, type Area, type MenuItem } from './menus';
import { UserMenu } from './UserMenu';

const drawerWidth = 260;

export function isSelected(item: MenuItem, area: Area, pathname: string): boolean {
  const alias = aliasedMenuKey(pathname);
  if (alias) return item.key === alias;
  const areaRoot = `/${area}`;
  if (item.path === areaRoot) return pathname === areaRoot || pathname === `${areaRoot}/`;
  return pathname === item.path || pathname.startsWith(`${item.path}/`);
}

/** Layout das áreas autenticadas: barra superior e menu lateral com os itens da área. */
export function AppLayout({ area }: { area: Area }) {
  const { t } = useTranslation();
  const theme = useTheme();
  const desktop = useMediaQuery(theme.breakpoints.up('md'), { noSsr: true });
  const [mobileOpen, setMobileOpen] = useState(false);
  const { pathname } = useLocation();
  const { me } = useAuth();
  // Itens sem a permissão não aparecem (o backend continua a decidir).
  const items = menus[area].filter(
    (item) => !item.permission || hasPermission(me, item.permission),
  );

  const menu = (
    <List
      component="nav"
      aria-label={t(`areas.${area}`)}
      subheader={<ListSubheader component="div">{t(`areas.${area}`)}</ListSubheader>}
    >
      {items.map((item) => {
        const Icon = item.icon;
        return (
          <ListItemButton
            key={item.key}
            component={RouterLink}
            to={item.path}
            selected={isSelected(item, area, pathname)}
            onClick={() => setMobileOpen(false)}
          >
            <ListItemIcon>
              <Icon />
            </ListItemIcon>
            <ListItemText primary={t(`menu.${area}.${item.key}`)} />
          </ListItemButton>
        );
      })}
    </List>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <AppBar position="fixed" elevation={0} sx={{ zIndex: theme.zIndex.drawer + 1 }}>
        <Toolbar sx={{ gap: 1 }}>
          {!desktop && (
            <IconButton
              color="inherit"
              edge="start"
              aria-label={t('nav.openMenu')}
              onClick={() => setMobileOpen(true)}
            >
              <MenuIcon />
            </IconButton>
          )}
          <Brand />
          <Box sx={{ flexGrow: 1 }} />
          <LanguageSelector />
          <UserMenu />
        </Toolbar>
      </AppBar>
      {desktop ? (
        <Drawer
          variant="permanent"
          sx={{
            width: drawerWidth,
            flexShrink: 0,
            '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
          }}
        >
          <Toolbar />
          {menu}
        </Drawer>
      ) : (
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
          ModalProps={{ keepMounted: true }}
          sx={{ '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' } }}
        >
          <Toolbar />
          {menu}
        </Drawer>
      )}
      <Box component="main" sx={{ flexGrow: 1, minWidth: 0, p: { xs: 2, sm: 3 } }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}
