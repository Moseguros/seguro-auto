import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { PolicyInstalment } from '../../../api/generated';
import { formatMoney } from '../../../format/money';
import { formatDate, formatDateTime } from '../../../i18n/format';

/** Prestações da apólice (n.º, vencimento, montante, estado, paga em), partilhado com o S6-06. */
export function InstalmentsTable({ instalments }: { instalments: PolicyInstalment[] }) {
  const { t, i18n } = useTranslation();
  const language = i18n.language;
  if (instalments.length === 0) {
    return <Typography>{t('insurerPolicies.instalments.empty')}</Typography>;
  }
  return (
    <TableContainer sx={{ overflowX: 'auto' }}>
      <Table size="small" aria-label={t('insurerPolicies.tabs.instalments')}>
        <TableHead>
          <TableRow>
            <TableCell>{t('insurerPolicies.instalments.seq')}</TableCell>
            <TableCell>{t('insurerPolicies.instalments.dueDate')}</TableCell>
            <TableCell align="right">{t('insurerPolicies.instalments.amount')}</TableCell>
            <TableCell>{t('insurerPolicies.instalments.status')}</TableCell>
            <TableCell>{t('insurerPolicies.instalments.paidAt')}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {instalments.map((instalment) => (
            <TableRow key={instalment.seq}>
              <TableCell>{instalment.seq}</TableCell>
              <TableCell sx={{ whiteSpace: 'nowrap' }}>
                {formatDate(instalment.dueDate, language)}
              </TableCell>
              <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                {formatMoney(instalment.amount, language)}
              </TableCell>
              <TableCell>{t(`instalmentStatus.${instalment.status}`)}</TableCell>
              <TableCell sx={{ whiteSpace: 'nowrap' }}>
                {instalment.paidAt ? formatDateTime(instalment.paidAt, language) : '—'}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
