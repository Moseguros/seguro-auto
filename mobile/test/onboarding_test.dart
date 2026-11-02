import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/features/onboarding/onboarding_controller.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'support/mock_api.dart';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi()
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions());
  });

  Future<bool?> savedFlag() async =>
      (await SharedPreferences.getInstance()).getBool(OnboardingController.key);

  testWidgets('primeira abertura mostra a apresentação; "Saltar" grava a flag', (
    tester,
  ) async {
    await pumpMoSeguros(tester, api, onboardingDone: false);

    expect(find.byIcon(Icons.compare_arrows), findsOneWidget);
    expect(find.text('Compare seguradoras'), findsOneWidget);
    expect(
      find.text(
        'Veja as propostas de várias seguradoras lado a lado e escolha a que lhe serve.',
      ),
      findsOneWidget,
    );
    expect(await savedFlag(), isNull);

    await tester.tap(find.text('Saltar'));
    await tester.pumpAndSettle();

    expect(await savedFlag(), isTrue);
    expect(find.text('Entrar na área do cliente'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('"Seguinte" percorre os 3 ecrãs e "Começar" grava a flag', (
    tester,
  ) async {
    await pumpMoSeguros(tester, api, onboardingDone: false);

    await tester.tap(find.text('Seguinte'));
    await tester.pumpAndSettle();
    expect(find.byIcon(Icons.bolt), findsOneWidget);
    expect(find.text('Compre em minutos'), findsOneWidget);

    await tester.tap(find.text('Seguinte'));
    await tester.pumpAndSettle();
    expect(find.byIcon(Icons.shield), findsOneWidget);
    expect(find.text('Tudo num só lugar'), findsOneWidget);
    expect(
      find.text('Apólices, pagamentos e sinistros sempre consigo.'),
      findsOneWidget,
    );
    expect(find.text('Saltar'), findsNothing);
    expect(await savedFlag(), isNull);

    await tester.tap(find.text('Começar'));
    await tester.pumpAndSettle();

    expect(await savedFlag(), isTrue);
    expect(find.text('Entrar na área do cliente'), findsOneWidget);
  });

  testWidgets('em inglês', (tester) async {
    await pumpMoSeguros(
      tester,
      api,
      onboardingDone: false,
      locale: const Locale('en'),
    );
    expect(find.text('Compare insurers'), findsOneWidget);
    expect(find.text('Skip'), findsOneWidget);
    expect(find.text('Next'), findsOneWidget);
  });

  testWidgets('com a flag gravada vai directo para o login', (tester) async {
    await pumpMoSeguros(tester, api);
    expect(find.text('Saltar'), findsNothing);
    expect(find.text('Entrar na área do cliente'), findsOneWidget);
  });
}
