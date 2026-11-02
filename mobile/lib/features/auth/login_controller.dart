import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../../core/auth/session_controller.dart';
import 'otp.dart';

/// Opções públicas do login (`getOtpOptions`): tipos, canais e tamanho do código.
final otpOptionsProvider = FutureProvider.autoDispose<OtpOptionsResponse>((
  ref,
) async {
  final response = await ref.read(apiClientProvider).identity.getOtpOptions();
  return response.data!;
}, retry: (_, _) => null);

/// Estado do login entre `/login` e `/login/code`.
@immutable
class LoginState {
  const LoginState({this.lastRequest, this.sent});

  /// Último pedido (ao voltar ao primeiro passo, os campos vêm preenchidos).
  final OtpRequestData? lastRequest;

  /// Código enviado (o segundo passo só abre com ele).
  final SentCode? sent;
}

class LoginController extends Notifier<LoginState> {
  @override
  LoginState build() => const LoginState();

  /// `requestOtp` (primeiro pedido e reenvio).
  Future<OtpRequestResponse> requestCode(OtpRequestData request) async {
    final response = await ref
        .read(apiClientProvider)
        .identity
        .requestOtp(
          requestOtpRequest: RequestOtpRequest(
            (b) => b
              ..identifierType = RequestOtpRequestIdentifierTypeEnum.valueOf(
                request.identifierType,
              )
              ..identifier = request.identifier
              ..channel = request.channel == null
                  ? null
                  : RequestOtpRequestChannelEnum.valueOf(request.channel!),
          ),
        );
    return response.data!;
  }

  void codeSent(SentCode sent) =>
      state = LoginState(lastRequest: sent.request, sent: sent);

  /// "Alterar": volta ao primeiro passo com os campos preenchidos.
  void changeIdentifier() => state = LoginState(lastRequest: state.lastRequest);

  /// `verifyOtp` e abre a sessão; o router segue para o perfil ou o início.
  Future<void> verify(String challengeId, String code) async {
    final response = await ref
        .read(apiClientProvider)
        .identity
        .verifyOtp(
          verifyOtpRequest: VerifyOtpRequest(
            (b) => b
              ..challengeId = challengeId
              ..code = code,
          ),
        );
    final tokens = response.data!;
    await ref
        .read(sessionControllerProvider.notifier)
        .login(
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken,
          isNewUser: tokens.isNewUser,
        );
    state = const LoginState();
  }
}

final loginControllerProvider = NotifierProvider<LoginController, LoginState>(
  LoginController.new,
);
