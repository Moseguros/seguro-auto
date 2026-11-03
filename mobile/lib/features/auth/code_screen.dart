import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/api/api_client.dart';
import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'login_controller.dart';
import 'login_screen.dart';
import 'otp.dart';
import 'otp_code_field.dart';

/// `/login/code`: segundo passo do login (o código recebido).
class CodeScreen extends ConsumerWidget {
  const CodeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final sent = ref.watch(loginControllerProvider).sent;
    final options = ref.watch(otpOptionsProvider).value;
    final controller = ref.read(loginControllerProvider.notifier);

    if (sent == null || options == null) {
      // Sem código pedido (ex.: a app foi reaberta neste ecrã): volta ao primeiro passo.
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (context.mounted) context.go('/login');
      });
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    return AuthScaffold(
      title: l10n.authLoginTitle,
      subtitle: l10n.authCodeSubtitle,
      child: CodeForm(
        sent: sent,
        codeLength: options.codeLength,
        onRequest: controller.requestCode,
        onVerify: controller.verify,
        getChallenge: (challengeId) async =>
            (await ref
                    .read(apiClientProvider)
                    .identity
                    .getOtpChallenge(challengeId: challengeId))
                .data!,
        onChangeIdentifier: () {
          controller.changeIdentifier();
          if (context.canPop()) {
            context.pop();
          } else {
            context.go('/login');
          }
        },
      ),
    );
  }
}

/// Durante 30 s depois de cada envio, consulta o canal a cada 5 s.
const challengePollInterval = Duration(seconds: 5);
const challengePollDuration = Duration(seconds: 30);

/// Códigos depois dos quais o código actual já não serve: o reenvio fica logo activo.
const _resendNowCodes = {
  'OTP_EXPIRED',
  'OTP_ATTEMPTS_EXCEEDED',
  'OTP_SUPERSEDED',
};

/// Segundo passo: o código recebido, com reenvio e aviso de mudança de canal.
///
/// [onRequest] é a mesma função do primeiro passo: `requestOtp` no login,
/// `startContactVerification` nos contactos.
class CodeForm extends StatefulWidget {
  const CodeForm({
    super.key,
    required this.sent,
    required this.codeLength,
    required this.onRequest,
    required this.onVerify,
    required this.getChallenge,
    required this.onChangeIdentifier,
  });

  final SentCode sent;
  final int codeLength;
  final RequestCode onRequest;
  final VerifyCode onVerify;
  final GetChallenge getChallenge;
  final VoidCallback onChangeIdentifier;

  @override
  State<CodeForm> createState() => _CodeFormState();
}

class _Fallback {
  const _Fallback(this.requested, this.used);

  final String requested;
  final String used;
}

class _CodeFormState extends State<CodeForm> {
  late SentCode _sent = widget.sent;
  late String _channel = widget.sent.channel;
  late _Fallback? _fallback = _fallbackOf(widget.sent);
  late final _countdown = Countdown(widget.sent.response.resendAfterSeconds);
  Timer? _poll;
  String? _error;
  String? _info;
  int _attempt = 0;
  bool _busy = false;

  @override
  void initState() {
    super.initState();
    _countdown.addListener(_tick);
    _startPolling();
  }

  @override
  void dispose() {
    _poll?.cancel();
    _countdown
      ..removeListener(_tick)
      ..dispose();
    super.dispose();
  }

  void _tick() => setState(() {});

  String get _requestedChannel => _sent.requestedChannel ?? _sent.channel;

  static _Fallback? _fallbackOf(SentCode sent) {
    if (!sent.response.fallbackUsed) return null;
    return _Fallback(sent.requestedChannel ?? sent.channel, sent.channel);
  }

  void _startPolling() {
    _poll?.cancel();
    final challengeId = _sent.response.challengeId;
    _poll = Timer.periodic(challengePollInterval, (timer) async {
      if (challengePollInterval * timer.tick >= challengePollDuration) {
        timer.cancel();
      }
      try {
        final challenge = await widget.getChallenge(challengeId);
        final channel = challenge.channel.name;
        if (!mounted || channel == _channel) return;
        setState(() {
          _channel = channel;
          _fallback = _Fallback(_requestedChannel, channel);
        });
      } catch (_) {
        // A consulta é só informativa.
      }
    });
  }

  Future<void> _verify(String code) async {
    if (_busy) return;
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
      _info = null;
    });
    try {
      await widget.onVerify(_sent.response.challengeId, code);
    } catch (failure) {
      if (!mounted) return;
      final problem = Problem.from(failure);
      setState(() {
        final remaining = problem.attemptsRemaining;
        _error = problem.code == 'OTP_INVALID' && remaining != null
            ? l10n.authCodeWrongCode(remaining)
            : problem.message(l10n);
        if (_resendNowCodes.contains(problem.code)) _countdown.finish();
        _attempt++;
      });
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  Future<void> _resend() async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
      _info = null;
    });
    try {
      final response = await widget.onRequest(_sent.request);
      if (!mounted) return;
      final next = SentCode(
        request: _sent.request,
        requestedChannel: _sent.requestedChannel,
        response: response,
      );
      setState(() {
        _sent = next;
        _channel = next.channel;
        _fallback = _fallbackOf(next);
        _countdown.restart(response.resendAfterSeconds);
        _info = l10n.authCodeResent;
        _attempt++;
      });
      _startPolling();
    } catch (failure) {
      if (!mounted) return;
      final problem = Problem.from(failure);
      setState(() {
        _error = problem.message(l10n);
        if (problem.status == 429) {
          _countdown.restart(problem.retryAfterSeconds ?? 0);
        }
      });
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final fallback = _fallback;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Text(
          l10n.authCodeSentTo(
            channelName(l10n, _channel),
            maskIdentifier(
              _sent.request.identifierType,
              _sent.request.identifier,
            ),
          ),
          key: const Key('code-sent-to'),
          style: theme.textTheme.bodyLarge,
        ),
        Align(
          alignment: AlignmentDirectional.centerStart,
          child: TextButton(
            onPressed: _busy ? null : widget.onChangeIdentifier,
            child: Text(l10n.authCodeChange),
          ),
        ),
        if (fallback != null) ...[
          const SizedBox(height: 8),
          MessageBanner(
            l10n.authCodeFallback(
              channelName(l10n, fallback.requested),
              channelName(l10n, fallback.used),
            ),
            key: const Key('code-fallback'),
            kind: MessageKind.info,
          ),
        ],
        const SizedBox(height: 16),
        OtpCodeField(
          key: ValueKey(_attempt),
          length: widget.codeLength,
          enabled: !_busy,
          hasError: _error != null,
          onCompleted: _verify,
        ),
        if (_error != null) ...[
          const SizedBox(height: 16),
          MessageBanner(_error!, key: const Key('code-error')),
        ],
        if (_info != null) ...[
          const SizedBox(height: 16),
          MessageBanner(_info!, kind: MessageKind.success),
        ],
        const SizedBox(height: 16),
        TextButton(
          key: const Key('resend-code'),
          onPressed: _busy || !_countdown.done ? null : _resend,
          child: Text(
            _countdown.done
                ? l10n.authCodeResend
                : l10n.authCodeResendIn(_countdown.value),
          ),
        ),
      ],
    );
  }
}
