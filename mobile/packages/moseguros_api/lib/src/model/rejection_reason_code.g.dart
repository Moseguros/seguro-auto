// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rejection_reason_code.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RejectionReasonCode _$RISK_NOT_ACCEPTED =
    const RejectionReasonCode._('RISK_NOT_ACCEPTED');
const RejectionReasonCode _$KYC_FAILED =
    const RejectionReasonCode._('KYC_FAILED');
const RejectionReasonCode _$INSPECTION_FAILED =
    const RejectionReasonCode._('INSPECTION_FAILED');
const RejectionReasonCode _$FALSE_DECLARATION =
    const RejectionReasonCode._('FALSE_DECLARATION');
const RejectionReasonCode _$DOCUMENTS_INVALID =
    const RejectionReasonCode._('DOCUMENTS_INVALID');
const RejectionReasonCode _$OTHER = const RejectionReasonCode._('OTHER');

RejectionReasonCode _$valueOf(String name) {
  switch (name) {
    case 'RISK_NOT_ACCEPTED':
      return _$RISK_NOT_ACCEPTED;
    case 'KYC_FAILED':
      return _$KYC_FAILED;
    case 'INSPECTION_FAILED':
      return _$INSPECTION_FAILED;
    case 'FALSE_DECLARATION':
      return _$FALSE_DECLARATION;
    case 'DOCUMENTS_INVALID':
      return _$DOCUMENTS_INVALID;
    case 'OTHER':
      return _$OTHER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RejectionReasonCode> _$values =
    BuiltSet<RejectionReasonCode>(const <RejectionReasonCode>[
  _$RISK_NOT_ACCEPTED,
  _$KYC_FAILED,
  _$INSPECTION_FAILED,
  _$FALSE_DECLARATION,
  _$DOCUMENTS_INVALID,
  _$OTHER,
]);

class _$RejectionReasonCodeMeta {
  const _$RejectionReasonCodeMeta();
  RejectionReasonCode get RISK_NOT_ACCEPTED => _$RISK_NOT_ACCEPTED;
  RejectionReasonCode get KYC_FAILED => _$KYC_FAILED;
  RejectionReasonCode get INSPECTION_FAILED => _$INSPECTION_FAILED;
  RejectionReasonCode get FALSE_DECLARATION => _$FALSE_DECLARATION;
  RejectionReasonCode get DOCUMENTS_INVALID => _$DOCUMENTS_INVALID;
  RejectionReasonCode get OTHER => _$OTHER;
  RejectionReasonCode valueOf(String name) => _$valueOf(name);
  BuiltSet<RejectionReasonCode> get values => _$values;
}

abstract class _$RejectionReasonCodeMixin {
  // ignore: non_constant_identifier_names
  _$RejectionReasonCodeMeta get RejectionReasonCode =>
      const _$RejectionReasonCodeMeta();
}

Serializer<RejectionReasonCode> _$rejectionReasonCodeSerializer =
    _$RejectionReasonCodeSerializer();

class _$RejectionReasonCodeSerializer
    implements PrimitiveSerializer<RejectionReasonCode> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'RISK_NOT_ACCEPTED': 'RISK_NOT_ACCEPTED',
    'KYC_FAILED': 'KYC_FAILED',
    'INSPECTION_FAILED': 'INSPECTION_FAILED',
    'FALSE_DECLARATION': 'FALSE_DECLARATION',
    'DOCUMENTS_INVALID': 'DOCUMENTS_INVALID',
    'OTHER': 'OTHER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'RISK_NOT_ACCEPTED': 'RISK_NOT_ACCEPTED',
    'KYC_FAILED': 'KYC_FAILED',
    'INSPECTION_FAILED': 'INSPECTION_FAILED',
    'FALSE_DECLARATION': 'FALSE_DECLARATION',
    'DOCUMENTS_INVALID': 'DOCUMENTS_INVALID',
    'OTHER': 'OTHER',
  };

  @override
  final Iterable<Type> types = const <Type>[RejectionReasonCode];
  @override
  final String wireName = 'RejectionReasonCode';

  @override
  Object serialize(Serializers serializers, RejectionReasonCode object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RejectionReasonCode deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RejectionReasonCode.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
