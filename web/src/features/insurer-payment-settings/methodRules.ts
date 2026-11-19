import { getAt, type Settings } from './schemaPaths';

/**
 * Regras de apresentação de cada forma de pagamento que o JSON Schema não exprime (S5-12 §2),
 * iguais às do backend (`SecretField.applies`/`required` do S5-01).
 */

/** Segredos de cada método, pela ordem da ficha do S5-01. */
export const secretFieldsByMethod: Record<string, string[]> = {
  MPESA: ['apiKey', 'publicKey', 'securityCredential'],
  BANK_REFERENCE: ['sftp.password', 'sftp.privateKey', 'sftp.passphrase'],
};

/** Segredos com várias linhas (chaves em PEM). */
const multilineSecrets = new Set(['publicKey', 'sftp.privateKey']);

export function isMultilineSecret(field: string): boolean {
  return multilineSecrets.has(field);
}

function authType(settings: Settings): unknown {
  return getAt(settings, 'sftp.authType');
}

/** O campo fica escondido (e não se envia) com estes dados. */
export function isHiddenField(method: string, path: string, settings: Settings): boolean {
  if (method !== 'BANK_REFERENCE') return false;
  if (path === 'bmepsCompanyId' || path === 'bmepsProfileId') {
    return getAt(settings, 'fileFormat') !== 'BMEPS';
  }
  if (path === 'sftp.processedDir' || path === 'sftp.errorDir') {
    return getAt(settings, 'sftp.moveAfterProcessing') === false;
  }
  return false;
}

/** O segredo aplica-se com estes dados (senão não aparece nem se envia). */
export function secretApplies(method: string, field: string, settings: Settings): boolean {
  if (method !== 'BANK_REFERENCE') return true;
  const auth = authType(settings);
  if (field === 'sftp.password') return auth !== 'KEY';
  if (field === 'sftp.privateKey' || field === 'sftp.passphrase') return auth !== 'PASSWORD';
  return true;
}

/** O segredo é obrigatório para activar com estes dados. */
export function secretRequired(
  method: string,
  field: string,
  settings: Settings,
  serverRequired: boolean,
): boolean {
  if (method !== 'BANK_REFERENCE') return serverRequired;
  const auth = authType(settings);
  if (field === 'sftp.password') return auth === 'PASSWORD';
  if (field === 'sftp.privateKey') return auth === 'KEY';
  return false;
}

/** Mudou a entidade ou o formato do ficheiro (pede confirmação num método activo)? */
export function referenceKeyChanged(method: string, before: Settings, after: Settings): boolean {
  if (method !== 'BANK_REFERENCE') return false;
  return (
    getAt(before, 'entity') !== getAt(after, 'entity') ||
    getAt(before, 'fileFormat') !== getAt(after, 'fileFormat')
  );
}
