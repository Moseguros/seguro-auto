import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/core/widgets/cta_button.dart';

import '../../support/mock_api.dart';
import '../../support/vehicles.dart';

void main() {
  late MockApi api;
  late FakeDevice device;

  setUp(() {
    api = MockApi();
    device = FakeDevice();
    stubCustomerSession(api);
  });

  Future<GoRouter> openVehicles(WidgetTester tester) async {
    final container = await pumpMoSeguros(
      tester,
      api,
      refreshToken: 'refresh-0',
      overrides: device.overrides,
    );
    final router = container.read(routerProvider);
    router.go('/vehicles');
    return router;
  }

  group('barra inferior', () {
    testWidgets('os três destinos navegam e "Condutores" abre /drivers', (
      tester,
    ) async {
      api
        ..on('GET', '/api/v1/me/vehicles', 200, VehicleFixtures.page([]))
        ..on('GET', '/api/v1/me/drivers', 200, VehicleFixtures.page([]));
      final container = await pumpMoSeguros(
        tester,
        api,
        refreshToken: 'refresh-0',
        overrides: device.overrides,
      );
      final router = container.read(routerProvider);
      String location() =>
          router.routerDelegate.currentConfiguration.uri.toString();

      expect(find.byType(NavigationBar), findsOneWidget);
      expect(find.byType(NavigationDestination), findsNWidgets(3));
      expect(find.text('Olá, Ana'), findsOneWidget);

      await tester.tap(find.byKey(const Key('nav-vehicles')));
      await tester.pumpAndSettle();
      expect(location(), '/vehicles');
      expect(find.text('Ainda não registou nenhum veículo.'), findsOneWidget);

      await tester.tap(find.text('Condutores'));
      await tester.pumpAndSettle();
      expect(location(), '/drivers');
      expect(find.text('Ainda não registou condutores.'), findsOneWidget);

      await tester.tap(find.byKey(const Key('nav-profile')));
      await tester.pumpAndSettle();
      expect(location(), '/profile');
      expect(find.text('Dados pessoais'), findsOneWidget);

      await tester.tap(find.byKey(const Key('nav-home')));
      await tester.pumpAndSettle();
      expect(location(), '/');
      expect(find.text('Olá, Ana'), findsOneWidget);

      // O ramo dos veículos lembra-se do segmento escolhido.
      await tester.tap(find.byKey(const Key('nav-vehicles')));
      await tester.pumpAndSettle();
      expect(location(), '/drivers');
      await tester.tap(find.text('Veículos').first);
      await tester.pumpAndSettle();
      expect(location(), '/vehicles');
    });

    testWidgets('o ícone de perfil da barra superior saiu do início', (
      tester,
    ) async {
      await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');
      expect(find.byKey(const Key('open-profile')), findsNothing);
      expect(find.byKey(const Key('nav-profile')), findsOneWidget);
    });
  });

  group('lista de veículos', () {
    testWidgets('a carregar mostra o indicador', (tester) async {
      api.on(
        'GET',
        '/api/v1/me/vehicles',
        200,
        VehicleFixtures.page([]),
        const {},
        null,
        const Duration(seconds: 2),
      );
      await openVehicles(tester);
      await tester.pump();
      await tester.pump(const Duration(milliseconds: 100));

      expect(find.byKey(const Key('list-loading')), findsOneWidget);
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('list-empty')), findsOneWidget);
    });

    testWidgets('vazio: texto e botão "Acrescentar veículo"', (tester) async {
      api.on('GET', '/api/v1/me/vehicles', 200, VehicleFixtures.page([]));
      await openVehicles(tester);
      await tester.pumpAndSettle();

      expect(find.text('Ainda não registou nenhum veículo.'), findsOneWidget);
      await tester.tap(find.byKey(const Key('vehicles-add-empty')));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('vehicle-plate')), findsOneWidget);
    });

    testWidgets('erro com "Tentar de novo"', (tester) async {
      api.on(
        'GET',
        '/api/v1/me/vehicles',
        500,
        MockApi.problem(500, 'INTERNAL_ERROR'),
      );
      await openVehicles(tester);
      await tester.pumpAndSettle();

      expect(
        find.text('Erro interno do servidor. Tente mais tarde.'),
        findsOneWidget,
      );
      api.on(
        'GET',
        '/api/v1/me/vehicles',
        200,
        VehicleFixtures.page([VehicleFixtures.vehicle()]),
      );
      await tester.tap(find.text('Tentar de novo'));
      await tester.pumpAndSettle();

      expect(find.byKey(const Key('list-error')), findsNothing);
      expect(find.text('AAA-100-MC'), findsOneWidget);
      expect(api.calls('GET', '/api/v1/me/vehicles'), hasLength(2));
    });

    testWidgets('sem rede: mensagem de rede e "Tentar de novo"', (
      tester,
    ) async {
      api.adapter.onGet(
        '/api/v1/me/vehicles',
        (server) => server.throws(
          0,
          DioException.connectionError(
            requestOptions: RequestOptions(path: '/api/v1/me/vehicles'),
            reason: 'offline',
          ),
        ),
      );
      await openVehicles(tester);
      await tester.pumpAndSettle();

      expect(find.byKey(const Key('list-error')), findsOneWidget);
      expect(find.textContaining('ligação'), findsOneWidget);
      expect(find.text('Tentar de novo'), findsOneWidget);
    });

    testWidgets('cartão: matrícula, dados, habitual e "Livrete em falta"', (
      tester,
    ) async {
      api.on(
        'GET',
        '/api/v1/me/vehicles',
        200,
        VehicleFixtures.page([
          VehicleFixtures.vehicle(
            drivers: [
              {
                'driverId': driverId,
                'fullName': 'Ana Maria Mussa',
                'habitual': true,
              },
            ],
          ),
          VehicleFixtures.vehicle(
            id: 'v2',
            plate: 'MLA-12-34',
            inatroVerified: false,
            registrationDoc: VehicleFixtures.registrationDoc(),
          ),
        ]),
      );
      await openVehicles(tester);
      await tester.pumpAndSettle();

      final first = find.byKey(const Key('vehicle-card-$vehicleId'));
      expect(
        find.descendant(of: first, matching: find.text('AAA-100-MC')),
        findsOneWidget,
      );
      expect(
        find.descendant(
          of: first,
          matching: find.text('Toyota Corolla · 2018'),
        ),
        findsOneWidget,
      );
      expect(
        find.descendant(
          of: first,
          matching: find.text('Ligeiro de passageiros · Particular'),
        ),
        findsOneWidget,
      );
      expect(
        find.descendant(
          of: first,
          matching: find.text('Condutor habitual: Ana Maria Mussa'),
        ),
        findsOneWidget,
      );
      expect(
        find.descendant(of: first, matching: find.text('Livrete em falta')),
        findsOneWidget,
      );
      expect(
        find.descendant(
          of: first,
          matching: find.text('Confirmado no registo'),
        ),
        findsOneWidget,
      );

      final second = find.byKey(const Key('vehicle-card-v2'));
      expect(
        find.descendant(of: second, matching: find.text('Livrete em falta')),
        findsNothing,
      );
      expect(
        find.descendant(
          of: second,
          matching: find.text('Sem condutor habitual'),
        ),
        findsOneWidget,
      );
      expect(find.byKey(const Key('vehicles-add')), findsOneWidget);
      // Nenhum botão laranja nos ecrãs de veículos.
      expect(find.byType(CtaButton), findsNothing);
      expect(tester.takeException(), isNull);
    });
  });
}
