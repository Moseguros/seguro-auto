import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/core/push/push_service.dart';
import 'package:moseguros/features/home/home_screen.dart'
    show pushPromptDoneKey;
import 'package:moseguros/features/notifications/notifications_screen.dart';
import 'package:moseguros/features/notifications/preferences_screen.dart';
import 'package:moseguros/features/policies/policy_detail_screen.dart';
import 'package:moseguros/l10n/app_localizations_pt.dart';

import '../../support/mock_api.dart';
import '../../support/notifications.dart';
import '../../support/policies.dart';
import '../../support/proposals.dart';

const _readId = '01970000-0000-7000-8000-000000000b02';

String _path(GoRouter router) =>
    router.routerDelegate.currentConfiguration.uri.toString();

void main() {
  group('centro de notificações', () {
    testWidgets('tocar marca como lida e abre a rota do deepLink', (
      tester,
    ) async {
      final api = MockApi()
        ..on(
          'GET',
          myNotificationsPath,
          200,
          NotificationFixtures.page([
            NotificationFixtures.notification(
              deepLink: 'moseguros://policy/$policyId',
            ),
            NotificationFixtures.notification(
              id: _readId,
              category: 'NEW_CATEGORY',
              title: 'Aviso antigo',
              body: 'Texto',
              readAt: '2027-01-01T10:00:00Z',
              createdAt: '2026-12-01T10:00:00Z',
            ),
          ]),
        )
        ..on('GET', unreadCountPath, 200, {'count': 1})
        ..on('POST', readPath(notificationId), 204)
        ..on('GET', policyPath, 200, PolicyFixtures.detail())
        ..on(
          'GET',
          myPoliciesPath,
          200,
          PolicyFixtures.page([PolicyFixtures.summary()]),
        );
      final (_, router) = await openAt(tester, api, '/notifications');

      expect(find.byType(NotificationsScreen), findsOneWidget);
      expect(find.text('Apólice emitida'), findsOneWidget);
      // 10/01/2027 09:00 − 08:55 = 5 min; mais de 7 dias → data.
      expect(find.text('há 5 min'), findsOneWidget);
      expect(find.text('01/12/2026'), findsOneWidget);
      expect(find.byKey(const Key('notification-unread-dot')), findsOneWidget);
      // Categoria desconhecida: ícone genérico.
      expect(
        find.descendant(
          of: find.byKey(const Key('notification-$_readId')),
          matching: find.byIcon(Icons.notifications_outlined),
        ),
        findsOneWidget,
      );
      expect(find.byKey(const Key('notifications-mark-all')), findsOneWidget);

      await tester.tap(find.text('Apólice emitida'));
      await settle(tester);

      expect(api.calls('POST', readPath(notificationId)), hasLength(1));
      expect(find.byType(PolicyDetailScreen), findsOneWidget);
      expect(_path(router), '/policies/$policyId');
    });

    testWidgets('sem rede: "Sem ligação."', (tester) async {
      final api = MockApi();
      await openAt(
        tester,
        api,
        '/notifications',
        connectivity: FakeConnectivity(online: false),
      );
      expect(find.text('Sem ligação.'), findsOneWidget);
    });

    testWidgets('vazio: "Não tem notificações."', (tester) async {
      final api = MockApi()
        ..on('GET', myNotificationsPath, 200, NotificationFixtures.page([]))
        ..on('GET', unreadCountPath, 200, {'count': 0});
      await openAt(tester, api, '/notifications');
      expect(find.text('Não tem notificações.'), findsWidgets);
      expect(find.byKey(const Key('notifications-mark-all')), findsNothing);
    });
  });

  group('preferências', () {
    testWidgets('categoria obrigatória sem canal externo bloqueia "Guardar"', (
      tester,
    ) async {
      final api = MockApi()
        ..on('GET', preferencesPath, 200, NotificationFixtures.preferences())
        ..on('PUT', preferencesPath, 200, NotificationFixtures.preferences());
      await openAt(tester, api, '/notifications/preferences');
      expect(find.byType(PreferencesScreen), findsOneWidget);
      expect(find.text('Obrigatório'), findsOneWidget);
      expect(
        find.text(
          'Estes avisos são obrigatórios: escolha pelo menos SMS, WhatsApp '
          'ou e-mail.',
        ),
        findsOneWidget,
      );

      FilledButton save() => tester.widget<FilledButton>(
        find.byKey(const Key('preferences-save')),
      );
      // Sem alterações, "Guardar" está desactivado.
      expect(save().onPressed, isNull);

      // Desligar o SMS de POLICY deixa a categoria sem canal externo.
      await tester.tap(find.byKey(const Key('preferences-POLICY-SMS')));
      await settle(tester);
      expect(
        find.text(AppLocalizationsPt().validationMandatoryCategoryNeedsChannel),
        findsOneWidget,
      );
      expect(save().onPressed, isNull);

      // O push não conta; o WhatsApp sim.
      await tester.tap(find.byKey(const Key('preferences-POLICY-WHATSAPP')));
      await settle(tester);
      expect(save().onPressed, isNotNull);

      await tester.tap(find.byKey(const Key('preferences-save')));
      await settle(tester);
      final sent = api.calls('PUT', preferencesPath).single.data as Map;
      final categories = sent['categories'] as List;
      expect(categories, hasLength(1));
      expect((categories.single as Map)['category'], 'POLICY');
      expect(find.text('Preferências guardadas'), findsOneWidget);
    });

    testWidgets('PUSH indisponível: ajuda e "Activar" só com push', (
      tester,
    ) async {
      final push = FakePushService();
      final api = MockApi()
        ..on('GET', preferencesPath, 200, NotificationFixtures.preferences())
        ..on('POST', devicesPath, 201, NotificationFixtures.device());
      await openAt(
        tester,
        api,
        '/notifications/preferences',
        overrides: [pushServiceProvider.overrideWithValue(push)],
        preferencesValues: {pushPromptDoneKey: true},
      );
      expect(
        find.text('Active as notificações para receber avisos no telemóvel.'),
        findsWidgets,
      );
      final enable = find.byKey(const Key('preferences-enable-push-POLICY'));
      expect(enable, findsOneWidget);
      // O arranque com sessão já registou o dispositivo uma vez.
      final registrations = api.calls('POST', devicesPath).length;
      await tapInList(tester, enable);
      await settle(tester);
      expect(push.permissionRequests, 1);
      expect(api.calls('POST', devicesPath).length, registrations + 1);
      expect(api.calls('GET', preferencesPath), hasLength(2));
    });

    testWidgets('sem push (NoopPushService): sem botão "Activar"', (
      tester,
    ) async {
      final api = MockApi()
        ..on('GET', preferencesPath, 200, NotificationFixtures.preferences());
      await openAt(tester, api, '/notifications/preferences');
      expect(
        find.byKey(const Key('preferences-enable-push-POLICY')),
        findsNothing,
      );
    });
  });
}
