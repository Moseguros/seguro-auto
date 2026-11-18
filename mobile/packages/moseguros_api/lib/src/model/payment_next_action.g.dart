// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_next_action.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentNextAction extends PaymentNextAction {
  @override
  final Money? amount;
  @override
  final String? entity;
  @override
  final DateTime? nextCheckAt;
  @override
  final String? reference;
  @override
  final DateTime? timeoutAt;
  @override
  final PaymentNextActionType type;
  @override
  final DateTime? validUntil;

  factory _$PaymentNextAction(
          [void Function(PaymentNextActionBuilder)? updates]) =>
      (PaymentNextActionBuilder()..update(updates))._build();

  _$PaymentNextAction._(
      {this.amount,
      this.entity,
      this.nextCheckAt,
      this.reference,
      this.timeoutAt,
      required this.type,
      this.validUntil})
      : super._();
  @override
  PaymentNextAction rebuild(void Function(PaymentNextActionBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentNextActionBuilder toBuilder() =>
      PaymentNextActionBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentNextAction &&
        amount == other.amount &&
        entity == other.entity &&
        nextCheckAt == other.nextCheckAt &&
        reference == other.reference &&
        timeoutAt == other.timeoutAt &&
        type == other.type &&
        validUntil == other.validUntil;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, entity.hashCode);
    _$hash = $jc(_$hash, nextCheckAt.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, timeoutAt.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, validUntil.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentNextAction')
          ..add('amount', amount)
          ..add('entity', entity)
          ..add('nextCheckAt', nextCheckAt)
          ..add('reference', reference)
          ..add('timeoutAt', timeoutAt)
          ..add('type', type)
          ..add('validUntil', validUntil))
        .toString();
  }
}

class PaymentNextActionBuilder
    implements Builder<PaymentNextAction, PaymentNextActionBuilder> {
  _$PaymentNextAction? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  String? _entity;
  String? get entity => _$this._entity;
  set entity(String? entity) => _$this._entity = entity;

  DateTime? _nextCheckAt;
  DateTime? get nextCheckAt => _$this._nextCheckAt;
  set nextCheckAt(DateTime? nextCheckAt) => _$this._nextCheckAt = nextCheckAt;

  String? _reference;
  String? get reference => _$this._reference;
  set reference(String? reference) => _$this._reference = reference;

  DateTime? _timeoutAt;
  DateTime? get timeoutAt => _$this._timeoutAt;
  set timeoutAt(DateTime? timeoutAt) => _$this._timeoutAt = timeoutAt;

  PaymentNextActionType? _type;
  PaymentNextActionType? get type => _$this._type;
  set type(PaymentNextActionType? type) => _$this._type = type;

  DateTime? _validUntil;
  DateTime? get validUntil => _$this._validUntil;
  set validUntil(DateTime? validUntil) => _$this._validUntil = validUntil;

  PaymentNextActionBuilder() {
    PaymentNextAction._defaults(this);
  }

  PaymentNextActionBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount?.toBuilder();
      _entity = $v.entity;
      _nextCheckAt = $v.nextCheckAt;
      _reference = $v.reference;
      _timeoutAt = $v.timeoutAt;
      _type = $v.type;
      _validUntil = $v.validUntil;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentNextAction other) {
    _$v = other as _$PaymentNextAction;
  }

  @override
  void update(void Function(PaymentNextActionBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentNextAction build() => _build();

  _$PaymentNextAction _build() {
    _$PaymentNextAction _$result;
    try {
      _$result = _$v ??
          _$PaymentNextAction._(
            amount: _amount?.build(),
            entity: entity,
            nextCheckAt: nextCheckAt,
            reference: reference,
            timeoutAt: timeoutAt,
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'PaymentNextAction', 'type'),
            validUntil: validUntil,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        _amount?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentNextAction', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
