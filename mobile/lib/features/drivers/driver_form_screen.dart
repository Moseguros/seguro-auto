import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../vehicles/vehicles_controller.dart';
import 'driver_form.dart';
import 'drivers_controller.dart';

/// `/drivers/new` (com `?vehicleId=` quando vem de "Gerir condutores") e
/// `/drivers/:id/edit`.
class DriverFormScreen extends ConsumerWidget {
  const DriverFormScreen({super.key, this.driverId, this.vehicleId});

  /// `null` ao registar.
  final String? driverId;

  /// Veículo para onde voltar depois de registar (a folha de condutores reabre).
  final String? vehicleId;

  void _done(BuildContext context, {required bool created}) {
    if (context.canPop()) {
      context.pop(created);
    } else if (vehicleId != null) {
      context.go('/vehicles/$vehicleId');
    } else {
      context.go('/drivers');
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final id = driverId;
    final repository = ref.read(driverRepositoryProvider);
    final drivers = ref.watch(driversControllerProvider);
    final categories = ref.watch(
      referenceValuesProvider(ReferenceDomains.licenceCategory),
    );
    final driver = id == null ? null : ref.watch(driverProvider(id));

    final Widget body;
    final error = categories.error ?? drivers.error ?? driver?.error;
    if (categories.hasValue &&
        drivers.hasValue &&
        (driver == null || driver.hasValue)) {
      final current = driver?.value;
      final canBeOwner = !drivers.requireValue.any(
        (d) => d.isOwner && d.id != current?.id,
      );
      body = DriverForm(
        key: ValueKey(current?.version),
        initial: current,
        canBeOwner: canBeOwner,
        categories: categories.requireValue,
        loadProfile: repository.myProfile,
        onSave: (input) => current == null
            ? repository.create(input.toCreateRequest())
            : repository.update(
                current.id,
                input.toUpdateRequest(current.version),
              ),
        onSaved: (_) => _done(context, created: current == null),
        onReload: () {
          if (id != null) ref.invalidate(driverProvider(id));
        },
      );
    } else if (error != null) {
      final problem = Problem.from(error);
      body = Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          MessageBanner(problem.message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(
            onPressed: problem.code == 'DRIVER_NOT_FOUND'
                ? () => context.go('/drivers')
                : () {
                    ref.invalidate(driversControllerProvider);
                    ref.invalidate(
                      referenceValuesProvider(ReferenceDomains.licenceCategory),
                    );
                    if (id != null) ref.invalidate(driverProvider(id));
                  },
            child: Text(
              problem.code == 'DRIVER_NOT_FOUND'
                  ? l10n.driversBackToList
                  : l10n.retry,
            ),
          ),
        ],
      );
    } else {
      body = const Padding(
        padding: EdgeInsets.all(24),
        child: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(id == null ? l10n.driversAddButton : l10n.driverEditTitle),
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
