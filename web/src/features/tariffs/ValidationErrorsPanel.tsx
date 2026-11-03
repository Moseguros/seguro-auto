import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Link from '@mui/material/Link';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import type { TFunction } from 'i18next';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../errors/problem';
import { locate, type TariffLocation } from './pointer';
import type { Rating, TariffError } from './ratingTypes';

/** Sítio de um erro em texto: "MTPL › Idade do condutor › linha 3". */
export function locationText(
  t: TFunction,
  location: TariffLocation | null,
  rating: Rating | null,
): string {
  if (!location || location.kind === 'top') return t('tariffs.errorsPanel.top');
  if (location.kind === 'duration') {
    const days = rating?.durationFactors?.[location.index]?.days;
    return days === undefined
      ? t('tariffs.errorsPanel.duration')
      : `${t('tariffs.errorsPanel.duration')} › ${t('simulator.days', { days })}`;
  }
  const coverage = rating?.coverages[location.coverage];
  const parts = [coverage?.coverageCode ?? `#${location.coverage + 1}`];
  if (location.kind === 'base') parts.push(t('tariffs.errorsPanel.base'));
  if (location.kind === 'factor') {
    const factor = coverage?.factors?.[location.factor];
    parts.push(factor?.label || factor?.code || `#${location.factor + 1}`);
  }
  if ((location.kind === 'base' || location.kind === 'factor') && location.row !== undefined) {
    parts.push(t('tariffs.errorsPanel.row', { n: location.row + 1 }));
  }
  return parts.join(' › ');
}

export interface ValidationErrorsPanelProps {
  errors: TariffError[];
  rating: Rating | null;
  onSelect(location: TariffLocation): void;
}

/** Lista dos erros da tarifa (do cliente ou do servidor); clicar leva ao sítio. */
export function ValidationErrorsPanel({ errors, rating, onSelect }: ValidationErrorsPanelProps) {
  const { t } = useTranslation();
  if (errors.length === 0) return null;
  return (
    <Alert severity="error" data-testid="tariff-errors">
      <AlertTitle>{t('tariffs.errorsPanel.title', { count: errors.length })}</AlertTitle>
      <List dense disablePadding>
        {errors.map((error, index) => {
          const location = locate(error.field);
          const where = locationText(t, location, rating);
          const text = `${fieldMessage(t, error.code)} (${where})`;
          return (
            <ListItem
              key={`${error.field}-${error.code}-${index}`}
              disableGutters
              data-code={error.code}
            >
              {location && location.kind !== 'top' ? (
                <Link
                  component="button"
                  type="button"
                  onClick={() => onSelect(location)}
                  sx={{ textAlign: 'left' }}
                >
                  {text}
                </Link>
              ) : (
                text
              )}
            </ListItem>
          );
        })}
      </List>
    </Alert>
  );
}
