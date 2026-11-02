// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_user_roles_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateUserRolesRequest extends UpdateUserRolesRequest {
  @override
  final BuiltList<String> roleCodes;
  @override
  final int version;

  factory _$UpdateUserRolesRequest(
          [void Function(UpdateUserRolesRequestBuilder)? updates]) =>
      (UpdateUserRolesRequestBuilder()..update(updates))._build();

  _$UpdateUserRolesRequest._({required this.roleCodes, required this.version})
      : super._();
  @override
  UpdateUserRolesRequest rebuild(
          void Function(UpdateUserRolesRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateUserRolesRequestBuilder toBuilder() =>
      UpdateUserRolesRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateUserRolesRequest &&
        roleCodes == other.roleCodes &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, roleCodes.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateUserRolesRequest')
          ..add('roleCodes', roleCodes)
          ..add('version', version))
        .toString();
  }
}

class UpdateUserRolesRequestBuilder
    implements Builder<UpdateUserRolesRequest, UpdateUserRolesRequestBuilder> {
  _$UpdateUserRolesRequest? _$v;

  ListBuilder<String>? _roleCodes;
  ListBuilder<String> get roleCodes =>
      _$this._roleCodes ??= ListBuilder<String>();
  set roleCodes(ListBuilder<String>? roleCodes) =>
      _$this._roleCodes = roleCodes;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateUserRolesRequestBuilder() {
    UpdateUserRolesRequest._defaults(this);
  }

  UpdateUserRolesRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _roleCodes = $v.roleCodes.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateUserRolesRequest other) {
    _$v = other as _$UpdateUserRolesRequest;
  }

  @override
  void update(void Function(UpdateUserRolesRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateUserRolesRequest build() => _build();

  _$UpdateUserRolesRequest _build() {
    _$UpdateUserRolesRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateUserRolesRequest._(
            roleCodes: roleCodes.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateUserRolesRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'roleCodes';
        roleCodes.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateUserRolesRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
