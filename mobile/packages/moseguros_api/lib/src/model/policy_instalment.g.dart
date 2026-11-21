// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_instalment.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PolicyInstalment extends PolicyInstalment {
  @override
  final Money amount;
  @override
  final Date dueDate;
  @override
  final DateTime? paidAt;
  @override
  final int seq;
  @override
  final InstalmentStatus status;

  factory _$PolicyInstalment(
          [void Function(PolicyInstalmentBuilder)? updates]) =>
      (PolicyInstalmentBuilder()..update(updates))._build();

  _$PolicyInstalment._(
      {required this.amount,
      required this.dueDate,
      this.paidAt,
      required this.seq,
      required this.status})
      : super._();
  @override
  PolicyInstalment rebuild(void Function(PolicyInstalmentBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyInstalmentBuilder toBuilder() =>
      PolicyInstalmentBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyInstalment &&
        amount == other.amount &&
        dueDate == other.dueDate &&
        paidAt == other.paidAt &&
        seq == other.seq &&
        status == other.status;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, dueDate.hashCode);
    _$hash = $jc(_$hash, paidAt.hashCode);
    _$hash = $jc(_$hash, seq.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyInstalment')
          ..add('amount', amount)
          ..add('dueDate', dueDate)
          ..add('paidAt', paidAt)
          ..add('seq', seq)
          ..add('status', status))
        .toString();
  }
}

class PolicyInstalmentBuilder
    implements Builder<PolicyInstalment, PolicyInstalmentBuilder> {
  _$PolicyInstalment? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  Date? _dueDate;
  Date? get dueDate => _$this._dueDate;
  set dueDate(Date? dueDate) => _$this._dueDate = dueDate;

  DateTime? _paidAt;
  DateTime? get paidAt => _$this._paidAt;
  set paidAt(DateTime? paidAt) => _$this._paidAt = paidAt;

  int? _seq;
  int? get seq => _$this._seq;
  set seq(int? seq) => _$this._seq = seq;

  InstalmentStatus? _status;
  InstalmentStatus? get status => _$this._status;
  set status(InstalmentStatus? status) => _$this._status = status;

  PolicyInstalmentBuilder() {
    PolicyInstalment._defaults(this);
  }

  PolicyInstalmentBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _dueDate = $v.dueDate;
      _paidAt = $v.paidAt;
      _seq = $v.seq;
      _status = $v.status;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyInstalment other) {
    _$v = other as _$PolicyInstalment;
  }

  @override
  void update(void Function(PolicyInstalmentBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyInstalment build() => _build();

  _$PolicyInstalment _build() {
    _$PolicyInstalment _$result;
    try {
      _$result = _$v ??
          _$PolicyInstalment._(
            amount: amount.build(),
            dueDate: BuiltValueNullFieldError.checkNotNull(
                dueDate, r'PolicyInstalment', 'dueDate'),
            paidAt: paidAt,
            seq: BuiltValueNullFieldError.checkNotNull(
                seq, r'PolicyInstalment', 'seq'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'PolicyInstalment', 'status'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PolicyInstalment', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
