import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../proposals/proposal_controller.dart';
import '../quote/quote_labels.dart';
import 'await_confirmation.dart';
import 'checking_status.dart';
import 'payment_controller.dart';
import 'payment_intent_controller.dart';
import 'payment_labels.dart';
import 'payment_outcome.dart';
import 'payment_summary_card.dart' show tabularFigures;
import 'reference_details.dart';

/// `/payments/:id` (S5-11 §4): o pagamento, lido de novo a cada 3 s (M-Pesa) ou
/// 30 s (entidade e referência) até a um estado final. Sem botões laranja.
class PaymentIntentScreen extends ConsumerWidget {
  const PaymentIntentScreen({super.key, required this.intentId});

  final String intentId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final provider = paymentIntentProvider(intentId);
    final intent = ref.watch(provider);

    // Num estado final a proposta mudou: relê-se quando se abrir.
    ref.listen(provider, (previous, next) {
      final value = next.value;
      if (value == null || !isFinalPayment(value.status)) return;
      if (previous?.value?.status == value.status) return;
      final proposalId = value.target.id;
      ref
        ..invalidate(proposalProvider(proposalId))
        ..invalidate(paymentMethodsProvider(proposalId))
        ..invalidate(myProposalsProvider)
        ..invalidate(homeProposalsProvider);
    });

    final value = intent.value;
    final Widget body;
    if (value != null) {
      body = RefreshIndicator(
        onRefresh: () => ref.read(provider.notifier).refresh(),
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _IntentHeader(intent: value),
            const SizedBox(height: 16),
            _IntentBody(
              intent: value,
              reload: () => ref.read(provider.notifier).refresh(),
            ),
          ],
        ),
      );
    } else if (intent.hasError) {
      final problem = Problem.from(intent.error!);
      final notFound =
          problem.code == 'PAYMENT_INTENT_NOT_FOUND' || problem.status == 404;
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(
            notFound ? l10n.paymentsIntentNotFound : problem.message(l10n),
            key: const Key('payment-intent-error'),
          ),
          const SizedBox(height: 16),
          if (notFound)
            OutlinedButton(
              style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: () => context.go('/payments'),
              child: Text(l10n.paymentsIntentBackToPayments),
            )
          else
            OutlinedButton(
              style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: () => ref.invalidate(provider),
              child: Text(l10n.retry),
            ),
        ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(
        title: Text(
          value == null
              ? l10n.paymentsListTitle
              : l10n.paymentsIntentReference(value.reference),
        ),
      ),
      body: SafeArea(child: body),
    );
  }
}

class _IntentHeader extends StatelessWidget {
  const _IntentHeader({required this.intent});

  final IntentView intent;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    intent.reference,
                    style: theme.textTheme.bodyMedium,
                  ),
                ),
                PaymentStatusChip(status: intent.status),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              money(l10n, intent.amount),
              style: theme.textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.w700,
                fontFeatures: tabularFigures,
              ),
            ),
            Text(
              '${intent.insurer.displayName ?? ''} · ${intent.target.label}',
              style: theme.textTheme.bodySmall,
            ),
          ],
        ),
      ),
    );
  }
}

class _IntentBody extends StatelessWidget {
  const _IntentBody({required this.intent, required this.reload});

  final IntentView intent;
  final VoidCallback reload;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    if (isFinalPayment(intent.status)) return PaymentOutcome(intent: intent);
    return switch (intent.nextAction.type) {
      PaymentNextActionType.AWAIT_CUSTOMER_CONFIRMATION => AwaitConfirmation(
        intent: intent,
      ),
      PaymentNextActionType.CHECKING_STATUS => CheckingStatus(
        nextCheckAt: intent.nextAction.nextCheckAt,
      ),
      PaymentNextActionType.PAY_BY_REFERENCE => ReferenceDetails(
        intent: intent,
        onReferenceInactive: reload,
      ),
      _ => Row(
        children: [
          const SizedBox.square(
            dimension: 20,
            child: CircularProgressIndicator(strokeWidth: 2),
          ),
          const SizedBox(width: 12),
          Expanded(child: Text(l10n.paymentsIntentPreparing)),
        ],
      ),
    };
  }
}
