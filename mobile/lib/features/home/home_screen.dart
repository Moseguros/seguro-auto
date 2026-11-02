import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/auth/session_controller.dart';
import '../../core/widgets/cta_button.dart';
import '../../l10n/app_localizations.dart';

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
      appBar: AppBar(
        title: Text(l10n.appName),
        actions: [
          IconButton(
            key: const Key('open-profile'),
            tooltip: l10n.profileOpen,
            icon: const Icon(Icons.account_circle_outlined),
            onPressed: () => context.push('/profile'),
          ),
        ],
      ),
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
                        onPressed: () => context.push('/profile?complete=1'),
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
                    // A simulação chega no Sprint 3.
                    CtaButton(
                      label: l10n.ctaQuote,
                      onPressed: () => ScaffoldMessenger.of(context)
                        ..hideCurrentSnackBar()
                        ..showSnackBar(
                          SnackBar(content: Text(l10n.comingSoon)),
                        ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
