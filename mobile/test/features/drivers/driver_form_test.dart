import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/features/drivers/driver_rules.dart';

import '../../support/mock_api.dart';
import '../../support/vehicles.dart';

final _today = DateTime(2026, 11, 3);

void main() {
  group('regras do condutor', () {
    test('data de nascimento: no passado e idade ≤ 100', () {
      expect(driverBirthDateError(null, _today), 'REQUIRED');
      expect(
        driverBirthDateError(DateTime(2026, 11, 3), _today),
        'INVALID_DATE',
      );
      expect(driverBirthDateError(DateTime(2010, 1, 1), _today), isNull);
      expect(driverBirthDateError(DateTime(1926, 11, 3), _today), isNull);
      expect(
        driverBirthDateError(DateTime(1925, 11, 2), _today),
        'INVALID_DATE',
      );
    });

    test('n.º da carta: ^[A-Z0-9/-]{5,20}\$ depois de normalizar', () {
      expect(licenceNumberError('', required: true), 'REQUIRED');
      expect(licenceNumberError('', required: false), isNull);
      expect(licenceNumberError('map 123/45-6', required: true), isNull);
      expect(normalizeLicenceNumber('map 123/45-6'), 'MAP123/45-6');
      expect(
        licenceNumberError('AB12', required: true),
        'INVALID_LICENCE_NUMBER',
      );
      expect(
        licenceNumberError('A' * 21, required: true),
        'INVALID_LICENCE_NUMBER',
      );
      expect(
        licenceNumberError('AB*123', required: true),
        'INVALID_LICENCE_NUMBER',
      );
    });

    test('categorias e data de emissão', () {
      expect(licenceCategoriesError([]), 'REQUIRED');
      expect(licenceCategoriesError(['B']), isNull);
      final birth = DateTime(1990, 5, 10);
      expect(licenceIssueDateError(null, birth, _today), 'REQUIRED');
      expect(
        licenceIssueDateError(DateTime(1990, 5, 9), birth, _today),
        'LICENCE_ISSUE_DATE_INVALID',
      );
      expect(
        licenceIssueDateError(DateTime(1990, 5, 10), birth, _today),
        isNull,
      );
      expect(licenceIssueDateError(_today, birth, _today), isNull);
      expect(
        licenceIssueDateError(DateTime(2026, 11, 4), birth, _today),
        'LICENCE_ISSUE_DATE_INVALID',
      );
    });
  });

  group('formulário do condutor', () {
    late MockApi api;

    setUp(() {
      api = MockApi();
      stubCustomerSession(api);
      api
        ..on('GET', '/api/v1/me/vehicles', 200, VehicleFixtures.page([]))
        ..on('GET', '/api/v1/me/drivers', 200, VehicleFixtures.page([]));
    });

    Future<void> open(WidgetTester tester, String location) async {
      final container = await pumpMoSeguros(
        tester,
        api,
        refreshToken: 'refresh-0',
        overrides: FakeDevice().overrides,
      );
      container.read(routerProvider).go(location);
      await tester.pumpAndSettle();
    }

    String textOf(WidgetTester tester, String key) => tester
        .widget<EditableText>(
          find.descendant(
            of: find.byKey(Key(key)),
            matching: find.byType(EditableText),
          ),
        )
        .controller
        .text;

    Future<void> tapKey(WidgetTester tester, String key) async {
      await tester.ensureVisible(find.byKey(Key(key)));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(Key(key)));
      await tester.pumpAndSettle();
    }

    Future<void> pickDate(WidgetTester tester, String key) async {
      await tapKey(tester, key);
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
    }

    testWidgets('"Sou eu" preenche nome e data de nascimento com o perfil', (
      tester,
    ) async {
      await open(tester, '/drivers/new');
      expect(textOf(tester, 'driver-fullName'), '');
      final before = api.calls('GET', '/api/v1/me/profile').length;

      await tapKey(tester, 'driver-isOwner');

      expect(api.calls('GET', '/api/v1/me/profile'), hasLength(before + 1));
      expect(textOf(tester, 'driver-fullName'), 'Ana Maria Machava');
      expect(textOf(tester, 'driver-birthDate'), isNotEmpty);
      expect(textOf(tester, 'driver-birthDate'), contains('1990'));
      // Continuam editáveis.
      await tester.enterText(
        find.byKey(const Key('driver-fullName')),
        'Ana Machava',
      );
      expect(textOf(tester, 'driver-fullName'), 'Ana Machava');
    });

    testWidgets('registar envia o pedido com as categorias e isOwner', (
      tester,
    ) async {
      api.on('POST', '/api/v1/me/drivers', 201, VehicleFixtures.driver());
      await open(tester, '/drivers/new');
      await tapKey(tester, 'driver-isOwner');
      await tester.enterText(
        find.byKey(const Key('driver-licenceNumber')),
        'map 1234567',
      );
      await tapKey(tester, 'licence-category-B');
      await tapKey(tester, 'licence-category-A');
      await pickDate(tester, 'driver-licenceIssueDate');
      await tapKey(tester, 'driver-save');

      final body = api.calls('POST', '/api/v1/me/drivers').single.data as Map;
      expect(body['fullName'], 'Ana Maria Machava');
      expect(body['birthDate'], '1990-05-10');
      expect(body['licenceNumber'], 'MAP1234567');
      expect(body['licenceCategories'], ['A', 'B']);
      expect(body['isOwner'], true);
      expect(body['licenceIssueDate'], isA<String>());
      // Volta à lista de condutores.
      expect(find.byKey(const Key('driver-save')), findsNothing);
    });

    testWidgets('erros no ecrã e DRIVER_ALREADY_REGISTERED no campo da carta', (
      tester,
    ) async {
      await open(tester, '/drivers/new');
      await tapKey(tester, 'driver-save');
      expect(find.text('Campo obrigatório.'), findsWidgets);
      expect(api.calls('POST', '/api/v1/me/drivers'), isEmpty);

      api.on(
        'POST',
        '/api/v1/me/drivers',
        409,
        MockApi.problem(409, 'DRIVER_ALREADY_REGISTERED'),
      );
      await tapKey(tester, 'driver-isOwner');
      await tester.enterText(
        find.byKey(const Key('driver-licenceNumber')),
        'MAP1234567',
      );
      await tapKey(tester, 'licence-category-B');
      await pickDate(tester, 'driver-licenceIssueDate');
      await tapKey(tester, 'driver-save');

      expect(
        find.descendant(
          of: find.byKey(const Key('driver-licenceNumber')),
          matching: find.text('Já tem um condutor com esta carta.'),
        ),
        findsOneWidget,
      );
    });

    testWidgets('alterar sem mexer na carta envia licenceNumber: null', (
      tester,
    ) async {
      api
        ..on(
          'GET',
          '/api/v1/me/drivers/$driverId',
          200,
          VehicleFixtures.driver(version: 2),
        )
        ..on(
          'GET',
          '/api/v1/me/drivers',
          200,
          VehicleFixtures.page([VehicleFixtures.driver(version: 2)]),
        )
        ..on(
          'PUT',
          '/api/v1/me/drivers/$driverId',
          200,
          VehicleFixtures.driver(version: 3),
        );
      await open(tester, '/drivers/$driverId/edit');

      expect(find.text('****4567'), findsOneWidget);
      expect(find.byKey(const Key('driver-licenceNumber')), findsNothing);
      await tester.enterText(
        find.byKey(const Key('driver-fullName')),
        'Ana Maria Mussa Tembe',
      );
      await tapKey(tester, 'driver-save');

      final put = api.calls('PUT', '/api/v1/me/drivers/$driverId').single;
      final body = put.data as Map;
      expect(body['licenceNumber'], isNull);
      expect(body.containsKey('licenceNumber'), isFalse);
      expect(body['version'], 2);
      expect(body['fullName'], 'Ana Maria Mussa Tembe');
      expect(body['licenceCategories'], ['B']);
    });

    testWidgets('"Alterar" na carta abre o campo vazio e envia o número novo', (
      tester,
    ) async {
      api
        ..on(
          'GET',
          '/api/v1/me/drivers/$driverId',
          200,
          VehicleFixtures.driver(),
        )
        ..on(
          'PUT',
          '/api/v1/me/drivers/$driverId',
          200,
          VehicleFixtures.driver(version: 1),
        );
      await open(tester, '/drivers/$driverId/edit');
      await tapKey(tester, 'driver-change-licence');
      expect(textOf(tester, 'driver-licenceNumber'), '');
      await tester.enterText(
        find.byKey(const Key('driver-licenceNumber')),
        'NEW12345',
      );
      await tapKey(tester, 'driver-save');

      expect(
        (api.calls('PUT', '/api/v1/me/drivers/$driverId').single.data
            as Map)['licenceNumber'],
        'NEW12345',
      );
    });

    testWidgets('"Sou eu" escondido quando já há outro condutor "Eu"', (
      tester,
    ) async {
      api.on(
        'GET',
        '/api/v1/me/drivers',
        200,
        VehicleFixtures.page([
          VehicleFixtures.driver(
            id: 'owner',
            fullName: 'Eu Mesmo',
            isOwner: true,
          ),
        ]),
      );
      await open(tester, '/drivers/new');
      expect(find.byKey(const Key('driver-isOwner')), findsNothing);
    });
  });

  group('lista de condutores', () {
    testWidgets('cartão e "Retirar" com DRIVER_IS_HABITUAL na confirmação', (
      tester,
    ) async {
      final api = MockApi();
      stubCustomerSession(api);
      api
        ..on(
          'GET',
          '/api/v1/me/drivers',
          200,
          VehicleFixtures.page([
            VehicleFixtures.driver(
              isOwner: true,
              vehicleIds: [vehicleId],
              categories: ['B', 'XYZ'],
            ),
          ]),
        )
        ..on(
          'DELETE',
          '/api/v1/me/drivers/$driverId',
          409,
          MockApi.problem(409, 'DRIVER_IS_HABITUAL'),
        );
      final container = await pumpMoSeguros(
        tester,
        api,
        refreshToken: 'refresh-0',
        overrides: FakeDevice().overrides,
      );
      container.read(routerProvider).go('/drivers');
      await tester.pumpAndSettle();

      expect(find.text('Ana Maria Mussa'), findsOneWidget);
      expect(find.text('Eu'), findsOneWidget);
      expect(find.text('Categorias: B — Ligeiros, XYZ'), findsOneWidget);
      expect(find.text('Carta: ****4567'), findsOneWidget);
      expect(find.text('1 veículo'), findsOneWidget);

      await tester.tap(find.byKey(const Key('driver-menu-$driverId')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Retirar').last);
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(const Key('confirm-remove')));
      await tester.pumpAndSettle();

      expect(
        find.text(
          'Este condutor é o habitual de um veículo. Escolha outro habitual primeiro.',
        ),
        findsOneWidget,
      );
      expect(find.byType(AlertDialog), findsOneWidget);
    });
  });
}
