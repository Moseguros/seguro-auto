// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_worker_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentWorkerStatus extends PaymentWorkerStatus {
  @override
  final bool brokerReachable;
  @override
  final DateTime? lastMessageAt;
  @override
  final BuiltList<PaymentQueueStatus> queues;

  factory _$PaymentWorkerStatus(
          [void Function(PaymentWorkerStatusBuilder)? updates]) =>
      (PaymentWorkerStatusBuilder()..update(updates))._build();

  _$PaymentWorkerStatus._(
      {required this.brokerReachable, this.lastMessageAt, required this.queues})
      : super._();
  @override
  PaymentWorkerStatus rebuild(
          void Function(PaymentWorkerStatusBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentWorkerStatusBuilder toBuilder() =>
      PaymentWorkerStatusBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentWorkerStatus &&
        brokerReachable == other.brokerReachable &&
        lastMessageAt == other.lastMessageAt &&
        queues == other.queues;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, brokerReachable.hashCode);
    _$hash = $jc(_$hash, lastMessageAt.hashCode);
    _$hash = $jc(_$hash, queues.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentWorkerStatus')
          ..add('brokerReachable', brokerReachable)
          ..add('lastMessageAt', lastMessageAt)
          ..add('queues', queues))
        .toString();
  }
}

class PaymentWorkerStatusBuilder
    implements Builder<PaymentWorkerStatus, PaymentWorkerStatusBuilder> {
  _$PaymentWorkerStatus? _$v;

  bool? _brokerReachable;
  bool? get brokerReachable => _$this._brokerReachable;
  set brokerReachable(bool? brokerReachable) =>
      _$this._brokerReachable = brokerReachable;

  DateTime? _lastMessageAt;
  DateTime? get lastMessageAt => _$this._lastMessageAt;
  set lastMessageAt(DateTime? lastMessageAt) =>
      _$this._lastMessageAt = lastMessageAt;

  ListBuilder<PaymentQueueStatus>? _queues;
  ListBuilder<PaymentQueueStatus> get queues =>
      _$this._queues ??= ListBuilder<PaymentQueueStatus>();
  set queues(ListBuilder<PaymentQueueStatus>? queues) =>
      _$this._queues = queues;

  PaymentWorkerStatusBuilder() {
    PaymentWorkerStatus._defaults(this);
  }

  PaymentWorkerStatusBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _brokerReachable = $v.brokerReachable;
      _lastMessageAt = $v.lastMessageAt;
      _queues = $v.queues.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentWorkerStatus other) {
    _$v = other as _$PaymentWorkerStatus;
  }

  @override
  void update(void Function(PaymentWorkerStatusBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentWorkerStatus build() => _build();

  _$PaymentWorkerStatus _build() {
    _$PaymentWorkerStatus _$result;
    try {
      _$result = _$v ??
          _$PaymentWorkerStatus._(
            brokerReachable: BuiltValueNullFieldError.checkNotNull(
                brokerReachable, r'PaymentWorkerStatus', 'brokerReachable'),
            lastMessageAt: lastMessageAt,
            queues: queues.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'queues';
        queues.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentWorkerStatus', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
