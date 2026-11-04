import Box from '@mui/material/Box';
import { useTranslation } from 'react-i18next';

/**
 * Faixa fixa no rodapé dos ambientes de demonstração (QA). Só aparece quando a web é
 * construída com `VITE_DEMO_BANNER=true`. Não captura cliques: o que fica por baixo
 * (barras de acções no fundo do ecrã) continua a funcionar.
 */
export function DemoBanner({ enabled = import.meta.env.VITE_DEMO_BANNER === 'true' }) {
  const { t } = useTranslation();
  if (!enabled) return null;
  return (
    <Box
      role="note"
      data-testid="demo-banner"
      sx={{
        position: 'fixed',
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: (theme) => theme.zIndex.snackbar + 1,
        pointerEvents: 'none',
        py: 0.25,
        textAlign: 'center',
        bgcolor: 'rgba(20, 45, 78, 0.92)',
        color: '#fff',
        fontSize: 12,
        fontWeight: 600,
        letterSpacing: 1,
      }}
    >
      {t('app.demoBanner')}
    </Box>
  );
}
