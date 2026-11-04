import BrokenImage from '@mui/icons-material/BrokenImage';
import Box from '@mui/material/Box';
import Skeleton from '@mui/material/Skeleton';
import Typography from '@mui/material/Typography';
import type { SxProps, Theme } from '@mui/material/styles';
import { useTranslation } from 'react-i18next';
import { useFileBlob } from './useFileBlob';

export interface ProtectedImageProps {
  fileId: string;
  /** Texto alternativo (obrigatório), ex.: `t('inspection.slot.FRONT')`. */
  alt: string;
  sx?: SxProps<Theme>;
  /** Altura do quadro enquanto carrega ou com erro. */
  height?: number | string;
  /** `contain` para ver a imagem inteira (lightbox), `cover` para miniaturas. */
  fit?: 'cover' | 'contain';
}

/** Imagem lida pela API com o token da sessão (nunca na URL). */
export function ProtectedImage({
  fileId,
  alt,
  sx,
  height = 160,
  fit = 'cover',
}: ProtectedImageProps) {
  const { t } = useTranslation();
  const { url, isLoading, error } = useFileBlob(fileId);

  if (error) {
    return (
      <Box
        role="img"
        aria-label={alt}
        sx={{
          height,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          gap: 1,
          bgcolor: 'action.hover',
          color: 'text.secondary',
          borderRadius: 1,
          p: 1,
          textAlign: 'center',
          ...sx,
        }}
      >
        <BrokenImage />
        <Typography variant="caption">{t('files.loadError')}</Typography>
      </Box>
    );
  }
  if (isLoading || !url) {
    return (
      <Skeleton
        variant="rectangular"
        data-testid="protected-image-loading"
        sx={{ height, width: '100%', borderRadius: 1, ...sx }}
      />
    );
  }
  return (
    <Box
      component="img"
      src={url}
      alt={alt}
      sx={{
        display: 'block',
        width: '100%',
        height,
        objectFit: fit,
        borderRadius: 1,
        ...sx,
      }}
    />
  );
}
