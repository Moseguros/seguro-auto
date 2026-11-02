import { createTheme, type PaletteMode, type Theme } from '@mui/material/styles';

declare module '@mui/material/Button' {
  interface ButtonPropsVariantOverrides {
    /** Botão laranja reservado às acções de conversão (simular, comparar, adquirir, renovar). */
    cta: true;
  }
}

/** Cores da marca (`docs/brand/README.md`), por modo. */
export const brand = {
  light: {
    primary: '#142D4E',
    onPrimary: '#FFFFFF',
    accent: '#F28C28',
    onAccent: '#142D4E',
    background: '#E8EFF6',
    surface: '#FFFFFF',
    textPrimary: '#142D4E',
    textSecondary: '#4A5D75',
    error: '#C62828',
    success: '#2E7D32',
  },
  dark: {
    primary: '#9DB8D9',
    onPrimary: '#0E1A2B',
    accent: '#F28C28',
    onAccent: '#0E1A2B',
    background: '#0E1A2B',
    surface: '#16263B',
    textPrimary: '#E8EFF6',
    textSecondary: '#9DB8D9',
    error: '#EF9A9A',
    success: '#81C784',
  },
} as const;

export const fontFamily = ['Inter', '"Segoe UI"', 'Roboto', 'Arial', 'sans-serif'].join(', ');

export function createAppTheme(mode: PaletteMode): Theme {
  const c = brand[mode];
  return createTheme({
    palette: {
      mode,
      primary: { main: c.primary, contrastText: c.onPrimary },
      secondary: { main: c.accent, contrastText: c.onAccent },
      background: { default: c.background, paper: c.surface },
      text: { primary: c.textPrimary, secondary: c.textSecondary },
      error: { main: c.error },
      success: { main: c.success },
    },
    typography: {
      fontFamily,
      h1: { fontWeight: 700 },
      h2: { fontWeight: 700 },
      h3: { fontWeight: 600 },
      h4: { fontWeight: 600 },
      h5: { fontWeight: 600 },
      h6: { fontWeight: 600 },
    },
    shape: { borderRadius: 8 },
    components: {
      MuiCard: {
        defaultProps: { elevation: 1 },
      },
      MuiButton: {
        variants: [
          {
            props: { variant: 'cta' },
            style: {
              backgroundColor: c.accent,
              color: c.onAccent,
              fontWeight: 600,
              '&:hover': { backgroundColor: '#D9761A' },
            },
          },
        ],
      },
    },
  });
}
