import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/router.dart';

import '../../support/mock_api.dart';
import '../../support/vehicles.dart';

const _vehiclePath = '/api/v1/me/vehicles/$vehicleId';
const _driversPath = '$_vehiclePath/drivers';
const _d1 = '01930000-0000-7000-8000-000000000201';
const _d2 = '01930000-0000-7000-8000-000000000202';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi();
    stubCustomerSession(api);
    api
      ..on('GET', '/api/v1/me/vehicles', 200, VehicleFixtures.page([]))
      ..on('GET', _vehiclePath, 200, VehicleFixtures.vehicle(version: 3))
      ..on(
        'GET',
        '/api/v1/me/drivers',
        200,
        VehicleFixtures.page([
          VehicleFixtures.driver(id: _d1, fullName: 'Ana Maria Mussa'),
          VehicleFixtures.driver(id: _d2, fullName: 'Carlos Tembe'),
        ]),
      );
  });

  Future<void> openSheet(WidgetTester tester) async {
    final container = await pumpMoSeguros(
      tester,
      api,
      refreshToken: 'refresh-0',
      overrides: FakeDevice().overrides,
    );
    container.read(routerProvider).go('/vehicles/$vehicleId');
    await tester.pumpAndSettle();
    await tester.scrollUntilVisible(
      find.byKey(const Key('manage-drivers')),
      300,
      scrollable: find.byType(Scrollable).last,
    );
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('manage-drivers')));
    await tester.pumpAndSettle();
  }

  FilledButton saveButton(WidgetTester tester) =>
      tester.widget<FilledButton>(find.byKey(const Key('manage-drivers-save')));

  Radio<String> habitual(WidgetTester tester, String id) =>
      tester.widget<Radio<String>>(find.byKey(Key('driver-habitual-$id')));

  bool isHabitual(WidgetTester tester, String id) {
    final radio = find.byKey(Key('driver-habitual-$id'));
    // O RadioGroup marca o rádio com o valor escolhido.
    return tester
            .widget<RadioGroup<String>>(
              find.ancestor(
                of: radio,
                matching: find.byType(RadioGroup<String>),
              ),
            )
            .groupValue ==
        habitual(tester, id).value;
  }

  testWidgets('não grava sem habitual', (tester) async {
    await openSheet(tester);
    expect(find.text('Gerir condutores'), findsWidgets);
    expect(saveButton(tester).onPressed, isNotNull);

    await tester.tap(find.byKey(const Key('driver-check-$_d1')));
    await tester.pumpAndSettle();

    expect(find.text('Escolha o condutor habitual.'), findsOneWidget);
    expect(saveButton(tester).onPressed, isNull);
    await tester.tap(find.byKey(const Key('manage-drivers-save')));
    await tester.pumpAndSettle();
    expect(api.calls('PUT', _driversPath), isEmpty);
  });

  testWidgets('só um habitual; grava com a version do veículo', (tester) async {
    api.on(
      'PUT',
      _driversPath,
      200,
      VehicleFixtures.vehicle(
        version: 4,
        drivers: [
          {'driverId': _d2, 'fullName': 'Carlos Tembe', 'habitual': true},
          {'driverId': _d1, 'fullName': 'Ana Maria Mussa', 'habitual': false},
        ],
      ),
    );
    await openSheet(tester);

    await tester.tap(find.byKey(const Key('driver-check-$_d1')));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('driver-habitual-$_d1')));
    await tester.pumpAndSettle();
    expect(isHabitual(tester, _d1), isTrue);
    expect(saveButton(tester).onPressed, isNotNull);

    await tester.tap(find.byKey(const Key('driver-check-$_d2')));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('driver-habitual-$_d2')));
    await tester.pumpAndSettle();
    expect(isHabitual(tester, _d2), isTrue);
    expect(isHabitual(tester, _d1), isFalse);

    // Desmarcar o habitual tira-lhe a escolha.
    await tester.tap(find.byKey(const Key('driver-check-$_d2')));
    await tester.pumpAndSettle();
    expect(saveButton(tester).onPressed, isNull);
    await tester.tap(find.byKey(const Key('driver-check-$_d2')));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('driver-habitual-$_d2')));
    await tester.pumpAndSettle();

    await tester.tap(find.byKey(const Key('manage-drivers-save')));
    await tester.pumpAndSettle();

    expect(api.calls('PUT', _driversPath).single.data, {
      'drivers': [
        {'driverId': _d1, 'habitual': false},
        {'driverId': _d2, 'habitual': true},
      ],
      'version': 3,
    });
    expect(find.byKey(const Key('manage-drivers-save')), findsNothing);
  });

  testWidgets('erro do servidor fica na folha', (tester) async {
    api.on(
      'PUT',
      _driversPath,
      409,
      MockApi.problem(409, 'CONCURRENT_MODIFICATION'),
    );
    await openSheet(tester);
    await tester.tap(find.byKey(const Key('driver-check-$_d1')));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('driver-habitual-$_d1')));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('manage-drivers-save')));
    await tester.pumpAndSettle();

    expect(
      find.text(
        'Outra pessoa alterou estes dados entretanto. Actualize e tente de novo.',
      ),
      findsOneWidget,
    );
    expect(find.byKey(const Key('manage-drivers-save')), findsOneWidget);
  });

  testWidgets('sem condutores: "Registar condutor" abre o formulário', (
    tester,
  ) async {
    api.on('GET', '/api/v1/me/drivers', 200, VehicleFixtures.page([]));
    await openSheet(tester);

    expect(find.text('Ainda não registou condutores.'), findsOneWidget);
    await tester.tap(find.byKey(const Key('manage-drivers-register')));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('driver-fullName')), findsOneWidget);
  });
}
