import Box from '@mui/material/Box';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

/**
 * Logótipo horizontal (símbolo e nome "MoSeguros"), com ligação para o início.
 * A barra superior é escura nos dois modos (no escuro o MUI usa a cor da superfície),
 * por isso usa-se sempre a variante para fundos escuros.
 * Os ficheiros em `public/brand/` são provisórios (ver `docs/brand/README.md`).
 */
export function Brand() {
  const { t } = useTranslation();
  return (
    <Box
      component={RouterLink}
      to="/"
      sx={{ display: 'flex', alignItems: 'center', color: 'inherit', minWidth: 0 }}
    >
      <Box
        component="img"
        src="/brand/logo-white.svg"
        alt={t('app.name')}
        sx={{ height: 36, width: 'auto' }}
      />
    </Box>
  );
}
