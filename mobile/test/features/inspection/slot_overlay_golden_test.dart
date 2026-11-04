import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/features/inspection/slot_overlay_painter.dart';

/// Aceita diferenças mínimas de anti-aliasing entre sistemas (o CI corre em Linux).
class _TolerantComparator extends LocalFileComparator {
  _TolerantComparator(super.testFile);

  static const tolerance = 0.005;

  @override
  Future<bool> compare(Uint8List imageBytes, Uri golden) async {
    final result = await GoldenFileComparator.compareLists(
      imageBytes,
      await getGoldenBytes(golden),
    );
    if (result.passed || result.diffPercent <= tolerance) {
      result.dispose();
      return true;
    }
    final error = await generateFailureOutput(result, golden, basedir);
    result.dispose();
    throw FlutterError(error);
  }
}

Widget _overlay(String slot) => Directionality(
  textDirection: TextDirection.ltr,
  child: Center(
    child: RepaintBoundary(
      child: SizedBox(
        width: 180,
        height: 320,
        child: ColoredBox(
          color: const Color(0xFF808080),
          child: CustomPaint(painter: SlotOverlayPainter(slotFrame(slot))),
        ),
      ),
    ),
  ),
);

void main() {
  setUpAll(() {
    final current = goldenFileComparator;
    if (current is LocalFileComparator) {
      goldenFileComparator = _TolerantComparator(
        Uri.parse('${current.basedir}slot_overlay_golden_test.dart'),
      );
    }
  });

  test('moldura por ângulo (fracções da largura)', () {
    expect(slotFrame('FRONT'), (width: 0.86, height: 0.48, shape: FrameShape.rect));
    expect(slotFrame('REAR'), slotFrame('FRONT'));
    expect(slotFrame('LEFT'), (width: 0.94, height: 0.36, shape: FrameShape.rect));
    expect(slotFrame('RIGHT'), slotFrame('LEFT'));
    expect(slotFrame('ODOMETER'), (width: 0.70, height: 0.30, shape: FrameShape.rect));
    expect(slotFrame('CHASSIS'), (width: 0.90, height: 0.14, shape: FrameShape.rect));
    expect(slotFrame('EXTRA'), isNull);
    final rect = frameRect(slotFrame('FRONT')!, const Size(360, 640));
    expect(rect.width, closeTo(309.6, 1e-9));
    expect(rect.height, closeTo(172.8, 1e-9));
    expect(rect.center, const Offset(180, 320));
  });

  for (final slot in ['FRONT', 'LEFT', 'CHASSIS']) {
    testWidgets('golden: moldura de $slot', (tester) async {
      await tester.pumpWidget(_overlay(slot));
      await expectLater(
        find.byType(RepaintBoundary).first,
        matchesGoldenFile('goldens/slot_${slot.toLowerCase()}.png'),
      );
    });
  }
}
