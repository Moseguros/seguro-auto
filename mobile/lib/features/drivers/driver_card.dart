import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';
import '../vehicles/vehicle_card.dart';
import '../vehicles/vehicle_labels.dart';

enum DriverAction { edit, remove }

/// Cartão da lista de condutores (o mesmo conteúdo da web, S2-06 §5).
class DriverCard extends StatelessWidget {
  const DriverCard({
    super.key,
    required this.driver,
    required this.categories,
    required this.onAction,
  });

  final DriverResponse driver;

  /// Valores de `LICENCE_CATEGORY` para os rótulos (código quando não existe).
  final List<PublicReferenceValueResponse> categories;
  final ValueChanged<DriverAction> onAction;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final language = Localizations.localeOf(context).languageCode;
    final secondary = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    return Card(
      key: Key('driver-card-${driver.id}'),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(16, 8, 4, 16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: Padding(
                padding: const EdgeInsets.only(top: 8),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Wrap(
                      spacing: 8,
                      runSpacing: 4,
                      crossAxisAlignment: WrapCrossAlignment.center,
                      children: [
                        Text(
                          driver.fullName,
                          style: theme.textTheme.titleMedium,
                        ),
                        if (driver.isOwner)
                          VehicleTag(
                            label: l10n.driverOwnerTag,
                            kind: TagKind.success,
                          ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      l10n.driverCategories(
                        [
                          for (final code in driver.licenceCategories)
                            referenceLabelFor(categories, code, language),
                        ].join(', '),
                      ),
                      style: secondary,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      l10n.driverLicenceMasked(driver.licenceNumberMasked),
                      style: secondary,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      l10n.driverVehicleCount(driver.vehicleIds.length),
                      style: secondary,
                    ),
                  ],
                ),
              ),
            ),
            PopupMenuButton<DriverAction>(
              key: Key('driver-menu-${driver.id}'),
              tooltip: l10n.moreOptions,
              onSelected: onAction,
              itemBuilder: (context) => [
                PopupMenuItem(
                  value: DriverAction.edit,
                  child: Text(l10n.driverEdit),
                ),
                PopupMenuItem(
                  value: DriverAction.remove,
                  child: Text(l10n.driverRemove),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
