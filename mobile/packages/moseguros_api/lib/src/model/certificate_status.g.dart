// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'certificate_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CertificateStatus _$VALID = const CertificateStatus._('VALID');
const CertificateStatus _$REVOKED = const CertificateStatus._('REVOKED');

CertificateStatus _$valueOf(String name) {
  switch (name) {
    case 'VALID':
      return _$VALID;
    case 'REVOKED':
      return _$REVOKED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CertificateStatus> _$values =
    BuiltSet<CertificateStatus>(const <CertificateStatus>[
  _$VALID,
  _$REVOKED,
]);

class _$CertificateStatusMeta {
  const _$CertificateStatusMeta();
  CertificateStatus get VALID => _$VALID;
  CertificateStatus get REVOKED => _$REVOKED;
  CertificateStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<CertificateStatus> get values => _$values;
}

abstract class _$CertificateStatusMixin {
  // ignore: non_constant_identifier_names
  _$CertificateStatusMeta get CertificateStatus =>
      const _$CertificateStatusMeta();
}

Serializer<CertificateStatus> _$certificateStatusSerializer =
    _$CertificateStatusSerializer();

class _$CertificateStatusSerializer
    implements PrimitiveSerializer<CertificateStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'VALID': 'VALID',
    'REVOKED': 'REVOKED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'VALID': 'VALID',
    'REVOKED': 'REVOKED',
  };

  @override
  final Iterable<Type> types = const <Type>[CertificateStatus];
  @override
  final String wireName = 'CertificateStatus';

  @override
  Object serialize(Serializers serializers, CertificateStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CertificateStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CertificateStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
