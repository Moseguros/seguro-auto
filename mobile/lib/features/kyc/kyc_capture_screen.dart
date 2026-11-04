import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/camera/camera_service.dart';
import '../../core/camera/capture_view.dart';
import '../../core/errors/problem.dart';
import '../../core/files/image_preparer.dart';
import '../../l10n/app_localizations.dart';
import '../inspection/slot_overlay_painter.dart';

/// O que se fotografa no KYC.
enum KycShot { docFront, docBack, selfie }

/// Abre a câmara do KYC em ecrã inteiro; devolve a fotografia preparada (JPEG,
/// 1920 px, qualidade 80, sem EXIF), ou `null` se o cliente fechar.
Future<Uint8List?> captureKycPhoto(BuildContext context, KycShot shot) =>
    Navigator.of(context, rootNavigator: true).push<Uint8List>(
      MaterialPageRoute(
        fullscreenDialog: true,
        builder: (context) => KycCaptureScreen(shot: shot),
      ),
    );

/// Captura do KYC (S4-09 §6): documento com a câmara traseira e moldura de
/// cartão; selfie com a frontal e moldura oval.
class KycCaptureScreen extends ConsumerWidget {
  const KycCaptureScreen({super.key, required this.shot});

  final KycShot shot;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final selfie = shot == KycShot.selfie;
    final title = switch (shot) {
      KycShot.docFront => l10n.kycDocFront,
      KycShot.docBack => l10n.kycDocBack,
      KycShot.selfie => l10n.kycSelfie,
    };
    return CaptureView(
      lens: selfie ? CameraLens.front : CameraLens.back,
      resolution: CameraResolution.veryHigh,
      overlay: SlotOverlay(frame: selfie ? kycSelfieFrame : kycDocumentFrame),
      header: Column(
        children: [
          Text(
            title,
            style: theme.textTheme.titleLarge?.copyWith(color: Colors.white),
          ),
          const SizedBox(height: 4),
          Text(
            selfie ? l10n.kycSelfieInstruction : l10n.kycDocumentInstruction,
            key: const Key('kyc-capture-instruction'),
            style: theme.textTheme.bodyMedium?.copyWith(color: Colors.white),
          ),
        ],
      ),
      useLabel: l10n.photoUse,
      retakeLabel: l10n.kycRetakePhoto,
      permissionDeniedText: l10n.kycCameraPermissionDenied,
      onUse: (photo) async {
        final prepared = await ref
            .read(inspectionImagePreparerProvider)
            .prepare(photo);
        if (context.mounted) Navigator.of(context).pop(prepared);
      },
      onClose: () => Navigator.of(context).pop(),
      errorText: (error) => switch (error) {
        FileTooLargeException() => l10n.errorFileTooLarge,
        ImageNotReadableException() => l10n.errorFileTypeNotAllowed,
        _ => Problem.from(error).message(l10n),
      },
    );
  }
}
