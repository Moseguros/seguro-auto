import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/files/file_pickers.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/core/camera/camera_service.dart';
import 'package:moseguros/features/vehicles/camera/document_frame_overlay.dart';

import '../../support/mock_api.dart';
import '../../support/vehicles.dart';

const _vehiclePath = '/api/v1/me/vehicles/$vehicleId';
const _uploadPath = '$_vehiclePath/registration-doc';
const _fileId = '01930000-0000-7000-8000-000000000301';

/// Tipo e nome do ficheiro enviado num pedido multipart.
({String? contentType, String filename}) _sentFile(RequestOptions request) {
  final file = (request.data as FormData).files.single;
  expect(file.key, 'file');
  return (
    contentType: file.value.contentType?.mimeType,
    filename: file.value.filename ?? '',
  );
}

void main() {
  late MockApi api;
  late FakeDevice device;

  setUp(() {
    api = MockApi();
    device = FakeDevice();
    stubCustomerSession(api);
    api
      ..on('GET', '/api/v1/me/vehicles', 200, VehicleFixtures.page([]))
      ..on('GET', '/api/v1/me/drivers', 200, VehicleFixtures.page([]))
      ..on('GET', _vehiclePath, 200, VehicleFixtures.vehicle());
  });

  /// Depois do envio, a API devolve o veículo com o livrete [contentType].
  void stubUpload([String contentType = 'image/jpeg']) {
    final withDoc = VehicleFixtures.vehicle(
      registrationDoc: VehicleFixtures.registrationDoc(
        contentType: contentType,
      ),
      version: 1,
    );
    api.on('POST', _uploadPath, 200, (RequestOptions request) {
      api.on('GET', _vehiclePath, 200, withDoc);
      return withDoc;
    });
    api.onBytes('/api/v1/files/$_fileId', tinyPng, contentType);
  }

  Future<void> openDetail(WidgetTester tester) async {
    final container = await pumpMoSeguros(
      tester,
      api,
      refreshToken: 'refresh-0',
      overrides: device.overrides,
    );
    container.read(routerProvider).go('/vehicles/$vehicleId');
    await tester.pumpAndSettle();
  }

  Future<void> scrollTo(WidgetTester tester, Finder finder) async {
    await tester.scrollUntilVisible(
      finder,
      300,
      scrollable: find.byType(Scrollable).last,
    );
    await tester.pumpAndSettle();
  }

  Future<void> chooseSource(WidgetTester tester, String source) async {
    await scrollTo(tester, find.byKey(const Key('doc-upload')));
    await tester.tap(find.byKey(const Key('doc-upload')));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(Key('doc-source-$source')));
    await tester.pumpAndSettle();
  }

  group('câmara guiada', () {
    testWidgets('a folha tem as três origens', (tester) async {
      await openDetail(tester);
      await scrollTo(tester, find.byKey(const Key('doc-upload')));
      expect(find.text('Enviar livrete'), findsOneWidget);
      await tester.tap(find.byKey(const Key('doc-upload')));
      await tester.pumpAndSettle();

      expect(find.text('Tirar foto'), findsOneWidget);
      expect(find.text('Escolher da galeria'), findsOneWidget);
      expect(find.text('Escolher PDF'), findsOneWidget);
    });

    testWidgets('permissão recusada mostra "Abrir definições"', (tester) async {
      device = FakeDevice(cameraGranted: false);
      await openDetail(tester);
      await chooseSource(tester, 'camera');

      expect(
        find.text('Precisamos da câmara para fotografar o livrete.'),
        findsOneWidget,
      );
      expect(find.text('Abrir definições'), findsOneWidget);
      expect(find.text('Escolher da galeria'), findsOneWidget);
      expect(device.cameras, isEmpty);

      await tester.tap(find.text('Abrir definições'));
      await tester.pumpAndSettle();
      expect(device.permission.settingsOpened, 1);
    });

    testWidgets(
      'disparo → revisão → "Usar foto" comprime e envia image/jpeg; volta ao detalhe',
      (tester) async {
        stubUpload();
        await openDetail(tester);
        await chooseSource(tester, 'camera');

        // Pré-visualização com a moldura, o texto e os botões.
        expect(find.byKey(const Key('fake-camera-preview')), findsOneWidget);
        expect(find.byType(DocumentFrameOverlay), findsOneWidget);
        expect(
          find.text(
            'Enquadre o livrete dentro do rectângulo, com boa luz e sem reflexos.',
          ),
          findsOneWidget,
        );
        expect(find.byType(NavigationBar), findsNothing);
        final shutter = tester.getSize(find.byKey(const Key('camera-shutter')));
        expect(shutter, const Size(72, 72));
        final frame = DocumentFrameOverlay.frameRect(const Size(360, 740));
        expect(frame.width, closeTo(360 * 0.88, 0.01));
        expect(frame.width / frame.height, closeTo(1.5, 0.001));

        await tester.tap(find.byKey(const Key('camera-flash')));
        await tester.pumpAndSettle();
        await tester.tap(find.byKey(const Key('camera-flash')));
        await tester.pumpAndSettle();
        expect(device.cameras.single.flashModes, [
          FlashMode.auto,
          FlashMode.always,
        ]);

        await tester.tap(find.byKey(const Key('camera-shutter')));
        await tester.pumpAndSettle();
        expect(device.cameras.single.captures, 1);
        expect(find.text('Repetir'), findsOneWidget);
        expect(find.text('Usar foto'), findsOneWidget);
        expect(device.compressor.calls, isEmpty);

        var uploadsBeforeCompress = -1;
        device.compressor.onCall = () =>
            uploadsBeforeCompress = api.calls('POST', _uploadPath).length;
        await tester.tap(find.text('Usar foto'));
        await tester.pumpAndSettle();

        expect(device.compressor.calls.single.keepExif, isFalse);
        expect(uploadsBeforeCompress, 0);
        final upload = api.calls('POST', _uploadPath).single;
        expect(_sentFile(upload), (
          contentType: 'image/jpeg',
          filename: 'livrete.jpg',
        ));
        expect(
          (upload.data as FormData).files.single.value.length,
          tinyJpeg.length,
        );
        // De volta ao detalhe, com o aviso e o livrete.
        expect(find.byKey(const Key('fake-camera-preview')), findsNothing);
        expect(find.text('Livrete guardado'), findsOneWidget);
        expect(device.cameras.single.disposed, isTrue);
      },
    );

    testWidgets('"Repetir" volta à câmara', (tester) async {
      await openDetail(tester);
      await chooseSource(tester, 'camera');
      await tester.tap(find.byKey(const Key('camera-shutter')));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('photo-review-image')), findsOneWidget);

      await tester.tap(find.text('Repetir'));
      await tester.pumpAndSettle();

      expect(find.byKey(const Key('photo-review-image')), findsNothing);
      expect(find.byKey(const Key('camera-shutter')), findsOneWidget);
      expect(find.byKey(const Key('fake-camera-preview')), findsOneWidget);
      expect(api.calls('POST', _uploadPath), isEmpty);
      expect(device.compressor.calls, isEmpty);
    });

    testWidgets('fechar volta ao detalhe sem enviar', (tester) async {
      await openDetail(tester);
      await chooseSource(tester, 'camera');
      await tester.tap(find.byKey(const Key('camera-close')));
      await tester.pumpAndSettle();

      expect(find.byKey(const Key('doc-upload')), findsOneWidget);
      expect(find.text('Livrete guardado'), findsNothing);
      expect(api.calls('POST', _uploadPath), isEmpty);
    });
  });

  group('galeria e PDF', () {
    testWidgets('galeria: prepara e envia JPEG; o temporário apaga-se', (
      tester,
    ) async {
      stubUpload();
      var discarded = false;
      device.gallery.file = ChosenFile(
        name: 'IMG_0001.HEIC',
        size: 3000000,
        contentType: 'image/heic',
        read: () async => tinyPng,
        discard: () async => discarded = true,
      );
      await openDetail(tester);
      await chooseSource(tester, 'gallery');

      expect(device.compressor.calls, hasLength(1));
      expect(_sentFile(api.calls('POST', _uploadPath).single), (
        contentType: 'image/jpeg',
        filename: 'livrete.jpg',
      ));
      expect(discarded, isTrue);
      expect(find.text('Livrete guardado'), findsOneWidget);
    });

    testWidgets('PDF de 11 MB recusado sem pedido à API', (tester) async {
      device.pdf.file = ChosenFile(
        name: 'livrete.pdf',
        size: 11 * 1024 * 1024,
        contentType: 'application/pdf',
        read: () async => Uint8List(11 * 1024 * 1024),
      );
      await openDetail(tester);
      await chooseSource(tester, 'pdf');

      expect(find.text('O ficheiro tem mais de 10 MB.'), findsOneWidget);
      expect(api.calls('POST', _uploadPath), isEmpty);
      expect(device.pdf.reads, 0);
      expect(device.pdf.discards, 1);
    });

    testWidgets('PDF até 10 MB é enviado como application/pdf', (tester) async {
      stubUpload('application/pdf');
      device.pdf.file = ChosenFile(
        name: 'livrete.pdf',
        size: 1024,
        contentType: 'application/pdf',
        read: () async => Uint8List.fromList('%PDF-1.4'.codeUnits),
      );
      await openDetail(tester);
      await chooseSource(tester, 'pdf');

      expect(_sentFile(api.calls('POST', _uploadPath).single), (
        contentType: 'application/pdf',
        filename: 'livrete.pdf',
      ));
      expect(device.compressor.calls, isEmpty);
      expect(find.text('Livrete guardado'), findsOneWidget);
    });

    testWidgets('FILE_TYPE_NOT_ALLOWED do servidor aparece na secção', (
      tester,
    ) async {
      api.on(
        'POST',
        _uploadPath,
        415,
        MockApi.problem(415, 'FILE_TYPE_NOT_ALLOWED'),
      );
      device.pdf.file = ChosenFile(
        name: 'livrete.pdf',
        size: 1024,
        contentType: 'application/pdf',
        read: () async => Uint8List(1024),
      );
      await openDetail(tester);
      await chooseSource(tester, 'pdf');

      expect(find.byKey(const Key('doc-error')), findsOneWidget);
      expect(find.text('Livrete guardado'), findsNothing);
    });
  });

  group('livrete guardado', () {
    testWidgets('JPEG: miniatura que abre em ecrã inteiro', (tester) async {
      api
        ..on(
          'GET',
          _vehiclePath,
          200,
          VehicleFixtures.vehicle(
            registrationDoc: VehicleFixtures.registrationDoc(),
          ),
        )
        ..onBytes('/api/v1/files/$_fileId', tinyPng, 'image/jpeg');
      await openDetail(tester);
      await scrollTo(tester, find.byKey(const Key('doc-thumbnail')));

      expect(find.text('Substituir'), findsOneWidget);
      expect(find.byKey(const Key('doc-open')), findsNothing);
      await tester.tap(find.byKey(const Key('doc-thumbnail')));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('image-viewer')), findsOneWidget);
      expect(find.byType(InteractiveViewer), findsOneWidget);
    });

    for (final (contentType, label) in [
      ('application/pdf', 'PDF'),
      ('image/heic', 'HEIC'),
    ]) {
      testWidgets('$label: ícone, tamanho e "Abrir" com open_filex', (
        tester,
      ) async {
        api
          ..on(
            'GET',
            _vehiclePath,
            200,
            VehicleFixtures.vehicle(
              registrationDoc: VehicleFixtures.registrationDoc(
                contentType: contentType,
              ),
            ),
          )
          ..onBytes('/api/v1/files/$_fileId', tinyPng, contentType);
        await openDetail(tester);
        await scrollTo(tester, find.byKey(const Key('doc-open')));

        expect(find.byKey(const Key('doc-thumbnail')), findsNothing);
        expect(find.textContaining('$label · '), findsOneWidget);
        await tester.tap(find.byKey(const Key('doc-open')));
        await tester.pumpAndSettle();

        expect(device.opener.opened.single.contentType, contentType);
        expect(device.opener.opened.single.length, tinyPng.length);
      });
    }
  });
}
