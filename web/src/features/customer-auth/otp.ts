import type { OtpRequestResponse } from '../../api/generated';

export type IdentifierType = 'MSISDN' | 'EMAIL';
export type OtpChannel = 'SMS' | 'WHATSAPP' | 'EMAIL';

/** Pedido de código: o login (`requestOtp`) e os contactos (`startContactVerification`). */
export interface OtpRequest {
  identifierType: IdentifierType;
  /** Telemóvel em E.164 (`+258…`) ou e-mail. */
  identifier: string;
  /** Só quando o cliente pode escolher o canal. */
  channel?: OtpChannel;
}

export type RequestCode = (request: OtpRequest) => Promise<OtpRequestResponse>;
export type VerifyCode = (challengeId: string, code: string) => Promise<void>;

/** Código enviado: o pedido, o canal pedido (escolhido ou por defeito) e a resposta. */
export interface SentCode {
  request: OtpRequest;
  requestedChannel: OtpChannel | undefined;
  response: OtpRequestResponse;
}

export const msisdnPrefix = '+258';
export const msisdnDigitsPattern = /^8[2-7]\d{7}$/;
export const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/** Destino mascarado no browser: `+258*****4567` ou `a***@exemplo.co.mz`. */
export function maskIdentifier(type: IdentifierType, identifier: string): string {
  if (type === 'MSISDN') {
    const digits = identifier.replace(/\D/g, '');
    const local = digits.startsWith('258') ? digits.slice(3) : digits;
    return `${msisdnPrefix}${'*'.repeat(Math.max(0, local.length - 4))}${local.slice(-4)}`;
  }
  const at = identifier.lastIndexOf('@');
  if (at <= 0) return identifier;
  return `${identifier.charAt(0)}***${identifier.slice(at)}`;
}
