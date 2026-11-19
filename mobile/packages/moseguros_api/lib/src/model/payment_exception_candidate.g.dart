// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_exception_candidate.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentExceptionCandidate extends PaymentExceptionCandidate {
  @override
  final Money amount;
  @override
  final String? bankReference;
  @override
  final DateTime createdAt;
  @override
  final String id;
  @override
  final bool matchesAmount;
  @override
  final bool matchesReference;
  @override
  final String reference;
  @override
  final PaymentStatus status;
  @override
  final String targetLabel;

  factory _$PaymentExceptionCandidate(
          [void Function(PaymentExceptionCandidateBuilder)? updates]) =>
      (PaymentExceptionCandidateBuilder()..update(updates))._build();

  _$PaymentExceptionCandidate._(
      {required this.amount,
      this.bankReference,
      required this.createdAt,
      required this.id,
      required this.matchesAmount,
      required this.matchesReference,
      required this.reference,
      required this.status,
      required this.targetLabel})
      : super._();
  @override
  PaymentExceptionCandidate rebuild(
          void Function(PaymentExceptionCandidateBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentExceptionCandidateBuilder toBuilder() =>
      PaymentExceptionCandidateBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentExceptionCandidate &&
        amount == other.amount &&
        bankReference == other.bankReference &&
        createdAt == other.createdAt &&
        id == other.id &&
        matchesAmount == other.matchesAmount &&
        matchesReference == other.matchesReference &&
        reference == other.reference &&
        status == other.status &&
        targetLabel == other.targetLabel;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, bankReference.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, matchesAmount.hashCode);
    _$hash = $jc(_$hash, matchesReference.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, targetLabel.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentExceptionCandidate')
          ..add('amount', amount)
          ..add('bankReference', bankReference)
          ..add('createdAt', createdAt)
          ..add('id', id)
          ..add('matchesAmount', matchesAmount)
          ..add('matchesReference', matchesReference)
          ..add('reference', reference)
          ..add('status', status)
          ..add('targetLabel', targetLabel))
        .toString();
  }
}

class PaymentExceptionCandidateBuilder
    implements
        Builder<PaymentExceptionCandidate, PaymentExceptionCandidateBuilder> {
  _$PaymentExceptionCandidate? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  String? _bankReference;
  String? get bankReference => _$this._bankReference;
  set bankReference(String? bankReference) =>
      _$this._bankReference = bankReference;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  bool? _matchesAmount;
  bool? get matchesAmount => _$this._matchesAmount;
  set matchesAmount(bool? matchesAmount) =>
      _$this._matchesAmount = matchesAmount;

  bool? _matchesReference;
  bool? get matchesReference => _$this._matchesReference;
  set matchesReference(bool? matchesReference) =>
      _$this._matchesReference = matchesReference;

  String? _reference;
  String? get reference => _$this._reference;
  set reference(String? reference) => _$this._reference = reference;

  PaymentStatus? _status;
  PaymentStatus? get status => _$this._status;
  set status(PaymentStatus? status) => _$this._status = status;

  String? _targetLabel;
  String? get targetLabel => _$this._targetLabel;
  set targetLabel(String? targetLabel) => _$this._targetLabel = targetLabel;

  PaymentExceptionCandidateBuilder() {
    PaymentExceptionCandidate._defaults(this);
  }

  PaymentExceptionCandidateBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _bankReference = $v.bankReference;
      _createdAt = $v.createdAt;
      _id = $v.id;
      _matchesAmount = $v.matchesAmount;
      _matchesReference = $v.matchesReference;
      _reference = $v.reference;
      _status = $v.status;
      _targetLabel = $v.targetLabel;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentExceptionCandidate other) {
    _$v = other as _$PaymentExceptionCandidate;
  }

  @override
  void update(void Function(PaymentExceptionCandidateBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentExceptionCandidate build() => _build();

  _$PaymentExceptionCandidate _build() {
    _$PaymentExceptionCandidate _$result;
    try {
      _$result = _$v ??
          _$PaymentExceptionCandidate._(
            amount: amount.build(),
            bankReference: bankReference,
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'PaymentExceptionCandidate', 'createdAt'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'PaymentExceptionCandidate', 'id'),
            matchesAmount: BuiltValueNullFieldError.checkNotNull(
                matchesAmount, r'PaymentExceptionCandidate', 'matchesAmount'),
            matchesReference: BuiltValueNullFieldError.checkNotNull(
                matchesReference,
                r'PaymentExceptionCandidate',
                'matchesReference'),
            reference: BuiltValueNullFieldError.checkNotNull(
                reference, r'PaymentExceptionCandidate', 'reference'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'PaymentExceptionCandidate', 'status'),
            targetLabel: BuiltValueNullFieldError.checkNotNull(
                targetLabel, r'PaymentExceptionCandidate', 'targetLabel'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentExceptionCandidate', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
