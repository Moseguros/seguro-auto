import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../vehicles/vehicles_controller.dart';

/// Condutores do cliente (`listMyDrivers`; o máximo são 10).
///
/// O número da carta nunca está aqui: a API só devolve a máscara.
class DriversController extends AsyncNotifier<List<DriverResponse>> {
  @override
  Future<List<DriverResponse>> build() async {
    final page =
        (await ref
                .read(apiClientProvider)
                .vehicle
                .listMyDrivers(size: 50, sort: 'createdAt,desc'))
            .data!;
    return page.content.toList();
  }
}

final driversControllerProvider =
    AsyncNotifierProvider.autoDispose<DriversController, List<DriverResponse>>(
      DriversController.new,
      retry: (_, _) => null,
    );

/// Um condutor (`getMyDriver`).
final driverProvider = FutureProvider.autoDispose
    .family<DriverResponse, String>(
      (ref, id) async =>
          (await ref.watch(apiClientProvider).vehicle.getMyDriver(id: id))
              .data!,
      retry: (_, _) => null,
    );

/// Escritas sobre condutores; os veículos também se voltam a ler (nomes e habituais).
class DriverRepository {
  DriverRepository(this._ref);

  final Ref _ref;

  VehicleApi get _api => _ref.read(apiClientProvider).vehicle;

  void _changed([String? id]) {
    _ref.invalidate(driversControllerProvider);
    if (id != null) _ref.invalidate(driverProvider(id));
    _ref.invalidate(vehiclesControllerProvider);
    _ref.invalidate(vehicleProvider);
  }

  Future<DriverResponse> create(CreateDriverRequest request) async {
    final driver = (await _api.createMyDriver(
      createDriverRequest: request,
    )).data!;
    _changed(driver.id);
    return driver;
  }

  Future<DriverResponse> update(String id, UpdateDriverRequest request) async {
    final driver = (await _api.updateMyDriver(
      id: id,
      updateDriverRequest: request,
    )).data!;
    _changed(id);
    return driver;
  }

  Future<void> delete(String id) async {
    await _api.deleteMyDriver(id: id);
    _changed(id);
  }

  /// Perfil do cliente, para "Sou eu" (`getMyProfile`).
  Future<CustomerProfileResponse> myProfile() async =>
      (await _ref.read(apiClientProvider).identity.getMyProfile()).data!;
}

final driverRepositoryProvider = Provider<DriverRepository>(
  DriverRepository.new,
);
