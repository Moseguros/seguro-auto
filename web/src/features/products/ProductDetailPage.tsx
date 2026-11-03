import ArrowBack from '@mui/icons-material/ArrowBack';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation, useNavigate, useParams } from 'react-router-dom';
import {
  createProductVersion,
  getInsurerProduct,
  retireInsurerProduct,
  type ProductResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatDate, formatDateTime } from '../../i18n/format';
import { ProductEditDialog } from './ProductEditDialog';
import { productQueryKey, productsQueryKey } from './productQueries';
import { ProductStatusChip } from './ProductStatusChip';

/** Estado de navegação: a versão acabada de publicar. */
export interface ProductDetailState {
  published?: number;
}

/** `/seguradora/produtos/:productId`: dados, acções e versões do produto. */
export function ProductDetailPage() {
  const { t, i18n } = useTranslation();
  const { productId = '' } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.productManage);
  const state = (location.state ?? {}) as ProductDetailState;
  const [editing, setEditing] = useState(false);
  const [retiring, setRetiring] = useState(false);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [done, setDone] = useState<string | null>(null);
  const query = useQuery({
    queryKey: productQueryKey(productId),
    queryFn: ({ signal }) => getInsurerProduct(productId, undefined, signal),
  });

  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    return <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>;
  }
  const product = query.data;
  const editable = canManage && product.status !== 'RETIRED';
  const editorPath = (versionId: string) =>
    `/seguradora/produtos/${product.id}/versoes/${versionId}`;

  const updated = (next: ProductResponse, text?: string) => {
    queryClient.setQueryData(productQueryKey(next.id), next);
    void queryClient.invalidateQueries({ queryKey: productsQueryKey });
    if (text) setDone(text);
  };

  const newVersion = async () => {
    setBusy(true);
    setMessage(null);
    try {
      const version = await createProductVersion(product.id);
      void queryClient.invalidateQueries({ queryKey: productQueryKey(product.id) });
      void queryClient.invalidateQueries({ queryKey: productsQueryKey });
      navigate(`${editorPath(version.id)}?passo=dados`);
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
      setBusy(false);
    }
  };

  const retire = async () => {
    setRetiring(false);
    setBusy(true);
    setMessage(null);
    try {
      updated(await retireInsurerProduct(product.id), t('products.detail.retired'));
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Stack spacing={3} sx={{ maxWidth: 1100, minWidth: 0 }}>
      <div>
        <Link
          component={RouterLink}
          to="/seguradora/produtos"
          sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
        >
          <ArrowBack fontSize="small" />
          {t('products.detail.back')}
        </Link>
      </div>
      {state.published !== undefined && (
        <Alert severity="success" role="status">
          {t('products.detail.published', { n: state.published })}
        </Alert>
      )}
      <Stack
        direction={{ xs: 'column', md: 'row' }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ md: 'flex-start' }}
      >
        <Stack spacing={1} sx={{ minWidth: 0 }}>
          <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
            {product.name}
          </Typography>
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap alignItems="center">
            <Typography variant="body2" color="text.secondary" sx={{ fontFamily: 'monospace' }}>
              {product.code}
            </Typography>
            <Chip size="small" label={t(`productType.${product.type}`)} />
            <ProductStatusChip status={product.status} />
          </Stack>
          {product.description && (
            <Typography variant="body2" sx={{ wordBreak: 'break-word' }}>
              {product.description}
            </Typography>
          )}
        </Stack>
        {editable && (
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} sx={{ flexShrink: 0 }}>
            <Button variant="outlined" onClick={() => setEditing(true)} disabled={busy}>
              {t('products.detail.edit')}
            </Button>
            {product.draftVersion ? (
              <Button
                variant="contained"
                component={RouterLink}
                to={`${editorPath(product.draftVersion.id)}?passo=dados`}
              >
                {t('products.detail.continueVersion', { n: product.draftVersion.versionNo })}
              </Button>
            ) : (
              <Button variant="contained" onClick={() => void newVersion()} disabled={busy}>
                {t('products.detail.newVersion')}
              </Button>
            )}
            <Button
              variant="outlined"
              color="error"
              onClick={() => setRetiring(true)}
              disabled={busy}
            >
              {t('products.detail.retire')}
            </Button>
          </Stack>
        )}
      </Stack>
      {message && (
        <Alert severity="error" role="alert" onClose={() => setMessage(null)}>
          {message}
        </Alert>
      )}
      <Stack spacing={1}>
        <Typography variant="h6" component="h2">
          {t('products.detail.versions')}
        </Typography>
        <TableContainer component={Paper} variant="outlined">
          <Table size="small" aria-label={t('products.detail.versions')}>
            <TableHead>
              <TableRow>
                <TableCell>{t('products.versionColumns.number')}</TableCell>
                <TableCell>{t('products.versionColumns.status')}</TableCell>
                <TableCell>{t('products.versionColumns.validity')}</TableCell>
                <TableCell>{t('products.versionColumns.publishedAt')}</TableCell>
                <TableCell>{t('products.versionColumns.notes')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {product.versions.map((version) => (
                <TableRow
                  key={version.id}
                  hover
                  onClick={() => navigate(editorPath(version.id))}
                  sx={{ cursor: 'pointer' }}
                  data-testid={`version-row-${version.versionNo}`}
                >
                  <TableCell>
                    <Link component={RouterLink} to={editorPath(version.id)}>
                      {t('products.detail.versionLabel', { n: version.versionNo })}
                    </Link>
                  </TableCell>
                  <TableCell>
                    <ProductStatusChip status={version.status} kind="version" />
                  </TableCell>
                  <TableCell sx={{ whiteSpace: 'nowrap' }}>
                    {version.validFrom
                      ? `${formatDate(version.validFrom, i18n.language)} – ${
                          version.validTo ? formatDate(version.validTo, i18n.language) : ''
                        }`
                      : '—'}
                  </TableCell>
                  <TableCell sx={{ whiteSpace: 'nowrap' }}>
                    {version.publishedAt ? formatDateTime(version.publishedAt, i18n.language) : '—'}
                  </TableCell>
                  <TableCell sx={{ minWidth: 160 }}>{version.changeNotes ?? ''}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Stack>
      {editing && (
        <ProductEditDialog
          product={product}
          onClose={() => setEditing(false)}
          onSaved={(next) => {
            setEditing(false);
            updated(next, t('products.detail.saved'));
          }}
          onConflict={() => void query.refetch()}
        />
      )}
      <Dialog open={retiring} onClose={() => setRetiring(false)} aria-labelledby="retire-title">
        <DialogTitle id="retire-title">{t('products.detail.retireDialog.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('products.detail.retireDialog.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRetiring(false)}>{t('common.cancel')}</Button>
          <Button color="error" variant="contained" onClick={() => void retire()}>
            {t('products.detail.retire')}
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={done !== null}
        autoHideDuration={4000}
        onClose={() => setDone(null)}
        message={done}
      />
    </Stack>
  );
}
