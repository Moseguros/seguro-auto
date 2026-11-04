// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'suitability_question.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const SuitabilityQuestion _$NEED = const SuitabilityQuestion._('NEED');
const SuitabilityQuestion _$FINANCED = const SuitabilityQuestion._('FINANCED');
const SuitabilityQuestion _$CAN_AFFORD_REPAIR =
    const SuitabilityQuestion._('CAN_AFFORD_REPAIR');
const SuitabilityQuestion _$CROSS_BORDER =
    const SuitabilityQuestion._('CROSS_BORDER');
const SuitabilityQuestion _$DEDUCTIBLE =
    const SuitabilityQuestion._('DEDUCTIBLE');

SuitabilityQuestion _$valueOf(String name) {
  switch (name) {
    case 'NEED':
      return _$NEED;
    case 'FINANCED':
      return _$FINANCED;
    case 'CAN_AFFORD_REPAIR':
      return _$CAN_AFFORD_REPAIR;
    case 'CROSS_BORDER':
      return _$CROSS_BORDER;
    case 'DEDUCTIBLE':
      return _$DEDUCTIBLE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<SuitabilityQuestion> _$values =
    BuiltSet<SuitabilityQuestion>(const <SuitabilityQuestion>[
  _$NEED,
  _$FINANCED,
  _$CAN_AFFORD_REPAIR,
  _$CROSS_BORDER,
  _$DEDUCTIBLE,
]);

class _$SuitabilityQuestionMeta {
  const _$SuitabilityQuestionMeta();
  SuitabilityQuestion get NEED => _$NEED;
  SuitabilityQuestion get FINANCED => _$FINANCED;
  SuitabilityQuestion get CAN_AFFORD_REPAIR => _$CAN_AFFORD_REPAIR;
  SuitabilityQuestion get CROSS_BORDER => _$CROSS_BORDER;
  SuitabilityQuestion get DEDUCTIBLE => _$DEDUCTIBLE;
  SuitabilityQuestion valueOf(String name) => _$valueOf(name);
  BuiltSet<SuitabilityQuestion> get values => _$values;
}

abstract class _$SuitabilityQuestionMixin {
  // ignore: non_constant_identifier_names
  _$SuitabilityQuestionMeta get SuitabilityQuestion =>
      const _$SuitabilityQuestionMeta();
}

Serializer<SuitabilityQuestion> _$suitabilityQuestionSerializer =
    _$SuitabilityQuestionSerializer();

class _$SuitabilityQuestionSerializer
    implements PrimitiveSerializer<SuitabilityQuestion> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'NEED': 'SUIT_NEED',
    'FINANCED': 'SUIT_FINANCED',
    'CAN_AFFORD_REPAIR': 'SUIT_CAN_AFFORD_REPAIR',
    'CROSS_BORDER': 'SUIT_CROSS_BORDER',
    'DEDUCTIBLE': 'SUIT_DEDUCTIBLE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'SUIT_NEED': 'NEED',
    'SUIT_FINANCED': 'FINANCED',
    'SUIT_CAN_AFFORD_REPAIR': 'CAN_AFFORD_REPAIR',
    'SUIT_CROSS_BORDER': 'CROSS_BORDER',
    'SUIT_DEDUCTIBLE': 'DEDUCTIBLE',
  };

  @override
  final Iterable<Type> types = const <Type>[SuitabilityQuestion];
  @override
  final String wireName = 'SuitabilityQuestion';

  @override
  Object serialize(Serializers serializers, SuitabilityQuestion object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  SuitabilityQuestion deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      SuitabilityQuestion.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
