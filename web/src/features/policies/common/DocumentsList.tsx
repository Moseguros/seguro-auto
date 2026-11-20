import Description from '@mui/icons-material/Description';
import Download from '@mui/icons-material/Download';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
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
import { downloadFile } from '../../../files/downloadFile';

/** Nome por defeito do ficheiro, se o `Content-Disposition` não trouxer nenhum. */
export function documentFileName(document: PolicyDocument): string {
  return document.fileName ?? `${document.type.toLowerCase()}.pdf`;
}

/**
 * Documentos da apólice (`documentType.<TYPE>`): "Descarregar" nos prontos, "A preparar…" nos
 * `PENDING`. O download vai por `GET /api/v1/files/{fileId}` com o `Authorization` da sessão.
 */
export function DocumentsList({ documents }: { documents: PolicyDocument[] }) {
  const { t } = useTranslation();
  const [busy, setBusy] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function download(document: PolicyDocument) {
    if (!document.fileId) return;
    setBusy(document.fileId);
    setError(null);
    try {
      await downloadFile(document.fileId, documentFileName(document));
    } catch (e) {
      setError(problemMessage(t, getProblem(e)));
    } finally {
      setBusy(null);
    }
  }

  if (documents.length === 0) {
    return <Typography color="text.secondary">{t('policyCommon.documents.empty')}</Typography>;
  }

  return (
    <Stack spacing={1}>
      {error && (
        <Alert severity="error" role="alert" onClose={() => setError(null)}>
          {error}
        </Alert>
      )}
      <List disablePadding>
        {documents.map((document) => {
          const name = t(`documentType.${document.type}`);
          const ready = document.status === 'READY' && !!document.fileId;
          return (
            <ListItem
              key={document.type}
              disableGutters
              data-testid={`document-${document.type}`}
              sx={{ flexWrap: 'wrap', columnGap: 1 }}
            >
              <ListItemIcon sx={{ minWidth: 36 }}>
                <Description color="action" />
              </ListItemIcon>
              <ListItemText primary={name} sx={{ minWidth: 0, flex: '1 1 140px' }} />
              {ready ? (
                <Button
                  size="small"
                  startIcon={
                    busy === document.fileId ? <CircularProgress size={16} /> : <Download />
                  }
                  disabled={busy === document.fileId}
                  aria-label={`${t('files.download')}: ${name}`}
                  onClick={() => void download(document)}
                >
                  {t('files.download')}
                </Button>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  {t('policyCommon.documents.preparing')}
                </Typography>
              )}
            </ListItem>
          );
        })}
      </List>
    </Stack>
  );
}
