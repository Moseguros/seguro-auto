import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:open_filex/open_filex.dart';
import 'package:path_provider/path_provider.dart';

import '../../l10n/app_localizations.dart';

/// Mostra uma imagem (JPEG/PNG) em ecrã inteiro, com zoom.
Future<void> showImageViewer(
  BuildContext context,
  Uint8List bytes, {
  required String title,
}) => Navigator.of(context, rootNavigator: true).push(
  MaterialPageRoute<void>(
    fullscreenDialog: true,
    builder: (context) => ImageViewerScreen(bytes: bytes, title: title),
  ),
);

class ImageViewerScreen extends StatelessWidget {
  const ImageViewerScreen({
    super.key,
    required this.bytes,
    required this.title,
  });

  final Uint8List bytes;
  final String title;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        title: Text(title),
        backgroundColor: Colors.black,
        foregroundColor: Colors.white,
        leading: IconButton(
          tooltip: AppLocalizations.of(context).close,
          icon: const Icon(Icons.close),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: InteractiveViewer(
        key: const Key('image-viewer'),
        minScale: 1,
        maxScale: 6,
        child: Center(child: Image.memory(bytes, fit: BoxFit.contain)),
      ),
    );
  }
}

/// Abre um ficheiro (PDF, HEIC…) noutra app do telemóvel.
abstract interface class FileOpener {
  /// `false` quando nenhuma app o consegue abrir.
  Future<bool> open(
    Uint8List bytes, {
    required String contentType,
    required String name,
  });
}

/// Escreve o ficheiro num temporário da app e abre-o com `open_filex`.
///
/// Só fica um temporário de cada vez: o anterior apaga-se ao abrir outro (a app
/// que o mostra ainda pode estar a lê-lo, por isso não se apaga logo).
class DeviceFileOpener implements FileOpener {
  const DeviceFileOpener();

  @override
  Future<bool> open(
    Uint8List bytes, {
    required String contentType,
    required String name,
  }) async {
    final temp = await getTemporaryDirectory();
    final dir = Directory('${temp.path}${Platform.pathSeparator}opened');
    if (dir.existsSync()) await dir.delete(recursive: true);
    await dir.create(recursive: true);
    final file = File('${dir.path}${Platform.pathSeparator}$name');
    await file.writeAsBytes(bytes, flush: true);
    final result = await OpenFilex.open(file.path, type: contentType);
    return result.type == ResultType.done;
  }
}

final fileOpenerProvider = Provider<FileOpener>(
  (ref) => const DeviceFileOpener(),
);

/// Extensão do ficheiro para um tipo aceite pela API.
String extensionFor(String contentType) => switch (contentType) {
  'image/jpeg' => 'jpg',
  'image/png' => 'png',
  'image/heic' || 'image/heif' => 'heic',
  'application/pdf' => 'pdf',
  _ => 'bin',
};
