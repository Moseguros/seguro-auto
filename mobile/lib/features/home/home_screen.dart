import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/auth/session_controller.dart';
import '../../core/widgets/cta_button.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../inspection/inspection_repository.dart';
import '../proposals/proposal_controller.dart';
import '../proposals/proposals_screen.dart' show ProposalCard;
import '../quote/quote_repository.dart';
import '../quote/widgets/recent_quotes.dart';
import '../quote/widgets/step_scaffold.dart' show QuoteSteps;

/// "Propostas em curso" (S4-09 §1): até 3, "Ver todas" e as fotografias da
/// vistoria por enviar.
class _ProposalsCard extends ConsumerWidget {
  const _ProposalsCard();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final proposals = ref.watch(homeProposalsProvider).value ?? const [];
    final pending = ref.watch(pendingUploadsProvider(null)).value ?? 0;
    if (proposals.isEmpty && pending == 0) return const SizedBox.shrink();
    return Card(
      key: const Key('home-proposals'),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(l10n.homeProposalsTitle, style: theme.textTheme.titleLarge),
            if (pending > 0) ...[
              const SizedBox(height: 8),
              MessageBanner(
                l10n.inspectionPendingUploads(pending),
                key: const Key('home-pending-uploads'),
                kind: MessageKind.info,
              ),
            ],
            const SizedBox(height: 8),
            for (final proposal in proposals) ProposalCard(proposal: proposal),
            Align(
              alignment: AlignmentDirectional.centerEnd,
              child: TextButton(
                key: const Key('home-proposals-all'),
                style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
                onPressed: () => context.push('/proposals'),
                child: Text(l10n.homeProposalsSeeAll),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _RecentQuotesCard extends ConsumerWidget {
  const _RecentQuotesCard();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final recent = ref.watch(recentQuotesProvider).value;
    if (recent == null || recent.isEmpty) return const SizedBox.shrink();
    return const Card(
      key: Key('home-my-quotes'),
      child: Padding(padding: EdgeInsets.all(16), child: RecentQuotes()),
    );
  }
}

/// Primeiro nome de um nome completo, ou `null` sem nome.
String? firstName(String? displayName) {
  final name = displayName?.trim() ?? '';
  return name.isEmpty ? null : name.split(RegExp(r'\s+')).first;
}

/// `/`: saudação, aviso de perfil incompleto e "Simular seguro".
class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final me = ref.watch(sessionControllerProvider).value?.me;
    final name = firstName(me?.displayName);

    return Scaffold(
      // O perfil está na barra inferior (S2-07).
      appBar: AppBar(title: Text(l10n.appName)),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(
              name == null ? l10n.homeGreetingNoName : l10n.homeGreeting(name),
              key: const Key('home-greeting'),
              style: theme.textTheme.headlineMedium?.copyWith(
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 24),
            if (me?.profileComplete == false) ...[
              Card(
                key: const Key('incomplete-profile'),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(l10n.homeIncompleteProfile),
                      const SizedBox(height: 16),
                      FilledButton(
                        onPressed: () => context.go('/profile?complete=1'),
                        child: Text(l10n.homeCompleteProfile),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
            ],
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      l10n.homeQuoteTitle,
                      style: theme.textTheme.titleLarge,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      l10n.homeQuoteText,
                      style: theme.textTheme.bodyMedium?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      ),
                    ),
                    const SizedBox(height: 16),
                    CtaButton(
                      key: const Key('home-quote'),
                      label: l10n.ctaQuote,
                      onPressed: () => context.push(QuoteSteps.vehicle),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            // Propostas em curso (S4-09 §1), só quando existem.
            const _ProposalsCard(),
            const SizedBox(height: 16),
            // As 3 simulações mais recentes, só quando existem (S3-05 §7).
            const _RecentQuotesCard(),
          ],
        ),
      ),
    );
  }
}
