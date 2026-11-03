import 'dart:io';

import 'package:camera/camera.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

import 'document_camera.dart';

/// Câmara traseira do telemóvel (plugin `camera`), em `ResolutionPreset.veryHigh`,
/// sem áudio e com a captura fixa em retrato.
class DeviceDocumentCamera implements DocumentCamera {
  CameraController? _controller;

  CameraController get _ready {
    final controller = _controller;
    if (controller == null || !controller.value.isInitialized) {
      throw StateError('Câmara não inicializada');
    }
    return controller;
  }

  @override
  Future<void> initialize() async {
    final cameras = await availableCameras();
    if (cameras.isEmpty) throw CameraException('NoCamera', null);
    final back = cameras.firstWhere(
      (camera) => camera.lensDirection == CameraLensDirection.back,
      orElse: () => cameras.first,
    );
    final controller = CameraController(
      back,
      ResolutionPreset.veryHigh,
      enableAudio: false,
      imageFormatGroup: ImageFormatGroup.jpeg,
    );
    _controller = controller;
    await controller.initialize();
    await controller.lockCaptureOrientation(DeviceOrientation.portraitUp);
    await controller.setFlashMode(FlashMode.off);
  }

  @override
  Widget preview() {
    final controller = _ready;
    final size = controller.value.previewSize;
    if (size == null) return CameraPreview(controller);
    // A pré-visualização ocupa o ecrã todo (corta as margens, como `BoxFit.cover`).
    return ClipRect(
      child: FittedBox(
        fit: BoxFit.cover,
        child: SizedBox(
          // Em retrato, a largura é o lado menor do sensor.
          width: size.shortestSide,
          height: size.longestSide,
          child: CameraPreview(controller),
        ),
      ),
    );
  }

  @override
  Future<Uint8List> capture() async {
    final file = await _ready.takePicture();
    try {
      return await file.readAsBytes();
    } finally {
      // A foto não fica nos ficheiros da app.
      try {
        await File(file.path).delete();
      } on FileSystemException {
        // Temporário da câmara que já não existe.
      }
    }
  }

  @override
  Future<void> setFlash(FlashMode mode) => _ready.setFlashMode(mode);

  @override
  Future<void> dispose() async {
    final controller = _controller;
    _controller = null;
    await controller?.dispose();
  }
}
