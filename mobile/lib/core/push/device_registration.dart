import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:moseguros_api/moseguros_api.dart';
import 'package:package_info_plus/package_info_plus.dart';

import '../api/api_client.dart';
import 'push_service.dart';

/// Guarda do `id` do dispositivo registado (`registerMyDevice`).
abstract interface class DeviceIdStorage {
  Future<String?> read();

  Future<void> write(String id);

  Future<void> delete();
}

/// `flutter_secure_storage`, chave `device_id`.
class SecureDeviceIdStorage implements DeviceIdStorage {
  SecureDeviceIdStorage([FlutterSecureStorage? storage])
    : _storage = storage ?? const FlutterSecureStorage();

  static const key = 'device_id';

  final FlutterSecureStorage _storage;

  @override
  Future<String?> read() => _storage.read(key: key);

  @override
  Future<void> write(String id) => _storage.write(key: key, value: id);

  @override
  Future<void> delete() => _storage.delete(key: key);
}

/// Guarda em memória (testes).
class MemoryDeviceIdStorage implements DeviceIdStorage {
  MemoryDeviceIdStorage([this.id]);

  String? id;

  @override
  Future<String?> read() async => id;

  @override
  Future<void> write(String id) async => this.id = id;

  @override
  Future<void> delete() async => id = null;
}

final deviceIdStorageProvider = Provider<DeviceIdStorage>(
  (ref) => SecureDeviceIdStorage(),
);

/// Tamanho máximo do `appVersion` aceite pela API.
const maxAppVersionLength = 30;

/// Versão da app para o registo: `version+buildNumber` (≤ 30 caracteres).
final appVersionProvider = FutureProvider<String?>((ref) async {
  try {
    final info = await PackageInfo.fromPlatform();
    final version = info.buildNumber.isEmpty
        ? info.version
        : '${info.version}+${info.buildNumber}';
    return version.length > maxAppVersionLength
        ? version.substring(0, maxAppVersionLength)
        : version;
  } on Object {
    return null;
  }
}, retry: (_, _) => null);

/// Plataforma do dispositivo para a API.
final devicePlatformProvider = Provider<DevicePlatform>(
  (ref) => defaultTargetPlatform == TargetPlatform.iOS
      ? DevicePlatform.IOS
      : DevicePlatform.ANDROID,
);

/// Registo do dispositivo para push (S6-07 §4).
///
/// Com sessão e um token de push, `registerMyDevice` e guarda o `id` devolvido
/// (`device_id`). Repete-se em cada arranque com sessão (o backend actualiza o
/// `last_seen_at`) e quando o token muda. No logout, `unregisterMyDevice` antes
/// de largar o token de acesso.
class DeviceRegistration {
  DeviceRegistration(this._ref);

  final Ref _ref;

  /// Tempo máximo do `unregisterMyDevice` no logout (sem rede não se espera).
  static const unregisterTimeout = Duration(seconds: 5);

  ApiClient get _api => _ref.read(apiClientProvider);

  /// Regista o dispositivo; não faz nada sem sessão ou sem token. Erros
  /// ignorados (repete no próximo arranque).
  Future<void> register({String? token}) async {
    if (_api.session?.accessToken == null) return;
    try {
      final pushToken = token ?? await _ref.read(pushServiceProvider).token();
      if (pushToken == null || pushToken.isEmpty) return;
      final appVersion = await _ref.read(appVersionProvider.future);
      final response = await _api.notification.registerMyDevice(
        registerDeviceRequest: RegisterDeviceRequest(
          (b) => b
            ..token = pushToken
            ..platform = _ref.read(devicePlatformProvider)
            ..appVersion = appVersion,
        ),
      );
      final id = response.data?.id;
      if (id != null) await _ref.read(deviceIdStorageProvider).write(id);
    } on Object {
      // Sem rede ou recusado: fica para a próxima.
    }
  }

  /// Anula o registo (logout), com o token de acesso ainda válido, e esquece o
  /// `device_id`. Erros ignorados.
  Future<void> unregister() async {
    try {
      final storage = _ref.read(deviceIdStorageProvider);
      final id = await storage.read();
      if (id == null) return;
      try {
        await _api.notification
            .unregisterMyDevice(id: id)
            .timeout(unregisterTimeout);
      } on Object {
        // Sem rede: o backend desactiva-o quando o FCM recusar o token.
      }
      await storage.delete();
    } on Object {
      // Sem guarda segura (testes): não há nada a anular.
    }
  }
}

final deviceRegistrationProvider = Provider<DeviceRegistration>(
  DeviceRegistration.new,
);
