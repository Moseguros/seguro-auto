import Box from '@mui/material/Box';
import { useRef, useState, type ClipboardEvent, type KeyboardEvent } from 'react';
import { useTranslation } from 'react-i18next';

export interface OtpCodeInputProps {
  length: number;
  /** Chamado uma vez quando todas as caixas ficam preenchidas. */
  onComplete(code: string): void;
  /** Chamado a cada alteração, com o código parcial. */
  onChange?(code: string): void;
  disabled?: boolean;
  error?: boolean;
  autoFocus?: boolean;
}

/**
 * Caixas do código (uma por dígito). Colar ou o preenchimento automático do sistema
 * (`autocomplete="one-time-code"`) preenchem todas. Para limpar, o pai muda a `key`.
 */
export function OtpCodeInput({
  length,
  onComplete,
  onChange,
  disabled,
  error,
  autoFocus,
}: OtpCodeInputProps) {
  const { t } = useTranslation();
  const [digits, setDigits] = useState<string[]>(() => Array.from({ length }, () => ''));
  const refs = useRef<(HTMLInputElement | null)[]>([]);

  function focus(index: number) {
    const target = refs.current[Math.max(0, Math.min(length - 1, index))];
    target?.focus();
    target?.select();
  }

  function update(next: string[], focusIndex: number) {
    setDigits(next);
    const code = next.join('');
    onChange?.(code);
    focus(focusIndex);
    if (next.every((d) => d !== '')) onComplete(code);
  }

  /** Preenche a partir de `start` com vários dígitos (colar, preenchimento automático). */
  function fill(start: number, value: string) {
    const incoming = value.replace(/\D/g, '');
    if (!incoming) return;
    // Um código inteiro substitui tudo, venha de que caixa vier.
    const from = incoming.length >= length ? 0 : start;
    const next = [...digits];
    let last = from;
    for (let i = 0; i < incoming.length && from + i < length; i++) {
      next[from + i] = incoming.charAt(i);
      last = from + i;
    }
    update(next, last + 1);
  }

  function handleChange(index: number, raw: string) {
    const value = raw.replace(/\D/g, '');
    if (value.length > 1) {
      fill(index, value);
      return;
    }
    const next = [...digits];
    next[index] = value;
    update(next, value ? index + 1 : index);
  }

  function handleKeyDown(index: number, event: KeyboardEvent<HTMLInputElement>) {
    if (event.key === 'Backspace' && !digits[index] && index > 0) {
      event.preventDefault();
      const next = [...digits];
      next[index - 1] = '';
      update(next, index - 1);
    } else if (event.key === 'ArrowLeft') {
      focus(index - 1);
    } else if (event.key === 'ArrowRight') {
      focus(index + 1);
    }
  }

  function handlePaste(index: number, event: ClipboardEvent<HTMLInputElement>) {
    event.preventDefault();
    fill(index, event.clipboardData.getData('text'));
  }

  return (
    <Box
      role="group"
      aria-label={t('auth.code.inputLabel')}
      sx={{ display: 'flex', gap: { xs: 0.75, sm: 1 }, justifyContent: 'center' }}
    >
      {digits.map((digit, index) => (
        <Box
          key={index}
          component="input"
          ref={(element: HTMLInputElement | null) => {
            refs.current[index] = element;
          }}
          value={digit}
          onChange={(event) => handleChange(index, event.target.value)}
          onKeyDown={(event) => handleKeyDown(index, event)}
          onPaste={(event) => handlePaste(index, event)}
          onFocus={(event) => event.target.select()}
          disabled={disabled}
          autoFocus={autoFocus && index === 0}
          autoComplete={index === 0 ? 'one-time-code' : 'off'}
          inputMode="numeric"
          pattern="[0-9]*"
          aria-label={t('auth.code.digitLabel', { position: index + 1, total: length })}
          aria-invalid={error || undefined}
          sx={{
            width: { xs: 40, sm: 48 },
            height: { xs: 48, sm: 56 },
            minWidth: 0,
            flex: '0 1 auto',
            textAlign: 'center',
            fontSize: 22,
            fontWeight: 600,
            fontFamily: 'inherit',
            color: 'text.primary',
            bgcolor: 'background.paper',
            border: 1,
            borderColor: error ? 'error.main' : 'divider',
            borderRadius: 1,
            outline: 'none',
            '&:focus': { borderColor: 'primary.main', borderWidth: 2 },
            '&:disabled': { opacity: 0.6 },
          }}
        />
      ))}
    </Box>
  );
}
