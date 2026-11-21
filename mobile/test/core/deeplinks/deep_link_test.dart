import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/auth/session_controller.dart';
import 'package:moseguros/core/deeplinks/deep_link_handler.dart';
import 'package:moseguros/core/deeplinks/deep_link_mapper.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/features/inspection/inspection_screen.dart';
import 'package:moseguros/features/policies/policy_detail_screen.dart';

import '../../support/mock_api.dart';
import '../../support/payments.dart';
import '../../support/policies.dart';
import '../../support/proposals.dart';
import '../../support/quotes.dart' show requestId;
import '../../support/vehicles.dart';

const _link = 'moseguros://proposal/$proposalId/inspection';
const _inspection = '/proposals/$proposalId/inspection';

/// Caminho actual do router.
String _location(ProviderContainer container) => container
    .read(routerProvider)
    .routerDelegate
    .currentConfiguration
    .uri
    .toString();

const _session = AsyncData<Session?>(
  Session(accessToken: 'a', refreshToken: 'r'),
);
const _anonymous = AsyncData<Session?>(null);

void main() {
  group('deepLinkLocation', () {
    String? map(String link) => deepLinkLocation(Uri.parse(link));

    test('as sete linhas da tabela (S6-07 §6)', () {
      expect(map('moseguros://policy/$policyId'), '/policies/$policyId');
      expect(map('moseguros://proposal/$proposalId'), '/proposals/$proposalId');
      expect(map(_link), _inspection);
      expect(
        map('moseguros://payment/$paymentIntentId'),
        '/payments/$paymentIntentId',
      );
      expect(map('moseguros://quote/$requestId'), '/quote/results/$requestId');
      expect(map('moseguros://notifications'), '/notifications');
      // Outro host, uuid inválido ou partes a mais.
      expect(map('moseguros://outra/coisa'), '/');
      expect(map('moseguros://claim/$policyId'), '/');
      expect(map('moseguros://policy/nao-e-um-uuid'), '/');
      expect(map('moseguros://policy/$policyId/extra'), '/');
      expect(map('moseguros://proposal/$proposalId/inspection/x'), '/');
      expect(map('moseguros://policy'), '/');
      expect(map('moseguros://notifications/x'), '/');
    });

    test('sem o esquema moseguros: null', () {
      expect(map('/proposals'), isNull);
      expect(map('https://moseguros.co.mz/policy/$policyId'), isNull);
    });

    test('DeepLinkMapper.map recebe texto', () {
      expect(
        DeepLinkMapper.map('moseguros://policy/$policyId'),
        '/policies/$policyId',
      );
      expect(DeepLinkMapper.map(null), isNull);
      expect(DeepLinkMapper.map('  '), isNull);
      expect(DeepLinkMapper.map('/policies'), isNull);
    });

    test('safeReturnPath: só caminhos internos', () {
      expect(
        safeReturnPath('/proposals/new?quoteId=1'),
        '/proposals/new?quoteId=1',
      );
      expect(safeReturnPath('https://mal.example/x'), isNull);
      expect(safeReturnPath('//mal.example/x'), isNull);
      expect(safeReturnPath('proposals'), isNull);
      expect(safeReturnPath('/login'), isNull);
    });
  });

  group('appRedirect', () {
    test('sem sessão guarda o destino e vai para /login?from=', () {
      final pending = PendingDestination();
      expect(
        appRedirect(
          onboardingDone: true,
          session: _anonymous,
          uri: Uri.parse(_inspection),
          pending: pending,
        ),
        '/login?from=%2Fproposals%2F$proposalId%2Finspection',
      );
      // Depois de entrar, volta ao destino (uma vez).
      expect(
        appRedirect(
          onboardingDone: true,
          session: _session,
          uri: Uri.parse('/login/code'),
          pending: pending,
        ),
        _inspection,
      );
      expect(pending.value, isNull);
    });

    test('perfil incompleto: /profile?complete=1&from=<destino>', () {
      final pending = PendingDestination()..value = '/proposals/new?quoteId=q1';
      expect(
        appRedirect(
          onboardingDone: true,
          session: const AsyncData(
            Session(accessToken: 'a', refreshToken: 'r', isNewUser: true),
          ),
          uri: Uri.parse('/splash'),
          pending: pending,
        ),
        '/profile?complete=1&from=%2Fproposals%2Fnew%3FquoteId%3Dq1',
      );
    });

    test('sessão a carregar guarda a ligação para depois do arranque', () {
      final pending = PendingDestination();
      expect(
        appRedirect(
          onboardingDone: true,
          session: const AsyncLoading(),
          uri: Uri.parse(_inspection),
          pending: pending,
        ),
        splashPath,
      );
      expect(
        appRedirect(
          onboardingDone: true,
          session: _session,
          uri: Uri.parse(splashPath),
          pending: pending,
        ),
        _inspection,
      );
    });
  });

  testWidgets('com sessão, a ligação abre a vistoria', (tester) async {
    final api = MockApi()
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal());
    final (container, _) = await openAt(tester, api, _link);

    expect(find.byType(InspectionScreen), findsOneWidget);
    expect(_location(container), _inspection);
  });

  testWidgets('sem sessão: login e depois volta à vistoria', (tester) async {
    final api = MockApi()
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal())
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions());
    stubCustomerSession(api);
    final container = await pumpMoSeguros(tester, api);
    container.read(routerProvider).go(_link);
    await settle(tester);

    expect(
      _location(container),
      '/login?from=%2Fproposals%2F$proposalId%2Finspection',
    );
    expect(find.byType(InspectionScreen), findsNothing);

    // O mesmo que o ecrã do código faz depois de `verifyOtp`; os pedidos
    // simulados só correm com frames, por isso não se espera aqui.
    final login = container
        .read(sessionControllerProvider.notifier)
        .login(accessToken: 'access-1', refreshToken: 'refresh-1');
    await settle(tester);
    await login;
    await settle(tester);

    expect(find.byType(InspectionScreen), findsOneWidget);
    expect(_location(container), _inspection);
  });

  testWidgets(
    'DeepLinkHandler: moseguros://policy/{id} sem sessão → login → apólice',
    (tester) async {
      final api = MockApi()
        ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
        ..on('GET', policyPath, 200, PolicyFixtures.detail())
        ..on(
          'GET',
          myPoliciesPath,
          200,
          PolicyFixtures.page([PolicyFixtures.summary()]),
        );
      stubCustomerSession(api);
      final container = await pumpMoSeguros(tester, api);
      final handler = container.read(deepLinkHandlerProvider);

      // Vazio: não faz nada.
      handler.open(null);
      handler.open('');
      await settle(tester);
      expect(_location(container), '/login');

      handler.open('moseguros://policy/$policyId');
      await settle(tester);
      expect(_location(container), '/login?from=%2Fpolicies%2F$policyId');

      final login = container
          .read(sessionControllerProvider.notifier)
          .login(accessToken: 'access-1', refreshToken: 'refresh-1');
      await settle(tester);
      await login;
      await settle(tester);

      expect(find.byType(PolicyDetailScreen), findsOneWidget);
      expect(_location(container), '/policies/$policyId');
    },
  );
}
