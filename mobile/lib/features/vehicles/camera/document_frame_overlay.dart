import 'package:flutter/material.dart';

/// Moldura do livrete por cima da pré-visualização: máscara escura semitransparente
/// com um rectângulo transparente ao centro (88 % da largura, proporção 3:2) e os
/// cantos marcados a branco.
class DocumentFrameOverlay extends StatelessWidget {
  const DocumentFrameOverlay({super.key, this.hint});

  /// Texto por cima da moldura.
  final String? hint;

  static const widthFactor = 0.88;

  /// Largura:altura do livrete.
  static const aspectRatio = 3 / 2;

  /// Rectângulo da moldura num ecrã de [size].
  static Rect frameRect(Size size) {
    final width = size.width * widthFactor;
    final height = width / aspectRatio;
    return Rect.fromCenter(
      center: size.center(Offset.zero),
      width: width,
      height: height,
    );
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final size = constraints.biggest;
        final frame = frameRect(size);
        final hint = this.hint;
        return Stack(
          fit: StackFit.expand,
          children: [
            CustomPaint(
              key: const Key('document-frame'),
              size: size,
              painter: DocumentFramePainter(frame),
            ),
            if (hint != null)
              Positioned(
                left: frame.left,
                width: frame.width,
                bottom: size.height - frame.top + 16,
                child: Text(
                  hint,
                  key: const Key('camera-frame-hint'),
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    color: Colors.white,
                    fontWeight: FontWeight.w600,
                    shadows: const [Shadow(blurRadius: 4)],
                  ),
                ),
              ),
          ],
        );
      },
    );
  }
}

class DocumentFramePainter extends CustomPainter {
  const DocumentFramePainter(this.frame);

  final Rect frame;

  static const cornerStroke = 4.0;
  static const cornerLength = 28.0;
  static const radius = Radius.circular(12);

  @override
  void paint(Canvas canvas, Size size) {
    final mask = Path()
      ..fillType = PathFillType.evenOdd
      ..addRect(Offset.zero & size)
      ..addRRect(RRect.fromRectAndRadius(frame, radius));
    canvas.drawPath(mask, Paint()..color = Colors.black54);

    final corner = Paint()
      ..color = Colors.white
      ..style = PaintingStyle.stroke
      ..strokeWidth = cornerStroke
      ..strokeCap = StrokeCap.round;
    final l = frame.left;
    final t = frame.top;
    final r = frame.right;
    final b = frame.bottom;
    const c = cornerLength;
    for (final points in [
      [Offset(l, t + c), Offset(l, t), Offset(l + c, t)],
      [Offset(r - c, t), Offset(r, t), Offset(r, t + c)],
      [Offset(r, b - c), Offset(r, b), Offset(r - c, b)],
      [Offset(l + c, b), Offset(l, b), Offset(l, b - c)],
    ]) {
      canvas.drawPath(
        Path()
          ..moveTo(points[0].dx, points[0].dy)
          ..lineTo(points[1].dx, points[1].dy)
          ..lineTo(points[2].dx, points[2].dy),
        corner,
      );
    }
  }

  @override
  bool shouldRepaint(DocumentFramePainter oldDelegate) =>
      oldDelegate.frame != frame;
}
