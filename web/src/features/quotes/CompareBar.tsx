import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { QUOTE_MAX_COMPARE } from './config';

/** Pode-se comparar com 2 ou 3 propostas marcadas. */
export function canCompare(count: number): boolean {
  return count >= 2 && count <= QUOTE_MAX_COMPARE;
}

/**
 * Nova selecção depois de marcar ou desmarcar `id`. Uma quarta é recusada (`refused`) e a
 * selecção fica como estava.
 */
export function toggleSelection(
  selected: readonly string[],
  id: string,
  checked: boolean,
): { selected: string[]; refused: boolean } {
  if (!checked) return { selected: selected.filter((s) => s !== id), refused: false };
  if (selected.includes(id)) return { selected: [...selected], refused: false };
  if (selected.length >= QUOTE_MAX_COMPARE) return { selected: [...selected], refused: true };
  return { selected: [...selected, id], refused: false };
}

/** Barra fixa no fundo com "{n} seleccionadas (máximo 3)" e "Comparar propostas". */
export function CompareBar({ count, onCompare }: { count: number; onCompare(): void }) {
  const { t } = useTranslation();
  if (count === 0) return null;
  return (
    <Paper
      elevation={8}
      square
      role="region"
      aria-label={t('quoteResults.compareBar')}
      sx={{
        position: 'fixed',
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: (theme) => theme.zIndex.appBar,
        px: 2,
        py: 1.5,
      }}
    >
      <Stack
        direction="row"
        spacing={2}
        alignItems="center"
        justifyContent="space-between"
        sx={{ maxWidth: 960, mx: 'auto' }}
      >
        <Typography variant="body2">
          {t('quoteResults.selected', { count, max: QUOTE_MAX_COMPARE })}
        </Typography>
        <Button variant="cta" disabled={!canCompare(count)} onClick={onCompare}>
          {t('quoteResults.compare')}
        </Button>
      </Stack>
    </Paper>
  );
}
