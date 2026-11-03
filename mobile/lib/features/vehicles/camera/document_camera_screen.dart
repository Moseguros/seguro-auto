import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/files/image_preparer.dart';
import '../../../l10n/app_localizations.dart';
import '../registration_doc_section.dart';
import '../vehicles_controller.dart';
import 'document_camera.dart';
import 'document_frame_overlay.dart';
import 'photo_review_screen.dart';

enum _Stage { permission, denied, starting, preview, review, failed }

/// `/vehicles/:id/registration-doc/camera`: câmara guiada do livrete.
///
/// Pede a permissão ao abrir; mostra a pré-visualização com a moldura; depois do
/// disparo, a revisão ("Repetir" / "Usar foto"). "Usar foto" prepara a imagem
/// (JPEG, sem EXIF), envia-a e volta ao detalhe com `true`.
class DocumentCameraScreen extends ConsumerStatefulWidget {
  const DocumentCameraScreen({super.key, required this.vehicleId});

  final String vehicleId;

  @override
  ConsumerState<DocumentCameraScreen> createState() =>
      _DocumentCameraScreenState();
}

class _DocumentCameraScreenState extends ConsumerState<DocumentCameraScreen> {
  DocumentCamera? _camera;
  _Stage _stage = _Stage.permission;
  FlashMode _flash = FlashMode.off;
  Uint8List? _photo;
  bool _busy = false;
  double? _progress;
  String? _error;

  @override
  void initState() {
    super.initState();
    SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);
    _start();
  }

  @override
  void dispose() {
    SystemChrome.setPreferredOrientations(const []);
    _camera?.dispose();
    super.dispose();
  }

  Future<void> _start() async {
    final granted = await ref.read(cameraPermissionProvider).request();
    if (!mounted) return;
    if (!granted) {
      setState(() => _stage = _Stage.denied);
      return;
    }
    setState(() => _stage = _Stage.starting);
    final camera = ref.read(documentCameraProvider)();
    _camera = camera;
    try {
      await camera.initialize();
      if (mounted) setState(() => _stage = _Stage.preview);
    } catch (_) {
      if (mounted) setState(() => _stage = _Stage.failed);
    }
  }

  Future<void> _capture() async {
    final camera = _camera;
    if (camera == null || _busy) return;
    setState(() => _busy = true);
    try {
      final photo = await camera.capture();
      if (!mounted) return;
      setState(() {
        _photo = photo;
        _error = null;
        _stage = _Stage.review;
      });
    } catch (_) {
      if (mounted) setState(() => _stage = _Stage.failed);
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  Future<void> _toggleFlash() async {
    final next = nextFlashMode(_flash);
    try {
      await _camera?.setFlash(next);
      if (mounted) setState(() => _flash = next);
    } catch (_) {
      // Câmara sem flash: o botão não muda.
    }
  }

  void _retake() => setState(() {
    _photo = null;
    _error = null;
    _stage = _Stage.preview;
  });

  Future<void> _use() async {
    final photo = _photo;
    if (photo == null) return;
    await _send(() => ref.read(imagePreparerProvider).prepare(photo));
  }

  Future<void> _useGallery() => _send(() async => pickGalleryPhoto(ref));

  /// Prepara (ou escolhe), envia e volta ao detalhe.
  Future<void> _send(Future<Uint8List?> Function() prepare) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
      _progress = null;
    });
    try {
      final bytes = await prepare();
      if (bytes == null) return;
      await ref
          .read(vehicleRepositoryProvider)
          .uploadRegistrationDoc(
            widget.vehicleId,
            bytes,
            filename: registrationPhotoName,
            contentType: 'image/jpeg',
            onProgress: (sent, total) {
              if (mounted && total > 0) {
                setState(() => _progress = sent / total);
              }
            },
          );
      // A foto só esteve em memória: ao sair, não fica cópia.
      _photo = null;
      if (mounted) context.pop(true);
    } catch (error) {
      if (mounted) {
        setState(() => _error = registrationDocErrorMessage(l10n, error));
      }
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  void _close() => context.pop(false);

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final Widget body = switch (_stage) {
      _Stage.permission || _Stage.starting => const Center(
        child: CircularProgressIndicator(color: Colors.white),
      ),
      _Stage.denied => _PermissionDenied(
        busy: _busy,
        error: _error,
        onOpenSettings: () => ref.read(cameraPermissionProvider).openSettings(),
        onGallery: _useGallery,
      ),
      _Stage.failed => _PermissionDenied(
        message: l10n.cameraUnavailable,
        busy: _busy,
        error: _error,
        onGallery: _useGallery,
      ),
      _Stage.preview => _Preview(
        camera: _camera!,
        flash: _flash,
        busy: _busy,
        onClose: _close,
        onFlash: _toggleFlash,
        onCapture: _capture,
      ),
      _Stage.review => PhotoReviewScreen(
        photo: _photo!,
        busy: _busy,
        progress: _progress,
        error: _error,
        onRetake: _retake,
        onUse: _use,
      ),
    };
    return Scaffold(backgroundColor: Colors.black, body: body);
  }
}

class _Preview extends StatelessWidget {
  const _Preview({
    required this.camera,
    required this.flash,
    required this.busy,
    required this.onClose,
    required this.onFlash,
    required this.onCapture,
  });

  final DocumentCamera camera;
  final FlashMode flash;
  final bool busy;
  final VoidCallback onClose;
  final VoidCallback onFlash;
  final VoidCallback onCapture;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final (flashIcon, flashLabel) = switch (flash) {
      FlashMode.auto => (Icons.flash_auto, l10n.cameraFlashAuto),
      FlashMode.always ||
      FlashMode.torch => (Icons.flash_on, l10n.cameraFlashOn),
      FlashMode.off => (Icons.flash_off, l10n.cameraFlashOff),
    };
    return Stack(
      fit: StackFit.expand,
      children: [
        camera.preview(),
        DocumentFrameOverlay(hint: l10n.cameraFrameHint),
        SafeArea(
          child: Stack(
            children: [
              Positioned(
                top: 8,
                left: 8,
                child: IconButton(
                  key: const Key('camera-close'),
                  tooltip: l10n.close,
                  color: Colors.white,
                  iconSize: 28,
                  onPressed: onClose,
                  icon: const Icon(Icons.close),
                ),
              ),
              Positioned(
                top: 8,
                right: 8,
                child: IconButton(
                  key: const Key('camera-flash'),
                  tooltip: flashLabel,
                  color: Colors.white,
                  iconSize: 28,
                  onPressed: onFlash,
                  icon: Icon(flashIcon),
                ),
              ),
              Positioned(
                left: 0,
                right: 0,
                bottom: 32,
                child: Center(
                  child: Semantics(
                    button: true,
                    label: l10n.cameraCapture,
                    child: GestureDetector(
                      key: const Key('camera-shutter'),
                      onTap: busy ? null : onCapture,
                      child: Container(
                        width: 72,
                        height: 72,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: Colors.white,
                          border: Border.all(color: Colors.white54, width: 6),
                        ),
                      ),
                    ),
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

/// Sem permissão (ou sem câmara): mensagem, "Abrir definições" e "Escolher da galeria".
class _PermissionDenied extends StatelessWidget {
  const _PermissionDenied({
    this.message,
    required this.busy,
    required this.onGallery,
    this.onOpenSettings,
    this.error,
  });

  final String? message;
  final bool busy;
  final VoidCallback onGallery;
  final VoidCallback? onOpenSettings;
  final String? error;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final error = this.error;
    final onOpenSettings = this.onOpenSettings;
    return ColoredBox(
      color: theme.scaffoldBackgroundColor,
      child: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: IconButton(
                key: const Key('camera-close'),
                tooltip: l10n.close,
                onPressed: () => context.pop(false),
                icon: const Icon(Icons.close),
              ),
            ),
            Expanded(
              child: ListView(
                padding: const EdgeInsets.all(24),
                children: [
                  Icon(
                    Icons.no_photography_outlined,
                    size: 56,
                    color: theme.colorScheme.primary,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    message ?? l10n.cameraPermissionDenied,
                    key: const Key('camera-permission-denied'),
                    textAlign: TextAlign.center,
                    style: theme.textTheme.titleMedium,
                  ),
                  const SizedBox(height: 24),
                  if (busy) ...[
                    const LinearProgressIndicator(),
                    const SizedBox(height: 16),
                  ],
                  if (error != null) ...[
                    Text(
                      error,
                      key: const Key('photo-error'),
                      textAlign: TextAlign.center,
                      style: TextStyle(color: theme.colorScheme.error),
                    ),
                    const SizedBox(height: 16),
                  ],
                  if (onOpenSettings != null) ...[
                    FilledButton(
                      key: const Key('camera-open-settings'),
                      style: FilledButton.styleFrom(
                        minimumSize: const Size(64, 48),
                      ),
                      onPressed: busy ? null : onOpenSettings,
                      child: Text(l10n.cameraOpenSettings),
                    ),
                    const SizedBox(height: 12),
                  ],
                  OutlinedButton(
                    key: const Key('camera-gallery'),
                    style: OutlinedButton.styleFrom(
                      minimumSize: const Size(64, 48),
                    ),
                    onPressed: busy ? null : onGallery,
                    child: Text(l10n.docSourceGallery),
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
