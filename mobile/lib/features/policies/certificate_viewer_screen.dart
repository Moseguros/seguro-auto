import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';
import 'package:pdfx/pdfx.dart';

import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'document_cache.dart';
import 'policy_labels.dart';
import 'policy_repository.dart';

/// Documento de uma apólice já no telemóvel (da cache ou descarregado).
class OpenedDocument {
  const OpenedDocument({required this.document, required this.file});

  final PolicyDocument document;
  final File file;
}

/// O documento [fileId] (ou o certificado) da apólice, a partir da cache ou
/// descarregado e guardado.
final openedDocumentProvider = FutureProvider.autoDispose
    .family<OpenedDocument, ({String policyId, String? fileId})>((
      ref,
      key,
    ) async {
      final entry = await ref
          .read(policyRepositoryProvider)
          .cachedOne(key.policyId);
      final detail = entry?.detail;
      if (detail == null) throw StateError('policy not cached');
      final document = key.fileId == null
          ? certificateDocument(detail)
          : detail.documents
                .where((document) => document.fileId == key.fileId)
                .firstOrNull;
      if (document == null || document.fileId == null) {
        throw StateError('document not available');
      }
      final file = await ref
          .read(documentCacheProvider)
          .open(policyId: key.policyId, document: document);
      return OpenedDocument(document: document, file: file);
    }, retry: (_, _) => null);

/// `/policies/:id/certificate` (`?fileId=`): o PDF do certificado (ou de outro
/// documento da apólice) com `pdfx`, em ecrã inteiro.
class CertificateViewerScreen extends ConsumerWidget {
  const CertificateViewerScreen({
    super.key,
    required this.policyId,
    this.fileId,
  });

  final String policyId;
  final String? fileId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final provider = openedDocumentProvider((
      policyId: policyId,
      fileId: fileId,
    ));
    final opened = ref.watch(provider);
    final document = opened.value?.document;

    final Widget body;
    if (opened.value case final value?) {
      body = _PdfView(key: ValueKey(value.file.path), file: value.file);
    } else if (opened.hasError) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(l10n.policyDocumentOpenFailed),
          const SizedBox(height: 16),
          OutlinedButton(
            style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: () => ref.invalidate(provider),
            child: Text(l10n.retry),
          ),
        ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }

    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false,
        title: Text(
          document == null
              ? l10n.documentTypeCertificate
              : documentTypeLabel(l10n, document.type),
        ),
        actions: [
          IconButton(
            key: const Key('certificate-close'),
            tooltip: l10n.close,
            icon: const Icon(Icons.close),
            onPressed: () => context.canPop()
                ? context.pop()
                : context.go('/policies/$policyId'),
          ),
        ],
      ),
      body: SafeArea(child: body),
    );
  }
}

class _PdfView extends StatefulWidget {
  const _PdfView({super.key, required this.file});

  final File file;

  @override
  State<_PdfView> createState() => _PdfViewState();
}

class _PdfViewState extends State<_PdfView> {
  late final PdfControllerPinch _controller = PdfControllerPinch(
    document: PdfDocument.openFile(widget.file.path),
  );

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) =>
      PdfViewPinch(key: const Key('certificate-pdf'), controller: _controller);
}
