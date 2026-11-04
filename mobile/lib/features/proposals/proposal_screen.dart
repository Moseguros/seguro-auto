import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../inspection/inspection_repository.dart';
import '../inspection/inspection_screen.dart';
import '../inspection/inspection_slots.dart';
import 'info_request_banner.dart';
import 'proposal_controller.dart';
import 'proposal_status_view.dart';
import 'steps/declarations_step.dart';
import 'steps/identity_step.dart';
import 'steps/policyholder_step.dart';
import 'steps/review_step.dart';
import 'steps/step_host.dart';
import 'steps/suitability_step.dart';
import 'wizard_steps.dart';

/// `/proposals/:id` (`?step=`): os passos, se a proposta for editável; senão o
/// estado (S4-08 §3 e §5).
class ProposalScreen extends ConsumerWidget {
  const ProposalScreen({super.key, required this.proposalId, this.step});

  final String proposalId;

  /// Passo pedido (`?step=`).
  final String? step;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final provider = proposalProvider(proposalId);
    final proposal = ref.watch(provider);
    final value = proposal.value;
    Future<void> reload() => ref.read(provider.notifier).reload();

    if (value != null && isEditable(value.status)) {
      return ProposalWizard(
        // Um passo pedido de novo (ligação do resumo) recria o assistente.
        key: ValueKey('wizard-$step'),
        proposal: value,
        initialStep: step,
      );
    }
    final Widget body;
    if (value != null) {
      body = RefreshIndicator(
        onRefresh: reload,
        child: ProposalStatusView(proposal: value),
      );
    } else if (proposal.hasError) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(Problem.from(proposal.error!).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(onPressed: reload, child: Text(l10n.retry)),
        ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(
        title: Text(
          value == null
              ? l10n.proposalsTitle
              : l10n.proposalTitle(value.number),
        ),
      ),
      body: SafeArea(child: body),
    );
  }
}

/// Os passos num `PageView` sem deslizar à mão (S4-09 §2), com a barra de
/// progresso e "Passo n de N · {nome}" no topo e "Voltar"/"Continuar" fixos no
/// fundo.
class ProposalWizard extends ConsumerStatefulWidget {
  const ProposalWizard({super.key, required this.proposal, this.initialStep});

  final ProposalResponse proposal;
  final String? initialStep;

  @override
  ConsumerState<ProposalWizard> createState() => _ProposalWizardState();
}

class _ProposalWizardState extends ConsumerState<ProposalWizard> {
  late final PageController _pages;
  late ProposalStep _step;
  bool _busy = false;
  final _keys = <ProposalStep, GlobalKey<ProposalStepState>>{
    for (final step in ProposalStep.values) step: GlobalKey(),
  };

  ProposalResponse get _proposal => widget.proposal;

  List<ProposalStep> get _steps => visibleSteps(_proposal);

  @override
  void initState() {
    super.initState();
    _step = initialStep(_proposal, widget.initialStep);
    _pages = PageController(initialPage: _steps.indexOf(_step));
  }

  @override
  void dispose() {
    _pages.dispose();
    super.dispose();
  }

  void _goTo(ProposalStep step) {
    final index = _steps.indexOf(step);
    if (index < 0) return;
    FocusScope.of(context).unfocus();
    setState(() => _step = step);
    _pages.jumpToPage(index);
  }

  void _advance() {
    final index = _steps.indexOf(_step);
    if (index >= 0 && index < _steps.length - 1) _goTo(_steps[index + 1]);
  }

  void _back() {
    final index = _steps.indexOf(_step);
    if (index > 0) {
      _goTo(_steps[index - 1]);
    } else if (context.canPop()) {
      context.pop();
    } else {
      context.go('/proposals');
    }
  }

  Future<void> _reload() =>
      ref.read(proposalProvider(_proposal.id).notifier).reload();

  Future<Problem?> _save(
    Future<ProposalResponse> Function(int version) action,
  ) async {
    final l10n = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    try {
      final updated = await action(_proposal.version);
      ref.read(proposalProvider(_proposal.id).notifier).replace(updated);
      return null;
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (problem.code == 'CONCURRENT_MODIFICATION') {
        messenger
          ..hideCurrentSnackBar()
          ..showSnackBar(
            SnackBar(content: Text(l10n.proposalChangedElsewhere)),
          );
        await _reload();
      } else if (problem.code == 'PROPOSAL_NOT_EDITABLE') {
        // O ecrã passa ao estado.
        await _reload();
      }
      return problem;
    }
  }

  Future<void> _continue() async {
    final state = _keys[_step]?.currentState;
    setState(() => _busy = true);
    try {
      final ok = state == null ? true : await state.submit();
      if (ok && mounted) _advance();
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  StepHost get _host => StepHost(
    proposal: _proposal,
    save: _save,
    goTo: _goTo,
    advance: _advance,
    reload: _reload,
  );

  Widget _page(ProposalStep step) {
    final key = _keys[step];
    return switch (step) {
      ProposalStep.policyholder => PolicyholderStep(key: key, host: _host),
      ProposalStep.declarations => DeclarationsStep(key: key, host: _host),
      ProposalStep.suitability => SuitabilityStep(key: key, host: _host),
      ProposalStep.inspection => _InspectionStepView(proposal: _proposal),
      ProposalStep.identity => IdentityStep(
        key: key,
        host: _host,
        onSkip: _advance,
      ),
      ProposalStep.review => ReviewStep(key: key, host: _host),
    };
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final steps = _steps;
    final index = steps.indexOf(_step);
    final infoRequest = _proposal.status == ProposalStatus.INFO_REQUESTED
        ? _proposal.decision?.infoRequest
        : null;
    final isReview = _step == ProposalStep.review;

    return Scaffold(
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              l10n.proposalTitle(_proposal.number),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
            Text(
              l10n.proposalStepOf(
                index + 1,
                steps.length,
                stepName(l10n, _step),
              ),
              key: const Key('proposal-step-of'),
              style: theme.textTheme.bodySmall,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
          ],
        ),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(4),
          child: LinearProgressIndicator(
            key: const Key('proposal-progress'),
            value: (index + 1) / steps.length,
          ),
        ),
      ),
      body: SafeArea(
        child: Column(
          children: [
            if (infoRequest != null)
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
                child: InfoRequestBanner(
                  request: infoRequest,
                  onOpenStep: _goTo,
                ),
              ),
            Expanded(
              child: PageView(
                controller: _pages,
                physics: const NeverScrollableScrollPhysics(),
                children: [
                  for (final step in steps)
                    KeyedSubtree(key: ValueKey(step), child: _page(step)),
                ],
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: SafeArea(
        child: Material(
          elevation: 3,
          color: theme.colorScheme.surface,
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
            child: Row(
              children: [
                TextButton(
                  key: const Key('proposal-back'),
                  style: TextButton.styleFrom(minimumSize: const Size(64, 48)),
                  onPressed: _busy ? null : _back,
                  child: Text(l10n.proposalBack),
                ),
                const SizedBox(width: 8),
                if (!isReview)
                  Expanded(
                    child: FilledButton(
                      key: const Key('proposal-continue'),
                      style: FilledButton.styleFrom(
                        minimumSize: const Size(64, 48),
                      ),
                      onPressed: _busy ? null : _continue,
                      child: Text(l10n.proposalNext),
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

/// Passo "Vistoria" dentro dos passos: estado resumido e "Fazer a vistoria",
/// que abre `/proposals/:id/inspection` (S4-09 §3).
class _InspectionStepView extends ConsumerWidget {
  const _InspectionStepView({required this.proposal});

  final ProposalResponse proposal;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final uploads =
        ref.watch(inspectionUploadsProvider(proposal.id)).value ?? const [];
    final pending = ref.watch(pendingUploadsProvider(proposal.id)).value ?? 0;
    final views = slotViews(proposal.inspection, uploads);
    final done = views.where((v) => v.state != SlotState.toTake).length;
    final inspection = proposal.inspection;
    final status = switch (inspection.status) {
      InspectionStatus.SUBMITTED => l10n.inspectionStatusSubmitted,
      InspectionStatus.APPROVED => l10n.inspectionStatusApproved,
      InspectionStatus.REJECTED => l10n.inspectionStatusRejected,
      _ => l10n.inspectionStatusPending,
    };
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text(l10n.inspectionIntro),
        const SizedBox(height: 16),
        Card(
          child: ListTile(
            leading: const Icon(Icons.directions_car_outlined),
            title: Text(status, key: const Key('inspection-step-status')),
            subtitle: Text(
              inspection.mode == InspectionMode.ON_SITE
                  ? l10n.inspectionOnSiteRequested
                  : l10n.inspectionProgress(done, requiredSlots.length),
            ),
          ),
        ),
        if (pending > 0) ...[
          const SizedBox(height: 8),
          MessageBanner(
            l10n.inspectionPendingUploads(pending),
            kind: MessageKind.info,
          ),
        ],
        const SizedBox(height: 16),
        FilledButton.icon(
          key: const Key('inspection-step-open'),
          style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: () => context.push('/proposals/${proposal.id}/inspection'),
          icon: const Icon(Icons.photo_camera_outlined),
          label: Text(l10n.inspectionOpen),
        ),
      ],
    );
  }
}
