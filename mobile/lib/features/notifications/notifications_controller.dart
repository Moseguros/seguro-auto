import 'dart:async';

import 'package:built_collection/built_collection.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../../core/auth/session_controller.dart';
import '../../core/clock.dart';
import '../../core/network/connectivity.dart';
import '../policies/policies_controller.dart' show offlineError;

/// Notificações por ler (`countMyUnreadNotifications`), para o sino do início:
/// ao abrir, ao voltar ao primeiro plano e quando chega um push
/// (`ref.invalidate`). Sem sessão ou com erro: 0.
class UnreadCountController extends AsyncNotifier<int> {
  @override
  Future<int> build() async {
    final lifecycle = AppLifecycleListener(
      onResume: () => ref.invalidateSelf(),
    );
    ref.onDispose(lifecycle.dispose);
    final session = ref.watch(
      sessionControllerProvider.select((value) => value.value != null),
    );
    if (!session) return 0;
    try {
      final response = await ref
          .read(apiClientProvider)
          .notification
          .countMyUnreadNotifications();
      return response.data?.count ?? 0;
    } on Object {
      return state.value ?? 0;
    }
  }
}

final unreadCountProvider = AsyncNotifierProvider<UnreadCountController, int>(
  UnreadCountController.new,
  retry: (_, _) => null,
);

/// Uma página de notificações já lida, com as seguintes por pedir.
class NotificationsPage {
  const NotificationsPage({
    required this.items,
    required this.total,
    this.loadingMore = false,
  });

  final List<MyNotificationResponse> items;
  final int total;
  final bool loadingMore;

  bool get hasMore => items.length < total;

  NotificationsPage copyWith({
    List<MyNotificationResponse>? items,
    int? total,
    bool? loadingMore,
  }) => NotificationsPage(
    items: items ?? this.items,
    total: total ?? this.total,
    loadingMore: loadingMore ?? this.loadingMore,
  );
}

/// Tamanho de cada página (paginação infinita).
const notificationsPageSize = 20;

/// `/notifications`: "Todas" (`false`) ou "Por ler" (`true`). Sem rede: erro
/// (sem cache).
class NotificationsController extends AsyncNotifier<NotificationsPage> {
  NotificationsController(this.unreadOnly);

  final bool unreadOnly;

  NotificationApi get _api => ref.read(apiClientProvider).notification;

  @override
  Future<NotificationsPage> build() async {
    final page = await _page(0);
    return NotificationsPage(
      items: page.content.toList(),
      total: page.totalElements,
    );
  }

  Future<PageResponseMyNotificationResponse> _page(int page) async {
    if (!await isOnlineNow(ref)) throw offlineError();
    final response = await _api.listMyNotifications(
      unreadOnly: unreadOnly,
      page: page,
      size: notificationsPageSize,
    );
    return response.data!;
  }

  /// Fim da lista: a página seguinte.
  Future<void> loadMore() async {
    final current = state.value;
    if (current == null || current.loadingMore || !current.hasMore) return;
    state = AsyncData(current.copyWith(loadingMore: true));
    try {
      final page = await _page(current.items.length ~/ notificationsPageSize);
      if (!ref.mounted) return;
      final known = {for (final item in current.items) item.id};
      state = AsyncData(
        NotificationsPage(
          items: [
            ...current.items,
            for (final item in page.content)
              if (!known.contains(item.id)) item,
          ],
          total: page.totalElements,
        ),
      );
    } on Object {
      if (ref.mounted) state = AsyncData(current.copyWith(loadingMore: false));
    }
  }

  /// Marca como lida aqui (o pedido vai sem esperar pela resposta).
  void markReadLocally(String id) {
    final current = state.value;
    if (current == null) return;
    final now = ref.read(clockProvider)().toUtc();
    state = AsyncData(
      current.copyWith(
        items: [
          for (final item in current.items)
            item.id == id && item.readAt == null
                ? item.rebuild((b) => b..readAt = now)
                : item,
        ],
      ),
    );
  }
}

final notificationsProvider = AsyncNotifierProvider.autoDispose
    .family<NotificationsController, NotificationsPage, bool>(
      NotificationsController.new,
      retry: (_, _) => null,
    );

/// Acções sobre as notificações partilhadas pelo ecrã e pelo push.
class NotificationActions {
  NotificationActions(this._ref);

  final Ref _ref;

  NotificationApi get _api => _ref.read(apiClientProvider).notification;

  /// `markMyNotificationRead` sem esperar pela resposta; actualiza as listas
  /// abertas e a contagem.
  void markRead(String id) {
    for (final unreadOnly in [false, true]) {
      if (_ref.exists(notificationsProvider(unreadOnly))) {
        _ref
            .read(notificationsProvider(unreadOnly).notifier)
            .markReadLocally(id);
      }
    }
    unawaited(
      _api
          .markMyNotificationRead(id: id)
          .then<void>(
            (_) => _ref.invalidate(unreadCountProvider),
            onError: (Object _) {},
          ),
    );
  }

  /// "Marcar todas como lidas".
  Future<void> markAllRead() async {
    await _api.markAllMyNotificationsRead();
    _ref
      ..invalidate(notificationsProvider)
      ..invalidate(unreadCountProvider);
  }
}

final notificationActionsProvider = Provider<NotificationActions>(
  NotificationActions.new,
);

/// Canais externos: numa categoria obrigatória, pelo menos um fica ligado (o
/// `PUSH` não conta).
const externalChannels = {
  NotificationChannel.SMS,
  NotificationChannel.WHATSAPP,
  NotificationChannel.EMAIL,
};

/// A mesma validação do backend (`MANDATORY_CATEGORY_NEEDS_CHANNEL`): numa
/// categoria obrigatória, `SMS`, `WHATSAPP` e `EMAIL` não podem ficar todos
/// desligados.
bool mandatoryCategoryInvalid(
  NotificationCategoryPreferences category,
  Map<NotificationChannel, bool> enabled,
) =>
    category.mandatory &&
    !externalChannels.any((channel) => enabled[channel] ?? false);

/// `/notifications/preferences`: `getMyNotificationPreferences`.
class PreferencesController
    extends AsyncNotifier<NotificationPreferencesResponse> {
  NotificationApi get _api => ref.read(apiClientProvider).notification;

  @override
  Future<NotificationPreferencesResponse> build() async =>
      (await _api.getMyNotificationPreferences()).data!;

  /// Grava só as [categories] alteradas (pela ordem do pedido: os erros
  /// `categories[i].channels` usam este índice).
  Future<void> save(
    List<NotificationCategoryPreferencesChange> categories,
  ) async {
    final response = await _api.updateMyNotificationPreferences(
      updateNotificationPreferencesRequest:
          UpdateNotificationPreferencesRequest(
            (b) => b..categories = ListBuilder(categories),
          ),
    );
    state = AsyncData(response.data!);
  }

  /// Volta a ler (depois de activar o push).
  Future<void> reload() async {
    state = await AsyncValue.guard(build);
  }
}

final preferencesProvider =
    AsyncNotifierProvider.autoDispose<
      PreferencesController,
      NotificationPreferencesResponse
    >(PreferencesController.new, retry: (_, _) => null);
