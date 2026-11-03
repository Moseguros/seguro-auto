import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/theme.dart';
import '../../l10n/app_localizations.dart';
import 'vehicle_labels.dart';

/// Matrícula em destaque, com algarismos de largura fixa.
TextStyle? plateStyle(TextStyle? base) => base?.copyWith(
  fontWeight: FontWeight.w700,
  fontFeatures: const [FontFeature.tabularFigures()],
);

enum TagKind { neutral, success, warning }

/// Etiqueta pequena ("Livrete em falta", "Confirmado no registo", "Eu"…).
class VehicleTag extends StatelessWidget {
  const VehicleTag({
    super.key,
    required this.label,
    this.icon,
    this.kind = TagKind.neutral,
  });

  final String label;
  final IconData? icon;
  final TagKind kind;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    final isDark = scheme.brightness == Brightness.dark;
    final color = switch (kind) {
      TagKind.neutral => scheme.onSurfaceVariant,
      TagKind.success => isDark ? BrandColors.successDark : BrandColors.success,
      // Aviso em âmbar escuro: o laranja da marca fica para as acções de conversão.
      TagKind.warning =>
        isDark ? const Color(0xFFFFCC80) : const Color(0xFF8A4B00),
    };
    final icon = this.icon;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.1),
        border: Border.all(color: color.withValues(alpha: 0.5)),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (icon != null) ...[
            Icon(icon, size: 16, color: color),
            const SizedBox(width: 4),
          ],
          Flexible(
            child: Text(
              label,
              style: Theme.of(
                context,
              ).textTheme.labelMedium?.copyWith(color: color),
            ),
          ),
        ],
      ),
    );
  }
}

/// Nome do condutor habitual de um veículo, se houver.
String? habitualDriverName(VehicleResponse vehicle) {
  for (final driver in vehicle.drivers) {
    if (driver.habitual) return driver.fullName;
  }
  return null;
}

/// Cartão da lista de veículos (o mesmo conteúdo da web, S2-06 §2).
class VehicleCard extends StatelessWidget {
  const VehicleCard({super.key, required this.vehicle, required this.onTap});

  final VehicleResponse vehicle;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final secondary = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    final habitual = habitualDriverName(vehicle);
    return Card(
      key: Key('vehicle-card-${vehicle.id}'),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                vehicle.plate,
                style: plateStyle(theme.textTheme.titleLarge),
              ),
              const SizedBox(height: 4),
              Text(
                l10n.vehicleMakeModelYear(
                  vehicle.make,
                  vehicle.model,
                  '${vehicle.year}',
                ),
                style: theme.textTheme.titleSmall,
              ),
              const SizedBox(height: 4),
              Text(
                '${vehicleCategoryName(l10n, vehicle.category.name)} · '
                '${vehicleUsageName(l10n, vehicle.usage.name)}',
                style: secondary,
              ),
              const SizedBox(height: 4),
              Text(
                habitual == null
                    ? l10n.vehicleNoHabitualDriver
                    : l10n.vehicleHabitualDriver(habitual),
                style: secondary,
              ),
              if (vehicle.registrationDoc == null ||
                  vehicle.inatroVerified) ...[
                const SizedBox(height: 8),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: [
                    if (vehicle.registrationDoc == null)
                      VehicleTag(
                        label: l10n.vehicleDocMissing,
                        icon: Icons.warning_amber_outlined,
                        kind: TagKind.warning,
                      ),
                    if (vehicle.inatroVerified)
                      VehicleTag(
                        label: l10n.vehicleRegistryVerified,
                        icon: Icons.verified_outlined,
                        kind: TagKind.success,
                      ),
                  ],
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
