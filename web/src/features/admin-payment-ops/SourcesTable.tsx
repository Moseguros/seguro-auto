import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { AdminPaymentFileSource } from '../../api/generated';
import { useCodeText } from '../insurer-payments/common';
import { formatStamp } from '../insurer-payments/format';

/** Fontes paradas primeiro; depois pelo nome da seguradora. */
export function sortSources(
  sources: AdminPaymentFileSource[],
  language = 'pt',
): AdminPaymentFileSource[] {
  return [...sources].sort((a, b) => {
    const stale = Number(!!b.staleSince) - Number(!!a.staleSince);
    if (stale !== 0) return stale;
    return a.insurer.displayName.localeCompare(b.insurer.displayName, language);
  });
}

/** Fontes de ficheiros de todas as seguradoras (S5-12 §6). */
export function SourcesTable({
  sources,
  narrow,
}: {
  sources: AdminPaymentFileSource[];
  narrow: boolean;
}) {
  const { t, i18n } = useTranslation();
  const codeText = useCodeText();
  const sorted = sortSources(sources, i18n.language);

  const outcome = (s: AdminPaymentFileSource) =>
    s.lastPollOutcome ? (
      <Stack spacing={0.5} alignItems="flex-start">
        <Chip
          size="small"
          variant="outlined"
          color={s.lastPollOutcome === 'OK' ? 'success' : 'error'}
          label={t(`paymentSettingsPage.fileSource.outcome.${s.lastPollOutcome}`)}
        />
        {s.lastPollError && (
          <Typography variant="caption">{codeText('pollErrorCode', s.lastPollError)}</Typography>
        )}
      </Stack>
    ) : (
      '—'
    );
  const stale = (s: AdminPaymentFileSource) =>
    s.staleSince ? (
      <Chip
        size="small"
        color="warning"
        label={`${t('adminPaymentOps.sources.stale')} · ${formatStamp(s.staleSince)}`}
        data-testid="source-stale"
      />
    ) : (
      '—'
    );
  const yesNo = (value: boolean) => t(value ? 'common.yes' : 'common.no');

  let content;
  if (sorted.length === 0) {
    content = <Typography variant="body2">{t('adminPaymentOps.sources.empty')}</Typography>;
  } else if (narrow) {
    content = (
      <Stack spacing={1.5} component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
        {sorted.map((s) => (
          <li key={s.sourceId}>
            <Card
              variant="outlined"
              data-testid="source"
              data-stale={s.staleSince ? 'true' : undefined}
              sx={s.staleSince ? { borderColor: 'warning.main' } : {}}
            >
              <CardContent>
                <Stack spacing={0.75}>
                  <Typography fontWeight={600}>{s.insurer.displayName}</Typography>
                  <Typography variant="body2">
                    {s.entity} · {s.fileFormat} · {t('adminPaymentOps.sources.enabled')}:{' '}
                    {yesNo(s.enabled)}
                  </Typography>
                  <Typography variant="body2">
                    {t('adminPaymentOps.sources.lastPoll')}: {formatStamp(s.lastPollAt) || '—'}
                  </Typography>
                  {outcome(s)}
                  <Typography variant="body2">
                    {t('adminPaymentOps.sources.lastSuccess')}:{' '}
                    {formatStamp(s.lastSuccessAt) || '—'}
                  </Typography>
                  {s.staleSince && stale(s)}
                  <Typography variant="body2">
                    {t('adminPaymentOps.sources.files24h')}: {s.filesLast24h} ·{' '}
                    {t('adminPaymentOps.sources.rejected24h')}: {s.rejectedLast24h}
                  </Typography>
                </Stack>
              </CardContent>
            </Card>
          </li>
        ))}
      </Stack>
    );
  } else {
    content = (
      <TableContainer sx={{ maxWidth: '100%', overflowX: 'auto' }}>
        <Table size="small" aria-label={t('adminPaymentOps.sources.title')}>
          <TableHead>
            <TableRow>
              <TableCell>{t('adminPaymentOps.sources.insurer')}</TableCell>
              <TableCell>{t('adminPaymentOps.sources.entity')}</TableCell>
              <TableCell>{t('adminPaymentOps.sources.format')}</TableCell>
              <TableCell>{t('adminPaymentOps.sources.enabled')}</TableCell>
              <TableCell>{t('adminPaymentOps.sources.lastPoll')}</TableCell>
              <TableCell>{t('adminPaymentOps.sources.outcome')}</TableCell>
              <TableCell>{t('adminPaymentOps.sources.lastSuccess')}</TableCell>
              <TableCell>{t('adminPaymentOps.sources.staleSince')}</TableCell>
              <TableCell align="right">{t('adminPaymentOps.sources.files24h')}</TableCell>
              <TableCell align="right">{t('adminPaymentOps.sources.rejected24h')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sorted.map((s) => (
              <TableRow
                key={s.sourceId}
                data-testid="source"
                data-stale={s.staleSince ? 'true' : undefined}
                sx={
                  s.staleSince
                    ? { bgcolor: (theme) => `${theme.palette.warning.main}14` }
                    : undefined
                }
              >
                <TableCell>{s.insurer.displayName}</TableCell>
                <TableCell>{s.entity}</TableCell>
                <TableCell>{s.fileFormat}</TableCell>
                <TableCell>{yesNo(s.enabled)}</TableCell>
                <TableCell sx={{ whiteSpace: 'nowrap' }}>
                  {formatStamp(s.lastPollAt) || '—'}
                </TableCell>
                <TableCell>{outcome(s)}</TableCell>
                <TableCell sx={{ whiteSpace: 'nowrap' }}>
                  {formatStamp(s.lastSuccessAt) || '—'}
                </TableCell>
                <TableCell>{stale(s)}</TableCell>
                <TableCell align="right" sx={{ fontVariantNumeric: 'tabular-nums' }}>
                  {s.filesLast24h}
                </TableCell>
                <TableCell align="right" sx={{ fontVariantNumeric: 'tabular-nums' }}>
                  {s.rejectedLast24h}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  }

  return (
    <Paper
      variant="outlined"
      component="section"
      aria-label={t('adminPaymentOps.sources.title')}
      sx={{ p: 2, minWidth: 0 }}
    >
      <Stack spacing={2}>
        <Typography variant="h6" component="h2">
          {t('adminPaymentOps.sources.title')}
        </Typography>
        {content}
      </Stack>
    </Paper>
  );
}
