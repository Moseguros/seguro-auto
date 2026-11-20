import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/errors/problem.dart';
import '../../core/format/dates.dart';
import '../../core/widgets/cta_button.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../quote/widgets/step_scaffold.dart' show QuoteSteps;
import 'policies_controller.dart';
import 'policy_labels.dart';
import 'policy_repository.dart';

/// Faixa dos dados da cache: "Sem ligação. Dados de {data}." ou o erro da
/// última actualização.
class CachedDataBanner extends StatelessWidget {
  const CachedDataBanner({super.key, required this.problem, this.syncedAt});

  final Problem? problem;
  final DateTime? syncedAt;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final problem = this.problem;
    if (problem == null) return const SizedBox.shrink();
    final syncedAt = this.syncedAt;
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: MessageBanner(
        problem.code == 'NETWORK_ERROR' && syncedAt != null
            ? l10n.policiesOffline(formatInstantDateTime(syncedAt))
            : problem.message(l10n),
        key: const Key('cached-data-banner'),
        kind: problem.code == 'NETWORK_ERROR'
            ? MessageKind.info
            : MessageKind.error,
      ),
    );
  }
}

/// Erro sem dados guardados: mensagem e "Tentar de novo".
class PolicyErrorView extends StatelessWidget {
  const PolicyErrorView({
    super.key,
    required this.error,
    required this.onRetry,
  });

  final Object error;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        MessageBanner(Problem.from(error).message(l10n)),
        const SizedBox(height: 16),
        OutlinedButton(
          key: const Key('policies-retry'),
          style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: onRetry,
          child: Text(l10n.retry),
        ),
      ],
    );
  }
}

/// `/policies` (S6-07 §3): separadores "Em vigor" / "Todas", a partir da cache.
class PolicyListScreen extends ConsumerWidget {
  const PolicyListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final policies = ref.watch(policyListProvider);
    final controller = ref.read(policyListProvider.notifier);

    Widget tab(bool activeOnly) {
      final value = policies.value;
      if (value == null) {
        if (policies.hasError) {
          return PolicyErrorView(
            error: policies.error!,
            onRetry: controller.refresh,
          );
        }
        return const Center(child: CircularProgressIndicator());
      }
      final entries = [
        for (final entry in value.value)
          if (!activeOnly || entry.isActive) entry,
      ];
      return RefreshIndicator(
        onRefresh: controller.refresh,
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16),
          children: [
            CachedDataBanner(problem: value.problem, syncedAt: value.syncedAt),
            if (entries.isEmpty)
              const _EmptyPolicies()
            else
              for (final entry in entries) PolicyCard(entry: entry),
          ],
        ),
      );
    }

    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: Text(l10n.policiesTitle),
          bottom: TabBar(
            tabs: [
              Tab(
                key: const Key('policies-tab-active'),
                text: l10n.policiesTabActive,
              ),
              Tab(
                key: const Key('policies-tab-all'),
                text: l10n.policiesTabAll,
              ),
            ],
          ),
        ),
        body: SafeArea(child: TabBarView(children: [tab(true), tab(false)])),
      ),
    );
  }
}

class _EmptyPolicies extends StatelessWidget {
  const _EmptyPolicies();

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Padding(
      key: const Key('policies-empty'),
      padding: const EdgeInsets.symmetric(vertical: 32),
      child: Column(
        children: [
          Icon(
            Icons.description_outlined,
            size: 56,
            color: theme.colorScheme.onSurfaceVariant,
          ),
          const SizedBox(height: 16),
          Text(
            l10n.policiesEmpty,
            textAlign: TextAlign.center,
            style: theme.textTheme.titleMedium,
          ),
          const SizedBox(height: 16),
          // O único botão laranja do ecrã.
          CtaButton(
            key: const Key('policies-quote'),
            label: l10n.ctaQuote,
            onPressed: () => context.push(QuoteSteps.vehicle),
          ),
        ],
      ),
    );
  }
}

/// Cartão de uma apólice: matrícula, marca e modelo, seguradora, produto,
/// estado da cobertura, "Válida até …" e n.º. Abre o detalhe.
class PolicyCard extends StatelessWidget {
  const PolicyCard({super.key, required this.entry});

  final PolicyEntry entry;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final makeModel = vehicleMakeModel(entry.vehicle);
    final secondary = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    return Card(
      key: Key('policy-${entry.id}'),
      margin: const EdgeInsets.only(bottom: 12),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: () => context.push('/policies/${entry.id}'),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Text(
                      entry.vehicle.plate,
                      style: theme.textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                  ),
                  CoverStatusChip(entry.coverStatus),
                ],
              ),
              if (makeModel.isNotEmpty) Text(makeModel),
              const SizedBox(height: 8),
              Text(
                [?entry.insurer.name, entry.productName].join(' · '),
                style: theme.textTheme.bodyLarge,
              ),
              Text(
                l10n.policiesValidUntil(formatInstantDateTime(entry.endAt)),
                style: secondary,
              ),
              Text(l10n.policiesNumber(entry.number), style: secondary),
            ],
          ),
        ),
      ),
    );
  }
}
