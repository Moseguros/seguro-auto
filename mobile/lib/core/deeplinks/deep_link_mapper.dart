/// Ligações profundas `moseguros://` (S4-09 §1, S6-07 §6).
library;

/// Esquema das ligações profundas da app (`AGENTS.md` §1).
const deepLinkScheme = 'moseguros';

final _uuid = RegExp(
  r'^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$',
);

/// Caminho interno de uma ligação profunda:
///
/// | Ligação | Rota |
/// | --- | --- |
/// | `moseguros://policy/{uuid}` | `/policies/{uuid}` |
/// | `moseguros://proposal/{uuid}` | `/proposals/{uuid}` |
/// | `moseguros://proposal/{uuid}/inspection` | `/proposals/{uuid}/inspection` |
/// | `moseguros://payment/{uuid}` | `/payments/{uuid}` |
/// | `moseguros://quote/{uuid}` | `/quote/results/{uuid}` |
/// | `moseguros://notifications` | `/notifications` |
/// | outro *host*, `uuid` inválido ou partes a mais | `/` |
///
/// O deep linking do Flutter entrega o URI inteiro: a primeira parte é o *host*.
/// `null` se [uri] não for uma ligação `moseguros://` (caminhos internos).
String? deepLinkLocation(Uri uri) {
  if (uri.scheme != deepLinkScheme) return null;
  final segments = [
    if (uri.host.isNotEmpty) uri.host,
    ...uri.pathSegments.where((segment) => segment.isNotEmpty),
  ];
  if (segments.length == 1 && segments.first == 'notifications') {
    return '/notifications';
  }
  if (segments.length < 2 || !_uuid.hasMatch(segments[1])) return '/';
  final id = segments[1];
  if (segments.length == 3) {
    return segments[0] == 'proposal' && segments[2] == 'inspection'
        ? '/proposals/$id/inspection'
        : '/';
  }
  if (segments.length != 2) return '/';
  return switch (segments.first) {
    'policy' => '/policies/$id',
    'proposal' => '/proposals/$id',
    'payment' => '/payments/$id',
    'quote' => '/quote/results/$id',
    _ => '/',
  };
}

/// O mesmo que [deepLinkLocation], para quem tem a ligação em texto (dados de
/// um push, `deepLink` de uma notificação).
abstract final class DeepLinkMapper {
  /// `null` com texto vazio, inválido ou sem o esquema `moseguros`.
  static String? map(String? link) {
    if (link == null || link.trim().isEmpty) return null;
    final uri = Uri.tryParse(link.trim());
    return uri == null ? null : deepLinkLocation(uri);
  }
}
