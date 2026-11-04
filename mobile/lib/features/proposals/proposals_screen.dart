import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/format/dates.dart';
import '../../core/widgets/cta_button.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart';
import '../quote/widgets/step_scaffold.dart' show QuoteSteps;
import 'proposal_controller.dart';
import 'proposal_status_view.dart';

/// `/proposals`: as propostas do cliente, a mais recente primeiro, com o filtro
/// "Em curso" / "Todas" (S4-08 §4). Vazia: "Simular seguro" (o único laranja).
class ProposalsScreen extends ConsumerStatefulWidget {
  const ProposalsScreen({super.key});

  @override
  ConsumerState<ProposalsScreen> createState() => _ProposalsScreenState();
}

class _ProposalsScreenState extends ConsumerState<ProposalsScreen> {
  bool _activeOnly = true;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final proposals = ref.watch(myProposalsProvider);
    final value = proposals.value;

    Widget body;
    if (value != null) {
      final shown = _activeOnly
          ? value
                .where((p) => activeProposalStatuses.contains(p.status))
                .toList()
          : value;
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          SegmentedButton<bool>(
            key: const Key('proposals-filter'),
            segments: [
              ButtonSegment(
                value: true,
                label: Text(l10n.proposalsFilterActive),
              ),
              ButtonSegment(value: false, label: Text(l10n.proposalsFilterAll)),
            ],
            selected: {_activeOnly},
            onSelectionChanged: (selected) =>
                setState(() => _activeOnly = selected.first),
          ),
          const SizedBox(height: 16),
          if (shown.isEmpty) ...[
            Text(l10n.proposalsEmpty, key: const Key('proposals-empty')),
            const SizedBox(height: 16),
            CtaButton(
              key: const Key('proposals-simulate'),
              label: l10n.ctaQuote,
              onPressed: () => context.push(QuoteSteps.vehicle),
            ),
          ],
          for (final proposal in shown) ProposalCard(proposal: proposal),
        ],
      );
    } else if (proposals.hasError) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(Problem.from(proposals.error!).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(
            onPressed: () => ref.invalidate(myProposalsProvider),
            child: Text(l10n.retry),
          ),
        ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: Text(l10n.proposalsTitle)),
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: () => ref.refresh(myProposalsProvider.future),
          child: body,
        ),
      ),
    );
  }
}

/// Cartão de uma proposta (lista e início).
class ProposalCard extends StatelessWidget {
  const ProposalCard({super.key, required this.proposal});

  final ProposalSummaryResponse proposal;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final muted = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    final editable = isEditable(proposal.status);
    final expiresAt = proposal.expiresAt;
    return Card(
      key: Key('proposal-${proposal.id}'),
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    proposal.number,
                    style: theme.textTheme.titleSmall,
                  ),
                ),
                ProposalStatusChip(status: proposal.status),
              ],
            ),
            const SizedBox(height: 4),
            Text(
              [
                proposal.insurer?.displayName,
                proposal.productName,
              ].whereType<String>().join(' · '),
              style: muted,
            ),
            Text(
              '${proposal.vehiclePlate} · ${money(l10n, proposal.total)}',
              style: muted,
            ),
            if (expiresAt != null &&
                activeProposalStatuses.contains(proposal.status))
              Text(
                l10n.proposalExpiresOn(formatInstantDate(expiresAt)),
                style: muted,
              ),
            Align(
              alignment: AlignmentDirectional.centerEnd,
              child: TextButton(
                key: Key('proposal-${proposal.id}-open'),
                style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
                onPressed: () => context.push('/proposals/${proposal.id}'),
                child: Text(
                  editable ? l10n.proposalContinue : l10n.proposalView,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
