import 'dart:math' as math;
import 'dart:typed_data';

import 'package:flutter_test/flutter_test.dart';
import 'package:image/image.dart' as img;
import 'package:moseguros/core/files/image_preparer.dart';

import '../../support/vehicles.dart';

/// Compressor com a mesma semântica do `flutter_image_compress` (que não corre
/// em `flutter test`), feito com o pacote `image`: reduz mantendo a proporção até
/// a largura chegar a `minWidth` ou a altura a `minHeight`.
class ImageLibCompressor implements ImageCompressor {
  ImageLibCompressor({this.alwaysKeepExif = false});

  /// Simula um compressor que ignora `keepExif: false`.
  final bool alwaysKeepExif;
  final List<bool> keepExifCalls = [];

  @override
  Future<Uint8List> compressToJpeg(
    Uint8List bytes, {
    required int minWidth,
    required int minHeight,
    required int quality,
    required bool keepExif,
  }) async {
    keepExifCalls.add(keepExif);
    final source = img.decodeImage(bytes)!;
    final scale = math.max(
      1.0,
      math.min(source.width / minWidth, source.height / minHeight),
    );
    final resized = img.copyResize(
      source,
      width: (source.width / scale).round(),
      height: (source.height / scale).round(),
    );
    resized.exif = keepExif || alwaysKeepExif
        ? img.ExifData.from(source.exif)
        : img.ExifData();
    return img.encodeJpg(resized, quality: quality);
  }
}

/// Foto de 4000×3000 com a localização GPS no EXIF.
Uint8List photoWithGps() {
  final image = img.Image(width: 4000, height: 3000);
  image.exif.gpsIfd['GPSLatitudeRef'] = img.IfdValueAscii('S');
  image.exif.gpsIfd['GPSLatitude'] = img.IfdValueRational(25, 1);
  image.exif.gpsIfd['GPSLongitudeRef'] = img.IfdValueAscii('E');
  image.exif.gpsIfd['GPSLongitude'] = img.IfdValueRational(32, 1);
  return img.encodeJpg(image, quality: 90);
}

void main() {
  late Uint8List photo;

  setUpAll(() => photo = photoWithGps());

  test('a foto de teste tem EXIF com GPS e 4000×3000', () {
    expect(hasExif(photo), isTrue);
    expect(img.decodeJpg(photo)!.exif.gpsIfd.isEmpty, isFalse);
    expect(readImageSize(photo), (width: 4000, height: 3000));
  });

  test(
    'o resultado não tem EXIF e o lado maior é ≤ 2400 px (4000×3000 com GPS)',
    () async {
      final compressor = ImageLibCompressor();
      final result = await ImagePreparer(compressor).prepare(photo);

      expect(compressor.keepExifCalls, everyElement(isFalse));
      expect(hasExif(result), isFalse);
      expect(img.decodeJpg(result)!.exif.isEmpty, isTrue);
      final size = readImageSize(result)!;
      expect(math.max(size.width, size.height), lessThanOrEqualTo(2400));
      expect(size, (width: 2400, height: 1800));
    },
    timeout: const Timeout(Duration(minutes: 2)),
  );

  test(
    'mesmo que o compressor deixe o EXIF, o preparador retira-o',
    () async {
      final result = await ImagePreparer(
        ImageLibCompressor(alwaysKeepExif: true),
      ).prepare(photo);

      expect(hasExif(result), isFalse);
      expect(img.decodeJpg(result)!.exif.gpsIfd.isEmpty, isTrue);
    },
    timeout: const Timeout(Duration(minutes: 2)),
  );

  test('parâmetros do compressor: proporção mantida e qualidade 85', () async {
    final compressor = RecordingCompressor();
    await ImagePreparer(compressor).prepare(photo);
    expect(compressor.calls.single, (
      minWidth: 2400,
      minHeight: 1800,
      quality: 85,
      keepExif: false,
    ));
    expect(ImagePreparer.targetSize((width: 3000, height: 4000)), (
      width: 1800,
      height: 2400,
    ));
    expect(ImagePreparer.targetSize((width: 1200, height: 900)), (
      width: 1200,
      height: 900,
    ));
  });

  test(
    'formato desconhecido (HEIC): converte e volta a reduzir se preciso',
    () async {
      final big = img.encodeJpg(img.Image(width: 3000, height: 2000));
      final compressor = _SequenceCompressor([big, tinyJpeg]);
      final result = await ImagePreparer(
        compressor,
      ).prepare(Uint8List.fromList([0, 0, 0, 24, 0x66, 0x74, 0x79, 0x70]));
      expect(compressor.calls, hasLength(2));
      expect(compressor.calls.first, (width: 2400, height: 2400));
      expect(compressor.calls.last, (width: 2400, height: 1600));
      expect(result, tinyJpeg);
    },
  );

  test('resultado com mais de 10 MB → FileTooLargeException', () async {
    // Um JPEG pequeno seguido de bytes até passar dos 10 MB.
    final huge = Uint8List(maxUploadBytes + 1)..setAll(0, tinyJpeg);
    final preparer = ImagePreparer(RecordingCompressor(result: huge));
    expect(
      () => preparer.prepare(photo),
      throwsA(isA<FileTooLargeException>()),
    );
  });
}

class _SequenceCompressor implements ImageCompressor {
  _SequenceCompressor(this.results);

  final List<Uint8List> results;
  final List<({int width, int height})> calls = [];

  @override
  Future<Uint8List> compressToJpeg(
    Uint8List bytes, {
    required int minWidth,
    required int minHeight,
    required int quality,
    required bool keepExif,
  }) async {
    calls.add((width: minWidth, height: minHeight));
    return results[calls.length - 1];
  }
}
