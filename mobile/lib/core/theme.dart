import 'package:flutter/material.dart';

/// Cores da marca (docs/brand/README.md).
abstract final class BrandColors {
  static const primary = Color(0xFF142D4E);
  static const accent = Color(0xFFF28C28);
  static const background = Color(0xFFE8EFF6);
  static const surface = Color(0xFFFFFFFF);
  static const textSecondary = Color(0xFF4A5D75);
  static const error = Color(0xFFC62828);
  static const success = Color(0xFF2E7D32);

  static const primaryDark = Color(0xFF9DB8D9);
  static const onPrimaryDark = Color(0xFF0E1A2B);
  static const backgroundDark = Color(0xFF0E1A2B);
  static const surfaceDark = Color(0xFF16263B);
  static const textPrimaryDark = Color(0xFFE8EFF6);
  static const errorDark = Color(0xFFEF9A9A);
  static const successDark = Color(0xFF81C784);
}

/// Tema Material 3 com `ColorScheme` explícito (não só cor semente).
abstract final class AppTheme {
  static const lightScheme = ColorScheme(
    brightness: Brightness.light,
    primary: BrandColors.primary,
    onPrimary: Colors.white,
    secondary: BrandColors.accent,
    // Texto sobre laranja: nunca branco.
    onSecondary: BrandColors.primary,
    error: BrandColors.error,
    onError: Colors.white,
    surface: BrandColors.surface,
    onSurface: BrandColors.primary,
    onSurfaceVariant: BrandColors.textSecondary,
    surfaceTint: Colors.transparent,
  );

  static const darkScheme = ColorScheme(
    brightness: Brightness.dark,
    primary: BrandColors.primaryDark,
    onPrimary: BrandColors.onPrimaryDark,
    secondary: BrandColors.accent,
    onSecondary: BrandColors.onPrimaryDark,
    error: BrandColors.errorDark,
    onError: BrandColors.onPrimaryDark,
    surface: BrandColors.surfaceDark,
    onSurface: BrandColors.textPrimaryDark,
    onSurfaceVariant: BrandColors.primaryDark,
    surfaceTint: Colors.transparent,
  );

  static ThemeData get light => _build(lightScheme, BrandColors.background);

  static ThemeData get dark => _build(darkScheme, BrandColors.backgroundDark);

  static const _radius = BorderRadius.all(Radius.circular(8));

  static ThemeData _build(ColorScheme scheme, Color background) {
    final isLight = scheme.brightness == Brightness.light;
    return ThemeData(
      useMaterial3: true,
      colorScheme: scheme,
      scaffoldBackgroundColor: background,
      appBarTheme: AppBarTheme(
        // Barra superior azul no claro; no escuro, a superfície.
        backgroundColor: isLight ? scheme.primary : scheme.surface,
        foregroundColor: isLight ? scheme.onPrimary : scheme.onSurface,
      ),
      cardTheme: CardThemeData(
        color: scheme.surface,
        elevation: 1,
        margin: EdgeInsets.zero,
        shape: const RoundedRectangleBorder(borderRadius: _radius),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          backgroundColor: scheme.primary,
          foregroundColor: scheme.onPrimary,
          shape: const RoundedRectangleBorder(borderRadius: _radius),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: scheme.primary,
          shape: const RoundedRectangleBorder(borderRadius: _radius),
        ),
      ),
    );
  }
}
