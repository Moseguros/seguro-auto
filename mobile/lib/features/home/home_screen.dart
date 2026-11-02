import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../l10n/app_localizations.dart';
import 'home_controller.dart';

/// Ecrã inicial: nome, frase e cartão "Estado do sistema".
class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(title: Text(l10n.appName)),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: SvgPicture.asset(
                isDark
                    ? 'assets/brand/logo-white.svg'
                    : 'assets/brand/logo.svg',
                height: 48,
                semanticsLabel: l10n.appName,
              ),
            ),
            const SizedBox(height: 24),
            Text(
              l10n.appName,
              style: theme.textTheme.headlineMedium?.copyWith(
                fontWeight: FontWeight.w700,
                color: theme.colorScheme.onSurface,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              l10n.homeTagline,
              style: theme.textTheme.titleMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: 24),
            const SystemStatusCard(),
          ],
        ),
      ),
    );
  }
}

/// Cartão com a versão e o commit da API, ou o erro com "Tentar de novo".
class SystemStatusCard extends ConsumerWidget {
  const SystemStatusCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final state = ref.watch(homeControllerProvider);

    final Widget content = switch (state) {
      AsyncData(:final value) => _VersionDetails(version: value),
      AsyncError(:final error) => Column(
        key: const Key('system-status-error'),
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '${l10n.systemStatusError} '
            '${apiErrorMessage(l10n, apiErrorCode(error))}',
            style: TextStyle(color: theme.colorScheme.error),
          ),
          const SizedBox(height: 16),
          OutlinedButton.icon(
            onPressed: () => ref.read(homeControllerProvider.notifier).retry(),
            icon: const Icon(Icons.refresh),
            label: Text(l10n.retry),
          ),
        ],
      ),
      _ => Row(
        key: const Key('system-status-loading'),
        children: [
          const SizedBox.square(
            dimension: 20,
            child: CircularProgressIndicator(strokeWidth: 2),
          ),
          const SizedBox(width: 16),
          Expanded(child: Text(l10n.systemStatusLoading)),
        ],
      ),
    };

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(l10n.systemStatusTitle, style: theme.textTheme.titleLarge),
            const SizedBox(height: 16),
            content,
          ],
        ),
      ),
    );
  }
}

class _VersionDetails extends StatelessWidget {
  const _VersionDetails({required this.version});

  final VersionResponse version;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final labelStyle = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('${l10n.systemStatusVersion}: ', style: labelStyle),
            Expanded(
              child: Text(
                version.version ?? l10n.systemStatusUnknown,
                key: const Key('api-version'),
                style: theme.textTheme.bodyMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('${l10n.systemStatusCommit}: ', style: labelStyle),
            Expanded(
              child: Text(
                version.commit ?? l10n.systemStatusUnknown,
                key: const Key('api-commit'),
                style: theme.textTheme.bodyMedium?.copyWith(
                  fontFamily: 'monospace',
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}
