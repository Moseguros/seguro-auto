// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_rejection_reason.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const KycRejectionReason _$DOCUMENT_UNREADABLE =
    const KycRejectionReason._('DOCUMENT_UNREADABLE');
const KycRejectionReason _$DOCUMENT_EXPIRED =
    const KycRejectionReason._('DOCUMENT_EXPIRED');
const KycRejectionReason _$DATA_MISMATCH =
    const KycRejectionReason._('DATA_MISMATCH');
const KycRejectionReason _$SELFIE_MISMATCH =
    const KycRejectionReason._('SELFIE_MISMATCH');
const KycRejectionReason _$OTHER = const KycRejectionReason._('OTHER');

KycRejectionReason _$valueOf(String name) {
  switch (name) {
    case 'DOCUMENT_UNREADABLE':
      return _$DOCUMENT_UNREADABLE;
    case 'DOCUMENT_EXPIRED':
      return _$DOCUMENT_EXPIRED;
    case 'DATA_MISMATCH':
      return _$DATA_MISMATCH;
    case 'SELFIE_MISMATCH':
      return _$SELFIE_MISMATCH;
    case 'OTHER':
      return _$OTHER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<KycRejectionReason> _$values =
    BuiltSet<KycRejectionReason>(const <KycRejectionReason>[
  _$DOCUMENT_UNREADABLE,
  _$DOCUMENT_EXPIRED,
  _$DATA_MISMATCH,
  _$SELFIE_MISMATCH,
  _$OTHER,
]);

class _$KycRejectionReasonMeta {
  const _$KycRejectionReasonMeta();
  KycRejectionReason get DOCUMENT_UNREADABLE => _$DOCUMENT_UNREADABLE;
  KycRejectionReason get DOCUMENT_EXPIRED => _$DOCUMENT_EXPIRED;
  KycRejectionReason get DATA_MISMATCH => _$DATA_MISMATCH;
  KycRejectionReason get SELFIE_MISMATCH => _$SELFIE_MISMATCH;
  KycRejectionReason get OTHER => _$OTHER;
  KycRejectionReason valueOf(String name) => _$valueOf(name);
  BuiltSet<KycRejectionReason> get values => _$values;
}

abstract class _$KycRejectionReasonMixin {
  // ignore: non_constant_identifier_names
  _$KycRejectionReasonMeta get KycRejectionReason =>
      const _$KycRejectionReasonMeta();
}

Serializer<KycRejectionReason> _$kycRejectionReasonSerializer =
    _$KycRejectionReasonSerializer();

class _$KycRejectionReasonSerializer
    implements PrimitiveSerializer<KycRejectionReason> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DOCUMENT_UNREADABLE': 'DOCUMENT_UNREADABLE',
    'DOCUMENT_EXPIRED': 'DOCUMENT_EXPIRED',
    'DATA_MISMATCH': 'DATA_MISMATCH',
    'SELFIE_MISMATCH': 'SELFIE_MISMATCH',
    'OTHER': 'OTHER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DOCUMENT_UNREADABLE': 'DOCUMENT_UNREADABLE',
    'DOCUMENT_EXPIRED': 'DOCUMENT_EXPIRED',
    'DATA_MISMATCH': 'DATA_MISMATCH',
    'SELFIE_MISMATCH': 'SELFIE_MISMATCH',
    'OTHER': 'OTHER',
  };

  @override
  final Iterable<Type> types = const <Type>[KycRejectionReason];
  @override
  final String wireName = 'KycRejectionReason';

  @override
  Object serialize(Serializers serializers, KycRejectionReason object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  KycRejectionReason deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      KycRejectionReason.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
