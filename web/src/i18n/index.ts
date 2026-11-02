import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './locales/en.json';
import ptMZ from './locales/pt-MZ.json';

export const languages = ['pt-MZ', 'en'] as const;
export type Language = (typeof languages)[number];
export const defaultLanguage: Language = 'pt-MZ';

const storageKey = 'moseguros.language';

export function isLanguage(value: unknown): value is Language {
  return typeof value === 'string' && (languages as readonly string[]).includes(value);
}

function storedLanguage(): Language | undefined {
  try {
    const value = globalThis.localStorage?.getItem(storageKey);
    return isLanguage(value) ? value : undefined;
  } catch {
    return undefined;
  }
}

export function changeLanguage(language: Language): Promise<unknown> {
  try {
    globalThis.localStorage?.setItem(storageKey, language);
  } catch {
    // Sem armazenamento (modo privado): a escolha vale só para esta sessão.
  }
  return i18n.changeLanguage(language);
}

i18n.on('languageChanged', (language) => {
  if (typeof document !== 'undefined') {
    document.documentElement.lang = language;
  }
});

void i18n.use(initReactI18next).init({
  resources: {
    'pt-MZ': { translation: ptMZ },
    en: { translation: en },
  },
  lng: storedLanguage() ?? defaultLanguage,
  fallbackLng: defaultLanguage,
  supportedLngs: languages,
  interpolation: { escapeValue: false },
});

export default i18n;
