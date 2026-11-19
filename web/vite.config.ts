/// <reference types="vitest" />
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  build: {
    rollupOptions: {
      output: {
        // Bibliotecas em blocos próprios: mudam pouco e ficam em cache entre versões.
        manualChunks(id) {
          // As traduções (pt-MZ e en) num bloco próprio: o pacote inicial fica abaixo dos 500 kB.
          if (/[\\/]src[\\/]i18n[\\/]locales[\\/]/.test(id)) return 'i18n';
          if (!id.includes('node_modules')) return undefined;
          // A grelha de dados (MUI X) só é usada na administração: bloco à parte.
          if (/[\\/]node_modules[\\/]@mui[\\/]x-/.test(id)) return 'mui-x';
          if (/[\\/]node_modules[\\/](@mui|@emotion)[\\/]/.test(id)) return 'mui';
          if (
            /[\\/]node_modules[\\/](react|react-dom|react-router|react-router-dom|scheduler|@remix-run)[\\/]/.test(
              id,
            )
          )
            return 'react';
          return 'vendor';
        },
      },
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    include: ['src/**/*.test.{ts,tsx}'],
    css: false,
    testTimeout: 15_000,
  },
});
