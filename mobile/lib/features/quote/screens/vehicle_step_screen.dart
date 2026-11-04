import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/api/api_error.dart';
import '../../../core/errors/problem.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../../drivers/drivers_controller.dart';
import '../../vehicles/vehicles_controller.dart';
import '../quote_draft_controller.dart';
import '../quote_labels.dart';
import '../quote_repository.dart';
import '../widgets/quote_load_error.dart';
import '../widgets/recent_quotes.dart';
import '../widgets/step_scaffold.dart';

/// Condutor por defeito: o habitual do veículo; senão o que é o próprio
/// cliente; senão nenhum (S3-04 §2).
String? defaultDriverFor(
  VehicleResponse vehicle,
  List<DriverResponse> drivers,
) {
  final ids = {for (final driver in drivers) driver.id};
  for (final driver in vehicle.drivers) {
    if (driver.habitual && ids.contains(driver.driverId)) {
      return driver.driverId;
    }
  }
  for (final driver in drivers) {
    if (driver.isOwner) return driver.id;
  }
  return null;
}

/// `/quote/vehicle` (passo 1): veículo e condutor. `?redo=<requestId>` preenche
/// o rascunho com os dados de um pedido anterior.
class VehicleStepScreen extends ConsumerStatefulWidget {
  const VehicleStepScreen({super.key, this.redo});

  final String? redo;

  @override
  ConsumerState<VehicleStepScreen> createState() => _VehicleStepScreenState();
}

class _VehicleStepScreenState extends ConsumerState<VehicleStepScreen> {
  bool _loadingRedo = false;
  bool _startDateMoved = false;
  Object? _redoError;

  @override
  void initState() {
    super.initState();
    final redo = widget.redo;
    if (redo != null && redo.isNotEmpty) _loadRedo(redo);
  }

  Future<void> _loadRedo(String requestId) async {
    setState(() {
      _loadingRedo = true;
      _redoError = null;
    });
    try {
      final request = await ref.read(quoteRepositoryProvider).get(requestId);
      final today = ref.read(quoteTodayProvider)();
      final moved = ref
          .read(quoteDraftControllerProvider.notifier)
          .loadFrom(request, today);
      if (mounted) setState(() => _startDateMoved = moved);
    } on Object catch (error) {
      if (mounted) setState(() => _redoError = error);
    } finally {
      if (mounted) setState(() => _loadingRedo = false);
    }
  }

  Future<void> _addVehicle() async {
    await context.push('/vehicles/new');
    ref.invalidate(vehiclesControllerProvider);
  }

  Future<void> _addDriver() async {
    final vehicleId = ref.read(quoteDraftControllerProvider).vehicleId;
    await context.push(
      vehicleId == null ? '/drivers/new' : '/drivers/new?vehicleId=$vehicleId',
    );
    ref
      ..invalidate(driversControllerProvider)
      ..invalidate(vehiclesControllerProvider);
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final draft = ref.watch(quoteDraftControllerProvider);
    final controller = ref.read(quoteDraftControllerProvider.notifier);
    final vehicles = ref.watch(vehiclesControllerProvider);
    final drivers = ref.watch(driversControllerProvider);

    final children = <Widget>[];
    if (_startDateMoved) {
      children.addAll([
        MessageBanner(
          l10n.quoteStartDateMoved,
          key: const Key('quote-start-moved'),
          kind: MessageKind.info,
        ),
        const SizedBox(height: 16),
      ]);
    }
    final redoError = _redoError;
    if (redoError != null) {
      children.addAll([
        MessageBanner(Problem.from(redoError).message(l10n)),
        const SizedBox(height: 16),
      ]);
    }
    final fieldError = draft.fieldError(['vehicleId', 'driverId']);
    if (fieldError != null) {
      children.addAll([
        MessageBanner(
          fieldErrorMessage(l10n, fieldError),
          key: const Key('quote-vehicle-field-error'),
        ),
        const SizedBox(height: 16),
      ]);
    }

    final vehicleList = vehicles.value;
    final driverList = drivers.value;
    VehicleResponse? selected;
    var valid = false;
    if (_loadingRedo ||
        (vehicleList == null && !vehicles.hasError) ||
        (driverList == null && !drivers.hasError)) {
      children.add(const Center(child: CircularProgressIndicator()));
    } else if (vehicles.hasError || drivers.hasError) {
      children.add(
        QuoteLoadError(
          error: (vehicles.error ?? drivers.error)!,
          onRetry: () => ref
            ..invalidate(vehiclesControllerProvider)
            ..invalidate(driversControllerProvider),
        ),
      );
    } else if (vehicleList!.isEmpty) {
      children.addAll([
        Text(l10n.quoteNoVehicles, key: const Key('quote-no-vehicles')),
        const SizedBox(height: 16),
        FilledButton(
          key: const Key('quote-add-vehicle'),
          onPressed: _addVehicle,
          child: Text(l10n.quoteAddVehicle),
        ),
      ]);
    } else {
      for (final vehicle in vehicleList) {
        if (vehicle.id == draft.vehicleId) selected = vehicle;
      }
      final drivers = driverList!;
      valid =
          selected != null &&
          drivers.any((driver) => driver.id == draft.driverId);
      children.addAll([
        Text(
          l10n.quoteVehicleLabel,
          style: Theme.of(context).textTheme.titleMedium,
        ),
        RadioGroup<String>(
          groupValue: selected?.id,
          onChanged: (id) {
            final vehicle = vehicleList.firstWhere((v) => v.id == id);
            controller.selectVehicle(
              vehicle.id,
              defaultDriverId: defaultDriverFor(vehicle, drivers),
            );
          },
          child: Column(
            children: [
              for (final vehicle in vehicleList)
                RadioListTile<String>(
                  key: Key('quote-vehicle-${vehicle.id}'),
                  contentPadding: EdgeInsets.zero,
                  value: vehicle.id,
                  title: Text(vehicle.plate),
                  subtitle: Text(
                    l10n.quoteVehicleLine(
                      vehicle.make,
                      vehicle.model,
                      vehicle.year,
                    ),
                  ),
                ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        if (drivers.isEmpty) ...[
          Text(l10n.quoteNoDrivers, key: const Key('quote-no-drivers')),
          const SizedBox(height: 8),
          OutlinedButton(
            key: const Key('quote-add-driver'),
            onPressed: _addDriver,
            child: Text(l10n.quoteAddDriver),
          ),
        ] else
          DropdownButtonFormField<String>(
            // O valor muda por fora (condutor por defeito de outro veículo).
            key: ValueKey('quote-driver-${draft.vehicleId}-${draft.driverId}'),
            isExpanded: true,
            initialValue: drivers.any((d) => d.id == draft.driverId)
                ? draft.driverId
                : null,
            decoration: InputDecoration(labelText: l10n.quoteDriverLabel),
            items: [
              for (final driver in drivers)
                DropdownMenuItem(
                  value: driver.id,
                  child: Text(driver.fullName, overflow: TextOverflow.ellipsis),
                ),
            ],
            onChanged: controller.selectDriver,
          ),
        const SizedBox(height: 16),
        OutlinedButton(
          key: const Key('quote-add-vehicle'),
          onPressed: _addVehicle,
          child: Text(l10n.quoteAddVehicle),
        ),
      ]);
    }
    children.addAll([const SizedBox(height: 24), const RecentQuotes()]);

    return StepScaffold(
      step: 1,
      title: l10n.quoteStepVehicle,
      onContinue: valid ? () => context.push(QuoteSteps.type) : null,
      children: children,
    );
  }
}
