import 'package:built_collection/built_collection.dart';
import 'package:built_value/serializer.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../../core/api/api_serializers.dart';

/// Estados "em curso" (S4-08 §4): editáveis, em análise ou aceites.
const activeProposalStatuses = [
  ProposalStatus.DRAFT,
  ProposalStatus.INFO_REQUESTED,
  ProposalStatus.UNDER_REVIEW,
  ProposalStatus.ACCEPTED,
];

/// Código de uma pergunta como na API (`SUIT_NEED`, `CLAIMS_LAST_3_YEARS`): o
/// nome Dart dos enums gerados nem sempre é o da API (ex.: `SuitabilityQuestion.NEED`).
String wireName(Object question) =>
    apiSerializers.serialize(question, specifiedType: FullType(question.runtimeType))
        as String;

/// Estados em que o cliente ainda altera a proposta.
bool isEditable(ProposalStatus status) =>
    status == ProposalStatus.DRAFT || status == ProposalStatus.INFO_REQUESTED;

/// Resposta de uma pergunta das declarações (S4-02 §5).
class DeclarationAnswer {
  const DeclarationAnswer({
    required this.questionCode,
    required this.answer,
    this.claimsCount,
    this.details,
  });

  final String questionCode;
  final bool answer;
  final int? claimsCount;
  final String? details;
}

/// Chamadas da API das propostas do cliente (S4-02 a S4-05).
class ProposalRepository {
  const ProposalRepository(this._api);

  final ApiClient _api;

  ProposalApi get _proposal => _api.proposal;

  Future<ProposalResponse> create(String quoteId) async => (await _proposal
          .createProposal(
            createProposalRequest: CreateProposalRequest(
              (b) => b
                ..quoteId = quoteId
                ..channel = ProposalChannel.APP,
            ),
          ))
      .data!;

  Future<ProposalResponse> get(String id) async =>
      (await _proposal.getProposal(id: id)).data!;

  /// As propostas do cliente, a mais recente primeiro.
  Future<List<ProposalSummaryResponse>> listMine({
    List<ProposalStatus>? status,
    int size = 50,
  }) async {
    final page = (await _proposal.listMyProposals(
      status: status == null ? null : BuiltList(status),
      size: size,
      sort: 'createdAt,desc',
    )).data!;
    return page.content.toList();
  }

  Future<ProposalResponse> updateStartDate(
    String id, {
    required Date startDate,
    required int version,
  }) async => (await _proposal.updateProposal(
    id: id,
    updateProposalRequest: UpdateProposalRequest(
      (b) => b
        ..startDate = startDate
        ..version = version,
    ),
  )).data!;

  Future<ProposalResponse> saveDeclarations(
    String id, {
    required List<DeclarationAnswer> answers,
    required bool truthfulnessConfirmed,
    required int version,
  }) async => (await _proposal.saveProposalDeclarations(
    id: id,
    saveDeclarationsRequest: SaveDeclarationsRequest(
      (b) => b
        ..version = version
        ..truthfulnessConfirmed = truthfulnessConfirmed
        ..answers.replace([
          for (final answer in answers)
            DeclarationAnswerRequest(
              (a) => a
                ..questionCode = answer.questionCode
                ..answer = answer.answer
                ..claimsCount = answer.claimsCount
                ..details = answer.details,
            ),
        ]),
    ),
  )).data!;

  Future<ProposalResponse> saveSuitability(
    String id, {
    required Map<String, String> answers,
    required int version,
  }) async => (await _proposal.saveProposalSuitability(
    id: id,
    saveSuitabilityRequest: SaveSuitabilityRequest(
      (b) => b
        ..version = version
        ..answers.replace([
          for (final entry in answers.entries)
            SuitabilityAnswerRequest(
              (a) => a
                ..questionCode = entry.key
                ..answer = entry.value,
            ),
        ]),
    ),
  )).data!;

  Future<ProposalResponse> acknowledgeSuitability(
    String id, {
    required int version,
  }) async => (await _proposal.acknowledgeProposalSuitability(
    id: id,
    acknowledgeSuitabilityRequest: AcknowledgeSuitabilityRequest(
      (b) => b..version = version,
    ),
  )).data!;

  Future<ProposalResponse> requestOnSiteInspection(
    String id, {
    required String notes,
    required int version,
  }) async => (await _proposal.requestOnSiteInspection(
    id: id,
    requestOnSiteInspectionRequest: RequestOnSiteInspectionRequest(
      (b) => b
        ..notes = notes
        ..version = version,
    ),
  )).data!;

  Future<SignatureRequestResponse> requestSignature(
    String id, {
    required int version,
  }) async => (await _proposal.requestProposalSignature(
    id: id,
    requestSignatureRequest: RequestSignatureRequest(
      (b) => b
        ..version = version
        ..documentsAcknowledged = true,
    ),
  )).data!;

  Future<ProposalResponse> confirmSignature(
    String id, {
    required String challengeId,
    required String code,
  }) async => (await _proposal.confirmProposalSignature(
    id: id,
    confirmSignatureRequest: ConfirmSignatureRequest(
      (b) => b
        ..challengeId = challengeId
        ..code = code,
    ),
  )).data!;

  Future<ProposalResponse> submit(String id, {required int version}) async =>
      (await _proposal.submitProposal(
        id: id,
        submitProposalRequest: SubmitProposalRequest(
          (b) => b..version = version,
        ),
      )).data!;
}

final proposalRepositoryProvider = Provider<ProposalRepository>(
  (ref) => ProposalRepository(ref.watch(apiClientProvider)),
);

/// Uma proposta (`getProposal`). As escritas substituem o valor pela resposta
/// ([replace]), para a gravação seguinte levar o `version` novo.
class ProposalController extends AsyncNotifier<ProposalResponse> {
  ProposalController(this.id);

  final String id;

  @override
  Future<ProposalResponse> build() =>
      ref.read(proposalRepositoryProvider).get(id);

  /// Resposta de uma escrita.
  void replace(ProposalResponse proposal) {
    if (!ref.mounted) return;
    state = AsyncData(proposal);
    ref
      ..invalidate(myProposalsProvider)
      ..invalidate(homeProposalsProvider);
  }

  /// Volta a ler (depois de um 409 ou ao puxar para actualizar).
  Future<void> reload() async {
    final repository = ref.read(proposalRepositoryProvider);
    final next = await AsyncValue.guard(() => repository.get(id));
    // O ecrã pode ter saído entretanto.
    if (!ref.mounted) return;
    // Um erro de rede ao recarregar não esconde a proposta já mostrada.
    if (next.hasError && state.hasValue) return;
    state = next;
  }
}

final proposalProvider = AsyncNotifierProvider.autoDispose
    .family<ProposalController, ProposalResponse, String>(
      ProposalController.new,
      retry: (_, _) => null,
    );

/// Até 3 propostas em curso (cartão do início).
final homeProposalsProvider =
    FutureProvider.autoDispose<List<ProposalSummaryResponse>>(
      (ref) => ref
          .read(proposalRepositoryProvider)
          .listMine(status: activeProposalStatuses, size: 3),
      retry: (_, _) => null,
    );

/// As propostas do cliente (lista).
final myProposalsProvider =
    FutureProvider.autoDispose<List<ProposalSummaryResponse>>(
      (ref) => ref.read(proposalRepositoryProvider).listMine(),
      retry: (_, _) => null,
    );
