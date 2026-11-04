import 'dart:async';
import 'dart:io';

import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/misc.dart' show Override;
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/core/clock.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/core/location/location_service.dart';
import 'package:moseguros/core/network/connectivity.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/features/inspection/inspection_screen.dart'
    show locationRationaleKey;
import 'package:moseguros/features/quote/quote_labels.dart';

import 'mock_api.dart';
import 'vehicles.dart';

/// "Hoje" dos testes das propostas (o início é 15/01/2027).
final proposalToday = DateTime.utc(2027, 1, 10, 9);

/// Abre a app com sessão (cliente de teste), o relógio fixo, e vai para [path].
Future<(ProviderContainer, GoRouter)> openAt(
  WidgetTester tester,
  MockApi api,
  String path, {
  List<Override> overrides = const [],
  AppDatabase? database,
  ConnectivityMonitor? connectivity,
  LocationService? location,
  Directory? filesDirectory,
  bool locationAsked = true,
  List<Map<String, Object?>> proposals = const [],
}) async {
  stubCustomerSession(api);
  // A lista (`/proposals` fica por baixo de `/proposals/:id`) e o início.
  api.on('GET', myProposalsPath, 200, ProposalFixtures.page(proposals));
  final container = await pumpMoSeguros(
    tester,
    api,
    refreshToken: 'refresh-0',
    database: database,
    connectivity: connectivity,
    location: location,
    filesDirectory: filesDirectory,
    preferencesValues: {if (locationAsked) locationRationaleKey: true},
    overrides: [
      clockProvider.overrideWithValue(() => proposalToday),
      quoteTodayProvider.overrideWithValue(() => proposalToday),
      ...overrides,
    ],
  );
  final router = container.read(routerProvider)..go(path);
  await settle(tester);
  return (container, router);
}

/// Deixa correr I/O real (ficheiros da fila) até [done], com frames pelo meio.
Future<void> pumpUntil(
  WidgetTester tester,
  bool Function() done, {
  int tries = 50,
}) async {
  for (var i = 0; i < tries && !done(); i++) {
    await tester.runAsync(
      () => Future<void>.delayed(const Duration(milliseconds: 100)),
    );
    await tester.pump(const Duration(milliseconds: 100));
  }
  await settle(tester);
}

/// `pumpAndSettle` que também deixa correr os pedidos simulados (temporizadores
/// de duração zero criados no último frame).
Future<void> settle(WidgetTester tester) async {
  await tester.pumpAndSettle();
  await tester.pump(const Duration(milliseconds: 50));
  await tester.pumpAndSettle();
}

/// Toca num elemento de uma lista com scroll, fazendo scroll até ele.
Future<void> tapInList(
  WidgetTester tester,
  Finder target, {
  Finder? scrollable,
}) async {
  await tester.scrollUntilVisible(
    target,
    200,
    scrollable: scrollable ?? find.byType(Scrollable).last,
  );
  await tester.pumpAndSettle();
  await tester.tap(target);
  await tester.pumpAndSettle();
}

/// Ids e números usados nas respostas simuladas das propostas.
const proposalId = '01940000-0000-7000-8000-000000000301';
const proposalNumber = 'PR-2026-000123';
const proposalQuoteId = '01940000-0000-7000-8000-000000000401';
const proposalInsurerId = '01940000-0000-7000-8000-000000000501';
const proposalPath = '/api/v1/proposals/$proposalId';
const myProposalsPath = '/api/v1/me/proposals';

const fixedSlots = ['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'];

Map<String, Object?> _money(int amount) => {
  'amount': amount,
  'currency': 'MZN',
};

/// Respostas da API das propostas (S4-02 a S4-05).
abstract final class ProposalFixtures {
  /// `ProposalResponse`. Por defeito: rascunho de danos próprios (com vistoria),
  /// declarações e adequação por fazer.
  static Map<String, Object?> proposal({
    String id = proposalId,
    String status = 'DRAFT',
    int version = 1,
    bool inspectionRequired = true,
    String inspectionStatus = 'PENDING',
    String? inspectionMode,
    List<Map<String, Object?>> photos = const [],
    List<String>? missingSlots,
    Map<String, Object?>? review,
    String checklistDeclarations = 'MISSING',
    String checklistSuitability = 'MISSING',
    String? checklistInspection,
    String checklistSignature = 'MISSING',
    String kycState = 'NONE',
    bool kycRequired = true,
    String idDocType = 'BI',
    Map<String, Object?>? declarations,
    Map<String, Object?>? suitability,
    Map<String, Object?>? signature,
    Map<String, Object?>? decision,
    String? submittedAt,
  }) => {
    'id': id,
    'number': proposalNumber,
    'quoteId': proposalQuoteId,
    'status': status,
    'channel': 'APP',
    'version': version,
    'createdAt': '2027-01-10T08:00:00Z',
    'updatedAt': '2027-01-10T08:00:00Z',
    'submittedAt': ?submittedAt,
    'expiresAt': '2027-02-09T08:00:00Z',
    'startAt': '2027-01-14T22:00:00Z',
    'endAt': '2028-01-14T21:59:59Z',
    'endDate': '2028-01-14',
    'durationDays': 365,
    'insurer': {'id': proposalInsurerId, 'displayName': 'Seguradora Demo'},
    'product': {
      'id': '01940000-0000-7000-8000-000000000601',
      'versionId': '01940000-0000-7000-8000-000000000602',
      'name': 'Auto Completo',
      'type': inspectionRequired ? 'COMPREHENSIVE' : 'MTPL_ONLY',
      'documents': [
        {'type': 'GENERAL_CONDITIONS', 'fileId': 'file-gc'},
        {'type': 'PRE_CONTRACT_INFO', 'fileId': 'file-pci'},
      ],
    },
    'vehicle': {
      'id': '01930000-0000-7000-8000-000000000101',
      'plate': 'AAA-100-MC',
      'make': 'Toyota',
      'model': 'Corolla',
      'year': 2020,
      'category': 'LIGHT_PASSENGER',
      'usage': 'PRIVATE',
    },
    'driver': {
      'id': '01930000-0000-7000-8000-000000000201',
      'fullName': 'Ana Maria Machava',
      'licenceNumberMasked': '****1234',
      'licenceIssueDate': '2015-03-01',
    },
    'policyholder': {
      'fullName': 'Ana Maria Machava',
      'birthDate': '1990-05-10',
      'idDocType': idDocType,
      'idDocNumberMasked': '********123A',
      'nuitMasked': '*****6789',
      'province': 'MAPUTO_CITY',
    },
    'coverages': [
      {
        'code': 'MTPL',
        'mandatory': true,
        'deductible': {'type': 'NONE'},
        'premium': _money(250000),
      },
      if (inspectionRequired)
        {
          'code': 'OD_COLLISION',
          'mandatory': false,
          'deductible': {'type': 'FIXED', 'amount': _money(500000)},
          'premium': _money(1500000),
        },
    ],
    'premium': {
      'commercial': _money(1750000),
      'charges': <Object?>[],
      'roundingAdjustment': _money(0),
      'total': _money(1750000),
    },
    'checklist': {
      'declarations': checklistDeclarations,
      'suitability': checklistSuitability,
      'inspection':
          checklistInspection ??
          (inspectionRequired ? 'MISSING' : 'NOT_REQUIRED'),
      'signature': checklistSignature,
    },
    'inspection': inspectionRequired
        ? {
            'required': true,
            'id': '01940000-0000-7000-8000-000000000701',
            'status': inspectionStatus,
            'mode': ?inspectionMode,
            'maxExtraPhotos': 6,
            'version': 0,
            'photos': photos,
            'missingSlots':
                missingSlots ??
                [
                  for (final slot in fixedSlots)
                    if (!photos.any((p) => p['slot'] == slot)) slot,
                ],
            'review': ?review,
          }
        : {'required': false},
    'kyc': {'state': kycState, 'requiredForAutomaticAcceptance': kycRequired},
    'declarations': ?declarations,
    'suitability': ?suitability,
    'signature': ?signature,
    'decision': ?decision,
  };

  static Map<String, Object?> photo(String slot, {String? id}) => {
    'id': id ?? 'photo-$slot',
    'slot': slot,
    'fileId': 'file-$slot',
    'source': 'CAMERA',
    'uploadedAt': '2027-01-10T09:00:00Z',
  };

  static Map<String, Object?> declarationsAllNo() => {
    'confirmedAt': '2027-01-10T08:10:00Z',
    'answers': [
      for (final code in [
        'CLAIMS_LAST_3_YEARS',
        'PREVIOUS_REFUSAL',
        'USAGE_DIFFERENT',
        'VEHICLE_MODIFIED',
        'NOVICE_DRIVERS',
      ])
        {'questionCode': code, 'answer': false},
    ],
  };

  static Map<String, Object?> suitabilityResult({
    String result = 'SUITABLE',
    List<String> reasons = const [],
    String? acknowledgedAt,
    Map<String, String> answers = const {
      'SUIT_NEED': 'OWN_DAMAGE',
      'SUIT_FINANCED': 'NO',
      'SUIT_CAN_AFFORD_REPAIR': 'YES',
      'SUIT_CROSS_BORDER': 'NO',
      'SUIT_DEDUCTIBLE': 'YES',
    },
  }) => {
    'result': result,
    'reasons': reasons,
    'answeredAt': '2027-01-10T08:20:00Z',
    'acknowledgedAt': ?acknowledgedAt,
    'answers': [
      for (final entry in answers.entries)
        {'questionCode': entry.key, 'answer': entry.value},
    ],
  };

  static Map<String, Object?> summary({
    String id = proposalId,
    String status = 'DRAFT',
  }) => {
    'id': id,
    'number': proposalNumber,
    'status': status,
    'insurer': {'id': proposalInsurerId, 'displayName': 'Seguradora Demo'},
    'productName': 'Auto Completo',
    'productType': 'COMPREHENSIVE',
    'vehiclePlate': 'AAA-100-MC',
    'total': _money(1750000),
    'startAt': '2027-01-14T22:00:00Z',
    'createdAt': '2027-01-10T08:00:00Z',
    'expiresAt': '2027-02-09T08:00:00Z',
  };

  static Map<String, Object?> page(List<Map<String, Object?>> content) => {
    'content': content,
    'page': 0,
    'size': 50,
    'totalElements': content.length,
  };

  static Map<String, Object?> signatureRequest({bool fallbackUsed = false}) => {
    'challengeId': 'sign-challenge-1',
    'channel': 'SMS',
    'destinationMasked': '+258*****4567',
    'documentFileId': 'file-proposal-pdf',
    'documentHash': 'abc',
    'expiresAt': '2027-01-10T08:35:00Z',
    'fallbackUsed': fallbackUsed,
    'resendAfterSeconds': 30,
  };

  static Map<String, Object?> signature() => {
    'signedAt': '2027-01-10T08:31:00Z',
    'channel': 'SMS',
    'destinationMasked': '+258*****4567',
    'documentFileId': 'file-proposal-pdf',
    'documentHash': 'abc',
    'assisted': false,
  };

  static Map<String, Object?> photoResponse(String slot, String id) => {
    'id': id,
    'slot': slot,
    'fileId': 'file-$id',
    'source': 'CAMERA',
    'uploadedAt': '2027-01-10T09:00:00Z',
  };

  static Map<String, Object?> myKyc({String state = 'NONE'}) => {
    'state': state,
  };

  static Map<String, Object?> kycCheck() => {
    'id': '01940000-0000-7000-8000-000000000801',
    'insurerId': proposalInsurerId,
    'status': 'PENDING',
    'idDocType': 'BI',
    'idDocNumberMasked': '********123A',
    'submittedAt': '2027-01-10T08:25:00Z',
  };
}

/// Rede simulada: [online] e as mudanças.
class FakeConnectivity implements ConnectivityMonitor {
  FakeConnectivity({bool online = true}) : _online = online;

  bool _online;
  final _changes = StreamController<bool>.broadcast();

  bool get online => _online;

  set online(bool value) {
    _online = value;
    _changes.add(value);
  }

  @override
  Future<bool> isOnline() async => _online;

  @override
  Stream<bool> get changes => _changes.stream;
}

/// Localização simulada (por defeito sem coordenadas).
class FakeLocation implements LocationService {
  FakeLocation([this.point]);

  final GeoPoint? point;
  int permissionRequests = 0;

  @override
  Future<bool> requestPermission() async {
    permissionRequests++;
    return point != null;
  }

  @override
  Future<GeoPoint?> current({
    Duration timeout = const Duration(seconds: 5),
  }) async => point;
}

/// Pasta temporária para os ficheiros da fila.
Directory tempFilesDirectory() =>
    Directory.systemTemp.createTempSync('moseguros-test-');
