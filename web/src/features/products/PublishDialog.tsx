import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  publishProductVersion,
  type ProductVersionResponse,
  type RuleError,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';

export interface PublishDialogProps {
  version: ProductVersionResponse;
  onClose(): void;
  onPublished(version: ProductVersionResponse): void;
  /** `PRODUCT_VERSION_NOT_PUBLISHABLE`: os erros da resposta. */
  onNotPublishable(errors: RuleError[]): void;
}

/** Confirmação da publicação de uma versão. */
export function PublishDialog({
  version,
  onClose,
  onPublished,
  onNotPublishable,
}: PublishDialogProps) {
  const { t } = useTranslation();
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const publish = async () => {
    setSaving(true);
    setMessage(null);
    try {
      onPublished(await publishProductVersion(version.id, { version: version.version }));
    } catch (error) {
      const problem = getProblem(error);
      setSaving(false);
      if (problem.code === 'PRODUCT_VERSION_NOT_PUBLISHABLE') {
        onNotPublishable(problem.errors);
        return;
      }
      setMessage(problemMessage(t, problem));
    }
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="publish-title">
      <DialogTitle id="publish-title">
        {t('products.publish.title', { n: version.versionNo })}
      </DialogTitle>
      <DialogContent>
        <DialogContentText>
          {version.versionNo > 1
            ? t('products.publish.textWithPrevious', { previous: version.versionNo - 1 })
            : t('products.publish.text')}
        </DialogContentText>
        {message && (
          <Alert severity="error" role="alert" sx={{ mt: 2 }}>
            {message}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('common.cancel')}</Button>
        <Button variant="contained" onClick={() => void publish()} disabled={saving}>
          {t('products.publish.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
