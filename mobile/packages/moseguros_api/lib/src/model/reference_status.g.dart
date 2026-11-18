// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reference_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ReferenceStatus _$ACTIVE = const ReferenceStatus._('ACTIVE');
const ReferenceStatus _$PAID = const ReferenceStatus._('PAID');
const ReferenceStatus _$EXPIRED = const ReferenceStatus._('EXPIRED');
const ReferenceStatus _$CANCELLED = const ReferenceStatus._('CANCELLED');

ReferenceStatus _$valueOf(String name) {
  switch (name) {
    case 'ACTIVE':
      return _$ACTIVE;
    case 'PAID':
      return _$PAID;
    case 'EXPIRED':
      return _$EXPIRED;
    case 'CANCELLED':
      return _$CANCELLED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ReferenceStatus> _$values =
    BuiltSet<ReferenceStatus>(const <ReferenceStatus>[
  _$ACTIVE,
  _$PAID,
  _$EXPIRED,
  _$CANCELLED,
]);

class _$ReferenceStatusMeta {
  const _$ReferenceStatusMeta();
  ReferenceStatus get ACTIVE => _$ACTIVE;
  ReferenceStatus get PAID => _$PAID;
  ReferenceStatus get EXPIRED => _$EXPIRED;
  ReferenceStatus get CANCELLED => _$CANCELLED;
  ReferenceStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<ReferenceStatus> get values => _$values;
}

abstract class _$ReferenceStatusMixin {
  // ignore: non_constant_identifier_names
  _$ReferenceStatusMeta get ReferenceStatus => const _$ReferenceStatusMeta();
}

Serializer<ReferenceStatus> _$referenceStatusSerializer =
    _$ReferenceStatusSerializer();

class _$ReferenceStatusSerializer
    implements PrimitiveSerializer<ReferenceStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'ACTIVE': 'ACTIVE',
    'PAID': 'PAID',
    'EXPIRED': 'EXPIRED',
    'CANCELLED': 'CANCELLED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'ACTIVE': 'ACTIVE',
    'PAID': 'PAID',
    'EXPIRED': 'EXPIRED',
    'CANCELLED': 'CANCELLED',
  };

  @override
  final Iterable<Type> types = const <Type>[ReferenceStatus];
  @override
  final String wireName = 'ReferenceStatus';

  @override
  Object serialize(Serializers serializers, ReferenceStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ReferenceStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ReferenceStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
