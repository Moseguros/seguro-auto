import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/api/api_error.dart';
import '../../../core/format/dates.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../../core/clock.dart';
import '../../../core/errors/problem.dart';
import '../proposal_controller.dart';
import '../proposal_summary.dart';
import 'step_host.dart';

/// Dias máximos entre hoje e o início (S3-02 / S4-02).
const maxStartDaysAhead = 60;

/// Passo "Tomador e data" (S4-08 §3): resumo, dados do tomador (do perfil) e a
/// data de início (hoje a hoje + 60 dias, Maputo).
class PolicyholderStep extends ConsumerStatefulWidget {
  const PolicyholderStep({super.key, required this.host});

  final StepHost host;

  @override
  ConsumerState<PolicyholderStep> createState() => _PolicyholderStepState();
}

class _PolicyholderStepState extends ConsumerState<PolicyholderStep>
    with ProposalStepState {
  DateTime? _start;
  String? _error;
  bool _busy = false;

  ProposalResponse get _proposal => widget.host.proposal;

  DateTime get _currentStart => _start ?? proposalStartDate(_proposal);

  Future<void> _pickDate() async {
    final today = todayInMaputo(ref.read(clockProvider)());
    final last = DateTime(
      today.year,
      today.month,
      today.day + maxStartDaysAhead,
    );
    var initial = _currentStart;
    if (initial.isBefore(today)) initial = today;
    if (initial.isAfter(last)) initial = last;
    final picked = await showDatePicker(
      context: context,
      initialDate: initial,
      firstDate: today,
      lastDate: last,
    );
    if (picked != null && mounted) {
      setState(() {
        _start = picked;
        _error = null;
      });
    }
  }

  Future<Problem?> _update(DateTime start) => widget.host.save(
    (version) => ref
        .read(proposalRepositoryProvider)
        .updateStartDate(
          _proposal.id,
          startDate: Date(start.year, start.month, start.day),
          version: version,
        ),
  );

  void _showProblem(Problem? problem) {
    if (problem == null || !mounted) return;
    final l10n = AppLocalizations.of(context);
    final field = problem.fieldError(['startDate']);
    setState(
      () => _error = field != null
          ? fieldErrorMessage(l10n, field.code)
          : problem.message(l10n),
    );
  }

  /// "Actualizar com o perfil": grava a mesma data; o servidor copia o perfil.
  Future<void> _refreshFromProfile() async {
    setState(() => _busy = true);
    final problem = await _update(_currentStart);
    if (mounted) setState(() => _busy = false);
    _showProblem(problem);
  }

  @override
  Future<bool> submit() async {
    final start = _start;
    if (start == null || start == proposalStartDate(_proposal)) return true;
    final problem = await _update(start);
    _showProblem(problem);
    if (problem == null) _start = null;
    return problem == null;
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final start = _currentStart;
    final error = _error;
    final back = Uri(
      path: '/proposals/${_proposal.id}',
      queryParameters: {'step': 'policyholder'},
    ).toString();
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        ProposalSummaryCard(proposal: _proposal),
        const SizedBox(height: 16),
        PolicyholderCard(policyholder: _proposal.policyholder),
        const SizedBox(height: 8),
        Text(l10n.proposalPolicyholderFromProfile),
        Wrap(
          spacing: 8,
          crossAxisAlignment: WrapCrossAlignment.center,
          children: [
            TextButton(
              key: const Key('proposal-edit-profile'),
              style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
              onPressed: () => context.go(
                Uri(
                  path: '/profile',
                  queryParameters: {'from': back},
                ).toString(),
              ),
              child: Text(l10n.proposalEditProfile),
            ),
            OutlinedButton(
              key: const Key('proposal-refresh-profile'),
              style: OutlinedButton.styleFrom(minimumSize: const Size(48, 48)),
              onPressed: _busy ? null : _refreshFromProfile,
              child: Text(l10n.proposalRefreshFromProfile),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Text(l10n.proposalStartDate, style: theme.textTheme.titleSmall),
        const SizedBox(height: 8),
        OutlinedButton.icon(
          key: const Key('proposal-start-date'),
          style: OutlinedButton.styleFrom(
            minimumSize: const Size(64, 48),
            alignment: AlignmentDirectional.centerStart,
          ),
          onPressed: _pickDate,
          icon: const Icon(Icons.event),
          label: Text(formatDate(start)),
        ),
        const SizedBox(height: 4),
        Text(
          l10n.proposalEndDate(formatDate(proposalEndFor(_proposal, start))),
          key: const Key('proposal-end-date'),
        ),
        if (error != null) ...[
          const SizedBox(height: 12),
          MessageBanner(error, key: const Key('policyholder-error')),
        ],
      ],
    );
  }
}
