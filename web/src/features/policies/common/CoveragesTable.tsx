import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import { useTranslation } from 'react-i18next';
import type { PolicyCoverage } from '../../../api/generated';
import { formatMoney } from '../../../format/money';

const dash = '—';

/** Franquia: montante fixo, percentagem ou `—`. */
export function deductibleText(coverage: PolicyCoverage, language: string): string {
  if (coverage.deductibleType === 'FIXED' && coverage.deductibleAmount) {
    return formatMoney(coverage.deductibleAmount, language);
  }
  if (coverage.deductibleType === 'PERCENT' && coverage.deductiblePercent !== undefined) {
    const value = new Intl.NumberFormat(language === 'en' ? 'en' : 'pt-MZ', {
      maximumFractionDigits: 2,
    }).format(coverage.deductiblePercent);
    return `${value} %`;
  }
  return dash;
}

/** Coberturas da apólice (nome, capital, franquia, prémio), partilhado com o S6-06. */
export function CoveragesTable({ coverages }: { coverages: PolicyCoverage[] }) {
  const { t, i18n } = useTranslation();
  const language = i18n.language;
  return (
    <TableContainer sx={{ overflowX: 'auto' }}>
      <Table size="small" aria-label={t('insurerPolicies.fields.coverages')}>
        <TableHead>
          <TableRow>
            <TableCell>{t('insurerPolicies.coverages.name')}</TableCell>
            <TableCell align="right">{t('insurerPolicies.coverages.capital')}</TableCell>
            <TableCell align="right">{t('insurerPolicies.coverages.deductible')}</TableCell>
            <TableCell align="right">{t('insurerPolicies.coverages.premium')}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {coverages.map((coverage) => (
            <TableRow key={coverage.code}>
              <TableCell>
                {t(`coverage.${coverage.code}`, { defaultValue: coverage.code })}
              </TableCell>
              <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                {coverage.capital ? formatMoney(coverage.capital, language) : dash}
              </TableCell>
              <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                {deductibleText(coverage, language)}
              </TableCell>
              <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                {coverage.premium ? formatMoney(coverage.premium, language) : dash}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
