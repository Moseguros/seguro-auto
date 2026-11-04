// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_organization.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalOrganization extends ProposalOrganization {
  @override
  final String displayName;
  @override
  final String id;

  factory _$ProposalOrganization(
          [void Function(ProposalOrganizationBuilder)? updates]) =>
      (ProposalOrganizationBuilder()..update(updates))._build();

  _$ProposalOrganization._({required this.displayName, required this.id})
      : super._();
  @override
  ProposalOrganization rebuild(
          void Function(ProposalOrganizationBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalOrganizationBuilder toBuilder() =>
      ProposalOrganizationBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalOrganization &&
        displayName == other.displayName &&
        id == other.id;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalOrganization')
          ..add('displayName', displayName)
          ..add('id', id))
        .toString();
  }
}

class ProposalOrganizationBuilder
    implements Builder<ProposalOrganization, ProposalOrganizationBuilder> {
  _$ProposalOrganization? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  ProposalOrganizationBuilder() {
    ProposalOrganization._defaults(this);
  }

  ProposalOrganizationBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _id = $v.id;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalOrganization other) {
    _$v = other as _$ProposalOrganization;
  }

  @override
  void update(void Function(ProposalOrganizationBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalOrganization build() => _build();

  _$ProposalOrganization _build() {
    final _$result = _$v ??
        _$ProposalOrganization._(
          displayName: BuiltValueNullFieldError.checkNotNull(
              displayName, r'ProposalOrganization', 'displayName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'ProposalOrganization', 'id'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
