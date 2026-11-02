// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'invite_user_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InviteUserRequest extends InviteUserRequest {
  @override
  final String email;
  @override
  final String fullName;
  @override
  final BuiltList<String> roleCodes;

  factory _$InviteUserRequest(
          [void Function(InviteUserRequestBuilder)? updates]) =>
      (InviteUserRequestBuilder()..update(updates))._build();

  _$InviteUserRequest._(
      {required this.email, required this.fullName, required this.roleCodes})
      : super._();
  @override
  InviteUserRequest rebuild(void Function(InviteUserRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InviteUserRequestBuilder toBuilder() =>
      InviteUserRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InviteUserRequest &&
        email == other.email &&
        fullName == other.fullName &&
        roleCodes == other.roleCodes;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, roleCodes.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InviteUserRequest')
          ..add('email', email)
          ..add('fullName', fullName)
          ..add('roleCodes', roleCodes))
        .toString();
  }
}

class InviteUserRequestBuilder
    implements Builder<InviteUserRequest, InviteUserRequestBuilder> {
  _$InviteUserRequest? _$v;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  ListBuilder<String>? _roleCodes;
  ListBuilder<String> get roleCodes =>
      _$this._roleCodes ??= ListBuilder<String>();
  set roleCodes(ListBuilder<String>? roleCodes) =>
      _$this._roleCodes = roleCodes;

  InviteUserRequestBuilder() {
    InviteUserRequest._defaults(this);
  }

  InviteUserRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _email = $v.email;
      _fullName = $v.fullName;
      _roleCodes = $v.roleCodes.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InviteUserRequest other) {
    _$v = other as _$InviteUserRequest;
  }

  @override
  void update(void Function(InviteUserRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InviteUserRequest build() => _build();

  _$InviteUserRequest _build() {
    _$InviteUserRequest _$result;
    try {
      _$result = _$v ??
          _$InviteUserRequest._(
            email: BuiltValueNullFieldError.checkNotNull(
                email, r'InviteUserRequest', 'email'),
            fullName: BuiltValueNullFieldError.checkNotNull(
                fullName, r'InviteUserRequest', 'fullName'),
            roleCodes: roleCodes.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'roleCodes';
        roleCodes.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InviteUserRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
