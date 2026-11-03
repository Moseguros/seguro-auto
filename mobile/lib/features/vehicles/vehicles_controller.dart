import 'dart:typed_data';

import 'package:built_collection/built_collection.dart';
import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../drivers/drivers_controller.dart';

/// Domínios das listas de referência usados nos formulários (S2-02).
abstract final class ReferenceDomains {
  static const vehicleMake = 'VEHICLE_MAKE';
  static const vehicleModel = 'VEHICLE_MODEL';
  static const licenceCategory = 'LICENCE_CATEGORY';
}

/// Veículos do cliente (`listMyVehicles`, os 50 mais recentes; o máximo são 20).
class VehiclesController extends AsyncNotifier<List<VehicleResponse>> {
  @override
  Future<List<VehicleResponse>> build() async {
    final page =
        (await ref
                .read(apiClientProvider)
                .vehicle
                .listMyVehicles(size: 50, sort: 'createdAt,desc'))
            .data!;
    return page.content.toList();
  }
}

final vehiclesControllerProvider =
    AsyncNotifierProvider.autoDispose<
      VehiclesController,
      List<VehicleResponse>
    >(VehiclesController.new, retry: (_, _) => null);

/// Um veículo (`getMyVehicle`).
final vehicleProvider = FutureProvider.autoDispose
    .family<VehicleResponse, String>(
      (ref, id) async =>
          (await ref.watch(apiClientProvider).vehicle.getMyVehicle(id: id))
              .data!,
      retry: (_, _) => null,
    );

/// Valores activos de uma lista de referência (`listReferenceValues`).
final referenceValuesProvider = FutureProvider.autoDispose
    .family<List<PublicReferenceValueResponse>, String>(
      (ref, domain) async =>
          (await ref
                  .watch(apiClientProvider)
                  .catalog
                  .listReferenceValues(domain: domain))
              .data!
              .values
              .toList(),
      retry: (_, _) => null,
    );

/// Exemplos dos formatos de matrícula activos (`listActivePlateFormats`).
final plateExamplesProvider = FutureProvider.autoDispose<List<String>>(
  (ref) async => [
    for (final format
        in (await ref.watch(apiClientProvider).catalog.listActivePlateFormats())
            .data!
            .formats)
      format.example,
  ],
  retry: (_, _) => null,
);

/// Escritas sobre veículos. Depois de cada uma, as listas e o detalhe voltam a
/// ser lidos.
class VehicleRepository {
  VehicleRepository(this._ref);

  final Ref _ref;

  VehicleApi get _api => _ref.read(apiClientProvider).vehicle;

  void _changed([String? id]) {
    _ref.invalidate(vehiclesControllerProvider);
    if (id != null) _ref.invalidate(vehicleProvider(id));
  }

  Future<RegistryLookupResponse> lookup(String plate) async =>
      (await _api.lookupVehicleRegistry(plate: plate)).data!;

  Future<VehicleResponse> create(CreateVehicleRequest request) async {
    final vehicle = (await _api.createMyVehicle(
      createVehicleRequest: request,
    )).data!;
    _changed(vehicle.id);
    return vehicle;
  }

  Future<VehicleResponse> update(
    String id,
    UpdateVehicleRequest request,
  ) async {
    final vehicle = (await _api.updateMyVehicle(
      id: id,
      updateVehicleRequest: request,
    )).data!;
    _changed(id);
    return vehicle;
  }

  Future<void> delete(String id) async {
    await _api.deleteMyVehicle(id: id);
    _changed();
    _ref.invalidate(driversControllerProvider);
  }

  /// `uploadMyVehicleRegistrationDoc` (multipart, parte `file`).
  Future<VehicleResponse> uploadRegistrationDoc(
    String id,
    Uint8List bytes, {
    required String filename,
    required String contentType,
    ProgressCallback? onProgress,
  }) async {
    final vehicle = (await _api.uploadMyVehicleRegistrationDoc(
      id: id,
      file: MultipartFile.fromBytes(
        bytes,
        filename: filename,
        contentType: DioMediaType.parse(contentType),
      ),
      onSendProgress: onProgress,
    )).data!;
    _changed(id);
    return vehicle;
  }

  /// `updateMyVehicleDrivers` com a `version` do veículo.
  Future<VehicleResponse> updateDrivers(
    String id, {
    required Map<String, bool> drivers,
    required int version,
  }) async {
    final vehicle = (await _api.updateMyVehicleDrivers(
      id: id,
      updateVehicleDriversRequest: UpdateVehicleDriversRequest(
        (b) => b
          ..version = version
          ..drivers = ListBuilder([
            for (final entry in drivers.entries)
              VehicleDriverAssignment(
                (d) => d
                  ..driverId = entry.key
                  ..habitual = entry.value,
              ),
          ]),
      ),
    )).data!;
    _changed(id);
    _ref.invalidate(driversControllerProvider);
    return vehicle;
  }
}

final vehicleRepositoryProvider = Provider<VehicleRepository>(
  VehicleRepository.new,
);
