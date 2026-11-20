import 'push_service.dart';

/// Sem push: a app corre sem projecto Firebase (CI, `local`, testes). Não faz
/// nada e `token()` devolve `null`.
class NoopPushService implements PushService {
  const NoopPushService();

  @override
  bool get supported => false;

  @override
  Future<void> init() async {}

  @override
  Future<bool> requestPermission() async => false;

  @override
  Future<String?> token() async => null;

  @override
  Stream<String> get onTokenRefresh => const Stream.empty();

  @override
  Stream<PushMessage> get onMessageOpened => const Stream.empty();

  @override
  Stream<PushMessage> get onForegroundMessage => const Stream.empty();

  @override
  Future<PushMessage?> initialMessage() async => null;
}
