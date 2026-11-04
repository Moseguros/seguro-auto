import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/api/api_error.dart';
import '../../../core/errors/problem.dart';
import '../../../core/network/connectivity.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../inspection/inspection_repository.dart';
import '../../quote/widgets/quote_document_button.dart';
import '../../vehicles/vehicle_labels.dart';
import '../proposal_controller.dart';
import '../proposal_summary.dart';
import '../sign_sheet.dart';
import '../wizard_steps.dart';
import 'declarations_step.dart';
import 'step_host.dart';
import 'suitability_step.dart';

/// Passo "Resumo" (S4-08 §3, S4-09 §7): o resumo, as respostas, os documentos do
/// produto, o que falta e "Assinar e enviar à seguradora".
class ReviewStep extends ConsumerStatefulWidget {
  const ReviewStep({super.key, required this.host});

  final StepHost host;

  @override
  ConsumerState<ReviewStep> createState() => ReviewStepState();
}

class ReviewStepState extends ConsumerState<ReviewStep> with ProposalStepState {
  bool _acknowledged = false;
  bool _busy = false;
  String? _error;

  ProposalResponse get _proposal => widget.host.proposal;

  /// A proposta já tem assinatura válida: falta só a submissão.
  bool get _signed => _proposal.signature != null;

  @override
  Future<bool> submit() async {
    await _signAndSend();
    return false;
  }

  /// Estado do botão principal (a barra do assistente mostra-o).
  bool canSign(int pendingUploads) =>
      !_busy &&
      pendingUploads == 0 &&
      missingItems(_proposal).isEmpty &&
      (_signed || _acknowledged);

  Future<void> _signAndSend() async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      if (!await ref.read(connectivityMonitorProvider).isOnline()) {
        setState(() => _error = l10n.errorNoConnection);
        return;
      }
      final repository = ref.read(proposalRepositoryProvider);
      if (_signed) {
        await widget.host.save(
          (version) => repository.submit(_proposal.id, version: version),
        );
        return;
      }
      final request = await repository.requestSignature(
        _proposal.id,
        version: _proposal.version,
      );
      if (!mounted) return;
      // A folha tem o seu próprio indicador.
      setState(() => _busy = false);
      final outcome = await showSignSheet(
        context,
        proposal: _proposal,
        request: request,
      );
      if (outcome == null || !mounted) return;
      ref
          .read(proposalProvider(_proposal.id).notifier)
          .replace(outcome.proposal);
      final submitError = outcome.submitError;
      if (submitError != null) {
        setState(() => _error = submitError.message(l10n));
      }
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (!mounted) return;
      setState(
        () => _error = problem.status == 0
            ? l10n.errorNoConnection
            : problem.message(l10n),
      );
      if (problem.code == 'CONCURRENT_MODIFICATION' ||
          problem.code == 'PROPOSAL_NOT_EDITABLE') {
        await widget.host.reload();
      }
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final proposal = _proposal;
    final missing = missingItems(proposal);
    final pending = ref.watch(pendingUploadsProvider(proposal.id)).value ?? 0;
    final usage = vehicleUsageName(l10n, proposal.vehicle.usage.name);
    final error = _error;
    final documents = [
      for (final document in proposal.product.documents)
        if (document.type ==
                ProposalProductDocumentTypeEnum.GENERAL_CONDITIONS ||
            document.type == ProposalProductDocumentTypeEnum.PRE_CONTRACT_INFO)
          document,
    ];

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        ProposalSummaryCard(proposal: proposal, detailed: true),
        const SizedBox(height: 16),
        if (proposal.declarations case final declarations?) ...[
          Text(
            l10n.proposalDeclarationsAnswers,
            style: theme.textTheme.titleSmall,
          ),
          for (final answer in declarations.answers)
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 4),
              child: Text(
                '${declarationQuestion(l10n, wireName(answer.questionCode), usage: usage)} '
                '${answer.answer ? l10n.yes : l10n.no}'
                '${answer.details == null ? '' : ' — ${answer.details}'}',
              ),
            ),
          const SizedBox(height: 16),
        ],
        if (proposal.suitability case final suitability?) ...[
          Text(
            l10n.proposalSuitabilityAnswers,
            style: theme.textTheme.titleSmall,
          ),
          for (final answer in suitability.answers)
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 4),
              child: Text(
                '${suitabilityQuestion(l10n, wireName(answer.questionCode))} '
                '${suitabilityAnswer(l10n, wireName(answer.questionCode), answer.answer)}',
              ),
            ),
          const SizedBox(height: 16),
        ],
        if (documents.isNotEmpty) ...[
          Text(l10n.proposalDocuments, style: theme.textTheme.titleSmall),
          const SizedBox(height: 8),
          for (final document in documents) ...[
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: QuoteDocumentButton(
                fileId: document.fileId,
                label:
                    document.type ==
                        ProposalProductDocumentTypeEnum.GENERAL_CONDITIONS
                    ? l10n.quoteGeneralConditions
                    : l10n.quotePreContractInfo,
                name:
                    document.type ==
                        ProposalProductDocumentTypeEnum.GENERAL_CONDITIONS
                    ? 'condicoes-gerais'
                    : 'informacao-pre-contratual',
              ),
            ),
            const SizedBox(height: 8),
          ],
          const SizedBox(height: 8),
        ],
        if (missing.isNotEmpty) ...[
          MessageBanner(
            l10n.proposalMissingTitle,
            key: const Key('review-missing'),
            kind: MessageKind.info,
          ),
          for (final item in missing)
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: TextButton(
                key: Key('review-missing-$item'),
                style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
                onPressed: () => widget.host.goTo(stepForMissing(item)),
                child: Text(proposalMissingItem(l10n, item)),
              ),
            ),
          const SizedBox(height: 8),
        ],
        if (!_signed)
          CheckboxListTile(
            key: const Key('review-documents-acknowledged'),
            contentPadding: EdgeInsets.zero,
            controlAffinity: ListTileControlAffinity.leading,
            value: _acknowledged,
            onChanged: (value) =>
                setState(() => _acknowledged = value ?? false),
            title: Text(l10n.proposalDocumentsAcknowledged),
          ),
        if (pending > 0) ...[
          const SizedBox(height: 8),
          MessageBanner(
            l10n.proposalWaitUploads,
            key: const Key('review-wait-uploads'),
            kind: MessageKind.info,
          ),
        ],
        if (error != null) ...[
          const SizedBox(height: 8),
          MessageBanner(error, key: const Key('review-error')),
        ],
        const SizedBox(height: 16),
        FilledButton(
          key: const Key('review-sign'),
          style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: canSign(pending) ? _signAndSend : null,
          child: _busy
              ? const SizedBox.square(
                  dimension: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              : Text(
                  _signed ? l10n.proposalSendOnly : l10n.proposalSignAndSend,
                  textAlign: TextAlign.center,
                ),
        ),
      ],
    );
  }
}
