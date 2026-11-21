/** Código de verificação do certificado (S6-01): 22 caracteres Base64url. */
export const verificationTokenPattern = /^[A-Za-z0-9_-]{22}$/;

/**
 * Código de verificação de um URL lido no QR do certificado: `http(s)://{qualquer domínio}/v/{código}`
 * (o caminho termina no código). Outro texto → `null`.
 */
export function parseVerificationUrl(text: string): string | null {
  let url: URL;
  try {
    url = new URL(text.trim());
  } catch {
    return null;
  }
  if (url.protocol !== 'https:' && url.protocol !== 'http:') return null;
  const match = /\/v\/([^/]+)$/.exec(url.pathname);
  const token = match?.[1];
  return token && verificationTokenPattern.test(token) ? token : null;
}
