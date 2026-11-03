import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';

/// A foto tirada, com "Repetir" (contorno) e "Usar foto" (primário).
class PhotoReviewScreen extends StatelessWidget {
  const PhotoReviewScreen({
    super.key,
    required this.photo,
    required this.onRetake,
    required this.onUse,
    this.busy = false,
    this.progress,
    this.error,
  });

  final Uint8List photo;
  final VoidCallback onRetake;
  final VoidCallback onUse;

  /// A preparar ou a enviar: os botões ficam desactivados.
  final bool busy;

  /// Fracção enviada (0–1) durante o envio.
  final double? progress;
  final String? error;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final error = this.error;
    return ColoredBox(
      color: Colors.black,
      child: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Expanded(
              child: Image.memory(
                photo,
                key: const Key('photo-review-image'),
                fit: BoxFit.contain,
                gaplessPlayback: true,
              ),
            ),
            if (busy) LinearProgressIndicator(value: progress),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  if (error != null) ...[
                    Material(
                      color: Theme.of(context).colorScheme.surface,
                      borderRadius: BorderRadius.circular(8),
                      child: MessageBanner(
                        error,
                        key: const Key('photo-error'),
                      ),
                    ),
                    const SizedBox(height: 12),
                  ],
                  Row(
                    children: [
                      Expanded(
                        child: OutlinedButton(
                          key: const Key('photo-retake'),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: Colors.white,
                            side: const BorderSide(color: Colors.white),
                            minimumSize: const Size(64, 48),
                          ),
                          onPressed: busy ? null : onRetake,
                          child: Text(l10n.photoRetake),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: FilledButton(
                          key: const Key('photo-use'),
                          style: FilledButton.styleFrom(
                            minimumSize: const Size(64, 48),
                          ),
                          onPressed: busy ? null : onUse,
                          child: Text(l10n.photoUse),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
