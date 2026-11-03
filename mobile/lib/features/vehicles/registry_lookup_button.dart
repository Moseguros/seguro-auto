import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../l10n/app_localizations.dart';
import 'vehicle_rules.dart';
import 'vehicles_controller.dart';

/// "Procurar no registo" (`lookupVehicleRegistry`).
///
/// - 200 → [onFound] (o formulário preenche os campos e mostra o aviso);
/// - 404 `VEHICLE_REGISTRY_NOT_FOUND` e 503 `VEHICLE_REGISTRY_UNAVAILABLE` →
///   [onNotice] com a mensagem do código; o formulário continua editável;
/// - 400 → [onPlateError] com o código do campo `plate`.
class RegistryLookupButton extends ConsumerStatefulWidget {
  const RegistryLookupButton({
    super.key,
    required this.plate,
    required this.onFound,
    required this.onNotice,
    required this.onPlateError,
  });

  final TextEditingController plate;
  final ValueChanged<RegistryLookupResponse> onFound;
  final ValueChanged<String> onNotice;
  final ValueChanged<String> onPlateError;

  @override
  ConsumerState<RegistryLookupButton> createState() =>
      _RegistryLookupButtonState();
}

class _RegistryLookupButtonState extends ConsumerState<RegistryLookupButton> {
  bool _loading = false;

  Future<void> _lookup() async {
    final l10n = AppLocalizations.of(context);
    final local = plateError(widget.plate.text);
    if (local != null) {
      widget.onPlateError(local);
      return;
    }
    setState(() => _loading = true);
    try {
      final data = await ref
          .read(vehicleRepositoryProvider)
          .lookup(widget.plate.text.trim());
      if (mounted) widget.onFound(data);
    } catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      final plate = problem.fieldError(const ['plate']);
      if (problem.status == 400 && plate != null) {
        widget.onPlateError(plate.code);
      } else {
        widget.onNotice(problem.message(l10n));
      }
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return OutlinedButton.icon(
      key: const Key('registry-lookup'),
      style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
      onPressed: _loading ? null : _lookup,
      icon: _loading
          ? const SizedBox.square(
              dimension: 18,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : const Icon(Icons.manage_search),
      label: Text(l10n.vehicleRegistryLookup),
    );
  }
}
