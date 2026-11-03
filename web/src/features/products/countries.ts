/** Países da SADC, para o âmbito territorial das coberturas (ISO 3166-1 alfa-2). */
export const sadcCountries = [
  'AO',
  'BW',
  'KM',
  'CD',
  'SZ',
  'LS',
  'MG',
  'MW',
  'MU',
  'MZ',
  'NA',
  'SC',
  'ZA',
  'TZ',
  'ZM',
  'ZW',
] as const;

/** Moçambique: sempre no âmbito e não se retira. */
export const homeCountry = 'MZ';

const cache = new Map<string, Intl.DisplayNames>();

/** Nome do país na língua da interface (sem chaves i18n). */
export function countryName(code: string, language: string): string {
  const lang = language === 'en' ? 'en' : 'pt';
  let names = cache.get(lang);
  if (!names) {
    names = new Intl.DisplayNames([lang], { type: 'region' });
    cache.set(lang, names);
  }
  try {
    return names.of(code) ?? code;
  } catch {
    return code;
  }
}
