// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'request_on_site_inspection_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RequestOnSiteInspectionRequest extends RequestOnSiteInspectionRequest {
  @override
  final String notes;
  @override
  final int version;

  factory _$RequestOnSiteInspectionRequest(
          [void Function(RequestOnSiteInspectionRequestBuilder)? updates]) =>
      (RequestOnSiteInspectionRequestBuilder()..update(updates))._build();

  _$RequestOnSiteInspectionRequest._(
      {required this.notes, required this.version})
      : super._();
  @override
  RequestOnSiteInspectionRequest rebuild(
          void Function(RequestOnSiteInspectionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RequestOnSiteInspectionRequestBuilder toBuilder() =>
      RequestOnSiteInspectionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RequestOnSiteInspectionRequest &&
        notes == other.notes &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, notes.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RequestOnSiteInspectionRequest')
          ..add('notes', notes)
          ..add('version', version))
        .toString();
  }
}

class RequestOnSiteInspectionRequestBuilder
    implements
        Builder<RequestOnSiteInspectionRequest,
            RequestOnSiteInspectionRequestBuilder> {
  _$RequestOnSiteInspectionRequest? _$v;

  String? _notes;
  String? get notes => _$this._notes;
  set notes(String? notes) => _$this._notes = notes;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RequestOnSiteInspectionRequestBuilder() {
    RequestOnSiteInspectionRequest._defaults(this);
  }

  RequestOnSiteInspectionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _notes = $v.notes;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RequestOnSiteInspectionRequest other) {
    _$v = other as _$RequestOnSiteInspectionRequest;
  }

  @override
  void update(void Function(RequestOnSiteInspectionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RequestOnSiteInspectionRequest build() => _build();

  _$RequestOnSiteInspectionRequest _build() {
    final _$result = _$v ??
        _$RequestOnSiteInspectionRequest._(
          notes: BuiltValueNullFieldError.checkNotNull(
              notes, r'RequestOnSiteInspectionRequest', 'notes'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'RequestOnSiteInspectionRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
