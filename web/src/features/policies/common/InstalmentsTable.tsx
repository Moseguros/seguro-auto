import Chip from '@mui/material/Chip';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import { useTranslation } from 'react-i18next';
import type { InstalmentStatus, PolicyInstalment } from '../../../api/generated';
import { formatMoney } from '../../../format/money';
import { formatDate } from '../../../i18n/format';

function instalmentColor(status: InstalmentStatus) {
  if (status === 'PAID') return 'success';
  if (status === 'OVERDUE') return 'error';
  if (status === 'DUE') return 'warning';
  return 'default';
}

/** Prestações da apólice, só de leitura (pagar prestações é do S7-02). */
export function InstalmentsTable({ instalments }: { instalments: PolicyInstalment[] }) {
  const { t, i18n } = useTranslation();
  return (
    <TableContainer sx={{ overflowX: 'auto' }}>
      <Table size="small" aria-label={t('customerPolicies.instalments')}>
        <TableHead>
          <TableRow>
            <TableCell>{t('policyCommon.instalments.seq')}</TableCell>
            <TableCell>{t('policyCommon.instalments.dueDate')}</TableCell>
            <TableCell align="right">{t('policyCommon.instalments.amount')}</TableCell>
            <TableCell>{t('policyCommon.instalments.status')}</TableCell>
            <TableCell>{t('policyCommon.instalments.paidAt')}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {instalments.map((instalment) => (
            <TableRow key={instalment.seq}>
              <TableCell>{instalment.seq}</TableCell>
              <TableCell sx={{ whiteSpace: 'nowrap' }}>
                {formatDate(instalment.dueDate, i18n.language)}
              </TableCell>
              <TableCell
                align="right"
                sx={{ fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' }}
              >
                {formatMoney(instalment.amount, i18n.language)}
              </TableCell>
              <TableCell>
                <Chip
                  size="small"
                  color={instalmentColor(instalment.status)}
                  label={t(`instalmentStatus.${instalment.status}`)}
                />
              </TableCell>
              <TableCell sx={{ whiteSpace: 'nowrap' }}>
                {instalment.paidAt ? formatDate(instalment.paidAt, i18n.language) : '—'}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
