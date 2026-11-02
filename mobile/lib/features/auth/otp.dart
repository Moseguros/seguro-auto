import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';

/// Tipos de identificador (`MSISDN`, `EMAIL`) e canais (`SMS`, `WHATSAPP`, `EMAIL`)
/// como na API.
abstract final class OtpCodes {
  static const msisdn = 'MSISDN';
  static const email = 'EMAIL';
}

/// Pedido de código: o login (`requestOtp`) e os contactos (`startContactVerification`).
@immutable
class OtpRequestData {
  const OtpRequestData({
    required this.identifierType,
    required this.identifier,
    this.channel,
  });

  /// `MSISDN` ou `EMAIL`.
  final String identifierType;

  /// Telemóvel em E.164 (`+258…`) ou e-mail.
  final String identifier;

  /// Só quando o cliente pode escolher o canal.
  final String? channel;
}

/// Pede o código (a mesma função no primeiro pedido e no reenvio).
typedef RequestCode =
    Future<OtpRequestResponse> Function(OtpRequestData request);

/// Valida o código recebido.
typedef VerifyCode = Future<void> Function(String challengeId, String code);

/// Estado do envio (`getOtpChallenge`).
typedef GetChallenge =
    Future<OtpChallengeResponse> Function(String challengeId);

/// Código enviado: o pedido, o canal pedido (escolhido ou por defeito) e a resposta.
@immutable
class SentCode {
  const SentCode({
    required this.request,
    required this.requestedChannel,
    required this.response,
  });

  final OtpRequestData request;
  final String? requestedChannel;
  final OtpRequestResponse response;

  /// Canal usado no envio (`SMS`, `WHATSAPP`, `EMAIL`).
  String get channel => response.channel.name;
}

const msisdnPrefix = '+258';
final msisdnDigitsPattern = RegExp(r'^8[2-7]\d{7}$');
final emailPattern = RegExp(r'^[^\s@]+@[^\s@]+\.[^\s@]+$');

/// Destino mascarado na app: `+258*****4567` ou `a***@exemplo.co.mz`.
String maskIdentifier(String type, String identifier) {
  if (type == OtpCodes.msisdn) {
    final digits = identifier.replaceAll(RegExp(r'\D'), '');
    final local = digits.startsWith('258') ? digits.substring(3) : digits;
    final hidden = local.length > 4 ? local.length - 4 : 0;
    return '$msisdnPrefix${'*' * hidden}${local.substring(hidden)}';
  }
  final at = identifier.lastIndexOf('@');
  if (at <= 0) return identifier;
  return '${identifier[0]}***${identifier.substring(at)}';
}

/// Os 9 dígitos locais de um telemóvel guardado em E.164.
String localMsisdnDigits(String identifier) {
  final digits = identifier.replaceAll(RegExp(r'\D'), '');
  return digits.startsWith('258') ? digits.substring(3) : digits;
}

/// Nome visível de um canal (`channel.<CODE>` na web).
String channelName(AppLocalizations l10n, String channel) => switch (channel) {
  'SMS' => l10n.channelSms,
  'WHATSAPP' => l10n.channelWhatsapp,
  'EMAIL' => l10n.channelEmail,
  _ => channel,
};

/// Contagem decrescente em segundos (reenvio do código, espera depois de um 429).
class Countdown extends ValueNotifier<int> {
  Countdown([int seconds = 0]) : super(0) {
    restart(seconds);
  }

  Timer? _timer;

  bool get done => value <= 0;

  void restart(int seconds) {
    _timer?.cancel();
    value = seconds > 0 ? seconds : 0;
    if (value > 0) {
      _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
        value = value - 1;
        if (value <= 0) timer.cancel();
      });
    }
  }

  void finish() => restart(0);

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }
}
