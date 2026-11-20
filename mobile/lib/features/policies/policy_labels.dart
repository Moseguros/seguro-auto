import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';
import 'package:qr_flutter/qr_flutter.dart';

import '../../core/theme.dart';
import '../../l10n/app_localizations.dart';

/// Textos das apólices, iguais aos da web (S6-05 §9).
String policyStatusLabel(AppLocalizations l10n, PolicyStatus status) =>
    switch (status) {
      PolicyStatus.ACTIVE => l10n.policyStatusActive,
      PolicyStatus.SUSPENDED => l10n.policyStatusSuspended,
      PolicyStatus.CANCELLED => l10n.policyStatusCancelled,
      PolicyStatus.EXPIRED => l10n.policyStatusExpired,
      PolicyStatus.LAPSED => l10n.policyStatusLapsed,
      _ => status.name,
    };

String coverStatusLabel(AppLocalizations l10n, CoverStatus status) =>
    switch (status) {
      CoverStatus.COVERED => l10n.coverStatusCovered,
      CoverStatus.NOT_STARTED => l10n.coverStatusNotStarted,
      CoverStatus.ENDED => l10n.coverStatusEnded,
      CoverStatus.SUSPENDED => l10n.coverStatusSuspended,
      CoverStatus.CANCELLED => l10n.coverStatusCancelled,
      CoverStatus.LAPSED => l10n.coverStatusLapsed,
      _ => status.name,
    };

String coverageTypeLabel(AppLocalizations l10n, CoverageType type) =>
    switch (type) {
      CoverageType.MTPL_ONLY => l10n.coverageTypeMtplOnly,
      CoverageType.COMPREHENSIVE => l10n.coverageTypeComprehensive,
      CoverageType.TEMPORARY => l10n.coverageTypeTemporary,
      CoverageType.BORDER => l10n.coverageTypeBorder,
      _ => type.name,
    };

String paymentPlanLabel(AppLocalizations l10n, PaymentPlan plan) =>
    switch (plan) {
      PaymentPlan.FULL => l10n.paymentPlanFull,
      PaymentPlan.SEMIANNUAL => l10n.paymentPlanSemiannual,
      PaymentPlan.QUARTERLY => l10n.paymentPlanQuarterly,
      PaymentPlan.MONTHLY => l10n.paymentPlanMonthly,
      _ => plan.name,
    };

String renewalTypeLabel(AppLocalizations l10n, RenewalType type) =>
    switch (type) {
      RenewalType.ANNUAL_RENEWABLE => l10n.renewalTypeAnnualRenewable,
      RenewalType.FIXED_TERM => l10n.renewalTypeFixedTerm,
      _ => type.name,
    };

String instalmentStatusLabel(AppLocalizations l10n, InstalmentStatus status) =>
    switch (status) {
      InstalmentStatus.DUE => l10n.instalmentStatusDue,
      InstalmentStatus.PAID => l10n.instalmentStatusPaid,
      InstalmentStatus.OVERDUE => l10n.instalmentStatusOverdue,
      InstalmentStatus.CANCELLED => l10n.instalmentStatusCancelled,
      _ => status.name,
    };

String documentTypeLabel(AppLocalizations l10n, PolicyDocumentType type) =>
    switch (type) {
      PolicyDocumentType.CERTIFICATE => l10n.documentTypeCertificate,
      PolicyDocumentType.SCHEDULE => l10n.documentTypeSchedule,
      PolicyDocumentType.GENERAL_CONDITIONS =>
        l10n.documentTypeGeneralConditions,
      PolicyDocumentType.PRE_CONTRACT_INFO => l10n.documentTypePreContractInfo,
      PolicyDocumentType.PROPOSAL => l10n.documentTypeProposal,
      _ => type.name,
    };

/// QR do certificado (`verificationUrl`, S6-02), sempre em fundo branco.
class CertificateQr extends StatelessWidget {
  const CertificateQr({super.key, required this.data, required this.size});

  /// O endereço de verificação (`/v/{token}`).
  final String data;
  final double size;

  @override
  Widget build(BuildContext context) => QrImageView(
    data: data,
    size: size,
    backgroundColor: Colors.white,
    semanticsLabel: AppLocalizations.of(context).policeQrHint,
  );
}

/// "Toyota Corolla" de um veículo da apólice (vazio sem marca nem modelo).
String vehicleMakeModel(PolicyVehicleRef vehicle) =>
    [?vehicle.make, ?vehicle.model].where((part) => part.isNotEmpty).join(' ');

/// Etiqueta do estado da cobertura (verde em vigor; cinzento nos outros).
class CoverStatusChip extends StatelessWidget {
  const CoverStatusChip(this.status, {super.key});

  final CoverStatus status;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final scheme = Theme.of(context).colorScheme;
    final covered = status == CoverStatus.COVERED;
    final color = covered
        ? (scheme.brightness == Brightness.dark
              ? BrandColors.successDark
              : BrandColors.success)
        : scheme.onSurfaceVariant;
    return _StatusChip(label: coverStatusLabel(l10n, status), color: color);
  }
}

/// Etiqueta do estado da apólice.
class PolicyStatusChip extends StatelessWidget {
  const PolicyStatusChip(this.status, {super.key});

  final PolicyStatus status;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final scheme = Theme.of(context).colorScheme;
    final color = status == PolicyStatus.ACTIVE
        ? scheme.primary
        : scheme.onSurfaceVariant;
    return _StatusChip(label: policyStatusLabel(l10n, status), color: color);
  }
}

class _StatusChip extends StatelessWidget {
  const _StatusChip({required this.label, required this.color});

  final String label;
  final Color color;

  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
    decoration: BoxDecoration(
      color: color.withValues(alpha: 0.1),
      border: Border.all(color: color.withValues(alpha: 0.5)),
      borderRadius: BorderRadius.circular(16),
    ),
    child: Text(
      label,
      style: Theme.of(context).textTheme.labelMedium?.copyWith(
        color: color,
        fontWeight: FontWeight.w600,
      ),
    ),
  );
}
