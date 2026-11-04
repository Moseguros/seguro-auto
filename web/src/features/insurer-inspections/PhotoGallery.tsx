import Place from '@mui/icons-material/Place';
import Box from '@mui/material/Box';
import ButtonBase from '@mui/material/ButtonBase';
import IconButton from '@mui/material/IconButton';
import Stack from '@mui/material/Stack';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  PhotoSlot,
  type InspectionPhotoResponse,
  type InspectionResponse,
} from '../../api/generated';
import { ProtectedImage } from '../../files/ProtectedImage';
import { formatDateTime } from '../../i18n/format';
import { PhotoLightbox } from './PhotoLightbox';

/** Ordem dos ângulos; todos menos `EXTRA` são obrigatórios nas fotografias do cliente (S4-03). */
export const slotOrder = Object.values(PhotoSlot);
export const requiredSlots = slotOrder.filter((slot) => slot !== 'EXTRA');

type Tile =
  { kind: 'photo'; photo: InspectionPhotoResponse } | { kind: 'missing'; slot: PhotoSlot };

/** Miniaturas pela ordem dos ângulos, com quadrados vazios nos obrigatórios em falta. */
export function galleryTiles(inspection: InspectionResponse): Tile[] {
  const photos = inspection.photos ?? [];
  const required = new Set<PhotoSlot>(
    inspection.mode === 'SELF_PHOTOS' ? requiredSlots : (inspection.missingSlots ?? []),
  );
  const tiles: Tile[] = [];
  for (const slot of slotOrder) {
    const ofSlot = photos.filter((photo) => photo.slot === slot);
    if (ofSlot.length) ofSlot.forEach((photo) => tiles.push({ kind: 'photo', photo }));
    else if (required.has(slot)) tiles.push({ kind: 'missing', slot });
  }
  return tiles;
}

const tileHeight = { xs: 120, sm: 150 };

/** Galeria da vistoria (3 colunas em ecrã largo, 2 a 360 px) com o lightbox. */
export function PhotoGallery({ inspection }: { inspection: InspectionResponse }) {
  const { t, i18n } = useTranslation();
  const [open, setOpen] = useState<number | null>(null);
  const tiles = galleryTiles(inspection);
  const photos = tiles.flatMap((tile) => (tile.kind === 'photo' ? [tile.photo] : []));
  const label = (photo: InspectionPhotoResponse) => t(`inspection.slot.${photo.slot}`);

  if (tiles.length === 0) {
    return (
      <Typography color="text.secondary">{t('insurerInspections.gallery.noPhotos')}</Typography>
    );
  }

  return (
    <>
      <Box
        component="ul"
        aria-label={t('insurerProposals.sections.inspection')}
        sx={{
          listStyle: 'none',
          p: 0,
          m: 0,
          display: 'grid',
          gap: 1.5,
          gridTemplateColumns: { xs: 'repeat(2, minmax(0, 1fr))', md: 'repeat(3, minmax(0, 1fr))' },
        }}
      >
        {tiles.map((tile) => {
          if (tile.kind === 'missing') {
            return (
              <Box component="li" key={`missing-${tile.slot}`} data-testid="missing-photo">
                <Box
                  sx={{
                    height: tileHeight,
                    border: 2,
                    borderStyle: 'dashed',
                    borderColor: 'divider',
                    borderRadius: 1,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'text.secondary',
                    textAlign: 'center',
                    p: 1,
                  }}
                >
                  <Typography variant="body2">{t('insurerInspections.gallery.noPhoto')}</Typography>
                </Box>
                <Typography variant="caption" component="p" fontWeight={600}>
                  {t(`inspection.slot.${tile.slot}`)}
                </Typography>
              </Box>
            );
          }
          const photo = tile.photo;
          const index = photos.indexOf(photo);
          const hasLocation = photo.latitude != null && photo.longitude != null;
          return (
            <Box component="li" key={photo.id} sx={{ minWidth: 0 }}>
              <ButtonBase
                onClick={() => setOpen(index)}
                aria-label={t('insurerInspections.gallery.open', { label: label(photo) })}
                sx={{ display: 'block', width: '100%', borderRadius: 1 }}
              >
                <ProtectedImage
                  fileId={photo.fileId}
                  alt={label(photo)}
                  height={150}
                  sx={{ height: tileHeight }}
                />
              </ButtonBase>
              <Stack direction="row" alignItems="center" spacing={0.5} sx={{ minWidth: 0 }}>
                <Stack sx={{ flexGrow: 1, minWidth: 0 }}>
                  <Typography variant="caption" fontWeight={600} noWrap>
                    {label(photo)}
                  </Typography>
                  {photo.takenAt && (
                    <Typography variant="caption" color="text.secondary" noWrap>
                      {formatDateTime(photo.takenAt, i18n.language)}
                    </Typography>
                  )}
                </Stack>
                {hasLocation && (
                  <Tooltip title={t('insurerInspections.gallery.location')}>
                    <IconButton
                      size="small"
                      component="a"
                      href={`https://www.openstreetmap.org/?mlat=${photo.latitude}&mlon=${photo.longitude}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      aria-label={t('insurerInspections.gallery.location')}
                    >
                      <Place fontSize="small" />
                    </IconButton>
                  </Tooltip>
                )}
              </Stack>
            </Box>
          );
        })}
      </Box>
      {open !== null && (
        <PhotoLightbox
          photos={photos.map((photo) => ({ fileId: photo.fileId, label: label(photo) }))}
          index={open}
          onIndexChange={setOpen}
          onClose={() => setOpen(null)}
        />
      )}
    </>
  );
}
