import InputAdornment from '@mui/material/InputAdornment';
import TextField, { type TextFieldProps } from '@mui/material/TextField';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { Money } from '../api/generated';
import { formatAmount, mzn, parseMeticais } from '../format/money';

export interface MoneyFieldProps {
  label: string;
  value: Money | null;
  /** Só com um valor válido (ou `null` com o campo vazio). */
  onChange(value: Money | null): void;
  /** O texto escrito deixou (ou voltou a ser) válido. */
  onValidityChange?(valid: boolean): void;
  error?: boolean;
  helperText?: string;
  disabled?: boolean;
  required?: boolean;
  fullWidth?: boolean;
  size?: TextFieldProps['size'];
  name?: string;
}

function textOf(value: Money | null): string {
  return value ? formatAmount(value) : '';
}

/** Campo de dinheiro em meticais (sufixo "MT"); guarda centavos. */
export function MoneyField({
  label,
  value,
  onChange,
  onValidityChange,
  error,
  helperText,
  disabled,
  required,
  fullWidth = true,
  size,
  name,
}: MoneyFieldProps) {
  const { t, i18n } = useTranslation();
  const [text, setText] = useState(() => textOf(value));
  const [invalid, setInvalid] = useState(false);
  const textRef = useRef(text);
  textRef.current = text;

  // Valor mudado por fora (ou outra língua): mostra-o, salvo se já é o que está escrito.
  useEffect(() => {
    const parsed = parseMeticais(textRef.current);
    const current = parsed === 'invalid' ? undefined : parsed;
    if (current === (value?.amount ?? null)) return;
    if (parsed === 'invalid' && value === null) return;
    setText(textOf(value));
    setInvalid(false);
  }, [value]);

  useEffect(() => {
    const parsed = parseMeticais(textRef.current);
    if (parsed !== 'invalid' && parsed !== null) setText(formatAmount({ amount: parsed }));
  }, [i18n.language]);

  const change = (next: string) => {
    setText(next);
    const parsed = parseMeticais(next);
    const nowInvalid = parsed === 'invalid';
    if (nowInvalid !== invalid) onValidityChange?.(!nowInvalid);
    setInvalid(nowInvalid);
    if (nowInvalid) return;
    if ((parsed ?? null) !== (value?.amount ?? null))
      onChange(parsed === null ? null : mzn(parsed));
  };

  const blur = () => {
    const parsed = parseMeticais(text);
    if (parsed !== 'invalid' && parsed !== null) setText(formatAmount({ amount: parsed }));
  };

  return (
    <TextField
      label={label}
      name={name}
      value={text}
      onChange={(event) => change(event.target.value)}
      onBlur={blur}
      error={invalid || error}
      helperText={invalid ? t('validation.INVALID_MONEY') : helperText}
      disabled={disabled}
      required={required}
      fullWidth={fullWidth}
      size={size}
      autoComplete="off"
      slotProps={{
        htmlInput: { inputMode: 'decimal' },
        input: { endAdornment: <InputAdornment position="end">MT</InputAdornment> },
      }}
    />
  );
}
