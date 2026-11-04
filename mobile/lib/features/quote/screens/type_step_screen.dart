import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/api/api_error.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../vehicles/vehicles_controller.dart';
import '../quote_draft_controller.dart';
import '../quote_rules.dart';
import '../widgets/quote_load_error.dart';
import '../widgets/step_scaffold.dart';

/// Veículo escolhido no rascunho, se ainda existir.
VehicleResponse? draftVehicle(List<VehicleResponse>? vehicles, String? id) {
  for (final vehicle in vehicles ?? const <VehicleResponse>[]) {
    if (vehicle.id == id) return vehicle;
  }
  return null;
}

/// `/quote/type` (passo 2): três cartões de escolha única.
class TypeStepScreen extends ConsumerWidget {
  const TypeStepScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final draft = ref.watch(quoteDraftControllerProvider);
    final controller = ref.read(quoteDraftControllerProvider.notifier);
    final vehicles = ref.watch(vehiclesControllerProvider);
    final vehicle = draftVehicle(vehicles.value, draft.vehicleId);
    final hasValue = vehicle?.commercialValue != null;

    final children = <Widget>[];
    final fieldError = draft.fieldError(['productType']);
    if (fieldError != null) {
      children.addAll([
        MessageBanner(fieldErrorMessage(l10n, fieldError)),
        const SizedBox(height: 16),
      ]);
    }
    if (vehicles.hasError) {
      children.add(
        QuoteLoadError(
          error: vehicles.error!,
          onRetry: () => ref.invalidate(vehiclesControllerProvider),
        ),
      );
    } else if (vehicle == null) {
      children.add(const Center(child: CircularProgressIndicator()));
    } else {
      for (final (type, title, text) in [
        (
          QuoteProductTypes.mtplOnly,
          l10n.quoteTypeMtplOnly,
          l10n.quoteTypeMtplOnlyText,
        ),
        (
          QuoteProductTypes.comprehensive,
          l10n.quoteTypeComprehensive,
          l10n.quoteTypeComprehensiveText,
        ),
        (
          QuoteProductTypes.temporary,
          l10n.quoteTypeTemporary,
          l10n.quoteTypeTemporaryText,
        ),
      ]) {
        final enabled = typeAllowedForVehicle(
          type,
          hasCommercialValue: hasValue,
        );
        children.add(
          _TypeCard(
            type: type,
            title: title,
            text: text,
            selected: draft.productType == type,
            enabled: enabled,
            onTap: () => controller.selectType(type),
            footer: enabled
                ? null
                : _NeedsValue(
                    onSetValue: () async {
                      await context.push('/vehicles/${vehicle.id}/edit');
                      ref.invalidate(vehiclesControllerProvider);
                    },
                  ),
          ),
        );
      }
    }

    final type = draft.productType;
    final valid =
        vehicle != null &&
        type != null &&
        productTypeError(type) == null &&
        typeAllowedForVehicle(type, hasCommercialValue: hasValue);
    return StepScaffold(
      step: 2,
      title: l10n.quoteStepType,
      onContinue: valid ? () => context.push(QuoteSteps.coverages) : null,
      children: children,
    );
  }
}

class _TypeCard extends StatelessWidget {
  const _TypeCard({
    required this.type,
    required this.title,
    required this.text,
    required this.selected,
    required this.enabled,
    required this.onTap,
    this.footer,
  });

  final String type;
  final String title;
  final String text;
  final bool selected;
  final bool enabled;
  final VoidCallback onTap;
  final Widget? footer;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final scheme = theme.colorScheme;
    final muted = scheme.onSurfaceVariant;
    final footer = this.footer;
    return Card(
      key: Key('quote-type-$type'),
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8),
        side: BorderSide(
          color: selected ? scheme.primary : scheme.outlineVariant,
          width: selected ? 2 : 1,
        ),
      ),
      child: Semantics(
        button: true,
        selected: selected,
        enabled: enabled,
        child: InkWell(
          borderRadius: BorderRadius.circular(8),
          onTap: enabled ? onTap : null,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Icon(
                      selected
                          ? Icons.radio_button_checked
                          : Icons.radio_button_unchecked,
                      color: enabled ? scheme.primary : muted,
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        title,
                        style: theme.textTheme.titleMedium?.copyWith(
                          color: enabled ? null : muted,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Text(text, style: TextStyle(color: muted)),
                if (footer != null) ...[const SizedBox(height: 8), footer],
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _NeedsValue extends StatelessWidget {
  const _NeedsValue({required this.onSetValue});

  final VoidCallback onSetValue;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Wrap(
      crossAxisAlignment: WrapCrossAlignment.center,
      spacing: 8,
      children: [
        Text(
          l10n.quoteNeedsCommercialValue,
          key: const Key('quote-needs-value'),
          style: TextStyle(color: Theme.of(context).colorScheme.error),
        ),
        TextButton(
          key: const Key('quote-set-value'),
          onPressed: onSetValue,
          child: Text(l10n.quoteSetCommercialValue),
        ),
      ],
    );
  }
}
