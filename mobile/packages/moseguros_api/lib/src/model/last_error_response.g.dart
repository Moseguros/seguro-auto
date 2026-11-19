// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'last_error_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$LastErrorResponse extends LastErrorResponse {
  @override
  final DateTime? at;
  @override
  final String code;

  factory _$LastErrorResponse(
          [void Function(LastErrorResponseBuilder)? updates]) =>
      (LastErrorResponseBuilder()..update(updates))._build();

  _$LastErrorResponse._({this.at, required this.code}) : super._();
  @override
  LastErrorResponse rebuild(void Function(LastErrorResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  LastErrorResponseBuilder toBuilder() =>
      LastErrorResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is LastErrorResponse && at == other.at && code == other.code;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, at.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'LastErrorResponse')
          ..add('at', at)
          ..add('code', code))
        .toString();
  }
}

class LastErrorResponseBuilder
    implements Builder<LastErrorResponse, LastErrorResponseBuilder> {
  _$LastErrorResponse? _$v;

  DateTime? _at;
  DateTime? get at => _$this._at;
  set at(DateTime? at) => _$this._at = at;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  LastErrorResponseBuilder() {
    LastErrorResponse._defaults(this);
  }

  LastErrorResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _at = $v.at;
      _code = $v.code;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(LastErrorResponse other) {
    _$v = other as _$LastErrorResponse;
  }

  @override
  void update(void Function(LastErrorResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  LastErrorResponse build() => _build();

  _$LastErrorResponse _build() {
    final _$result = _$v ??
        _$LastErrorResponse._(
          at: at,
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'LastErrorResponse', 'code'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
