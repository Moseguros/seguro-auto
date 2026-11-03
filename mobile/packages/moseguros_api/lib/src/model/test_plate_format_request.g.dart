// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'test_plate_format_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$TestPlateFormatRequest extends TestPlateFormatRequest {
  @override
  final String displayTemplate;
  @override
  final String pattern;
  @override
  final String value;

  factory _$TestPlateFormatRequest(
          [void Function(TestPlateFormatRequestBuilder)? updates]) =>
      (TestPlateFormatRequestBuilder()..update(updates))._build();

  _$TestPlateFormatRequest._(
      {required this.displayTemplate,
      required this.pattern,
      required this.value})
      : super._();
  @override
  TestPlateFormatRequest rebuild(
          void Function(TestPlateFormatRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  TestPlateFormatRequestBuilder toBuilder() =>
      TestPlateFormatRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is TestPlateFormatRequest &&
        displayTemplate == other.displayTemplate &&
        pattern == other.pattern &&
        value == other.value;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayTemplate.hashCode);
    _$hash = $jc(_$hash, pattern.hashCode);
    _$hash = $jc(_$hash, value.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'TestPlateFormatRequest')
          ..add('displayTemplate', displayTemplate)
          ..add('pattern', pattern)
          ..add('value', value))
        .toString();
  }
}

class TestPlateFormatRequestBuilder
    implements Builder<TestPlateFormatRequest, TestPlateFormatRequestBuilder> {
  _$TestPlateFormatRequest? _$v;

  String? _displayTemplate;
  String? get displayTemplate => _$this._displayTemplate;
  set displayTemplate(String? displayTemplate) =>
      _$this._displayTemplate = displayTemplate;

  String? _pattern;
  String? get pattern => _$this._pattern;
  set pattern(String? pattern) => _$this._pattern = pattern;

  String? _value;
  String? get value => _$this._value;
  set value(String? value) => _$this._value = value;

  TestPlateFormatRequestBuilder() {
    TestPlateFormatRequest._defaults(this);
  }

  TestPlateFormatRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayTemplate = $v.displayTemplate;
      _pattern = $v.pattern;
      _value = $v.value;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(TestPlateFormatRequest other) {
    _$v = other as _$TestPlateFormatRequest;
  }

  @override
  void update(void Function(TestPlateFormatRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  TestPlateFormatRequest build() => _build();

  _$TestPlateFormatRequest _build() {
    final _$result = _$v ??
        _$TestPlateFormatRequest._(
          displayTemplate: BuiltValueNullFieldError.checkNotNull(
              displayTemplate, r'TestPlateFormatRequest', 'displayTemplate'),
          pattern: BuiltValueNullFieldError.checkNotNull(
              pattern, r'TestPlateFormatRequest', 'pattern'),
          value: BuiltValueNullFieldError.checkNotNull(
              value, r'TestPlateFormatRequest', 'value'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
