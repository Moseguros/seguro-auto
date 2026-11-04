// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_inspection.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalInspection extends ProposalInspection {
  @override
  final bool required_;

  factory _$ProposalInspection(
          [void Function(ProposalInspectionBuilder)? updates]) =>
      (ProposalInspectionBuilder()..update(updates))._build();

  _$ProposalInspection._({required this.required_}) : super._();
  @override
  ProposalInspection rebuild(
          void Function(ProposalInspectionBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalInspectionBuilder toBuilder() =>
      ProposalInspectionBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalInspection && required_ == other.required_;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, required_.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalInspection')
          ..add('required_', required_))
        .toString();
  }
}

class ProposalInspectionBuilder
    implements Builder<ProposalInspection, ProposalInspectionBuilder> {
  _$ProposalInspection? _$v;

  bool? _required_;
  bool? get required_ => _$this._required_;
  set required_(bool? required_) => _$this._required_ = required_;

  ProposalInspectionBuilder() {
    ProposalInspection._defaults(this);
  }

  ProposalInspectionBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _required_ = $v.required_;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalInspection other) {
    _$v = other as _$ProposalInspection;
  }

  @override
  void update(void Function(ProposalInspectionBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalInspection build() => _build();

  _$ProposalInspection _build() {
    final _$result = _$v ??
        _$ProposalInspection._(
          required_: BuiltValueNullFieldError.checkNotNull(
              required_, r'ProposalInspection', 'required_'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
