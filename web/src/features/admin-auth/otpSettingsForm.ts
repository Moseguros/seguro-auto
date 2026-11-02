import type { OtpSettingsResponse, UpdateOtpSettingsRequest } from '../../api/generated';

export const otpChannels = ['SMS', 'WHATSAPP', 'EMAIL'] as const;
export type OtpChannel = (typeof otpChannels)[number];

export type NumberField =
  'codeLength' | 'validityMinutes' | 'maxAttempts' | 'resendAfterSeconds' | 'maxRequestsPerHour';

/** Limites dos parâmetros (iguais aos do backend; a validade vê-se em minutos). */
export const ranges: Record<NumberField, [number, number]> = {
  codeLength: [4, 8],
  validityMinutes: [1, 30],
  maxAttempts: [1, 10],
  resendAfterSeconds: [15, 600],
  maxRequestsPerHour: [1, 50],
};

export const numberFields = Object.keys(ranges) as NumberField[];

export interface OtpSettingsForm {
  enabledChannels: OtpChannel[];
  defaultChannel: OtpChannel | '';
  customerCanChoose: boolean;
  fallbackOrder: OtpChannel[];
  codeLength: string;
  validityMinutes: string;
  maxAttempts: string;
  resendAfterSeconds: string;
  maxRequestsPerHour: string;
}

export type FormField = keyof OtpSettingsForm;
export type FormErrors = Partial<Record<FormField, string>>;

export function formFrom(settings: OtpSettingsResponse): OtpSettingsForm {
  return {
    enabledChannels: [...settings.enabledChannels],
    defaultChannel: settings.defaultChannel,
    customerCanChoose: settings.customerCanChoose,
    fallbackOrder: [...settings.fallbackOrder],
    codeLength: String(settings.codeLength),
    validityMinutes: String(Math.round(settings.validitySeconds / 60)),
    maxAttempts: String(settings.maxAttempts),
    resendAfterSeconds: String(settings.resendAfterSeconds),
    maxRequestsPerHour: String(settings.maxRequestsPerHour),
  };
}

/**
 * Liga ou desliga um canal: ligar acrescenta-o no fim da ordem de alternativa, desligar
 * retira-o. Se o canal por defeito deixar de estar activo, passa a ser o primeiro da ordem.
 */
export function toggleChannel(
  form: OtpSettingsForm,
  channel: OtpChannel,
  enabled: boolean,
): OtpSettingsForm {
  const isOn = form.enabledChannels.includes(channel);
  if (enabled === isOn) return form;
  if (enabled) {
    return {
      ...form,
      enabledChannels: otpChannels.filter((c) => c === channel || form.enabledChannels.includes(c)),
      fallbackOrder: [...form.fallbackOrder.filter((c) => c !== channel), channel],
      defaultChannel: form.defaultChannel || channel,
    };
  }
  const fallbackOrder = form.fallbackOrder.filter((c) => c !== channel);
  return {
    ...form,
    enabledChannels: form.enabledChannels.filter((c) => c !== channel),
    fallbackOrder,
    defaultChannel:
      form.defaultChannel === channel ? (fallbackOrder[0] ?? '') : form.defaultChannel,
  };
}

/** Move um canal da posição `from` para `to` na ordem de alternativa. */
export function moveChannel(order: OtpChannel[], from: number, to: number): OtpChannel[] {
  if (from === to || from < 0 || to < 0 || from >= order.length || to >= order.length) {
    return order;
  }
  const next = [...order];
  const [item] = next.splice(from, 1);
  if (item === undefined) return order;
  next.splice(to, 0, item);
  return next;
}

function parseInteger(value: string): number | null {
  return /^\d+$/.test(value.trim()) ? Number(value.trim()) : null;
}

/** Erros do formulário (códigos `validation.*`). */
export function validate(form: OtpSettingsForm): FormErrors {
  const errors: FormErrors = {};
  if (form.enabledChannels.length === 0) errors.enabledChannels = 'CHANNELS_EMPTY';
  else if (!form.defaultChannel || !form.enabledChannels.includes(form.defaultChannel)) {
    errors.defaultChannel = 'DEFAULT_CHANNEL_NOT_ENABLED';
  }
  for (const field of numberFields) {
    const value = form[field];
    const [min, max] = ranges[field];
    if (!value.trim()) {
      errors[field] = 'REQUIRED';
      continue;
    }
    const number = parseInteger(value);
    if (number === null || number < min || number > max) errors[field] = 'OUT_OF_RANGE';
  }
  return errors;
}

export function toRequest(form: OtpSettingsForm, version: number): UpdateOtpSettingsRequest {
  return {
    enabledChannels: form.enabledChannels,
    defaultChannel: form.defaultChannel as OtpChannel,
    customerCanChoose: form.customerCanChoose,
    fallbackOrder: form.fallbackOrder,
    codeLength: Number(form.codeLength),
    validitySeconds: Number(form.validityMinutes) * 60,
    maxAttempts: Number(form.maxAttempts),
    resendAfterSeconds: Number(form.resendAfterSeconds),
    maxRequestsPerHour: Number(form.maxRequestsPerHour),
    version,
  };
}

/** Campo do formulário de um erro do servidor (`errors[].field`). */
export function formFieldOf(serverField: string): FormField | null {
  if (serverField === 'validitySeconds') return 'validityMinutes';
  const fields: FormField[] = [
    'enabledChannels',
    'defaultChannel',
    'customerCanChoose',
    'fallbackOrder',
    ...numberFields,
  ];
  return (fields as string[]).includes(serverField) ? (serverField as FormField) : null;
}
