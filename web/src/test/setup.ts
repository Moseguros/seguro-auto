import '@testing-library/jest-dom/vitest';
import { cleanup, configure } from '@testing-library/react';
import { afterEach } from 'vitest';
import { resetSessionForTests } from '../auth/session';
import i18n from '../i18n';

// Os testes correm em paralelo e o jsdom com MUI é lento: dá-se mais tempo às esperas.
configure({ asyncUtilTimeout: 3000 });

afterEach(async () => {
  cleanup();
  resetSessionForTests();
  try {
    localStorage.clear();
    sessionStorage.clear();
  } catch {
    // ignora
  }
  await i18n.changeLanguage('pt-MZ');
});
