// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reference_sms_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ReferenceSmsRequest extends ReferenceSmsRequest {
  @override
  final String msisdn;

  factory _$ReferenceSmsRequest(
          [void Function(ReferenceSmsRequestBuilder)? updates]) =>
      (ReferenceSmsRequestBuilder()..update(updates))._build();

  _$ReferenceSmsRequest._({required this.msisdn}) : super._();
  @override
  ReferenceSmsRequest rebuild(
          void Function(ReferenceSmsRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ReferenceSmsRequestBuilder toBuilder() =>
      ReferenceSmsRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ReferenceSmsRequest && msisdn == other.msisdn;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, msisdn.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ReferenceSmsRequest')
          ..add('msisdn', msisdn))
        .toString();
  }
}

class ReferenceSmsRequestBuilder
    implements Builder<ReferenceSmsRequest, ReferenceSmsRequestBuilder> {
  _$ReferenceSmsRequest? _$v;

  String? _msisdn;
  String? get msisdn => _$this._msisdn;
  set msisdn(String? msisdn) => _$this._msisdn = msisdn;

  ReferenceSmsRequestBuilder() {
    ReferenceSmsRequest._defaults(this);
  }

  ReferenceSmsRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _msisdn = $v.msisdn;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ReferenceSmsRequest other) {
    _$v = other as _$ReferenceSmsRequest;
  }

  @override
  void update(void Function(ReferenceSmsRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ReferenceSmsRequest build() => _build();

  _$ReferenceSmsRequest _build() {
    final _$result = _$v ??
        _$ReferenceSmsRequest._(
          msisdn: BuiltValueNullFieldError.checkNotNull(
              msisdn, r'ReferenceSmsRequest', 'msisdn'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
