import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/format/dates.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart';

/// Algarismos tabulares (montantes, entidade e referência).
const tabularFigures = [FontFeature.tabularFigures()];

/// Resumo do que se paga (S5-11 §3): proposta, seguradora, total, prazo e a
/// frase fixa de que o dinheiro vai directamente para a seguradora.
class PaymentSummaryCard extends StatelessWidget {
  const PaymentSummaryCard({
    super.key,
    required this.proposalNumber,
    required this.insurerName,
    required this.amount,
    this.expiresAt,
  });

  final String proposalNumber;
  final String insurerName;
  final Money amount;
  final DateTime? expiresAt;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final expires = expiresAt;
    return Card(
      key: const Key('payment-summary'),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '${l10n.paymentsSummaryProposal(proposalNumber)} · $insurerName',
              style: theme.textTheme.bodyMedium,
            ),
            const SizedBox(height: 8),
            Text(l10n.paymentsSummaryTotal, style: theme.textTheme.bodySmall),
            Text(
              money(l10n, amount),
              key: const Key('payment-total'),
              style: theme.textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.w700,
                fontFeatures: tabularFigures,
              ),
            ),
            if (expires != null) ...[
              const SizedBox(height: 8),
              Text(l10n.paymentsSummaryPayBy(formatInstantDateTime(expires))),
            ],
            const SizedBox(height: 8),
            Text(
              l10n.paymentsSummaryDirect(insurerName),
              style: theme.textTheme.bodySmall,
            ),
          ],
        ),
      ),
    );
  }
}
