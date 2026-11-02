import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../l10n/app_localizations.dart';
import 'version_controller.dart';

/// Cartão "Sobre": a versão e o commit da API, ou o erro com "Tentar de novo".
class AboutCard extends ConsumerWidget {
  const AboutCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final state = ref.watch(versionControllerProvider);

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
            onPressed: () =>
                ref.read(versionControllerProvider.notifier).retry(),
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
            Text(l10n.aboutTitle, style: theme.textTheme.titleLarge),
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
