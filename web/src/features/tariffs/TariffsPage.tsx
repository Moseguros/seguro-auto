import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import ButtonBase from '@mui/material/ButtonBase';
import Chip from '@mui/material/Chip';
import Collapse from '@mui/material/Collapse';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import {
  getInsurerProduct,
  getRatingRules,
  listInsurerProducts,
  type ProductSummaryResponse,
  type ProductVersionRefResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { getProblem, problemMessage } from '../../errors/problem';
import { ProductStatusChip } from '../products/ProductStatusChip';
import { productQueryKey, productsQueryKey } from '../products/productQueries';
import { ChargesPanel, useRatingSettings } from './ChargesPanel';

export const ratingRulesQueryKey = (versionId: string) => ['ratingRules', versionId] as const;

/** Etiqueta "Com tarifa" / "Sem tarifa" de uma versão (lê a tarifa quando aparece). */
function TariffChip({ versionId }: { versionId: string }) {
  const { t } = useTranslation();
  const query = useQuery({
    queryKey: ratingRulesQueryKey(versionId),
    queryFn: ({ signal }) => getRatingRules(versionId, undefined, signal),
    refetchOnWindowFocus: false,
  });
  if (query.isPending) return <Skeleton width={90} />;
  if (query.isError) return <Typography variant="body2">—</Typography>;
  return query.data.configured ? (
    <Chip size="small" color="success" variant="outlined" label={t('tariffs.configured')} />
  ) : (
    <Chip size="small" color="warning" variant="outlined" label={t('tariffs.notConfigured')} />
  );
}

function VersionAction({ version }: { version: ProductVersionRefResponse }) {
  const { t } = useTranslation();
  const canManage = usePermission(Permissions.ratingManage);
  const editable = version.status === 'DRAFT' && canManage;
  return (
    <Button
      size="small"
      variant={editable ? 'contained' : 'outlined'}
      component={RouterLink}
      to={`/seguradora/tarifas/${version.id}`}
    >
      {editable ? t('tariffs.edit') : t('tariffs.view')}
    </Button>
  );
}

/** Um produto: abre-se para ver as versões e o estado da tarifa de cada uma. */
function ProductSection({ product }: { product: ProductSummaryResponse }) {
  const { t } = useTranslation();
  const theme = useTheme();
  const narrow = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const [open, setOpen] = useState(false);
  const query = useQuery({
    queryKey: productQueryKey(product.id),
    queryFn: ({ signal }) => getInsurerProduct(product.id, undefined, signal),
    enabled: open,
    refetchOnWindowFocus: false,
  });
  const versions = [...(query.data?.versions ?? [])].sort((a, b) => b.versionNo - a.versionNo);
  const regionId = `product-${product.id}-versions`;

  return (
    <Paper variant="outlined" data-testid={`product-${product.code}`}>
      <ButtonBase
        onClick={() => setOpen((o) => !o)}
        aria-expanded={open}
        aria-controls={regionId}
        sx={{ width: '100%', justifyContent: 'space-between', p: 2, textAlign: 'left', gap: 2 }}
      >
        <Stack spacing={0.25} sx={{ minWidth: 0 }}>
          <Typography
            variant="subtitle1"
            component="h2"
            sx={{ fontWeight: 600, wordBreak: 'break-word' }}
          >
            {product.name}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {product.code} · {t(`productType.${product.type}`)}
          </Typography>
        </Stack>
        {open ? <ExpandLess /> : <ExpandMore />}
      </ButtonBase>
      <Collapse in={open} unmountOnExit>
        <Stack id={regionId} sx={{ px: 2, pb: 2 }} spacing={1}>
          {query.isPending && <Skeleton variant="rectangular" height={48} />}
          {query.isError && (
            <Alert
              severity="error"
              action={
                <Button color="inherit" size="small" onClick={() => void query.refetch()}>
                  {t('tariffs.retry')}
                </Button>
              }
            >
              {problemMessage(t, getProblem(query.error))}
            </Alert>
          )}
          {query.isSuccess && versions.length === 0 && (
            <Typography variant="body2">{t('tariffs.noVersions')}</Typography>
          )}
          {query.isSuccess &&
            versions.length > 0 &&
            (narrow ? (
              versions.map((version) => (
                <Paper
                  key={version.id}
                  variant="outlined"
                  sx={{ p: 1.5 }}
                  data-testid={`version-${version.id}`}
                >
                  <Stack spacing={1}>
                    <Stack
                      direction="row"
                      spacing={1}
                      alignItems="center"
                      useFlexGap
                      flexWrap="wrap"
                    >
                      <Typography variant="subtitle2">
                        {t('tariffs.versionNo', { n: version.versionNo })}
                      </Typography>
                      <ProductStatusChip status={version.status} kind="version" />
                      <TariffChip versionId={version.id} />
                    </Stack>
                    <div>
                      <VersionAction version={version} />
                    </div>
                  </Stack>
                </Paper>
              ))
            ) : (
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>{t('tariffs.columns.version')}</TableCell>
                    <TableCell>{t('tariffs.columns.status')}</TableCell>
                    <TableCell>{t('tariffs.columns.tariff')}</TableCell>
                    <TableCell align="right">{t('tariffs.columns.actions')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {versions.map((version) => (
                    <TableRow key={version.id} data-testid={`version-${version.id}`}>
                      <TableCell>{version.versionNo}</TableCell>
                      <TableCell>
                        <ProductStatusChip status={version.status} kind="version" />
                      </TableCell>
                      <TableCell>
                        <TariffChip versionId={version.id} />
                      </TableCell>
                      <TableCell align="right">
                        <VersionAction version={version} />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ))}
        </Stack>
      </Collapse>
    </Paper>
  );
}

function ProductsTab() {
  const { t } = useTranslation();
  const params = { page: 0, size: 100, sort: 'name,asc' };
  const query = useQuery({
    queryKey: [...productsQueryKey, 'list', params],
    queryFn: ({ signal }) => listInsurerProducts(params, undefined, signal),
  });

  if (query.isPending) {
    return (
      <Stack spacing={1} data-testid="tariffs-loading">
        <Skeleton variant="rectangular" height={64} />
        <Skeleton variant="rectangular" height={64} />
      </Stack>
    );
  }
  if (query.isError) {
    return (
      <Alert
        severity="error"
        action={
          <Button color="inherit" size="small" onClick={() => void query.refetch()}>
            {t('tariffs.retry')}
          </Button>
        }
      >
        {problemMessage(t, getProblem(query.error))}
      </Alert>
    );
  }
  if (query.data.content.length === 0) {
    return (
      <Paper variant="outlined" sx={{ p: 4, textAlign: 'center' }}>
        <Stack spacing={1} alignItems="center">
          <Typography>{t('tariffs.empty')}</Typography>
          <Link component={RouterLink} to="/seguradora/produtos">
            {t('tariffs.emptyLink')}
          </Link>
        </Stack>
      </Paper>
    );
  }
  return (
    <Stack spacing={1.5}>
      {query.data.content.map((product) => (
        <ProductSection key={product.id} product={product} />
      ))}
    </Stack>
  );
}

/** `/seguradora/tarifas`: produtos com o estado da tarifa de cada versão, e os encargos. */
export function TariffsPage() {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const tab = searchParams.get('separador') === 'encargos' ? 'encargos' : 'produtos';
  const settings = useRatingSettings();

  return (
    <Stack spacing={3} sx={{ minWidth: 0, maxWidth: 1100 }}>
      <Typography variant="h4" component="h1">
        {t('tariffs.title')}
      </Typography>
      {settings.data?.ratingMode === 'EXTERNAL' && (
        <Alert severity="info" data-testid="external-notice">
          {t('tariffs.externalNotice')}
        </Alert>
      )}
      <Tabs
        value={tab}
        onChange={(_, value: string) =>
          setSearchParams(value === 'encargos' ? { separador: 'encargos' } : {}, { replace: true })
        }
        variant="scrollable"
        allowScrollButtonsMobile
      >
        <Tab value="produtos" label={t('tariffs.tabs.products')} />
        <Tab value="encargos" label={t('tariffs.tabs.charges')} />
      </Tabs>
      {tab === 'produtos' ? <ProductsTab /> : <ChargesPanel />}
    </Stack>
  );
}
