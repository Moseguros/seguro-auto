import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import Skeleton from '@mui/material/Skeleton';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { enUS, ptPT } from '@mui/x-data-grid/locales';
import { useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation } from 'react-router-dom';
import { getProblem, problemMessage } from '../../errors/problem';

/** Chaves das consultas das propostas da seguradora (invalidadas depois de cada decisão). */
export const insurerProposalsKey = ['insurer-proposals'] as const;
export const insurerProposalKey = (id: string) => ['insurer-proposal', id] as const;
export const insurerKycKey = ['insurer-kyc'] as const;
export const insurerInspectionsKey = ['insurer-inspections'] as const;

/** Estado de navegação partilhado pelas filas e pelos detalhes. */
export interface QueueNavigationState {
  /** Filtros da fila (`location.search`), para voltar a ela como estava. */
  queueSearch?: string;
  /** Aviso a mostrar na fila depois de uma decisão. */
  notice?: string;
  /** Vistoria aberta a partir da fila das vistorias. */
  inspectionId?: string;
  /** Para onde voltar depois de rever o KYC (o detalhe da proposta). */
  returnTo?: string;
}

/** Comprimento de um texto obrigatório (sem os espaços das pontas) dentro dos limites? */
export function lengthOk(text: string, min: number, max: number): boolean {
  const length = text.trim().length;
  return length >= min && length <= max;
}

/** Textos da `DataGrid` na língua actual. */
export function useGridLocaleText() {
  const { i18n } = useTranslation();
  return (i18n.language === 'en' ? enUS : ptPT).components.MuiDataGrid.defaultProps.localeText;
}

/** Cinco linhas a carregar. */
export function QueueSkeleton() {
  return (
    <Stack spacing={1} data-testid="queue-loading">
      {Array.from({ length: 5 }, (_, i) => (
        <Skeleton key={i} variant="rectangular" height={44} sx={{ borderRadius: 1 }} />
      ))}
    </Stack>
  );
}

/** Erro de uma fila com "Tentar de novo". */
export function QueueError({ error, onRetry }: { error: unknown; onRetry(): void }) {
  const { t } = useTranslation();
  return (
    <Alert
      severity="error"
      role="alert"
      action={
        <Button color="inherit" size="small" onClick={onRetry}>
          {t('common.retry')}
        </Button>
      }
    >
      {problemMessage(t, getProblem(error))}
    </Alert>
  );
}

/** Fila vazia. */
export function QueueEmpty({ children }: { children: ReactNode }) {
  return (
    <Paper variant="outlined" sx={{ p: 4, textAlign: 'center' }}>
      <Typography>{children}</Typography>
    </Paper>
  );
}

/** Aviso que chega pela navegação (`state.notice`), ex.: "Proposta aceite". */
export function NavigationNotice() {
  const location = useLocation();
  const notice = (location.state as QueueNavigationState | null)?.notice;
  return <Notice key={location.key} notice={notice} />;
}

function Notice({ notice }: { notice?: string }) {
  const [open, setOpen] = useState(!!notice);
  return (
    <Snackbar
      open={open && !!notice}
      autoHideDuration={6000}
      onClose={() => setOpen(false)}
      message={notice}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
    />
  );
}

/** Rótulo e valor numa lista de dados (`<dl>`). */
export function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <Stack spacing={0.25} sx={{ minWidth: 0 }}>
      <Typography component="dt" variant="caption" color="text.secondary">
        {label}
      </Typography>
      <Typography component="dd" variant="body2" sx={{ m: 0, wordBreak: 'break-word' }}>
        {children === '' || children === null || children === undefined ? '—' : children}
      </Typography>
    </Stack>
  );
}

/** Contador `n/máx` para os textos longos. */
export function counterText(text: string, max: number): string {
  return `${text.trim().length}/${max}`;
}
