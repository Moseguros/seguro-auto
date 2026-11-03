import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/format/dates.dart';
import '../../l10n/app_localizations.dart';
import 'vehicle_detail_screen.dart';
import 'vehicle_form.dart';
import 'vehicles_controller.dart';

/// `/vehicles/new` (acrescentar) e `/vehicles/:id/edit` (alterar).
class VehicleFormScreen extends ConsumerWidget {
  const VehicleFormScreen({super.key, this.vehicleId, this.currentYear});

  /// `null` ao acrescentar.
  final String? vehicleId;

  /// Ano actual em Maputo (testes).
  final int? currentYear;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final id = vehicleId;
    final repository = ref.read(vehicleRepositoryProvider);
    // As listas de sugestões são opcionais: sem elas o texto continua livre.
    final makes =
        ref
            .watch(referenceValuesProvider(ReferenceDomains.vehicleMake))
            .value ??
        const <PublicReferenceValueResponse>[];
    final models =
        ref
            .watch(referenceValuesProvider(ReferenceDomains.vehicleModel))
            .value ??
        const <PublicReferenceValueResponse>[];
    final examples = ref.watch(plateExamplesProvider).value ?? const <String>[];
    final year = currentYear ?? currentYearInMaputo();

    Widget body;
    if (id == null) {
      body = VehicleForm(
        currentYear: year,
        makes: makes,
        models: models,
        plateExamples: examples,
        onSave: (input) => repository.create(input.toCreateRequest()),
        onSaved: (vehicle) {
          ScaffoldMessenger.of(context)
            ..hideCurrentSnackBar()
            ..showSnackBar(SnackBar(content: Text(l10n.vehicleCreated)));
          context.pushReplacement('/vehicles/${vehicle.id}');
        },
        onReload: () {},
      );
    } else {
      final vehicle = ref.watch(vehicleProvider(id));
      final value = vehicle.value;
      if (value != null) {
        body = VehicleForm(
          key: ValueKey(value.version),
          initial: value,
          currentYear: year,
          makes: makes,
          models: models,
          plateExamples: examples,
          onSave: (input) =>
              repository.update(id, input.toUpdateRequest(value.version)),
          onSaved: (_) {
            if (context.canPop()) {
              context.pop();
            } else {
              context.go('/vehicles/$id');
            }
          },
          onReload: () => ref.invalidate(vehicleProvider(id)),
        );
      } else if (vehicle.hasError) {
        body = VehicleLoadError(
          error: vehicle.error!,
          onRetry: () => ref.invalidate(vehicleProvider(id)),
        );
      } else {
        body = const Center(child: CircularProgressIndicator());
      }
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(id == null ? l10n.vehicleNewTitle : l10n.vehicleEditTitle),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: body,
        ),
      ),
    );
  }
}
