import 'dart:io';
import 'dart:typed_data';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/api/api_client.dart';
import 'package:moseguros/core/auth/session_controller.dart';
import 'package:moseguros/core/auth/token_storage.dart';
import 'package:moseguros/core/clock.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/core/push/device_registration.dart';
import 'package:moseguros/features/policies/document_cache.dart';
import 'package:moseguros/features/policies/policy_repository.dart';
import 'package:path/path.dart' as p;

import '../../support/mock_api.dart';
import '../../support/notifications.dart';
import '../../support/policies.dart';

final _now = DateTime.utc(2027, 1, 10, 9);

void main() {
  late MockApi api;
  late AppDatabase db;
  late Directory files;
  late ProviderContainer container;

  setUp(() {
    api = MockApi();
    db = AppDatabase.memory();
    files = Directory.systemTemp.createTempSync('ms-policies-');
    container = ProviderContainer(
      overrides: [
        apiClientProvider.overrideWithValue(api.client),
        appDatabaseProvider.overrideWithValue(db),
        appFilesDirectoryProvider.overrideWith((ref) async => files),
        clockProvider.overrideWithValue(() => _now),
        tokenStorageProvider.overrideWithValue(MemoryTokenStorage()),
        deviceIdStorageProvider.overrideWithValue(
          MemoryDeviceIdStorage(deviceId),
        ),
      ],
    );
  });

  tearDown(() async {
    container.dispose();
    await db.close();
    try {
      files.deleteSync(recursive: true);
    } on FileSystemException {
      // Já apagada.
    }
  });

  PolicyRepository repository() => container.read(policyRepositoryProvider);

  test('devolve primeiro a cache e depois a rede', () async {
    // Na cache: a apólice com a matrícula antiga.
    api.on('GET', policyPath, 200, PolicyFixtures.detail(plate: 'OLD-111-MC'));
    await repository().syncDetail(policyId);

    api.on(
      'GET',
      myPoliciesPath,
      200,
      PolicyFixtures.page([
        PolicyFixtures.summary(plate: 'NEW-222-MC'),
        PolicyFixtures.summary(id: policyId2, plate: 'BBB-200-MC'),
      ]),
    );
    final emissions = await repository().list().toList();

    expect(emissions, hasLength(2));
    expect(emissions.first.single.vehicle.plate, 'OLD-111-MC');
    expect(emissions.last.map((e) => e.id), containsAll([policyId, policyId2]));
    final stored = await repository().cachedOne(policyId2);
    expect(stored?.summary?.vehicle.plate, 'BBB-200-MC');
    expect(stored?.syncedAt, _now);
  });

  test('apólices que deixaram de vir saem da cache', () async {
    api.on(
      'GET',
      myPoliciesPath,
      200,
      PolicyFixtures.page([
        PolicyFixtures.summary(),
        PolicyFixtures.summary(id: policyId2),
      ]),
    );
    await repository().syncList();
    api.on(
      'GET',
      myPoliciesPath,
      200,
      PolicyFixtures.page([PolicyFixtures.summary()]),
    );
    final entries = await repository().syncList();
    expect(entries.map((e) => e.id), [policyId]);
  });

  test('POLICY_NOT_FOUND tira a apólice da cache', () async {
    api.on('GET', policyPath, 200, PolicyFixtures.detail());
    await repository().syncDetail(policyId);
    api.on('GET', policyPath, 404, MockApi.problem(404, 'POLICY_NOT_FOUND'));
    await expectLater(repository().syncDetail(policyId), throwsA(anything));
    expect(await repository().cachedOne(policyId), isNull);
  });

  test('o certificado de uma apólice activa fica guardado', () async {
    api
      ..on('GET', policyPath, 200, PolicyFixtures.detail())
      ..onBytes(
        '/api/v1/files/$certificateFileId',
        Uint8List.fromList('%PDF-1.4'.codeUnits),
        'application/pdf',
      );
    final entry = await repository().syncDetail(policyId);
    await container
        .read(documentCacheProvider)
        .prefetchCertificate(entry.detail!);

    final file = await container
        .read(documentCacheProvider)
        .cached(certificateFileId);
    expect(file, isNotNull);
    expect(
      file!.path,
      p.join(files.path, 'certificates', '$certificateFileId.pdf'),
    );
  });

  test(
    'terminar sessão anula o dispositivo com o token válido e apaga a cache',
    () async {
      api
        ..on('GET', '/api/v1/me', 200, Fixtures.me())
        ..on('POST', '/api/v1/auth/logout', 204)
        ..on('DELETE', '$devicesPath/$deviceId', 204)
        ..on('GET', policyPath, 200, PolicyFixtures.detail())
        ..onBytes(
          '/api/v1/files/$certificateFileId',
          Uint8List.fromList('%PDF-1.4'.codeUnits),
          'application/pdf',
        );
      final session = container.read(sessionControllerProvider.notifier);
      await container.read(sessionControllerProvider.future);
      await session.login(accessToken: 'access-1', refreshToken: 'refresh-1');

      final entry = await repository().syncDetail(policyId);
      await container
          .read(documentCacheProvider)
          .prefetchCertificate(entry.detail!);
      final certificates = Directory(p.join(files.path, 'certificates'));
      expect(certificates.existsSync(), isTrue);

      await session.logout();

      final unregister = api.calls('DELETE', '$devicesPath/$deviceId').single;
      expect(unregister.headers['Authorization'], 'Bearer access-1');
      expect(await container.read(deviceIdStorageProvider).read(), isNull);
      expect(await db.select(db.cachedPolicies).get(), isEmpty);
      expect(await db.select(db.cachedDocuments).get(), isEmpty);
      expect(certificates.existsSync(), isFalse);
    },
  );
}
