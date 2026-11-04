// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'suitability_result.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const SuitabilityResult _$SUITABLE = const SuitabilityResult._('SUITABLE');
const SuitabilityResult _$WARNED = const SuitabilityResult._('WARNED');

SuitabilityResult _$valueOf(String name) {
  switch (name) {
    case 'SUITABLE':
      return _$SUITABLE;
    case 'WARNED':
      return _$WARNED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<SuitabilityResult> _$values =
    BuiltSet<SuitabilityResult>(const <SuitabilityResult>[
  _$SUITABLE,
  _$WARNED,
]);

class _$SuitabilityResultMeta {
  const _$SuitabilityResultMeta();
  SuitabilityResult get SUITABLE => _$SUITABLE;
  SuitabilityResult get WARNED => _$WARNED;
  SuitabilityResult valueOf(String name) => _$valueOf(name);
  BuiltSet<SuitabilityResult> get values => _$values;
}

abstract class _$SuitabilityResultMixin {
  // ignore: non_constant_identifier_names
  _$SuitabilityResultMeta get SuitabilityResult =>
      const _$SuitabilityResultMeta();
}

Serializer<SuitabilityResult> _$suitabilityResultSerializer =
    _$SuitabilityResultSerializer();

class _$SuitabilityResultSerializer
    implements PrimitiveSerializer<SuitabilityResult> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'SUITABLE': 'SUITABLE',
    'WARNED': 'WARNED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'SUITABLE': 'SUITABLE',
    'WARNED': 'WARNED',
  };

  @override
  final Iterable<Type> types = const <Type>[SuitabilityResult];
  @override
  final String wireName = 'SuitabilityResult';

  @override
  Object serialize(Serializers serializers, SuitabilityResult object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  SuitabilityResult deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      SuitabilityResult.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
