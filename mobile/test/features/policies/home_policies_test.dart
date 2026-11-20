import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/core/push/push_service.dart';
import 'package:moseguros/features/home/home_screen.dart';
import 'package:moseguros/features/notifications/notifications_screen.dart';
import 'package:moseguros/features/notifications/preferences_screen.dart';
import 'package:moseguros/features/onboarding/onboarding_controller.dart';
import 'package:moseguros/features/policies/policy_list_screen.dart';
import 'package:moseguros/features/policies/show_to_police_screen.dart';

import '../../support/mock_api.dart';
import '../../support/notifications.dart';
import '../../support/policies.dart';
import '../../support/proposals.dart';

AppDatabase _database() {
  final db = AppDatabase.memory();
  addTearDown(db.close);
  return db;
}

void main() {
  group('início', () {
    testWidgets('atalho "Mostrar à polícia" com uma apólice activa em cache', (
      tester,
    ) async {
      final db = _database();
      await tester.runAsync(
        () => seedPolicy(
          db,
          syncedAt: proposalToday,
          summary: PolicyFixtures.summary(),
          detail: PolicyFixtures.detail(),
        ),
      );
      await openAt(
        tester,
        MockApi(),
        '/',
        database: db,
        connectivity: FakeConnectivity(online: false),
      );

      final shortcut = find.byKey(const Key('home-police'));
      expect(shortcut, findsOneWidget);
      expect(
        find.descendant(of: shortcut, matching: find.text('AAA-100-MC')),
        findsOneWidget,
      );
      await tester.tap(shortcut);
      await settle(tester);
      expect(find.byType(ShowToPoliceScreen), findsOneWidget);

      await tester.tap(find.byKey(const Key('police-close')));
      await settle(tester);
      expect(find.byType(HomeScreen), findsOneWidget);
    });

    testWidgets('várias apólices activas: escolhe-se numa lista', (
      tester,
    ) async {
      final db = _database();
      await tester.runAsync(() async {
        await seedPolicy(
          db,
          syncedAt: proposalToday,
          detail: PolicyFixtures.detail(),
        );
        await seedPolicy(
          db,
          syncedAt: proposalToday,
          detail: PolicyFixtures.detail(id: policyId2, plate: 'BBB-200-MC'),
        );
      });
      await openAt(
        tester,
        MockApi(),
        '/',
        database: db,
        connectivity: FakeConnectivity(online: false),
      );

      await tester.tap(find.byKey(const Key('home-police')));
      await settle(tester);
      await tester.tap(find.byKey(const Key('police-choose-$policyId2')));
      await settle(tester);
      expect(find.byType(ShowToPoliceScreen), findsOneWidget);
      expect(find.byKey(const Key('police-plate')), findsOneWidget);
      expect(find.text('BBB-200-MC'), findsOneWidget);
    });

    testWidgets('sem apólice activa em cache: sem atalho', (tester) async {
      final db = _database();
      await tester.runAsync(
        () => seedPolicy(
          db,
          syncedAt: proposalToday,
          summary: PolicyFixtures.summary(
            status: 'EXPIRED',
            coverStatus: 'ENDED',
          ),
        ),
      );
      await openAt(
        tester,
        MockApi(),
        '/',
        database: db,
        connectivity: FakeConnectivity(online: false),
      );
      expect(find.byKey(const Key('home-police')), findsNothing);

      // A entrada para a lista existe sempre.
      await tapInList(tester, find.byKey(const Key('home-policies')));
      await settle(tester);
      expect(find.byType(PolicyListScreen), findsOneWidget);
    });

    testWidgets('sino com a contagem de não lidas abre as notificações', (
      tester,
    ) async {
      final api = MockApi()
        ..on('GET', unreadCountPath, 200, {'count': 3})
        ..on('GET', myPoliciesPath, 200, PolicyFixtures.page([]))
        ..on(
          'GET',
          myNotificationsPath,
          200,
          NotificationFixtures.page([NotificationFixtures.notification()]),
        );
      await openAt(tester, api, '/');

      final bell = find.byKey(const Key('notification-bell'));
      expect(bell, findsOneWidget);
      expect(
        find.descendant(of: bell, matching: find.text('3')),
        findsOneWidget,
      );
      expect(find.byTooltip('Notificações, 3 por ler'), findsOneWidget);

      await tester.tap(bell);
      await settle(tester);
      expect(find.byType(NotificationsScreen), findsOneWidget);
    });

    testWidgets('push: o pedido de permissão aparece uma vez', (tester) async {
      final push = FakePushService();
      final api = MockApi()
        ..on('POST', devicesPath, 201, NotificationFixtures.device());
      final (container, _) = await openAt(
        tester,
        api,
        '/',
        overrides: [pushServiceProvider.overrideWithValue(push)],
      );
      expect(find.text('Receber avisos no telemóvel'), findsOneWidget);
      await tester.tap(find.byKey(const Key('push-prompt-enable')));
      await settle(tester);
      expect(push.permissionRequests, 1);
      expect(find.byKey(const Key('push-prompt')), findsNothing);
      expect(
        container.read(sharedPreferencesProvider).getBool(pushPromptDoneKey),
        isTrue,
      );
    });

    testWidgets('sem push (NoopPushService): sem pedido de permissão', (
      tester,
    ) async {
      await openAt(tester, MockApi(), '/');
      expect(find.byKey(const Key('push-prompt')), findsNothing);
    });
  });

  testWidgets('perfil: "Notificações" abre as preferências', (tester) async {
    final api = MockApi()
      ..on('GET', preferencesPath, 200, NotificationFixtures.preferences());
    await openAt(tester, api, '/profile');
    await tapInList(
      tester,
      find.byKey(const Key('profile-notifications')),
      scrollable: find.byType(Scrollable).first,
    );
    await settle(tester);
    expect(find.byType(PreferencesScreen), findsOneWidget);
  });
}
