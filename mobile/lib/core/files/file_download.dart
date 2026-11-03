import 'dart:typed_data';

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../api/api_client.dart';

/// Conteúdo de um ficheiro descarregado com `downloadFile` (S2-01).
class DownloadedFile {
  const DownloadedFile({required this.bytes, required this.contentType});

  final Uint8List bytes;

  /// `Content-Type` da resposta (ex.: `image/jpeg`).
  final String contentType;

  bool get isImage => contentType == 'image/jpeg' || contentType == 'image/png';
}

/// `GET /api/v1/files/{id}` com o token da sessão (o `AuthInterceptor` junta-o).
Future<DownloadedFile> downloadFileBytes(
  ApiClient api,
  String fileId, {
  String? fallbackContentType,
}) async {
  final response = await api.document.downloadFile(id: fileId);
  final header = response.headers.value('content-type');
  final contentType = (header ?? fallbackContentType ?? '')
      .split(';')
      .first
      .trim()
      .toLowerCase();
  return DownloadedFile(
    bytes: response.data ?? Uint8List(0),
    contentType: contentType.isEmpty ? 'application/octet-stream' : contentType,
  );
}

/// Ficheiro por id, só em memória enquanto o ecrã o mostra.
final fileBytesProvider = FutureProvider.autoDispose
    .family<DownloadedFile, String>(
      (ref, fileId) => downloadFileBytes(ref.watch(apiClientProvider), fileId),
      retry: (_, _) => null,
    );
