import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { VehicleResponse } from '../../api/generated';

/** Cartão de um veículo na lista; o clique abre o detalhe. */
export function VehicleCard({ vehicle }: { vehicle: VehicleResponse }) {
  const { t } = useTranslation();
  const habitual = vehicle.drivers.find((driver) => driver.habitual);
  return (
    <Card data-testid="vehicle-card" sx={{ height: '100%' }}>
      <CardActionArea
        component={RouterLink}
        to={`/cliente/veiculos/${vehicle.id}`}
        sx={{ height: '100%', alignItems: 'flex-start' }}
      >
        <CardContent>
          <Stack spacing={1}>
            <Typography
              variant="h5"
              component="h2"
              sx={{ fontVariantNumeric: 'tabular-nums', fontWeight: 700 }}
            >
              {vehicle.plate}
            </Typography>
            <Typography sx={{ wordBreak: 'break-word' }}>
              {t('vehicles.card.summary', {
                make: vehicle.make,
                model: vehicle.model,
                year: vehicle.year,
              })}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t(`vehicleCategory.${vehicle.category}`)} · {t(`vehicleUsage.${vehicle.usage}`)}
            </Typography>
            <Typography variant="body2">
              {habitual
                ? t('vehicles.card.habitualDriver', { name: habitual.fullName })
                : t('vehicles.card.noHabitualDriver')}
            </Typography>
            {(!vehicle.registrationDoc || vehicle.inatroVerified) && (
              <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                {!vehicle.registrationDoc && (
                  <Chip size="small" color="warning" label={t('vehicles.card.missingDoc')} />
                )}
                {vehicle.inatroVerified && (
                  <Chip
                    size="small"
                    color="success"
                    variant="outlined"
                    label={t('vehicles.verified')}
                  />
                )}
              </Stack>
            )}
          </Stack>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}
