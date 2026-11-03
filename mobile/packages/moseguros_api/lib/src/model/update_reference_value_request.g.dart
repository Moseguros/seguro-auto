// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_reference_value_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateReferenceValueRequest extends UpdateReferenceValueRequest {
  @override
  final String? labelEn;
  @override
  final String labelPt;
  @override
  final int? sortOrder;
  @override
  final int version;

  factory _$UpdateReferenceValueRequest(
          [void Function(UpdateReferenceValueRequestBuilder)? updates]) =>
      (UpdateReferenceValueRequestBuilder()..update(updates))._build();

  _$UpdateReferenceValueRequest._(
      {this.labelEn,
      required this.labelPt,
      this.sortOrder,
      required this.version})
      : super._();
  @override
  UpdateReferenceValueRequest rebuild(
          void Function(UpdateReferenceValueRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateReferenceValueRequestBuilder toBuilder() =>
      UpdateReferenceValueRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateReferenceValueRequest &&
        labelEn == other.labelEn &&
        labelPt == other.labelPt &&
        sortOrder == other.sortOrder &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, labelEn.hashCode);
    _$hash = $jc(_$hash, labelPt.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateReferenceValueRequest')
          ..add('labelEn', labelEn)
          ..add('labelPt', labelPt)
          ..add('sortOrder', sortOrder)
          ..add('version', version))
        .toString();
  }
}

class UpdateReferenceValueRequestBuilder
    implements
        Builder<UpdateReferenceValueRequest,
            UpdateReferenceValueRequestBuilder> {
  _$UpdateReferenceValueRequest? _$v;

  String? _labelEn;
  String? get labelEn => _$this._labelEn;
  set labelEn(String? labelEn) => _$this._labelEn = labelEn;

  String? _labelPt;
  String? get labelPt => _$this._labelPt;
  set labelPt(String? labelPt) => _$this._labelPt = labelPt;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateReferenceValueRequestBuilder() {
    UpdateReferenceValueRequest._defaults(this);
  }

  UpdateReferenceValueRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _labelEn = $v.labelEn;
      _labelPt = $v.labelPt;
      _sortOrder = $v.sortOrder;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateReferenceValueRequest other) {
    _$v = other as _$UpdateReferenceValueRequest;
  }

  @override
  void update(void Function(UpdateReferenceValueRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateReferenceValueRequest build() => _build();

  _$UpdateReferenceValueRequest _build() {
    final _$result = _$v ??
        _$UpdateReferenceValueRequest._(
          labelEn: labelEn,
          labelPt: BuiltValueNullFieldError.checkNotNull(
              labelPt, r'UpdateReferenceValueRequest', 'labelPt'),
          sortOrder: sortOrder,
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdateReferenceValueRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
