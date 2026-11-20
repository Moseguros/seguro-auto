import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../l10n/app_localizations.dart';
import 'notifications_controller.dart';

/// Sino das acções da `AppBar` do início, com o número de notificações por ler
/// (S6-07 §5). Abre `/notifications`.
class NotificationBell extends ConsumerWidget {
  const NotificationBell({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final count = ref.watch(unreadCountProvider).value ?? 0;
    return IconButton(
      key: const Key('notification-bell'),
      tooltip: l10n.notificationsBell(count),
      onPressed: () => context.push('/notifications'),
      icon: Badge(
        key: const Key('notification-badge'),
        isLabelVisible: count > 0,
        label: Text(count > 99 ? '99+' : '$count'),
        child: Icon(
          count > 0 ? Icons.notifications : Icons.notifications_outlined,
        ),
      ),
    );
  }
}
