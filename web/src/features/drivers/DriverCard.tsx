import MoreVert from '@mui/icons-material/MoreVert';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import type { DriverResponse, PublicReferenceValueResponse } from '../../api/generated';
import { useReferenceLabel } from '../vehicles/vehicleQueries';

export interface DriverCardProps {
  driver: DriverResponse;
  /** Valores de `LICENCE_CATEGORY` (para os rótulos). */
  categories: PublicReferenceValueResponse[];
  onRemove(driver: DriverResponse): void;
}

/** Cartão de um condutor: nome, "Eu", categorias, carta mascarada e veículos; menu de acções. */
export function DriverCard({ driver, categories, onRemove }: DriverCardProps) {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const label = useReferenceLabel();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  const categoryLabels = driver.licenceCategories.map((code) => {
    const value = categories.find((category) => category.code === code);
    return value ? label(value) : code;
  });
  const menuId = `driver-menu-${driver.id}`;

  return (
    <Card data-testid="driver-card" sx={{ height: '100%' }}>
      <CardContent>
        <Stack direction="row" spacing={1} alignItems="flex-start">
          <Stack spacing={1} sx={{ minWidth: 0, flexGrow: 1 }}>
            <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
              <Typography variant="h6" component="h2" sx={{ wordBreak: 'break-word' }}>
                {driver.fullName}
              </Typography>
              {driver.isOwner && <Chip size="small" color="primary" label={t('drivers.me')} />}
            </Stack>
            <Typography variant="body2">
              {t('drivers.card.categories', { list: categoryLabels.join(', ') })}
            </Typography>
            <Typography variant="body2" sx={{ fontVariantNumeric: 'tabular-nums' }}>
              {t('drivers.card.licence', { masked: driver.licenceNumberMasked })}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t('drivers.card.vehicles', { count: driver.vehicleIds.length })}
            </Typography>
          </Stack>
          <IconButton
            aria-label={t('drivers.actions', { name: driver.fullName })}
            aria-haspopup="menu"
            aria-controls={anchor ? menuId : undefined}
            aria-expanded={anchor ? true : undefined}
            onClick={(event) => setAnchor(event.currentTarget)}
          >
            <MoreVert />
          </IconButton>
        </Stack>
      </CardContent>
      <Menu id={menuId} anchorEl={anchor} open={anchor !== null} onClose={() => setAnchor(null)}>
        <MenuItem
          onClick={() => {
            setAnchor(null);
            navigate(`/cliente/condutores/${driver.id}/editar`);
          }}
        >
          {t('drivers.edit')}
        </MenuItem>
        <MenuItem
          sx={{ color: 'error.main' }}
          onClick={() => {
            setAnchor(null);
            onRemove(driver);
          }}
        >
          {t('drivers.remove')}
        </MenuItem>
      </Menu>
    </Card>
  );
}
