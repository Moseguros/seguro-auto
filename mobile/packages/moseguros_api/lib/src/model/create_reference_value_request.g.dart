// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_reference_value_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CreateReferenceValueRequest extends CreateReferenceValueRequest {
  @override
  final String code;
  @override
  final String? labelEn;
  @override
  final String labelPt;
  @override
  final String? parentCode;
  @override
  final int? sortOrder;

  factory _$CreateReferenceValueRequest(
          [void Function(CreateReferenceValueRequestBuilder)? updates]) =>
      (CreateReferenceValueRequestBuilder()..update(updates))._build();

  _$CreateReferenceValueRequest._(
      {required this.code,
      this.labelEn,
      required this.labelPt,
      this.parentCode,
      this.sortOrder})
      : super._();
  @override
  CreateReferenceValueRequest rebuild(
          void Function(CreateReferenceValueRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreateReferenceValueRequestBuilder toBuilder() =>
      CreateReferenceValueRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreateReferenceValueRequest &&
        code == other.code &&
        labelEn == other.labelEn &&
        labelPt == other.labelPt &&
        parentCode == other.parentCode &&
        sortOrder == other.sortOrder;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, labelEn.hashCode);
    _$hash = $jc(_$hash, labelPt.hashCode);
    _$hash = $jc(_$hash, parentCode.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreateReferenceValueRequest')
          ..add('code', code)
          ..add('labelEn', labelEn)
          ..add('labelPt', labelPt)
          ..add('parentCode', parentCode)
          ..add('sortOrder', sortOrder))
        .toString();
  }
}

class CreateReferenceValueRequestBuilder
    implements
        Builder<CreateReferenceValueRequest,
            CreateReferenceValueRequestBuilder> {
  _$CreateReferenceValueRequest? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _labelEn;
  String? get labelEn => _$this._labelEn;
  set labelEn(String? labelEn) => _$this._labelEn = labelEn;

  String? _labelPt;
  String? get labelPt => _$this._labelPt;
  set labelPt(String? labelPt) => _$this._labelPt = labelPt;

  String? _parentCode;
  String? get parentCode => _$this._parentCode;
  set parentCode(String? parentCode) => _$this._parentCode = parentCode;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  CreateReferenceValueRequestBuilder() {
    CreateReferenceValueRequest._defaults(this);
  }

  CreateReferenceValueRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _labelEn = $v.labelEn;
      _labelPt = $v.labelPt;
      _parentCode = $v.parentCode;
      _sortOrder = $v.sortOrder;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreateReferenceValueRequest other) {
    _$v = other as _$CreateReferenceValueRequest;
  }

  @override
  void update(void Function(CreateReferenceValueRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreateReferenceValueRequest build() => _build();

  _$CreateReferenceValueRequest _build() {
    final _$result = _$v ??
        _$CreateReferenceValueRequest._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'CreateReferenceValueRequest', 'code'),
          labelEn: labelEn,
          labelPt: BuiltValueNullFieldError.checkNotNull(
              labelPt, r'CreateReferenceValueRequest', 'labelPt'),
          parentCode: parentCode,
          sortOrder: sortOrder,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
