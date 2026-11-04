import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/misc.dart' show Override;
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/features/quote/quote_labels.dart';

import 'mock_api.dart';
import 'vehicles.dart';

/// "Hoje" nos testes das cotações (`Africa/Maputo`).
final quoteToday = DateTime(2027, 1, 10);

const requestId = '01930000-0000-7000-8000-000000000501';
const quoteDemoId = '01930000-0000-7000-8000-000000000601';
const quoteDemobId = '01930000-0000-7000-8000-000000000602';
const quoteThirdId = '01930000-0000-7000-8000-000000000603';
const quoteUnavailableId = '01930000-0000-7000-8000-000000000604';
const preContractFileId = '01930000-0000-7000-8000-000000000701';
const conditionsFileId = '01930000-0000-7000-8000-000000000702';

Map<String, Object?> mzn(int amount) => {'amount': amount, 'currency': 'MZN'};

/// Respostas da API das cotações e do catálogo.
abstract final class QuoteFixtures {
  static Map<String, Object?> coverage(
    String code, {
    bool mandatory = false,
    String capitalType = 'VEHICLE_VALUE',
    int? capital,
    List<int>? capitalOptions,
  }) => {
    'code': code,
    'mandatory': mandatory,
    'capitalType': capitalType,
    'capital': capital == null ? null : mzn(capital),
    'capitalOptions': capitalOptions?.map(mzn).toList(),
    'deductibleType': 'NONE',
  };

  static Map<String, Object?> product({
    required String insurer,
    String type = 'COMPREHENSIVE',
    String code = 'AUTO',
    List<Map<String, Object?>>? coverages,
  }) => {
    'productId': 'p-$insurer-$code',
    'productVersionId': 'v-$insurer-$code',
    'code': code,
    'name': 'Auto $insurer',
    'type': type,
    'status': 'PUBLISHED',
    'versionNo': 1,
    'validFrom': '2026-01-01',
    'vehicleCategories': ['LIGHT_PASSENGER'],
    'usages': ['PRIVATE'],
    'insurer': {'id': 'i-$insurer', 'displayName': 'Seguradora $insurer'},
    'documents': <Object>[],
    'coverages':
        coverages ??
        [
          coverage(
            'MTPL',
            mandatory: true,
            capitalType: 'FIXED',
            capital: 300000000,
          ),
          coverage(
            'VTPL',
            capitalType: 'OPTIONS',
            capitalOptions: [500000000, 1000000000],
          ),
          coverage('OD_COLLISION'),
          coverage('OD_FIRE'),
          coverage('OD_NATURE'),
          coverage('OD_THEFT'),
        ],
  };

  /// Duas seguradoras com danos próprios e uma com só RC.
  static Map<String, Object?> catalog() => {
    'content': [
      product(insurer: 'DEMO'),
      product(
        insurer: 'DEMOB',
        coverages: [
          coverage(
            'MTPL',
            mandatory: true,
            capitalType: 'FIXED',
            capital: 300000000,
          ),
          coverage(
            'VTPL',
            capitalType: 'OPTIONS',
            capitalOptions: [250000000, 500000000],
          ),
          coverage('OD_COLLISION'),
          coverage('OD_FIRE'),
          coverage('OD_NATURE'),
          coverage('OD_GLASS'),
        ],
      ),
      product(
        insurer: 'DEMO',
        type: 'MTPL_ONLY',
        code: 'RC',
        coverages: [
          coverage(
            'MTPL',
            mandatory: true,
            capitalType: 'FIXED',
            capital: 300000000,
          ),
        ],
      ),
    ],
    'page': 0,
    'size': 100,
    'totalElements': 3,
  };

  static Map<String, Object?> quote({
    String id = quoteDemoId,
    String insurer = 'DEMO',
    String status = 'OFFERED',
    int total = 5226200,
    String? reason,
  }) => {
    'id': id,
    'status': status,
    'insurer': {
      'id': 'i-$insurer',
      'displayName': 'Seguradora $insurer',
      'shortCode': insurer,
    },
    'product': {
      'id': 'p-$insurer',
      'code': '$insurer-AUTO',
      'name': 'Auto $insurer',
      'type': 'COMPREHENSIVE',
      'versionId': 'v-$insurer',
      'versionNo': 1,
      'generalConditionsFileId': conditionsFileId,
      'preContractInfoFileId': preContractFileId,
    },
    if (status != 'NOT_AVAILABLE') ...{
      'coverages': [
        {
          'code': 'MTPL',
          'mandatory': true,
          'capital': mzn(300000000),
          'deductible': {'type': 'NONE'},
          'premium': mzn(495000),
        },
        {
          'code': 'OD_COLLISION',
          'mandatory': false,
          'capital': mzn(85000000),
          'deductible': {
            'type': 'PERCENT',
            'percent': '10',
            'minAmount': mzn(2500000),
          },
          'premium': mzn(total - 495000 - 127344 - 90),
        },
      ],
      'commercialPremium': mzn(total - 127344 - 90),
      'charges': [
        {
          'code': '${insurer}_STAMP',
          'labelPt': 'Imposto de selo',
          'labelEn': 'Stamp duty',
          'amount': mzn(127344),
        },
      ],
      'roundingAdjustment': mzn(90),
      'totalAmount': mzn(total),
      'validUntil': '2027-01-25T08:15:00Z',
    },
    if (status == 'NOT_AVAILABLE') ...{
      'unavailableReason': reason ?? 'NO_MATCHING_ROW',
      'unavailableDetail': <String, Object?>{},
    },
  };

  /// Duas propostas (a da DEMO mais barata) e uma sem proposta.
  static List<Map<String, Object?>> defaultQuotes() => [
    quote(),
    quote(id: quoteDemobId, insurer: 'DEMOB', total: 6100000),
    quote(
      id: quoteUnavailableId,
      insurer: 'DEMOX',
      status: 'NOT_AVAILABLE',
      reason: 'INSURER_TIMEOUT',
    ),
  ];

  static Map<String, Object?> request({
    String id = requestId,
    List<Map<String, Object?>>? quotes,
    String origin = 'CUSTOMER',
    String startDate = '2027-01-15',
  }) => {
    'id': id,
    'origin': origin,
    'clientMsisdn': null,
    'createdAt': '2027-01-10T08:15:00Z',
    'productType': 'COMPREHENSIVE',
    'vehicle': {
      'vehicleId': vehicleId,
      'plate': 'AAA-100-MC',
      'make': 'Toyota',
      'model': 'Corolla',
      'year': 2018,
      'engineCc': 1598,
      'category': 'LIGHT_PASSENGER',
      'usage': 'PRIVATE',
      'seats': 5,
      'provinceOfCirculation': 'MAPUTO_CITY',
      'commercialValue': mzn(85000000),
      'hasTracker': false,
    },
    'driver': {
      'driverId': driverId,
      'birthDate': '1990-05-10',
      'licenceIssueDate': '2010-06-01',
      'licenceCategories': ['B'],
    },
    'requestedCoverages': [
      {'code': 'MTPL', 'capital': null},
      {'code': 'OD_COLLISION', 'capital': null},
      {'code': 'OD_FIRE', 'capital': null},
      {'code': 'OD_NATURE', 'capital': null},
    ],
    'startDate': startDate,
    'endDate': '2028-01-14',
    'durationDays': 365,
    'quotes': quotes ?? defaultQuotes(),
  };

  static Map<String, Object?> summary({
    String id = requestId,
    String origin = 'CUSTOMER',
    bool expired = false,
    int offeredCount = 2,
  }) => {
    'id': id,
    'createdAt': '2027-01-10T08:15:00Z',
    'origin': origin,
    'productType': 'COMPREHENSIVE',
    'vehicle': {'plate': 'AAA-100-MC', 'make': 'Toyota', 'model': 'Corolla'},
    'clientMsisdn': null,
    'startDate': '2027-01-15',
    'durationDays': 365,
    'offeredCount': offeredCount,
    'bestTotal': offeredCount == 0 ? null : mzn(5226200),
    'validUntil': expired ? null : '2027-01-25T08:15:00Z',
    'expired': expired,
  };

  static Map<String, Object?> page(
    List<Map<String, Object?>> content, {
    int? total,
    int page = 0,
    int size = 20,
  }) => {
    'content': content,
    'page': page,
    'size': size,
    'totalElements': total ?? content.length,
  };
}

/// Sessão, um veículo com valor comercial e o condutor habitual.
void stubQuoteBasics(
  MockApi api, {
  List<Map<String, Object?>>? vehicles,
  List<Map<String, Object?>>? drivers,
  List<Map<String, Object?>> recent = const [],
}) {
  stubCustomerSession(api);
  api
    ..on(
      'GET',
      '/api/v1/me/vehicles',
      200,
      VehicleFixtures.page(
        vehicles ??
            [
              VehicleFixtures.vehicle(
                drivers: [
                  {
                    'driverId': driverId,
                    'fullName': 'Ana Maria Mussa',
                    'habitual': true,
                  },
                ],
              ),
            ],
      ),
    )
    ..on(
      'GET',
      '/api/v1/me/drivers',
      200,
      VehicleFixtures.page(
        drivers ??
            [
              VehicleFixtures.driver(vehicleIds: [vehicleId]),
            ],
      ),
    )
    ..on('GET', '/api/v1/me/quotes', 200, QuoteFixtures.page(recent))
    ..on('GET', '/api/v1/catalog/products', 200, QuoteFixtures.catalog());
}

/// Abre a app com sessão, "hoje" fixo, e vai para [location].
Future<(ProviderContainer, GoRouter)> openQuote(
  WidgetTester tester,
  MockApi api,
  String location, {
  List<Override> overrides = const [],
  void Function(ProviderContainer container)? before,
}) async {
  final container = await pumpMoSeguros(
    tester,
    api,
    refreshToken: 'refresh-0',
    overrides: [
      quoteTodayProvider.overrideWithValue(() => quoteToday),
      ...overrides,
    ],
  );
  before?.call(container);
  final router = container.read(routerProvider)..go(location);
  await tester.pumpAndSettle();
  return (container, router);
}
