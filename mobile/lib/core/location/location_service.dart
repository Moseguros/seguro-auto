import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:geolocator/geolocator.dart';

/// Coordenadas de uma fotografia.
typedef GeoPoint = ({double latitude, double longitude});

/// Localização do telemóvel (`geolocator`). Opcional: sem permissão ou sem resposta
/// a fotografia segue sem coordenadas. Os testes usam uma implementação falsa
/// através de [locationServiceProvider].
abstract interface class LocationService {
  /// Pede a permissão "durante a utilização" (se ainda não foi pedida).
  Future<bool> requestPermission();

  /// Posição actual (precisão média, até [timeout]); `null` sem permissão, sem
  /// serviço de localização ou sem resposta.
  Future<GeoPoint?> current({Duration timeout});
}

class DeviceLocationService implements LocationService {
  const DeviceLocationService();

  @override
  Future<bool> requestPermission() async {
    try {
      var permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
      }
      return permission == LocationPermission.always ||
          permission == LocationPermission.whileInUse;
    } on Object {
      return false;
    }
  }

  @override
  Future<GeoPoint?> current({
    Duration timeout = const Duration(seconds: 5),
  }) async {
    try {
      final permission = await Geolocator.checkPermission();
      if (permission != LocationPermission.always &&
          permission != LocationPermission.whileInUse) {
        return null;
      }
      final position = await Geolocator.getCurrentPosition(
        locationSettings: LocationSettings(
          accuracy: LocationAccuracy.medium,
          timeLimit: timeout,
        ),
      ).timeout(timeout);
      return (latitude: position.latitude, longitude: position.longitude);
    } on Object {
      // Sem serviço, sem permissão ou sem resposta a tempo: é permitido.
      return null;
    }
  }
}

final locationServiceProvider = Provider<LocationService>(
  (ref) => const DeviceLocationService(),
);
