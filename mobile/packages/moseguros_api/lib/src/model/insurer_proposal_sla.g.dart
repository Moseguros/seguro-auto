// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_proposal_sla.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerProposalSla extends InsurerProposalSla {
  @override
  final DateTime? alertAt;
  @override
  final DateTime? alertedAt;
  @override
  final DateTime? dueAt;
  @override
  final DateTime? reviewStartedAt;
  @override
  final SlaStatus? status;

  factory _$InsurerProposalSla(
          [void Function(InsurerProposalSlaBuilder)? updates]) =>
      (InsurerProposalSlaBuilder()..update(updates))._build();

  _$InsurerProposalSla._(
      {this.alertAt,
      this.alertedAt,
      this.dueAt,
      this.reviewStartedAt,
      this.status})
      : super._();
  @override
  InsurerProposalSla rebuild(
          void Function(InsurerProposalSlaBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProposalSlaBuilder toBuilder() =>
      InsurerProposalSlaBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProposalSla &&
        alertAt == other.alertAt &&
        alertedAt == other.alertedAt &&
        dueAt == other.dueAt &&
        reviewStartedAt == other.reviewStartedAt &&
        status == other.status;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, alertAt.hashCode);
    _$hash = $jc(_$hash, alertedAt.hashCode);
    _$hash = $jc(_$hash, dueAt.hashCode);
    _$hash = $jc(_$hash, reviewStartedAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProposalSla')
          ..add('alertAt', alertAt)
          ..add('alertedAt', alertedAt)
          ..add('dueAt', dueAt)
          ..add('reviewStartedAt', reviewStartedAt)
          ..add('status', status))
        .toString();
  }
}

class InsurerProposalSlaBuilder
    implements Builder<InsurerProposalSla, InsurerProposalSlaBuilder> {
  _$InsurerProposalSla? _$v;

  DateTime? _alertAt;
  DateTime? get alertAt => _$this._alertAt;
  set alertAt(DateTime? alertAt) => _$this._alertAt = alertAt;

  DateTime? _alertedAt;
  DateTime? get alertedAt => _$this._alertedAt;
  set alertedAt(DateTime? alertedAt) => _$this._alertedAt = alertedAt;

  DateTime? _dueAt;
  DateTime? get dueAt => _$this._dueAt;
  set dueAt(DateTime? dueAt) => _$this._dueAt = dueAt;

  DateTime? _reviewStartedAt;
  DateTime? get reviewStartedAt => _$this._reviewStartedAt;
  set reviewStartedAt(DateTime? reviewStartedAt) =>
      _$this._reviewStartedAt = reviewStartedAt;

  SlaStatus? _status;
  SlaStatus? get status => _$this._status;
  set status(SlaStatus? status) => _$this._status = status;

  InsurerProposalSlaBuilder() {
    InsurerProposalSla._defaults(this);
  }

  InsurerProposalSlaBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _alertAt = $v.alertAt;
      _alertedAt = $v.alertedAt;
      _dueAt = $v.dueAt;
      _reviewStartedAt = $v.reviewStartedAt;
      _status = $v.status;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProposalSla other) {
    _$v = other as _$InsurerProposalSla;
  }

  @override
  void update(void Function(InsurerProposalSlaBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProposalSla build() => _build();

  _$InsurerProposalSla _build() {
    final _$result = _$v ??
        _$InsurerProposalSla._(
          alertAt: alertAt,
          alertedAt: alertedAt,
          dueAt: dueAt,
          reviewStartedAt: reviewStartedAt,
          status: status,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
