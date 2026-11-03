import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../l10n/app_localizations.dart';

/// Página de erro do router (rota inexistente).
class NotFoundScreen extends StatelessWidget {
  const NotFoundScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: Text(l10n.appName)),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(l10n.notFoundTitle, style: theme.textTheme.headlineSmall),
            const SizedBox(height: 8),
            Text(l10n.notFoundDescription),
            const SizedBox(height: 24),
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: FilledButton(
                onPressed: () => context.go('/'),
                child: Text(l10n.backHome),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
