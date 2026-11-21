import 'dart:convert';

import 'package:drift/drift.dart' show Value;
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/features/policies/screen_keeper.dart';

/// Guarda uma apólice na cache local (como depois de uma sincronização).
Future<void> seedPolicy(
  AppDatabase db, {
  required DateTime syncedAt,
  Map<String, Object?>? summary,
  Map<String, Object?>? detail,
}) async {
  final source = detail ?? summary!;
  await db
      .into(db.cachedPolicies)
      .insertOnConflictUpdate(
        CachedPoliciesCompanion.insert(
          id: source['id']! as String,
          json: Value(detail == null ? null : jsonEncode(detail)),
          summaryJson: Value(summary == null ? null : jsonEncode(summary)),
          status: source['status']! as String,
          endAt: DateTime.parse(source['endAt']! as String),
          syncedAt: syncedAt,
        ),
      );
}

/// Ids e caminhos das respostas simuladas das apólices (S6-07).
const policyId = '01960000-0000-7000-8000-000000000801';
const policyId2 = '01960000-0000-7000-8000-000000000802';
const certificateFileId = '01960000-0000-7000-8000-000000000901';
const scheduleFileId = '01960000-0000-7000-8000-000000000902';
const policyNumber = 'AP-2027-000123';
const certificateNumber = 'CT-2027-000045';
const verificationUrl = 'https://moseguros.test/v/abc123TOKEN';
const myPoliciesPath = '/api/v1/me/policies';
const policyPath = '/api/v1/me/policies/$policyId';

Map<String, Object?> _money(int amount) => {
  'amount': amount,
  'currency': 'MZN',
};

abstract final class PolicyFixtures {
  static Map<String, Object?> summary({
    String id = policyId,
    String status = 'ACTIVE',
    String coverStatus = 'COVERED',
    String plate = 'AAA-100-MC',
    String number = policyNumber,
    String startAt = '2027-01-15T00:00:00Z',
    String endAt = '2028-01-14T21:59:59Z',
    bool certificateReady = true,
  }) => {
    'id': id,
    'number': number,
    'status': status,
    'coverStatus': coverStatus,
    'coverageType': 'MTPL_ONLY',
    'productName': 'RC Auto',
    'insurer': {
      'id': '01940000-0000-7000-8000-000000000501',
      'name': 'Seguradora Demo',
    },
    'vehicle': {
      'id': '01930000-0000-7000-8000-000000000101',
      'plate': plate,
      'make': 'Toyota',
      'model': 'Corolla',
      'year': 2018,
    },
    'startAt': startAt,
    'endAt': endAt,
    'certificateReady': certificateReady,
  };

  static Map<String, Object?> page(List<Map<String, Object?>> content) => {
    'content': content,
    'page': 0,
    'size': 50,
    'totalElements': content.length,
  };

  static Map<String, Object?> document(
    String type, {
    String status = 'READY',
    String? fileId,
  }) => {
    'type': type,
    'status': status,
    if (status == 'READY') 'fileId': fileId,
    if (status == 'READY') 'fileName': '${type.toLowerCase()}.pdf',
  };

  static Map<String, Object?> detail({
    String id = policyId,
    String status = 'ACTIVE',
    String coverStatus = 'COVERED',
    String plate = 'AAA-100-MC',
    String certificateStatus = 'READY',
    bool withCertificate = true,
  }) => {
    'id': id,
    'number': policyNumber,
    'status': status,
    'coverStatus': coverStatus,
    'coverageType': 'MTPL_ONLY',
    'paymentPlan': 'FULL',
    'renewalType': 'ANNUAL_RENEWABLE',
    'policyholderName': 'Ana Maria Machava',
    'issuedAt': '2027-01-10T09:05:00Z',
    'startAt': '2027-01-15T00:00:00Z',
    'endAt': '2028-01-14T21:59:59Z',
    'totalPremium': _money(299900),
    'insurer': {
      'id': '01940000-0000-7000-8000-000000000501',
      'name': 'Seguradora Demo',
    },
    'product': {
      'name': 'RC Auto',
      'type': 'MTPL_ONLY',
      'versionId': '01940000-0000-7000-8000-000000000601',
    },
    'vehicle': {
      'id': '01930000-0000-7000-8000-000000000101',
      'plate': plate,
      'make': 'Toyota',
      'model': 'Corolla',
      'year': 2018,
    },
    'coverages': [
      {
        'code': 'MTPL',
        'mandatory': true,
        'capital': _money(300000000),
        'premium': _money(299900),
        'deductibleType': 'NONE',
      },
    ],
    'instalments': [
      {
        'seq': 1,
        'dueDate': '2027-01-15',
        'amount': _money(299900),
        'status': 'PAID',
        'paidAt': '2027-01-10T09:00:00Z',
      },
    ],
    if (withCertificate)
      'certificate': {
        'id': '01960000-0000-7000-8000-000000000851',
        'number': certificateNumber,
        'status': 'VALID',
        'validFrom': '2027-01-15T00:00:00Z',
        'validTo': '2028-01-14T21:59:59Z',
        'verificationUrl': verificationUrl,
        if (certificateStatus == 'READY') 'fileId': certificateFileId,
      },
    'documents': [
      document(
        'CERTIFICATE',
        status: certificateStatus,
        fileId: certificateFileId,
      ),
      document('SCHEDULE', fileId: scheduleFileId),
    ],
  };
}

/// Falso do brilho e do ecrã ligado: regista as chamadas.
class RecordingScreenKeeper implements ScreenKeeper {
  final List<String> calls = [];

  @override
  Future<void> keepBright() async => calls.add('keepBright');

  @override
  Future<void> restore() async => calls.add('restore');
}
