import { describe, expect, it } from 'vitest';
import { chunkNames, staticClosure, verificationRoots, type Manifest } from './bundleManifest';

const manifest: Manifest = {
  'index.html': { file: 'assets/index.js', isEntry: true, imports: ['_react.js', '_mui.js'] },
  '_react.js': { file: 'assets/react.js', name: 'react' },
  '_mui.js': { file: 'assets/mui.js', name: 'mui', imports: ['_react.js'] },
  '_mui-x.js': { file: 'assets/mui-x.js', name: 'mui-x' },
  '_zxing.js': { file: 'assets/zxing.js', name: 'zxing' },
  'src/features/verification/VerifyPage.tsx': {
    file: 'assets/VerifyPage.js',
    name: 'VerifyPage',
    isDynamicEntry: true,
    imports: ['index.html'],
    dynamicImports: ['src/features/verification/QrScanner.tsx'],
  },
  'src/features/verification/TokenResultPage.tsx': {
    file: 'assets/TokenResultPage.js',
    name: 'TokenResultPage',
    isDynamicEntry: true,
    imports: ['src/features/verification/VerifyPage.tsx'],
  },
  'src/features/verification/QrScanner.tsx': {
    file: 'assets/QrScanner.js',
    name: 'QrScanner',
    isDynamicEntry: true,
    imports: ['_zxing.js'],
  },
};

describe('manifesto do build', () => {
  it('segue só as importações estáticas', () => {
    const names = chunkNames(manifest, verificationRoots);
    expect(names).toEqual(expect.arrayContaining(['react', 'mui', 'VerifyPage']));
    expect(names).not.toContain('zxing');
    expect(names).not.toContain('mui-x');
    expect(staticClosure(manifest, ['src/features/verification/QrScanner.tsx'])).toContain(
      '_zxing.js',
    );
  });

  it('um bloco inexistente é um erro', () => {
    expect(() => staticClosure(manifest, ['nada.tsx'])).toThrow('nada.tsx');
  });
});
