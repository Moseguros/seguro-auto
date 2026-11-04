import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import {
  acknowledgeProposalSuitability,
  type ProposalResponse,
  type SuitabilityReason,
} from '../../api/generated';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';

export interface SuitabilityWarningsProps {
  proposalId: string;
  version: number;
  /** Motivos pela ordem da resposta. */
  reasons: SuitabilityReason[];
  /** "Pedir outra cotação". */
  quotesPath: string;
  onAcknowledged(proposal: ProposalResponse): void;
  /** Erro que o pai trata (409 comuns); `true` se tratou. */
  onError?(problem: Problem): boolean;
}

/** Avisos de adequação (`WARNED`): um por motivo e a confirmação consciente. */
export function SuitabilityWarnings({
  proposalId,
  version,
  reasons,
  quotesPath,
  onAcknowledged,
  onError,
}: SuitabilityWarningsProps) {
  const { t } = useTranslation();
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  async function acknowledge() {
    setBusy(true);
    setMessage(null);
    try {
      onAcknowledged(await acknowledgeProposalSuitability(proposalId, { version }));
    } catch (error) {
      const problem = getProblem(error);
      if (onError?.(problem)) return;
      setMessage(problemMessage(t, problem));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Stack spacing={1.5} data-testid="suitability-warnings">
      <Typography variant="h6" component="h2">
        {t('proposal.suitability.warningsTitle')}
      </Typography>
      {reasons.map((reason) => (
        <Alert key={reason} severity="warning">
          {t(`suitability.reason.${reason}`)}
        </Alert>
      ))}
      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
        <Button variant="contained" disabled={busy} onClick={() => void acknowledge()}>
          {t('proposal.suitability.acknowledge')}
        </Button>
        <Button variant="outlined" component={RouterLink} to={quotesPath}>
          {t('proposal.suitability.otherQuote')}
        </Button>
      </Stack>
    </Stack>
  );
}
