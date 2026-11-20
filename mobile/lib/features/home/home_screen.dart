import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/auth/session_controller.dart';
import '../../core/push/device_registration.dart';
import '../../core/push/push_service.dart';
import '../../core/widgets/cta_button.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../inspection/inspection_repository.dart';
import '../notifications/notification_bell.dart';
import '../onboarding/onboarding_controller.dart';
import '../policies/policies_controller.dart';
import '../policies/policy_repository.dart';
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

/// Atalho "Mostrar à polícia" (S6-07 §3): só com pelo menos uma apólice
/// `ACTIVE` guardada; funciona sem rede. Com várias, escolhe-se numa lista.
class _PoliceShortcut extends ConsumerWidget {
  const _PoliceShortcut();

  Future<void> _open(BuildContext context, List<PolicyEntry> policies) async {
    final l10n = AppLocalizations.of(context);
    var chosen = policies.length == 1 ? policies.single : null;
    chosen ??= await showModalBottomSheet<PolicyEntry>(
      context: context,
      showDragHandle: true,
      builder: (context) => SafeArea(
        child: ListView(
          shrinkWrap: true,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Text(
                l10n.homePoliceChoose,
                style: Theme.of(context).textTheme.titleLarge,
              ),
            ),
            for (final policy in policies)
              ListTile(
                key: Key('police-choose-${policy.id}'),
                leading: const Icon(Icons.directions_car_outlined),
                title: Text(policy.vehicle.plate),
                subtitle: Text(
                  [?policy.insurer.name, policy.productName].join(' · '),
                ),
                onTap: () => Navigator.of(context).pop(policy),
              ),
          ],
        ),
      ),
    );
    if (chosen == null || !context.mounted) return;
    await context.push('/policies/${chosen.id}/police');
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final policies = ref.watch(activePoliciesProvider).value ?? const [];
    if (policies.isEmpty) return const SizedBox.shrink();
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Card(
        key: const Key('home-police'),
        child: ListTile(
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 8,
          ),
          leading: const Icon(Icons.qr_code_2, size: 36),
          title: Text(l10n.policyShowToPolice),
          subtitle: Text(
            policies.length == 1
                ? policies.single.vehicle.plate
                : l10n.homePoliceText,
          ),
          trailing: const Icon(Icons.chevron_right),
          onTap: () => _open(context, policies),
        ),
      ),
    );
  }
}

/// "As minhas apólices" (questão de 19/11/2026: entrada pela lista no início).
class _PoliciesCard extends StatelessWidget {
  const _PoliciesCard();

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Card(
      key: const Key('home-policies'),
      child: ListTile(
        leading: const Icon(Icons.description_outlined),
        title: Text(l10n.policiesTitle),
        subtitle: Text(l10n.homePoliciesText),
        trailing: const Icon(Icons.chevron_right),
        onTap: () => context.push('/policies'),
      ),
    );
  }
}

/// Chave em `shared_preferences`: o pedido de permissão do push já foi feito.
const pushPromptDoneKey = 'push_prompt_done';

/// Na primeira vez que o início abre com sessão, um `BottomSheet` pede para
/// activar os avisos (S6-07 §4). Não aparece sem push ([PushService.supported]).
Future<void> maybeAskForPush(BuildContext context, WidgetRef ref) async {
  final push = ref.read(pushServiceProvider);
  final preferences = ref.read(sharedPreferencesProvider);
  if (!push.supported || (preferences.getBool(pushPromptDoneKey) ?? false)) {
    return;
  }
  await preferences.setBool(pushPromptDoneKey, true);
  if (!context.mounted) return;
  final l10n = AppLocalizations.of(context);
  final enable = await showModalBottomSheet<bool>(
    context: context,
    showDragHandle: true,
    builder: (context) => SafeArea(
      child: Padding(
        key: const Key('push-prompt'),
        padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              l10n.pushPromptTitle,
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text(l10n.pushPromptText),
            const SizedBox(height: 16),
            FilledButton(
              key: const Key('push-prompt-enable'),
              style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: () => Navigator.of(context).pop(true),
              child: Text(l10n.pushPromptEnable),
            ),
            const SizedBox(height: 8),
            TextButton(
              key: const Key('push-prompt-later'),
              style: TextButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: () => Navigator.of(context).pop(false),
              child: Text(l10n.pushPromptLater),
            ),
          ],
        ),
      ),
    ),
  );
  if (enable != true) return;
  if (await push.requestPermission()) {
    await ref.read(deviceRegistrationProvider).register();
  }
}

/// Primeiro nome de um nome completo, ou `null` sem nome.
String? firstName(String? displayName) {
  final name = displayName?.trim() ?? '';
  return name.isEmpty ? null : name.split(RegExp(r'\s+')).first;
}

/// `/`: saudação, sino das notificações, aviso de perfil incompleto, "Mostrar
/// à polícia", "Simular seguro" e as apólices.
class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted && ref.read(sessionControllerProvider).value != null) {
        maybeAskForPush(context, ref);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final me = ref.watch(sessionControllerProvider).value?.me;
    final name = firstName(me?.displayName);

    return Scaffold(
      // O perfil está na barra inferior (S2-07).
      appBar: AppBar(
        title: Text(l10n.appName),
        actions: const [NotificationBell()],
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
                        onPressed: () => context.go('/profile?complete=1'),
                        child: Text(l10n.homeCompleteProfile),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
            ],
            // Atalho "Mostrar à polícia", só com uma apólice activa guardada.
            const _PoliceShortcut(),
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
            const _PoliciesCard(),
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
