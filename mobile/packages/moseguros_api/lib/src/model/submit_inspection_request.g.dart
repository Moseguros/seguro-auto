// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'submit_inspection_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SubmitInspectionRequest extends SubmitInspectionRequest {
  @override
  final int odometerKm;
  @override
  final int version;

  factory _$SubmitInspectionRequest(
          [void Function(SubmitInspectionRequestBuilder)? updates]) =>
      (SubmitInspectionRequestBuilder()..update(updates))._build();

  _$SubmitInspectionRequest._({required this.odometerKm, required this.version})
      : super._();
  @override
  SubmitInspectionRequest rebuild(
          void Function(SubmitInspectionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SubmitInspectionRequestBuilder toBuilder() =>
      SubmitInspectionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SubmitInspectionRequest &&
        odometerKm == other.odometerKm &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, odometerKm.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SubmitInspectionRequest')
          ..add('odometerKm', odometerKm)
          ..add('version', version))
        .toString();
  }
}

class SubmitInspectionRequestBuilder
    implements
        Builder<SubmitInspectionRequest, SubmitInspectionRequestBuilder> {
  _$SubmitInspectionRequest? _$v;

  int? _odometerKm;
  int? get odometerKm => _$this._odometerKm;
  set odometerKm(int? odometerKm) => _$this._odometerKm = odometerKm;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  SubmitInspectionRequestBuilder() {
    SubmitInspectionRequest._defaults(this);
  }

  SubmitInspectionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _odometerKm = $v.odometerKm;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SubmitInspectionRequest other) {
    _$v = other as _$SubmitInspectionRequest;
  }

  @override
  void update(void Function(SubmitInspectionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SubmitInspectionRequest build() => _build();

  _$SubmitInspectionRequest _build() {
    final _$result = _$v ??
        _$SubmitInspectionRequest._(
          odometerKm: BuiltValueNullFieldError.checkNotNull(
              odometerKm, r'SubmitInspectionRequest', 'odometerKm'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'SubmitInspectionRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
