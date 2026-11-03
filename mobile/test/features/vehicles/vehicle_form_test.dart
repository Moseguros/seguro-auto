import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/router.dart';

import '../../support/mock_api.dart';
import '../../support/vehicles.dart';

const _lookupPath = '/api/v1/me/vehicles/registry-lookup';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi();
    stubCustomerSession(api);
    api
      ..on('GET', '/api/v1/me/vehicles', 200, VehicleFixtures.page([]))
      ..on('GET', '/api/v1/me/drivers', 200, VehicleFixtures.page([]));
  });

  Future<void> openForm(WidgetTester tester) async {
    final container = await pumpMoSeguros(
      tester,
      api,
      refreshToken: 'refresh-0',
      overrides: FakeDevice().overrides,
    );
    container.read(routerProvider).go('/vehicles/new');
    await tester.pumpAndSettle();
  }

  Finder field(String key) => find.byKey(Key('vehicle-$key'));

  /// Texto de erro dentro do campo [key].
  Finder errorIn(String key, String text) =>
      find.descendant(of: field(key), matching: find.text(text));

  String textOf(WidgetTester tester, String key) => tester
      .widget<EditableText>(
        find.descendant(of: field(key), matching: find.byType(EditableText)),
      )
      .controller
      .text;

  Future<void> choose(WidgetTester tester, String key, String option) async {
    await tester.ensureVisible(field(key));
    await tester.pumpAndSettle();
    await tester.tap(field(key));
    await tester.pumpAndSettle();
    await tester.tap(find.text(option).last);
    await tester.pumpAndSettle();
  }

  Future<void> save(WidgetTester tester) async {
    await tester.ensureVisible(field('save'));
    await tester.pumpAndSettle();
    await tester.tap(field('save'));
    await tester.pumpAndSettle();
  }

  Future<void> lookup(WidgetTester tester) async {
    await tester.tap(find.byKey(const Key('registry-lookup')));
    await tester.pumpAndSettle();
  }

  Future<void> fillCommon(WidgetTester tester) async {
    await tester.enterText(field('plate'), 'aaa-100-mc');
    await tester.enterText(field('chassis'), 'jtdbr32e720012345');
    await tester.enterText(field('make'), 'Toyota');
    await tester.enterText(field('model'), 'Corolla');
    await tester.enterText(field('year'), '2018');
    await tester.pumpAndSettle();
  }

  testWidgets('ajuda da matrícula com os exemplos dos formatos activos', (
    tester,
  ) async {
    await openForm(tester);
    expect(find.text('Ex.: AAA-100-MC ou MLA-12-34'), findsOneWidget);
    final plate = tester.widget<EditableText>(
      find.descendant(of: field('plate'), matching: find.byType(EditableText)),
    );
    expect(plate.textCapitalization, TextCapitalization.characters);
  });

  testWidgets('TRAILER esconde a cilindrada, fixa 0 lugares e pede o peso', (
    tester,
  ) async {
    await openForm(tester);
    expect(field('engineCc'), findsOneWidget);

    await choose(tester, 'category', 'Reboque');
    expect(field('engineCc'), findsNothing);
    expect(textOf(tester, 'seats'), '0');
    expect(
      tester
          .widget<TextField>(
            find.descendant(
              of: field('seats'),
              matching: find.byType(TextField),
            ),
          )
          .enabled,
      isFalse,
    );

    await save(tester);
    expect(errorIn('grossWeight', 'Campo obrigatório.'), findsOneWidget);
    expect(errorIn('seats', 'Campo obrigatório.'), findsNothing);

    // Ao sair de TRAILER, os lugares voltam a ser escritos pelo cliente.
    await choose(tester, 'category', 'Motociclo');
    expect(field('engineCc'), findsOneWidget);
    expect(textOf(tester, 'seats'), '');
  });

  testWidgets('HEAVY sem peso dá erro; ligeiro sem peso não', (tester) async {
    await openForm(tester);
    await choose(tester, 'category', 'Pesado');
    await save(tester);
    expect(errorIn('grossWeight', 'Campo obrigatório.'), findsOneWidget);

    await choose(tester, 'category', 'Ligeiro misto');
    await save(tester);
    expect(errorIn('grossWeight', 'Campo obrigatório.'), findsNothing);
  });

  testWidgets('regras dos campos no ecrã (chassis, ano, lugares, valor)', (
    tester,
  ) async {
    await openForm(tester);
    await tester.enterText(field('chassis'), 'ABC');
    await tester.enterText(field('year'), '1899');
    await choose(tester, 'category', 'Motociclo');
    await tester.enterText(field('seats'), '4');
    await tester.enterText(field('commercialValue'), '1,234');
    await save(tester);

    expect(errorIn('plate', 'Campo obrigatório.'), findsOneWidget);
    expect(errorIn('chassis', 'N.º de chassis inválido.'), findsOneWidget);
    expect(errorIn('make', 'Campo obrigatório.'), findsOneWidget);
    expect(errorIn('year', 'Ano inválido.'), findsOneWidget);
    expect(
      errorIn('seats', 'Valor fora dos limites permitidos.'),
      findsOneWidget,
    );
    expect(
      errorIn(
        'commercialValue',
        'Valor inválido. Use no máximo 2 casas decimais.',
      ),
      findsOneWidget,
    );
    expect(errorIn('usage', 'Campo obrigatório.'), findsOneWidget);
    expect(errorIn('province', 'Campo obrigatório.'), findsOneWidget);
    expect(api.calls('POST', '/api/v1/me/vehicles'), isEmpty);
  });

  testWidgets('"Procurar no registo" com 200 preenche os campos e avisa', (
    tester,
  ) async {
    api.on('GET', _lookupPath, 200, VehicleFixtures.registry());
    await openForm(tester);
    await tester.enterText(field('plate'), 'AAA-100-MC');
    await tester.enterText(field('chassis'), 'OUTRO12345');
    await lookup(tester);

    expect(
      api.calls('GET', _lookupPath).single.queryParameters['plate'],
      'AAA-100-MC',
    );
    expect(
      find.text(
        'Preenchemos os dados a partir do registo automóvel. '
        'Confirme-os com o seu livrete.',
      ),
      findsOneWidget,
    );
    expect(textOf(tester, 'chassis'), 'JTDBR32E720012345');
    expect(textOf(tester, 'make'), 'Toyota');
    expect(textOf(tester, 'model'), 'Corolla');
    expect(textOf(tester, 'year'), '2018');
    expect(textOf(tester, 'engineCc'), '1598');
    expect(textOf(tester, 'seats'), '5');
    expect(
      find.descendant(
        of: field('category'),
        matching: find.text('Ligeiro de passageiros'),
      ),
      findsOneWidget,
    );
  });

  for (final (status, code, message) in [
    (
      404,
      'VEHICLE_REGISTRY_NOT_FOUND',
      'Não encontrámos esta matrícula no registo. Preencha os dados do livrete.',
    ),
    (
      503,
      'VEHICLE_REGISTRY_UNAVAILABLE',
      'O registo automóvel não está disponível. Preencha os dados do livrete.',
    ),
  ]) {
    testWidgets('"Procurar no registo" com $status avisa e não apaga nada', (
      tester,
    ) async {
      api.on('GET', _lookupPath, status, MockApi.problem(status, code));
      await openForm(tester);
      await fillCommon(tester);
      await lookup(tester);

      expect(find.text(message), findsOneWidget);
      expect(textOf(tester, 'chassis'), 'jtdbr32e720012345');
      expect(textOf(tester, 'make'), 'Toyota');
      expect(textOf(tester, 'year'), '2018');
      // Continua editável.
      await tester.enterText(field('model'), 'Hilux');
      expect(textOf(tester, 'model'), 'Hilux');
    });
  }

  testWidgets('"Procurar no registo" com 400 mostra o erro na matrícula', (
    tester,
  ) async {
    api.on(
      'GET',
      _lookupPath,
      400,
      MockApi.problem(
        400,
        'VALIDATION_ERROR',
        errors: [
          {'field': 'plate', 'code': 'INVALID_PLATE'},
        ],
      ),
    );
    await openForm(tester);
    await tester.enterText(field('plate'), 'XYZ');
    await lookup(tester);

    expect(errorIn('plate', 'Matrícula inválida.'), findsOneWidget);
  });

  testWidgets('criar envia o pedido e abre o detalhe com o aviso', (
    tester,
  ) async {
    api
      ..on('POST', '/api/v1/me/vehicles', 201, VehicleFixtures.vehicle())
      ..on(
        'GET',
        '/api/v1/me/vehicles/$vehicleId',
        200,
        VehicleFixtures.vehicle(),
      );
    await openForm(tester);
    await fillCommon(tester);
    await choose(tester, 'category', 'Ligeiro de passageiros');
    await tester.enterText(field('engineCc'), '1598');
    await tester.enterText(field('seats'), '5');
    await choose(tester, 'usage', 'Particular');
    await choose(tester, 'province', 'Maputo Cidade');
    await tester.enterText(field('commercialValue'), '850 000,50');
    await tester.ensureVisible(field('hasTracker'));
    await tester.pumpAndSettle();
    await tester.tap(field('hasTracker'));
    await save(tester);

    expect(api.calls('POST', '/api/v1/me/vehicles').single.data, {
      'plate': 'aaa-100-mc',
      'chassisNumber': 'JTDBR32E720012345',
      'make': 'Toyota',
      'model': 'Corolla',
      'year': 2018,
      'engineCc': 1598,
      'category': 'LIGHT_PASSENGER',
      'seats': 5,
      'usage': 'PRIVATE',
      'provinceOfCirculation': 'MAPUTO_CITY',
      'commercialValue': {'amount': 85000050, 'currency': 'MZN'},
      'hasTracker': true,
    });
    expect(
      find.text('Veículo acrescentado. Envie agora a foto do livrete.'),
      findsOneWidget,
    );
    expect(find.byKey(const Key('vehicle-edit')), findsOneWidget);
    await tester.scrollUntilVisible(
      find.text('Ainda não enviou o livrete.'),
      300,
      scrollable: find.byType(Scrollable).last,
    );
    expect(find.text('Ainda não enviou o livrete.'), findsOneWidget);
  });

  testWidgets('VEHICLE_ALREADY_REGISTERED aparece no campo da matrícula', (
    tester,
  ) async {
    api.on(
      'POST',
      '/api/v1/me/vehicles',
      409,
      MockApi.problem(409, 'VEHICLE_ALREADY_REGISTERED'),
    );
    await openForm(tester);
    await fillCommon(tester);
    await choose(tester, 'category', 'Reboque');
    await tester.enterText(field('grossWeight'), '3500');
    await choose(tester, 'usage', 'Particular');
    await choose(tester, 'province', 'Gaza');
    await save(tester);

    final body = api.calls('POST', '/api/v1/me/vehicles').single.data as Map;
    expect(body.containsKey('engineCc'), isFalse);
    expect(body['seats'], 0);
    expect(body['grossWeightKg'], 3500);
    await tester.ensureVisible(field('plate'));
    await tester.pumpAndSettle();
    expect(
      errorIn('plate', 'Já tem um veículo com esta matrícula.'),
      findsOneWidget,
    );
  });
}
