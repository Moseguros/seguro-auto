import 'dart:io';

import 'package:camera/camera.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:permission_handler/permission_handler.dart';

export 'package:camera/camera.dart' show FlashMode;

/// Câmara do telemóvel: traseira (livrete, vistoria, documento do KYC) ou
/// frontal (selfie).
enum CameraLens { back, front }

/// Resolução da captura.
enum CameraResolution {
  /// `ResolutionPreset.high` (vistoria, S4-09 §4).
  high,

  /// `ResolutionPreset.veryHigh` (documentos: livrete e KYC).
  veryHigh,
}

/// Câmara usada nos ecrãs de captura (livrete, vistoria, KYC). Os testes usam uma
/// implementação falsa através de [cameraServiceProvider].
abstract interface class CameraService {
  Future<void> initialize();

  /// Pré-visualização a ocupar o espaço disponível.
  Widget preview();

  /// Tira a foto e devolve os bytes (JPEG). Não fica cópia em ficheiro.
  Future<Uint8List> capture();

  Future<void> setFlash(FlashMode mode);

  Future<void> dispose();
}

/// Cria a câmara de cada vez que um ecrã de captura abre.
typedef CameraServiceFactory =
    CameraService Function({
      CameraLens lens,
      CameraResolution resolution,
      FlashMode initialFlash,
    });

final cameraServiceProvider = Provider<CameraServiceFactory>(
  (ref) =>
      ({
        CameraLens lens = CameraLens.back,
        CameraResolution resolution = CameraResolution.veryHigh,
        FlashMode initialFlash = FlashMode.off,
      }) => DeviceCameraService(
        lens: lens,
        resolution: resolution,
        initialFlash: initialFlash,
      ),
);

/// Câmara do telemóvel (plugin `camera`), sem áudio e com a captura fixa em retrato.
class DeviceCameraService implements CameraService {
  DeviceCameraService({
    this.lens = CameraLens.back,
    this.resolution = CameraResolution.veryHigh,
    this.initialFlash = FlashMode.off,
  });

  final CameraLens lens;
  final CameraResolution resolution;
  final FlashMode initialFlash;

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
    final direction = lens == CameraLens.front
        ? CameraLensDirection.front
        : CameraLensDirection.back;
    final description = cameras.firstWhere(
      (camera) => camera.lensDirection == direction,
      orElse: () => cameras.first,
    );
    final controller = CameraController(
      description,
      resolution == CameraResolution.high
          ? ResolutionPreset.high
          : ResolutionPreset.veryHigh,
      enableAudio: false,
      imageFormatGroup: ImageFormatGroup.jpeg,
    );
    _controller = controller;
    await controller.initialize();
    await controller.lockCaptureOrientation(DeviceOrientation.portraitUp);
    try {
      await controller.setFlashMode(initialFlash);
    } on CameraException {
      // Câmara sem flash (a frontal, na maioria dos telemóveis).
    }
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
      // A foto não fica nos ficheiros temporários da câmara.
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

/// Permissão da câmara.
abstract interface class CameraPermission {
  /// Pede a permissão (se ainda não foi dada); `true` se ficar concedida.
  Future<bool> request();

  /// Abre as definições da app no sistema.
  Future<void> openSettings();
}

/// `permission_handler`.
class DeviceCameraPermission implements CameraPermission {
  const DeviceCameraPermission();

  @override
  Future<bool> request() async => (await Permission.camera.request()).isGranted;

  @override
  Future<void> openSettings() async {
    await openAppSettings();
  }
}

final cameraPermissionProvider = Provider<CameraPermission>(
  (ref) => const DeviceCameraPermission(),
);

/// Ordem do botão do flash: desligado → automático → sempre ligado.
FlashMode nextFlashMode(FlashMode mode) => switch (mode) {
  FlashMode.off => FlashMode.auto,
  FlashMode.auto => FlashMode.always,
  _ => FlashMode.off,
};
