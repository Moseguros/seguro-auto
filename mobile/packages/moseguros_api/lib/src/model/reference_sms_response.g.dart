// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reference_sms_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ReferenceSmsResponse extends ReferenceSmsResponse {
  @override
  final String sentTo;

  factory _$ReferenceSmsResponse(
          [void Function(ReferenceSmsResponseBuilder)? updates]) =>
      (ReferenceSmsResponseBuilder()..update(updates))._build();

  _$ReferenceSmsResponse._({required this.sentTo}) : super._();
  @override
  ReferenceSmsResponse rebuild(
          void Function(ReferenceSmsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ReferenceSmsResponseBuilder toBuilder() =>
      ReferenceSmsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ReferenceSmsResponse && sentTo == other.sentTo;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, sentTo.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ReferenceSmsResponse')
          ..add('sentTo', sentTo))
        .toString();
  }
}

class ReferenceSmsResponseBuilder
    implements Builder<ReferenceSmsResponse, ReferenceSmsResponseBuilder> {
  _$ReferenceSmsResponse? _$v;

  String? _sentTo;
  String? get sentTo => _$this._sentTo;
  set sentTo(String? sentTo) => _$this._sentTo = sentTo;

  ReferenceSmsResponseBuilder() {
    ReferenceSmsResponse._defaults(this);
  }

  ReferenceSmsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _sentTo = $v.sentTo;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ReferenceSmsResponse other) {
    _$v = other as _$ReferenceSmsResponse;
  }

  @override
  void update(void Function(ReferenceSmsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ReferenceSmsResponse build() => _build();

  _$ReferenceSmsResponse _build() {
    final _$result = _$v ??
        _$ReferenceSmsResponse._(
          sentTo: BuiltValueNullFieldError.checkNotNull(
              sentTo, r'ReferenceSmsResponse', 'sentTo'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
