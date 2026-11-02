import { AxiosError, AxiosHeaders, type AxiosResponse } from 'axios';

/** Erro Axios com um ProblemDetail, como o que a API devolve. */
export function problemError(status: number, data: Record<string, unknown>) {
  const config = { headers: new AxiosHeaders() };
  const response = {
    status,
    data,
    statusText: '',
    headers: {},
    config,
  } as unknown as AxiosResponse;
  return new AxiosError('failed', 'ERR_BAD_REQUEST', config, undefined, response);
}
