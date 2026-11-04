// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_charge.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalCharge extends ProposalCharge {
  @override
  final Money amount;
  @override
  final String code;
  @override
  final String labelEn;
  @override
  final String labelPt;

  factory _$ProposalCharge([void Function(ProposalChargeBuilder)? updates]) =>
      (ProposalChargeBuilder()..update(updates))._build();

  _$ProposalCharge._(
      {required this.amount,
      required this.code,
      required this.labelEn,
      required this.labelPt})
      : super._();
  @override
  ProposalCharge rebuild(void Function(ProposalChargeBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalChargeBuilder toBuilder() => ProposalChargeBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalCharge &&
        amount == other.amount &&
        code == other.code &&
        labelEn == other.labelEn &&
        labelPt == other.labelPt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, labelEn.hashCode);
    _$hash = $jc(_$hash, labelPt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalCharge')
          ..add('amount', amount)
          ..add('code', code)
          ..add('labelEn', labelEn)
          ..add('labelPt', labelPt))
        .toString();
  }
}

class ProposalChargeBuilder
    implements Builder<ProposalCharge, ProposalChargeBuilder> {
  _$ProposalCharge? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _labelEn;
  String? get labelEn => _$this._labelEn;
  set labelEn(String? labelEn) => _$this._labelEn = labelEn;

  String? _labelPt;
  String? get labelPt => _$this._labelPt;
  set labelPt(String? labelPt) => _$this._labelPt = labelPt;

  ProposalChargeBuilder() {
    ProposalCharge._defaults(this);
  }

  ProposalChargeBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _code = $v.code;
      _labelEn = $v.labelEn;
      _labelPt = $v.labelPt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalCharge other) {
    _$v = other as _$ProposalCharge;
  }

  @override
  void update(void Function(ProposalChargeBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalCharge build() => _build();

  _$ProposalCharge _build() {
    _$ProposalCharge _$result;
    try {
      _$result = _$v ??
          _$ProposalCharge._(
            amount: amount.build(),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'ProposalCharge', 'code'),
            labelEn: BuiltValueNullFieldError.checkNotNull(
                labelEn, r'ProposalCharge', 'labelEn'),
            labelPt: BuiltValueNullFieldError.checkNotNull(
                labelPt, r'ProposalCharge', 'labelPt'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalCharge', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
