import 'dart:typed_data';

import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/misc.dart' show Override;
import 'package:image/image.dart' as img;
import 'package:moseguros/core/files/file_pickers.dart';
import 'package:moseguros/core/files/file_viewer.dart';
import 'package:moseguros/core/files/image_preparer.dart';
import 'package:moseguros/core/camera/camera_service.dart';

import 'mock_api.dart';

/// PNG de 1×1 (imagem válida para `Image.memory` nos testes).
final Uint8List tinyPng = Uint8List.fromList([
  0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, //
  0x49, 0x48, 0x44, 0x52, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
  0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, 0xC4, 0x89, 0x00, 0x00, 0x00,
  0x0D, 0x49, 0x44, 0x41, 0x54, 0x78, 0x9C, 0x63, 0xF8, 0xCF, 0xC0, 0xF0,
  0x1F, 0x00, 0x05, 0x00, 0x01, 0xFF, 0x89, 0x99, 0x3D, 0x1D, 0x00, 0x00,
  0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE, 0x42, 0x60, 0x82,
]);

/// JPEG pequeno (o "resultado" do compressor falso).
final Uint8List tinyJpeg = img.encodeJpg(img.Image(width: 4, height: 3));

/// Câmara falsa: a pré-visualização é um rectângulo e a foto é [photo].
class FakeCameraService implements CameraService {
  FakeCameraService({
    Uint8List? photo,
    this.lens = CameraLens.back,
    this.resolution = CameraResolution.veryHigh,
  }) : photo = photo ?? tinyPng;

  final Uint8List photo;
  final CameraLens lens;
  final CameraResolution resolution;
  int captures = 0;
  bool initialized = false;
  bool disposed = false;
  final List<FlashMode> flashModes = [];

  @override
  Future<void> initialize() async => initialized = true;

  @override
  Widget preview() => const ColoredBox(
    key: Key('fake-camera-preview'),
    color: Color(0xFF333333),
  );

  @override
  Future<Uint8List> capture() async {
    captures++;
    return photo;
  }

  @override
  Future<void> setFlash(FlashMode mode) async => flashModes.add(mode);

  @override
  Future<void> dispose() async => disposed = true;
}

class FakeCameraPermission implements CameraPermission {
  FakeCameraPermission({this.granted = true});

  final bool granted;
  int settingsOpened = 0;

  @override
  Future<bool> request() async => granted;

  @override
  Future<void> openSettings() async => settingsOpened++;
}

/// Compressor falso: regista as chamadas e devolve [result].
class RecordingCompressor implements ImageCompressor {
  RecordingCompressor({Uint8List? result}) : result = result ?? tinyJpeg;

  final Uint8List result;
  final List<({int minWidth, int minHeight, int quality, bool keepExif})>
  calls = [];

  /// Chamado em cada compressão (para verificar a ordem em relação ao envio).
  void Function()? onCall;

  @override
  Future<Uint8List> compressToJpeg(
    Uint8List bytes, {
    required int minWidth,
    required int minHeight,
    required int quality,
    required bool keepExif,
  }) async {
    onCall?.call();
    calls.add((
      minWidth: minWidth,
      minHeight: minHeight,
      quality: quality,
      keepExif: keepExif,
    ));
    return result;
  }
}

class FakeGalleryPicker implements GalleryPicker {
  FakeGalleryPicker([this.file]);

  ChosenFile? file;
  int picks = 0;

  @override
  Future<ChosenFile?> pickImage() async {
    picks++;
    return file;
  }
}

class FakePdfPicker implements PdfPicker {
  FakePdfPicker([this.file]);

  ChosenFile? file;
  int reads = 0;
  int discards = 0;

  @override
  Future<ChosenFile?> pickPdf() async {
    final file = this.file;
    if (file == null) return null;
    return ChosenFile(
      name: file.name,
      size: file.size,
      contentType: file.contentType,
      read: () {
        reads++;
        return file.read();
      },
      discard: () async => discards++,
    );
  }
}

class FakeFileOpener implements FileOpener {
  final List<({String contentType, String name, int length})> opened = [];

  @override
  Future<bool> open(
    Uint8List bytes, {
    required String contentType,
    required String name,
  }) async {
    opened.add((contentType: contentType, name: name, length: bytes.length));
    return true;
  }
}

/// Dispositivo falso: câmara, permissão, seletores, compressor e abertura de ficheiros.
class FakeDevice {
  FakeDevice({bool cameraGranted = true})
    : permission = FakeCameraPermission(granted: cameraGranted);

  final FakeCameraPermission permission;
  final cameras = <FakeCameraService>[];
  final compressor = RecordingCompressor();
  final gallery = FakeGalleryPicker();
  final pdf = FakePdfPicker();
  final opener = FakeFileOpener();

  List<Override> get overrides => [
    cameraServiceProvider.overrideWithValue(({
      CameraLens lens = CameraLens.back,
      CameraResolution resolution = CameraResolution.veryHigh,
      FlashMode initialFlash = FlashMode.off,
    }) {
      final camera = FakeCameraService(lens: lens, resolution: resolution);
      cameras.add(camera);
      return camera;
    }),
    cameraPermissionProvider.overrideWithValue(permission),
    imageCompressorProvider.overrideWithValue(compressor),
    galleryPickerProvider.overrideWithValue(gallery),
    pdfPickerProvider.overrideWithValue(pdf),
    fileOpenerProvider.overrideWithValue(opener),
  ];
}

const vehicleId = '01930000-0000-7000-8000-000000000101';
const driverId = '01930000-0000-7000-8000-000000000201';

/// Respostas da API de veículos e condutores.
abstract final class VehicleFixtures {
  static Map<String, Object?> vehicle({
    String id = vehicleId,
    String plate = 'AAA-100-MC',
    String category = 'LIGHT_PASSENGER',
    int? engineCc = 1598,
    int seats = 5,
    int? grossWeightKg,
    bool inatroVerified = true,
    Map<String, Object?>? registrationDoc,
    List<Map<String, Object?>> drivers = const [],
    int version = 0,
    int? commercialValue = 85000000,
  }) => {
    'id': id,
    'plate': plate,
    'chassisNumber': 'JTDBR32E720012345',
    'make': 'Toyota',
    'model': 'Corolla',
    'year': 2018,
    'engineCc': ?engineCc,
    'category': category,
    'seats': seats,
    'grossWeightKg': ?grossWeightKg,
    'usage': 'PRIVATE',
    'provinceOfCirculation': 'MAPUTO_CITY',
    if (commercialValue != null)
      'commercialValue': {'amount': commercialValue, 'currency': 'MZN'},
    'hasTracker': false,
    'inatroVerified': inatroVerified,
    'registrationDoc': ?registrationDoc,
    'drivers': drivers,
    'version': version,
    'createdAt': '2026-12-03T10:00:00Z',
    'updatedAt': '2026-12-03T10:15:00Z',
  };

  static Map<String, Object?> registrationDoc({
    String fileId = '01930000-0000-7000-8000-000000000301',
    String contentType = 'image/jpeg',
    int sizeBytes = 812345,
  }) => {
    'fileId': fileId,
    'contentType': contentType,
    'sizeBytes': sizeBytes,
    'uploadedAt': '2026-12-03T10:15:00Z',
  };

  static Map<String, Object?> page(List<Map<String, Object?>> content) => {
    'content': content,
    'page': 0,
    'size': 50,
    'totalElements': content.length,
  };

  static Map<String, Object?> driver({
    String id = driverId,
    String fullName = 'Ana Maria Mussa',
    bool isOwner = false,
    List<String> vehicleIds = const [],
    List<String> categories = const ['B'],
    int version = 0,
  }) => {
    'id': id,
    'fullName': fullName,
    'birthDate': '1990-05-10',
    'licenceNumberMasked': '****4567',
    'licenceCategories': categories,
    'licenceIssueDate': '2010-06-01',
    'isOwner': isOwner,
    'vehicleIds': vehicleIds,
    'version': version,
    'createdAt': '2026-12-03T10:00:00Z',
    'updatedAt': '2026-12-03T10:00:00Z',
  };

  static Map<String, Object?> registry({
    String plate = 'AAA-100-MC',
    String category = 'LIGHT_PASSENGER',
    int? engineCc = 1598,
    int seats = 5,
    int? grossWeightKg,
  }) => {
    'plate': plate,
    'chassisNumber': 'JTDBR32E720012345',
    'make': 'Toyota',
    'model': 'Corolla',
    'year': 2018,
    'engineCc': ?engineCc,
    'category': category,
    'seats': seats,
    'grossWeightKg': ?grossWeightKg,
    'source': 'MOCK',
  };

  static Map<String, Object?> references(String domain) => {
    'values': switch (domain) {
      'VEHICLE_MAKE' => [
        {'code': 'TOYOTA', 'labelPt': 'Toyota', 'labelEn': 'Toyota'},
        {'code': 'NISSAN', 'labelPt': 'Nissan', 'labelEn': 'Nissan'},
      ],
      'VEHICLE_MODEL' => [
        {'code': 'COROLLA', 'labelPt': 'Corolla', 'parentCode': 'TOYOTA'},
        {'code': 'HILUX', 'labelPt': 'Hilux', 'parentCode': 'TOYOTA'},
        {'code': 'HARDBODY', 'labelPt': 'Hardbody', 'parentCode': 'NISSAN'},
      ],
      'LICENCE_CATEGORY' => [
        {
          'code': 'A',
          'labelPt': 'A — Motociclos',
          'labelEn': 'A — Motorcycles',
        },
        {
          'code': 'B',
          'labelPt': 'B — Ligeiros',
          'labelEn': 'B — Light vehicles',
        },
        {
          'code': 'C',
          'labelPt': 'C — Pesados',
          'labelEn': 'C — Heavy vehicles',
        },
      ],
      _ => <Object>[],
    },
  };

  static const plateFormats = {
    'formats': [
      {'code': 'MZ_2005', 'label': 'Actual', 'example': 'AAA-100-MC'},
      {'code': 'MZ_OLD', 'label': 'Antigo', 'example': 'MLA-12-34'},
    ],
  };
}

/// Sessão do cliente e listas de referência (o que os ecrãs de veículos lêem).
void stubCustomerSession(MockApi api) {
  api
    ..on('POST', '/api/v1/auth/refresh', 200, Fixtures.tokens())
    ..on('GET', '/api/v1/me', 200, Fixtures.me())
    ..on(
      'GET',
      '/api/v1/me/profile',
      200,
      Fixtures.profile(
        complete: true,
        fullName: 'Ana Maria Machava',
        birthDate: '1990-05-10',
        province: 'MAPUTO_CITY',
      ),
    )
    ..on('GET', '/api/v1/me/contacts', 200, Fixtures.contacts())
    ..on('GET', '/api/v1/meta/version', 200, {'version': '1.0.0'})
    ..on('GET', '/api/v1/plate-formats', 200, VehicleFixtures.plateFormats);
  for (final domain in ['VEHICLE_MAKE', 'VEHICLE_MODEL', 'LICENCE_CATEGORY']) {
    api.on(
      'GET',
      '/api/v1/reference-domains/$domain/values',
      200,
      VehicleFixtures.references(domain),
    );
  }
}
