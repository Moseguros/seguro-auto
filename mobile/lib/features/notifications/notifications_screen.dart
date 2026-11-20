import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/clock.dart';
import '../../core/deeplinks/deep_link_handler.dart';
import '../../core/errors/problem.dart';
import '../../core/format/dates.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'notifications_controller.dart';

/// "há 5 min", "há 2 h", "há 3 dias"; mais de 7 dias → `dd/MM/yyyy`.
String relativeTime(AppLocalizations l10n, DateTime instant, DateTime now) {
  final diff = now.toUtc().difference(instant.toUtc());
  if (diff.inMinutes < 1) return l10n.timeJustNow;
  if (diff.inHours < 1) return l10n.timeMinutesAgo(diff.inMinutes);
  if (diff.inDays < 1) return l10n.timeHoursAgo(diff.inHours);
  if (diff.inDays <= 7) return l10n.timeDaysAgo(diff.inDays);
  return formatInstantDate(instant);
}

/// Ícone de uma categoria; as desconhecidas (sprints seguintes) usam o genérico.
IconData notificationIcon(String category) => switch (category) {
  'PAYMENT' => Icons.payments_outlined,
  'POLICY' => Icons.description_outlined,
  'PROPOSAL' => Icons.assignment_outlined,
  'LEGAL' => Icons.gavel_outlined,
  'QUOTE' => Icons.calculate_outlined,
  _ => Icons.notifications_outlined,
};

/// `/notifications` (S6-07 §5): "Todas" / "Por ler", "Marcar todas como lidas",
/// paginação infinita; tocar marca como lida e abre a ligação (`deepLink`).
class NotificationsScreen extends ConsumerWidget {
  const NotificationsScreen({super.key});

  Future<void> _markAll(BuildContext context, WidgetRef ref) async {
    final l10n = AppLocalizations.of(context);
    try {
      await ref.read(notificationActionsProvider).markAllRead();
    } on Object catch (error) {
      if (!context.mounted) return;
      ScaffoldMessenger.of(context)
        ..hideCurrentSnackBar()
        ..showSnackBar(
          SnackBar(content: Text(Problem.from(error).message(l10n))),
        );
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final unread = ref.watch(unreadCountProvider).value ?? 0;
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: Text(l10n.notificationsTitle),
          actions: [
            IconButton(
              key: const Key('notifications-preferences'),
              tooltip: l10n.notificationsPreferences,
              icon: const Icon(Icons.tune),
              onPressed: () => context.push('/notifications/preferences'),
            ),
          ],
          bottom: TabBar(
            tabs: [
              Tab(
                key: const Key('notifications-tab-all'),
                text: l10n.notificationsTabAll,
              ),
              Tab(
                key: const Key('notifications-tab-unread'),
                text: l10n.notificationsTabUnread,
              ),
            ],
          ),
        ),
        body: SafeArea(
          child: Column(
            children: [
              if (unread > 0)
                Align(
                  alignment: AlignmentDirectional.centerEnd,
                  child: TextButton(
                    key: const Key('notifications-mark-all'),
                    style: TextButton.styleFrom(
                      minimumSize: const Size(48, 48),
                    ),
                    onPressed: () => _markAll(context, ref),
                    child: Text(l10n.notificationsMarkAll),
                  ),
                ),
              const Expanded(
                child: TabBarView(
                  children: [
                    _NotificationsList(unreadOnly: false),
                    _NotificationsList(unreadOnly: true),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _NotificationsList extends ConsumerWidget {
  const _NotificationsList({required this.unreadOnly});

  final bool unreadOnly;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final provider = notificationsProvider(unreadOnly);
    final state = ref.watch(provider);
    final value = state.value;
    if (value == null) {
      if (state.hasError) {
        final problem = Problem.from(state.error!);
        return ListView(
          padding: const EdgeInsets.all(16),
          children: [
            MessageBanner(
              problem.code == 'NETWORK_ERROR'
                  ? l10n.notificationsOffline
                  : problem.message(l10n),
              key: const Key('notifications-error'),
            ),
            const SizedBox(height: 16),
            OutlinedButton(
              style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: () => ref.invalidate(provider),
              child: Text(l10n.retry),
            ),
          ],
        );
      }
      return const Center(child: CircularProgressIndicator());
    }
    if (value.items.isEmpty) {
      return RefreshIndicator(
        onRefresh: () => ref.refresh(provider.future),
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(32),
          children: [
            Text(
              l10n.notificationsEmpty,
              key: const Key('notifications-empty'),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      );
    }
    final now = ref.read(clockProvider)();
    return RefreshIndicator(
      onRefresh: () => ref.refresh(provider.future),
      child: ListView.builder(
        physics: const AlwaysScrollableScrollPhysics(),
        itemCount: value.items.length + (value.hasMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index >= value.items.length) {
            WidgetsBinding.instance.addPostFrameCallback(
              (_) => ref.read(provider.notifier).loadMore(),
            );
            return const Padding(
              padding: EdgeInsets.all(16),
              child: Center(child: CircularProgressIndicator()),
            );
          }
          return NotificationItem(notification: value.items[index], now: now);
        },
      ),
    );
  }
}

/// Uma notificação: ícone da categoria, título, texto e data relativa. As por
/// ler têm fundo e um ponto azul.
class NotificationItem extends ConsumerWidget {
  const NotificationItem({
    super.key,
    required this.notification,
    required this.now,
  });

  final MyNotificationResponse notification;
  final DateTime now;

  void _open(WidgetRef ref) {
    if (notification.readAt == null) {
      ref.read(notificationActionsProvider).markRead(notification.id);
    }
    ref.read(deepLinkHandlerProvider).open(notification.deepLink);
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final scheme = Theme.of(context).colorScheme;
    final unread = notification.readAt == null;
    return Material(
      color: unread
          ? scheme.primary.withValues(alpha: 0.06)
          : Colors.transparent,
      child: ListTile(
        key: Key('notification-${notification.id}'),
        minVerticalPadding: 12,
        leading: Icon(notificationIcon(notification.category)),
        title: Text(
          notification.title,
          style: unread ? const TextStyle(fontWeight: FontWeight.w600) : null,
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(notification.body),
            const SizedBox(height: 4),
            Text(relativeTime(l10n, notification.createdAt, now)),
          ],
        ),
        trailing: unread
            ? Container(
                key: const Key('notification-unread-dot'),
                width: 10,
                height: 10,
                decoration: BoxDecoration(
                  color: scheme.primary,
                  shape: BoxShape.circle,
                ),
              )
            : null,
        onTap: () => _open(ref),
      ),
    );
  }
}
