import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';

/// Passos da proposta (S4-08 §3), pela ordem. O nome é o valor de `?step=`.
enum ProposalStep {
  policyholder,
  declarations,
  suitability,
  inspection,
  identity,
  review;

  static ProposalStep? fromName(String? name) {
    for (final step in values) {
      if (step.name == name) return step;
    }
    return null;
  }
}

/// Passos mostrados: a vistoria só quando `inspection.required`.
List<ProposalStep> visibleSteps(ProposalResponse proposal) => [
  for (final step in ProposalStep.values)
    if (step != ProposalStep.inspection || proposal.inspection.required_) step,
];

/// Passo concluído (S4-08 §3, coluna "Concluído quando").
///
/// [identitySkipped]: o cliente escolheu "Agora não" na identidade.
bool isStepDone(
  ProposalStep step,
  ProposalResponse proposal, {
  bool identitySkipped = false,
}) {
  final checklist = proposal.checklist;
  return switch (step) {
    ProposalStep.policyholder => true,
    ProposalStep.declarations =>
      checklist.declarations == ProposalChecklistDeclarationsEnum.DONE,
    ProposalStep.suitability =>
      checklist.suitability == ProposalChecklistSuitabilityEnum.DONE,
    ProposalStep.inspection =>
      checklist.inspection == ProposalChecklistInspectionEnum.DONE ||
          checklist.inspection == ProposalChecklistInspectionEnum.NOT_REQUIRED,
    ProposalStep.identity =>
      identitySkipped ||
          proposal.kyc.state == KycState.VERIFIED ||
          proposal.kyc.state == KycState.PENDING,
    ProposalStep.review => false,
  };
}

/// Primeiro passo por concluir (abre por defeito). O tomador é só revisão: conta
/// como concluído, por isso uma proposta nova abre nas declarações.
ProposalStep firstPendingStep(
  ProposalResponse proposal, {
  bool identitySkipped = false,
}) {
  for (final step in visibleSteps(proposal)) {
    if (step == ProposalStep.policyholder) continue;
    if (!isStepDone(step, proposal, identitySkipped: identitySkipped)) {
      return step;
    }
  }
  return ProposalStep.review;
}

/// Passo pedido em `?step=`, se for visível; senão o primeiro por concluir.
ProposalStep initialStep(
  ProposalResponse proposal,
  String? requested, {
  bool identitySkipped = false,
}) {
  final step = ProposalStep.fromName(requested);
  if (step != null && visibleSteps(proposal).contains(step)) return step;
  return firstPendingStep(proposal, identitySkipped: identitySkipped);
}

/// Passo de um item de um pedido de informação (S4-08 §3).
ProposalStep stepForInfoItem(InfoRequestItem item) => switch (item) {
  InfoRequestItem.DECLARATIONS => ProposalStep.declarations,
  InfoRequestItem.SUITABILITY => ProposalStep.suitability,
  InfoRequestItem.INSPECTION => ProposalStep.inspection,
  InfoRequestItem.KYC => ProposalStep.identity,
  InfoRequestItem.POLICYHOLDER => ProposalStep.policyholder,
  _ => ProposalStep.review,
};

/// Passo de um item em falta (`PROPOSAL_INCOMPLETE.missing`, S4-04 §10).
ProposalStep stepForMissing(String item) => switch (item) {
  'DECLARATIONS' => ProposalStep.declarations,
  'SUITABILITY' || 'SUITABILITY_ACKNOWLEDGEMENT' => ProposalStep.suitability,
  'INSPECTION' => ProposalStep.inspection,
  'START_DATE' => ProposalStep.policyholder,
  _ => ProposalStep.review,
};

/// Itens por fazer a partir do `checklist` (a lista do resumo).
List<String> missingItems(ProposalResponse proposal) {
  final checklist = proposal.checklist;
  return [
    if (checklist.declarations != ProposalChecklistDeclarationsEnum.DONE)
      'DECLARATIONS',
    if (checklist.suitability == ProposalChecklistSuitabilityEnum.MISSING)
      'SUITABILITY',
    if (checklist.suitability == ProposalChecklistSuitabilityEnum.ACK_REQUIRED)
      'SUITABILITY_ACKNOWLEDGEMENT',
    if (checklist.inspection == ProposalChecklistInspectionEnum.MISSING ||
        checklist.inspection == ProposalChecklistInspectionEnum.REJECTED)
      'INSPECTION',
  ];
}

/// Nome visível de um passo.
String stepName(AppLocalizations l10n, ProposalStep step) => switch (step) {
  ProposalStep.policyholder => l10n.proposalStepPolicyholder,
  ProposalStep.declarations => l10n.proposalStepDeclarations,
  ProposalStep.suitability => l10n.proposalStepSuitability,
  ProposalStep.inspection => l10n.proposalStepInspection,
  ProposalStep.identity => l10n.proposalStepIdentity,
  ProposalStep.review => l10n.proposalStepReview,
};
