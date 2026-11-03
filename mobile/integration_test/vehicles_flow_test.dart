import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import '../test/support/mock_api.dart';
import '../test/support/vehicles.dart';

/// Fluxo principal do S2-07 com a API simulada e a câmara falsa.
///
/// Num emulador Android:
///   flutter test integration_test/vehicles_flow_test.dart --flavor dev
/// Sem emulador: `flutter test integration_test/vehicles_flow_test.dart -d flutter-tester`.
Future<void> pumpUntilFound(WidgetTester tester, Finder finder) async {
  for (var i = 0; i < 100 && finder.evaluate().isEmpty; i++) {
    await tester.pump(const Duration(milliseconds: 100));
  }
  await tester.pumpAndSettle();
}

Future<void> tapVisible(WidgetTester tester, Finder finder) async {
  await pumpUntilFound(tester, finder);
  await tester.ensureVisible(finder);
  await tester.pumpAndSettle();
  await tester.tap(finder);
  await tester.pumpAndSettle();
}

/// Traz para o ecrã um widget já construído mas fora da área visível da lista.
/// Num emulador o arrastar sintético do `scrollUntilVisible` não rola o detalhe
/// depois do fluxo da câmara; o `ensureVisible` rola sem gesto.
Future<void> scrollIntoView(WidgetTester tester, String key) async {
  await tester.ensureVisible(find.byKey(Key(key), skipOffstage: false));
  await tester.pumpAndSettle();
}

Future<void> choose(WidgetTester tester, String key, String option) async {
  await tapVisible(tester, find.byKey(Key(key)));
  await tester.tap(find.text(option).last);
  await tester.pumpAndSettle();
}

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets(
    'início → Veículos → acrescentar pelo registo → livrete com a câmara → '
    'condutor "Sou eu" habitual → lista',
    (tester) async {
      final api = MockApi();
      final device = FakeDevice();
      stubCustomerSession(api);

      // Estado do "servidor".
      Map<String, Object?>? vehicle;
      Map<String, Object?>? driver;
      List<Map<String, Object?>> vehicles() => [?vehicle];
      List<Map<String, Object?>> drivers() => [?driver];
      const vehiclePath = '/api/v1/me/vehicles/$vehicleId';

      api
        ..on(
          'GET',
          '/api/v1/me/vehicles',
          200,
          (RequestOptions _) => VehicleFixtures.page(vehicles()),
        )
        ..on(
          'GET',
          '/api/v1/me/drivers',
          200,
          (RequestOptions _) => VehicleFixtures.page(drivers()),
        )
        ..on(
          'GET',
          '/api/v1/me/vehicles/registry-lookup',
          200,
          VehicleFixtures.registry(),
        )
        ..on('POST', '/api/v1/me/vehicles', 201, (RequestOptions _) {
          vehicle = VehicleFixtures.vehicle();
          return vehicle;
        })
        ..on('GET', vehiclePath, 200, (RequestOptions _) => vehicle)
        ..on('POST', '$vehiclePath/registration-doc', 200, (RequestOptions _) {
          vehicle = {
            ...vehicle!,
            'registrationDoc': VehicleFixtures.registrationDoc(),
            'version': 1,
          };
          return vehicle;
        })
        ..onBytes(
          '/api/v1/files/01930000-0000-7000-8000-000000000301',
          tinyPng,
          'image/jpeg',
        )
        ..on('POST', '/api/v1/me/drivers', 201, (RequestOptions request) {
          final body = request.data as Map;
          driver = VehicleFixtures.driver(
            fullName: body['fullName'] as String,
            isOwner: body['isOwner'] == true,
          );
          return driver;
        })
        ..on('PUT', '$vehiclePath/drivers', 200, (RequestOptions request) {
          final body = request.data as Map;
          expect(body['version'], 1);
          vehicle = {
            ...vehicle!,
            'drivers': [
              {
                'driverId': driverId,
                'fullName': driver!['fullName'],
                'habitual': true,
              },
            ],
            'version': 2,
          };
          driver = {
            ...driver!,
            'vehicleIds': [vehicleId],
          };
          return vehicle;
        });

      await pumpMoSeguros(
        tester,
        api,
        refreshToken: 'refresh-0',
        overrides: device.overrides,
      );

      // Início → Veículos (lista vazia).
      await pumpUntilFound(tester, find.text('Olá, Ana'));
      await tester.tap(find.byKey(const Key('nav-vehicles')));
      await tester.pumpAndSettle();
      await pumpUntilFound(
        tester,
        find.text('Ainda não registou nenhum veículo.'),
      );

      // Acrescentar AAA-100-MC pelo registo.
      await tapVisible(tester, find.byKey(const Key('vehicles-add-empty')));
      await pumpUntilFound(tester, find.byKey(const Key('vehicle-plate')));
      await tester.enterText(
        find.byKey(const Key('vehicle-plate')),
        'AAA-100-MC',
      );
      await tapVisible(tester, find.byKey(const Key('registry-lookup')));
      expect(find.byKey(const Key('registry-filled')), findsOneWidget);
      await choose(tester, 'vehicle-usage', 'Particular');
      await choose(tester, 'vehicle-province', 'Maputo Cidade');
      await tapVisible(tester, find.byKey(const Key('vehicle-save')));

      // Detalhe → "Enviar livrete" → "Tirar foto" → "Usar foto".
      await pumpUntilFound(tester, find.byKey(const Key('vehicle-edit')));
      expect(
        find.text('Veículo acrescentado. Envie agora a foto do livrete.'),
        findsOneWidget,
      );
      await tester.scrollUntilVisible(
        find.byKey(const Key('doc-upload')),
        300,
        scrollable: find.byType(Scrollable).last,
      );
      await tapVisible(tester, find.byKey(const Key('doc-upload')));
      await tapVisible(tester, find.text('Tirar foto'));
      await tapVisible(tester, find.byKey(const Key('camera-shutter')));
      await tapVisible(tester, find.text('Usar foto'));
      await pumpUntilFound(tester, find.text('Livrete guardado'));
      expect(device.compressor.calls, hasLength(1));
      final upload = api.calls('POST', '$vehiclePath/registration-doc').single;
      expect(
        (upload.data as FormData).files.single.value.contentType?.mimeType,
        'image/jpeg',
      );

      // O aviso "Livrete guardado" desaparece (taparia o fundo do ecrã).
      tester
          .state<ScaffoldMessengerState>(find.byType(ScaffoldMessenger))
          .hideCurrentSnackBar();
      await tester.pumpAndSettle();

      // Gerir condutores → sem condutores → registar "Sou eu".
      await scrollIntoView(tester, 'manage-drivers');
      await tapVisible(tester, find.byKey(const Key('manage-drivers')));
      await tapVisible(
        tester,
        find.byKey(const Key('manage-drivers-register')),
      );
      await pumpUntilFound(tester, find.byKey(const Key('driver-isOwner')));
      await tapVisible(tester, find.byKey(const Key('driver-isOwner')));
      await tester.enterText(
        find.byKey(const Key('driver-licenceNumber')),
        'MAP1234567',
      );
      // Num emulador o teclado real fica aberto e muda a disposição a meio dos
      // toques seguintes: fecha-o, como faria quem usa a app.
      FocusManager.instance.primaryFocus?.unfocus();
      await tester.pumpAndSettle();
      await tapVisible(tester, find.byKey(const Key('licence-category-B')));
      await tapVisible(
        tester,
        find.byKey(const Key('driver-licenceIssueDate')),
      );
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      await tapVisible(tester, find.byKey(const Key('driver-save')));

      // De volta ao veículo, a folha reabre: associar como habitual.
      await pumpUntilFound(
        tester,
        find.byKey(const Key('driver-check-$driverId')),
      );
      await tapVisible(tester, find.byKey(const Key('driver-check-$driverId')));
      await tapVisible(
        tester,
        find.byKey(const Key('driver-habitual-$driverId')),
      );
      await tapVisible(tester, find.byKey(const Key('manage-drivers-save')));
      expect(api.calls('PUT', '$vehiclePath/drivers'), hasLength(1));

      // A lista mostra o veículo com o condutor habitual e sem "Livrete em falta".
      await tester.tap(find.byKey(const Key('nav-vehicles')));
      await tester.pumpAndSettle();
      await pumpUntilFound(
        tester,
        find.byKey(const Key('vehicle-card-$vehicleId')),
      );
      expect(find.text('Condutor habitual: Ana Maria Machava'), findsOneWidget);
      expect(find.text('Livrete em falta'), findsNothing);
      expect(find.text('Confirmado no registo'), findsOneWidget);
    },
  );
}
