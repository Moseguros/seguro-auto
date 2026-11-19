// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_exception_intent.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentExceptionIntent extends PaymentExceptionIntent {
  @override
  final Money amount;
  @override
  final String id;
  @override
  final String reference;
  @override
  final PaymentStatus status;
  @override
  final String targetLabel;

  factory _$PaymentExceptionIntent(
          [void Function(PaymentExceptionIntentBuilder)? updates]) =>
      (PaymentExceptionIntentBuilder()..update(updates))._build();

  _$PaymentExceptionIntent._(
      {required this.amount,
      required this.id,
      required this.reference,
      required this.status,
      required this.targetLabel})
      : super._();
  @override
  PaymentExceptionIntent rebuild(
          void Function(PaymentExceptionIntentBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentExceptionIntentBuilder toBuilder() =>
      PaymentExceptionIntentBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentExceptionIntent &&
        amount == other.amount &&
        id == other.id &&
        reference == other.reference &&
        status == other.status &&
        targetLabel == other.targetLabel;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, targetLabel.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentExceptionIntent')
          ..add('amount', amount)
          ..add('id', id)
          ..add('reference', reference)
          ..add('status', status)
          ..add('targetLabel', targetLabel))
        .toString();
  }
}

class PaymentExceptionIntentBuilder
    implements Builder<PaymentExceptionIntent, PaymentExceptionIntentBuilder> {
  _$PaymentExceptionIntent? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _reference;
  String? get reference => _$this._reference;
  set reference(String? reference) => _$this._reference = reference;

  PaymentStatus? _status;
  PaymentStatus? get status => _$this._status;
  set status(PaymentStatus? status) => _$this._status = status;

  String? _targetLabel;
  String? get targetLabel => _$this._targetLabel;
  set targetLabel(String? targetLabel) => _$this._targetLabel = targetLabel;

  PaymentExceptionIntentBuilder() {
    PaymentExceptionIntent._defaults(this);
  }

  PaymentExceptionIntentBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _id = $v.id;
      _reference = $v.reference;
      _status = $v.status;
      _targetLabel = $v.targetLabel;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentExceptionIntent other) {
    _$v = other as _$PaymentExceptionIntent;
  }

  @override
  void update(void Function(PaymentExceptionIntentBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentExceptionIntent build() => _build();

  _$PaymentExceptionIntent _build() {
    _$PaymentExceptionIntent _$result;
    try {
      _$result = _$v ??
          _$PaymentExceptionIntent._(
            amount: amount.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'PaymentExceptionIntent', 'id'),
            reference: BuiltValueNullFieldError.checkNotNull(
                reference, r'PaymentExceptionIntent', 'reference'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'PaymentExceptionIntent', 'status'),
            targetLabel: BuiltValueNullFieldError.checkNotNull(
                targetLabel, r'PaymentExceptionIntent', 'targetLabel'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentExceptionIntent', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
