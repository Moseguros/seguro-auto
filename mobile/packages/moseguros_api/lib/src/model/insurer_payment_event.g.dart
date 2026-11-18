// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_payment_event.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerPaymentEvent extends InsurerPaymentEvent {
  @override
  final PaymentStatus? fromStatus;
  @override
  final DateTime receivedAt;
  @override
  final PaymentStatus? toStatus;
  @override
  final PaymentEventType type;

  factory _$InsurerPaymentEvent(
          [void Function(InsurerPaymentEventBuilder)? updates]) =>
      (InsurerPaymentEventBuilder()..update(updates))._build();

  _$InsurerPaymentEvent._(
      {this.fromStatus,
      required this.receivedAt,
      this.toStatus,
      required this.type})
      : super._();
  @override
  InsurerPaymentEvent rebuild(
          void Function(InsurerPaymentEventBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerPaymentEventBuilder toBuilder() =>
      InsurerPaymentEventBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerPaymentEvent &&
        fromStatus == other.fromStatus &&
        receivedAt == other.receivedAt &&
        toStatus == other.toStatus &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fromStatus.hashCode);
    _$hash = $jc(_$hash, receivedAt.hashCode);
    _$hash = $jc(_$hash, toStatus.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerPaymentEvent')
          ..add('fromStatus', fromStatus)
          ..add('receivedAt', receivedAt)
          ..add('toStatus', toStatus)
          ..add('type', type))
        .toString();
  }
}

class InsurerPaymentEventBuilder
    implements Builder<InsurerPaymentEvent, InsurerPaymentEventBuilder> {
  _$InsurerPaymentEvent? _$v;

  PaymentStatus? _fromStatus;
  PaymentStatus? get fromStatus => _$this._fromStatus;
  set fromStatus(PaymentStatus? fromStatus) => _$this._fromStatus = fromStatus;

  DateTime? _receivedAt;
  DateTime? get receivedAt => _$this._receivedAt;
  set receivedAt(DateTime? receivedAt) => _$this._receivedAt = receivedAt;

  PaymentStatus? _toStatus;
  PaymentStatus? get toStatus => _$this._toStatus;
  set toStatus(PaymentStatus? toStatus) => _$this._toStatus = toStatus;

  PaymentEventType? _type;
  PaymentEventType? get type => _$this._type;
  set type(PaymentEventType? type) => _$this._type = type;

  InsurerPaymentEventBuilder() {
    InsurerPaymentEvent._defaults(this);
  }

  InsurerPaymentEventBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fromStatus = $v.fromStatus;
      _receivedAt = $v.receivedAt;
      _toStatus = $v.toStatus;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerPaymentEvent other) {
    _$v = other as _$InsurerPaymentEvent;
  }

  @override
  void update(void Function(InsurerPaymentEventBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerPaymentEvent build() => _build();

  _$InsurerPaymentEvent _build() {
    final _$result = _$v ??
        _$InsurerPaymentEvent._(
          fromStatus: fromStatus,
          receivedAt: BuiltValueNullFieldError.checkNotNull(
              receivedAt, r'InsurerPaymentEvent', 'receivedAt'),
          toStatus: toStatus,
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'InsurerPaymentEvent', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
