// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_decision.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalDecision extends ProposalDecision {
  @override
  final DateTime? decidedAt;
  @override
  final ProposalInfoRequest? infoRequest;
  @override
  final DateTime? insurerDueAt;
  @override
  final AcceptanceMode? mode;
  @override
  final String? reason;
  @override
  final RejectionReasonCode? rejectionReasonCode;

  factory _$ProposalDecision(
          [void Function(ProposalDecisionBuilder)? updates]) =>
      (ProposalDecisionBuilder()..update(updates))._build();

  _$ProposalDecision._(
      {this.decidedAt,
      this.infoRequest,
      this.insurerDueAt,
      this.mode,
      this.reason,
      this.rejectionReasonCode})
      : super._();
  @override
  ProposalDecision rebuild(void Function(ProposalDecisionBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalDecisionBuilder toBuilder() =>
      ProposalDecisionBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalDecision &&
        decidedAt == other.decidedAt &&
        infoRequest == other.infoRequest &&
        insurerDueAt == other.insurerDueAt &&
        mode == other.mode &&
        reason == other.reason &&
        rejectionReasonCode == other.rejectionReasonCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, decidedAt.hashCode);
    _$hash = $jc(_$hash, infoRequest.hashCode);
    _$hash = $jc(_$hash, insurerDueAt.hashCode);
    _$hash = $jc(_$hash, mode.hashCode);
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jc(_$hash, rejectionReasonCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalDecision')
          ..add('decidedAt', decidedAt)
          ..add('infoRequest', infoRequest)
          ..add('insurerDueAt', insurerDueAt)
          ..add('mode', mode)
          ..add('reason', reason)
          ..add('rejectionReasonCode', rejectionReasonCode))
        .toString();
  }
}

class ProposalDecisionBuilder
    implements Builder<ProposalDecision, ProposalDecisionBuilder> {
  _$ProposalDecision? _$v;

  DateTime? _decidedAt;
  DateTime? get decidedAt => _$this._decidedAt;
  set decidedAt(DateTime? decidedAt) => _$this._decidedAt = decidedAt;

  ProposalInfoRequestBuilder? _infoRequest;
  ProposalInfoRequestBuilder get infoRequest =>
      _$this._infoRequest ??= ProposalInfoRequestBuilder();
  set infoRequest(ProposalInfoRequestBuilder? infoRequest) =>
      _$this._infoRequest = infoRequest;

  DateTime? _insurerDueAt;
  DateTime? get insurerDueAt => _$this._insurerDueAt;
  set insurerDueAt(DateTime? insurerDueAt) =>
      _$this._insurerDueAt = insurerDueAt;

  AcceptanceMode? _mode;
  AcceptanceMode? get mode => _$this._mode;
  set mode(AcceptanceMode? mode) => _$this._mode = mode;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  RejectionReasonCode? _rejectionReasonCode;
  RejectionReasonCode? get rejectionReasonCode => _$this._rejectionReasonCode;
  set rejectionReasonCode(RejectionReasonCode? rejectionReasonCode) =>
      _$this._rejectionReasonCode = rejectionReasonCode;

  ProposalDecisionBuilder() {
    ProposalDecision._defaults(this);
  }

  ProposalDecisionBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _decidedAt = $v.decidedAt;
      _infoRequest = $v.infoRequest?.toBuilder();
      _insurerDueAt = $v.insurerDueAt;
      _mode = $v.mode;
      _reason = $v.reason;
      _rejectionReasonCode = $v.rejectionReasonCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalDecision other) {
    _$v = other as _$ProposalDecision;
  }

  @override
  void update(void Function(ProposalDecisionBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalDecision build() => _build();

  _$ProposalDecision _build() {
    _$ProposalDecision _$result;
    try {
      _$result = _$v ??
          _$ProposalDecision._(
            decidedAt: decidedAt,
            infoRequest: _infoRequest?.build(),
            insurerDueAt: insurerDueAt,
            mode: mode,
            reason: reason,
            rejectionReasonCode: rejectionReasonCode,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'infoRequest';
        _infoRequest?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalDecision', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
