import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  getOtpOptions,
  startContactVerification,
  verifyContact,
  type ContactsResponse,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { CodeStep } from '../customer-auth/CodeStep';
import { otpOptionsQueryKey } from '../customer-auth/CustomerLoginPage';
import { IdentifierStep } from '../customer-auth/IdentifierStep';
import type {
  IdentifierType,
  OtpRequest,
  RequestCode,
  SentCode,
  VerifyCode,
} from '../customer-auth/otp';

export interface ContactDialogProps {
  type: IdentifierType;
  /** Já existe um contacto deste tipo (o título diz "Alterar"). */
  replacing: boolean;
  onClose(): void;
  onVerified(contacts: ContactsResponse): void;
}

/**
 * Acrescenta ou troca um contacto: os mesmos passos do login, mas com
 * `startContactVerification` (também no reenvio) e `verifyContact`.
 */
export function ContactDialog({ type, replacing, onClose, onVerified }: ContactDialogProps) {
  const { t } = useTranslation();
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const [sent, setSent] = useState<SentCode | null>(null);
  const [lastRequest, setLastRequest] = useState<OtpRequest | undefined>();
  const options = useQuery({
    queryKey: otpOptionsQueryKey,
    queryFn: ({ signal }) => getOtpOptions(undefined, signal),
  });

  const request: RequestCode = ({ identifierType, identifier, channel }) =>
    startContactVerification({ type: identifierType, value: identifier, channel });
  const verify: VerifyCode = async (challengeId, code) => {
    onVerified(await verifyContact({ challengeId, code }));
  };

  let content;
  if (options.isPending) content = <LoadingScreen />;
  else if (options.isError) {
    content = <Alert severity="error">{problemMessage(t, getProblem(options.error))}</Alert>;
  } else if (sent) {
    content = (
      <CodeStep
        sent={sent}
        codeLength={options.data.codeLength}
        onRequest={request}
        onVerify={verify}
        onChangeIdentifier={() => setSent(null)}
      />
    );
  } else {
    content = (
      <IdentifierStep
        options={options.data}
        types={[type]}
        initial={lastRequest}
        onRequest={request}
        onSent={(value) => {
          setLastRequest(value.request);
          setSent(value);
        }}
      />
    );
  }

  const titleKey = `contacts.dialog.${replacing ? 'change' : 'add'}.${type}`;
  return (
    <Dialog
      open
      onClose={onClose}
      fullScreen={fullScreen}
      fullWidth
      maxWidth="xs"
      aria-labelledby="contact-dialog-title"
    >
      <DialogTitle id="contact-dialog-title">{t(titleKey)}</DialogTitle>
      <DialogContent sx={{ pt: '8px !important' }}>{content}</DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('contacts.cancel')}</Button>
      </DialogActions>
    </Dialog>
  );
}
