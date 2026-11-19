import InputAdornment from '@mui/material/InputAdornment';
import TextField from '@mui/material/TextField';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../errors/problem';

/** Número M-Pesa (Vodacom): 9 dígitos começados por 84 ou 85 (igual ao backend). */
export const mpesaNational = /^8[45][0-9]{7}$/;
/** Qualquer telemóvel moçambicano: 9 dígitos de 82 a 87 (igual ao `Msisdn` do backend). */
export const mozNational = /^8[2-7][0-9]{7}$/;

/** `+258841234567` → `841234567`; outro formato → `''`. */
export function nationalFromMsisdn(msisdn: string | undefined): string {
  const digits = (msisdn ?? '').replace(/\D/g, '');
  if (digits.length === 12 && digits.startsWith('258')) return digits.slice(3);
  if (digits.length === 9) return digits;
  return '';
}

/** 9 dígitos nacionais → E.164 do contrato (`+258841234567`). */
export function e164(national: string): string {
  return `+258${national}`;
}

/** `REQUIRED`, `MSISDN_NOT_MPESA` ou `null` (válido). */
export function validateMpesaMsisdn(national: string): string | null {
  if (national === '') return 'REQUIRED';
  return mpesaNational.test(national) ? null : 'MSISDN_NOT_MPESA';
}

/** `REQUIRED`, `INVALID_MSISDN` ou `null` (válido). */
export function validateMozMsisdn(national: string): string | null {
  if (national === '') return 'REQUIRED';
  return mozNational.test(national) ? null : 'INVALID_MSISDN';
}

/** Campo de telemóvel com o prefixo fixo `+258` e 9 dígitos (teclado numérico). */
export function PayerMsisdnField({
  label,
  value,
  onChange,
  errorCode,
  onBlur,
  autoFocus,
}: {
  label: string;
  value: string;
  onChange: (national: string) => void;
  /** Código `validation.<code>` a mostrar, ou `null`. */
  errorCode: string | null;
  onBlur?: () => void;
  autoFocus?: boolean;
}) {
  const { t } = useTranslation();
  return (
    <TextField
      label={label}
      value={value}
      onChange={(e) => onChange(e.target.value.replace(/\D/g, '').slice(0, 9))}
      onBlur={onBlur}
      autoFocus={autoFocus}
      fullWidth
      required
      error={errorCode !== null}
      helperText={errorCode ? fieldMessage(t, errorCode) : ' '}
      slotProps={{
        input: {
          startAdornment: <InputAdornment position="start">+258</InputAdornment>,
        },
        htmlInput: {
          inputMode: 'numeric',
          autoComplete: 'tel-national',
          maxLength: 9,
          pattern: '[0-9]*',
        },
      }}
    />
  );
}
