import { expect, type APIRequestContext } from '@playwright/test';

/** Mailpit do compose (API v1: https://mailpit.axllent.org/docs/api-v1/). */
export const MAILPIT_URL = (process.env.MAILPIT_URL ?? 'http://localhost:8025').replace(/\/$/, '');

interface MailSummary {
  ID: string;
  Subject: string;
  To: { Address: string }[];
  Created: string;
}

interface MailMessage {
  ID: string;
  Subject: string;
  Text: string;
  HTML: string;
}

async function list(request: APIRequestContext): Promise<MailSummary[]> {
  const response = await request.get(`${MAILPIT_URL}/api/v1/messages?limit=200`);
  expect(response.ok(), 'Mailpit responde').toBeTruthy();
  const body = (await response.json()) as { messages: MailSummary[] };
  return body.messages;
}

/**
 * Espera por um e-mail para `to` cujo assunto satisfaz `subject` e devolve-o completo (o mais recente).
 * Os e-mails saem depois do commit, por isso a espera é por sondagem.
 */
export async function waitForMail(
  request: APIRequestContext,
  to: string,
  subject: RegExp,
  timeoutMs = 90_000,
): Promise<MailMessage> {
  let found: MailSummary | undefined;
  await expect
    .poll(
      async () => {
        const messages = await list(request);
        found = messages.find(
          (m) =>
            subject.test(m.Subject) &&
            m.To.some((t) => t.Address.toLowerCase() === to.toLowerCase()),
        );
        return found !== undefined;
      },
      { timeout: timeoutMs, intervals: [1_000, 2_000, 5_000], message: `e-mail para ${to}` },
    )
    .toBe(true);
  const response = await request.get(`${MAILPIT_URL}/api/v1/message/${found!.ID}`);
  expect(response.ok()).toBeTruthy();
  return (await response.json()) as MailMessage;
}

/** Token do convite no link `…/convite/{token}` de um e-mail. */
export function invitationToken(message: MailMessage): string {
  const match = /\/convite\/([A-Za-z0-9_-]+)/.exec(`${message.Text}\n${message.HTML}`);
  expect(match, 'link do convite no e-mail').not.toBeNull();
  return match![1]!;
}
