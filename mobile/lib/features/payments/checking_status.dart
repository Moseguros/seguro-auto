import 'package:flutter/material.dart';

import '../../core/format/dates.dart';
import '../../l10n/app_localizations.dart';

/// Estado `UNKNOWN` (S5-11 §4): estamos a confirmar; sem botões de pagamento.
class CheckingStatus extends StatelessWidget {
  const CheckingStatus({super.key, this.nextCheckAt});

  final DateTime? nextCheckAt;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final next = nextCheckAt;
    final color = Colors.orange.shade800;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Semantics(
          liveRegion: true,
          container: true,
          child: Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: color.withValues(alpha: 0.08),
              border: Border.all(color: color.withValues(alpha: 0.5)),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Text.rich(
              key: const Key('payment-checking'),
              TextSpan(
                children: [
                  TextSpan(text: '${l10n.paymentsCheckingLead} '),
                  TextSpan(
                    text: l10n.paymentsCheckingDontPay,
                    style: const TextStyle(fontWeight: FontWeight.w700),
                  ),
                  TextSpan(text: l10n.paymentsCheckingRest),
                ],
              ),
            ),
          ),
        ),
        if (next != null) ...[
          const SizedBox(height: 12),
          Text(
            l10n.paymentsCheckingNextCheck(formatInstantTime(next)),
            style: theme.textTheme.bodyMedium,
          ),
        ],
      ],
    );
  }
}
