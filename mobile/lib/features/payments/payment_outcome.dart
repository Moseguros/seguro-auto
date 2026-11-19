import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';
import 'payment_labels.dart';

/// Resultado de um pagamento num estado final (S5-11 §4), com o passo seguinte.
class PaymentOutcome extends StatelessWidget {
  const PaymentOutcome({super.key, required this.intent});

  final IntentView intent;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final proposalId = intent.target.id;
    final (
      IconData icon,
      Color color,
      String? title,
      String message,
      bool retry,
    ) = switch (intent.status) {
      PaymentStatus.SUCCEEDED => (
        Icons.check_circle,
        Colors.green.shade700,
        l10n.paymentsOutcomeSucceeded,
        l10n.paymentsOutcomeSucceededText,
        false,
      ),
      PaymentStatus.FAILED => (
        Icons.error_outline,
        theme.colorScheme.error,
        null,
        intent.failureCode == null
            ? l10n.paymentStatusFailed
            : paymentFailureLabel(l10n, intent.failureCode!),
        true,
      ),
      PaymentStatus.EXPIRED => (
        Icons.schedule,
        theme.colorScheme.primary,
        null,
        l10n.paymentsOutcomeExpired,
        true,
      ),
      PaymentStatus.CANCELLED => (
        Icons.info_outline,
        theme.colorScheme.primary,
        null,
        l10n.paymentsOutcomeCancelled,
        false,
      ),
      PaymentStatus.REFUNDED => (
        Icons.info_outline,
        theme.colorScheme.primary,
        null,
        l10n.paymentsOutcomeRefunded,
        false,
      ),
      _ => (
        Icons.info_outline,
        theme.colorScheme.primary,
        null,
        paymentStatusLabel(l10n, intent.status),
        false,
      ),
    };
    return Semantics(
      liveRegion: true,
      container: true,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Icon(icon, color: color, size: 56),
          const SizedBox(height: 8),
          if (title != null)
            Text(
              title,
              key: const Key('payment-outcome-title'),
              textAlign: TextAlign.center,
              style: theme.textTheme.titleLarge,
            ),
          Text(
            message,
            key: const Key('payment-outcome-message'),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          FilledButton(
            key: Key(
              retry ? 'payment-choose-another' : 'payment-view-proposal',
            ),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: () => context.go(
              retry ? '/proposals/$proposalId/pay' : '/proposals/$proposalId',
            ),
            child: Text(
              retry
                  ? l10n.paymentsOutcomeChooseAnother
                  : l10n.paymentsOutcomeViewProposal,
            ),
          ),
        ],
      ),
    );
  }
}
