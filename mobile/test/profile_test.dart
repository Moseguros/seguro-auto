import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/theme.dart';
import 'package:moseguros/features/profile/profile_form.dart';
import 'package:moseguros/features/profile/profile_rules.dart';
import 'package:moseguros/l10n/app_localizations.dart';
import 'package:moseguros_api/moseguros_api.dart';

import 'support/mock_api.dart';

final _today = DateTime(2026, 11, 2);

void main() {
  group('regras do perfil', () {
    test('nome completo', () {
      expect(nameError(''), 'REQUIRED');
      expect(nameError('   '), 'REQUIRED');
      expect(nameError('Ana'), 'INVALID_NAME');
      expect(nameError('Ana 3'), 'INVALID_NAME');
      expect(nameError("Ana ' -"), 'INVALID_NAME');
      expect(nameError('Ana Maria'), isNull);
      expect(nameError("  Ana   D'Almeida-Machava "), isNull);
      expect(nameError('Zélia Tembe'), isNull);
    });

    test('data de nascimento: menor de 18 bloqueado', () {
      expect(birthDateError(null, _today), 'REQUIRED');
      expect(birthDateError(DateTime(2008, 11, 2), _today), isNull);
      expect(birthDateError(DateTime(2008, 11, 3), _today), 'UNDERAGE');
      expect(birthDateError(DateTime(2027, 1, 1), _today), 'UNDERAGE');
      expect(birthDateError(DateTime(1906, 11, 2), _today), isNull);
      expect(birthDateError(DateTime(1906, 11, 1), _today), isNull);
      expect(birthDateError(DateTime(1905, 11, 1), _today), 'INVALID_DATE');
      expect(latestBirthDate(_today), DateTime(2008, 11, 2));
    });

    test('hoje em Africa/Maputo (UTC+2)', () {
      expect(
        todayInMaputo(DateTime.utc(2026, 11, 2, 22, 30)),
        DateTime(2026, 11, 3),
      );
      expect(
        todayInMaputo(DateTime.utc(2026, 11, 2, 21, 59)),
        DateTime(2026, 11, 2),
      );
    });

    test('documento, NUIT, tipo, morada e província', () {
      expect(documentNumberError('', required: true), 'REQUIRED');
      expect(documentNumberError('', required: false), isNull);
      expect(documentNumberError('ab 123', required: true), isNull);
      expect(
        documentNumberError('AB1', required: true),
        'INVALID_DOCUMENT_NUMBER',
      );
      expect(
        documentNumberError('AB-123', required: true),
        'INVALID_DOCUMENT_NUMBER',
      );
      expect(nuitError('', required: true), 'REQUIRED');
      expect(nuitError('', required: false), isNull);
      expect(nuitError('123 456 789', required: true), isNull);
      expect(nuitError('12345678', required: true), 'INVALID_NUIT');
      expect(idDocTypeError(null), 'REQUIRED');
      expect(idDocTypeError('PASSPORT'), isNull);
      expect(addressError('x' * 300), isNull);
      expect(addressError('x' * 301), 'SIZE');
      expect(provinceError(null, ['GAZA']), 'REQUIRED');
      expect(provinceError('GAZA', ['GAZA']), isNull);
    });
  });

  group('ProfileForm', () {
    late List<UpdateMyProfileRequest> saved;

    Future<void> pumpForm(
      WidgetTester tester,
      Map<String, Object?> profile,
    ) async {
      useSmallPhone(tester);
      saved = [];
      final response = standardSerializers.deserializeWith(
        CustomerProfileResponse.serializer,
        profile,
      )!;
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          locale: const Locale('pt'),
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,
          home: Scaffold(
            body: SingleChildScrollView(
              child: ProfileForm(
                profile: response,
                today: _today,
                onSave: (request) async {
                  saved.add(request);
                  return response;
                },
                onSaved: (_) {},
                onConflict: () {},
              ),
            ),
          ),
        ),
      );
    }

    Future<void> save(WidgetTester tester) async {
      await tester.ensureVisible(find.byKey(const Key('profile-save')));
      await tester.tap(find.byKey(const Key('profile-save')));
      await tester.pumpAndSettle();
    }

    Future<void> choose(WidgetTester tester, String key, String option) async {
      await tester.ensureVisible(find.byKey(Key(key)));
      await tester.tap(find.byKey(Key(key)));
      await tester.pumpAndSettle();
      await tester.tap(find.text(option).last);
      await tester.pumpAndSettle();
    }

    Future<void> pickBirthDate(WidgetTester tester) async {
      await tester.tap(find.byKey(const Key('profile-birthDate')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
    }

    testWidgets('perfil vazio: cada campo obrigatório', (tester) async {
      await pumpForm(tester, Fixtures.profile());
      await save(tester);

      // Nome, data, tipo, número, NUIT e província.
      expect(find.text('Campo obrigatório.'), findsNWidgets(6));
      expect(saved, isEmpty);
    });

    testWidgets('cada regra de formato tem a sua mensagem', (tester) async {
      await pumpForm(tester, Fixtures.profile());
      await tester.enterText(find.byKey(const Key('profile-fullName')), 'Ana');
      await tester.enterText(
        find.byKey(const Key('profile-idDocNumber')),
        'A-1',
      );
      await tester.enterText(find.byKey(const Key('profile-nuit')), '1234');
      await tester.enterText(
        find.byKey(const Key('profile-address')),
        'x' * 301,
      );
      await save(tester);

      expect(
        find.text(
          "Indique o nome completo (pelo menos duas palavras, só letras, espaços, ' e -).",
        ),
        findsOneWidget,
      );
      expect(
        find.text(
          'Número de documento inválido (5 a 20 letras maiúsculas ou dígitos).',
        ),
        findsOneWidget,
      );
      expect(find.text('O NUIT tem 9 dígitos.'), findsOneWidget);
      expect(
        find.text('Comprimento fora dos limites permitidos.'),
        findsOneWidget,
      );
      expect(saved, isEmpty);
    });

    testWidgets('menor de 18 bloqueado: o calendário acaba em hoje − 18 anos', (
      tester,
    ) async {
      await pumpForm(tester, Fixtures.profile());
      await tester.tap(find.byKey(const Key('profile-birthDate')));
      await tester.pumpAndSettle();

      final picker = tester.widget<DatePickerDialog>(
        find.byType(DatePickerDialog),
      );
      expect(picker.lastDate, DateTime(2008, 11, 2));
      expect(picker.firstDate, DateTime(1906, 11, 2));
      expect(picker.initialDate, DateTime(2008, 11, 2));
    });

    testWidgets('data guardada de um menor é recusada ao gravar', (
      tester,
    ) async {
      await pumpForm(
        tester,
        Fixtures.profile(
          fullName: 'Ana Machava',
          birthDate: '2010-01-01',
          idDocType: 'BI',
          idDocNumberMasked: '*******123A',
          nuitMasked: '******789',
          province: 'GAZA',
        ),
      );
      await save(tester);
      expect(find.text('Tem de ter pelo menos 18 anos.'), findsOneWidget);
      expect(saved, isEmpty);
    });

    testWidgets('perfil válido grava com documento e NUIT normalizados', (
      tester,
    ) async {
      await pumpForm(tester, Fixtures.profile(version: 3));
      await tester.enterText(
        find.byKey(const Key('profile-fullName')),
        '  Ana   Maria Machava ',
      );
      await pickBirthDate(tester);
      await choose(tester, 'profile-idDocType', 'Passaporte');
      await tester.enterText(
        find.byKey(const Key('profile-idDocNumber')),
        'ab 12345',
      );
      await tester.enterText(
        find.byKey(const Key('profile-nuit')),
        '123 456 789',
      );
      await choose(tester, 'profile-province', 'Maputo Cidade');
      await save(tester);

      final request = saved.single;
      expect(request.fullName, 'Ana Maria Machava');
      expect(request.birthDate, Date(2008, 11, 2));
      expect(request.idDocType, UpdateMyProfileRequestIdDocTypeEnum.PASSPORT);
      expect(request.idDocNumber, 'AB12345');
      expect(request.nuit, '123456789');
      expect(request.address, isNull);
      expect(request.province, UpdateMyProfileRequestProvinceEnum.MAPUTO_CITY);
      expect(request.version, 3);
    });

    testWidgets('documento e NUIT mascarados com "Alterar"', (tester) async {
      await pumpForm(
        tester,
        Fixtures.profile(
          complete: true,
          fullName: 'Ana Machava',
          birthDate: '1990-05-10',
          idDocType: 'BI',
          idDocNumberMasked: '*******123A',
          nuitMasked: '******789',
          province: 'GAZA',
        ),
      );
      expect(find.text('*******123A'), findsOneWidget);
      expect(find.text('******789'), findsOneWidget);
      expect(find.byKey(const Key('profile-idDocNumber')), findsNothing);

      await save(tester);
      expect(saved.single.idDocNumber, isNull);
      expect(saved.single.nuit, isNull);

      // "Alterar" abre o campo vazio.
      await tester.ensureVisible(find.text('Alterar').last);
      await tester.tap(find.text('Alterar').last);
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('profile-nuit')), findsOneWidget);

      // Mudar o tipo de documento obriga a indicar o número novo.
      await choose(tester, 'profile-idDocType', 'DIRE');
      expect(find.byKey(const Key('profile-idDocNumber')), findsOneWidget);
      await save(tester);
      expect(find.text('Campo obrigatório.'), findsOneWidget);
      expect(saved, hasLength(1));
    });

    testWidgets('erros de campo da API aparecem nos campos', (tester) async {
      useSmallPhone(tester);
      final response = standardSerializers.deserializeWith(
        CustomerProfileResponse.serializer,
        Fixtures.profile(
          fullName: 'Ana Machava',
          birthDate: '1990-05-10',
          idDocType: 'BI',
          idDocNumberMasked: '*******123A',
          nuitMasked: '******789',
          province: 'GAZA',
        ),
      )!;
      await tester.pumpWidget(
        MaterialApp(
          locale: const Locale('pt'),
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,
          home: Scaffold(
            body: SingleChildScrollView(
              child: ProfileForm(
                profile: response,
                today: _today,
                onSave: (_) async {
                  throw dioError(
                    400,
                    MockApi.problem(
                      400,
                      'VALIDATION_ERROR',
                      errors: [
                        {'field': 'fullName', 'code': 'INVALID_NAME'},
                      ],
                    ),
                  );
                },
                onSaved: (_) {},
                onConflict: () {},
              ),
            ),
          ),
        ),
      );
      await save(tester);
      expect(
        find.text(
          "Indique o nome completo (pelo menos duas palavras, só letras, espaços, ' e -).",
        ),
        findsOneWidget,
      );
    });
  });

  group('ecrã de perfil', () {
    late MockApi api;

    setUp(() {
      api = MockApi()
        ..on('POST', '/api/v1/auth/refresh', 200, Fixtures.tokens())
        ..on('GET', '/api/v1/me/contacts', 200, Fixtures.contacts())
        ..on('GET', '/api/v1/meta/version', 200, {
          'name': 'moseguros-backend',
          'version': '1.2.3',
          'commit': 'abc1234',
        });
    });

    testWidgets(
      'perfil incompleto: à entrada vai completar e depois para o início',
      (tester) async {
        api
          ..on(
            'GET',
            '/api/v1/me',
            200,
            Fixtures.me(displayName: null, profileComplete: false),
          )
          ..on('GET', '/api/v1/me/profile', 200, Fixtures.profile());
        await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');

        expect(
          find.text('Precisamos destes dados para emitir as suas apólices.'),
          findsOneWidget,
        );
        await tester.scrollUntilVisible(
          find.text('1.2.3'),
          300,
          scrollable: find.byType(Scrollable).first,
        );
        expect(find.text('Sobre'), findsOneWidget);
        await tester.scrollUntilVisible(
          find.byKey(const Key('profile-fullName')),
          -300,
          scrollable: find.byType(Scrollable).first,
        );

        await tester.enterText(
          find.byKey(const Key('profile-fullName')),
          'Ana Machava',
        );
        await tester.tap(find.byKey(const Key('profile-birthDate')));
        await tester.pumpAndSettle();
        await tester.tap(find.text('OK'));
        await tester.pumpAndSettle();
        for (final (key, option) in [
          ('profile-idDocType', 'Bilhete de Identidade'),
          ('profile-province', 'Gaza'),
        ]) {
          await tester.ensureVisible(find.byKey(Key(key)));
          await tester.tap(find.byKey(Key(key)));
          await tester.pumpAndSettle();
          await tester.tap(find.text(option).last);
          await tester.pumpAndSettle();
        }
        await tester.enterText(
          find.byKey(const Key('profile-idDocNumber')),
          '110100123456A',
        );
        await tester.enterText(
          find.byKey(const Key('profile-nuit')),
          '123456789',
        );

        api
          ..on(
            'PUT',
            '/api/v1/me/profile',
            200,
            Fixtures.profile(
              complete: true,
              version: 1,
              fullName: 'Ana Machava',
              birthDate: '2008-11-02',
              idDocType: 'BI',
              idDocNumberMasked: '*********456A',
              nuitMasked: '******789',
              province: 'GAZA',
            ),
          )
          ..on(
            'GET',
            '/api/v1/me',
            200,
            Fixtures.me(displayName: 'Ana Machava'),
          );
        await tester.ensureVisible(find.byKey(const Key('profile-save')));
        await tester.tap(find.byKey(const Key('profile-save')));
        await tester.pumpAndSettle();

        expect(api.calls('PUT', '/api/v1/me/profile'), hasLength(1));
        expect(find.text('Olá, Ana'), findsOneWidget);
        expect(find.byKey(const Key('incomplete-profile')), findsNothing);
        expect(find.text('Perfil guardado'), findsOneWidget);
      },
    );

    testWidgets('409 mostra a mensagem e recarrega o perfil', (tester) async {
      api
        ..on('GET', '/api/v1/me', 200, Fixtures.me())
        ..on(
          'GET',
          '/api/v1/me/profile',
          200,
          Fixtures.profile(
            complete: true,
            fullName: 'Ana Machava',
            birthDate: '1990-05-10',
            idDocType: 'BI',
            idDocNumberMasked: '*******123A',
            nuitMasked: '******789',
            province: 'GAZA',
          ),
        )
        ..on(
          'PUT',
          '/api/v1/me/profile',
          409,
          MockApi.problem(409, 'CONCURRENT_MODIFICATION'),
        );
      await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');
      await tester.tap(find.byKey(const Key('nav-profile')));
      await tester.pumpAndSettle();

      await tester.ensureVisible(find.byKey(const Key('profile-save')));
      // Com a barra inferior, o botão só fica visível depois de deslocar a lista.
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(const Key('profile-save')));
      await tester.pumpAndSettle();

      expect(api.calls('GET', '/api/v1/me/profile'), hasLength(2));
      expect(
        find.text(
          'Outra pessoa alterou estes dados entretanto. Actualize e tente de novo.',
        ),
        findsOneWidget,
      );
      expect(find.byKey(const Key('profile-save')), findsOneWidget);
    });

    testWidgets('"Terminar sessão" pede confirmação e volta ao login', (
      tester,
    ) async {
      api
        ..on('GET', '/api/v1/me', 200, Fixtures.me())
        ..on('GET', '/api/v1/me/profile', 200, Fixtures.profile(complete: true))
        ..on('POST', '/api/v1/auth/logout', 204)
        ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions());
      await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');
      await tester.tap(find.byKey(const Key('nav-profile')));
      await tester.pumpAndSettle();

      await tester.scrollUntilVisible(
        find.byKey(const Key('logout')),
        300,
        scrollable: find.byType(Scrollable).first,
      );
      await tester.tap(find.byKey(const Key('logout')));
      await tester.pumpAndSettle();
      expect(find.text('Terminar sessão?'), findsOneWidget);

      await tester.tap(find.byKey(const Key('confirm-logout')));
      await tester.pumpAndSettle();

      expect(find.text('Entrar na área do cliente'), findsOneWidget);
      expect(api.calls('POST', '/api/v1/auth/logout'), hasLength(1));
    });
  });
}
