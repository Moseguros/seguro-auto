import 'package:flutter/material.dart';

import '../theme.dart';

enum MessageKind { error, info, success }

/// Aviso dentro do ecrã (erro, informação ou sucesso), lido pelos leitores de ecrã.
class MessageBanner extends StatelessWidget {
  const MessageBanner(this.message, {super.key, this.kind = MessageKind.error});

  final String message;
  final MessageKind kind;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    final isDark = scheme.brightness == Brightness.dark;
    final (color, icon) = switch (kind) {
      MessageKind.error => (scheme.error, Icons.error_outline),
      MessageKind.info => (scheme.primary, Icons.info_outline),
      MessageKind.success => (
        isDark ? BrandColors.successDark : BrandColors.success,
        Icons.check_circle_outline,
      ),
    };
    return Semantics(
      liveRegion: true,
      container: true,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: color.withValues(alpha: 0.08),
          border: Border.all(color: color.withValues(alpha: 0.5)),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: color, size: 20),
            const SizedBox(width: 12),
            Expanded(
              child: Text(message, style: TextStyle(color: scheme.onSurface)),
            ),
          ],
        ),
      ),
    );
  }
}
