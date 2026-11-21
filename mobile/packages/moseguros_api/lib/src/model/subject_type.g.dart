// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'subject_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const SubjectType _$PLATE = const SubjectType._('PLATE');
const SubjectType _$TOKEN = const SubjectType._('TOKEN');

SubjectType _$valueOf(String name) {
  switch (name) {
    case 'PLATE':
      return _$PLATE;
    case 'TOKEN':
      return _$TOKEN;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<SubjectType> _$values =
    BuiltSet<SubjectType>(const <SubjectType>[
  _$PLATE,
  _$TOKEN,
]);

class _$SubjectTypeMeta {
  const _$SubjectTypeMeta();
  SubjectType get PLATE => _$PLATE;
  SubjectType get TOKEN => _$TOKEN;
  SubjectType valueOf(String name) => _$valueOf(name);
  BuiltSet<SubjectType> get values => _$values;
}

abstract class _$SubjectTypeMixin {
  // ignore: non_constant_identifier_names
  _$SubjectTypeMeta get SubjectType => const _$SubjectTypeMeta();
}

Serializer<SubjectType> _$subjectTypeSerializer = _$SubjectTypeSerializer();

class _$SubjectTypeSerializer implements PrimitiveSerializer<SubjectType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PLATE': 'PLATE',
    'TOKEN': 'TOKEN',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PLATE': 'PLATE',
    'TOKEN': 'TOKEN',
  };

  @override
  final Iterable<Type> types = const <Type>[SubjectType];
  @override
  final String wireName = 'SubjectType';

  @override
  Object serialize(Serializers serializers, SubjectType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  SubjectType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      SubjectType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
