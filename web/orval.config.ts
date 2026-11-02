import { defineConfig } from 'orval';

export default defineConfig({
  moseguros: {
    input: {
      target: '../api/openapi.yaml',
    },
    output: {
      target: 'src/api/generated.ts',
      client: 'react-query',
      mode: 'single',
      override: {
        mutator: {
          path: 'src/api/http.ts',
          name: 'http',
        },
      },
    },
  },
});
