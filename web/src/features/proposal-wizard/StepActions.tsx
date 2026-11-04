import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';

/** Barra fixa no fundo com "Voltar" (texto) e a acção principal do passo. */
export function StepActions({ onBack, children }: { onBack?: () => void; children?: ReactNode }) {
  const { t } = useTranslation();
  return (
    <Box
      data-testid="step-actions"
      sx={{
        position: 'sticky',
        bottom: 0,
        zIndex: 2,
        mx: { xs: -2, sm: 0 },
        px: { xs: 2, sm: 0 },
        py: 1.5,
        bgcolor: 'background.default',
        borderTop: 1,
        borderColor: 'divider',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: 1,
        flexWrap: 'wrap',
      }}
    >
      {onBack ? (
        <Button variant="text" onClick={onBack}>
          {t('proposal.wizard.back')}
        </Button>
      ) : (
        <span />
      )}
      <Stack direction="row" spacing={1} sx={{ ml: 'auto' }}>
        {children}
      </Stack>
    </Box>
  );
}
