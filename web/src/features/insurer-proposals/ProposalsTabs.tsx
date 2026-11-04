import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { Permissions, hasPermission, type Permission } from '../../auth/permissions';
import { useAuth } from '../../auth/useAuth';

export const proposalsPath = '/seguradora/propostas';
export const kycPath = '/seguradora/propostas/kyc';
export const inspectionsPath = '/seguradora/propostas/vistorias';

export const proposalTabs: { key: string; path: string; permission: Permission }[] = [
  { key: 'proposals', path: proposalsPath, permission: Permissions.proposalRead },
  { key: 'kyc', path: kycPath, permission: Permissions.kycReview },
  { key: 'inspections', path: inspectionsPath, permission: Permissions.inspectionReview },
];

/** Separadores a que o utilizador tem acesso (pela ordem). */
export function useAllowedProposalTabs() {
  const { me } = useAuth();
  return proposalTabs.filter((tab) => hasPermission(me, tab.permission));
}

/** Separadores das filas "Propostas", "Identidade" e "Vistorias" (só os que pode ver). */
export function ProposalsTabs() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const tabs = useAllowedProposalTabs();
  const current = tabs.find((tab) => tab.path === pathname)?.path ?? false;

  return (
    <Tabs
      value={current}
      variant={narrow ? 'scrollable' : 'standard'}
      scrollButtons={narrow ? 'auto' : false}
      allowScrollButtonsMobile
      aria-label={t('insurerProposals.title')}
      sx={{ borderBottom: 1, borderColor: 'divider', maxWidth: '100%' }}
    >
      {tabs.map((tab) => (
        <Tab
          key={tab.key}
          value={tab.path}
          label={t(`insurerProposals.tabs.${tab.key}`)}
          component={RouterLink}
          to={tab.path}
        />
      ))}
    </Tabs>
  );
}
