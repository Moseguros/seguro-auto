/** Entrada de `dist/.vite/manifest.json` (com `build.manifest: true`). */
export interface ManifestChunk {
  file: string;
  name?: string;
  src?: string;
  isEntry?: boolean;
  isDynamicEntry?: boolean;
  imports?: string[];
  dynamicImports?: string[];
}

export type Manifest = Record<string, ManifestChunk>;

/** Chave do bloco: a própria ou a do bloco com esse `src`. */
function resolveKey(manifest: Manifest, key: string): string {
  if (manifest[key]) return key;
  const found = Object.entries(manifest).find(([, chunk]) => chunk.src === key);
  if (!found) throw new Error(`Bloco inexistente no manifesto: ${key}`);
  return found[0];
}

/** As entradas dadas e tudo o que elas importam de forma estática (as chaves do manifesto). */
export function staticClosure(manifest: Manifest, roots: string[]): Set<string> {
  const seen = new Set<string>();
  const stack = [...roots];
  while (stack.length > 0) {
    const key = resolveKey(manifest, stack.pop() as string);
    if (seen.has(key)) continue;
    const chunk = manifest[key] as ManifestChunk;
    seen.add(key);
    stack.push(...(chunk.imports ?? []));
  }
  return seen;
}

/** Nomes dos blocos (`name`) carregados pelas entradas dadas. */
export function chunkNames(manifest: Manifest, roots: string[]): string[] {
  return [...staticClosure(manifest, roots)].map((key) => manifest[key]?.name ?? key);
}

/** Blocos da verificação pública (S6-05 §1): a entrada e as duas páginas de `/verificar`. */
export const verificationRoots = [
  'index.html',
  'src/features/verification/VerifyPage.tsx',
  'src/features/verification/TokenResultPage.tsx',
];
