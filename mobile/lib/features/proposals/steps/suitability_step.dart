import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../quote/widgets/step_scaffold.dart' show QuoteSteps;
import '../proposal_controller.dart';
import 'step_host.dart';

/// Perguntas de adequação e respostas possíveis (S4-02 §6).
const suitabilityOptions = <String, List<String>>{
  'SUIT_NEED': ['MANDATORY_ONLY', 'OWN_DAMAGE', 'FULL', 'UNSURE'],
  'SUIT_FINANCED': ['YES', 'NO'],
  'SUIT_CAN_AFFORD_REPAIR': ['YES', 'NO', 'UNSURE'],
  'SUIT_CROSS_BORDER': ['YES', 'NO'],
  'SUIT_DEDUCTIBLE': ['YES', 'NO', 'UNSURE'],
};

String suitabilityQuestion(AppLocalizations l10n, String code) =>
    switch (code) {
      'SUIT_NEED' => l10n.suitabilityNeedQuestion,
      'SUIT_FINANCED' => l10n.suitabilityFinancedQuestion,
      'SUIT_CAN_AFFORD_REPAIR' => l10n.suitabilityCanAffordRepairQuestion,
      'SUIT_CROSS_BORDER' => l10n.suitabilityCrossBorderQuestion,
      'SUIT_DEDUCTIBLE' => l10n.suitabilityDeductibleQuestion,
      _ => code,
    };

/// Texto de uma resposta (as de `SUIT_NEED` são próprias; as outras são
/// Sim/Não/Não sei).
String suitabilityAnswer(
  AppLocalizations l10n,
  String question,
  String option,
) => switch ((question, option)) {
  ('SUIT_NEED', 'MANDATORY_ONLY') => l10n.suitabilityNeedOptionMandatoryOnly,
  ('SUIT_NEED', 'OWN_DAMAGE') => l10n.suitabilityNeedOptionOwnDamage,
  ('SUIT_NEED', 'FULL') => l10n.suitabilityNeedOptionFull,
  ('SUIT_NEED', 'UNSURE') => l10n.suitabilityNeedOptionUnsure,
  (_, 'YES') => l10n.suitabilityAnswerYes,
  (_, 'NO') => l10n.suitabilityAnswerNo,
  (_, 'UNSURE') => l10n.suitabilityAnswerUnsure,
  _ => option,
};

/// Aviso de um motivo de inadequação.
String suitabilityReasonText(AppLocalizations l10n, SuitabilityReason reason) =>
    switch (reason) {
      SuitabilityReason.NEED_NOT_COVERED_OWN_DAMAGE =>
        l10n.suitabilityReasonNeedNotCoveredOwnDamage,
      SuitabilityReason.NEED_NOT_COVERED_THEFT_FIRE =>
        l10n.suitabilityReasonNeedNotCoveredTheftFire,
      SuitabilityReason.COVER_ABOVE_NEED =>
        l10n.suitabilityReasonCoverAboveNeed,
      SuitabilityReason.NEED_UNSURE => l10n.suitabilityReasonNeedUnsure,
      SuitabilityReason.FINANCED_WITHOUT_OWN_DAMAGE =>
        l10n.suitabilityReasonFinancedWithoutOwnDamage,
      SuitabilityReason.CANNOT_AFFORD_REPAIR =>
        l10n.suitabilityReasonCannotAffordRepair,
      SuitabilityReason.CROSS_BORDER_NOT_COVERED =>
        l10n.suitabilityReasonCrossBorderNotCovered,
      _ => l10n.suitabilityReasonDeductibleNotWanted,
    };

/// Passo "Adequação": 5 perguntas; com avisos (`WARNED`), o cliente confirma que
/// os leu ou pede outra cotação. Mudar uma resposta exige confirmar de novo.
class SuitabilityStep extends ConsumerStatefulWidget {
  const SuitabilityStep({super.key, required this.host});

  final StepHost host;

  @override
  ConsumerState<SuitabilityStep> createState() => _SuitabilityStepState();
}

class _SuitabilityStepState extends ConsumerState<SuitabilityStep>
    with ProposalStepState {
  final Map<String, String?> _answers = {};
  bool _showErrors = false;
  bool _busy = false;
  String? _error;

  ProposalSuitability? get _saved => widget.host.proposal.suitability;

  @override
  void initState() {
    super.initState();
    for (final code in suitabilityOptions.keys) {
      _answers[code] = _saved?.answers
          .where((a) => wireName(a.questionCode) == code)
          .firstOrNull
          ?.answer;
    }
  }

  bool get _changed {
    final saved = _saved;
    if (saved == null) return true;
    return suitabilityOptions.keys.any(
      (code) =>
          saved.answers
              .where((a) => wireName(a.questionCode) == code)
              .firstOrNull
              ?.answer !=
          _answers[code],
    );
  }

  /// Avisos por confirmar na proposta [proposal].
  static bool needsAck(ProposalResponse proposal) {
    final saved = proposal.suitability;
    return saved != null &&
        saved.result == SuitabilityResult.WARNED &&
        saved.acknowledgedAt == null;
  }

  @override
  Future<bool> submit() async {
    setState(() {
      _showErrors = true;
      _error = null;
    });
    if (_answers.values.any((a) => a == null)) return false;
    if (_changed) {
      final problem = await widget.host.save(
        (version) => ref
            .read(proposalRepositoryProvider)
            .saveSuitability(
              widget.host.proposal.id,
              answers: {
                for (final entry in _answers.entries) entry.key: entry.value!,
              },
              version: version,
            ),
      );
      if (problem != null) {
        if (mounted) {
          setState(
            () => _error = problem.message(AppLocalizations.of(context)),
          );
        }
        return false;
      }
    }
    // Com avisos por confirmar fica no passo (os avisos aparecem por baixo).
    // A resposta da gravação já está no provider (o `widget` ainda é o antigo).
    final current =
        ref.read(proposalProvider(widget.host.proposal.id)).value ??
        widget.host.proposal;
    return !needsAck(current);
  }

  Future<void> _acknowledge() async {
    setState(() => _busy = true);
    final problem = await widget.host.save(
      (version) => ref
          .read(proposalRepositoryProvider)
          .acknowledgeSuitability(widget.host.proposal.id, version: version),
    );
    if (!mounted) return;
    setState(() {
      _busy = false;
      _error = problem?.message(AppLocalizations.of(context));
    });
    if (problem == null) widget.host.advance();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final saved = _saved;
    final error = _error;
    final showWarnings =
        saved != null && saved.result == SuitabilityResult.WARNED && !_changed;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text(l10n.suitabilityIntro),
        const SizedBox(height: 16),
        for (final entry in suitabilityOptions.entries) ...[
          Text(
            suitabilityQuestion(l10n, entry.key),
            key: Key('suitability-${entry.key}'),
            style: theme.textTheme.titleSmall,
          ),
          RadioGroup<String>(
            groupValue: _answers[entry.key],
            onChanged: (value) => setState(() => _answers[entry.key] = value),
            child: Column(
              children: [
                for (final option in entry.value)
                  RadioListTile<String>(
                    key: Key('suitability-${entry.key}-$option'),
                    contentPadding: EdgeInsets.zero,
                    value: option,
                    title: Text(suitabilityAnswer(l10n, entry.key, option)),
                  ),
              ],
            ),
          ),
          if (_showErrors && _answers[entry.key] == null)
            Text(
              l10n.proposalAnswerRequired,
              style: TextStyle(color: theme.colorScheme.error),
            ),
          const Divider(height: 24),
        ],
        if (showWarnings) ...[
          for (final reason in saved.reasons) ...[
            Container(
              key: Key('suitability-warning-${reason.name}'),
              child: _Warning(suitabilityReasonText(l10n, reason)),
            ),
            const SizedBox(height: 8),
          ],
          if (saved.acknowledgedAt != null)
            MessageBanner(
              l10n.suitabilityAcknowledged,
              key: const Key('suitability-acknowledged'),
              kind: MessageKind.success,
            )
          else ...[
            const SizedBox(height: 8),
            FilledButton(
              key: const Key('suitability-acknowledge'),
              style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: _busy ? null : _acknowledge,
              child: Text(
                l10n.suitabilityAcknowledge,
                textAlign: TextAlign.center,
              ),
            ),
            const SizedBox(height: 8),
            OutlinedButton(
              key: const Key('suitability-other-quote'),
              style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: _busy ? null : () => context.push(QuoteSteps.vehicle),
              child: Text(l10n.suitabilityRequestOtherQuote),
            ),
          ],
        ],
        if (error != null) ...[
          const SizedBox(height: 8),
          MessageBanner(error, key: const Key('suitability-error')),
        ],
      ],
    );
  }
}

/// Aviso de adequação (cor de aviso, um por motivo).
class _Warning extends StatelessWidget {
  const _Warning(this.text);

  final String text;

  @override
  Widget build(BuildContext context) {
    final color = Colors.orange.shade800;
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.08),
        border: Border.all(color: color.withValues(alpha: 0.6)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(Icons.warning_amber_rounded, color: color, size: 20),
          const SizedBox(width: 12),
          Expanded(child: Text(text)),
        ],
      ),
    );
  }
}
