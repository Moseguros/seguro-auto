import 'dart:typed_data';

import 'package:camera/camera.dart' show FlashMode;
import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:permission_handler/permission_handler.dart';

import 'device_document_camera.dart';

export 'package:camera/camera.dart' show FlashMode;

/// Câmara usada para fotografar documentos. Os testes usam uma implementação falsa
/// através de [documentCameraProvider].
abstract interface class DocumentCamera {
  Future<void> initialize();

  /// Pré-visualização a ocupar o espaço disponível.
  Widget preview();

  /// Tira a foto e devolve os bytes (JPEG). Não fica cópia em ficheiro.
  Future<Uint8List> capture();

  Future<void> setFlash(FlashMode mode);

  Future<void> dispose();
}

/// Cria a câmara de cada vez que o ecrã abre.
typedef DocumentCameraFactory = DocumentCamera Function();

final documentCameraProvider = Provider<DocumentCameraFactory>(
  (ref) => DeviceDocumentCamera.new,
);

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
