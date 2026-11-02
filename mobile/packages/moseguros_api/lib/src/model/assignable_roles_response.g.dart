// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'assignable_roles_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$AssignableRolesResponse extends AssignableRolesResponse {
  @override
  final BuiltList<String> managerInviteRoles;
  @override
  final BuiltList<String> managerRoles;
  @override
  final BuiltList<String> roles;

  factory _$AssignableRolesResponse(
          [void Function(AssignableRolesResponseBuilder)? updates]) =>
      (AssignableRolesResponseBuilder()..update(updates))._build();

  _$AssignableRolesResponse._(
      {required this.managerInviteRoles,
      required this.managerRoles,
      required this.roles})
      : super._();
  @override
  AssignableRolesResponse rebuild(
          void Function(AssignableRolesResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AssignableRolesResponseBuilder toBuilder() =>
      AssignableRolesResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AssignableRolesResponse &&
        managerInviteRoles == other.managerInviteRoles &&
        managerRoles == other.managerRoles &&
        roles == other.roles;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, managerInviteRoles.hashCode);
    _$hash = $jc(_$hash, managerRoles.hashCode);
    _$hash = $jc(_$hash, roles.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'AssignableRolesResponse')
          ..add('managerInviteRoles', managerInviteRoles)
          ..add('managerRoles', managerRoles)
          ..add('roles', roles))
        .toString();
  }
}

class AssignableRolesResponseBuilder
    implements
        Builder<AssignableRolesResponse, AssignableRolesResponseBuilder> {
  _$AssignableRolesResponse? _$v;

  ListBuilder<String>? _managerInviteRoles;
  ListBuilder<String> get managerInviteRoles =>
      _$this._managerInviteRoles ??= ListBuilder<String>();
  set managerInviteRoles(ListBuilder<String>? managerInviteRoles) =>
      _$this._managerInviteRoles = managerInviteRoles;

  ListBuilder<String>? _managerRoles;
  ListBuilder<String> get managerRoles =>
      _$this._managerRoles ??= ListBuilder<String>();
  set managerRoles(ListBuilder<String>? managerRoles) =>
      _$this._managerRoles = managerRoles;

  ListBuilder<String>? _roles;
  ListBuilder<String> get roles => _$this._roles ??= ListBuilder<String>();
  set roles(ListBuilder<String>? roles) => _$this._roles = roles;

  AssignableRolesResponseBuilder() {
    AssignableRolesResponse._defaults(this);
  }

  AssignableRolesResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _managerInviteRoles = $v.managerInviteRoles.toBuilder();
      _managerRoles = $v.managerRoles.toBuilder();
      _roles = $v.roles.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AssignableRolesResponse other) {
    _$v = other as _$AssignableRolesResponse;
  }

  @override
  void update(void Function(AssignableRolesResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AssignableRolesResponse build() => _build();

  _$AssignableRolesResponse _build() {
    _$AssignableRolesResponse _$result;
    try {
      _$result = _$v ??
          _$AssignableRolesResponse._(
            managerInviteRoles: managerInviteRoles.build(),
            managerRoles: managerRoles.build(),
            roles: roles.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'managerInviteRoles';
        managerInviteRoles.build();
        _$failedField = 'managerRoles';
        managerRoles.build();
        _$failedField = 'roles';
        roles.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'AssignableRolesResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
