import Chip, { type ChipProps } from '@mui/material/Chip';
import { useTranslation } from 'react-i18next';

const colors: Record<string, ChipProps['color']> = {
  DRAFT: 'info',
  PUBLISHED: 'success',
  SUPERSEDED: 'default',
  RETIRED: 'default',
};

/** Estado de um produto (`productStatus.*`) ou de uma versão (`productVersionStatus.*`). */
export function ProductStatusChip({
  status,
  kind = 'product',
}: {
  status: string;
  kind?: 'product' | 'version';
}) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      variant="outlined"
      color={colors[status] ?? 'default'}
      label={t(kind === 'product' ? `productStatus.${status}` : `productVersionStatus.${status}`)}
      data-status={status}
    />
  );
}
