import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation } from 'react-router-dom';

/** Separadores "Veículos" e "Condutores" no topo das duas listas. */
export function VehiclesTabs() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const value = pathname.startsWith('/cliente/condutores') ? 'drivers' : 'vehicles';
  return (
    <Tabs value={value} aria-label={t('vehicles.tabs.label')}>
      <Tab
        value="vehicles"
        label={t('vehicles.tabs.vehicles')}
        component={RouterLink}
        to="/cliente/veiculos"
      />
      <Tab
        value="drivers"
        label={t('vehicles.tabs.drivers')}
        component={RouterLink}
        to="/cliente/condutores"
      />
    </Tabs>
  );
}
