// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_proposal_ref.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PolicyProposalRef extends PolicyProposalRef {
  @override
  final String id;
  @override
  final String? number;

  factory _$PolicyProposalRef(
          [void Function(PolicyProposalRefBuilder)? updates]) =>
      (PolicyProposalRefBuilder()..update(updates))._build();

  _$PolicyProposalRef._({required this.id, this.number}) : super._();
  @override
  PolicyProposalRef rebuild(void Function(PolicyProposalRefBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyProposalRefBuilder toBuilder() =>
      PolicyProposalRefBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyProposalRef &&
        id == other.id &&
        number == other.number;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyProposalRef')
          ..add('id', id)
          ..add('number', number))
        .toString();
  }
}

class PolicyProposalRefBuilder
    implements Builder<PolicyProposalRef, PolicyProposalRefBuilder> {
  _$PolicyProposalRef? _$v;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  PolicyProposalRefBuilder() {
    PolicyProposalRef._defaults(this);
  }

  PolicyProposalRefBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _id = $v.id;
      _number = $v.number;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyProposalRef other) {
    _$v = other as _$PolicyProposalRef;
  }

  @override
  void update(void Function(PolicyProposalRefBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyProposalRef build() => _build();

  _$PolicyProposalRef _build() {
    final _$result = _$v ??
        _$PolicyProposalRef._(
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PolicyProposalRef', 'id'),
          number: number,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
