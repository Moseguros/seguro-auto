import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/api/api_error.dart';
import '../../../core/errors/problem.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../vehicles/vehicle_labels.dart';
import '../proposal_controller.dart';
import 'step_host.dart';

/// Perguntas das declarações, pela ordem (S4-02 §5, `api/proposal-questions.json`).
const declarationCodes = [
  'CLAIMS_LAST_3_YEARS',
  'PREVIOUS_REFUSAL',
  'USAGE_DIFFERENT',
  'VEHICLE_MODIFIED',
  'NOVICE_DRIVERS',
];

/// Só `CLAIMS_LAST_3_YEARS` pede o número de sinistros.
const claimsCountQuestion = 'CLAIMS_LAST_3_YEARS';

String declarationQuestion(
  AppLocalizations l10n,
  String code, {
  required String usage,
}) => switch (code) {
  'CLAIMS_LAST_3_YEARS' => l10n.declarationClaimsLast3YearsQuestion,
  'PREVIOUS_REFUSAL' => l10n.declarationPreviousRefusalQuestion,
  'USAGE_DIFFERENT' => l10n.declarationUsageDifferentQuestion(usage),
  'VEHICLE_MODIFIED' => l10n.declarationVehicleModifiedQuestion,
  'NOVICE_DRIVERS' => l10n.declarationNoviceDriversQuestion,
  _ => code,
};

String declarationDetailsLabel(AppLocalizations l10n, String code) =>
    switch (code) {
      'CLAIMS_LAST_3_YEARS' => l10n.declarationClaimsLast3YearsDetails,
      'PREVIOUS_REFUSAL' => l10n.declarationPreviousRefusalDetails,
      'USAGE_DIFFERENT' => l10n.declarationUsageDifferentDetails,
      'VEHICLE_MODIFIED' => l10n.declarationVehicleModifiedDetails,
      'NOVICE_DRIVERS' => l10n.declarationNoviceDriversDetails,
      _ => code,
    };

/// Passo "Declarações": 5 perguntas sim/não (sem valor por defeito); com "Sim",
/// os seguimentos são obrigatórios; com "Não", limpam-se. Caixa de veracidade.
class DeclarationsStep extends ConsumerStatefulWidget {
  const DeclarationsStep({super.key, required this.host});

  final StepHost host;

  @override
  ConsumerState<DeclarationsStep> createState() => _DeclarationsStepState();
}

class _DeclarationsStepState extends ConsumerState<DeclarationsStep>
    with ProposalStepState {
  final _form = GlobalKey<FormState>();
  final Map<String, bool?> _answers = {};
  final Map<String, TextEditingController> _details = {};
  final Map<String, TextEditingController> _counts = {};
  final Map<String, String> _serverErrors = {};
  bool _truthful = false;
  bool _showErrors = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    final saved = widget.host.proposal.declarations;
    for (final code in declarationCodes) {
      final answer = saved?.answers
          .where((a) => wireName(a.questionCode) == code)
          .firstOrNull;
      _answers[code] = answer?.answer;
      _details[code] = TextEditingController(text: answer?.details ?? '');
      _counts[code] = TextEditingController(
        text: answer?.claimsCount?.toString() ?? '',
      );
    }
    _truthful = saved != null;
  }

  @override
  void dispose() {
    for (final c in [..._details.values, ..._counts.values]) {
      c.dispose();
    }
    super.dispose();
  }

  void _answer(String code, bool value) => setState(() {
    _answers[code] = value;
    _serverErrors.removeWhere((key, _) => key.startsWith(code));
    if (!value) {
      _details[code]!.clear();
      _counts[code]!.clear();
    }
  });

  @override
  Future<bool> submit() async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _showErrors = true;
      _error = null;
      _serverErrors.clear();
    });
    final formValid = _form.currentState!.validate();
    final allAnswered = _answers.values.every((a) => a != null);
    if (!formValid || !allAnswered) return false;
    if (!_truthful) {
      setState(() => _error = l10n.proposalTruthfulnessRequired);
      return false;
    }
    final answers = [
      for (final code in declarationCodes)
        DeclarationAnswer(
          questionCode: code,
          answer: _answers[code]!,
          claimsCount: _answers[code]! && code == claimsCountQuestion
              ? int.tryParse(_counts[code]!.text)
              : null,
          details: _answers[code]! ? _details[code]!.text.trim() : null,
        ),
    ];
    final problem = await widget.host.save(
      (version) => ref
          .read(proposalRepositoryProvider)
          .saveDeclarations(
            widget.host.proposal.id,
            answers: answers,
            truthfulnessConfirmed: true,
            version: version,
          ),
    );
    if (problem != null && mounted) _showProblem(problem);
    return problem == null;
  }

  /// `answers[n].details` → o campo da pergunta n.
  void _showProblem(Problem problem) {
    final l10n = AppLocalizations.of(context);
    final fieldErrors = <String, String>{};
    for (final error in problem.errors) {
      final match = RegExp(r'^answers\[(\d+)\]\.(\w+)$').firstMatch(error.field);
      if (match == null) continue;
      final index = int.parse(match.group(1)!);
      if (index >= declarationCodes.length) continue;
      fieldErrors['${declarationCodes[index]}.${match.group(2)}'] =
          fieldErrorMessage(l10n, error.code);
    }
    setState(() {
      _serverErrors.addAll(fieldErrors);
      if (fieldErrors.isEmpty) _error = problem.message(l10n);
    });
    _form.currentState?.validate();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final usage = vehicleUsageName(
      l10n,
      widget.host.proposal.vehicle.usage.name,
    );
    final error = _error;
    return Form(
      key: _form,
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          for (final (index, code) in declarationCodes.indexed) ...[
            Text(
              declarationQuestion(l10n, code, usage: usage),
              key: Key('declaration-$code'),
              style: theme.textTheme.titleSmall,
            ),
            RadioGroup<bool>(
              groupValue: _answers[code],
              onChanged: (value) {
                if (value != null) _answer(code, value);
              },
              child: Column(
                children: [
                  RadioListTile<bool>(
                    key: Key('declaration-$code-yes'),
                    contentPadding: EdgeInsets.zero,
                    value: true,
                    title: Text(l10n.yes),
                  ),
                  RadioListTile<bool>(
                    key: Key('declaration-$code-no'),
                    contentPadding: EdgeInsets.zero,
                    value: false,
                    title: Text(l10n.no),
                  ),
                ],
              ),
            ),
            if (_showErrors && _answers[code] == null)
              Text(
                l10n.proposalAnswerRequired,
                key: Key('declaration-$code-required'),
                style: TextStyle(color: theme.colorScheme.error),
              ),
            if (_answers[code] == true) ...[
              if (code == claimsCountQuestion) ...[
                const SizedBox(height: 8),
                TextFormField(
                  key: Key('declaration-$code-count'),
                  controller: _counts[code],
                  keyboardType: TextInputType.number,
                  inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                  decoration: InputDecoration(
                    labelText: l10n.declarationClaimsLast3YearsClaimsCount,
                    border: const OutlineInputBorder(),
                  ),
                  validator: (value) {
                    final server = _serverErrors['$code.claimsCount'];
                    if (server != null) return server;
                    final count = int.tryParse(value ?? '');
                    return count == null || count < 1 || count > 20
                        ? l10n.proposalClaimsCountRange
                        : null;
                  },
                ),
              ],
              const SizedBox(height: 8),
              TextFormField(
                key: Key('declaration-$code-details'),
                controller: _details[code],
                minLines: 2,
                maxLines: 5,
                maxLength: 500,
                decoration: InputDecoration(
                  labelText: declarationDetailsLabel(l10n, code),
                  border: const OutlineInputBorder(),
                ),
                validator: (value) {
                  final server = _serverErrors['$code.details'];
                  if (server != null) return server;
                  final length = value?.trim().length ?? 0;
                  return length < 10 || length > 500
                      ? l10n.proposalDetailsLength
                      : null;
                },
              ),
            ],
            if (index < declarationCodes.length - 1) const Divider(height: 24),
          ],
          const SizedBox(height: 16),
          CheckboxListTile(
            key: const Key('declaration-truthfulness'),
            contentPadding: EdgeInsets.zero,
            controlAffinity: ListTileControlAffinity.leading,
            value: _truthful,
            onChanged: (value) => setState(() {
              _truthful = value ?? false;
              _error = null;
            }),
            title: Text(l10n.declarationTruthfulness),
          ),
          if (error != null) ...[
            const SizedBox(height: 8),
            MessageBanner(error, key: const Key('declarations-error')),
          ],
        ],
      ),
    );
  }
}
