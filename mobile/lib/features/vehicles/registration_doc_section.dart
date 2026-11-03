import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/files/file_download.dart';
import '../../core/files/file_pickers.dart';
import '../../core/files/file_viewer.dart';
import '../../core/files/image_preparer.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'registration_doc_source_sheet.dart';
import 'vehicle_detail_screen.dart';
import 'vehicles_controller.dart';

/// Nome do ficheiro enviado com uma foto do livrete (sempre JPEG depois de preparada).
const registrationPhotoName = 'livrete.jpg';

/// Mensagem de um erro ao escolher, preparar ou enviar o livrete.
String registrationDocErrorMessage(AppLocalizations l10n, Object error) =>
    switch (error) {
      FileTooLargeException() => l10n.errorFileTooLarge,
      ImageNotReadableException() => l10n.errorFileTypeNotAllowed,
      _ => Problem.from(error).message(l10n),
    };

/// Foto da galeria já preparada (JPEG, sem EXIF, ≤ 2400 px); `null` se o
/// cliente não escolheu nenhuma. O temporário do seletor apaga-se.
Future<Uint8List?> pickGalleryPhoto(WidgetRef ref) async {
  final file = await ref.read(galleryPickerProvider).pickImage();
  if (file == null) return null;
  try {
    final bytes = await file.read();
    return await ref.read(imagePreparerProvider).prepare(bytes);
  } finally {
    await file.discard();
  }
}

/// Tamanho de um ficheiro para mostrar (ex.: `812 KB`, `1,2 MB`).
String formatFileSize(int bytes, String languageCode) {
  final locale = languageCode == 'en' ? 'en' : 'pt_PT';
  if (bytes < 1024 * 1024) {
    return '${NumberFormat.decimalPattern(locale).format((bytes / 1024).ceil())} KB';
  }
  return '${NumberFormat.decimalPatternDigits(locale: locale, decimalDigits: 1).format(bytes / (1024 * 1024))} MB';
}

/// Secção "Livrete" do detalhe do veículo.
class RegistrationDocSection extends ConsumerStatefulWidget {
  const RegistrationDocSection({super.key, required this.vehicle});

  final VehicleResponse vehicle;

  @override
  ConsumerState<RegistrationDocSection> createState() =>
      _RegistrationDocSectionState();
}

class _RegistrationDocSectionState
    extends ConsumerState<RegistrationDocSection> {
  bool _busy = false;

  /// Fracção enviada (0–1) durante um envio; `null` sem envio.
  double? _progress;
  String? _error;

  VehicleResponse get _vehicle => widget.vehicle;

  void _saved() {
    if (!mounted) return;
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(
        SnackBar(content: Text(AppLocalizations.of(context).vehicleDocSaved)),
      );
  }

  Future<void> _choose() async {
    final source = await showRegistrationDocSourceSheet(context);
    if (source == null || !mounted) return;
    setState(() => _error = null);
    switch (source) {
      case RegistrationDocSource.camera:
        final uploaded = await context.push<bool>(
          '/vehicles/${_vehicle.id}/registration-doc/camera',
        );
        if (uploaded == true) _saved();
      case RegistrationDocSource.gallery:
        await _run(() async {
          final photo = await pickGalleryPhoto(ref);
          if (photo == null) return false;
          await _upload(photo, registrationPhotoName, 'image/jpeg');
          return true;
        });
      case RegistrationDocSource.pdf:
        await _run(() async {
          final file = await ref.read(pdfPickerProvider).pickPdf();
          if (file == null) return false;
          try {
            // Verificado antes de ler o ficheiro: um PDF grande não chega à API.
            if (file.size > maxUploadBytes) throw const FileTooLargeException();
            final bytes = await file.read();
            await _upload(bytes, file.name, 'application/pdf');
            return true;
          } finally {
            await file.discard();
          }
        });
    }
  }

  Future<void> _run(Future<bool> Function() action) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      if (await action()) _saved();
    } catch (error) {
      if (mounted) {
        setState(() => _error = registrationDocErrorMessage(l10n, error));
      }
    } finally {
      if (mounted) {
        setState(() {
          _busy = false;
          _progress = null;
        });
      }
    }
  }

  Future<void> _upload(Uint8List bytes, String name, String contentType) async {
    setState(() => _progress = 0);
    await ref
        .read(vehicleRepositoryProvider)
        .uploadRegistrationDoc(
          _vehicle.id,
          bytes,
          filename: name,
          contentType: contentType,
          onProgress: (sent, total) {
            if (mounted && total > 0) setState(() => _progress = sent / total);
          },
        );
  }

  Future<void> _open(RegistrationDocResponse doc) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      final file = await ref.read(fileBytesProvider(doc.fileId).future);
      final contentType = file.contentType == 'application/octet-stream'
          ? doc.contentType
          : file.contentType;
      final opened = await ref
          .read(fileOpenerProvider)
          .open(
            file.bytes,
            contentType: contentType,
            name: 'livrete.${extensionFor(contentType)}',
          );
      if (!opened && mounted) setState(() => _error = l10n.fileOpenFailed);
    } catch (error) {
      if (mounted) {
        setState(() => _error = registrationDocErrorMessage(l10n, error));
      }
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final doc = _vehicle.registrationDoc;
    final progress = _progress;
    return DetailSection(
      title: l10n.vehicleSectionDoc,
      children: [
        if (doc == null)
          Text(l10n.vehicleDocNone, key: const Key('doc-none'))
        else if (doc.contentType == 'image/jpeg' ||
            doc.contentType == 'image/png')
          _Thumbnail(doc: doc)
        else
          _FileRow(doc: doc, onOpen: _busy ? null : () => _open(doc)),
        if (progress != null) ...[
          const SizedBox(height: 12),
          LinearProgressIndicator(
            key: const Key('doc-upload-progress'),
            value: progress,
          ),
          const SizedBox(height: 4),
          Text(l10n.vehicleDocUploading),
        ],
        if (_error != null) ...[
          const SizedBox(height: 12),
          MessageBanner(_error!, key: const Key('doc-error')),
        ],
        const SizedBox(height: 12),
        Align(
          alignment: AlignmentDirectional.centerStart,
          child: FilledButton.icon(
            key: const Key('doc-upload'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: _busy ? null : _choose,
            icon: const Icon(Icons.upload_file),
            label: Text(
              doc == null ? l10n.vehicleDocUpload : l10n.vehicleDocReplace,
            ),
          ),
        ),
      ],
    );
  }
}

/// Miniatura de um JPEG/PNG; um toque abre-o em ecrã inteiro.
class _Thumbnail extends ConsumerWidget {
  const _Thumbnail({required this.doc});

  final RegistrationDocResponse doc;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final file = ref.watch(fileBytesProvider(doc.fileId));
    final value = file.value;
    if (value != null) {
      return Semantics(
        button: true,
        label: l10n.vehicleDocOpen,
        child: InkWell(
          key: const Key('doc-thumbnail'),
          onTap: () => showImageViewer(
            context,
            value.bytes,
            title: l10n.vehicleSectionDoc,
          ),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: Image.memory(
              value.bytes,
              height: 200,
              fit: BoxFit.cover,
              gaplessPlayback: true,
            ),
          ),
        ),
      );
    }
    if (file.hasError) {
      return MessageBanner(Problem.from(file.error!).message(l10n));
    }
    return const SizedBox(
      height: 200,
      child: Center(child: CircularProgressIndicator()),
    );
  }
}

/// PDF ou HEIC (sem pré-visualização): ícone, tipo, tamanho e "Abrir".
class _FileRow extends StatelessWidget {
  const _FileRow({required this.doc, required this.onOpen});

  final RegistrationDocResponse doc;
  final VoidCallback? onOpen;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final language = Localizations.localeOf(context).languageCode;
    final isPdf = doc.contentType == 'application/pdf';
    return Row(
      children: [
        Icon(
          isPdf ? Icons.picture_as_pdf_outlined : Icons.image_outlined,
          size: 40,
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Text(
            '${isPdf ? 'PDF' : 'HEIC'} · ${formatFileSize(doc.sizeBytes, language)}',
          ),
        ),
        TextButton(
          key: const Key('doc-open'),
          onPressed: onOpen,
          child: Text(l10n.vehicleDocOpen),
        ),
      ],
    );
  }
}
