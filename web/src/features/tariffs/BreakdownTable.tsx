import ExpandMore from '@mui/icons-material/ExpandMore';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import type { PremiumBreakdown } from '../../api/generated';
import { formatMoney } from '../../format/money';
import { centsToMeticais, formatDecimal } from './decimal';
import { rowValueText } from './rowValue';
import { valueKindOf, type BaseType } from './ratingTypes';

/** Decomposição do prémio (S3-01 §10), como chega do simulador. */
interface BreakdownFactor {
  code: string;
  label?: string | null;
  rowIndex: number;
  rowLabel?: string | null;
  value: string;
}

interface BreakdownCoverage {
  code: string;
  capitalAmount?: number | null;
  base: {
    type: BaseType;
    rowIndex: number;
    rowLabel?: string | null;
    value: string | number;
    amount: string | number;
  };
  factors?: BreakdownFactor[];
  bonusMalusFactor: string;
  exactPremium: string;
  premiumAmount: number;
}

interface BreakdownCharge {
  code: string;
  labelPt: string;
  labelEn: string;
  amount: number;
}

export interface Breakdown {
  variables?: Record<string, unknown>;
  durationFactor?: string;
  coverages: BreakdownCoverage[];
  commercialPremiumAmount: number;
  charges?: BreakdownCharge[];
  subtotalAmount?: number;
  roundingAdjustmentAmount: number;
  totalAmount: number;
}

const tabular = { fontVariantNumeric: 'tabular-nums' } as const;

function Line({ label, value, strong }: { label: ReactNode; value: ReactNode; strong?: boolean }) {
  return (
    <Stack direction="row" justifyContent="space-between" spacing={2}>
      <Typography variant="body2" sx={{ fontWeight: strong ? 700 : undefined, minWidth: 0 }}>
        {label}
      </Typography>
      <Typography
        variant="body2"
        component="span"
        sx={{
          ...tabular,
          fontWeight: strong ? 700 : undefined,
          textAlign: 'right',
          whiteSpace: 'nowrap',
        }}
      >
        {value}
      </Typography>
    </Stack>
  );
}

const computed = ['driverAgeYears', 'vehicleAgeYears', 'licenceYears'] as const;

/** Resultado `OFFERED` do simulador: variáveis, cada cobertura e os totais. */
export function BreakdownTable({ breakdown }: { breakdown: PremiumBreakdown }) {
  const { t, i18n } = useTranslation();
  const language = i18n.language;
  const b = breakdown as unknown as Breakdown;
  const money = (amount: number) => formatMoney({ amount }, language);
  const label = (text: string | null | undefined) => (text ? ` · ${text}` : '');

  return (
    <Stack spacing={2} data-testid="breakdown">
      <Stack spacing={0.5}>
        <Typography variant="subtitle2" component="h3">
          {t('simulator.breakdown.variables')}
        </Typography>
        {computed
          .filter((name) => typeof b.variables?.[name] === 'number')
          .map((name) => (
            <Typography key={name} variant="body2">
              {t(`simulator.computed.${name}`, { n: b.variables![name] })}
            </Typography>
          ))}
      </Stack>

      <Stack spacing={1}>
        <Typography variant="subtitle2" component="h3">
          {t('simulator.breakdown.coverages')}
        </Typography>
        {b.coverages.map((c) => {
          const baseKind = valueKindOf(c.base.type);
          return (
            <Accordion key={c.code} disableGutters variant="outlined">
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Stack
                  direction="row"
                  justifyContent="space-between"
                  spacing={2}
                  sx={{ width: '100%', pr: 1 }}
                >
                  <Typography sx={{ minWidth: 0 }}>{t(`coverage.${c.code}`)}</Typography>
                  <Typography component="span" sx={{ ...tabular, whiteSpace: 'nowrap' }}>
                    {money(c.premiumAmount)}
                  </Typography>
                </Stack>
              </AccordionSummary>
              <AccordionDetails>
                <Stack spacing={0.75} data-testid={`breakdown-${c.code}`}>
                  {typeof c.capitalAmount === 'number' && (
                    <Line label={t('simulator.breakdown.capital')} value={money(c.capitalAmount)} />
                  )}
                  <Line
                    label={`${t('simulator.breakdown.baseRow', { n: c.base.rowIndex + 1 })}${label(c.base.rowLabel)}`}
                    value={rowValueText(c.base.value, baseKind, language) ?? String(c.base.value)}
                  />
                  <Line
                    label={t('simulator.breakdown.baseAmount')}
                    value={`${centsToMeticais(c.base.amount, 4, language)} MT`}
                  />
                  {(c.factors ?? []).map((f) => (
                    <Line
                      key={f.code}
                      label={`${t('simulator.breakdown.factorRow', { name: f.label || f.code, n: f.rowIndex + 1 })}${label(f.rowLabel)}`}
                      value={`× ${formatDecimal(f.value, language)}`}
                    />
                  ))}
                  <Line
                    label={t('simulator.breakdown.bonusMalus')}
                    value={`× ${formatDecimal(c.bonusMalusFactor, language)}`}
                  />
                  <Line
                    label={t('simulator.breakdown.duration')}
                    value={`× ${formatDecimal(b.durationFactor ?? '1', language)}`}
                  />
                  <Line
                    label={t('simulator.breakdown.exactPremium')}
                    value={`${centsToMeticais(c.exactPremium, 4, language)} MT`}
                  />
                  <Line
                    label={t('simulator.breakdown.premium')}
                    value={money(c.premiumAmount)}
                    strong
                  />
                </Stack>
              </AccordionDetails>
            </Accordion>
          );
        })}
      </Stack>

      <Box>
        <Stack spacing={0.75}>
          <Line
            label={t('simulator.breakdown.commercialPremium')}
            value={money(b.commercialPremiumAmount)}
          />
          {(b.charges ?? []).map((charge) => (
            <Line
              key={charge.code}
              label={language === 'en' ? charge.labelEn : charge.labelPt}
              value={money(charge.amount)}
            />
          ))}
          <Line
            label={t('simulator.breakdown.rounding')}
            value={money(b.roundingAdjustmentAmount)}
          />
          <Divider />
          <Box data-testid="simulation-total">
            <Line label={t('simulator.breakdown.total')} value={money(b.totalAmount)} strong />
          </Box>
        </Stack>
      </Box>
    </Stack>
  );
}
