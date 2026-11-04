import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../../core/network/connectivity.dart';
import '../../l10n/app_localizations.dart';
import '../proposals/proposal_controller.dart';

/// O cliente não tem rede: o KYC não usa a fila offline (S4-09 §6).
class NoConnectionException implements Exception {
  const NoConnectionException();
}

/// Fotografias do KYC, já preparadas (JPEG, sem EXIF).
class KycPhotos {
  const KycPhotos({this.docFront, this.docBack, this.selfie});

  final Uint8List? docFront;
  final Uint8List? docBack;
  final Uint8List? selfie;

  /// Prontas a enviar (o verso não se pede para passaporte).
  bool isComplete({required bool needsBack}) =>
      docFront != null && selfie != null && (!needsBack || docBack != null);
}

/// O passaporte não tem verso.
bool kycNeedsBack(String? idDocType) => idDocType != 'PASSPORT';

/// `getMyKyc`: estado e última verificação.
final myKycProvider = FutureProvider.autoDispose<MyKycResponse>(
  (ref) async => (await ref.read(apiClientProvider).identity.getMyKyc()).data!,
  retry: (_, _) => null,
);

/// Seguradora a quem se envia o KYC fora de uma proposta (`/identity`): a da
/// proposta editável ou em análise mais recente; `null` sem nenhuma (S4-08 §3).
final kycInsurerProvider = FutureProvider.autoDispose<String?>((ref) async {
  final proposals = await ref
      .read(proposalRepositoryProvider)
      .listMine(
        status: const [
          ProposalStatus.DRAFT,
          ProposalStatus.INFO_REQUESTED,
          ProposalStatus.UNDER_REVIEW,
        ],
        size: 1,
      );
  return proposals.firstOrNull?.insurer?.id;
}, retry: (_, _) => null);

/// Tipo de documento do perfil (`BI`, `PASSPORT`, `DIRE`).
final kycIdDocTypeProvider = FutureProvider.autoDispose<String?>(
  (ref) async => (await ref.read(apiClientProvider).identity.getMyProfile())
      .data
      ?.idDocType
      ?.name,
  retry: (_, _) => null,
);

/// Envia o KYC (`submitMyKyc`). Sem rede lança [NoConnectionException] sem pedido.
class KycSubmitter {
  const KycSubmitter(this._ref);

  final Ref _ref;

  Future<KycCheckResponse> submit({
    required String insurerId,
    required KycPhotos photos,
  }) async {
    if (!await _ref.read(connectivityMonitorProvider).isOnline()) {
      throw const NoConnectionException();
    }
    MultipartFile part(Uint8List bytes, String name) => MultipartFile.fromBytes(
      bytes,
      filename: '$name.jpg',
      contentType: DioMediaType('image', 'jpeg'),
    );
    final response = await _ref
        .read(apiClientProvider)
        .identity
        .submitMyKyc(
          insurerId: insurerId,
          docFront: part(photos.docFront!, 'doc-front'),
          docBack: photos.docBack == null
              ? null
              : part(photos.docBack!, 'doc-back'),
          selfie: part(photos.selfie!, 'selfie'),
        );
    _ref.invalidate(myKycProvider);
    return response.data!;
  }
}

final kycSubmitterProvider = Provider<KycSubmitter>(KycSubmitter.new);

/// Motivo de uma recusa do KYC.
String kycRejectionText(AppLocalizations l10n, KycCheckResponse? check) {
  final code = check?.rejectionReasonCode;
  final reason = check?.rejectionReason;
  if (code == KycRejectionReason.OTHER && reason != null && reason.isNotEmpty) {
    return reason;
  }
  return switch (code) {
    KycRejectionReason.DOCUMENT_UNREADABLE => l10n.kycReasonDocumentUnreadable,
    KycRejectionReason.DOCUMENT_EXPIRED => l10n.kycReasonDocumentExpired,
    KycRejectionReason.DATA_MISMATCH => l10n.kycReasonDataMismatch,
    KycRejectionReason.SELFIE_MISMATCH => l10n.kycReasonSelfieMismatch,
    _ => reason ?? l10n.kycReasonOther,
  };
}
