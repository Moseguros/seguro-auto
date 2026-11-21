// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'instalment_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InstalmentStatus _$DUE = const InstalmentStatus._('DUE');
const InstalmentStatus _$PAID = const InstalmentStatus._('PAID');
const InstalmentStatus _$OVERDUE = const InstalmentStatus._('OVERDUE');
const InstalmentStatus _$CANCELLED = const InstalmentStatus._('CANCELLED');

InstalmentStatus _$valueOf(String name) {
  switch (name) {
    case 'DUE':
      return _$DUE;
    case 'PAID':
      return _$PAID;
    case 'OVERDUE':
      return _$OVERDUE;
    case 'CANCELLED':
      return _$CANCELLED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InstalmentStatus> _$values =
    BuiltSet<InstalmentStatus>(const <InstalmentStatus>[
  _$DUE,
  _$PAID,
  _$OVERDUE,
  _$CANCELLED,
]);

class _$InstalmentStatusMeta {
  const _$InstalmentStatusMeta();
  InstalmentStatus get DUE => _$DUE;
  InstalmentStatus get PAID => _$PAID;
  InstalmentStatus get OVERDUE => _$OVERDUE;
  InstalmentStatus get CANCELLED => _$CANCELLED;
  InstalmentStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<InstalmentStatus> get values => _$values;
}

abstract class _$InstalmentStatusMixin {
  // ignore: non_constant_identifier_names
  _$InstalmentStatusMeta get InstalmentStatus => const _$InstalmentStatusMeta();
}

Serializer<InstalmentStatus> _$instalmentStatusSerializer =
    _$InstalmentStatusSerializer();

class _$InstalmentStatusSerializer
    implements PrimitiveSerializer<InstalmentStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DUE': 'DUE',
    'PAID': 'PAID',
    'OVERDUE': 'OVERDUE',
    'CANCELLED': 'CANCELLED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DUE': 'DUE',
    'PAID': 'PAID',
    'OVERDUE': 'OVERDUE',
    'CANCELLED': 'CANCELLED',
  };

  @override
  final Iterable<Type> types = const <Type>[InstalmentStatus];
  @override
  final String wireName = 'InstalmentStatus';

  @override
  Object serialize(Serializers serializers, InstalmentStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InstalmentStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InstalmentStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
