import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';

/// Estados finais de um pagamento (o acompanhamento pára).
const finalPaymentStatuses = {
  PaymentStatus.SUCCEEDED,
  PaymentStatus.FAILED,
  PaymentStatus.EXPIRED,
  PaymentStatus.CANCELLED,
  PaymentStatus.REFUNDED,
};

bool isFinalPayment(PaymentStatus status) =>
    finalPaymentStatuses.contains(status);

/// `paymentStatus.<STATUS>`.
String paymentStatusLabel(AppLocalizations l10n, PaymentStatus status) =>
    switch (status) {
      PaymentStatus.CREATED => l10n.paymentStatusCreated,
      PaymentStatus.PENDING => l10n.paymentStatusPending,
      PaymentStatus.UNKNOWN => l10n.paymentStatusUnknown,
      PaymentStatus.SUCCEEDED => l10n.paymentStatusSucceeded,
      PaymentStatus.FAILED => l10n.paymentStatusFailed,
      PaymentStatus.EXPIRED => l10n.paymentStatusExpired,
      PaymentStatus.CANCELLED => l10n.paymentStatusCancelled,
      PaymentStatus.REFUNDED => l10n.paymentStatusRefunded,
      _ => status.name,
    };

/// `paymentMethod.<METHOD>` pelo nome da API (`MPESA`, `BANK_REFERENCE`).
String paymentMethodLabel(AppLocalizations l10n, String method) =>
    switch (method) {
      'MPESA' => l10n.paymentMethodMpesa,
      'BANK_REFERENCE' => l10n.paymentMethodBankReference,
      _ => method,
    };

/// `paymentFailure.<CODE>`.
String paymentFailureLabel(
  AppLocalizations l10n,
  FailureCode code,
) => switch (code) {
  FailureCode.CUSTOMER_CANCELLED => l10n.paymentFailureCustomerCancelled,
  FailureCode.INSUFFICIENT_BALANCE => l10n.paymentFailureInsufficientBalance,
  FailureCode.INVALID_MSISDN => l10n.paymentFailureInvalidMsisdn,
  FailureCode.PROVIDER_REJECTED => l10n.paymentFailureProviderRejected,
  FailureCode.PROVIDER_CONFIG_ERROR => l10n.paymentFailureProviderConfigError,
  FailureCode.PROVIDER_UNAVAILABLE => l10n.paymentFailureProviderUnavailable,
  FailureCode.TIMEOUT_EXPIRED => l10n.paymentFailureTimeoutExpired,
  FailureCode.EXPIRED => l10n.paymentFailureExpired,
  FailureCode.SUPERSEDED => l10n.paymentFailureSuperseded,
  FailureCode.MANUAL => l10n.paymentFailureManual,
  _ => l10n.paymentStatusFailed,
};

/// Etiqueta do estado de um pagamento.
class PaymentStatusChip extends StatelessWidget {
  const PaymentStatusChip({super.key, required this.status});

  final PaymentStatus status;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final scheme = Theme.of(context).colorScheme;
    final color = switch (status) {
      PaymentStatus.SUCCEEDED => Colors.green.shade700,
      PaymentStatus.FAILED => scheme.error,
      PaymentStatus.UNKNOWN => Colors.orange.shade800,
      _ => scheme.primary,
    };
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        border: Border.all(color: color),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        paymentStatusLabel(l10n, status),
        key: const Key('payment-status'),
        style: TextStyle(color: color, fontWeight: FontWeight.w600),
      ),
    );
  }
}
