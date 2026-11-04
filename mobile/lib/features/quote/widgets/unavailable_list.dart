import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../l10n/app_localizations.dart';
import '../quote_labels.dart';

/// Secção recolhida "Sem proposta ({n})": seguradora, produto e motivo.
class UnavailableList extends StatelessWidget {
  const UnavailableList({super.key, required this.quotes});

  final List<QuoteResponse> quotes;

  @override
  Widget build(BuildContext context) {
    if (quotes.isEmpty) return const SizedBox.shrink();
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Card(
      key: const Key('unavailable-list'),
      clipBehavior: Clip.antiAlias,
      child: ExpansionTile(
        title: Text(l10n.quoteUnavailableTitle(quotes.length)),
        children: [
          for (final quote in quotes)
            ListTile(
              key: Key('unavailable-${quote.id}'),
              title: Text(
                '${quote.insurer.displayName} · ${quote.product.name}',
              ),
              subtitle: Text(
                unavailableReasonText(l10n, quote.unavailableReason?.name),
                style: TextStyle(color: theme.colorScheme.onSurfaceVariant),
              ),
            ),
        ],
      ),
    );
  }
}
