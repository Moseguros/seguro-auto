// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'certificate_check_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CertificateCheckStatus _$CURRENT =
    const CertificateCheckStatus._('CURRENT');
const CertificateCheckStatus _$REVOKED =
    const CertificateCheckStatus._('REVOKED');

CertificateCheckStatus _$valueOf(String name) {
  switch (name) {
    case 'CURRENT':
      return _$CURRENT;
    case 'REVOKED':
      return _$REVOKED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CertificateCheckStatus> _$values =
    BuiltSet<CertificateCheckStatus>(const <CertificateCheckStatus>[
  _$CURRENT,
  _$REVOKED,
]);

class _$CertificateCheckStatusMeta {
  const _$CertificateCheckStatusMeta();
  CertificateCheckStatus get CURRENT => _$CURRENT;
  CertificateCheckStatus get REVOKED => _$REVOKED;
  CertificateCheckStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<CertificateCheckStatus> get values => _$values;
}

abstract class _$CertificateCheckStatusMixin {
  // ignore: non_constant_identifier_names
  _$CertificateCheckStatusMeta get CertificateCheckStatus =>
      const _$CertificateCheckStatusMeta();
}

Serializer<CertificateCheckStatus> _$certificateCheckStatusSerializer =
    _$CertificateCheckStatusSerializer();

class _$CertificateCheckStatusSerializer
    implements PrimitiveSerializer<CertificateCheckStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CURRENT': 'CURRENT',
    'REVOKED': 'REVOKED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CURRENT': 'CURRENT',
    'REVOKED': 'REVOKED',
  };

  @override
  final Iterable<Type> types = const <Type>[CertificateCheckStatus];
  @override
  final String wireName = 'CertificateCheckStatus';

  @override
  Object serialize(Serializers serializers, CertificateCheckStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CertificateCheckStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CertificateCheckStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
