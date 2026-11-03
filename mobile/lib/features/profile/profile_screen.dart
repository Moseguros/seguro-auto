import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/auth/session_controller.dart';
import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'about_card.dart';
import 'contacts_section.dart';
import 'profile_controller.dart';
import 'profile_form.dart';

/// `/profile` (com `?complete=1` depois do primeiro login): dados pessoais,
/// contactos, "Terminar sessão" e "Sobre".
class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key, this.completing = false});

  /// Veio de `?complete=1`: mostra o aviso e, ao completar, vai para o início.
  final bool completing;

  Future<void> _logout(BuildContext context, WidgetRef ref) async {
    final l10n = AppLocalizations.of(context);
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(l10n.logoutConfirmTitle),
        content: Text(l10n.logoutConfirmText),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text(l10n.cancel),
          ),
          TextButton(
            key: const Key('confirm-logout'),
            onPressed: () => Navigator.of(context).pop(true),
            child: Text(l10n.logout),
          ),
        ],
      ),
    );
    // O router vai para o login quando a sessão termina.
    if (confirmed == true) {
      await ref.read(sessionControllerProvider.notifier).logout();
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final profile = ref.watch(profileControllerProvider);
    final controller = ref.read(profileControllerProvider.notifier);

    // Durante uma recarga (409) o formulário mantém-se com os dados anteriores.
    final value = profile.value;
    final Widget form;
    if (value != null) {
      form = ProfileForm(
        key: ValueKey(value.version),
        profile: value,
        onSave: controller.save,
        onSaved: (saved) {
          if (!context.mounted) return;
          if (completing && saved.complete) {
            context.go('/');
          }
          ScaffoldMessenger.of(context)
            ..hideCurrentSnackBar()
            ..showSnackBar(SnackBar(content: Text(l10n.profileSaved)));
        },
        onConflict: () {
          // O formulário é recriado com a versão nova: o aviso fica numa SnackBar.
          ScaffoldMessenger.of(context)
            ..hideCurrentSnackBar()
            ..showSnackBar(
              SnackBar(content: Text(l10n.errorConcurrentModification)),
            );
          controller.reload();
        },
      );
    } else if (profile.hasError) {
      form = MessageBanner(Problem.from(profile.error!).message(l10n));
    } else {
      form = const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: CircularProgressIndicator(),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(title: Text(l10n.profileTitle)),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            if (completing) ...[
              MessageBanner(
                l10n.profileCompleteNotice,
                key: const Key('complete-notice'),
                kind: MessageKind.info,
              ),
              const SizedBox(height: 16),
            ],
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Text(
                      l10n.profilePersonalData,
                      style: theme.textTheme.titleLarge,
                    ),
                    const SizedBox(height: 16),
                    form,
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            const ContactsSection(),
            const SizedBox(height: 24),
            OutlinedButton.icon(
              key: const Key('logout'),
              style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: () => _logout(context, ref),
              icon: const Icon(Icons.logout),
              label: Text(l10n.logout),
            ),
            const SizedBox(height: 24),
            const AboutCard(),
          ],
        ),
      ),
    );
  }
}
