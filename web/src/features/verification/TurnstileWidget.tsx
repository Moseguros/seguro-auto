import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';

/** O que a página usa da API do Cloudflare Turnstile (`window.turnstile`). */
export interface TurnstileApi {
  render(
    container: HTMLElement,
    options: {
      sitekey: string;
      language?: string;
      theme?: 'auto' | 'light' | 'dark';
      callback?: (token: string) => void;
      'expired-callback'?: () => void;
      'error-callback'?: () => void;
    },
  ): string | undefined;
  reset(widgetId?: string): void;
  remove(widgetId?: string): void;
}

declare global {
  interface Window {
    turnstile?: TurnstileApi;
  }
}

export const turnstileScriptUrl =
  'https://challenges.cloudflare.com/turnstile/v0/api.js?render=explicit';

let loading: Promise<TurnstileApi> | undefined;

/** Carrega o script do Turnstile uma só vez (ou usa o que já existe). */
export function loadTurnstile(): Promise<TurnstileApi> {
  if (window.turnstile) return Promise.resolve(window.turnstile);
  if (!loading) {
    loading = new Promise<TurnstileApi>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = turnstileScriptUrl;
      script.async = true;
      script.defer = true;
      script.onload = () =>
        window.turnstile ? resolve(window.turnstile) : reject(new Error('turnstile'));
      script.onerror = () => {
        loading = undefined;
        script.remove();
        reject(new Error('turnstile'));
      };
      document.head.appendChild(script);
    });
  }
  return loading;
}

export interface TurnstileHandle {
  /** Novo desafio: o token só serve uma vez. */
  reset(): void;
}

/**
 * Desafio anti-robô do Cloudflare Turnstile (S6-05 §2). Chama `onToken` com o token recebido e com
 * `null` quando expira, falha ou é reposto.
 */
export const TurnstileWidget = forwardRef<
  TurnstileHandle,
  { siteKey: string; onToken(token: string | null): void }
>(function TurnstileWidget({ siteKey, onToken }, ref) {
  const { i18n, t } = useTranslation();
  const container = useRef<HTMLDivElement>(null);
  const widgetId = useRef<string | undefined>(undefined);
  const api = useRef<TurnstileApi | undefined>(undefined);
  const onTokenRef = useRef(onToken);
  onTokenRef.current = onToken;
  const [failed, setFailed] = useState(false);
  const language = i18n.language === 'en' ? 'en' : 'pt';

  useImperativeHandle(ref, () => ({
    reset() {
      onTokenRef.current(null);
      api.current?.reset(widgetId.current);
    },
  }));

  useEffect(() => {
    let cancelled = false;
    loadTurnstile()
      .then((turnstile) => {
        if (cancelled || !container.current) return;
        api.current = turnstile;
        widgetId.current = turnstile.render(container.current, {
          sitekey: siteKey,
          language,
          theme: 'auto',
          callback: (token) => onTokenRef.current(token),
          'expired-callback': () => onTokenRef.current(null),
          'error-callback': () => onTokenRef.current(null),
        });
      })
      .catch(() => {
        if (!cancelled) setFailed(true);
      });
    return () => {
      cancelled = true;
      if (widgetId.current !== undefined) api.current?.remove(widgetId.current);
      widgetId.current = undefined;
    };
  }, [siteKey, language]);

  if (failed) return <Alert severity="error">{t('verify.captchaLoadError')}</Alert>;
  return <Box ref={container} data-testid="turnstile" sx={{ minHeight: 65, maxWidth: '100%' }} />;
});
