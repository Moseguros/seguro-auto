import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'vehicle_card.dart';
import 'vehicles_controller.dart';

/// Espera por uma recarga sem deixar escapar o erro (o ecrã mostra-o).
Future<void> refreshQuietly(Future<Object?> reload) async {
  try {
    await reload;
  } catch (_) {
    // O estado de erro já aparece na lista.
  }
}

/// Segmento activo no topo das listas de veículos e de condutores.
enum VehiclesSegment { vehicles, drivers }

/// "Veículos | Condutores": troca entre `/vehicles` e `/drivers`.
class VehiclesSegments extends StatelessWidget {
  const VehiclesSegments({super.key, required this.selected});

  final VehiclesSegment selected;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 4),
      child: SizedBox(
        width: double.infinity,
        child: SegmentedButton<VehiclesSegment>(
          key: const Key('vehicles-segments'),
          showSelectedIcon: false,
          segments: [
            ButtonSegment(
              value: VehiclesSegment.vehicles,
              icon: const Icon(Icons.directions_car_outlined),
              label: Text(l10n.navVehicles),
            ),
            ButtonSegment(
              value: VehiclesSegment.drivers,
              icon: const Icon(Icons.badge_outlined),
              label: Text(l10n.driversTitle),
            ),
          ],
          selected: {selected},
          onSelectionChanged: (value) => context.go(
            value.single == VehiclesSegment.vehicles ? '/vehicles' : '/drivers',
          ),
        ),
      ),
    );
  }
}

/// Lista com os estados a carregar, vazio e erro, comum a veículos e condutores.
class AsyncListView<T> extends StatelessWidget {
  const AsyncListView({
    super.key,
    required this.value,
    required this.onRefresh,
    required this.emptyText,
    required this.emptyAction,
    required this.itemBuilder,
  });

  final AsyncValue<List<T>> value;
  final Future<void> Function() onRefresh;
  final String emptyText;
  final Widget emptyAction;
  final Widget Function(BuildContext context, T item) itemBuilder;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final items = value.value;
    final Widget content;
    if (items == null && value.hasError) {
      content = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(
            Problem.from(value.error!).message(l10n),
            key: const Key('list-error'),
          ),
          const SizedBox(height: 16),
          OutlinedButton(
            key: const Key('list-retry'),
            style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: onRefresh,
            child: Text(l10n.retry),
          ),
        ],
      );
    } else if (items == null) {
      return const Center(
        child: CircularProgressIndicator(key: Key('list-loading')),
      );
    } else if (items.isEmpty) {
      content = ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const SizedBox(height: 24),
          Text(
            emptyText,
            key: const Key('list-empty'),
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.titleMedium,
          ),
          const SizedBox(height: 16),
          Center(child: emptyAction),
        ],
      );
    } else {
      content = ListView.separated(
        // Espaço no fim para o botão flutuante não tapar o último cartão.
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 96),
        itemCount: items.length,
        separatorBuilder: (_, _) => const SizedBox(height: 12),
        itemBuilder: (context, index) => itemBuilder(context, items[index]),
      );
    }
    return RefreshIndicator(onRefresh: onRefresh, child: content);
  }
}

/// Botão flutuante das listas: cor primária (o laranja é só para a conversão).
class PrimaryFab extends StatelessWidget {
  const PrimaryFab({
    super.key,
    required this.label,
    required this.icon,
    required this.onPressed,
  });

  final String label;
  final IconData icon;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return FloatingActionButton.extended(
      heroTag: null,
      backgroundColor: scheme.primary,
      foregroundColor: scheme.onPrimary,
      onPressed: onPressed,
      icon: Icon(icon),
      label: Text(label),
    );
  }
}

/// `/vehicles`: "Os meus veículos".
class VehiclesScreen extends ConsumerWidget {
  const VehiclesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final vehicles = ref.watch(vehiclesControllerProvider);
    void add() => context.push('/vehicles/new');

    return Scaffold(
      appBar: AppBar(title: Text(l10n.navVehicles)),
      floatingActionButton: (vehicles.value?.isNotEmpty ?? false)
          ? PrimaryFab(
              key: const Key('vehicles-add'),
              label: l10n.vehiclesAddButton,
              icon: Icons.add,
              onPressed: add,
            )
          : null,
      body: SafeArea(
        child: Column(
          children: [
            const VehiclesSegments(selected: VehiclesSegment.vehicles),
            Expanded(
              child: AsyncListView<VehicleResponse>(
                value: vehicles,
                onRefresh: () => refreshQuietly(
                  ref.refresh(vehiclesControllerProvider.future),
                ),
                emptyText: l10n.vehiclesEmpty,
                emptyAction: FilledButton.icon(
                  key: const Key('vehicles-add-empty'),
                  style: FilledButton.styleFrom(
                    minimumSize: const Size(64, 48),
                  ),
                  onPressed: add,
                  icon: const Icon(Icons.add),
                  label: Text(l10n.vehiclesAddButton),
                ),
                itemBuilder: (context, vehicle) => VehicleCard(
                  vehicle: vehicle,
                  onTap: () => context.push('/vehicles/${vehicle.id}'),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
