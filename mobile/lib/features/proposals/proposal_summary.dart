import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/format/dates.dart';
import '../../core/l10n/province_name.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart';
import 'steps/step_host.dart';

/// Data de início da proposta em Maputo.
DateTime proposalStartDate(ProposalResponse proposal) =>
    dateInMaputo(proposal.startAt);

/// Fim calculado para um início novo, com a mesma duração (S4-02).
DateTime proposalEndFor(ProposalResponse proposal, DateTime start) {
  final days = proposal.endDate
      .toDateTime()
      .difference(proposalStartDate(proposal))
      .inDays;
  return DateTime(start.year, start.month, start.day + days);
}

/// Seguradora, produto, coberturas e total (resumo curto da proposta).
class ProposalSummaryCard extends StatelessWidget {
  const ProposalSummaryCard({
    super.key,
    required this.proposal,
    this.detailed = false,
  });

  final ProposalResponse proposal;

  /// Também o período, o veículo e o condutor (resumo do último passo).
  final bool detailed;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final vehicle = proposal.vehicle;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              proposal.insurer?.displayName ?? '',
              style: theme.textTheme.titleMedium,
            ),
            Text(
              proposal.product.name,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
            const Divider(height: 24),
            Text(l10n.proposalCoverages, style: theme.textTheme.titleSmall),
            for (final coverage in proposal.coverages)
              InfoLine(
                coverageName(l10n, coverage.code.name),
                money(l10n, coverage.premium),
              ),
            const Divider(height: 24),
            InfoLine(
              l10n.proposalTotal,
              money(l10n, proposal.premium.total),
              strong: true,
            ),
            if (detailed) ...[
              const Divider(height: 24),
              InfoLine(
                l10n.quoteDetailPeriod,
                l10n.quotePeriodRange(
                  formatDate(proposalStartDate(proposal)),
                  apiDate(proposal.endDate),
                ),
              ),
              InfoLine(
                l10n.proposalVehicle,
                quotedVehicleTitle(
                  plate: vehicle.plate,
                  make: vehicle.make,
                  model: vehicle.model,
                ),
              ),
              InfoLine(l10n.proposalDriver, proposal.driver.fullName),
            ],
          ],
        ),
      ),
    );
  }
}

/// Dados do tomador (mascarados).
class PolicyholderCard extends StatelessWidget {
  const PolicyholderCard({super.key, required this.policyholder});

  final ProposalPolicyholder policyholder;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final address = policyholder.address;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              l10n.proposalPolicyholderTitle,
              style: theme.textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            InfoLine(l10n.profileFieldFullName, policyholder.fullName),
            InfoLine(
              l10n.profileFieldBirthDate,
              apiDate(policyholder.birthDate),
            ),
            InfoLine(
              idDocTypeName(l10n, policyholder.idDocType),
              policyholder.idDocNumberMasked,
            ),
            InfoLine(l10n.profileFieldNuit, policyholder.nuitMasked),
            InfoLine(
              l10n.profileFieldProvince,
              provinceName(l10n, policyholder.province),
            ),
            if (address != null && address.isNotEmpty)
              InfoLine(l10n.profileFieldAddress, address),
          ],
        ),
      ),
    );
  }
}

/// Nome do tipo de documento de identificação.
String idDocTypeName(AppLocalizations l10n, String type) => switch (type) {
  'BI' => l10n.idDocTypeBi,
  'PASSPORT' => l10n.idDocTypePassport,
  'DIRE' => l10n.idDocTypeDire,
  _ => l10n.profileFieldIdDocNumber,
};
