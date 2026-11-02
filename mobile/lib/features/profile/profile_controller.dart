import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../../core/auth/session_controller.dart';
import '../auth/otp.dart';

/// Perfil do cliente (`getMyProfile` / `updateMyProfile`).
class ProfileController extends AsyncNotifier<CustomerProfileResponse> {
  IdentityApi get _identity => ref.read(apiClientProvider).identity;

  @override
  Future<CustomerProfileResponse> build() async =>
      (await _identity.getMyProfile()).data!;

  /// Grava e actualiza o utilizador da sessão (nome e `profileComplete`).
  Future<CustomerProfileResponse> save(UpdateMyProfileRequest request) async {
    final saved = (await _identity.updateMyProfile(
      updateMyProfileRequest: request,
    )).data!;
    state = AsyncData(saved);
    await ref.read(sessionControllerProvider.notifier).refreshMe();
    return saved;
  }

  /// Volta a ler o perfil (409 `CONCURRENT_MODIFICATION`).
  void reload() => ref.invalidateSelf();
}

final profileControllerProvider =
    AsyncNotifierProvider.autoDispose<
      ProfileController,
      CustomerProfileResponse
    >(ProfileController.new, retry: (_, _) => null);

/// Contactos verificados do cliente (`listMyContacts`).
class ContactsController extends AsyncNotifier<ContactsResponse> {
  IdentityApi get _identity => ref.read(apiClientProvider).identity;

  @override
  Future<ContactsResponse> build() async =>
      (await _identity.listMyContacts()).data!;

  /// `startContactVerification` (primeiro pedido e reenvio no `BottomSheet`).
  Future<OtpRequestResponse> startVerification(OtpRequestData request) async =>
      (await _identity.startContactVerification(
        startContactVerificationRequest: StartContactVerificationRequest(
          (b) => b
            ..type = StartContactVerificationRequestTypeEnum.valueOf(
              request.identifierType,
            )
            ..value = request.identifier
            ..channel = request.channel == null
                ? null
                : StartContactVerificationRequestChannelEnum.valueOf(
                    request.channel!,
                  ),
        ),
      )).data!;

  /// `verifyContact`: o contacto fica confirmado.
  Future<void> verify(String challengeId, String code) async {
    final contacts = (await _identity.verifyContact(
      verifyContactRequest: VerifyContactRequest(
        (b) => b
          ..challengeId = challengeId
          ..code = code,
      ),
    )).data!;
    state = AsyncData(contacts);
    await ref.read(sessionControllerProvider.notifier).refreshMe();
  }

  /// `removeMyContact`; `LAST_CONTACT` chega como erro.
  Future<void> remove(String type) async {
    await _identity.removeMyContact(type: type);
    state = AsyncData((await _identity.listMyContacts()).data!);
    await ref.read(sessionControllerProvider.notifier).refreshMe();
  }
}

final contactsControllerProvider =
    AsyncNotifierProvider.autoDispose<ContactsController, ContactsResponse>(
      ContactsController.new,
      retry: (_, _) => null,
    );
