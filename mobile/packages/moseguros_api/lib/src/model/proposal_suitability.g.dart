// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_suitability.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalSuitability extends ProposalSuitability {
  @override
  final DateTime? acknowledgedAt;
  @override
  final DateTime answeredAt;
  @override
  final BuiltList<SuitabilityAnswerResponse> answers;
  @override
  final BuiltList<SuitabilityReason> reasons;
  @override
  final SuitabilityResult result;

  factory _$ProposalSuitability(
          [void Function(ProposalSuitabilityBuilder)? updates]) =>
      (ProposalSuitabilityBuilder()..update(updates))._build();

  _$ProposalSuitability._(
      {this.acknowledgedAt,
      required this.answeredAt,
      required this.answers,
      required this.reasons,
      required this.result})
      : super._();
  @override
  ProposalSuitability rebuild(
          void Function(ProposalSuitabilityBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalSuitabilityBuilder toBuilder() =>
      ProposalSuitabilityBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalSuitability &&
        acknowledgedAt == other.acknowledgedAt &&
        answeredAt == other.answeredAt &&
        answers == other.answers &&
        reasons == other.reasons &&
        result == other.result;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, acknowledgedAt.hashCode);
    _$hash = $jc(_$hash, answeredAt.hashCode);
    _$hash = $jc(_$hash, answers.hashCode);
    _$hash = $jc(_$hash, reasons.hashCode);
    _$hash = $jc(_$hash, result.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalSuitability')
          ..add('acknowledgedAt', acknowledgedAt)
          ..add('answeredAt', answeredAt)
          ..add('answers', answers)
          ..add('reasons', reasons)
          ..add('result', result))
        .toString();
  }
}

class ProposalSuitabilityBuilder
    implements Builder<ProposalSuitability, ProposalSuitabilityBuilder> {
  _$ProposalSuitability? _$v;

  DateTime? _acknowledgedAt;
  DateTime? get acknowledgedAt => _$this._acknowledgedAt;
  set acknowledgedAt(DateTime? acknowledgedAt) =>
      _$this._acknowledgedAt = acknowledgedAt;

  DateTime? _answeredAt;
  DateTime? get answeredAt => _$this._answeredAt;
  set answeredAt(DateTime? answeredAt) => _$this._answeredAt = answeredAt;

  ListBuilder<SuitabilityAnswerResponse>? _answers;
  ListBuilder<SuitabilityAnswerResponse> get answers =>
      _$this._answers ??= ListBuilder<SuitabilityAnswerResponse>();
  set answers(ListBuilder<SuitabilityAnswerResponse>? answers) =>
      _$this._answers = answers;

  ListBuilder<SuitabilityReason>? _reasons;
  ListBuilder<SuitabilityReason> get reasons =>
      _$this._reasons ??= ListBuilder<SuitabilityReason>();
  set reasons(ListBuilder<SuitabilityReason>? reasons) =>
      _$this._reasons = reasons;

  SuitabilityResult? _result;
  SuitabilityResult? get result => _$this._result;
  set result(SuitabilityResult? result) => _$this._result = result;

  ProposalSuitabilityBuilder() {
    ProposalSuitability._defaults(this);
  }

  ProposalSuitabilityBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _acknowledgedAt = $v.acknowledgedAt;
      _answeredAt = $v.answeredAt;
      _answers = $v.answers.toBuilder();
      _reasons = $v.reasons.toBuilder();
      _result = $v.result;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalSuitability other) {
    _$v = other as _$ProposalSuitability;
  }

  @override
  void update(void Function(ProposalSuitabilityBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalSuitability build() => _build();

  _$ProposalSuitability _build() {
    _$ProposalSuitability _$result;
    try {
      _$result = _$v ??
          _$ProposalSuitability._(
            acknowledgedAt: acknowledgedAt,
            answeredAt: BuiltValueNullFieldError.checkNotNull(
                answeredAt, r'ProposalSuitability', 'answeredAt'),
            answers: answers.build(),
            reasons: reasons.build(),
            result: BuiltValueNullFieldError.checkNotNull(
                result, r'ProposalSuitability', 'result'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'answers';
        answers.build();
        _$failedField = 'reasons';
        reasons.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalSuitability', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
