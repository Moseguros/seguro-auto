import 'dart:io';
import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:image_picker/image_picker.dart';

/// Ficheiro escolhido pelo cliente. O conteúdo só se lê depois de verificar o
/// tamanho, e o temporário apaga-se com [discard] depois do envio.
class ChosenFile {
  const ChosenFile({
    required this.name,
    required this.size,
    required this.contentType,
    required this.read,
    this.discard = _nothing,
  });

  final String name;

  /// Tamanho em bytes.
  final int size;

  /// Tipo pelo nome (`image/jpeg`, `image/png`, `image/heic`, `application/pdf`).
  final String contentType;

  final Future<Uint8List> Function() read;

  /// Apaga a cópia temporária feita pelo seletor (se houver).
  final Future<void> Function() discard;

  static Future<void> _nothing() async {}
}

/// Tipo de um ficheiro pela extensão.
String contentTypeForName(String name) {
  final dot = name.lastIndexOf('.');
  final extension = dot < 0 ? '' : name.substring(dot + 1).toLowerCase();
  return switch (extension) {
    'jpg' || 'jpeg' => 'image/jpeg',
    'png' => 'image/png',
    'heic' || 'heif' => 'image/heic',
    'pdf' => 'application/pdf',
    _ => 'application/octet-stream',
  };
}

/// Escolher uma foto da galeria.
abstract interface class GalleryPicker {
  Future<ChosenFile?> pickImage();
}

/// Escolher um PDF nos ficheiros do telemóvel.
abstract interface class PdfPicker {
  Future<ChosenFile?> pickPdf();
}

/// `image_picker` (`ImageSource.gallery`), sem pedir os metadados ao sistema.
class DeviceGalleryPicker implements GalleryPicker {
  const DeviceGalleryPicker();

  @override
  Future<ChosenFile?> pickImage() async {
    final file = await ImagePicker().pickImage(
      source: ImageSource.gallery,
      requestFullMetadata: false,
    );
    if (file == null) return null;
    return ChosenFile(
      name: file.name,
      size: await file.length(),
      contentType: file.mimeType ?? contentTypeForName(file.name),
      read: file.readAsBytes,
      discard: () => _deleteQuietly(file.path),
    );
  }
}

/// `file_picker` (`FileType.custom`, só `pdf`).
class DevicePdfPicker implements PdfPicker {
  const DevicePdfPicker();

  @override
  Future<ChosenFile?> pickPdf() async {
    final file = await FilePicker.pickFile(
      type: FileType.custom,
      allowedExtensions: const ['pdf'],
    );
    if (file == null) return null;
    return ChosenFile(
      name: file.name,
      size: file.lengthSync() ?? await file.length() ?? 0,
      contentType: 'application/pdf',
      read: file.readAsBytes,
      discard: FilePicker.clearTemporaryFiles,
    );
  }
}

Future<void> _deleteQuietly(String path) async {
  try {
    final file = File(path);
    if (file.existsSync()) await file.delete();
  } on FileSystemException {
    // Um temporário que não se apaga não impede o envio.
  }
}

final galleryPickerProvider = Provider<GalleryPicker>(
  (ref) => const DeviceGalleryPicker(),
);

final pdfPickerProvider = Provider<PdfPicker>((ref) => const DevicePdfPicker());
