import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import type { TFunction } from 'i18next';
import { useTranslation } from 'react-i18next';
import type { PolicyCoverage } from '../../../api/generated';
import { formatMoney } from '../../../format/money';

const dash = '—';

/** Franquia: montante fixo, percentagem ou `—`. */
export function deductibleText(coverage: PolicyCoverage, t: TFunction, language: string): string {
  if (coverage.deductibleType === 'FIXED' && coverage.deductibleAmount) {
    return formatMoney(coverage.deductibleAmount, language);
  }
  if (coverage.deductibleType === 'PERCENT' && coverage.deductiblePercent != null) {
    return t('policyCommon.coverages.percent', { value: coverage.deductiblePercent });
  }
  return dash;
}

/** Coberturas da apólice: nome (`coverage.<CODE>`), capital, franquia e prémio. */
export function CoveragesTable({ coverages }: { coverages: PolicyCoverage[] }) {
  const { t, i18n } = useTranslation();
  const number = { fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' } as const;
  return (
    <TableContainer sx={{ overflowX: 'auto' }}>
      <Table size="small" aria-label={t('customerPolicies.coverages')}>
        <TableHead>
          <TableRow>
            <TableCell>{t('policyCommon.coverages.name')}</TableCell>
            <TableCell align="right">{t('policyCommon.coverages.capital')}</TableCell>
            <TableCell align="right">{t('policyCommon.coverages.deductible')}</TableCell>
            <TableCell align="right">{t('policyCommon.coverages.premium')}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {coverages.map((coverage) => (
            <TableRow key={coverage.code}>
              <TableCell sx={{ wordBreak: 'break-word' }}>
                {t(`coverage.${coverage.code}`, { defaultValue: coverage.code })}
              </TableCell>
              <TableCell align="right" sx={number}>
                {coverage.capital ? formatMoney(coverage.capital, i18n.language) : dash}
              </TableCell>
              <TableCell align="right" sx={number}>
                {deductibleText(coverage, t, i18n.language)}
              </TableCell>
              <TableCell align="right" sx={number}>
                {formatMoney(coverage.premium, i18n.language)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
