import axios, {
  AxiosHeaders,
  isAxiosError,
  type AxiosError,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
} from 'axios';

/**
 * Cliente HTTP usado pelo código gerado pelo orval (`src/api/generated.ts`).
 *
 * O `baseURL` é vazio por defeito: os caminhos do contrato já começam por `/api/v1`
 * e, em desenvolvimento, o proxy do Vite encaminha `/api` para o backend. O valor de
 * `VITE_API_BASE_URL` é aplicado em `main.tsx` com {@link configureHttp}: o orval empacota
 * este ficheiro em CommonJS para o analisar e avisa se ele usar `import.meta`.
 *
 * Este ficheiro não importa a sessão nem o i18n (o orval empacota-o sozinho): a sessão
 * regista-se com {@link configureAuth} (`src/auth/session.ts`) e a língua com
 * {@link configureLanguage} (`src/i18n/index.ts`).
 */
export const httpClient = axios.create({ baseURL: '' });

export function configureHttp({ baseURL }: { baseURL: string }): void {
  httpClient.defaults.baseURL = baseURL;
}

/** Ligação entre os interceptores e a sessão. */
export interface AuthHandlers {
  /** Token de acesso da sessão activa, ou `undefined` sem sessão. */
  accessToken(): string | undefined;
  /**
   * Renova a sessão e devolve o token de acesso novo. Pedidos simultâneos recebem a mesma
   * promessa (uma única renovação). Se falhar, a sessão termina e a promessa é rejeitada.
   */
  renew(): Promise<string>;
}

let authHandlers: AuthHandlers | undefined;
let languageProvider: (() => string | undefined) | undefined;

export function configureAuth(handlers: AuthHandlers | undefined): void {
  authHandlers = handlers;
}

export function configureLanguage(provider: () => string | undefined): void {
  languageProvider = provider;
}

const authPath = /\/api\/v1\/auth(\/|$|\?)/;

/** Os pedidos a `/api/v1/auth/**` são públicos e nunca levam `Authorization`. */
export function isAuthRequest(url: string | undefined): boolean {
  return url !== undefined && authPath.test(url);
}

type RetriableConfig = InternalAxiosRequestConfig & { _authRetried?: boolean };

function bearer(token: string): string {
  return `Bearer ${token}`;
}

httpClient.interceptors.request.use((config) => {
  const headers = AxiosHeaders.from(config.headers);
  const language = languageProvider?.();
  if (language) headers.set('Accept-Language', language);
  if (isAuthRequest(config.url)) {
    headers.delete('Authorization');
  } else {
    const token = authHandlers?.accessToken();
    if (token && !headers.has('Authorization')) headers.set('Authorization', bearer(token));
  }
  config.headers = headers;
  return config;
});

httpClient.interceptors.response.use(undefined, async (error: unknown) => {
  if (!isAxiosError(error) || error.response?.status !== 401 || !error.config) throw error;
  const config = error.config as RetriableConfig;
  const handlers = authHandlers;
  if (!handlers || config._authRetried || isAuthRequest(config.url)) throw error;
  const current = handlers.accessToken();
  if (!current) throw error;

  const sent = AxiosHeaders.from(config.headers).get('Authorization');
  let token: string;
  if (sent !== bearer(current)) {
    // Outro pedido já renovou a sessão depois deste ter partido: basta repetir.
    token = current;
  } else {
    try {
      token = await handlers.renew();
    } catch {
      throw error;
    }
  }
  const headers = AxiosHeaders.from(config.headers);
  headers.set('Authorization', bearer(token));
  return httpClient({ ...config, headers, _authRetried: true } as RetriableConfig);
});

export const http = <T>(config: AxiosRequestConfig, options?: AxiosRequestConfig): Promise<T> =>
  httpClient({ ...config, ...options }).then(({ data }) => data as T);

export type ErrorType<E> = AxiosError<E>;

export type BodyType<B> = B;
