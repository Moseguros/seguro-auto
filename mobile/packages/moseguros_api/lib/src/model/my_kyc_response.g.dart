// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'my_kyc_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$MyKycResponse extends MyKycResponse {
  @override
  final KycCheckResponse? check;
  @override
  final KycState state;

  factory _$MyKycResponse([void Function(MyKycResponseBuilder)? updates]) =>
      (MyKycResponseBuilder()..update(updates))._build();

  _$MyKycResponse._({this.check, required this.state}) : super._();
  @override
  MyKycResponse rebuild(void Function(MyKycResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  MyKycResponseBuilder toBuilder() => MyKycResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is MyKycResponse &&
        check == other.check &&
        state == other.state;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, check.hashCode);
    _$hash = $jc(_$hash, state.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'MyKycResponse')
          ..add('check', check)
          ..add('state', state))
        .toString();
  }
}

class MyKycResponseBuilder
    implements Builder<MyKycResponse, MyKycResponseBuilder> {
  _$MyKycResponse? _$v;

  KycCheckResponseBuilder? _check;
  KycCheckResponseBuilder get check =>
      _$this._check ??= KycCheckResponseBuilder();
  set check(KycCheckResponseBuilder? check) => _$this._check = check;

  KycState? _state;
  KycState? get state => _$this._state;
  set state(KycState? state) => _$this._state = state;

  MyKycResponseBuilder() {
    MyKycResponse._defaults(this);
  }

  MyKycResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _check = $v.check?.toBuilder();
      _state = $v.state;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(MyKycResponse other) {
    _$v = other as _$MyKycResponse;
  }

  @override
  void update(void Function(MyKycResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  MyKycResponse build() => _build();

  _$MyKycResponse _build() {
    _$MyKycResponse _$result;
    try {
      _$result = _$v ??
          _$MyKycResponse._(
            check: _check?.build(),
            state: BuiltValueNullFieldError.checkNotNull(
                state, r'MyKycResponse', 'state'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'check';
        _check?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'MyKycResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
