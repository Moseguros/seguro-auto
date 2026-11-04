import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/camera/camera_service.dart';

import '../../support/mock_api.dart';
import '../../support/proposals.dart';
import '../../support/vehicles.dart';

const _kycPath = '/api/v1/me/kyc';

Future<void> _capture(WidgetTester tester, String key) async {
  await tester.ensureVisible(find.byKey(Key(key)));
  await tester.pumpAndSettle();
  await tester.tap(
    find.descendant(
      of: find.byKey(Key(key)),
      matching: find.byType(TextButton),
    ),
  );
  await settle(tester);
  await tester.tap(find.byKey(const Key('camera-shutter')));
  await settle(tester);
  await tester.tap(find.byKey(const Key('photo-use')));
  await settle(tester);
}

void main() {
  testWidgets('passaporte: sem verso; selfie com a câmara frontal', (
    tester,
  ) async {
    final device = FakeDevice();
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(
          idDocType: 'PASSPORT',
          checklistDeclarations: 'DONE',
          checklistSuitability: 'DONE',
          checklistInspection: 'DONE',
        ),
      )
      ..on('POST', _kycPath, 201, ProposalFixtures.kycCheck());
    await openAt(
      tester,
      api,
      '/proposals/$proposalId?step=identity',
      overrides: device.overrides,
    );

    expect(find.text('Passo 5 de 6 · Identidade'), findsOneWidget);
    expect(
      find.textContaining('Verifique a sua identidade para a proposta poder'),
      findsOneWidget,
    );
    expect(find.byKey(const Key('kyc-doc-front')), findsOneWidget);
    expect(find.byKey(const Key('kyc-doc-back')), findsNothing);

    await _capture(tester, 'kyc-doc-front');
    expect(device.cameras.last.lens, CameraLens.back);
    await _capture(tester, 'kyc-selfie');
    expect(device.cameras.last.lens, CameraLens.front);
    expect(
      device.compressor.calls.map((c) => (c.quality, c.keepExif)),
      everyElement((80, false)),
    );

    await tester.ensureVisible(find.byKey(const Key('kyc-submit')));
    await tester.tap(find.byKey(const Key('kyc-submit')));
    await settle(tester);

    final request = api.calls('POST', _kycPath).single;
    final fields = {
      for (final field in (request.data as dynamic).fields as List)
        (field as MapEntry<String, String>).key: field.value,
    };
    expect(fields['insurerId'], proposalInsurerId);
    final files = [
      for (final file in (request.data as dynamic).files as List)
        (file as MapEntry).key,
    ];
    expect(files, unorderedEquals(['docFront', 'selfie']));
    // Enviado: passa ao resumo.
    expect(find.text('Passo 6 de 6 · Resumo'), findsOneWidget);
  });

  testWidgets('sem rede: errorNoConnection e as imagens ficam', (tester) async {
    final device = FakeDevice();
    final api = MockApi()..on('GET', _kycPath, 200, ProposalFixtures.myKyc());
    await openAt(
      tester,
      api,
      '/identity',
      connectivity: FakeConnectivity(online: false),
      overrides: device.overrides,
      proposals: [ProposalFixtures.summary()],
    );

    expect(find.text('Verificação de identidade'), findsOneWidget);
    // Perfil sem tipo de documento: pede o verso.
    expect(find.byKey(const Key('kyc-doc-back')), findsOneWidget);
    await _capture(tester, 'kyc-doc-front');
    await _capture(tester, 'kyc-doc-back');
    await _capture(tester, 'kyc-selfie');

    await tester.ensureVisible(find.byKey(const Key('kyc-submit')));
    await tester.tap(find.byKey(const Key('kyc-submit')));
    await settle(tester);

    expect(find.text('Sem ligação à internet.'), findsOneWidget);
    expect(api.calls('POST', _kycPath), isEmpty);
    // As fotografias continuam no ecrã ("Repetir" em vez de "Tirar fotografia").
    expect(find.text('Repetir'), findsNWidgets(3));
  });

  testWidgets('sem proposta: só o estado e a explicação', (tester) async {
    final api = MockApi()
      ..on('GET', _kycPath, 200, ProposalFixtures.myKyc(state: 'VERIFIED'));
    await openAt(tester, api, '/identity');

    expect(find.text('Identidade verificada'), findsOneWidget);
    expect(
      find.text(
        'A verificação de identidade é pedida quando faz uma proposta.',
      ),
      findsOneWidget,
    );
    expect(find.byKey(const Key('kyc-submit')), findsNothing);
  });
}
