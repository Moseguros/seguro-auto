// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'invitation_details_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InvitationDetailsResponse extends InvitationDetailsResponse {
  @override
  final String email;
  @override
  final DateTime expiresAt;
  @override
  final String fullName;
  @override
  final InvitationOrganization? organization;

  factory _$InvitationDetailsResponse(
          [void Function(InvitationDetailsResponseBuilder)? updates]) =>
      (InvitationDetailsResponseBuilder()..update(updates))._build();

  _$InvitationDetailsResponse._(
      {required this.email,
      required this.expiresAt,
      required this.fullName,
      this.organization})
      : super._();
  @override
  InvitationDetailsResponse rebuild(
          void Function(InvitationDetailsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InvitationDetailsResponseBuilder toBuilder() =>
      InvitationDetailsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InvitationDetailsResponse &&
        email == other.email &&
        expiresAt == other.expiresAt &&
        fullName == other.fullName &&
        organization == other.organization;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, organization.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InvitationDetailsResponse')
          ..add('email', email)
          ..add('expiresAt', expiresAt)
          ..add('fullName', fullName)
          ..add('organization', organization))
        .toString();
  }
}

class InvitationDetailsResponseBuilder
    implements
        Builder<InvitationDetailsResponse, InvitationDetailsResponseBuilder> {
  _$InvitationDetailsResponse? _$v;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  InvitationOrganizationBuilder? _organization;
  InvitationOrganizationBuilder get organization =>
      _$this._organization ??= InvitationOrganizationBuilder();
  set organization(InvitationOrganizationBuilder? organization) =>
      _$this._organization = organization;

  InvitationDetailsResponseBuilder() {
    InvitationDetailsResponse._defaults(this);
  }

  InvitationDetailsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _email = $v.email;
      _expiresAt = $v.expiresAt;
      _fullName = $v.fullName;
      _organization = $v.organization?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InvitationDetailsResponse other) {
    _$v = other as _$InvitationDetailsResponse;
  }

  @override
  void update(void Function(InvitationDetailsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InvitationDetailsResponse build() => _build();

  _$InvitationDetailsResponse _build() {
    _$InvitationDetailsResponse _$result;
    try {
      _$result = _$v ??
          _$InvitationDetailsResponse._(
            email: BuiltValueNullFieldError.checkNotNull(
                email, r'InvitationDetailsResponse', 'email'),
            expiresAt: BuiltValueNullFieldError.checkNotNull(
                expiresAt, r'InvitationDetailsResponse', 'expiresAt'),
            fullName: BuiltValueNullFieldError.checkNotNull(
                fullName, r'InvitationDetailsResponse', 'fullName'),
            organization: _organization?.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'organization';
        _organization?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InvitationDetailsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
