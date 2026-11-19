import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/format/dates.dart';
import '../../core/widgets/cta_button.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart';
import '../quote/widgets/quote_document_button.dart';
import '../quote/widgets/step_scaffold.dart' show QuoteSteps;

/// Estado de uma proposta como etiqueta.
class ProposalStatusChip extends StatelessWidget {
  const ProposalStatusChip({super.key, required this.status});

  final ProposalStatus status;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final scheme = Theme.of(context).colorScheme;
    final color = switch (status) {
      ProposalStatus.REJECTED || ProposalStatus.EXPIRED => scheme.error,
      ProposalStatus.ACCEPTED ||
      ProposalStatus.PAID ||
      ProposalStatus.CONVERTED => Colors.green.shade700,
      _ => scheme.primary,
    };
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        border: Border.all(color: color),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        proposalStatusLabel(l10n, status.name),
        key: const Key('proposal-status'),
        style: TextStyle(color: color, fontWeight: FontWeight.w600),
      ),
    );
  }
}

/// Estado da proposta fora dos passos (S4-08 §5): cabeçalho, linha temporal e o
/// texto de cada estado.
class ProposalStatusView extends StatelessWidget {
  const ProposalStatusView({super.key, required this.proposal});

  final ProposalResponse proposal;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final decision = proposal.decision;
    final insurer = proposal.insurer?.displayName ?? '';
    final signature = proposal.signature;
    final expiresAt = proposal.expiresAt;
    final simulate = CtaButton(
      key: const Key('proposal-simulate'),
      label: l10n.ctaQuote,
      onPressed: () => context.push(QuoteSteps.vehicle),
    );

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Row(
          children: [
            Expanded(
              child: Text(proposal.number, style: theme.textTheme.titleLarge),
            ),
            ProposalStatusChip(status: proposal.status),
          ],
        ),
        const SizedBox(height: 4),
        Text(
          '${proposal.product.name} · ${money(l10n, proposal.premium.total)}',
          style: theme.textTheme.bodyMedium,
        ),
        const SizedBox(height: 16),
        _TimelineEntry(
          label: l10n.proposalTimelineCreated,
          at: proposal.createdAt,
        ),
        if (proposal.submittedAt case final submitted?)
          _TimelineEntry(label: l10n.proposalTimelineSubmitted, at: submitted),
        if (decision?.decidedAt case final decided?)
          _TimelineEntry(label: l10n.proposalTimelineDecided, at: decided),
        const SizedBox(height: 16),
        switch (proposal.status) {
          ProposalStatus.UNDER_REVIEW => MessageBanner(
            decision?.insurerDueAt == null
                ? l10n.proposalUnderReviewNoDue(insurer)
                : l10n.proposalUnderReviewText(
                    insurer,
                    formatInstantDate(decision!.insurerDueAt!),
                  ),
            key: const Key('proposal-under-review'),
            kind: MessageKind.info,
          ),
          ProposalStatus.ACCEPTED => Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              MessageBanner(
                l10n.proposalAcceptedText,
                key: const Key('proposal-accepted'),
                kind: MessageKind.success,
              ),
              const SizedBox(height: 8),
              if (expiresAt != null)
                Text(l10n.proposalExpiresOn(formatInstantDate(expiresAt))),
              const SizedBox(height: 16),
              // O único botão laranja do ecrã (S5-11 §2).
              CtaButton(
                key: const Key('proposal-pay'),
                label: l10n.paymentsPaySubmit,
                onPressed: () => context.push('/proposals/${proposal.id}/pay'),
              ),
            ],
          ),
          ProposalStatus.PAID => MessageBanner(
            l10n.paymentsProposalPaid,
            key: const Key('proposal-paid'),
            kind: MessageKind.success,
          ),
          ProposalStatus.REJECTED => Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              MessageBanner(
                [
                  l10n.proposalRejectedText,
                  if (decision?.rejectionReasonCode case final code?)
                    proposalRejectionReason(l10n, code.name),
                  ?decision?.reason,
                ].join(' '),
                key: const Key('proposal-rejected'),
              ),
              const SizedBox(height: 16),
              simulate,
            ],
          ),
          ProposalStatus.EXPIRED => Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              MessageBanner(
                l10n.proposalExpiredText,
                key: const Key('proposal-expired'),
              ),
              const SizedBox(height: 16),
              simulate,
            ],
          ),
          _ => const SizedBox.shrink(),
        },
        if (signature != null) ...[
          const SizedBox(height: 16),
          Align(
            alignment: AlignmentDirectional.centerStart,
            child: QuoteDocumentButton(
              fileId: signature.documentFileId,
              label: l10n.proposalDownloadSigned,
              name: 'proposta-${proposal.number}',
            ),
          ),
        ],
      ],
    );
  }
}

class _TimelineEntry extends StatelessWidget {
  const _TimelineEntry({required this.label, required this.at});

  final String label;
  final DateTime at;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          Icon(Icons.circle, size: 10, color: theme.colorScheme.primary),
          const SizedBox(width: 12),
          Expanded(child: Text(label)),
          Text(formatInstantDate(at), style: theme.textTheme.bodySmall),
        ],
      ),
    );
  }
}
