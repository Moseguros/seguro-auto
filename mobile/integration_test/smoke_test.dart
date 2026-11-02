import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/app.dart';
import 'package:moseguros/core/config/env.dart';

/// Abre a app (flavor dev) e confirma que o ecrã inicial aparece.
///
/// Num emulador Android:
///   flutter test integration_test/smoke_test.dart --flavor dev
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('abre a app e mostra o ecrã inicial', (tester) async {
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          envProvider.overrideWithValue(
            const Env(apiBaseUrl: 'http://10.0.2.2:8080', name: 'dev'),
          ),
        ],
        child: const MoSegurosApp(locale: Locale('pt')),
      ),
    );
    await tester.pump();

    expect(
      find.text('Compare, compre e gira o seu seguro automóvel num só lugar.'),
      findsOneWidget,
    );
    expect(find.text('Estado do sistema'), findsOneWidget);

    // Com ou sem backend, o cartão sai do estado "a carregar" (timeout de 15 s).
    await tester.pumpAndSettle(
      const Duration(milliseconds: 100),
      EnginePhase.sendSemanticsUpdate,
      const Duration(seconds: 30),
    );
    expect(
      find.byKey(const Key('api-version')).evaluate().isNotEmpty ||
          find.byKey(const Key('system-status-error')).evaluate().isNotEmpty,
      isTrue,
    );
  });
}
