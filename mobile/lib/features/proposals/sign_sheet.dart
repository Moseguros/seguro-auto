import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../auth/login_controller.dart' show otpOptionsProvider;
import '../auth/otp.dart';
import '../auth/otp_code_field.dart';
import '../quote/widgets/quote_document_button.dart';
import 'proposal_controller.dart';

/// Comprimento do código quando as opções do OTP ainda não chegaram.
const defaultSignCodeLength = 6;

/// Códigos depois dos quais é preciso um código novo.
const _newCodeNeeded = {
  'OTP_EXPIRED',
  'OTP_ATTEMPTS_EXCEEDED',
  'OTP_SUPERSEDED',
  'SIGN_REQUEST_NOT_FOUND',
  'PROPOSAL_CHANGED_SINCE_SIGN_REQUEST',
};

/// Resultado da folha de assinatura.
class SignOutcome {
  const SignOutcome({required this.proposal, this.submitError});

  /// A proposta depois de assinada (e, se correu bem, submetida).
  final ProposalResponse proposal;

  /// A assinatura ficou gravada mas a submissão falhou (ex.: `INSURER_NOT_ACTIVE`).
  final Problem? submitError;
}

/// Abre a folha de assinatura com o código já pedido ([request]).
Future<SignOutcome?> showSignSheet(
  BuildContext context, {
  required ProposalResponse proposal,
  required SignatureRequestResponse request,
}) => showModalBottomSheet<SignOutcome>(
  context: context,
  isScrollControlled: true,
  useSafeArea: true,
  builder: (context) => FractionallySizedBox(
    heightFactor: 1,
    child: SignSheet(proposal: proposal, request: request),
  ),
);

/// Assinatura por código (S4-09 §7, igual ao `SignDialog` do cliente na web):
/// "Enviámos um código por {canal} para {destino}.", a proposta em PDF, o código
/// com reenvio e, completo, `confirmProposalSignature` → `submitProposal`.
class SignSheet extends ConsumerStatefulWidget {
  const SignSheet({super.key, required this.proposal, required this.request});

  final ProposalResponse proposal;
  final SignatureRequestResponse request;

  @override
  ConsumerState<SignSheet> createState() => _SignSheetState();
}

class _SignSheetState extends ConsumerState<SignSheet> {
  late SignatureRequestResponse _request = widget.request;
  late final _countdown = Countdown(widget.request.resendAfterSeconds);
  late int _version = widget.proposal.version;
  bool _busy = false;
  bool _submitting = false;
  String? _error;
  int _attempt = 0;

  @override
  void initState() {
    super.initState();
    _countdown.addListener(_tick);
  }

  void _tick() {
    if (mounted) setState(() {});
  }

  @override
  void dispose() {
    _countdown
      ..removeListener(_tick)
      ..dispose();
    super.dispose();
  }

  ProposalRepository get _repository => ref.read(proposalRepositoryProvider);

  Future<void> _verify(String code) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    ProposalResponse signed;
    try {
      signed = await _repository.confirmSignature(
        widget.proposal.id,
        challengeId: _request.challengeId,
        code: code,
      );
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (!mounted) return;
      setState(() {
        _busy = false;
        _attempt++;
        _error = problem.message(l10n);
      });
      if (_newCodeNeeded.contains(problem.code)) _countdown.finish();
      return;
    }
    if (!mounted) return;
    setState(() => _submitting = true);
    try {
      final submitted = await _repository.submit(
        widget.proposal.id,
        version: signed.version,
      );
      if (mounted) {
        Navigator.of(context).pop(SignOutcome(proposal: submitted));
      }
    } on Object catch (error) {
      if (mounted) {
        Navigator.of(
          context,
        ).pop(SignOutcome(proposal: signed, submitError: Problem.from(error)));
      }
    }
  }

  Future<void> _resend() async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      // A proposta pode ter mudado de versão (ex.: depois de um 409).
      final current = await _repository.get(widget.proposal.id);
      _version = current.version;
      final next = await _repository.requestSignature(
        widget.proposal.id,
        version: _version,
      );
      if (!mounted) return;
      setState(() {
        _request = next;
        _attempt++;
      });
      _countdown.restart(next.resendAfterSeconds);
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (!mounted) return;
      setState(() => _error = problem.message(l10n));
      if (problem.retryAfterSeconds != null) {
        _countdown.restart(problem.retryAfterSeconds!);
      }
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final codeLength =
        ref.watch(otpOptionsProvider).value?.codeLength ??
        defaultSignCodeLength;
    final error = _error;
    final channel = channelName(l10n, _request.channel.name);
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false,
        title: Text(l10n.signTitle),
        actions: [
          IconButton(
            key: const Key('sign-close'),
            tooltip: l10n.close,
            onPressed: _busy ? null : () => Navigator.of(context).pop(),
            icon: const Icon(Icons.close),
          ),
        ],
      ),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(
              l10n.signCodeSent(channel, _request.destinationMasked),
              key: const Key('sign-code-sent'),
              style: theme.textTheme.bodyLarge,
            ),
            if (_request.fallbackUsed) ...[
              const SizedBox(height: 8),
              MessageBanner(
                l10n.signFallback(channel),
                key: const Key('sign-fallback'),
                kind: MessageKind.info,
              ),
            ],
            const SizedBox(height: 8),
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: QuoteDocumentButton(
                fileId: _request.documentFileId,
                label: l10n.signViewPdf,
                name: 'proposta-${widget.proposal.number}',
              ),
            ),
            const SizedBox(height: 16),
            OtpCodeField(
              key: ValueKey('sign-code-$_attempt-$codeLength'),
              length: codeLength,
              enabled: !_busy,
              hasError: error != null,
              onCompleted: _verify,
            ),
            if (_submitting) ...[
              const SizedBox(height: 16),
              Row(
                children: [
                  const SizedBox.square(
                    dimension: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  ),
                  const SizedBox(width: 12),
                  Expanded(child: Text(l10n.signSubmitting)),
                ],
              ),
            ],
            if (error != null) ...[
              const SizedBox(height: 16),
              MessageBanner(error, key: const Key('sign-error')),
            ],
            const SizedBox(height: 16),
            TextButton(
              key: const Key('sign-resend'),
              style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
              onPressed: _busy || !_countdown.done ? null : _resend,
              child: Text(
                _countdown.done
                    ? l10n.authCodeResend
                    : l10n.authCodeResendIn(_countdown.value),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
