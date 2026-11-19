// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_queue_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentQueueStatus extends PaymentQueueStatus {
  @override
  final int consumers;
  @override
  final int messages;
  @override
  final String name;

  factory _$PaymentQueueStatus(
          [void Function(PaymentQueueStatusBuilder)? updates]) =>
      (PaymentQueueStatusBuilder()..update(updates))._build();

  _$PaymentQueueStatus._(
      {required this.consumers, required this.messages, required this.name})
      : super._();
  @override
  PaymentQueueStatus rebuild(
          void Function(PaymentQueueStatusBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentQueueStatusBuilder toBuilder() =>
      PaymentQueueStatusBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentQueueStatus &&
        consumers == other.consumers &&
        messages == other.messages &&
        name == other.name;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, consumers.hashCode);
    _$hash = $jc(_$hash, messages.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentQueueStatus')
          ..add('consumers', consumers)
          ..add('messages', messages)
          ..add('name', name))
        .toString();
  }
}

class PaymentQueueStatusBuilder
    implements Builder<PaymentQueueStatus, PaymentQueueStatusBuilder> {
  _$PaymentQueueStatus? _$v;

  int? _consumers;
  int? get consumers => _$this._consumers;
  set consumers(int? consumers) => _$this._consumers = consumers;

  int? _messages;
  int? get messages => _$this._messages;
  set messages(int? messages) => _$this._messages = messages;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  PaymentQueueStatusBuilder() {
    PaymentQueueStatus._defaults(this);
  }

  PaymentQueueStatusBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _consumers = $v.consumers;
      _messages = $v.messages;
      _name = $v.name;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentQueueStatus other) {
    _$v = other as _$PaymentQueueStatus;
  }

  @override
  void update(void Function(PaymentQueueStatusBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentQueueStatus build() => _build();

  _$PaymentQueueStatus _build() {
    final _$result = _$v ??
        _$PaymentQueueStatus._(
          consumers: BuiltValueNullFieldError.checkNotNull(
              consumers, r'PaymentQueueStatus', 'consumers'),
          messages: BuiltValueNullFieldError.checkNotNull(
              messages, r'PaymentQueueStatus', 'messages'),
          name: BuiltValueNullFieldError.checkNotNull(
              name, r'PaymentQueueStatus', 'name'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
