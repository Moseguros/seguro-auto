// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_link_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentLinkResponse extends PaymentLinkResponse {
  @override
  final DateTime sentAt;
  @override
  final String sentTo;

  factory _$PaymentLinkResponse(
          [void Function(PaymentLinkResponseBuilder)? updates]) =>
      (PaymentLinkResponseBuilder()..update(updates))._build();

  _$PaymentLinkResponse._({required this.sentAt, required this.sentTo})
      : super._();
  @override
  PaymentLinkResponse rebuild(
          void Function(PaymentLinkResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentLinkResponseBuilder toBuilder() =>
      PaymentLinkResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentLinkResponse &&
        sentAt == other.sentAt &&
        sentTo == other.sentTo;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, sentAt.hashCode);
    _$hash = $jc(_$hash, sentTo.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentLinkResponse')
          ..add('sentAt', sentAt)
          ..add('sentTo', sentTo))
        .toString();
  }
}

class PaymentLinkResponseBuilder
    implements Builder<PaymentLinkResponse, PaymentLinkResponseBuilder> {
  _$PaymentLinkResponse? _$v;

  DateTime? _sentAt;
  DateTime? get sentAt => _$this._sentAt;
  set sentAt(DateTime? sentAt) => _$this._sentAt = sentAt;

  String? _sentTo;
  String? get sentTo => _$this._sentTo;
  set sentTo(String? sentTo) => _$this._sentTo = sentTo;

  PaymentLinkResponseBuilder() {
    PaymentLinkResponse._defaults(this);
  }

  PaymentLinkResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _sentAt = $v.sentAt;
      _sentTo = $v.sentTo;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentLinkResponse other) {
    _$v = other as _$PaymentLinkResponse;
  }

  @override
  void update(void Function(PaymentLinkResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentLinkResponse build() => _build();

  _$PaymentLinkResponse _build() {
    final _$result = _$v ??
        _$PaymentLinkResponse._(
          sentAt: BuiltValueNullFieldError.checkNotNull(
              sentAt, r'PaymentLinkResponse', 'sentAt'),
          sentTo: BuiltValueNullFieldError.checkNotNull(
              sentTo, r'PaymentLinkResponse', 'sentTo'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
