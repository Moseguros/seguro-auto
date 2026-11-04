import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/api/api_error.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../vehicles/vehicles_controller.dart';
import '../coverage_options.dart';
import '../quote_draft.dart';
import '../quote_draft_controller.dart';
import '../quote_labels.dart';
import '../quote_repository.dart';
import '../quote_rules.dart';
import '../widgets/quote_load_error.dart';
import '../widgets/step_scaffold.dart';
import 'type_step_screen.dart' show draftVehicle;

/// Coberturas e capitais em vigor: as do rascunho ou, ainda sem escolha, as por
/// defeito (obrigatórias, mais choque, incêndio e fenómenos da natureza em
/// `COMPREHENSIVE`, com o menor capital nas `OPTIONS`).
Map<String, int?> effectiveCoverages(
  QuoteDraft draft,
  CoverageOptions options,
) {
  final chosen = draft.coverages;
  final checked = chosen == null
      ? defaultCoverages(draft.productType, options.optionalCodes).toSet()
      : chosen.keys.where(options.optionalCodes.contains).toSet();
  int? capitalFor(CoverageOption option) {
    if (!option.hasCapitalChoice) return null;
    final value = chosen?[option.code];
    return value != null && option.capitalOptions.contains(value)
        ? value
        : option.defaultCapital;
  }

  return {
    for (final option in options.mandatory) option.code: capitalFor(option),
    for (final option in options.optional)
      if (checked.contains(option.code)) option.code: capitalFor(option),
  };
}

/// `/quote/coverages` (passo 3).
class CoveragesStepScreen extends ConsumerWidget {
  const CoveragesStepScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final draft = ref.watch(quoteDraftControllerProvider);
    final controller = ref.read(quoteDraftControllerProvider.notifier);
    final vehicles = ref.watch(vehiclesControllerProvider);
    final vehicle = draftVehicle(vehicles.value, draft.vehicleId);
    final type = draft.productType;

    final children = <Widget>[];
    VoidCallback? onContinue;
    if (vehicles.hasError) {
      children.add(
        QuoteLoadError(
          error: vehicles.error!,
          onRetry: () => ref.invalidate(vehiclesControllerProvider),
        ),
      );
    } else if (vehicle == null || type == null) {
      children.add(const Center(child: CircularProgressIndicator()));
    } else {
      final key = (category: vehicle.category.name, usage: vehicle.usage.name);
      final products = ref.watch(catalogProductsProvider(key));
      final list = products.value;
      if (products.hasError) {
        children.add(
          QuoteLoadError(
            error: products.error!,
            onRetry: () => ref.invalidate(catalogProductsProvider(key)),
          ),
        );
      } else if (list == null) {
        children.add(const Center(child: CircularProgressIndicator()));
      } else {
        final options = buildCoverageOptions(list, type);
        if (options.isEmpty) {
          children.add(
            MessageBanner(
              l10n.quoteNoProducts,
              key: const Key('quote-no-products'),
              kind: MessageKind.info,
            ),
          );
        } else {
          final current = effectiveCoverages(draft, options);
          final ruleError = coveragesError(type, current.keys.toList());
          final serverError = draft.fieldError(['coverages']);
          if (ruleError == null) {
            onContinue = () {
              controller.setCoverages(current);
              context.push(QuoteSteps.period);
            };
          }
          children.add(
            _CoverageList(
              options: options,
              current: current,
              onChanged: controller.setCoverages,
            ),
          );
          if (current.containsKey(CoverageCodes.odTheft)) {
            children.addAll([
              const SizedBox(height: 8),
              MessageBanner(
                l10n.quoteTheftNotice,
                key: const Key('quote-theft-notice'),
                kind: MessageKind.info,
              ),
            ]);
          }
          final error = ruleError ?? serverError;
          if (error != null) {
            children.addAll([
              const SizedBox(height: 8),
              MessageBanner(
                fieldErrorMessage(l10n, error),
                key: const Key('quote-coverages-error'),
              ),
            ]);
          }
        }
      }
    }

    return StepScaffold(
      step: 3,
      title: l10n.quoteStepCoverages,
      onContinue: onContinue,
      children: children,
    );
  }
}

class _CoverageList extends StatelessWidget {
  const _CoverageList({
    required this.options,
    required this.current,
    required this.onChanged,
  });

  final CoverageOptions options;
  final Map<String, int?> current;
  final ValueChanged<Map<String, int?>> onChanged;

  void _toggle(String code, bool checked) {
    final codes = toggleCoverage(current.keys.toSet(), code, checked: checked);
    onChanged({
      for (final option in options.all)
        if (codes.contains(option.code))
          option.code: current[option.code] ?? option.defaultCapital,
    });
  }

  void _capital(String code, int? cents) =>
      onChanged({...current, code: cents});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final muted = theme.textTheme.bodySmall?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    Widget tile(CoverageOption option) {
      final checked = option.mandatory || current.containsKey(option.code);
      final capital = option.capital;
      return Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          CheckboxListTile(
            key: Key('quote-coverage-${option.code}'),
            contentPadding: EdgeInsets.zero,
            controlAffinity: ListTileControlAffinity.leading,
            value: checked,
            onChanged: option.mandatory
                ? null
                : (value) => _toggle(option.code, value ?? false),
            title: Text(coverageName(l10n, option.code)),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  option.mandatory
                      ? l10n.quoteCoverageMandatory
                      : l10n.quoteCoverageAvailableIn(
                          option.availableIn,
                          option.total,
                        ),
                  style: muted,
                ),
                if (option.capitalType == CapitalTypes.perPerson &&
                    capital != null)
                  Text(
                    l10n.quoteCapitalPerPerson(moneyCents(l10n, capital)),
                    style: muted,
                  ),
              ],
            ),
          ),
          if (checked && option.hasCapitalChoice)
            Padding(
              padding: const EdgeInsetsDirectional.only(start: 40, bottom: 8),
              child: DropdownButtonFormField<int>(
                key: ValueKey(
                  'quote-capital-${option.code}-${current[option.code]}',
                ),
                isExpanded: true,
                initialValue: current[option.code] ?? option.defaultCapital,
                decoration: InputDecoration(
                  labelText: l10n.quoteCoverageCapital,
                ),
                items: [
                  for (final cents in option.capitalOptions)
                    DropdownMenuItem(
                      value: cents,
                      child: Text(moneyCents(l10n, cents)),
                    ),
                ],
                onChanged: (cents) => _capital(option.code, cents),
              ),
            ),
        ],
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        for (final option in options.mandatory) tile(option),
        for (final option in options.optional) tile(option),
      ],
    );
  }
}
