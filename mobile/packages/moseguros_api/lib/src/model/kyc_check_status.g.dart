// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_check_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const KycCheckStatus _$PENDING = const KycCheckStatus._('PENDING');
const KycCheckStatus _$VERIFIED = const KycCheckStatus._('VERIFIED');
const KycCheckStatus _$REJECTED = const KycCheckStatus._('REJECTED');

KycCheckStatus _$valueOf(String name) {
  switch (name) {
    case 'PENDING':
      return _$PENDING;
    case 'VERIFIED':
      return _$VERIFIED;
    case 'REJECTED':
      return _$REJECTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<KycCheckStatus> _$values =
    BuiltSet<KycCheckStatus>(const <KycCheckStatus>[
  _$PENDING,
  _$VERIFIED,
  _$REJECTED,
]);

class _$KycCheckStatusMeta {
  const _$KycCheckStatusMeta();
  KycCheckStatus get PENDING => _$PENDING;
  KycCheckStatus get VERIFIED => _$VERIFIED;
  KycCheckStatus get REJECTED => _$REJECTED;
  KycCheckStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<KycCheckStatus> get values => _$values;
}

abstract class _$KycCheckStatusMixin {
  // ignore: non_constant_identifier_names
  _$KycCheckStatusMeta get KycCheckStatus => const _$KycCheckStatusMeta();
}

Serializer<KycCheckStatus> _$kycCheckStatusSerializer =
    _$KycCheckStatusSerializer();

class _$KycCheckStatusSerializer
    implements PrimitiveSerializer<KycCheckStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PENDING': 'PENDING',
    'VERIFIED': 'VERIFIED',
    'REJECTED': 'REJECTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PENDING': 'PENDING',
    'VERIFIED': 'VERIFIED',
    'REJECTED': 'REJECTED',
  };

  @override
  final Iterable<Type> types = const <Type>[KycCheckStatus];
  @override
  final String wireName = 'KycCheckStatus';

  @override
  Object serialize(Serializers serializers, KycCheckStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  KycCheckStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      KycCheckStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
