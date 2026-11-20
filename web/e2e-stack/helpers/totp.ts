import { Secret, TOTP } from 'otpauth';

const PERIOD_SECONDS = 30;

function currentCounter(): number {
  return Math.floor(Date.now() / 1000 / PERIOD_SECONDS);
}

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Códigos TOTP do staff (S1-05: SHA-1, 6 dígitos, 30 s), calculados a partir do `totpSecret` (Base32) que
 * o `acceptInvitation` devolve (`TotpSetupResponse`). O backend recusa repetir um passo já usado, por isso
 * `nextCode` espera pelo passo seguinte ao último usado.
 */
export class StaffTotp {
  private readonly totp: TOTP;
  private lastCounter = -1;

  constructor(secret: string) {
    this.totp = new TOTP({
      algorithm: 'SHA1',
      digits: 6,
      period: PERIOD_SECONDS,
      secret: Secret.fromBase32(secret.replace(/\s+/g, '')),
    });
  }

  /** Código de um passo ainda não usado (espera até 30 s, se for preciso). */
  async nextCode(): Promise<string> {
    let counter = currentCounter();
    while (counter <= this.lastCounter) {
      await sleep(1_000);
      counter = currentCounter();
    }
    // Com menos de 3 s do passo, espera pelo seguinte (o pedido pode chegar já fora dele).
    const periodMs = PERIOD_SECONDS * 1000;
    const remaining = periodMs - (Date.now() % periodMs);
    if (remaining < 3_000) {
      await sleep(remaining + 500);
      counter = currentCounter();
    }
    this.lastCounter = counter;
    return this.totp.generate({ timestamp: counter * periodMs });
  }
}
