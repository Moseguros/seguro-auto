import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'login_controller.dart';
import 'otp.dart';

/// `/login`: primeiro passo do login do cliente (telemóvel ou e-mail).
class LoginScreen extends ConsumerWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final options = ref.watch(otpOptionsProvider);
    final controller = ref.read(loginControllerProvider.notifier);

    final Widget content = switch (options) {
      AsyncData(:final value) => IdentifierForm(
        options: value,
        initial: ref.watch(loginControllerProvider).lastRequest,
        onRequest: controller.requestCode,
        onSent: (sent) {
          controller.codeSent(sent);
          context.push('/login/code');
        },
      ),
      AsyncError(:final error) => Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          MessageBanner(Problem.from(error).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(
            onPressed: () => ref.invalidate(otpOptionsProvider),
            child: Text(l10n.retry),
          ),
        ],
      ),
      _ => const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: CircularProgressIndicator(),
        ),
      ),
    };

    return AuthScaffold(
      title: l10n.authLoginTitle,
      subtitle: l10n.authLoginSubtitle,
      child: content,
    );
  }
}

/// Moldura dos dois passos do login: título, subtítulo e conteúdo num cartão.
class AuthScaffold extends StatelessWidget {
  const AuthScaffold({
    super.key,
    required this.title,
    required this.subtitle,
    required this.child,
  });

  final String title;
  final String subtitle;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(title: Text(AppLocalizations.of(context).appName)),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(title, style: theme.textTheme.headlineSmall),
            const SizedBox(height: 8),
            Text(
              subtitle,
              style: theme.textTheme.bodyLarge?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: 24),
            Card(
              child: Padding(padding: const EdgeInsets.all(16), child: child),
            ),
          ],
        ),
      ),
    );
  }
}

/// Primeiro passo: telemóvel ou e-mail e, se permitido, o canal de envio.
///
/// Recebe a função de pedir o código por parâmetro: `requestOtp` no login,
/// `startContactVerification` nos contactos do perfil.
class IdentifierForm extends StatefulWidget {
  const IdentifierForm({
    super.key,
    required this.options,
    required this.onRequest,
    required this.onSent,
    this.types,
    this.initial,
  });

  final OtpOptionsResponse options;

  /// Tipos permitidos; por defeito os `identifierTypes` das opções.
  final List<String>? types;

  /// Pedido anterior (ao voltar do passo do código).
  final OtpRequestData? initial;
  final RequestCode onRequest;
  final ValueChanged<SentCode> onSent;

  @override
  State<IdentifierForm> createState() => _IdentifierFormState();
}

class _IdentifierFormState extends State<IdentifierForm> {
  late final List<String> _allowed =
      widget.types ??
      [for (final type in widget.options.identifierTypes) type.name];
  late String _type;
  late final TextEditingController _msisdn;
  late final TextEditingController _email;
  final Map<String, String> _chosen = {};
  final _countdown = Countdown();
  String? _fieldError;
  String? _message;
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    final initial = widget.initial;
    _type = initial != null && _allowed.contains(initial.identifierType)
        ? initial.identifierType
        : (_allowed.isNotEmpty ? _allowed.first : OtpCodes.msisdn);
    _msisdn = TextEditingController(
      text: initial?.identifierType == OtpCodes.msisdn
          ? localMsisdnDigits(initial!.identifier)
          : '',
    );
    _email = TextEditingController(
      text: initial?.identifierType == OtpCodes.email
          ? initial!.identifier
          : '',
    );
    final channel = initial?.channel;
    if (initial != null && channel != null) {
      _chosen[initial.identifierType] = channel;
    }
    _countdown.addListener(_tick);
  }

  @override
  void dispose() {
    _countdown
      ..removeListener(_tick)
      ..dispose();
    _msisdn.dispose();
    _email.dispose();
    super.dispose();
  }

  void _tick() => setState(() {});

  List<String> get _channels => [
    for (final channel in widget.options.channels[_type] ?? const <InnerEnum>[])
      channel.name,
  ];

  bool get _canChoose =>
      widget.options.customerCanChoose && _channels.length > 1;

  String? get _channel {
    final channels = _channels;
    final chosen = _chosen[_type];
    if (chosen != null && channels.contains(chosen)) return chosen;
    return widget.options.defaultChannels[_type]?.name ??
        (channels.isEmpty ? null : channels.first);
  }

  void _changeType(String type) => setState(() {
    _type = type;
    _fieldError = null;
  });

  Future<void> _submit() async {
    setState(() => _message = null);
    final String identifier;
    if (_type == OtpCodes.msisdn) {
      if (!msisdnDigitsPattern.hasMatch(_msisdn.text)) {
        setState(() => _fieldError = 'INVALID_MSISDN');
        return;
      }
      identifier = '$msisdnPrefix${_msisdn.text}';
    } else {
      final value = _email.text.trim();
      if (!emailPattern.hasMatch(value)) {
        setState(() => _fieldError = 'INVALID_EMAIL');
        return;
      }
      identifier = value;
    }
    final channel = _channel;
    final request = OtpRequestData(
      identifierType: _type,
      identifier: identifier,
      channel: _canChoose ? channel : null,
    );
    setState(() {
      _fieldError = null;
      _submitting = true;
    });
    try {
      final response = await widget.onRequest(request);
      if (!mounted) return;
      widget.onSent(
        SentCode(
          request: request,
          requestedChannel: channel,
          response: response,
        ),
      );
    } catch (error) {
      if (!mounted) return;
      final l10n = AppLocalizations.of(context);
      final problem = Problem.from(error);
      final onField = problem.fieldError(const [
        'identifier',
        'value',
        'identifierType',
        'type',
      ]);
      setState(() {
        if (problem.status == 429) {
          _countdown.restart(problem.retryAfterSeconds ?? 0);
          _message = problem.message(l10n);
        } else if (onField != null) {
          _fieldError = onField.code;
        } else {
          _message = problem.message(l10n);
          if (problem.code == 'OTP_CHANNEL_NOT_AVAILABLE') {
            final other = _allowed.where((type) => type != _type);
            if (other.isNotEmpty) {
              _type = other.first;
              _fieldError = null;
            }
          }
        }
      });
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final fieldError = _fieldError == null
        ? null
        : fieldErrorMessage(l10n, _fieldError!);
    final channel = _channel;

    return AutofillGroup(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          if (_allowed.length > 1) ...[
            Semantics(
              label: l10n.authIdentifierTypeLabel,
              child: SegmentedButton<String>(
                key: const Key('identifier-type'),
                segments: [
                  for (final type in _allowed)
                    ButtonSegment(
                      value: type,
                      label: Text(
                        type == OtpCodes.msisdn
                            ? l10n.authIdentifierTypeMsisdn
                            : l10n.authIdentifierTypeEmail,
                      ),
                      icon: Icon(
                        type == OtpCodes.msisdn
                            ? Icons.smartphone
                            : Icons.email_outlined,
                      ),
                    ),
                ],
                selected: {_type},
                showSelectedIcon: false,
                onSelectionChanged: (value) => _changeType(value.first),
              ),
            ),
            const SizedBox(height: 20),
          ],
          if (_type == OtpCodes.msisdn)
            TextField(
              key: const Key('msisdn-field'),
              controller: _msisdn,
              keyboardType: TextInputType.phone,
              autofillHints: const [AutofillHints.telephoneNumberNational],
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
                LengthLimitingTextInputFormatter(9),
              ],
              textInputAction: TextInputAction.done,
              onSubmitted: (_) => _submit(),
              decoration: InputDecoration(
                labelText: l10n.authMsisdnLabel,
                prefixText: '$msisdnPrefix ',
                helperText: l10n.authMsisdnHelper,
                errorText: fieldError,
                errorMaxLines: 3,
                helperMaxLines: 2,
              ),
            )
          else
            TextField(
              key: const Key('email-field'),
              controller: _email,
              keyboardType: TextInputType.emailAddress,
              autofillHints: const [AutofillHints.email],
              autocorrect: false,
              textInputAction: TextInputAction.done,
              onSubmitted: (_) => _submit(),
              decoration: InputDecoration(
                labelText: l10n.authEmailLabel,
                errorText: fieldError,
                errorMaxLines: 3,
              ),
            ),
          if (_canChoose && channel != null) ...[
            const SizedBox(height: 20),
            Text(
              l10n.authChannelLabel,
              style: Theme.of(context).textTheme.labelLarge,
            ),
            const SizedBox(height: 8),
            SegmentedButton<String>(
              key: const Key('otp-channel'),
              segments: [
                for (final candidate in _channels)
                  ButtonSegment(
                    value: candidate,
                    label: Text(channelName(l10n, candidate)),
                  ),
              ],
              selected: {channel},
              onSelectionChanged: (value) =>
                  setState(() => _chosen[_type] = value.first),
            ),
          ],
          if (_message != null) ...[
            const SizedBox(height: 20),
            MessageBanner(
              _countdown.done
                  ? _message!
                  : '${_message!} ${l10n.authRetryIn(_countdown.value)}',
            ),
          ],
          const SizedBox(height: 24),
          FilledButton(
            key: const Key('request-code'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: _submitting || !_countdown.done ? null : _submit,
            child: Text(l10n.authSubmit),
          ),
        ],
      ),
    );
  }
}
