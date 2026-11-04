import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import type { TFunction } from 'i18next';
import { useTranslation } from 'react-i18next';
import { formatMoney } from '../../format/money';
import type { CodeSet, Condition, ValueRange } from './ratingTypes';
import { variableDef, variables } from './variables';

/** Texto do valor de uma condição: "25 a 69", "menos de 25", "Ligeiro misto, Pesado", "sim". */
export function conditionValueText(
  t: TFunction,
  name: keyof Condition,
  condition: unknown,
  language: string,
): string {
  const def = variableDef(name);
  if (!def) return '';
  if (def.kind === 'set') {
    return ((condition as CodeSet).in ?? []).map((c) => t(`${def.optionKey}.${c}`)).join(', ');
  }
  if (def.kind === 'bool') {
    return condition ? t('tariffs.conditions.yes') : t('tariffs.conditions.no');
  }
  const range = condition as ValueRange;
  const money = def.kind === 'money';
  const show = (n: number) => (money ? formatMoney({ amount: n }, language) : String(n));
  if (range.gte !== undefined && range.lt !== undefined) {
    return money
      ? t('tariffs.conditions.moneyBetween', { gte: show(range.gte), lt: show(range.lt) })
      : t('tariffs.conditions.between', { gte: show(range.gte), to: show(range.lt - 1) });
  }
  if (range.gte !== undefined) return t('tariffs.conditions.from', { gte: show(range.gte) });
  if (range.lt !== undefined) return t('tariffs.conditions.below', { lt: show(range.lt) });
  return '';
}

/** Uma etiqueta por condição ("Idade do condutor (anos): 25 a 69"); sem condições, "Sempre". */
export function ConditionSummary({ when }: { when: Condition }) {
  const { t, i18n } = useTranslation();
  const present = variables.filter((v) => when[v.name] !== undefined);
  if (present.length === 0) {
    return <Chip size="small" variant="outlined" label={t('tariffs.conditions.always')} />;
  }
  return (
    <Stack direction="row" spacing={0.5} useFlexGap flexWrap="wrap">
      {present.map((v) => (
        <Chip
          key={v.name}
          size="small"
          label={`${t(`variables.${v.name}`)}: ${conditionValueText(t, v.name, when[v.name], i18n.language)}`}
          sx={{ maxWidth: '100%', height: 'auto', '& .MuiChip-label': { whiteSpace: 'normal' } }}
        />
      ))}
    </Stack>
  );
}
