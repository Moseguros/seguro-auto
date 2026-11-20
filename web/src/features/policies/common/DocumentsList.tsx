import Description from '@mui/icons-material/Description';
import Download from '@mui/icons-material/Download';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { PolicyDocument } from '../../../api/generated';
import { getProblem, problemMessage } from '../../../errors/problem';
// Atenção: o cliente gerado também exporta um `downloadFile`; este é o de `files/`.
import { downloadFile } from '../../../files/downloadFile';

/**
 * Documentos da apólice (S6-05 §6), partilhado com o S6-06: um item por documento com
 * "Descarregar"; `PENDING` mostra "A preparar…" sem botão.
 */
export function DocumentsList({ documents }: { documents: PolicyDocument[] }) {
  const { t } = useTranslation();
  const [busy, setBusy] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  if (documents.length === 0) {
    return <Typography>{t('insurerPolicies.documents.empty')}</Typography>;
  }

  const download = async (document: PolicyDocument) => {
    if (!document.fileId) return;
    setError(null);
    setBusy(document.fileId);
    try {
      await downloadFile(
        document.fileId,
        document.fileName ?? `${t(`documentType.${document.type}`)}.pdf`,
      );
    } catch (e) {
      setError(problemMessage(t, getProblem(e)));
    } finally {
      setBusy(null);
    }
  };

  return (
    <Stack spacing={1}>
      {error && (
        <Alert severity="error" role="alert" onClose={() => setError(null)}>
          {error}
        </Alert>
      )}
      <List disablePadding aria-label={t('insurerPolicies.tabs.documents')}>
        {documents.map((document) => {
          const ready = document.status === 'READY' && !!document.fileId;
          const name = t(`documentType.${document.type}`);
          return (
            <ListItem
              key={document.type}
              divider
              data-testid={`document-${document.type}`}
              sx={{ flexWrap: 'wrap', gap: 1 }}
            >
              <ListItemIcon sx={{ minWidth: 40 }}>
                <Description />
              </ListItemIcon>
              <ListItemText
                primary={name}
                secondary={ready ? document.fileName : t('insurerPolicies.documents.preparing')}
                sx={{ minWidth: 0, flex: '1 1 160px' }}
                slotProps={{ secondary: { sx: { wordBreak: 'break-all' } } }}
              />
              {ready && (
                <Button
                  variant="outlined"
                  size="small"
                  startIcon={<Download />}
                  disabled={busy === document.fileId}
                  onClick={() => void download(document)}
                  aria-label={`${t('files.download')} ${name}`}
                >
                  {t('files.download')}
                </Button>
              )}
            </ListItem>
          );
        })}
      </List>
    </Stack>
  );
}
