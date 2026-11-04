import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/errors/problem.dart';
import '../../../core/format/dates.dart';
import '../../../core/widgets/cta_button.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../drivers/drivers_controller.dart';
import '../../profile/profile_rules.dart' show ageInYears;
import '../../vehicles/vehicles_controller.dart';
import '../period.dart';
import '../quote_draft.dart';
import '../quote_draft_controller.dart';
import '../quote_labels.dart';
import '../quote_repository.dart';
import '../quote_rules.dart';
import '../widgets/step_scaffold.dart';
import 'type_step_screen.dart' show draftVehicle;

/// Passo onde se corrige um campo do pedido (`errors[].field`).
String? stepForField(String field) {
  if (field.startsWith('vehicleId') || field.startsWith('driverId')) {
    return QuoteSteps.vehicle;
  }
  if (field.startsWith('productType')) return QuoteSteps.type;
  if (field.startsWith('coverages')) return QuoteSteps.coverages;
  if (field.startsWith('startDate') || field.startsWith('durationDays')) {
    return QuoteSteps.period;
  }
  return null;
}

const _stepOrder = [
  QuoteSteps.vehicle,
  QuoteSteps.type,
  QuoteSteps.coverages,
  QuoteSteps.period,
];

/// Minutos de espera de um 429 (pelo menos 1).
int retryMinutes(int seconds) => seconds <= 0 ? 1 : (seconds + 59) ~/ 60;

/// `/quote/summary` (passo 5): os blocos com "Alterar" e o `CtaButton`
/// "Simular seguro".
class SummaryStepScreen extends ConsumerStatefulWidget {
  const SummaryStepScreen({super.key});

  @override
  ConsumerState<SummaryStepScreen> createState() => _SummaryStepScreenState();
}

class _SummaryStepScreenState extends ConsumerState<SummaryStepScreen> {
  bool _sending = false;
  Problem? _problem;

  Future<void> _submit(QuoteDraft draft) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _sending = true;
      _problem = null;
    });
    try {
      final response = await ref
          .read(quoteRepositoryProvider)
          .create(draft.toRequest());
      if (!mounted) return;
      ref.read(quoteDraftControllerProvider.notifier).reset();
      context.go('/quote/results/${response.id}');
    } on Object catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      setState(() => _sending = false);
      switch (problem.code) {
        case 'VEHICLE_VALUE_REQUIRED' ||
            'VEHICLE_NOT_FOUND' ||
            'DRIVER_NOT_FOUND':
          ref
            ..invalidate(vehiclesControllerProvider)
            ..invalidate(driversControllerProvider);
          ScaffoldMessenger.of(context)
            ..hideCurrentSnackBar()
            ..showSnackBar(SnackBar(content: Text(problem.message(l10n))));
          popToStep(context, QuoteSteps.vehicle);
        case 'VALIDATION_ERROR' when problem.errors.isNotEmpty:
          final errors = {
            for (final error in problem.errors) error.field: error.code,
          };
          final steps = {for (final field in errors.keys) ?stepForField(field)};
          final target = _stepOrder.where(steps.contains).firstOrNull;
          if (target == null) {
            setState(() => _problem = problem);
            return;
          }
          ref
              .read(quoteDraftControllerProvider.notifier)
              .setFieldErrors(errors);
          popToStep(context, target);
        default:
          setState(() => _problem = problem);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final draft = ref.watch(quoteDraftControllerProvider);
    final vehicle = draftVehicle(
      ref.watch(vehiclesControllerProvider).value,
      draft.vehicleId,
    );
    DriverResponse? driver;
    for (final d
        in ref.watch(driversControllerProvider).value ??
            const <DriverResponse>[]) {
      if (d.id == draft.driverId) driver = d;
    }
    final start = draft.startDate;
    final duration = draft.durationDays;
    final type = draft.productType;
    final coverages = draft.coverages;
    final complete =
        vehicle != null &&
        driver != null &&
        type != null &&
        coverages != null &&
        start != null &&
        duration != null;

    final problem = _problem;
    final children = <Widget>[
      // No topo, para se ver sem scroll enquanto as seguradoras respondem.
      if (_sending) ...[
        Row(
          key: const Key('quote-requesting'),
          children: [
            const SizedBox.square(
              dimension: 20,
              child: CircularProgressIndicator(strokeWidth: 2),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                l10n.quoteRequesting,
                style: theme.textTheme.bodyLarge,
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
      ],
      if (problem != null) ...[
        MessageBanner(
          problem.message(l10n),
          key: const Key('quote-summary-error'),
        ),
        if (problem.code == 'QUOTE_RATE_LIMITED' &&
            problem.retryAfterSeconds != null) ...[
          const SizedBox(height: 8),
          Text(
            l10n.quoteRetryInMinutes(retryMinutes(problem.retryAfterSeconds!)),
            key: const Key('quote-retry-minutes'),
          ),
        ],
        const SizedBox(height: 16),
      ],
      if (complete) ...[
        _Block(
          title: l10n.quoteVehicleLabel,
          step: QuoteSteps.vehicle,
          lines: [
            vehicle.plate,
            l10n.quoteVehicleLine(vehicle.make, vehicle.model, vehicle.year),
          ],
        ),
        _Block(
          title: l10n.quoteDriverLabel,
          step: QuoteSteps.vehicle,
          lines: [
            driver.fullName,
            l10n.quoteDriverAges(
              ageInYears(driver.birthDate.toDateTime(), start),
              ageInYears(driver.licenceIssueDate.toDateTime(), start),
            ),
          ],
        ),
        _Block(
          title: l10n.quoteStepType,
          step: QuoteSteps.type,
          lines: [productTypeName(l10n, type)],
        ),
        _Block(
          title: l10n.quoteStepCoverages,
          step: QuoteSteps.coverages,
          lines: [
            for (final code in CoverageCodes.all)
              if (coverages.containsKey(code))
                coverages[code] == null
                    ? coverageName(l10n, code)
                    : '${coverageName(l10n, code)} · '
                          '${moneyCents(l10n, coverages[code]!)}',
          ],
        ),
        _Block(
          title: l10n.quoteDetailPeriod,
          step: QuoteSteps.period,
          lines: [
            l10n.quotePeriodRange(
              formatDate(start),
              formatDate(quoteEndDate(start, duration)),
            ),
            durationLabel(l10n, duration),
          ],
        ),
      ] else
        const Center(child: CircularProgressIndicator()),
    ];

    return StepScaffold(
      step: 5,
      title: l10n.quoteStepSummary,
      action: CtaButton(
        key: const Key('quote-submit'),
        label: l10n.ctaQuote,
        onPressed: complete && !_sending ? () => _submit(draft) : null,
      ),
      children: children,
    );
  }
}

class _Block extends StatelessWidget {
  const _Block({required this.title, required this.step, required this.lines});

  final String title;
  final String step;
  final List<String> lines;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(16, 8, 8, 12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(title, style: theme.textTheme.titleMedium),
                ),
                TextButton(
                  key: Key('quote-change-$title'),
                  onPressed: () => popToStep(context, step),
                  child: Text(l10n.quoteChange),
                ),
              ],
            ),
            for (final line in lines) Text(line),
          ],
        ),
      ),
    );
  }
}
