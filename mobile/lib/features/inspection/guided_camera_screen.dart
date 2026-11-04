import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/camera/camera_service.dart';
import '../../core/camera/capture_view.dart';
import '../../core/clock.dart';
import '../../core/errors/problem.dart';
import '../../core/files/image_preparer.dart';
import '../../core/location/location_service.dart';
import '../../l10n/app_localizations.dart';
import 'inspection_repository.dart';
import 'inspection_slots.dart';
import 'slot_overlay_painter.dart';
import 'upload_queue_runner.dart';

/// Caminho da câmara guiada de um ângulo; [then] são os ângulos seguintes
/// ("Começar vistoria" percorre os que faltam).
String guidedCameraPath(
  String proposalId,
  String slot, {
  List<String> then = const [],
}) => Uri(
  path: '/proposals/$proposalId/inspection/camera/$slot',
  queryParameters: then.isEmpty ? null : {'then': then.join(',')},
).toString();

/// `/proposals/:id/inspection/camera/:slot`: câmara traseira com a moldura do
/// ângulo (S4-09 §4).
///
/// "Usar fotografia": hora (UTC), localização (opcional, até 5 s), compressão
/// (JPEG, 1920 px, qualidade 80, sem EXIF), ficheiro local, fila e executor.
/// Depois passa ao ângulo seguinte de [then] ou volta à vistoria.
class GuidedCameraScreen extends ConsumerWidget {
  const GuidedCameraScreen({
    super.key,
    required this.proposalId,
    required this.slot,
    this.then = const [],
  });

  final String proposalId;
  final String slot;
  final List<String> then;

  Future<void> _use(
    BuildContext context,
    WidgetRef ref,
    Uint8List photo,
  ) async {
    final takenAt = ref.read(clockProvider)().toUtc();
    final location = await ref
        .read(locationServiceProvider)
        .current(timeout: const Duration(seconds: 5));
    final bytes = await ref
        .read(inspectionImagePreparerProvider)
        .prepare(photo);
    await ref
        .read(inspectionRepositoryProvider)
        .enqueuePhoto(
          proposalId: proposalId,
          slot: slot,
          bytes: bytes,
          takenAt: takenAt,
          location: location,
        );
    ref.read(uploadQueueRunnerProvider).wake();
    if (!context.mounted) return;
    if (then.isEmpty) {
      context.pop(true);
    } else {
      context.pushReplacement(
        guidedCameraPath(proposalId, then.first, then: then.sublist(1)),
      );
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final isSide = slot == 'LEFT' || slot == 'RIGHT';
    return CaptureView(
      key: ValueKey(slot),
      lens: CameraLens.back,
      resolution: CameraResolution.high,
      initialFlash: FlashMode.auto,
      overlay: SlotOverlay(
        frame: slotFrame(slot),
        note: isSide ? l10n.inspectionSideHint : null,
      ),
      header: Column(
        children: [
          Text(
            slotName(l10n, slot),
            key: const Key('camera-slot-name'),
            style: theme.textTheme.titleLarge?.copyWith(color: Colors.white),
          ),
          const SizedBox(height: 4),
          Text(
            slotInstruction(l10n, slot),
            key: const Key('camera-slot-instruction'),
            style: theme.textTheme.bodyMedium?.copyWith(color: Colors.white),
          ),
        ],
      ),
      useLabel: l10n.inspectionUsePhoto,
      retakeLabel: l10n.photoRetake,
      permissionDeniedText: l10n.cameraPermissionDenied,
      onUse: (photo) => _use(context, ref, photo),
      onClose: () => context.pop(false),
      errorText: (error) => switch (error) {
        FileTooLargeException() => l10n.errorFileTooLarge,
        ImageNotReadableException() => l10n.errorFileTypeNotAllowed,
        _ => Problem.from(error).message(l10n),
      },
    );
  }
}
