import 'package:flutter/material.dart';

import '../../core/theme.dart';

/// Forma da moldura sobre a pré-visualização.
enum FrameShape { rect, oval }

/// Moldura de um ângulo: largura e altura em fracções da **largura** da
/// pré-visualização (S4-09 §4).
typedef SlotFrame = ({double width, double height, FrameShape shape});

/// Moldura de cada ângulo da vistoria; `null` em `EXTRA` (sem moldura).
SlotFrame? slotFrame(String slot) => switch (slot) {
  'FRONT' || 'REAR' => (width: 0.86, height: 0.48, shape: FrameShape.rect),
  'LEFT' || 'RIGHT' => (width: 0.94, height: 0.36, shape: FrameShape.rect),
  'ODOMETER' => (width: 0.70, height: 0.30, shape: FrameShape.rect),
  'CHASSIS' => (width: 0.90, height: 0.14, shape: FrameShape.rect),
  _ => null,
};

/// Documento de identificação: cartão 1,586:1 a 90 % da largura (S4-09 §6).
const SlotFrame kycDocumentFrame = (
  width: 0.90,
  height: 0.90 / 1.586,
  shape: FrameShape.rect,
);

/// Selfie: oval de 60 % × 75 % da largura (S4-09 §6).
const SlotFrame kycSelfieFrame = (
  width: 0.60,
  height: 0.75,
  shape: FrameShape.oval,
);

/// Rectângulo da moldura num ecrã de [size], centrado.
Rect frameRect(SlotFrame frame, Size size) => Rect.fromCenter(
  center: size.center(Offset.zero),
  width: size.width * frame.width,
  height: size.width * frame.height,
);

/// Pinta por cima da pré-visualização: fora da moldura, azul MoSeguros
/// (`#142D4E`) a 55 %; a moldura transparente, com contorno branco de 3 px e
/// cantos de 8 px. Sem moldura ([frame] `null`), não pinta nada.
class SlotOverlayPainter extends CustomPainter {
  const SlotOverlayPainter(this.frame);

  final SlotFrame? frame;

  static final maskColor = BrandColors.primary.withValues(alpha: 0.55);
  static const strokeWidth = 3.0;
  static const radius = Radius.circular(8);

  @override
  void paint(Canvas canvas, Size size) {
    final frame = this.frame;
    if (frame == null) return;
    final rect = frameRect(frame, size);
    final hole = Path();
    if (frame.shape == FrameShape.oval) {
      hole.addOval(rect);
    } else {
      hole.addRRect(RRect.fromRectAndRadius(rect, radius));
    }
    final mask = Path()
      ..fillType = PathFillType.evenOdd
      ..addRect(Offset.zero & size)
      ..addPath(hole, Offset.zero);
    canvas.drawPath(mask, Paint()..color = maskColor);
    canvas.drawPath(
      hole,
      Paint()
        ..color = Colors.white
        ..style = PaintingStyle.stroke
        ..strokeWidth = strokeWidth,
    );
  }

  @override
  bool shouldRepaint(SlotOverlayPainter oldDelegate) =>
      oldDelegate.frame != frame;
}

/// A moldura com uma nota opcional por baixo (ex.: "Afaste-se até o veículo
/// caber inteiro" nos lados).
class SlotOverlay extends StatelessWidget {
  const SlotOverlay({super.key, required this.frame, this.note});

  final SlotFrame? frame;
  final String? note;

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final size = constraints.biggest;
        final frame = this.frame;
        final note = this.note;
        return Stack(
          fit: StackFit.expand,
          children: [
            CustomPaint(
              key: const Key('slot-overlay'),
              size: size,
              painter: SlotOverlayPainter(frame),
            ),
            if (note != null && frame != null)
              Positioned(
                left: 16,
                right: 16,
                top: frameRect(frame, size).bottom + 12,
                child: Text(
                  note,
                  key: const Key('slot-overlay-note'),
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
