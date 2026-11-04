import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_error.dart';
import '../../../core/format/dates.dart';
import '../../../l10n/app_localizations.dart';
import '../period.dart';
import '../quote_draft_controller.dart';
import '../quote_labels.dart';
import '../quote_rules.dart';
import '../widgets/step_scaffold.dart';

/// `/quote/period` (passo 4): data de início (hoje a hoje + 60 dias, em
/// `Africa/Maputo`) e, em `TEMPORARY`, a duração.
class PeriodStepScreen extends ConsumerWidget {
  const PeriodStepScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final draft = ref.watch(quoteDraftControllerProvider);
    final controller = ref.read(quoteDraftControllerProvider.notifier);
    final today = ref.watch(quoteTodayProvider)();
    final type = draft.productType;
    final temporary = type == QuoteProductTypes.temporary;

    // Sem escolha, a data de início é hoje.
    final start = draft.startDate ?? today;
    final duration = temporary ? draft.durationDays : oneYearDays;
    final startError =
        startDateError(start, today) ?? draft.fieldError(['startDate']);
    final serverDurationError = draft.fieldError(['durationDays']);
    final valid =
        startDateError(start, today) == null &&
        type != null &&
        serverDurationError == null &&
        durationError(type, duration) == null;

    Future<void> pickDate() async {
      final latest = latestStartDate(today);
      final initial = start.isBefore(today) || start.isAfter(latest)
          ? today
          : start;
      final picked = await showDatePicker(
        context: context,
        initialDate: initial,
        firstDate: today,
        lastDate: latest,
      );
      if (picked != null) controller.setStartDate(picked);
    }

    final children = <Widget>[
      Text(l10n.quoteStartDate, style: theme.textTheme.titleMedium),
      const SizedBox(height: 8),
      InkWell(
        key: const Key('quote-start-date'),
        onTap: pickDate,
        child: InputDecorator(
          decoration: InputDecoration(
            labelText: l10n.quoteStartDate,
            suffixIcon: const Icon(Icons.calendar_today),
            errorText: startError == null
                ? null
                : fieldErrorMessage(l10n, startError),
          ),
          child: Text(formatDate(start)),
        ),
      ),
      const SizedBox(height: 24),
      Text(l10n.quoteDuration, style: theme.textTheme.titleMedium),
      const SizedBox(height: 8),
    ];
    if (temporary) {
      children.add(
        RadioGroup<int>(
          groupValue: duration,
          onChanged: (days) {
            if (days != null) controller.setDuration(days);
          },
          child: Column(
            key: const Key('quote-durations'),
            children: [
              for (final days in temporaryDurations)
                RadioListTile<int>(
                  key: Key('quote-duration-$days'),
                  contentPadding: EdgeInsets.zero,
                  value: days,
                  title: Text(durationLabel(l10n, days)),
                ),
            ],
          ),
        ),
      );
    } else {
      children.add(
        Text(l10n.quoteDurationYear, key: const Key('quote-duration-fixed')),
      );
    }
    if (serverDurationError != null) {
      children.add(
        Text(
          fieldErrorMessage(l10n, serverDurationError),
          style: TextStyle(color: theme.colorScheme.error),
        ),
      );
    }
    if (duration != null) {
      children.addAll([
        const SizedBox(height: 24),
        Text(
          l10n.quoteEndsOn(formatDate(quoteEndDate(start, duration))),
          key: const Key('quote-ends-on'),
          style: theme.textTheme.titleMedium,
        ),
      ]);
    }

    return StepScaffold(
      step: 4,
      title: l10n.quoteStepPeriod,
      onContinue: valid
          ? () {
              controller
                ..setStartDate(start)
                ..setDuration(duration!);
              context.push(QuoteSteps.summary);
            }
          : null,
      children: children,
    );
  }
}
