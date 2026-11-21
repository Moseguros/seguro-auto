// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'cover_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CoverStatus _$COVERED = const CoverStatus._('COVERED');
const CoverStatus _$NOT_STARTED = const CoverStatus._('NOT_STARTED');
const CoverStatus _$ENDED = const CoverStatus._('ENDED');
const CoverStatus _$SUSPENDED = const CoverStatus._('SUSPENDED');
const CoverStatus _$CANCELLED = const CoverStatus._('CANCELLED');
const CoverStatus _$LAPSED = const CoverStatus._('LAPSED');

CoverStatus _$valueOf(String name) {
  switch (name) {
    case 'COVERED':
      return _$COVERED;
    case 'NOT_STARTED':
      return _$NOT_STARTED;
    case 'ENDED':
      return _$ENDED;
    case 'SUSPENDED':
      return _$SUSPENDED;
    case 'CANCELLED':
      return _$CANCELLED;
    case 'LAPSED':
      return _$LAPSED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CoverStatus> _$values =
    BuiltSet<CoverStatus>(const <CoverStatus>[
  _$COVERED,
  _$NOT_STARTED,
  _$ENDED,
  _$SUSPENDED,
  _$CANCELLED,
  _$LAPSED,
]);

class _$CoverStatusMeta {
  const _$CoverStatusMeta();
  CoverStatus get COVERED => _$COVERED;
  CoverStatus get NOT_STARTED => _$NOT_STARTED;
  CoverStatus get ENDED => _$ENDED;
  CoverStatus get SUSPENDED => _$SUSPENDED;
  CoverStatus get CANCELLED => _$CANCELLED;
  CoverStatus get LAPSED => _$LAPSED;
  CoverStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<CoverStatus> get values => _$values;
}

abstract class _$CoverStatusMixin {
  // ignore: non_constant_identifier_names
  _$CoverStatusMeta get CoverStatus => const _$CoverStatusMeta();
}

Serializer<CoverStatus> _$coverStatusSerializer = _$CoverStatusSerializer();

class _$CoverStatusSerializer implements PrimitiveSerializer<CoverStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'COVERED': 'COVERED',
    'NOT_STARTED': 'NOT_STARTED',
    'ENDED': 'ENDED',
    'SUSPENDED': 'SUSPENDED',
    'CANCELLED': 'CANCELLED',
    'LAPSED': 'LAPSED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'COVERED': 'COVERED',
    'NOT_STARTED': 'NOT_STARTED',
    'ENDED': 'ENDED',
    'SUSPENDED': 'SUSPENDED',
    'CANCELLED': 'CANCELLED',
    'LAPSED': 'LAPSED',
  };

  @override
  final Iterable<Type> types = const <Type>[CoverStatus];
  @override
  final String wireName = 'CoverStatus';

  @override
  Object serialize(Serializers serializers, CoverStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CoverStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CoverStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
