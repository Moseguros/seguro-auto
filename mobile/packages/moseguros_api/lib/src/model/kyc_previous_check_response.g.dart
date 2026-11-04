// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_previous_check_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$KycPreviousCheckResponse extends KycPreviousCheckResponse {
  @override
  final String id;
  @override
  final KycRejectionReason? rejectionReasonCode;
  @override
  final KycCheckStatus status;
  @override
  final DateTime submittedAt;

  factory _$KycPreviousCheckResponse(
          [void Function(KycPreviousCheckResponseBuilder)? updates]) =>
      (KycPreviousCheckResponseBuilder()..update(updates))._build();

  _$KycPreviousCheckResponse._(
      {required this.id,
      this.rejectionReasonCode,
      required this.status,
      required this.submittedAt})
      : super._();
  @override
  KycPreviousCheckResponse rebuild(
          void Function(KycPreviousCheckResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  KycPreviousCheckResponseBuilder toBuilder() =>
      KycPreviousCheckResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is KycPreviousCheckResponse &&
        id == other.id &&
        rejectionReasonCode == other.rejectionReasonCode &&
        status == other.status &&
        submittedAt == other.submittedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, rejectionReasonCode.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'KycPreviousCheckResponse')
          ..add('id', id)
          ..add('rejectionReasonCode', rejectionReasonCode)
          ..add('status', status)
          ..add('submittedAt', submittedAt))
        .toString();
  }
}

class KycPreviousCheckResponseBuilder
    implements
        Builder<KycPreviousCheckResponse, KycPreviousCheckResponseBuilder> {
  _$KycPreviousCheckResponse? _$v;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  KycRejectionReason? _rejectionReasonCode;
  KycRejectionReason? get rejectionReasonCode => _$this._rejectionReasonCode;
  set rejectionReasonCode(KycRejectionReason? rejectionReasonCode) =>
      _$this._rejectionReasonCode = rejectionReasonCode;

  KycCheckStatus? _status;
  KycCheckStatus? get status => _$this._status;
  set status(KycCheckStatus? status) => _$this._status = status;

  DateTime? _submittedAt;
  DateTime? get submittedAt => _$this._submittedAt;
  set submittedAt(DateTime? submittedAt) => _$this._submittedAt = submittedAt;

  KycPreviousCheckResponseBuilder() {
    KycPreviousCheckResponse._defaults(this);
  }

  KycPreviousCheckResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _id = $v.id;
      _rejectionReasonCode = $v.rejectionReasonCode;
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(KycPreviousCheckResponse other) {
    _$v = other as _$KycPreviousCheckResponse;
  }

  @override
  void update(void Function(KycPreviousCheckResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  KycPreviousCheckResponse build() => _build();

  _$KycPreviousCheckResponse _build() {
    final _$result = _$v ??
        _$KycPreviousCheckResponse._(
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'KycPreviousCheckResponse', 'id'),
          rejectionReasonCode: rejectionReasonCode,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'KycPreviousCheckResponse', 'status'),
          submittedAt: BuiltValueNullFieldError.checkNotNull(
              submittedAt, r'KycPreviousCheckResponse', 'submittedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
