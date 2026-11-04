import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/api/api_serializers.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/proposals/steps/declarations_step.dart';
import 'package:moseguros/features/proposals/steps/policyholder_step.dart';
import 'package:moseguros/features/proposals/steps/suitability_step.dart';
import 'package:moseguros/features/proposals/wizard_steps.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../support/mock_api.dart';
import '../../support/proposals.dart';

Finder _list(Type step) => find
    .descendant(of: find.byType(step), matching: find.byType(Scrollable))
    .first;

Future<void> _continue(WidgetTester tester) async {
  await tester.tap(find.byKey(const Key('proposal-continue')));
  await tester.pumpAndSettle();
}

/// Desserializa um `ProposalResponse` como o cliente gerado.
ProposalResponse _parse(Map<String, Object?> json) =>
    apiSerializers.deserializeWith(ProposalResponse.serializer, json)!;

void main() {
  group('wizardSteps', () {
    test('a vistoria só aparece com inspection.required', () {
      expect(
        visibleSteps(_parse(ProposalFixtures.proposal())),
        contains(ProposalStep.inspection),
      );
      expect(
        visibleSteps(
          _parse(ProposalFixtures.proposal(inspectionRequired: false)),
        ),
        isNot(contains(ProposalStep.inspection)),
      );
    });

    test('abre no primeiro passo por concluir', () {
      expect(
        firstPendingStep(_parse(ProposalFixtures.proposal())),
        ProposalStep.declarations,
      );
      expect(
        firstPendingStep(
          _parse(
            ProposalFixtures.proposal(
              checklistDeclarations: 'DONE',
              checklistSuitability: 'ACK_REQUIRED',
            ),
          ),
        ),
        ProposalStep.suitability,
      );
      final allDone = _parse(
        ProposalFixtures.proposal(
          checklistDeclarations: 'DONE',
          checklistSuitability: 'DONE',
          checklistInspection: 'DONE',
          kycState: 'VERIFIED',
        ),
      );
      expect(firstPendingStep(allDone), ProposalStep.review);
      expect(
        firstPendingStep(
          _parse(
            ProposalFixtures.proposal(
              checklistDeclarations: 'DONE',
              checklistSuitability: 'DONE',
              checklistInspection: 'DONE',
            ),
          ),
          identitySkipped: true,
        ),
        ProposalStep.review,
      );
      expect(initialStep(allDone, 'inspection'), ProposalStep.inspection);
      expect(
        initialStep(
          _parse(ProposalFixtures.proposal(inspectionRequired: false)),
          'inspection',
        ),
        ProposalStep.declarations,
      );
    });
  });

  testWidgets('passos: "Passo n de 6" com vistoria', (tester) async {
    final api = MockApi()
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal());
    await openAt(tester, api, '/proposals/$proposalId');
    expect(find.text('Passo 2 de 6 · Declarações'), findsOneWidget);
  });

  testWidgets('passos: "de 5" sem vistoria e nenhum laranja', (tester) async {
    final rcOnly = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(inspectionRequired: false),
      );
    await openAt(tester, rcOnly, '/proposals/$proposalId?step=policyholder');
    expect(find.text('Passo 1 de 5 · Tomador e data'), findsOneWidget);
    // Nenhum laranja nos passos.
    expect(find.byType(CtaButton), findsNothing);
  });

  testWidgets('declarações: "Sim" exige os seguimentos e "Não" limpa-os', (
    tester,
  ) async {
    final api = MockApi()
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal())
      ..on(
        'POST',
        '$proposalPath/declarations',
        200,
        ProposalFixtures.proposal(
          version: 2,
          checklistDeclarations: 'DONE',
          declarations: ProposalFixtures.declarationsAllNo(),
        ),
      );
    await openAt(tester, api, '/proposals/$proposalId?step=declarations');
    final list = _list(DeclarationsStep);

    await tapInList(
      tester,
      find.byKey(const Key('declaration-CLAIMS_LAST_3_YEARS-yes')),
      scrollable: list,
    );
    expect(
      find.byKey(const Key('declaration-CLAIMS_LAST_3_YEARS-count')),
      findsOneWidget,
    );
    expect(
      find.byKey(const Key('declaration-CLAIMS_LAST_3_YEARS-details')),
      findsOneWidget,
    );

    // Sem os seguimentos nem as outras respostas não grava.
    await _continue(tester);
    expect(api.calls('POST', '$proposalPath/declarations'), isEmpty);
    expect(find.text('Indique um número de 1 a 20.'), findsOneWidget);
    expect(find.text('Escreva entre 10 e 500 caracteres.'), findsOneWidget);

    // "Não" limpa os seguimentos.
    await tester.enterText(
      find.byKey(const Key('declaration-CLAIMS_LAST_3_YEARS-details')),
      'Toque ligeiro em 2025, sem feridos.',
    );
    await tapInList(
      tester,
      find.byKey(const Key('declaration-CLAIMS_LAST_3_YEARS-no')),
      scrollable: list,
    );
    expect(
      find.byKey(const Key('declaration-CLAIMS_LAST_3_YEARS-details')),
      findsNothing,
    );

    for (final code in declarationCodes.skip(1)) {
      await tapInList(
        tester,
        find.byKey(Key('declaration-$code-no')),
        scrollable: list,
      );
    }
    // Sem a caixa de veracidade não grava.
    await _continue(tester);
    expect(api.calls('POST', '$proposalPath/declarations'), isEmpty);
    expect(find.text('Confirme a declaração para continuar.'), findsOneWidget);

    await tapInList(
      tester,
      find.byKey(const Key('declaration-truthfulness')),
      scrollable: list,
    );
    await _continue(tester);

    final body =
        api.calls('POST', '$proposalPath/declarations').single.data
            as Map<String, dynamic>;
    expect(body['version'], 1);
    expect(body['truthfulnessConfirmed'], isTrue);
    expect(
      (body['answers'] as List).map((a) => (a as Map)['answer']),
      everyElement(isFalse),
    );
    expect(
      (body['answers'] as List).first,
      {'questionCode': 'CLAIMS_LAST_3_YEARS', 'answer': false},
    );
    expect(find.text('Passo 3 de 6 · Adequação'), findsOneWidget);
  });

  testWidgets('adequação: um aviso por motivo e a confirmação', (tester) async {
    final warned = ProposalFixtures.proposal(
      version: 3,
      checklistDeclarations: 'DONE',
      checklistSuitability: 'ACK_REQUIRED',
      suitability: ProposalFixtures.suitabilityResult(
        result: 'WARNED',
        reasons: ['NEED_NOT_COVERED_THEFT_FIRE', 'DEDUCTIBLE_NOT_WANTED'],
        answers: {
          'SUIT_NEED': 'FULL',
          'SUIT_FINANCED': 'NO',
          'SUIT_CAN_AFFORD_REPAIR': 'YES',
          'SUIT_CROSS_BORDER': 'NO',
          'SUIT_DEDUCTIBLE': 'NO',
        },
      ),
    );
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(version: 2, checklistDeclarations: 'DONE'),
      )
      ..on('POST', '$proposalPath/suitability', 200, warned)
      ..on(
        'POST',
        '$proposalPath/suitability/acknowledge',
        200,
        ProposalFixtures.proposal(
          version: 4,
          checklistDeclarations: 'DONE',
          checklistSuitability: 'DONE',
          suitability: ProposalFixtures.suitabilityResult(
            result: 'WARNED',
            reasons: ['NEED_NOT_COVERED_THEFT_FIRE', 'DEDUCTIBLE_NOT_WANTED'],
            acknowledgedAt: '2027-01-10T08:21:00Z',
          ),
        ),
      );
    await openAt(tester, api, '/proposals/$proposalId?step=suitability');
    final list = _list(SuitabilityStep);
    for (final entry in {
      'SUIT_NEED': 'FULL',
      'SUIT_FINANCED': 'NO',
      'SUIT_CAN_AFFORD_REPAIR': 'YES',
      'SUIT_CROSS_BORDER': 'NO',
      'SUIT_DEDUCTIBLE': 'NO',
    }.entries) {
      await tapInList(
        tester,
        find.byKey(Key('suitability-${entry.key}-${entry.value}')),
        scrollable: list,
      );
    }
    await _continue(tester);

    expect(
      api.calls('POST', '$proposalPath/suitability').single.data,
      containsPair('version', 2),
    );
    // Fica no passo, com os dois avisos pela ordem da resposta.
    expect(find.text('Passo 3 de 6 · Adequação'), findsOneWidget);
    final warnings = find.byKey(
      const Key('suitability-warning-NEED_NOT_COVERED_THEFT_FIRE'),
    );
    await tester.scrollUntilVisible(warnings, 200, scrollable: list);
    await tester.scrollUntilVisible(
      find.byKey(const Key('suitability-warning-DEDUCTIBLE_NOT_WANTED')),
      100,
      scrollable: list,
    );
    // Pela ordem da resposta.
    expect(
      tester
          .getTopLeft(
            find.byKey(const Key('suitability-warning-DEDUCTIBLE_NOT_WANTED')),
          )
          .dy,
      greaterThan(tester.getTopLeft(warnings).dy),
    );
    expect(
      find.text(
        'Disse que não quer franquia, mas pelo menos uma cobertura de danos '
        'próprios desta proposta tem franquia.',
      ),
      findsOneWidget,
    );

    await tapInList(
      tester,
      find.byKey(const Key('suitability-acknowledge')),
      scrollable: list,
    );
    expect(
      api.calls('POST', '$proposalPath/suitability/acknowledge').single.data,
      {'version': 3},
    );
    expect(find.text('Passo 4 de 6 · Vistoria'), findsOneWidget);
  });

  testWidgets('409 CONCURRENT_MODIFICATION avisa e recarrega', (tester) async {
    final api = MockApi()
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal())
      ..on(
        'PUT',
        proposalPath,
        409,
        MockApi.problem(409, 'CONCURRENT_MODIFICATION'),
      );
    final (container, _) = await openAt(
      tester,
      api,
      '/proposals/$proposalId?step=policyholder',
    );
    await tapInList(
      tester,
      find.byKey(const Key('proposal-refresh-profile')),
      scrollable: _list(PolicyholderStep),
    );

    expect(find.text('A proposta foi alterada noutro lado.'), findsOneWidget);
    expect(api.calls('GET', proposalPath), hasLength(2));
    expect(container, isNotNull);
  });
}
