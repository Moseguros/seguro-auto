import {
  AxiosError,
  AxiosHeaders,
  type AxiosAdapter,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios';
import { httpClient } from '../api/http';

type MockResult = { status: number; data?: unknown } | 'network-error';
type Handler = (config: InternalAxiosRequestConfig) => MockResult;

/** Simula a API substituindo o adaptador do cliente Axios usado pelo código gerado. */
export function mockApi(handler: Handler): () => void {
  const previous = httpClient.defaults.adapter;
  const adapter: AxiosAdapter = async (config) => {
    const result = handler(config);
    if (result === 'network-error') {
      throw new AxiosError('Network Error', AxiosError.ERR_NETWORK, config);
    }
    const response: AxiosResponse = {
      data: result.data,
      status: result.status,
      statusText: String(result.status),
      headers: new AxiosHeaders(),
      config,
    };
    if (result.status >= 400) {
      throw new AxiosError(
        `Request failed with status code ${result.status}`,
        AxiosError.ERR_BAD_RESPONSE,
        config,
        undefined,
        response,
      );
    }
    return response;
  };
  httpClient.defaults.adapter = adapter;
  return () => {
    httpClient.defaults.adapter = previous;
  };
}
