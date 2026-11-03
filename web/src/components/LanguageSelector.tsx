import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import { useTranslation } from 'react-i18next';
import { changeLanguage, defaultLanguage, isLanguage, languages } from '../i18n';

export function LanguageSelector() {
  const { t, i18n } = useTranslation();
  const current = isLanguage(i18n.resolvedLanguage) ? i18n.resolvedLanguage : defaultLanguage;

  return (
    <Select
      variant="standard"
      disableUnderline
      value={current}
      onChange={(event) => {
        if (isLanguage(event.target.value)) void changeLanguage(event.target.value);
      }}
      inputProps={{ 'aria-label': t('language.label') }}
      sx={{ color: 'inherit', fontSize: 14, '& .MuiSvgIcon-root': { color: 'inherit' } }}
    >
      {languages.map((language) => (
        <MenuItem key={language} value={language} lang={language}>
          {t(`language.${language}`)}
        </MenuItem>
      ))}
    </Select>
  );
}
