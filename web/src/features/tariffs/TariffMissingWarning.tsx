import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { getRatingRules } from '../../api/generated';
import { useRatingSettings } from './ChargesPanel';
import { ratingRulesQueryKey } from './TariffsPage';

/**
 * Aviso (não bloqueia) antes de publicar uma versão sem tarifa numa seguradora com tarifação
 * interna: a versão não apareceria nas cotações (S3-03 §8).
 */
export function TariffMissingWarning({ versionId }: { versionId: string }) {
  const { t } = useTranslation();
  const rules = useQuery({
    queryKey: ratingRulesQueryKey(versionId),
    queryFn: ({ signal }) => getRatingRules(versionId, undefined, signal),
    refetchOnWindowFocus: false,
  });
  const settings = useRatingSettings();
  if (rules.data?.configured !== false || settings.data?.ratingMode !== 'INTERNAL') return null;
  return (
    <Alert
      severity="warning"
      data-testid="tariff-missing-warning"
      action={
        <Button
          color="inherit"
          size="small"
          component={RouterLink}
          to={`/seguradora/tarifas/${versionId}`}
        >
          {t('tariffs.publishWarning.link')}
        </Button>
      }
    >
      {t('tariffs.publishWarning.text')}
    </Alert>
  );
}
