import { useCallback, useEffect, useState } from 'react';

/** Contagem decrescente em segundos (reenvio do código, espera depois de um 429). */
export function useResendCountdown(initialSeconds: number) {
  const [remaining, setRemaining] = useState(() => Math.max(0, Math.ceil(initialSeconds)));

  useEffect(() => {
    if (remaining <= 0) return;
    const id = setTimeout(() => setRemaining((value) => Math.max(0, value - 1)), 1000);
    return () => clearTimeout(id);
  }, [remaining]);

  const restart = useCallback((seconds: number) => {
    setRemaining(Math.max(0, Math.ceil(seconds)));
  }, []);
  const finish = useCallback(() => setRemaining(0), []);

  return { remaining, done: remaining <= 0, restart, finish };
}
