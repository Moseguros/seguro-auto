// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reject_inspection_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RejectInspectionRequest extends RejectInspectionRequest {
  @override
  final String reason;
  @override
  final BuiltList<PhotoSlot>? retakeSlots;
  @override
  final int version;

  factory _$RejectInspectionRequest(
          [void Function(RejectInspectionRequestBuilder)? updates]) =>
      (RejectInspectionRequestBuilder()..update(updates))._build();

  _$RejectInspectionRequest._(
      {required this.reason, this.retakeSlots, required this.version})
      : super._();
  @override
  RejectInspectionRequest rebuild(
          void Function(RejectInspectionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RejectInspectionRequestBuilder toBuilder() =>
      RejectInspectionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RejectInspectionRequest &&
        reason == other.reason &&
        retakeSlots == other.retakeSlots &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jc(_$hash, retakeSlots.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RejectInspectionRequest')
          ..add('reason', reason)
          ..add('retakeSlots', retakeSlots)
          ..add('version', version))
        .toString();
  }
}

class RejectInspectionRequestBuilder
    implements
        Builder<RejectInspectionRequest, RejectInspectionRequestBuilder> {
  _$RejectInspectionRequest? _$v;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  ListBuilder<PhotoSlot>? _retakeSlots;
  ListBuilder<PhotoSlot> get retakeSlots =>
      _$this._retakeSlots ??= ListBuilder<PhotoSlot>();
  set retakeSlots(ListBuilder<PhotoSlot>? retakeSlots) =>
      _$this._retakeSlots = retakeSlots;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RejectInspectionRequestBuilder() {
    RejectInspectionRequest._defaults(this);
  }

  RejectInspectionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _reason = $v.reason;
      _retakeSlots = $v.retakeSlots?.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RejectInspectionRequest other) {
    _$v = other as _$RejectInspectionRequest;
  }

  @override
  void update(void Function(RejectInspectionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RejectInspectionRequest build() => _build();

  _$RejectInspectionRequest _build() {
    _$RejectInspectionRequest _$result;
    try {
      _$result = _$v ??
          _$RejectInspectionRequest._(
            reason: BuiltValueNullFieldError.checkNotNull(
                reason, r'RejectInspectionRequest', 'reason'),
            retakeSlots: _retakeSlots?.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'RejectInspectionRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'retakeSlots';
        _retakeSlots?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RejectInspectionRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
