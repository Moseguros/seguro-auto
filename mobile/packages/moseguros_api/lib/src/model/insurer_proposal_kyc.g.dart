// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_proposal_kyc.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerProposalKyc extends InsurerProposalKyc {
  @override
  final String? checkId;
  @override
  final bool requiredForAutomaticAcceptance;
  @override
  final KycState state;
  @override
  final DateTime? verifiedAt;
  @override
  final bool verifiedByThisInsurer;

  factory _$InsurerProposalKyc(
          [void Function(InsurerProposalKycBuilder)? updates]) =>
      (InsurerProposalKycBuilder()..update(updates))._build();

  _$InsurerProposalKyc._(
      {this.checkId,
      required this.requiredForAutomaticAcceptance,
      required this.state,
      this.verifiedAt,
      required this.verifiedByThisInsurer})
      : super._();
  @override
  InsurerProposalKyc rebuild(
          void Function(InsurerProposalKycBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProposalKycBuilder toBuilder() =>
      InsurerProposalKycBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProposalKyc &&
        checkId == other.checkId &&
        requiredForAutomaticAcceptance ==
            other.requiredForAutomaticAcceptance &&
        state == other.state &&
        verifiedAt == other.verifiedAt &&
        verifiedByThisInsurer == other.verifiedByThisInsurer;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, checkId.hashCode);
    _$hash = $jc(_$hash, requiredForAutomaticAcceptance.hashCode);
    _$hash = $jc(_$hash, state.hashCode);
    _$hash = $jc(_$hash, verifiedAt.hashCode);
    _$hash = $jc(_$hash, verifiedByThisInsurer.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProposalKyc')
          ..add('checkId', checkId)
          ..add(
              'requiredForAutomaticAcceptance', requiredForAutomaticAcceptance)
          ..add('state', state)
          ..add('verifiedAt', verifiedAt)
          ..add('verifiedByThisInsurer', verifiedByThisInsurer))
        .toString();
  }
}

class InsurerProposalKycBuilder
    implements Builder<InsurerProposalKyc, InsurerProposalKycBuilder> {
  _$InsurerProposalKyc? _$v;

  String? _checkId;
  String? get checkId => _$this._checkId;
  set checkId(String? checkId) => _$this._checkId = checkId;

  bool? _requiredForAutomaticAcceptance;
  bool? get requiredForAutomaticAcceptance =>
      _$this._requiredForAutomaticAcceptance;
  set requiredForAutomaticAcceptance(bool? requiredForAutomaticAcceptance) =>
      _$this._requiredForAutomaticAcceptance = requiredForAutomaticAcceptance;

  KycState? _state;
  KycState? get state => _$this._state;
  set state(KycState? state) => _$this._state = state;

  DateTime? _verifiedAt;
  DateTime? get verifiedAt => _$this._verifiedAt;
  set verifiedAt(DateTime? verifiedAt) => _$this._verifiedAt = verifiedAt;

  bool? _verifiedByThisInsurer;
  bool? get verifiedByThisInsurer => _$this._verifiedByThisInsurer;
  set verifiedByThisInsurer(bool? verifiedByThisInsurer) =>
      _$this._verifiedByThisInsurer = verifiedByThisInsurer;

  InsurerProposalKycBuilder() {
    InsurerProposalKyc._defaults(this);
  }

  InsurerProposalKycBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _checkId = $v.checkId;
      _requiredForAutomaticAcceptance = $v.requiredForAutomaticAcceptance;
      _state = $v.state;
      _verifiedAt = $v.verifiedAt;
      _verifiedByThisInsurer = $v.verifiedByThisInsurer;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProposalKyc other) {
    _$v = other as _$InsurerProposalKyc;
  }

  @override
  void update(void Function(InsurerProposalKycBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProposalKyc build() => _build();

  _$InsurerProposalKyc _build() {
    final _$result = _$v ??
        _$InsurerProposalKyc._(
          checkId: checkId,
          requiredForAutomaticAcceptance: BuiltValueNullFieldError.checkNotNull(
              requiredForAutomaticAcceptance,
              r'InsurerProposalKyc',
              'requiredForAutomaticAcceptance'),
          state: BuiltValueNullFieldError.checkNotNull(
              state, r'InsurerProposalKyc', 'state'),
          verifiedAt: verifiedAt,
          verifiedByThisInsurer: BuiltValueNullFieldError.checkNotNull(
              verifiedByThisInsurer,
              r'InsurerProposalKyc',
              'verifiedByThisInsurer'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
