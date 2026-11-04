// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_kyc.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalKyc extends ProposalKyc {
  @override
  final bool requiredForAutomaticAcceptance;
  @override
  final KycState state;

  factory _$ProposalKyc([void Function(ProposalKycBuilder)? updates]) =>
      (ProposalKycBuilder()..update(updates))._build();

  _$ProposalKyc._(
      {required this.requiredForAutomaticAcceptance, required this.state})
      : super._();
  @override
  ProposalKyc rebuild(void Function(ProposalKycBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalKycBuilder toBuilder() => ProposalKycBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalKyc &&
        requiredForAutomaticAcceptance ==
            other.requiredForAutomaticAcceptance &&
        state == other.state;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, requiredForAutomaticAcceptance.hashCode);
    _$hash = $jc(_$hash, state.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalKyc')
          ..add(
              'requiredForAutomaticAcceptance', requiredForAutomaticAcceptance)
          ..add('state', state))
        .toString();
  }
}

class ProposalKycBuilder implements Builder<ProposalKyc, ProposalKycBuilder> {
  _$ProposalKyc? _$v;

  bool? _requiredForAutomaticAcceptance;
  bool? get requiredForAutomaticAcceptance =>
      _$this._requiredForAutomaticAcceptance;
  set requiredForAutomaticAcceptance(bool? requiredForAutomaticAcceptance) =>
      _$this._requiredForAutomaticAcceptance = requiredForAutomaticAcceptance;

  KycState? _state;
  KycState? get state => _$this._state;
  set state(KycState? state) => _$this._state = state;

  ProposalKycBuilder() {
    ProposalKyc._defaults(this);
  }

  ProposalKycBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _requiredForAutomaticAcceptance = $v.requiredForAutomaticAcceptance;
      _state = $v.state;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalKyc other) {
    _$v = other as _$ProposalKyc;
  }

  @override
  void update(void Function(ProposalKycBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalKyc build() => _build();

  _$ProposalKyc _build() {
    final _$result = _$v ??
        _$ProposalKyc._(
          requiredForAutomaticAcceptance: BuiltValueNullFieldError.checkNotNull(
              requiredForAutomaticAcceptance,
              r'ProposalKyc',
              'requiredForAutomaticAcceptance'),
          state: BuiltValueNullFieldError.checkNotNull(
              state, r'ProposalKyc', 'state'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
