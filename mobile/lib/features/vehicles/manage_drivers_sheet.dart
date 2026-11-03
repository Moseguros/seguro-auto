import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../drivers/drivers_controller.dart';
import 'vehicles_controller.dart';

/// Máximo de condutores por veículo (S2-03 §3.5).
const maxDriversPerVehicle = 5;

/// Abre "Gerir condutores" para o veículo [vehicleId].
///
/// Sem condutores registados, "Registar condutor" vai para
/// `/drivers/new?vehicleId={id}`; ao voltar com um condutor novo, a folha reabre.
Future<void> showManageDriversSheet(
  BuildContext context,
  String vehicleId,
) async {
  final register = await showModalBottomSheet<bool>(
    context: context,
    isScrollControlled: true,
    showDragHandle: true,
    useSafeArea: true,
    builder: (context) => ManageDriversSheet(vehicleId: vehicleId),
  );
  if (register != true || !context.mounted) return;
  final created = await context.push<bool>('/drivers/new?vehicleId=$vehicleId');
  if (created == true && context.mounted) {
    await showManageDriversSheet(context, vehicleId);
  }
}

/// Condutores de um veículo: caixas com os condutores do cliente e, em cada caixa
/// marcada, a opção "Habitual" (só uma). Grava com `updateMyVehicleDrivers`.
///
/// Fecha com `true` quando o cliente quer registar um condutor.
class ManageDriversSheet extends ConsumerStatefulWidget {
  const ManageDriversSheet({super.key, required this.vehicleId});

  final String vehicleId;

  @override
  ConsumerState<ManageDriversSheet> createState() => _ManageDriversSheetState();
}

class _ManageDriversSheetState extends ConsumerState<ManageDriversSheet> {
  /// Condutores marcados, pela ordem em que foram marcados.
  final List<String> _selected = [];
  String? _habitual;
  bool _initialized = false;
  bool _saving = false;
  String? _error;

  void _init(VehicleResponse vehicle) {
    if (_initialized) return;
    _initialized = true;
    for (final driver in vehicle.drivers) {
      _selected.add(driver.driverId);
      if (driver.habitual) _habitual = driver.driverId;
    }
  }

  void _toggle(String id, bool checked) => setState(() {
    _error = null;
    if (checked) {
      if (_selected.length < maxDriversPerVehicle) _selected.add(id);
    } else {
      _selected.remove(id);
      if (_habitual == id) _habitual = null;
    }
  });

  bool get _missingHabitual => _selected.isNotEmpty && _habitual == null;

  Future<void> _save(VehicleResponse vehicle) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _saving = true;
      _error = null;
    });
    try {
      await ref
          .read(vehicleRepositoryProvider)
          .updateDrivers(
            vehicle.id,
            drivers: {for (final id in _selected) id: id == _habitual},
            version: vehicle.version,
          );
      if (mounted) Navigator.of(context).pop(false);
    } catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      final field = problem.fieldError(const ['drivers']);
      setState(() {
        _saving = false;
        _error = field != null
            ? fieldErrorMessage(l10n, field.code)
            : problem.message(l10n);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final vehicle = ref.watch(vehicleProvider(widget.vehicleId));
    final drivers = ref.watch(driversControllerProvider);

    final vehicleValue = vehicle.value;
    final driversValue = drivers.value;
    final error = vehicle.error ?? drivers.error;

    final Widget content;
    if (vehicleValue != null && driversValue != null) {
      _init(vehicleValue);
      content = _content(context, vehicleValue, driversValue);
    } else if (error != null) {
      content = Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          MessageBanner(Problem.from(error).message(l10n)),
          const SizedBox(height: 12),
          OutlinedButton(
            onPressed: () {
              ref.invalidate(vehicleProvider(widget.vehicleId));
              ref.invalidate(driversControllerProvider);
            },
            child: Text(l10n.retry),
          ),
        ],
      );
    } else {
      content = const Padding(
        padding: EdgeInsets.all(24),
        child: Center(child: CircularProgressIndicator()),
      );
    }

    return Padding(
      padding: EdgeInsets.fromLTRB(
        16,
        0,
        16,
        16 + MediaQuery.viewInsetsOf(context).bottom,
      ),
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(l10n.vehicleManageDrivers, style: theme.textTheme.titleLarge),
            const SizedBox(height: 12),
            content,
          ],
        ),
      ),
    );
  }

  Widget _content(
    BuildContext context,
    VehicleResponse vehicle,
    List<DriverResponse> drivers,
  ) {
    final l10n = AppLocalizations.of(context);
    if (drivers.isEmpty) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(l10n.driversEmpty),
          const SizedBox(height: 16),
          FilledButton(
            key: const Key('manage-drivers-register'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: () => Navigator.of(context).pop(true),
            child: Text(l10n.driversAddButton),
          ),
        ],
      );
    }
    final full = _selected.length >= maxDriversPerVehicle;
    return RadioGroup<String>(
      groupValue: _habitual,
      onChanged: (id) => setState(() {
        _habitual = id;
        _error = null;
      }),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(l10n.manageDriversHelp(maxDriversPerVehicle)),
          const SizedBox(height: 8),
          for (final driver in drivers) ...[
            CheckboxListTile(
              key: Key('driver-check-${driver.id}'),
              contentPadding: EdgeInsets.zero,
              controlAffinity: ListTileControlAffinity.leading,
              title: Text(driver.fullName),
              value: _selected.contains(driver.id),
              onChanged: !_selected.contains(driver.id) && full
                  ? null
                  : (checked) => _toggle(driver.id, checked ?? false),
            ),
            if (_selected.contains(driver.id))
              Padding(
                padding: const EdgeInsetsDirectional.only(start: 40),
                child: Row(
                  children: [
                    Radio<String>(
                      key: Key('driver-habitual-${driver.id}'),
                      value: driver.id,
                    ),
                    Text(l10n.vehicleDriverHabitual),
                  ],
                ),
              ),
          ],
          if (_missingHabitual) ...[
            const SizedBox(height: 8),
            MessageBanner(
              l10n.validationHabitualDriverRequired,
              key: const Key('habitual-required'),
            ),
          ],
          if (_error != null) ...[
            const SizedBox(height: 8),
            MessageBanner(_error!, key: const Key('manage-drivers-error')),
          ],
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  key: const Key('manage-drivers-register'),
                  style: OutlinedButton.styleFrom(
                    minimumSize: const Size(64, 48),
                  ),
                  onPressed: _saving
                      ? null
                      : () => Navigator.of(context).pop(true),
                  child: Text(l10n.driversAddButton),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: FilledButton(
                  key: const Key('manage-drivers-save'),
                  style: FilledButton.styleFrom(
                    minimumSize: const Size(64, 48),
                  ),
                  onPressed: _saving || _missingHabitual
                      ? null
                      : () => _save(vehicle),
                  child: Text(l10n.save),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
