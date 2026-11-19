import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'payer_msisdn_field.dart';
import 'payment_controller.dart';

/// Resultado do envio por SMS.
sealed class ReferenceSmsResult {
  const ReferenceSmsResult();
}

class ReferenceSmsSent extends ReferenceSmsResult {
  const ReferenceSmsSent(this.sentTo);

  final String sentTo;
}

/// A referência deixou de estar activa: quem abriu relê o pagamento.
class ReferenceSmsInactive extends ReferenceSmsResult {
  const ReferenceSmsInactive();
}

/// Abre a folha para enviar a entidade e a referência por SMS (S5-11 §4).
Future<ReferenceSmsResult?> showSendReferenceSmsSheet(
  BuildContext context, {
  required String intentId,
  required String defaultMsisdn,
}) => showModalBottomSheet<ReferenceSmsResult>(
  context: context,
  isScrollControlled: true,
  showDragHandle: true,
  builder: (context) =>
      SendReferenceSmsSheet(intentId: intentId, defaultMsisdn: defaultMsisdn),
);

class SendReferenceSmsSheet extends ConsumerStatefulWidget {
  const SendReferenceSmsSheet({
    super.key,
    required this.intentId,
    required this.defaultMsisdn,
  });

  final String intentId;

  /// 9 dígitos nacionais, ou `''`.
  final String defaultMsisdn;

  @override
  ConsumerState<SendReferenceSmsSheet> createState() =>
      _SendReferenceSmsSheetState();
}

class _SendReferenceSmsSheetState extends ConsumerState<SendReferenceSmsSheet> {
  late final TextEditingController _msisdn = TextEditingController(
    text: widget.defaultMsisdn,
  );
  bool _touched = false;
  bool _sending = false;
  String? _serverFieldError;
  String? _error;

  @override
  void dispose() {
    _msisdn.dispose();
    super.dispose();
  }

  String? get _fieldError =>
      _serverFieldError ?? validateMozMsisdn(_msisdn.text);

  Future<void> _send() async {
    final l10n = AppLocalizations.of(context);
    if (_fieldError != null) {
      setState(() => _touched = true);
      return;
    }
    setState(() {
      _sending = true;
      _error = null;
    });
    try {
      final sentTo = await ref
          .read(paymentRepositoryProvider)
          .sendReferenceSms(widget.intentId, e164(_msisdn.text));
      if (mounted) Navigator.of(context).pop(ReferenceSmsSent(sentTo));
    } on Object catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      if (problem.code == 'REFERENCE_NOT_ACTIVE') {
        Navigator.of(context).pop(const ReferenceSmsInactive());
        return;
      }
      final field = problem.code == 'VALIDATION_ERROR'
          ? problem.fieldError(['msisdn'])
          : null;
      setState(() {
        _sending = false;
        if (field != null) {
          _serverFieldError = field.code;
          _touched = true;
        } else {
          _error = problem.code == 'REFERENCE_SMS_RATE_LIMITED'
              ? l10n.paymentsSmsRateLimited
              : problem.message(l10n);
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final error = _error;
    return Padding(
      padding: EdgeInsets.fromLTRB(
        16,
        0,
        16,
        16 + MediaQuery.viewInsetsOf(context).bottom,
      ),
      child: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(l10n.paymentsSmsTitle, style: theme.textTheme.titleLarge),
            const SizedBox(height: 16),
            if (error != null) ...[
              MessageBanner(error, key: const Key('reference-sms-error')),
              const SizedBox(height: 12),
            ],
            PayerMsisdnField(
              key: const Key('reference-sms-msisdn'),
              controller: _msisdn,
              label: l10n.paymentsSmsMsisdn,
              autofocus: widget.defaultMsisdn.isEmpty,
              errorCode: _touched ? _fieldError : null,
              onChanged: (_) => setState(() {
                _serverFieldError = null;
                _error = null;
              }),
            ),
            const SizedBox(height: 16),
            FilledButton(
              key: const Key('reference-sms-send'),
              style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: _sending ? null : _send,
              child: _sending
                  ? const SizedBox.square(
                      dimension: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : Text(l10n.paymentsSmsSend),
            ),
          ],
        ),
      ),
    );
  }
}
