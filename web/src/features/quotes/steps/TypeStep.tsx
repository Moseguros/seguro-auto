import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { fieldMessage } from '../../../errors/problem';
import { backToWizard, ONE_YEAR_DAYS } from '../config';
import { allowedDurations, quoteProductTypes, type QuoteProductType } from '../quoteRules';
import { useStepValidity, type RiskVehicle, type StepProps } from './stepTypes';

export interface TypeStepProps extends StepProps {
  vehicle: RiskVehicle;
}

/** Passo "Tipo": três cartões de escolha única. */
export function TypeStep({
  mode,
  draft,
  update,
  onValidity,
  serverErrors,
  vehicle,
}: TypeStepProps) {
  const { t } = useTranslation();
  const noValue = !vehicle.commercialValue;
  const disabled = (type: QuoteProductType) => type === 'COMPREHENSIVE' && noValue;
  const current = quoteProductTypes.find((type) => type === draft.productType);
  useStepValidity(!!current && !disabled(current), onValidity);

  const choose = (type: QuoteProductType) =>
    update((draftNow) => ({
      productType: type,
      durationDays: allowedDurations(type).includes(draftNow.durationDays)
        ? draftNow.durationDays
        : ONE_YEAR_DAYS,
    }));

  return (
    <Stack spacing={1.5} role="radiogroup" aria-label={t('quoteWizard.steps.type')}>
      {serverErrors.productType && (
        <Typography color="error">{fieldMessage(t, serverErrors.productType)}</Typography>
      )}
      {quoteProductTypes.map((type) => {
        const selected = type === draft.productType;
        const off = disabled(type);
        return (
          <Card
            key={type}
            variant="outlined"
            elevation={0}
            sx={{
              borderColor: selected && !off ? 'primary.main' : undefined,
              borderWidth: selected && !off ? 2 : 1,
              opacity: off ? 0.7 : 1,
            }}
          >
            <CardActionArea
              role="radio"
              aria-checked={selected && !off}
              aria-disabled={off}
              disabled={off}
              onClick={() => choose(type)}
            >
              <CardContent>
                <Typography variant="h6" component="span" sx={{ display: 'block' }}>
                  {t(`quote.types.${type}.title`)}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {t(`quote.types.${type}.text`)}
                </Typography>
              </CardContent>
            </CardActionArea>
            {off && (
              <CardContent sx={{ pt: 0 }}>
                <Typography variant="body2" color="warning.main">
                  {t('quoteWizard.type.needsValue')}
                </Typography>
                {mode === 'customer' && vehicle.vehicleId ? (
                  <Link
                    component={RouterLink}
                    to={`/cliente/veiculos/${vehicle.vehicleId}/editar?voltar=${backToWizard}`}
                  >
                    {t('quoteWizard.type.setValue')}
                  </Link>
                ) : (
                  <Typography variant="body2">{t('quoteWizard.type.setValueBroker')}</Typography>
                )}
              </CardContent>
            )}
          </Card>
        );
      })}
    </Stack>
  );
}
