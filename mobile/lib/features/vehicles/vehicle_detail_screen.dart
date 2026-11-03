import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/format/money.dart';
import '../../core/l10n/province_name.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'manage_drivers_sheet.dart';
import 'registration_doc_section.dart';
import 'vehicle_card.dart';
import 'vehicle_labels.dart';
import 'vehicles_controller.dart';
import 'vehicles_screen.dart' show refreshQuietly;

/// `/vehicles/:id`: dados, livrete e condutores, com "Alterar" e "Retirar veículo".
class VehicleDetailScreen extends ConsumerWidget {
  const VehicleDetailScreen({super.key, required this.vehicleId});

  final String vehicleId;

  Future<void> _remove(BuildContext context, WidgetRef ref) async {
    final l10n = AppLocalizations.of(context);
    final removed = await confirmRemove(
      context,
      title: l10n.vehicleRemoveConfirmTitle,
      text: l10n.vehicleRemoveConfirmText,
      confirmLabel: l10n.vehicleRemoveConfirm,
      onConfirm: () => ref.read(vehicleRepositoryProvider).delete(vehicleId),
    );
    if (!removed || !context.mounted) return;
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(SnackBar(content: Text(l10n.vehicleRemoved)));
    context.go('/vehicles');
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final vehicle = ref.watch(vehicleProvider(vehicleId));
    final value = vehicle.value;

    final Widget body;
    if (value != null) {
      body = RefreshIndicator(
        onRefresh: () =>
            refreshQuietly(ref.refresh(vehicleProvider(vehicleId).future)),
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _Header(
              vehicle: value,
              onEdit: () => context.push('/vehicles/$vehicleId/edit'),
              onRemove: () => _remove(context, ref),
            ),
            const SizedBox(height: 16),
            _DataSection(vehicle: value),
            const SizedBox(height: 16),
            RegistrationDocSection(vehicle: value),
            const SizedBox(height: 16),
            _DriversSection(vehicle: value),
          ],
        ),
      );
    } else if (vehicle.hasError) {
      body = Padding(
        padding: const EdgeInsets.all(16),
        child: VehicleLoadError(
          error: vehicle.error!,
          onRetry: () => ref.invalidate(vehicleProvider(vehicleId)),
        ),
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }

    return Scaffold(
      appBar: AppBar(title: Text(value?.plate ?? l10n.navVehicles)),
      body: SafeArea(child: body),
    );
  }
}

/// Erro ao ler um veículo: `VEHICLE_NOT_FOUND` → mensagem e ligação para a lista;
/// outros → mensagem e "Tentar de novo".
class VehicleLoadError extends StatelessWidget {
  const VehicleLoadError({
    super.key,
    required this.error,
    required this.onRetry,
  });

  final Object error;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final problem = Problem.from(error);
    final notFound = problem.code == 'VEHICLE_NOT_FOUND';
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        MessageBanner(problem.message(l10n), key: const Key('vehicle-error')),
        const SizedBox(height: 16),
        OutlinedButton(
          onPressed: notFound ? () => context.go('/vehicles') : onRetry,
          child: Text(notFound ? l10n.vehiclesBackToList : l10n.retry),
        ),
      ],
    );
  }
}

class _Header extends StatelessWidget {
  const _Header({
    required this.vehicle,
    required this.onEdit,
    required this.onRemove,
  });

  final VehicleResponse vehicle;
  final VoidCallback onEdit;
  final VoidCallback onRemove;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              vehicle.plate,
              style: plateStyle(theme.textTheme.headlineSmall),
            ),
            const SizedBox(height: 4),
            Text(
              '${vehicle.make} ${vehicle.model}',
              style: theme.textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            VehicleTag(
              label: vehicle.inatroVerified
                  ? l10n.vehicleRegistryVerified
                  : l10n.vehicleRegistryNotVerified,
              icon: vehicle.inatroVerified
                  ? Icons.verified_outlined
                  : Icons.help_outline,
              kind: vehicle.inatroVerified ? TagKind.success : TagKind.neutral,
            ),
            const SizedBox(height: 16),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                OutlinedButton.icon(
                  key: const Key('vehicle-edit'),
                  onPressed: onEdit,
                  icon: const Icon(Icons.edit_outlined),
                  label: Text(l10n.vehicleEdit),
                ),
                TextButton.icon(
                  key: const Key('vehicle-remove'),
                  style: TextButton.styleFrom(
                    foregroundColor: theme.colorScheme.error,
                  ),
                  onPressed: onRemove,
                  icon: const Icon(Icons.delete_outline),
                  label: Text(l10n.vehicleRemove),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

/// Cartão de uma secção do detalhe, com título.
class DetailSection extends StatelessWidget {
  const DetailSection({super.key, required this.title, required this.children});

  final String title;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(title, style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 12),
            ...children,
          ],
        ),
      ),
    );
  }
}

class _DataSection extends StatelessWidget {
  const _DataSection({required this.vehicle});

  final VehicleResponse vehicle;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final language = Localizations.localeOf(context).languageCode;
    String orDash(Object? value) =>
        value == null ? l10n.valueMissing : '$value';
    final value = vehicle.commercialValue;
    final rows = [
      (l10n.vehicleFieldPlate, vehicle.plate),
      (l10n.vehicleFieldChassis, vehicle.chassisNumber),
      (l10n.vehicleFieldMake, vehicle.make),
      (l10n.vehicleFieldModel, vehicle.model),
      (l10n.vehicleFieldYear, '${vehicle.year}'),
      (
        l10n.vehicleFieldCategory,
        vehicleCategoryName(l10n, vehicle.category.name),
      ),
      (l10n.vehicleFieldEngineCc, orDash(vehicle.engineCc)),
      (l10n.vehicleFieldSeats, '${vehicle.seats}'),
      (l10n.vehicleFieldGrossWeight, orDash(vehicle.grossWeightKg)),
      (l10n.vehicleFieldUsage, vehicleUsageName(l10n, vehicle.usage.name)),
      (
        l10n.vehicleFieldProvince,
        provinceName(l10n, vehicle.provinceOfCirculation.name),
      ),
      (
        l10n.vehicleFieldCommercialValue,
        value == null ? l10n.valueMissing : formatMoney(value.amount, language),
      ),
      (l10n.vehicleFieldHasTracker, vehicle.hasTracker ? l10n.yes : l10n.no),
    ];
    final theme = Theme.of(context);
    return DetailSection(
      title: l10n.vehicleSectionData,
      children: [
        for (final (label, text) in rows)
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 6),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Text(
                    label,
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: theme.colorScheme.onSurfaceVariant,
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    text,
                    textAlign: TextAlign.end,
                    style: theme.textTheme.bodyMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            ),
          ),
      ],
    );
  }
}

class _DriversSection extends ConsumerWidget {
  const _DriversSection({required this.vehicle});

  final VehicleResponse vehicle;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    return DetailSection(
      title: l10n.vehicleSectionDrivers,
      children: [
        if (vehicle.drivers.isEmpty)
          Text(l10n.vehicleNoDrivers)
        else
          for (final driver in vehicle.drivers)
            ListTile(
              contentPadding: EdgeInsets.zero,
              leading: const Icon(Icons.person_outline),
              title: Text(driver.fullName),
              trailing: driver.habitual
                  ? VehicleTag(
                      label: l10n.vehicleDriverHabitual,
                      kind: TagKind.success,
                    )
                  : null,
            ),
        const SizedBox(height: 8),
        Align(
          alignment: AlignmentDirectional.centerStart,
          child: OutlinedButton.icon(
            key: const Key('manage-drivers'),
            onPressed: () => showManageDriversSheet(context, vehicle.id),
            icon: const Icon(Icons.group_outlined),
            label: Text(l10n.vehicleManageDrivers),
          ),
        ),
      ],
    );
  }
}

/// Confirmação de "Retirar": a acção corre dentro do diálogo para que um erro
/// (`VEHICLE_REMOVAL_BLOCKED`, `DRIVER_IS_HABITUAL`) apareça lá e nada mude.
class _ConfirmRemoveDialog extends StatefulWidget {
  const _ConfirmRemoveDialog({
    required this.title,
    required this.text,
    required this.confirmLabel,
    required this.onConfirm,
  });

  final String title;
  final String text;
  final String confirmLabel;
  final Future<void> Function() onConfirm;

  @override
  State<_ConfirmRemoveDialog> createState() => _ConfirmRemoveDialogState();
}

class _ConfirmRemoveDialogState extends State<_ConfirmRemoveDialog> {
  bool _busy = false;
  String? _error;

  Future<void> _confirm() async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await widget.onConfirm();
      if (mounted) Navigator.of(context).pop(true);
    } catch (error) {
      if (!mounted) return;
      setState(() {
        _busy = false;
        _error = Problem.from(error).message(l10n);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return AlertDialog(
      title: Text(widget.title),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(widget.text),
          if (_error != null) ...[
            const SizedBox(height: 16),
            MessageBanner(_error!, key: const Key('remove-error')),
          ],
        ],
      ),
      actions: [
        TextButton(
          onPressed: _busy ? null : () => Navigator.of(context).pop(false),
          child: Text(l10n.cancel),
        ),
        TextButton(
          key: const Key('confirm-remove'),
          style: TextButton.styleFrom(
            foregroundColor: Theme.of(context).colorScheme.error,
          ),
          onPressed: _busy ? null : _confirm,
          child: Text(widget.confirmLabel),
        ),
      ],
    );
  }
}

/// Mostra a confirmação de "Retirar" (veículo ou condutor); `true` se retirou.
Future<bool> confirmRemove(
  BuildContext context, {
  required String title,
  required String text,
  required String confirmLabel,
  required Future<void> Function() onConfirm,
}) async =>
    await showDialog<bool>(
      context: context,
      builder: (context) => _ConfirmRemoveDialog(
        title: title,
        text: text,
        confirmLabel: confirmLabel,
        onConfirm: onConfirm,
      ),
    ) ==
    true;
