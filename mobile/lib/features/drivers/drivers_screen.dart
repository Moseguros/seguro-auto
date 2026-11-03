import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';
import '../vehicles/vehicle_detail_screen.dart';
import '../vehicles/vehicles_controller.dart';
import '../vehicles/vehicles_screen.dart';
import 'driver_card.dart';
import 'drivers_controller.dart';

/// `/drivers`: condutores do cliente (segmento "Condutores").
class DriversScreen extends ConsumerWidget {
  const DriversScreen({super.key});

  Future<void> _remove(
    BuildContext context,
    WidgetRef ref,
    DriverResponse driver,
  ) async {
    final l10n = AppLocalizations.of(context);
    final removed = await confirmRemove(
      context,
      title: l10n.driverRemoveConfirmTitle,
      text: l10n.driverRemoveConfirmText,
      confirmLabel: l10n.driverRemove,
      onConfirm: () => ref.read(driverRepositoryProvider).delete(driver.id),
    );
    if (!removed || !context.mounted) return;
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(SnackBar(content: Text(l10n.driverRemoved)));
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final drivers = ref.watch(driversControllerProvider);
    // Só para os rótulos das categorias; sem a lista mostram-se os códigos.
    final categories =
        ref
            .watch(referenceValuesProvider(ReferenceDomains.licenceCategory))
            .value ??
        const <PublicReferenceValueResponse>[];
    void add() => context.push('/drivers/new');

    return Scaffold(
      appBar: AppBar(title: Text(l10n.driversTitle)),
      floatingActionButton: (drivers.value?.isNotEmpty ?? false)
          ? PrimaryFab(
              key: const Key('drivers-add'),
              label: l10n.driversAddButton,
              icon: Icons.person_add_alt,
              onPressed: add,
            )
          : null,
      body: SafeArea(
        child: Column(
          children: [
            const VehiclesSegments(selected: VehiclesSegment.drivers),
            Expanded(
              child: AsyncListView<DriverResponse>(
                value: drivers,
                onRefresh: () => refreshQuietly(
                  ref.refresh(driversControllerProvider.future),
                ),
                emptyText: l10n.driversEmpty,
                emptyAction: FilledButton.icon(
                  key: const Key('drivers-add-empty'),
                  style: FilledButton.styleFrom(
                    minimumSize: const Size(64, 48),
                  ),
                  onPressed: add,
                  icon: const Icon(Icons.person_add_alt),
                  label: Text(l10n.driversAddButton),
                ),
                itemBuilder: (context, driver) => DriverCard(
                  driver: driver,
                  categories: categories,
                  onAction: (action) => switch (action) {
                    DriverAction.edit => context.push(
                      '/drivers/${driver.id}/edit',
                    ),
                    DriverAction.remove => _remove(context, ref, driver),
                  },
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
