import MoreVert from '@mui/icons-material/MoreVert';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';

export interface UserActionsMenuProps<A extends string> {
  /** Nome da linha (para o rótulo acessível). */
  name: string;
  actions: A[];
  onAction(action: A): void;
  disabled?: boolean;
}

/** Menu "⋮" de acções de uma linha; sem acções não mostra nada. */
export function UserActionsMenu<A extends string>({
  name,
  actions,
  onAction,
  disabled,
}: UserActionsMenuProps<A>) {
  const { t } = useTranslation();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  if (actions.length === 0) return null;
  return (
    <>
      <IconButton
        aria-label={t('users.actions.open', { name })}
        aria-haspopup="menu"
        disabled={disabled}
        onClick={(event) => setAnchor(event.currentTarget)}
      >
        <MoreVert />
      </IconButton>
      <Menu anchorEl={anchor} open={anchor !== null} onClose={() => setAnchor(null)}>
        {actions.map((action) => (
          <MenuItem
            key={action}
            onClick={() => {
              setAnchor(null);
              onAction(action);
            }}
          >
            {t(`users.actions.${action}`)}
          </MenuItem>
        ))}
      </Menu>
    </>
  );
}
