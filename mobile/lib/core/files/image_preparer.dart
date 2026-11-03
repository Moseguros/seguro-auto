import 'dart:math' as math;
import 'dart:typed_data';

import 'package:flutter_image_compress/flutter_image_compress.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

/// Tamanho máximo de um ficheiro enviado à API (S2-01).
const maxUploadBytes = 10 * 1024 * 1024;

/// O ficheiro tem mais de [maxUploadBytes]: mostra-se `errorFileTooLarge` e não se envia.
class FileTooLargeException implements Exception {
  const FileTooLargeException();
}

/// A imagem não se conseguiu ler nem converter (formato desconhecido).
class ImageNotReadableException implements Exception {
  const ImageNotReadableException();
}

/// Largura e altura em pixels.
typedef ImageSize = ({int width, int height});

/// Comprime para JPEG. Semântica do `flutter_image_compress`: a imagem reduz-se
/// mantendo a proporção até que a largura chegue a [minWidth] **ou** a altura a
/// [minHeight] (nunca aumenta).
abstract interface class ImageCompressor {
  Future<Uint8List> compressToJpeg(
    Uint8List bytes, {
    required int minWidth,
    required int minHeight,
    required int quality,
    required bool keepExif,
  });
}

class FlutterImageCompressor implements ImageCompressor {
  const FlutterImageCompressor();

  @override
  Future<Uint8List> compressToJpeg(
    Uint8List bytes, {
    required int minWidth,
    required int minHeight,
    required int quality,
    required bool keepExif,
  }) => FlutterImageCompress.compressWithList(
    bytes,
    minWidth: minWidth,
    minHeight: minHeight,
    quality: quality,
    keepExif: keepExif,
    format: CompressFormat.jpeg,
  );
}

final imageCompressorProvider = Provider<ImageCompressor>(
  (ref) => const FlutterImageCompressor(),
);

/// Prepara uma foto (câmara ou galeria) para envio: JPEG, lado maior ≤ 2400 px,
/// qualidade 85 e **sem metadados** (EXIF com a localização GPS, XMP, IPTC).
/// Converte HEIC para JPEG.
class ImagePreparer {
  const ImagePreparer(this._compressor);

  final ImageCompressor _compressor;

  static const maxSide = 2400;
  static const quality = 85;

  Future<Uint8List> prepare(Uint8List source) async {
    var current = source;
    // Com o tamanho conhecido basta uma passagem; um HEIC (tamanho desconhecido)
    // passa a JPEG na primeira e reduz-se na segunda, se ainda for grande.
    for (var pass = 0; pass < 2; pass++) {
      final target = targetSize(readImageSize(current));
      current = await _compressor.compressToJpeg(
        current,
        minWidth: target.width,
        minHeight: target.height,
        quality: quality,
        keepExif: false,
      );
      final size = readImageSize(current);
      if (size == null) throw const ImageNotReadableException();
      if (math.max(size.width, size.height) <= maxSide) break;
    }
    final result = stripJpegMetadata(current);
    if (result.length > maxUploadBytes) throw const FileTooLargeException();
    return result;
  }

  /// Parâmetros para o compressor: a mesma proporção, com o lado maior ≤ [maxSide].
  static ImageSize targetSize(ImageSize? size) {
    if (size == null) return (width: maxSide, height: maxSide);
    final longest = math.max(size.width, size.height);
    if (longest <= maxSide) return size;
    final factor = maxSide / longest;
    return (
      width: math.max(1, (size.width * factor).floor()),
      height: math.max(1, (size.height * factor).floor()),
    );
  }
}

final imagePreparerProvider = Provider<ImagePreparer>(
  (ref) => ImagePreparer(ref.watch(imageCompressorProvider)),
);

/// Tamanho de um JPEG ou PNG lido do cabeçalho; `null` noutros formatos.
ImageSize? readImageSize(Uint8List bytes) {
  if (_isPng(bytes) && bytes.length >= 24) {
    final data = ByteData.sublistView(bytes);
    return (width: data.getUint32(16), height: data.getUint32(20));
  }
  if (!_isJpeg(bytes)) return null;
  var offset = 2;
  while (offset + 4 <= bytes.length) {
    if (bytes[offset] != 0xFF) return null;
    final marker = bytes[offset + 1];
    if (marker == 0xFF) {
      offset += 1;
      continue;
    }
    if (_isStandalone(marker)) {
      offset += 2;
      continue;
    }
    final length = (bytes[offset + 2] << 8) | bytes[offset + 3];
    if (_isStartOfFrame(marker) && offset + 9 <= bytes.length) {
      final height = (bytes[offset + 5] << 8) | bytes[offset + 6];
      final width = (bytes[offset + 7] << 8) | bytes[offset + 8];
      return (width: width, height: height);
    }
    if (marker == 0xDA) return null;
    offset += 2 + length;
  }
  return null;
}

/// Retira de um JPEG os segmentos com metadados (APP1: EXIF e XMP; APP13: IPTC;
/// comentários). O compressor já o faz (`keepExif: false`); isto garante-o.
Uint8List stripJpegMetadata(Uint8List bytes) {
  if (!_isJpeg(bytes)) return bytes;
  final out = BytesBuilder(copy: false)..add(bytes.sublist(0, 2));
  var offset = 2;
  while (offset + 4 <= bytes.length) {
    if (bytes[offset] != 0xFF) break;
    final marker = bytes[offset + 1];
    if (_isStandalone(marker)) {
      out.add(bytes.sublist(offset, offset + 2));
      offset += 2;
      continue;
    }
    final length = (bytes[offset + 2] << 8) | bytes[offset + 3];
    final end = math.min(bytes.length, offset + 2 + length);
    if (marker == 0xDA) break;
    final isMetadata = marker == 0xE1 || marker == 0xED || marker == 0xFE;
    if (!isMetadata) out.add(bytes.sublist(offset, end));
    offset = end;
  }
  // Do início da imagem (SOS) até ao fim, sem alterações.
  if (offset < bytes.length) out.add(bytes.sublist(offset));
  return out.toBytes();
}

/// `true` se o JPEG tiver um segmento EXIF (APP1 com `Exif\0\0`).
bool hasExif(Uint8List bytes) {
  if (!_isJpeg(bytes)) return false;
  var offset = 2;
  while (offset + 4 <= bytes.length) {
    if (bytes[offset] != 0xFF) return false;
    final marker = bytes[offset + 1];
    if (_isStandalone(marker)) {
      offset += 2;
      continue;
    }
    if (marker == 0xDA) return false;
    final length = (bytes[offset + 2] << 8) | bytes[offset + 3];
    if (marker == 0xE1 &&
        offset + 8 <= bytes.length &&
        String.fromCharCodes(bytes.sublist(offset + 4, offset + 8)) == 'Exif') {
      return true;
    }
    offset += 2 + length;
  }
  return false;
}

bool _isJpeg(Uint8List bytes) =>
    bytes.length > 3 && bytes[0] == 0xFF && bytes[1] == 0xD8;

bool _isPng(Uint8List bytes) =>
    bytes.length > 8 &&
    bytes[0] == 0x89 &&
    bytes[1] == 0x50 &&
    bytes[2] == 0x4E &&
    bytes[3] == 0x47;

bool _isStandalone(int marker) =>
    marker == 0x01 || (marker >= 0xD0 && marker <= 0xD9);

bool _isStartOfFrame(int marker) =>
    marker >= 0xC0 &&
    marker <= 0xCF &&
    marker != 0xC4 &&
    marker != 0xC8 &&
    marker != 0xCC;
