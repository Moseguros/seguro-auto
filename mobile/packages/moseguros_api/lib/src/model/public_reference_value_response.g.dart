// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_reference_value_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PublicReferenceValueResponse extends PublicReferenceValueResponse {
  @override
  final String code;
  @override
  final String? labelEn;
  @override
  final String labelPt;
  @override
  final String? parentCode;

  factory _$PublicReferenceValueResponse(
          [void Function(PublicReferenceValueResponseBuilder)? updates]) =>
      (PublicReferenceValueResponseBuilder()..update(updates))._build();

  _$PublicReferenceValueResponse._(
      {required this.code,
      this.labelEn,
      required this.labelPt,
      this.parentCode})
      : super._();
  @override
  PublicReferenceValueResponse rebuild(
          void Function(PublicReferenceValueResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublicReferenceValueResponseBuilder toBuilder() =>
      PublicReferenceValueResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublicReferenceValueResponse &&
        code == other.code &&
        labelEn == other.labelEn &&
        labelPt == other.labelPt &&
        parentCode == other.parentCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, labelEn.hashCode);
    _$hash = $jc(_$hash, labelPt.hashCode);
    _$hash = $jc(_$hash, parentCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PublicReferenceValueResponse')
          ..add('code', code)
          ..add('labelEn', labelEn)
          ..add('labelPt', labelPt)
          ..add('parentCode', parentCode))
        .toString();
  }
}

class PublicReferenceValueResponseBuilder
    implements
        Builder<PublicReferenceValueResponse,
            PublicReferenceValueResponseBuilder> {
  _$PublicReferenceValueResponse? _$v;

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

  PublicReferenceValueResponseBuilder() {
    PublicReferenceValueResponse._defaults(this);
  }

  PublicReferenceValueResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _labelEn = $v.labelEn;
      _labelPt = $v.labelPt;
      _parentCode = $v.parentCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublicReferenceValueResponse other) {
    _$v = other as _$PublicReferenceValueResponse;
  }

  @override
  void update(void Function(PublicReferenceValueResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublicReferenceValueResponse build() => _build();

  _$PublicReferenceValueResponse _build() {
    final _$result = _$v ??
        _$PublicReferenceValueResponse._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'PublicReferenceValueResponse', 'code'),
          labelEn: labelEn,
          labelPt: BuiltValueNullFieldError.checkNotNull(
              labelPt, r'PublicReferenceValueResponse', 'labelPt'),
          parentCode: parentCode,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
