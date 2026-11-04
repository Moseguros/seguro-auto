import 'package:flutter/material.dart';

import '../../../core/errors/problem.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';

/// Erro ao ler dados de um ecrã de cotações, com "Tentar de novo".
class QuoteLoadError extends StatelessWidget {
  const QuoteLoadError({super.key, required this.error, required this.onRetry});

  final Object error;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        MessageBanner(
          Problem.from(error).message(l10n),
          key: const Key('quote-load-error'),
        ),
        const SizedBox(height: 16),
        OutlinedButton(onPressed: onRetry, child: Text(l10n.retry)),
      ],
    );
  }
}
