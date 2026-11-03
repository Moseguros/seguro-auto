// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'test_plate_format_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$TestPlateFormatResponse extends TestPlateFormatResponse {
  @override
  final String? formatted;
  @override
  final bool matches;

  factory _$TestPlateFormatResponse(
          [void Function(TestPlateFormatResponseBuilder)? updates]) =>
      (TestPlateFormatResponseBuilder()..update(updates))._build();

  _$TestPlateFormatResponse._({this.formatted, required this.matches})
      : super._();
  @override
  TestPlateFormatResponse rebuild(
          void Function(TestPlateFormatResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  TestPlateFormatResponseBuilder toBuilder() =>
      TestPlateFormatResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is TestPlateFormatResponse &&
        formatted == other.formatted &&
        matches == other.matches;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, formatted.hashCode);
    _$hash = $jc(_$hash, matches.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'TestPlateFormatResponse')
          ..add('formatted', formatted)
          ..add('matches', matches))
        .toString();
  }
}

class TestPlateFormatResponseBuilder
    implements
        Builder<TestPlateFormatResponse, TestPlateFormatResponseBuilder> {
  _$TestPlateFormatResponse? _$v;

  String? _formatted;
  String? get formatted => _$this._formatted;
  set formatted(String? formatted) => _$this._formatted = formatted;

  bool? _matches;
  bool? get matches => _$this._matches;
  set matches(bool? matches) => _$this._matches = matches;

  TestPlateFormatResponseBuilder() {
    TestPlateFormatResponse._defaults(this);
  }

  TestPlateFormatResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _formatted = $v.formatted;
      _matches = $v.matches;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(TestPlateFormatResponse other) {
    _$v = other as _$TestPlateFormatResponse;
  }

  @override
  void update(void Function(TestPlateFormatResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  TestPlateFormatResponse build() => _build();

  _$TestPlateFormatResponse _build() {
    final _$result = _$v ??
        _$TestPlateFormatResponse._(
          formatted: formatted,
          matches: BuiltValueNullFieldError.checkNotNull(
              matches, r'TestPlateFormatResponse', 'matches'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
