import MoreVert from '@mui/icons-material/MoreVert';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';

/** Menu de acções de uma linha: "Editar" e "Activar"/"Desactivar". */
export function RowActions({
  name,
  active,
  onEdit,
  onToggle,
}: {
  name: string;
  active: boolean;
  onEdit(): void;
  onToggle(): void;
}) {
  const { t } = useTranslation();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  const run = (action: () => void) => {
    setAnchor(null);
    action();
  };
  return (
    <>
      <IconButton
        size="small"
        aria-label={t('references.actionsFor', { name })}
        aria-haspopup="menu"
        onClick={(event) => {
          event.stopPropagation();
          setAnchor(event.currentTarget);
        }}
      >
        <MoreVert fontSize="small" />
      </IconButton>
      <Menu anchorEl={anchor} open={!!anchor} onClose={() => setAnchor(null)}>
        <MenuItem onClick={() => run(onEdit)}>{t('references.edit')}</MenuItem>
        <MenuItem onClick={() => run(onToggle)}>
          {active ? t('references.deactivate') : t('references.activate')}
        </MenuItem>
      </Menu>
    </>
  );
}
