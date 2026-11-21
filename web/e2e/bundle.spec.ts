import { existsSync, readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { expect, test } from '@playwright/test';
import {
  chunkNames,
  staticClosure,
  verificationRoots,
  type Manifest,
} from '../src/build/bundleManifest';

/**
 * S6-05 §1: a verificação pública (`/verificar`, `/v/:token`) e o que ela importa de forma estática
 * (a entrada e o `vendor` incluídos) não trazem a grelha de dados (`mui-x`) nem o leitor de QR
 * (`zxing`, só importado dinamicamente pelo `QrScanner`). Lê o manifesto do `pnpm build`.
 */
const manifestPath = fileURLToPath(new URL('../dist/.vite/manifest.json', import.meta.url));

test('a verificação pública não carrega mui-x nem zxing', () => {
  expect(existsSync(manifestPath), 'corra `pnpm build` antes dos testes e2e').toBe(true);
  const manifest = JSON.parse(readFileSync(manifestPath, 'utf-8')) as Manifest;

  const names = chunkNames(manifest, verificationRoots);
  expect(names).not.toContain('mui-x');
  expect(names).not.toContain('zxing');
  expect(names).toContain('vendor');

  // O leitor existe, num bloco próprio, e só o `QrScanner` o importa.
  const scanner = 'src/features/verification/QrScanner.tsx';
  expect(manifest[scanner]?.isDynamicEntry).toBe(true);
  const scannerNames = [...staticClosure(manifest, [scanner])].map((k) => manifest[k]?.name);
  expect(scannerNames).toContain('zxing');
});
