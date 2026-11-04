import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/api/api_client.dart';
import '../../../core/errors/problem.dart';
import '../../../core/files/file_download.dart';
import '../../../core/files/file_viewer.dart';
import '../../../l10n/app_localizations.dart';

/// Descarrega um documento do produto (`GET /api/v1/files/{id}`) e abre-o no
/// visualizador do sistema; durante a descarga, o botão mostra um indicador.
class QuoteDocumentButton extends ConsumerStatefulWidget {
  const QuoteDocumentButton({
    super.key,
    required this.fileId,
    required this.label,
    required this.name,
  });

  final String fileId;
  final String label;

  /// Nome do ficheiro temporário, sem extensão.
  final String name;

  @override
  ConsumerState<QuoteDocumentButton> createState() =>
      _QuoteDocumentButtonState();
}

class _QuoteDocumentButtonState extends ConsumerState<QuoteDocumentButton> {
  bool _loading = false;

  Future<void> _open() async {
    final l10n = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    setState(() => _loading = true);
    try {
      final file = await downloadFileBytes(
        ref.read(apiClientProvider),
        widget.fileId,
        fallbackContentType: 'application/pdf',
      );
      final opened = await ref
          .read(fileOpenerProvider)
          .open(
            file.bytes,
            contentType: file.contentType,
            name: '${widget.name}.${extensionFor(file.contentType)}',
          );
      if (!opened) {
        messenger
          ..hideCurrentSnackBar()
          ..showSnackBar(SnackBar(content: Text(l10n.errorFileNotFound)));
      }
    } on Object catch (error) {
      final problem = Problem.from(error);
      messenger
        ..hideCurrentSnackBar()
        ..showSnackBar(
          SnackBar(
            content: Text(
              problem.status == 404 || problem.status == 403
                  ? l10n.errorFileNotFound
                  : problem.message(l10n),
            ),
          ),
        );
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return OutlinedButton.icon(
      key: Key('quote-document-${widget.fileId}'),
      onPressed: _loading ? null : _open,
      icon: _loading
          ? const SizedBox.square(
              dimension: 16,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : const Icon(Icons.picture_as_pdf_outlined),
      label: Text(widget.label),
    );
  }
}
