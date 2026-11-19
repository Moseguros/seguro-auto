import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/clock.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'payment_summary_card.dart' show tabularFigures;

/// Duração → `mm:ss` (nunca negativa).
String formatCountdown(Duration remaining) {
  final total = remaining.isNegative
      ? 0
      : (remaining.inMilliseconds / 1000).ceil();
  final minutes = (total ~/ 60).toString().padLeft(2, '0');
  final seconds = (total % 60).toString().padLeft(2, '0');
  return '$minutes:$seconds';
}

/// M-Pesa à espera da confirmação no telemóvel (S5-11 §4), com a contagem até
/// `timeoutAt`.
class AwaitConfirmation extends ConsumerStatefulWidget {
  const AwaitConfirmation({super.key, required this.intent});

  final IntentView intent;

  @override
  ConsumerState<AwaitConfirmation> createState() => _AwaitConfirmationState();
}

class _AwaitConfirmationState extends ConsumerState<AwaitConfirmation> {
  Timer? _ticker;

  @override
  void initState() {
    super.initState();
    _ticker = Timer.periodic(const Duration(seconds: 1), (_) {
      if (mounted) setState(() {});
    });
  }

  @override
  void dispose() {
    _ticker?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final intent = widget.intent;
    final timeoutAt = intent.nextAction.timeoutAt;
    final now = ref.watch(clockProvider)();
    final remaining = timeoutAt?.difference(now);
    final timedOut = remaining != null && remaining <= Duration.zero;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          children: [
            Icon(Icons.phone_android, color: theme.colorScheme.primary),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                l10n.paymentsAwaitTitle,
                style: theme.textTheme.titleMedium,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Text(l10n.paymentsAwaitStep1(intent.payerMsisdnMasked ?? '')),
        Text(l10n.paymentsAwaitStep2),
        Text(l10n.paymentsAwaitStep3),
        if (remaining != null) ...[
          const SizedBox(height: 16),
          Text(
            l10n.paymentsAwaitRemaining,
            textAlign: TextAlign.center,
            style: theme.textTheme.bodySmall,
          ),
          Text(
            formatCountdown(remaining),
            key: const Key('payment-countdown'),
            textAlign: TextAlign.center,
            style: theme.textTheme.displaySmall?.copyWith(
              fontWeight: FontWeight.w700,
              fontFeatures: tabularFigures,
            ),
          ),
        ],
        if (timedOut && intent.status == PaymentStatus.PENDING) ...[
          const SizedBox(height: 16),
          MessageBanner(
            l10n.paymentsAwaitStillWaiting,
            key: const Key('payment-still-waiting'),
            kind: MessageKind.info,
          ),
        ],
        const SizedBox(height: 16),
        OutlinedButton(
          key: const Key('payment-back-to-proposal'),
          style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: () => context.go('/proposals/${intent.target.id}'),
          child: Text(l10n.paymentsAwaitBackToProposal),
        ),
      ],
    );
  }
}
