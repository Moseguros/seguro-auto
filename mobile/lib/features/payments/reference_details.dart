import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/auth/session_controller.dart';
import '../../core/errors/problem.dart';
import '../../core/format/dates.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart';
import 'format_reference.dart';
import 'payment_controller.dart';
import 'payment_summary_card.dart' show tabularFigures;
import 'send_reference_sms_sheet.dart';

/// Entidade, referência e montante para pagar na rede Ponto24 (S5-11 §4): cada
/// um com "Copiar", o prazo, como pagar, o aviso da confirmação, o PDF e o SMS.
class ReferenceDetails extends ConsumerStatefulWidget {
  const ReferenceDetails({
    super.key,
    required this.intent,
    required this.onReferenceInactive,
  });

  final IntentView intent;

  /// A referência deixou de estar activa: quem mostra relê o pagamento.
  final VoidCallback onReferenceInactive;

  @override
  ConsumerState<ReferenceDetails> createState() => _ReferenceDetailsState();
}

class _ReferenceDetailsState extends ConsumerState<ReferenceDetails> {
  bool _downloading = false;
  String? _error;

  void _toast(String message) {
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(SnackBar(content: Text(message)));
  }

  Future<void> _copy(String value) async {
    final l10n = AppLocalizations.of(context);
    await Clipboard.setData(ClipboardData(text: value));
    if (mounted) _toast(l10n.paymentsReferenceCopied);
  }

  Future<void> _pdf(String reference) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _downloading = true;
      _error = null;
    });
    try {
      final file = await ref
          .read(paymentRepositoryProvider)
          .referenceDocument(widget.intent.id);
      await ref.read(pdfSharerProvider)(
        file.bytes,
        'referencia-${referenceCopyValue(reference)}.pdf',
      );
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (problem.code == 'REFERENCE_NOT_ACTIVE' ||
          problem.code == 'NOT_A_REFERENCE_PAYMENT') {
        widget.onReferenceInactive();
      }
      if (mounted) setState(() => _error = problem.message(l10n));
    } finally {
      if (mounted) setState(() => _downloading = false);
    }
  }

  Future<void> _sms() async {
    final l10n = AppLocalizations.of(context);
    final account = nationalFromMsisdn(
      ref.read(sessionControllerProvider).value?.me?.msisdn,
    );
    final result = await showSendReferenceSmsSheet(
      context,
      intentId: widget.intent.id,
      defaultMsisdn: mozNational.hasMatch(account) ? account : '',
    );
    if (!mounted) return;
    switch (result) {
      case ReferenceSmsSent(:final sentTo):
        _toast(l10n.paymentsSmsSent(sentTo));
      case ReferenceSmsInactive():
        widget.onReferenceInactive();
      case null:
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final action = widget.intent.nextAction;
    final entity = action.entity ?? '';
    final reference = action.reference ?? '';
    final amount = action.amount ?? widget.intent.amount;
    final validUntil = action.validUntil;
    final error = _error;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                _CopyRow(
                  keyName: 'entity',
                  label: l10n.paymentsReferenceEntity,
                  display: entity,
                  onCopy: () => _copy(entity),
                ),
                const Divider(height: 24),
                _CopyRow(
                  keyName: 'reference',
                  label: l10n.paymentsReferenceReference,
                  display: formatReference(reference),
                  onCopy: () => _copy(referenceCopyValue(reference)),
                ),
                const Divider(height: 24),
                _CopyRow(
                  keyName: 'amount',
                  label: l10n.paymentsReferenceAmount,
                  display: money(l10n, amount),
                  onCopy: () => _copy(amountCopyValue(amount.amount)),
                ),
                const SizedBox(height: 4),
                Text(
                  l10n.paymentsReferenceExactAmount,
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: Colors.orange.shade900,
                  ),
                ),
              ],
            ),
          ),
        ),
        if (validUntil != null) ...[
          const SizedBox(height: 12),
          Text(
            l10n.paymentsReferenceValidUntil(formatInstantDateTime(validUntil)),
          ),
        ],
        const SizedBox(height: 12),
        Text(l10n.paymentsReferenceHowTo),
        const SizedBox(height: 12),
        MessageBanner(
          l10n.paymentsReferenceConfirmationInfo,
          kind: MessageKind.info,
        ),
        if (error != null) ...[
          const SizedBox(height: 12),
          MessageBanner(error),
        ],
        const SizedBox(height: 16),
        OutlinedButton.icon(
          key: const Key('reference-pdf'),
          style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: _downloading ? null : () => _pdf(reference),
          icon: _downloading
              ? const SizedBox.square(
                  dimension: 18,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              : const Icon(Icons.download),
          label: Text(l10n.paymentsReferenceDownloadPdf),
        ),
        const SizedBox(height: 8),
        OutlinedButton.icon(
          key: const Key('reference-sms'),
          style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: _sms,
          icon: const Icon(Icons.sms_outlined),
          label: Text(l10n.paymentsReferenceSendSms),
        ),
      ],
    );
  }
}

class _CopyRow extends StatelessWidget {
  const _CopyRow({
    required this.keyName,
    required this.label,
    required this.display,
    required this.onCopy,
  });

  final String keyName;
  final String label;
  final String display;
  final VoidCallback onCopy;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Row(
      children: [
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: theme.textTheme.bodySmall),
              Text(
                display,
                key: Key('reference-$keyName'),
                style: const TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.w700,
                  fontFeatures: tabularFigures,
                ),
              ),
            ],
          ),
        ),
        const SizedBox(width: 8),
        Semantics(
          label: l10n.paymentsReferenceCopy(label),
          button: true,
          excludeSemantics: true,
          child: OutlinedButton(
            key: Key('copy-$keyName'),
            style: OutlinedButton.styleFrom(minimumSize: const Size(48, 48)),
            onPressed: onCopy,
            child: Text(l10n.paymentsReferenceCopyButton),
          ),
        ),
      ],
    );
  }
}
