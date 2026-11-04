import '../../l10n/app_localizations.dart';

/// Ângulos obrigatórios da vistoria, pela ordem da câmara (S4-03).
const requiredSlots = ['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'];

/// Fotografias extra (danos, pormenores).
const extraSlot = 'EXTRA';

/// Idade máxima de uma fotografia aceite pelo servidor (S4-03).
const maxPhotoAge = Duration(days: 7);

/// Nome do ângulo (`inspectionSlot<Slot>`).
String slotName(AppLocalizations l10n, String slot) => switch (slot) {
  'FRONT' => l10n.inspectionSlotFront,
  'REAR' => l10n.inspectionSlotRear,
  'LEFT' => l10n.inspectionSlotLeft,
  'RIGHT' => l10n.inspectionSlotRight,
  'ODOMETER' => l10n.inspectionSlotOdometer,
  'CHASSIS' => l10n.inspectionSlotChassis,
  _ => l10n.inspectionSlotExtra,
};

/// Instrução do ângulo (`inspectionInstruction<Slot>`, S4-08 §6).
String slotInstruction(AppLocalizations l10n, String slot) => switch (slot) {
  'FRONT' => l10n.inspectionInstructionFront,
  'REAR' => l10n.inspectionInstructionRear,
  'LEFT' => l10n.inspectionInstructionLeft,
  'RIGHT' => l10n.inspectionInstructionRight,
  'ODOMETER' => l10n.inspectionInstructionOdometer,
  'CHASSIS' => l10n.inspectionInstructionChassis,
  _ => l10n.inspectionInstructionExtra,
};
