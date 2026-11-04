import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/auth/session_controller.dart';
import '../../core/widgets/cta_button.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_repository.dart';
import '../quote/widgets/recent_quotes.dart';
import '../quote/widgets/step_scaffold.dart' show QuoteSteps;

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
            // As 3 simulações mais recentes, só quando existem (S3-05 §7).
            const _RecentQuotesCard(),
          ],
        ),
      ),
    );
  }
}
