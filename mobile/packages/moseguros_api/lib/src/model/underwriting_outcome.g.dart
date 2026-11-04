// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'underwriting_outcome.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UnderwritingOutcome _$AUTOMATIC =
    const UnderwritingOutcome._('AUTOMATIC');
const UnderwritingOutcome _$MANUAL_REVIEW =
    const UnderwritingOutcome._('MANUAL_REVIEW');

UnderwritingOutcome _$valueOf(String name) {
  switch (name) {
    case 'AUTOMATIC':
      return _$AUTOMATIC;
    case 'MANUAL_REVIEW':
      return _$MANUAL_REVIEW;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UnderwritingOutcome> _$values =
    BuiltSet<UnderwritingOutcome>(const <UnderwritingOutcome>[
  _$AUTOMATIC,
  _$MANUAL_REVIEW,
]);

class _$UnderwritingOutcomeMeta {
  const _$UnderwritingOutcomeMeta();
  UnderwritingOutcome get AUTOMATIC => _$AUTOMATIC;
  UnderwritingOutcome get MANUAL_REVIEW => _$MANUAL_REVIEW;
  UnderwritingOutcome valueOf(String name) => _$valueOf(name);
  BuiltSet<UnderwritingOutcome> get values => _$values;
}

abstract class _$UnderwritingOutcomeMixin {
  // ignore: non_constant_identifier_names
  _$UnderwritingOutcomeMeta get UnderwritingOutcome =>
      const _$UnderwritingOutcomeMeta();
}

Serializer<UnderwritingOutcome> _$underwritingOutcomeSerializer =
    _$UnderwritingOutcomeSerializer();

class _$UnderwritingOutcomeSerializer
    implements PrimitiveSerializer<UnderwritingOutcome> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'AUTOMATIC': 'AUTOMATIC',
    'MANUAL_REVIEW': 'MANUAL_REVIEW',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'AUTOMATIC': 'AUTOMATIC',
    'MANUAL_REVIEW': 'MANUAL_REVIEW',
  };

  @override
  final Iterable<Type> types = const <Type>[UnderwritingOutcome];
  @override
  final String wireName = 'UnderwritingOutcome';

  @override
  Object serialize(Serializers serializers, UnderwritingOutcome object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UnderwritingOutcome deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UnderwritingOutcome.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
