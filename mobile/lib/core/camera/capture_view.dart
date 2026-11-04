import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../l10n/app_localizations.dart';
import '../widgets/message_banner.dart';
import 'camera_service.dart';

enum _Stage { permission, denied, starting, preview, review, failed }

/// Câmara em ecrã inteiro para uma fotografia guiada (vistoria e KYC):
/// pede a permissão ao abrir, mostra a pré-visualização com a moldura de
/// [overlay] e os textos de [header], o flash e o disparo; depois do disparo,
/// a revisão com [useLabel] (primário) e "Repetir" (contorno).
///
/// [onUse] recebe a fotografia (bytes JPEG da câmara); enquanto corre, os botões
/// ficam desactivados. Um erro lançado por [onUse] mostra-se na revisão
/// ([errorText] traduz o erro).
class CaptureView extends ConsumerStatefulWidget {
  const CaptureView({
    super.key,
    required this.lens,
    required this.resolution,
    required this.overlay,
    required this.header,
    required this.useLabel,
    required this.retakeLabel,
    required this.permissionDeniedText,
    required this.onUse,
    required this.onClose,
    required this.errorText,
    this.initialFlash = FlashMode.off,
  });

  final CameraLens lens;
  final CameraResolution resolution;
  final FlashMode initialFlash;

  /// Moldura por cima da pré-visualização.
  final Widget overlay;

  /// Nome e instrução, no topo.
  final Widget header;

  final String useLabel;
  final String retakeLabel;
  final String permissionDeniedText;
  final Future<void> Function(Uint8List photo) onUse;
  final VoidCallback onClose;
  final String Function(Object error) errorText;

  @override
  ConsumerState<CaptureView> createState() => _CaptureViewState();
}

class _CaptureViewState extends ConsumerState<CaptureView> {
  CameraService? _camera;
  _Stage _stage = _Stage.permission;
  late FlashMode _flash = widget.initialFlash;
  Uint8List? _photo;
  bool _busy = false;
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
    final camera = ref.read(cameraServiceProvider)(
      lens: widget.lens,
      resolution: widget.resolution,
      initialFlash: widget.initialFlash,
    );
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
    if (photo == null || _busy) return;
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await widget.onUse(photo);
    } catch (error) {
      if (mounted) setState(() => _error = widget.errorText(error));
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final Widget body = switch (_stage) {
      _Stage.permission || _Stage.starting => const Center(
        child: CircularProgressIndicator(color: Colors.white),
      ),
      _Stage.denied => _CameraUnavailable(
        message: widget.permissionDeniedText,
        onClose: widget.onClose,
        onOpenSettings: () => ref.read(cameraPermissionProvider).openSettings(),
      ),
      _Stage.failed => _CameraUnavailable(
        message: l10n.cameraUnavailableNoGallery,
        onClose: widget.onClose,
      ),
      _Stage.preview => _Preview(
        camera: _camera!,
        flash: _flash,
        busy: _busy,
        overlay: widget.overlay,
        header: widget.header,
        onClose: widget.onClose,
        onFlash: _toggleFlash,
        onCapture: _capture,
      ),
      _Stage.review => _Review(
        photo: _photo!,
        busy: _busy,
        error: _error,
        useLabel: widget.useLabel,
        retakeLabel: widget.retakeLabel,
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
    required this.overlay,
    required this.header,
    required this.onClose,
    required this.onFlash,
    required this.onCapture,
  });

  final CameraService camera;
  final FlashMode flash;
  final bool busy;
  final Widget overlay;
  final Widget header;
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
        overlay,
        SafeArea(
          child: Column(
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  IconButton(
                    key: const Key('camera-close'),
                    tooltip: l10n.close,
                    color: Colors.white,
                    iconSize: 28,
                    onPressed: onClose,
                    icon: const Icon(Icons.close),
                  ),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.only(top: 8),
                      child: DefaultTextStyle.merge(
                        style: const TextStyle(
                          color: Colors.white,
                          shadows: [Shadow(blurRadius: 4)],
                        ),
                        textAlign: TextAlign.center,
                        child: header,
                      ),
                    ),
                  ),
                  IconButton(
                    key: const Key('camera-flash'),
                    tooltip: flashLabel,
                    color: Colors.white,
                    iconSize: 28,
                    onPressed: onFlash,
                    icon: Icon(flashIcon),
                  ),
                ],
              ),
              const Spacer(),
              Padding(
                padding: const EdgeInsets.only(bottom: 32),
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
            ],
          ),
        ),
      ],
    );
  }
}

class _Review extends StatelessWidget {
  const _Review({
    required this.photo,
    required this.busy,
    required this.error,
    required this.useLabel,
    required this.retakeLabel,
    required this.onRetake,
    required this.onUse,
  });

  final Uint8List photo;
  final bool busy;
  final String? error;
  final String useLabel;
  final String retakeLabel;
  final VoidCallback onRetake;
  final VoidCallback onUse;

  @override
  Widget build(BuildContext context) {
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
            if (busy) const LinearProgressIndicator(),
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
                          child: Text(retakeLabel),
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
                          child: Text(useLabel),
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

/// Sem permissão (com "Abrir definições") ou sem câmara.
class _CameraUnavailable extends StatelessWidget {
  const _CameraUnavailable({
    required this.message,
    required this.onClose,
    this.onOpenSettings,
  });

  final String message;
  final VoidCallback onClose;
  final VoidCallback? onOpenSettings;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
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
                onPressed: onClose,
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
                    message,
                    key: const Key('camera-permission-denied'),
                    textAlign: TextAlign.center,
                    style: theme.textTheme.titleMedium,
                  ),
                  const SizedBox(height: 24),
                  if (onOpenSettings != null)
                    FilledButton(
                      key: const Key('camera-open-settings'),
                      style: FilledButton.styleFrom(
                        minimumSize: const Size(64, 48),
                      ),
                      onPressed: onOpenSettings,
                      child: Text(l10n.cameraOpenSettings),
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
