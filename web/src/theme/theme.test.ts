import { describe, expect, it } from 'vitest';
import { createAppTheme } from './theme';

describe('createAppTheme', () => {
  it('usa as cores da marca no modo claro', () => {
    const theme = createAppTheme('light');
    expect(theme.palette.primary.main).toBe('#142D4E');
    expect(theme.palette.secondary.main).toBe('#F28C28');
    expect(theme.palette.secondary.contrastText).toBe('#142D4E');
    expect(theme.palette.background.default).toBe('#E8EFF6');
    expect(theme.palette.background.paper).toBe('#FFFFFF');
    expect(theme.shape.borderRadius).toBe(8);
    expect(theme.typography.fontFamily).toMatch(/^Inter, /);
  });

  it('usa as variantes escuras da marca no modo escuro', () => {
    const theme = createAppTheme('dark');
    expect(theme.palette.primary.main).toBe('#9DB8D9');
    expect(theme.palette.background.default).toBe('#0E1A2B');
    expect(theme.palette.background.paper).toBe('#16263B');
    expect(theme.palette.secondary.contrastText).toBe('#0E1A2B');
  });

  it('define a variante de botão cta laranja com texto azul (nunca branco)', () => {
    const theme = createAppTheme('light');
    const cta = theme.components?.MuiButton?.variants?.find(
      (v) => (v.props as { variant?: string }).variant === 'cta',
    );
    expect(cta?.style).toMatchObject({ backgroundColor: '#F28C28', color: '#142D4E' });
  });
});
