import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/app.dart';
import 'package:moseguros/core/config/env.dart';
import 'package:moseguros/core/network/connectivity.dart';
import 'package:moseguros/features/onboarding/onboarding_controller.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../test/support/mock_api.dart';

/// Abre a app (flavor dev) pela primeira vez e confirma que a apresentação aparece.
///
/// Num emulador Android:
///   flutter test integration_test/smoke_test.dart --flavor dev
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('abre a app e mostra a apresentação', (tester) async {
    SharedPreferences.setMockInitialValues({});
    final preferences = await SharedPreferences.getInstance();
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          envProvider.overrideWithValue(
            const Env(apiBaseUrl: 'http://10.0.2.2:8080', name: 'dev'),
          ),
          sharedPreferencesProvider.overrideWithValue(preferences),
          // Sem o plugin connectivity_plus no flutter-tester (S4-09): a fila de
          // envio da vistoria escuta a rede desde o arranque.
          connectivityMonitorProvider.overrideWithValue(AlwaysOnline()),
        ],
        child: const MoSegurosApp(locale: Locale('pt')),
      ),
    );
    await tester.pumpAndSettle();

    expect(find.text('Compare seguradoras'), findsOneWidget);
    expect(find.text('Saltar'), findsOneWidget);
  });
}
