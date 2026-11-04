// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reject_proposal_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RejectProposalRequestReasonCodeEnum
    _$rejectProposalRequestReasonCodeEnum_RISK_NOT_ACCEPTED =
    const RejectProposalRequestReasonCodeEnum._('RISK_NOT_ACCEPTED');
const RejectProposalRequestReasonCodeEnum
    _$rejectProposalRequestReasonCodeEnum_KYC_FAILED =
    const RejectProposalRequestReasonCodeEnum._('KYC_FAILED');
const RejectProposalRequestReasonCodeEnum
    _$rejectProposalRequestReasonCodeEnum_INSPECTION_FAILED =
    const RejectProposalRequestReasonCodeEnum._('INSPECTION_FAILED');
const RejectProposalRequestReasonCodeEnum
    _$rejectProposalRequestReasonCodeEnum_FALSE_DECLARATION =
    const RejectProposalRequestReasonCodeEnum._('FALSE_DECLARATION');
const RejectProposalRequestReasonCodeEnum
    _$rejectProposalRequestReasonCodeEnum_DOCUMENTS_INVALID =
    const RejectProposalRequestReasonCodeEnum._('DOCUMENTS_INVALID');
const RejectProposalRequestReasonCodeEnum
    _$rejectProposalRequestReasonCodeEnum_OTHER =
    const RejectProposalRequestReasonCodeEnum._('OTHER');

RejectProposalRequestReasonCodeEnum
    _$rejectProposalRequestReasonCodeEnumValueOf(String name) {
  switch (name) {
    case 'RISK_NOT_ACCEPTED':
      return _$rejectProposalRequestReasonCodeEnum_RISK_NOT_ACCEPTED;
    case 'KYC_FAILED':
      return _$rejectProposalRequestReasonCodeEnum_KYC_FAILED;
    case 'INSPECTION_FAILED':
      return _$rejectProposalRequestReasonCodeEnum_INSPECTION_FAILED;
    case 'FALSE_DECLARATION':
      return _$rejectProposalRequestReasonCodeEnum_FALSE_DECLARATION;
    case 'DOCUMENTS_INVALID':
      return _$rejectProposalRequestReasonCodeEnum_DOCUMENTS_INVALID;
    case 'OTHER':
      return _$rejectProposalRequestReasonCodeEnum_OTHER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RejectProposalRequestReasonCodeEnum>
    _$rejectProposalRequestReasonCodeEnumValues = BuiltSet<
        RejectProposalRequestReasonCodeEnum>(const <RejectProposalRequestReasonCodeEnum>[
  _$rejectProposalRequestReasonCodeEnum_RISK_NOT_ACCEPTED,
  _$rejectProposalRequestReasonCodeEnum_KYC_FAILED,
  _$rejectProposalRequestReasonCodeEnum_INSPECTION_FAILED,
  _$rejectProposalRequestReasonCodeEnum_FALSE_DECLARATION,
  _$rejectProposalRequestReasonCodeEnum_DOCUMENTS_INVALID,
  _$rejectProposalRequestReasonCodeEnum_OTHER,
]);

Serializer<RejectProposalRequestReasonCodeEnum>
    _$rejectProposalRequestReasonCodeEnumSerializer =
    _$RejectProposalRequestReasonCodeEnumSerializer();

class _$RejectProposalRequestReasonCodeEnumSerializer
    implements PrimitiveSerializer<RejectProposalRequestReasonCodeEnum> {
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
  final Iterable<Type> types = const <Type>[
    RejectProposalRequestReasonCodeEnum
  ];
  @override
  final String wireName = 'RejectProposalRequestReasonCodeEnum';

  @override
  Object serialize(
          Serializers serializers, RejectProposalRequestReasonCodeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RejectProposalRequestReasonCodeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RejectProposalRequestReasonCodeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RejectProposalRequest extends RejectProposalRequest {
  @override
  final String reason;
  @override
  final RejectProposalRequestReasonCodeEnum reasonCode;
  @override
  final int version;

  factory _$RejectProposalRequest(
          [void Function(RejectProposalRequestBuilder)? updates]) =>
      (RejectProposalRequestBuilder()..update(updates))._build();

  _$RejectProposalRequest._(
      {required this.reason, required this.reasonCode, required this.version})
      : super._();
  @override
  RejectProposalRequest rebuild(
          void Function(RejectProposalRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RejectProposalRequestBuilder toBuilder() =>
      RejectProposalRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RejectProposalRequest &&
        reason == other.reason &&
        reasonCode == other.reasonCode &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jc(_$hash, reasonCode.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RejectProposalRequest')
          ..add('reason', reason)
          ..add('reasonCode', reasonCode)
          ..add('version', version))
        .toString();
  }
}

class RejectProposalRequestBuilder
    implements Builder<RejectProposalRequest, RejectProposalRequestBuilder> {
  _$RejectProposalRequest? _$v;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  RejectProposalRequestReasonCodeEnum? _reasonCode;
  RejectProposalRequestReasonCodeEnum? get reasonCode => _$this._reasonCode;
  set reasonCode(RejectProposalRequestReasonCodeEnum? reasonCode) =>
      _$this._reasonCode = reasonCode;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RejectProposalRequestBuilder() {
    RejectProposalRequest._defaults(this);
  }

  RejectProposalRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _reason = $v.reason;
      _reasonCode = $v.reasonCode;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RejectProposalRequest other) {
    _$v = other as _$RejectProposalRequest;
  }

  @override
  void update(void Function(RejectProposalRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RejectProposalRequest build() => _build();

  _$RejectProposalRequest _build() {
    final _$result = _$v ??
        _$RejectProposalRequest._(
          reason: BuiltValueNullFieldError.checkNotNull(
              reason, r'RejectProposalRequest', 'reason'),
          reasonCode: BuiltValueNullFieldError.checkNotNull(
              reasonCode, r'RejectProposalRequest', 'reasonCode'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'RejectProposalRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
