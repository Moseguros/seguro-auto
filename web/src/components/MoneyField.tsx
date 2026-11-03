import InputAdornment from '@mui/material/InputAdornment';
import TextField, { type TextFieldProps } from '@mui/material/TextField';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { Money } from '../api/generated';
import { formatMoney, parseMeticais } from '../format/money';

export interface MoneyFieldProps extends Omit<
  TextFieldProps,
  'value' | 'onChange' | 'error' | 'helperText' | 'type'
> {
  value: Money | null;
  onChange(value: Money | null): void;
  error?: boolean;
  helperText?: string;
  /** Chamado quando o texto passa a ser (ou deixa de ser) um valor inválido. */
  onInvalidChange?(invalid: boolean): void;
}

function textOf(value: Money | null): string {
  return value ? formatMoney(value).replace(/\s*MT$/, '') : '';
}

/**
 * Valor em meticais: aceita `3 000 000`, `3.000.000,50` ou `3000000,5` e devolve centavos
 * (`Money`). Ao perder o foco mostra o valor formatado.
 */
export function MoneyField({
  value,
  onChange,
  error,
  helperText,
  onInvalidChange,
  onBlur,
  onFocus,
  slotProps,
  ...props
}: MoneyFieldProps) {
  const { t, i18n } = useTranslation();
  const [text, setText] = useState(() => textOf(value));
  const [invalid, setInvalid] = useState(false);
  const focused = useRef(false);

  // Valor alterado fora do campo (ou língua mudada): mostra-o formatado.
  useEffect(() => {
    if (!focused.current) {
      setText(textOf(value));
      setInvalid(false);
    }
  }, [value?.amount, i18n.language]); // eslint-disable-line react-hooks/exhaustive-deps

  const update = (next: string) => {
    setText(next);
    const parsed = parseMeticais(next);
    const nowInvalid = parsed === 'invalid';
    if (nowInvalid !== invalid) {
      setInvalid(nowInvalid);
      onInvalidChange?.(nowInvalid);
    }
    if (parsed !== 'invalid')
      onChange(parsed === null ? null : { amount: parsed, currency: 'MZN' });
  };

  return (
    <TextField
      {...props}
      value={text}
      onChange={(event) => update(event.target.value)}
      onFocus={(event) => {
        focused.current = true;
        onFocus?.(event);
      }}
      onBlur={(event) => {
        focused.current = false;
        const parsed = parseMeticais(text);
        if (parsed !== 'invalid')
          setText(textOf(parsed === null ? null : { amount: parsed, currency: 'MZN' }));
        onBlur?.(event);
      }}
      error={invalid || error}
      helperText={invalid ? t('validation.INVALID_MONEY') : helperText}
      slotProps={{
        ...slotProps,
        htmlInput: { inputMode: 'decimal', ...(slotProps?.htmlInput as object) },
        input: {
          endAdornment: <InputAdornment position="end">MT</InputAdornment>,
          ...(slotProps?.input as object),
        },
      }}
    />
  );
}
