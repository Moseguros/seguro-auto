// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_insurer_ref.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PolicyInsurerRef extends PolicyInsurerRef {
  @override
  final String id;
  @override
  final String? name;

  factory _$PolicyInsurerRef(
          [void Function(PolicyInsurerRefBuilder)? updates]) =>
      (PolicyInsurerRefBuilder()..update(updates))._build();

  _$PolicyInsurerRef._({required this.id, this.name}) : super._();
  @override
  PolicyInsurerRef rebuild(void Function(PolicyInsurerRefBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyInsurerRefBuilder toBuilder() =>
      PolicyInsurerRefBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyInsurerRef && id == other.id && name == other.name;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyInsurerRef')
          ..add('id', id)
          ..add('name', name))
        .toString();
  }
}

class PolicyInsurerRefBuilder
    implements Builder<PolicyInsurerRef, PolicyInsurerRefBuilder> {
  _$PolicyInsurerRef? _$v;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  PolicyInsurerRefBuilder() {
    PolicyInsurerRef._defaults(this);
  }

  PolicyInsurerRefBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _id = $v.id;
      _name = $v.name;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyInsurerRef other) {
    _$v = other as _$PolicyInsurerRef;
  }

  @override
  void update(void Function(PolicyInsurerRefBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyInsurerRef build() => _build();

  _$PolicyInsurerRef _build() {
    final _$result = _$v ??
        _$PolicyInsurerRef._(
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PolicyInsurerRef', 'id'),
          name: name,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
