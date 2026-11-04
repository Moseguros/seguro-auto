// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'declaration_question.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const DeclarationQuestion _$cLAIMSLAST3YEARS =
    const DeclarationQuestion._('cLAIMSLAST3YEARS');
const DeclarationQuestion _$PREVIOUS_REFUSAL =
    const DeclarationQuestion._('PREVIOUS_REFUSAL');
const DeclarationQuestion _$USAGE_DIFFERENT =
    const DeclarationQuestion._('USAGE_DIFFERENT');
const DeclarationQuestion _$VEHICLE_MODIFIED =
    const DeclarationQuestion._('VEHICLE_MODIFIED');
const DeclarationQuestion _$NOVICE_DRIVERS =
    const DeclarationQuestion._('NOVICE_DRIVERS');

DeclarationQuestion _$valueOf(String name) {
  switch (name) {
    case 'cLAIMSLAST3YEARS':
      return _$cLAIMSLAST3YEARS;
    case 'PREVIOUS_REFUSAL':
      return _$PREVIOUS_REFUSAL;
    case 'USAGE_DIFFERENT':
      return _$USAGE_DIFFERENT;
    case 'VEHICLE_MODIFIED':
      return _$VEHICLE_MODIFIED;
    case 'NOVICE_DRIVERS':
      return _$NOVICE_DRIVERS;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<DeclarationQuestion> _$values =
    BuiltSet<DeclarationQuestion>(const <DeclarationQuestion>[
  _$cLAIMSLAST3YEARS,
  _$PREVIOUS_REFUSAL,
  _$USAGE_DIFFERENT,
  _$VEHICLE_MODIFIED,
  _$NOVICE_DRIVERS,
]);

class _$DeclarationQuestionMeta {
  const _$DeclarationQuestionMeta();
  DeclarationQuestion get cLAIMSLAST3YEARS => _$cLAIMSLAST3YEARS;
  DeclarationQuestion get PREVIOUS_REFUSAL => _$PREVIOUS_REFUSAL;
  DeclarationQuestion get USAGE_DIFFERENT => _$USAGE_DIFFERENT;
  DeclarationQuestion get VEHICLE_MODIFIED => _$VEHICLE_MODIFIED;
  DeclarationQuestion get NOVICE_DRIVERS => _$NOVICE_DRIVERS;
  DeclarationQuestion valueOf(String name) => _$valueOf(name);
  BuiltSet<DeclarationQuestion> get values => _$values;
}

abstract class _$DeclarationQuestionMixin {
  // ignore: non_constant_identifier_names
  _$DeclarationQuestionMeta get DeclarationQuestion =>
      const _$DeclarationQuestionMeta();
}

Serializer<DeclarationQuestion> _$declarationQuestionSerializer =
    _$DeclarationQuestionSerializer();

class _$DeclarationQuestionSerializer
    implements PrimitiveSerializer<DeclarationQuestion> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'cLAIMSLAST3YEARS': 'CLAIMS_LAST_3_YEARS',
    'PREVIOUS_REFUSAL': 'PREVIOUS_REFUSAL',
    'USAGE_DIFFERENT': 'USAGE_DIFFERENT',
    'VEHICLE_MODIFIED': 'VEHICLE_MODIFIED',
    'NOVICE_DRIVERS': 'NOVICE_DRIVERS',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CLAIMS_LAST_3_YEARS': 'cLAIMSLAST3YEARS',
    'PREVIOUS_REFUSAL': 'PREVIOUS_REFUSAL',
    'USAGE_DIFFERENT': 'USAGE_DIFFERENT',
    'VEHICLE_MODIFIED': 'VEHICLE_MODIFIED',
    'NOVICE_DRIVERS': 'NOVICE_DRIVERS',
  };

  @override
  final Iterable<Type> types = const <Type>[DeclarationQuestion];
  @override
  final String wireName = 'DeclarationQuestion';

  @override
  Object serialize(Serializers serializers, DeclarationQuestion object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  DeclarationQuestion deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      DeclarationQuestion.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
