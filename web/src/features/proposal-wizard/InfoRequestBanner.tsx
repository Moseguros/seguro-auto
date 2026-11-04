import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { ProposalInfoRequest } from '../../api/generated';
import { formatDateTime } from '../../i18n/format';
import { infoRequestItemStep, type WizardStep } from './wizardSteps';

/** Pedido de informação da seguradora (`INFO_REQUESTED`), com uma ligação por item. */
export function InfoRequestBanner({
  insurerName,
  request,
  onOpen,
}: {
  insurerName: string;
  request: NonNullable<ProposalInfoRequest>;
  onOpen(step: WizardStep): void;
}) {
  const { t, i18n } = useTranslation();
  return (
    <Alert severity="info" data-testid="info-request">
      <AlertTitle>{t('proposal.infoRequest.title', { insurer: insurerName })}</AlertTitle>
      <Stack spacing={1}>
        <Typography variant="body2" sx={{ whiteSpace: 'pre-line', wordBreak: 'break-word' }}>
          {request.message}
        </Typography>
        <Typography variant="caption" color="text.secondary">
          {t('proposal.infoRequest.requestedAt', {
            date: formatDateTime(request.requestedAt, i18n.language),
          })}
        </Typography>
        <Stack component="ul" spacing={0.5} sx={{ m: 0, pl: 2.5 }}>
          {request.items.map((item) => (
            <li key={item}>
              <Link
                component="button"
                type="button"
                onClick={() => onOpen(infoRequestItemStep[item])}
              >
                {t(`infoRequestItem.${item}`)}
              </Link>
            </li>
          ))}
        </Stack>
      </Stack>
    </Alert>
  );
}
