import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'proposals.dart';

/// Escolhe uma forma de pagamento (`MPESA`, `BANK_REFERENCE`) no ecrã de
/// pagamento, fazendo scroll até ela.
Future<void> chooseMethod(WidgetTester tester, String method) async {
  final card = find.byKey(Key('method-$method'));
  await tester.ensureVisible(card);
  await tester.pumpAndSettle();
  await tester.tap(
    find.descendant(of: card, matching: find.byType(Radio<String>)),
  );
  await tester.pumpAndSettle();
}

/// Respostas simuladas dos pagamentos do cliente (S5-11).
const paymentIntentId = '01950000-0000-7000-8000-000000000701';
const paymentMethodsPath = '/api/v1/proposals/$proposalId/payment-methods';
const proposalPayPath = '/api/v1/proposals/$proposalId/payment-intents';
const paymentIntentPath = '/api/v1/payment-intents/$paymentIntentId';
const myPaymentsPath = '/api/v1/me/payments';

Map<String, Object?> _money(int amount) => {
  'amount': amount,
  'currency': 'MZN',
};

abstract final class PaymentFixtures {
  static Map<String, Object?> methods({
    List<Map<String, Object?>>? methods,
    bool paid = false,
    Map<String, Object?>? activeIntent,
  }) => {
    'proposalId': proposalId,
    'proposalNumber': proposalNumber,
    'insurer': {'id': proposalInsurerId, 'displayName': 'Seguradora Demo'},
    'amount': _money(299900),
    'expiresAt': '2027-01-20T10:00:00Z',
    'paid': paid,
    'methods':
        methods ??
        [
          {'method': 'MPESA', 'sortOrder': 1, 'displayName': 'M-Pesa Vodacom'},
          {'method': 'BANK_REFERENCE', 'sortOrder': 2},
        ],
    'activeIntent': ?activeIntent,
  };

  static Map<String, Object?> intent({
    String id = paymentIntentId,
    String status = 'PENDING',
    String method = 'MPESA',
    String? failureCode,
    Map<String, Object?>? nextAction,
  }) => {
    'id': id,
    'reference': 'PG-2027-0001234',
    'status': status,
    'method': method,
    'purpose': 'NEW_POLICY',
    'target': {'type': 'PROPOSAL', 'id': proposalId, 'label': proposalNumber},
    'insurer': {'id': proposalInsurerId, 'displayName': 'Seguradora Demo'},
    'payerUserId': '0190f0f0-0000-7000-8000-000000000001',
    'amount': _money(299900),
    if (method == 'MPESA') 'payerMsisdnMasked': '+258*****4567',
    'failureCode': ?failureCode,
    'expiresAt': '2027-01-20T10:00:00Z',
    'createdAt': '2027-01-10T09:00:00Z',
    'nextAction':
        nextAction ??
        (method == 'MPESA'
            ? {
                'type': 'AWAIT_CUSTOMER_CONFIRMATION',
                // `proposalToday` + 90 s.
                'timeoutAt': '2027-01-10T09:01:30Z',
              }
            : {
                'type': 'PAY_BY_REFERENCE',
                'entity': '12345',
                'reference': '00548781108',
                'amount': _money(299900),
                'validUntil': '2027-01-20T10:00:00Z',
              }),
  };

  static Map<String, Object?> page(
    List<Map<String, Object?>> content, {
    int page = 0,
    int size = 20,
    int? total,
  }) => {
    'content': content,
    'page': page,
    'size': size,
    'totalElements': total ?? content.length,
  };
}
