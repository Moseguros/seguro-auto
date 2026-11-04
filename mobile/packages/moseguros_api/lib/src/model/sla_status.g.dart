// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sla_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const SlaStatus _$ON_TIME = const SlaStatus._('ON_TIME');
const SlaStatus _$ALERT = const SlaStatus._('ALERT');
const SlaStatus _$OVERDUE = const SlaStatus._('OVERDUE');

SlaStatus _$valueOf(String name) {
  switch (name) {
    case 'ON_TIME':
      return _$ON_TIME;
    case 'ALERT':
      return _$ALERT;
    case 'OVERDUE':
      return _$OVERDUE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<SlaStatus> _$values = BuiltSet<SlaStatus>(const <SlaStatus>[
  _$ON_TIME,
  _$ALERT,
  _$OVERDUE,
]);

class _$SlaStatusMeta {
  const _$SlaStatusMeta();
  SlaStatus get ON_TIME => _$ON_TIME;
  SlaStatus get ALERT => _$ALERT;
  SlaStatus get OVERDUE => _$OVERDUE;
  SlaStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<SlaStatus> get values => _$values;
}

abstract class _$SlaStatusMixin {
  // ignore: non_constant_identifier_names
  _$SlaStatusMeta get SlaStatus => const _$SlaStatusMeta();
}

Serializer<SlaStatus> _$slaStatusSerializer = _$SlaStatusSerializer();

class _$SlaStatusSerializer implements PrimitiveSerializer<SlaStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'ON_TIME': 'ON_TIME',
    'ALERT': 'ALERT',
    'OVERDUE': 'OVERDUE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'ON_TIME': 'ON_TIME',
    'ALERT': 'ALERT',
    'OVERDUE': 'OVERDUE',
  };

  @override
  final Iterable<Type> types = const <Type>[SlaStatus];
  @override
  final String wireName = 'SlaStatus';

  @override
  Object serialize(Serializers serializers, SlaStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  SlaStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      SlaStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
