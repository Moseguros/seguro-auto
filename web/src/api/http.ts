import axios, { type AxiosError, type AxiosRequestConfig } from 'axios';

/**
 * Cliente HTTP usado pelo código gerado pelo orval (`src/api/generated.ts`).
 *
 * O `baseURL` é vazio por defeito: os caminhos do contrato já começam por `/api/v1`
 * e, em desenvolvimento, o proxy do Vite encaminha `/api` para o backend. O valor de
 * `VITE_API_BASE_URL` é aplicado em `main.tsx` com {@link configureHttp}: o orval empacota
 * este ficheiro em CommonJS para o analisar e avisa se ele usar `import.meta`.
 */
export const httpClient = axios.create({ baseURL: '' });

export function configureHttp({ baseURL }: { baseURL: string }): void {
  httpClient.defaults.baseURL = baseURL;
}

export const http = <T>(config: AxiosRequestConfig, options?: AxiosRequestConfig): Promise<T> =>
  httpClient({ ...config, ...options }).then(({ data }) => data as T);

export type ErrorType<E> = AxiosError<E>;

export type BodyType<B> = B;
