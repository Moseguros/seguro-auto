import Add from '@mui/icons-material/Add';
import Delete from '@mui/icons-material/Delete';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import LinearProgress from '@mui/material/LinearProgress';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  deleteProductVersionDocument,
  uploadProductVersionDocument,
  type ProductDocumentResponse,
  type ProductVersionResponse,
  type UploadProductVersionDocumentBodyDocType,
  type UploadProductVersionDocumentBodyLanguage,
} from '../../../api/generated';
import { getProblem, problemMessage } from '../../../errors/problem';
import { FileOpenButton } from '../../../files/FileOpenButton';
import { FileUploadButton } from '../../../files/FileUploadButton';
import { fieldText } from '../../organizations/OrganizationForm';
import type { FieldErrors } from '../editorModel';

type DocType = UploadProductVersionDocumentBodyDocType;
type Language = UploadProductVersionDocumentBodyLanguage;

const pdf = 'application/pdf';
const mainTypes: DocType[] = ['GENERAL_CONDITIONS', 'PRE_CONTRACT_INFO'];
const otherTypes: DocType[] = ['SPECIAL_CONDITIONS', 'OTHER'];

export interface DocumentsStepProps {
  version: ProductVersionResponse;
  readOnly: boolean;
  errors: FieldErrors;
  /** Um documento foi enviado ou removido: a versão tem de ser lida de novo. */
  onChanged(): Promise<unknown>;
}

interface PendingDocument {
  key: number;
  docType: DocType;
  title: string;
}

/** Passo 4: condições gerais, informação pré-contratual e outros documentos (PDF). */
export function DocumentsStep({ version, readOnly, errors, onChanged }: DocumentsStepProps) {
  const { t } = useTranslation();
  const [progress, setProgress] = useState<Record<string, number>>({});
  const [message, setMessage] = useState<string | null>(null);
  const [removing, setRemoving] = useState<ProductDocumentResponse | null>(null);
  const [pending, setPending] = useState<PendingDocument[]>([]);
  const [nextKey, setNextKey] = useState(1);

  const find = (docType: DocType, language: Language) =>
    version.documents.find((d) => d.docType === docType && d.language === language);
  const others = version.documents.filter((d) => otherTypes.includes(d.docType as DocType));

  const upload = async (
    slot: string,
    file: File,
    body: { docType: DocType; language: Language; title?: string },
    replaced?: ProductDocumentResponse,
  ) => {
    setMessage(null);
    setProgress((p) => ({ ...p, [slot]: 0 }));
    try {
      await uploadProductVersionDocument(
        version.id,
        { file, ...body },
        {
          onUploadProgress: (event) => {
            const total = event.total ?? file.size;
            setProgress((p) => ({
              ...p,
              [slot]: total ? Math.round((event.loaded / total) * 100) : 0,
            }));
          },
        },
      );
      // Os outros documentos não se substituem no servidor: envia-se o novo e tira-se o antigo.
      if (replaced) await deleteProductVersionDocument(version.id, replaced.id);
      await onChanged();
      return true;
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
      return false;
    } finally {
      setProgress((p) => {
        const next = { ...p };
        delete next[slot];
        return next;
      });
    }
  };

  const remove = async (document: ProductDocumentResponse) => {
    setRemoving(null);
    setMessage(null);
    try {
      await deleteProductVersionDocument(version.id, document.id);
      await onChanged();
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    }
  };

  const actions = (
    slot: string,
    label: string,
    document: ProductDocumentResponse | undefined,
    onFile: (file: File) => void,
  ) => (
    <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap alignItems="center">
      {!readOnly && (
        <FileUploadButton
          label={document ? t('products.documents.replace') : t('products.documents.upload')}
          accept={pdf}
          onFile={onFile}
          disabled={slot in progress}
        />
      )}
      {document && (
        <FileOpenButton
          fileId={document.fileId}
          label={t('products.documents.view')}
          onError={setMessage}
        />
      )}
      {document && !readOnly && (
        <Button
          size="small"
          color="error"
          startIcon={<Delete />}
          onClick={() => setRemoving(document)}
          aria-label={t('products.documents.removeOf', { name: label })}
        >
          {t('products.documents.remove')}
        </Button>
      )}
      {slot in progress && (
        <LinearProgress
          variant="determinate"
          value={progress[slot] ?? 0}
          sx={{ width: '100%' }}
          aria-label={t('products.documents.uploading')}
        />
      )}
    </Stack>
  );

  const mainSlot = (docType: DocType, language: Language) => {
    const document = find(docType, language);
    const slot = `${docType}-${language}`;
    const label =
      language === 'pt'
        ? t(`productDocumentType.${docType}`)
        : t('products.documents.inEnglish', { name: t(`productDocumentType.${docType}`) });
    const error = language === 'pt' ? errors[`documents.${docType}`] : undefined;
    return (
      <Paper key={slot} component="section" variant="outlined" aria-label={label} sx={{ p: 2 }}>
        <Stack spacing={1}>
          <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
            <Typography sx={{ fontWeight: 600 }}>{label}</Typography>
            <Chip
              size="small"
              variant="outlined"
              color={document ? 'success' : language === 'pt' ? 'warning' : 'default'}
              label={document ? t('products.documents.sent') : t('products.documents.missing')}
            />
          </Stack>
          {error && !document && (
            <Typography variant="body2" color="error">
              {fieldText(t, error)}
            </Typography>
          )}
          {actions(slot, label, document, (file) => {
            void upload(slot, file, { docType, language });
          })}
        </Stack>
      </Paper>
    );
  };

  return (
    <Stack spacing={3}>
      {message && (
        <Alert severity="error" onClose={() => setMessage(null)}>
          {message}
        </Alert>
      )}
      <Stack spacing={2}>
        <Typography variant="h6" component="h3">
          {t('products.documents.required')}
        </Typography>
        {mainTypes.map((docType) => mainSlot(docType, 'pt'))}
      </Stack>
      <Stack spacing={2}>
        <Typography variant="h6" component="h3">
          {t('products.documents.english')}
        </Typography>
        {mainTypes.map((docType) => mainSlot(docType, 'en'))}
      </Stack>
      <Stack spacing={2}>
        <Typography variant="h6" component="h3">
          {t('products.documents.others')}
        </Typography>
        {others.length === 0 && pending.length === 0 && (
          <Typography variant="body2" color="text.secondary">
            {t('products.documents.noOthers')}
          </Typography>
        )}
        {others.map((document) => {
          const slot = document.id;
          const label = document.title || t(`productDocumentType.${document.docType}`);
          return (
            <Paper
              key={slot}
              component="section"
              variant="outlined"
              aria-label={label}
              sx={{ p: 2 }}
            >
              <Stack spacing={1}>
                <Typography sx={{ fontWeight: 600, wordBreak: 'break-word' }}>{label}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {t(`productDocumentType.${document.docType}`)} ·{' '}
                  {t(`products.documents.language.${document.language}`)}
                </Typography>
                {actions(slot, label, document, (file) => {
                  void upload(
                    slot,
                    file,
                    {
                      docType: document.docType,
                      language: document.language,
                      title: document.title,
                    },
                    document,
                  );
                })}
              </Stack>
            </Paper>
          );
        })}
        {pending.map((item) => {
          const slot = `pending-${item.key}`;
          const set = (patch: Partial<PendingDocument>) =>
            setPending((list) => list.map((p) => (p.key === item.key ? { ...p, ...patch } : p)));
          return (
            <Paper
              key={slot}
              component="section"
              variant="outlined"
              aria-label={t('products.documents.newDocument')}
              sx={{ p: 2 }}
            >
              <Stack spacing={2}>
                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                  <TextField
                    select
                    label={t('products.documents.type')}
                    value={item.docType}
                    onChange={(event) => set({ docType: event.target.value as DocType })}
                    fullWidth
                    slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
                  >
                    {otherTypes.map((type) => (
                      <option key={type} value={type}>
                        {t(`productDocumentType.${type}`)}
                      </option>
                    ))}
                  </TextField>
                  <TextField
                    label={t('products.documents.title')}
                    value={item.title}
                    onChange={(event) => set({ title: event.target.value })}
                    fullWidth
                    slotProps={{ htmlInput: { maxLength: 120 } }}
                  />
                </Stack>
                <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                  <FileUploadButton
                    label={t('products.documents.upload')}
                    accept={pdf}
                    disabled={slot in progress}
                    onFile={(file) => {
                      void upload(slot, file, {
                        docType: item.docType,
                        language: 'pt',
                        title: item.title.trim() || undefined,
                      }).then((ok) => {
                        if (ok) setPending((list) => list.filter((p) => p.key !== item.key));
                      });
                    }}
                  />
                  <Button
                    size="small"
                    onClick={() => setPending((list) => list.filter((p) => p.key !== item.key))}
                  >
                    {t('common.cancel')}
                  </Button>
                  {slot in progress && (
                    <LinearProgress
                      variant="determinate"
                      value={progress[slot] ?? 0}
                      sx={{ width: '100%' }}
                      aria-label={t('products.documents.uploading')}
                    />
                  )}
                </Stack>
              </Stack>
            </Paper>
          );
        })}
        {!readOnly && (
          <div>
            <Button
              variant="outlined"
              startIcon={<Add />}
              onClick={() => {
                setPending((list) => [...list, { key: nextKey, docType: 'OTHER', title: '' }]);
                setNextKey((k) => k + 1);
              }}
            >
              {t('products.documents.add')}
            </Button>
          </div>
        )}
      </Stack>
      <Dialog open={!!removing} onClose={() => setRemoving(null)} aria-labelledby="remove-doc">
        <DialogTitle id="remove-doc">{t('products.documents.removeDialog.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('products.documents.removeDialog.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRemoving(null)}>{t('common.cancel')}</Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => removing && void remove(removing)}
          >
            {t('products.documents.remove')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
