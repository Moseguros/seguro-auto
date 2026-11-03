import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';
import { PlateFormatsTab } from './PlateFormatsTab';
import { PlatformSettingsTab } from './PlatformSettingsTab';
import { isReferenceTab, referenceTabs, tabDomains, type ReferenceTab } from './referenceDomains';
import { ReferenceValuesTab } from './ReferenceValuesTab';

/** `/admin/referencias`: tabelas de referência, formatos de matrícula e parâmetros. */
export function ReferenceTablesPage() {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const param = searchParams.get('separador');
  const tab: ReferenceTab = isReferenceTab(param) ? param : 'marcas';
  const domain = tabDomains[tab];

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Typography variant="h4" component="h1">
        {t('references.title')}
      </Typography>
      <Box sx={{ borderBottom: 1, borderColor: 'divider', minWidth: 0 }}>
        <Tabs
          value={tab}
          onChange={(_, value: ReferenceTab) => setSearchParams({ separador: value })}
          variant="scrollable"
          scrollButtons="auto"
          allowScrollButtonsMobile
          aria-label={t('references.title')}
        >
          {referenceTabs.map((value) => (
            <Tab key={value} value={value} label={t(`references.tabs.${value}`)} />
          ))}
        </Tabs>
      </Box>
      {domain && <ReferenceValuesTab key={domain} domain={domain} />}
      {tab === 'matriculas' && <PlateFormatsTab />}
      {tab === 'parametros' && <PlatformSettingsTab />}
    </Stack>
  );
}
