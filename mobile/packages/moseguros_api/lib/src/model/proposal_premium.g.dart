// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_premium.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalPremium extends ProposalPremium {
  @override
  final BuiltList<ProposalCharge> charges;
  @override
  final Money commercial;
  @override
  final Money roundingAdjustment;
  @override
  final Money total;

  factory _$ProposalPremium([void Function(ProposalPremiumBuilder)? updates]) =>
      (ProposalPremiumBuilder()..update(updates))._build();

  _$ProposalPremium._(
      {required this.charges,
      required this.commercial,
      required this.roundingAdjustment,
      required this.total})
      : super._();
  @override
  ProposalPremium rebuild(void Function(ProposalPremiumBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalPremiumBuilder toBuilder() => ProposalPremiumBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalPremium &&
        charges == other.charges &&
        commercial == other.commercial &&
        roundingAdjustment == other.roundingAdjustment &&
        total == other.total;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, charges.hashCode);
    _$hash = $jc(_$hash, commercial.hashCode);
    _$hash = $jc(_$hash, roundingAdjustment.hashCode);
    _$hash = $jc(_$hash, total.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalPremium')
          ..add('charges', charges)
          ..add('commercial', commercial)
          ..add('roundingAdjustment', roundingAdjustment)
          ..add('total', total))
        .toString();
  }
}

class ProposalPremiumBuilder
    implements Builder<ProposalPremium, ProposalPremiumBuilder> {
  _$ProposalPremium? _$v;

  ListBuilder<ProposalCharge>? _charges;
  ListBuilder<ProposalCharge> get charges =>
      _$this._charges ??= ListBuilder<ProposalCharge>();
  set charges(ListBuilder<ProposalCharge>? charges) =>
      _$this._charges = charges;

  MoneyBuilder? _commercial;
  MoneyBuilder get commercial => _$this._commercial ??= MoneyBuilder();
  set commercial(MoneyBuilder? commercial) => _$this._commercial = commercial;

  MoneyBuilder? _roundingAdjustment;
  MoneyBuilder get roundingAdjustment =>
      _$this._roundingAdjustment ??= MoneyBuilder();
  set roundingAdjustment(MoneyBuilder? roundingAdjustment) =>
      _$this._roundingAdjustment = roundingAdjustment;

  MoneyBuilder? _total;
  MoneyBuilder get total => _$this._total ??= MoneyBuilder();
  set total(MoneyBuilder? total) => _$this._total = total;

  ProposalPremiumBuilder() {
    ProposalPremium._defaults(this);
  }

  ProposalPremiumBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _charges = $v.charges.toBuilder();
      _commercial = $v.commercial.toBuilder();
      _roundingAdjustment = $v.roundingAdjustment.toBuilder();
      _total = $v.total.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalPremium other) {
    _$v = other as _$ProposalPremium;
  }

  @override
  void update(void Function(ProposalPremiumBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalPremium build() => _build();

  _$ProposalPremium _build() {
    _$ProposalPremium _$result;
    try {
      _$result = _$v ??
          _$ProposalPremium._(
            charges: charges.build(),
            commercial: commercial.build(),
            roundingAdjustment: roundingAdjustment.build(),
            total: total.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'charges';
        charges.build();
        _$failedField = 'commercial';
        commercial.build();
        _$failedField = 'roundingAdjustment';
        roundingAdjustment.build();
        _$failedField = 'total';
        total.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalPremium', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
