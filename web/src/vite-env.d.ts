/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string;
  /** `true` mostra a faixa "DADOS DE DEMONSTRAÇÃO" (ambientes de QA). */
  readonly VITE_DEMO_BANNER?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
