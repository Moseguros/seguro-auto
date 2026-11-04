import ChevronLeft from '@mui/icons-material/ChevronLeft';
import ChevronRight from '@mui/icons-material/ChevronRight';
import Close from '@mui/icons-material/Close';
import ZoomIn from '@mui/icons-material/ZoomIn';
import ZoomOut from '@mui/icons-material/ZoomOut';
import Box from '@mui/material/Box';
import Dialog from '@mui/material/Dialog';
import IconButton from '@mui/material/IconButton';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useRef, useState, type KeyboardEvent, type TouchEvent, type WheelEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { ProtectedImage } from '../../files/ProtectedImage';

export interface LightboxPhoto {
  fileId: string;
  label: string;
}

export interface PhotoLightboxProps {
  photos: LightboxPhoto[];
  index: number;
  onIndexChange(index: number): void;
  onClose(): void;
}

const minScale = 1;
const maxScale = 5;
const clamp = (value: number) => Math.min(maxScale, Math.max(minScale, value));

function distance(event: TouchEvent): number {
  const [a, b] = [event.touches[0]!, event.touches[1]!];
  return Math.hypot(a.clientX - b.clientX, a.clientY - b.clientY);
}

/**
 * Fotografia em tamanho real: setas e teclado (← →, Esc), zoom com a roda e com dois dedos
 * (CSS `transform`) e a etiqueta do ângulo.
 */
export function PhotoLightbox({ photos, index, onIndexChange, onClose }: PhotoLightboxProps) {
  const { t } = useTranslation();
  const [scale, setScale] = useState(1);
  const pinch = useRef<{ distance: number; scale: number } | null>(null);
  const photo = photos[index];
  if (!photo) return null;

  const go = (next: number) => {
    if (next < 0 || next >= photos.length) return;
    setScale(1);
    onIndexChange(next);
  };
  const onKeyDown = (event: KeyboardEvent) => {
    if (event.key === 'ArrowLeft') go(index - 1);
    else if (event.key === 'ArrowRight') go(index + 1);
  };
  const onWheel = (event: WheelEvent) => {
    setScale((current) => clamp(current * (event.deltaY < 0 ? 1.15 : 1 / 1.15)));
  };
  const onTouchStart = (event: TouchEvent) => {
    if (event.touches.length === 2) pinch.current = { distance: distance(event), scale };
  };
  const onTouchMove = (event: TouchEvent) => {
    if (event.touches.length === 2 && pinch.current) {
      setScale(clamp((pinch.current.scale * distance(event)) / pinch.current.distance));
    }
  };
  const onTouchEnd = () => {
    pinch.current = null;
  };

  return (
    <Dialog
      open
      fullScreen
      onClose={onClose}
      onKeyDown={onKeyDown}
      aria-labelledby="lightbox-title"
      slotProps={{ paper: { sx: { bgcolor: 'grey.900', color: 'common.white' } } }}
    >
      <Stack sx={{ height: '100%' }}>
        <Stack direction="row" alignItems="center" spacing={1} sx={{ p: 1 }}>
          <Typography id="lightbox-title" sx={{ flexGrow: 1, minWidth: 0 }} noWrap>
            {photo.label}
          </Typography>
          <Typography variant="caption" sx={{ whiteSpace: 'nowrap' }}>
            {t('insurerInspections.lightbox.position', { index: index + 1, total: photos.length })}
          </Typography>
          <IconButton
            color="inherit"
            aria-label={t('insurerInspections.lightbox.zoomOut')}
            onClick={() => setScale((s) => clamp(s / 1.25))}
            disabled={scale <= minScale}
          >
            <ZoomOut />
          </IconButton>
          <IconButton
            color="inherit"
            aria-label={t('insurerInspections.lightbox.zoomIn')}
            onClick={() => setScale((s) => clamp(s * 1.25))}
            disabled={scale >= maxScale}
          >
            <ZoomIn />
          </IconButton>
          <IconButton color="inherit" aria-label={t('common.close')} onClick={onClose}>
            <Close />
          </IconButton>
        </Stack>
        <Box
          sx={{ flexGrow: 1, position: 'relative', overflow: 'hidden', touchAction: 'none' }}
          onWheel={onWheel}
          onTouchStart={onTouchStart}
          onTouchMove={onTouchMove}
          onTouchEnd={onTouchEnd}
        >
          <Box
            data-testid="lightbox-image"
            sx={{
              position: 'absolute',
              inset: 0,
              transform: `scale(${scale})`,
              transition: 'transform 80ms linear',
            }}
          >
            <ProtectedImage
              key={photo.fileId}
              fileId={photo.fileId}
              alt={photo.label}
              fit="contain"
              height="100%"
            />
          </Box>
          <IconButton
            aria-label={t('insurerInspections.lightbox.previous')}
            onClick={() => go(index - 1)}
            disabled={index === 0}
            sx={{
              position: 'absolute',
              left: 8,
              top: '50%',
              transform: 'translateY(-50%)',
              color: 'common.white',
              bgcolor: 'rgba(0,0,0,0.4)',
            }}
          >
            <ChevronLeft />
          </IconButton>
          <IconButton
            aria-label={t('insurerInspections.lightbox.next')}
            onClick={() => go(index + 1)}
            disabled={index >= photos.length - 1}
            sx={{
              position: 'absolute',
              right: 8,
              top: '50%',
              transform: 'translateY(-50%)',
              color: 'common.white',
              bgcolor: 'rgba(0,0,0,0.4)',
            }}
          >
            <ChevronRight />
          </IconButton>
        </Box>
      </Stack>
    </Dialog>
  );
}
