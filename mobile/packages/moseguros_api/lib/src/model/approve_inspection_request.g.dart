// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'approve_inspection_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ApproveInspectionRequest extends ApproveInspectionRequest {
  @override
  final InspectionMode? mode;
  @override
  final String? notes;
  @override
  final int version;

  factory _$ApproveInspectionRequest(
          [void Function(ApproveInspectionRequestBuilder)? updates]) =>
      (ApproveInspectionRequestBuilder()..update(updates))._build();

  _$ApproveInspectionRequest._({this.mode, this.notes, required this.version})
      : super._();
  @override
  ApproveInspectionRequest rebuild(
          void Function(ApproveInspectionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ApproveInspectionRequestBuilder toBuilder() =>
      ApproveInspectionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ApproveInspectionRequest &&
        mode == other.mode &&
        notes == other.notes &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, mode.hashCode);
    _$hash = $jc(_$hash, notes.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ApproveInspectionRequest')
          ..add('mode', mode)
          ..add('notes', notes)
          ..add('version', version))
        .toString();
  }
}

class ApproveInspectionRequestBuilder
    implements
        Builder<ApproveInspectionRequest, ApproveInspectionRequestBuilder> {
  _$ApproveInspectionRequest? _$v;

  InspectionMode? _mode;
  InspectionMode? get mode => _$this._mode;
  set mode(InspectionMode? mode) => _$this._mode = mode;

  String? _notes;
  String? get notes => _$this._notes;
  set notes(String? notes) => _$this._notes = notes;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  ApproveInspectionRequestBuilder() {
    ApproveInspectionRequest._defaults(this);
  }

  ApproveInspectionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _mode = $v.mode;
      _notes = $v.notes;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ApproveInspectionRequest other) {
    _$v = other as _$ApproveInspectionRequest;
  }

  @override
  void update(void Function(ApproveInspectionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ApproveInspectionRequest build() => _build();

  _$ApproveInspectionRequest _build() {
    final _$result = _$v ??
        _$ApproveInspectionRequest._(
          mode: mode,
          notes: notes,
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'ApproveInspectionRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
