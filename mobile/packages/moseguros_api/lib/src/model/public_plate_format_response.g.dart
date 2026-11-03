// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_plate_format_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PublicPlateFormatResponse extends PublicPlateFormatResponse {
  @override
  final String code;
  @override
  final String example;
  @override
  final String label;

  factory _$PublicPlateFormatResponse(
          [void Function(PublicPlateFormatResponseBuilder)? updates]) =>
      (PublicPlateFormatResponseBuilder()..update(updates))._build();

  _$PublicPlateFormatResponse._(
      {required this.code, required this.example, required this.label})
      : super._();
  @override
  PublicPlateFormatResponse rebuild(
          void Function(PublicPlateFormatResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublicPlateFormatResponseBuilder toBuilder() =>
      PublicPlateFormatResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublicPlateFormatResponse &&
        code == other.code &&
        example == other.example &&
        label == other.label;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, example.hashCode);
    _$hash = $jc(_$hash, label.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PublicPlateFormatResponse')
          ..add('code', code)
          ..add('example', example)
          ..add('label', label))
        .toString();
  }
}

class PublicPlateFormatResponseBuilder
    implements
        Builder<PublicPlateFormatResponse, PublicPlateFormatResponseBuilder> {
  _$PublicPlateFormatResponse? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _example;
  String? get example => _$this._example;
  set example(String? example) => _$this._example = example;

  String? _label;
  String? get label => _$this._label;
  set label(String? label) => _$this._label = label;

  PublicPlateFormatResponseBuilder() {
    PublicPlateFormatResponse._defaults(this);
  }

  PublicPlateFormatResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _example = $v.example;
      _label = $v.label;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublicPlateFormatResponse other) {
    _$v = other as _$PublicPlateFormatResponse;
  }

  @override
  void update(void Function(PublicPlateFormatResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublicPlateFormatResponse build() => _build();

  _$PublicPlateFormatResponse _build() {
    final _$result = _$v ??
        _$PublicPlateFormatResponse._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'PublicPlateFormatResponse', 'code'),
          example: BuiltValueNullFieldError.checkNotNull(
              example, r'PublicPlateFormatResponse', 'example'),
          label: BuiltValueNullFieldError.checkNotNull(
              label, r'PublicPlateFormatResponse', 'label'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
