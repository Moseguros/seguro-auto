import Add from '@mui/icons-material/Add';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Divider from '@mui/material/Divider';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../errors/problem';
import { customFactorCodeError, factorPresets } from './factorPresets';

export interface NewFactor {
  code: string;
  label: string;
}

export interface FactorMenuProps {
  /** Códigos dos factores que a cobertura já tem (os presets iguais ficam desactivados). */
  existingCodes: readonly string[];
  disabled?: boolean;
  onAdd(factor: NewFactor): void;
}

/** Botão "Acrescentar factor": factores pré-definidos ou um personalizado. */
export function FactorMenu({ existingCodes, disabled, onAdd }: FactorMenuProps) {
  const { t } = useTranslation();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  const [custom, setCustom] = useState(false);
  const [code, setCode] = useState('');
  const [label, setLabel] = useState('');
  const [errors, setErrors] = useState<{ code?: string; label?: string }>({});

  const openCustom = () => {
    setAnchor(null);
    setCode('');
    setLabel('');
    setErrors({});
    setCustom(true);
  };

  const addCustom = () => {
    const trimmedCode = code.trim();
    const trimmedLabel = label.trim();
    const found: { code?: string; label?: string } = {};
    const codeError = customFactorCodeError(trimmedCode, existingCodes);
    if (codeError) found.code = codeError;
    if (trimmedLabel === '') found.label = 'REQUIRED';
    else if (trimmedLabel.length > 80) found.label = 'TOO_LONG';
    setErrors(found);
    if (found.code || found.label) return;
    setCustom(false);
    onAdd({ code: trimmedCode, label: trimmedLabel });
  };

  return (
    <>
      <Button
        size="small"
        variant="outlined"
        startIcon={<Add />}
        disabled={disabled}
        aria-haspopup="menu"
        onClick={(event) => setAnchor(event.currentTarget)}
      >
        {t('tariffs.factorMenu.add')}
      </Button>
      <Menu open={!!anchor} anchorEl={anchor} onClose={() => setAnchor(null)}>
        {factorPresets.map((preset) => (
          <MenuItem
            key={preset.code}
            disabled={existingCodes.includes(preset.code)}
            onClick={() => {
              setAnchor(null);
              onAdd({ code: preset.code, label: t(`factorPreset.${preset.code}`) });
            }}
          >
            {t(`factorPreset.${preset.code}`)}
          </MenuItem>
        ))}
        <Divider />
        <MenuItem onClick={openCustom}>{t('tariffs.factorMenu.custom')}</MenuItem>
      </Menu>
      <Dialog
        open={custom}
        onClose={() => setCustom(false)}
        aria-labelledby="custom-factor-title"
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle id="custom-factor-title">{t('tariffs.factorMenu.custom')}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField
              label={t('tariffs.factorMenu.code')}
              value={code}
              onChange={(event) => setCode(event.target.value.toUpperCase())}
              error={!!errors.code}
              helperText={errors.code ? fieldMessage(t, errors.code) : undefined}
              slotProps={{ htmlInput: { maxLength: 30 } }}
              fullWidth
            />
            <TextField
              label={t('tariffs.factorMenu.label')}
              value={label}
              onChange={(event) => setLabel(event.target.value)}
              error={!!errors.label}
              helperText={errors.label ? fieldMessage(t, errors.label) : undefined}
              fullWidth
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCustom(false)}>{t('tariffs.factorMenu.cancel')}</Button>
          <Button variant="contained" onClick={addCustom}>
            {t('tariffs.factorMenu.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
