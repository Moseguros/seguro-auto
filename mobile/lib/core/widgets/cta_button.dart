import 'package:flutter/material.dart';

/// Botão laranja reservado às acções de conversão: Simular seguro,
/// Comparar propostas, Adquirir apólice e Renovar seguro.
///
/// No máximo um por ecrã; as restantes acções usam `FilledButton` (azul)
/// ou `OutlinedButton`. O texto sobre laranja usa `onSecondary`, nunca branco.
class CtaButton extends StatelessWidget {
  const CtaButton({
    super.key,
    required this.label,
    required this.onPressed,
    this.icon,
  });

  final String label;
  final VoidCallback? onPressed;
  final Widget? icon;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    final style = FilledButton.styleFrom(
      backgroundColor: scheme.secondary,
      foregroundColor: scheme.onSecondary,
      minimumSize: const Size(64, 48),
      textStyle: const TextStyle(fontWeight: FontWeight.w600),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(8)),
      ),
    );
    final icon = this.icon;
    if (icon != null) {
      return FilledButton.icon(
        onPressed: onPressed,
        style: style,
        icon: icon,
        label: Text(label),
      );
    }
    return FilledButton(onPressed: onPressed, style: style, child: Text(label));
  }
}
