import 'dart:io';
import 'dart:typed_data';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';
import 'package:uuid/uuid.dart';

import '../../core/api/api_client.dart';
import '../../core/files/file_download.dart';

/// Número M-Pesa (Vodacom): 9 dígitos começados por 84 ou 85 (igual ao backend).
final mpesaNational = RegExp(r'^8[45][0-9]{7}$');

/// Qualquer telemóvel moçambicano: 9 dígitos de 82 a 87.
final mozNational = RegExp(r'^8[2-7][0-9]{7}$');

/// `+258841234567` → `841234567`; outro formato → `''`.
String nationalFromMsisdn(String? msisdn) {
  final digits = (msisdn ?? '').replaceAll(RegExp(r'\D'), '');
  if (digits.length == 12 && digits.startsWith('258')) {
    return digits.substring(3);
  }
  return digits.length == 9 ? digits : '';
}

/// 9 dígitos nacionais → E.164 do contrato (`+258841234567`).
String e164(String national) => '+258$national';

/// `REQUIRED`, `MSISDN_NOT_MPESA` ou `null` (válido).
String? validateMpesaMsisdn(String national) {
  if (national.isEmpty) return 'REQUIRED';
  return mpesaNational.hasMatch(national) ? null : 'MSISDN_NOT_MPESA';
}

/// `REQUIRED`, `INVALID_MSISDN` ou `null` (válido).
String? validateMozMsisdn(String national) {
  if (national.isEmpty) return 'REQUIRED';
  return mozNational.hasMatch(national) ? null : 'INVALID_MSISDN';
}

/// Chamadas da API dos pagamentos do cliente (S5-02, S5-11).
class PaymentRepository {
  const PaymentRepository(this._api);

  final ApiClient _api;

  Future<ProposalPaymentMethodsResponse> methods(String proposalId) async =>
      (await _api.proposal.listProposalPaymentMethods(id: proposalId)).data!;

  Future<IntentView> payProposal(
    String proposalId, {
    required String idempotencyKey,
    required String method,
    String? payerMsisdn,
  }) async => (await _api.proposal.createProposalPaymentIntent(
    id: proposalId,
    idempotencyKey: idempotencyKey,
    createProposalPaymentIntentRequest: CreateProposalPaymentIntentRequest(
      (b) => b
        ..method = CreateProposalPaymentIntentRequestMethodEnum.valueOf(method)
        ..channel = ProposalPaymentChannel.APP
        ..payerMsisdn = payerMsisdn,
    ),
  )).data!;

  Future<IntentView> intent(String id) async =>
      (await _api.payment.getPaymentIntent(id: id)).data!;

  Future<PageResponseIntentView> listMine({
    int page = 0,
    int size = 20,
  }) async => (await _api.payment.listMyPayments(
    page: page,
    size: size,
    sort: 'createdAt,desc',
  )).data!;

  /// Envia a entidade e a referência por SMS; devolve o número mascarado.
  Future<String> sendReferenceSms(String intentId, String msisdn) async =>
      (await _api.payment.sendReferenceSms(
        id: intentId,
        referenceSmsRequest: ReferenceSmsRequest((b) => b..msisdn = msisdn),
      )).data!.sentTo;

  /// Gera (uma vez) o PDF da referência e descarrega-o.
  Future<DownloadedFile> referenceDocument(String intentId) async {
    final file = (await _api.payment.createReferenceDocument(
      id: intentId,
    )).data!;
    return downloadFileBytes(
      _api,
      file.fileId,
      fallbackContentType: 'application/pdf',
    );
  }
}

final paymentRepositoryProvider = Provider<PaymentRepository>(
  (ref) => PaymentRepository(ref.watch(apiClientProvider)),
);

/// Formas de pagamento de uma proposta aceite (`listProposalPaymentMethods`).
final paymentMethodsProvider = FutureProvider.autoDispose
    .family<ProposalPaymentMethodsResponse, String>(
      (ref, proposalId) =>
          ref.read(paymentRepositoryProvider).methods(proposalId),
      retry: (_, _) => null,
    );

/// Gerador das chaves de idempotência (os testes fixam-no).
final idempotencyKeyGeneratorProvider = Provider<String Function()>(
  (ref) =>
      () => const Uuid().v4(),
);

/// Partilha um PDF (grava em `getTemporaryDirectory()` e abre a folha de
/// partilha). Os testes substituem-no.
typedef PdfSharer = Future<void> Function(Uint8List bytes, String fileName);

final pdfSharerProvider = Provider<PdfSharer>(
  (ref) => (bytes, fileName) async {
    final directory = await getTemporaryDirectory();
    final file = File('${directory.path}/$fileName');
    await file.writeAsBytes(bytes, flush: true);
    await SharePlus.instance.share(
      ShareParams(files: [XFile(file.path, mimeType: 'application/pdf')]),
    );
  },
);

/// Pagamento de uma proposta (S5-11 §3) com a chave de idempotência: a mesma
/// enquanto o conjunto `(método, número)` não mudar (um pedido que falhou por
/// rede ou *timeout* repete-se com ela); nova quando o conjunto muda ou depois
/// de uma resposta 2xx.
class PaymentController {
  PaymentController(this._repository, this.proposalId, this._newKey);

  final PaymentRepository _repository;
  final String proposalId;
  final String Function() _newKey;

  String? _signature;
  String? _key;

  /// Chave do conjunto `(método, número)`.
  String keyFor(String method, String? payerMsisdn) {
    final signature = '$method|${payerMsisdn ?? ''}';
    if (_signature != signature || _key == null) {
      _signature = signature;
      _key = _newKey();
    }
    return _key!;
  }

  /// Esquece a chave (depois de uma resposta 2xx).
  void reset() {
    _signature = null;
    _key = null;
  }

  Future<IntentView> pay({required String method, String? payerMsisdn}) async {
    final key = keyFor(method, payerMsisdn);
    final intent = await _repository.payProposal(
      proposalId,
      idempotencyKey: key,
      method: method,
      payerMsisdn: payerMsisdn,
    );
    reset();
    return intent;
  }
}

final paymentControllerProvider = Provider.autoDispose
    .family<PaymentController, String>(
      (ref, proposalId) => PaymentController(
        ref.watch(paymentRepositoryProvider),
        proposalId,
        ref.watch(idempotencyKeyGeneratorProvider),
      ),
    );
