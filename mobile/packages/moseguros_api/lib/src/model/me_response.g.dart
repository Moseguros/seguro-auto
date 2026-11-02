// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'me_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const MeResponseTypeEnum _$meResponseTypeEnum_CUSTOMER =
    const MeResponseTypeEnum._('CUSTOMER');
const MeResponseTypeEnum _$meResponseTypeEnum_STAFF =
    const MeResponseTypeEnum._('STAFF');

MeResponseTypeEnum _$meResponseTypeEnumValueOf(String name) {
  switch (name) {
    case 'CUSTOMER':
      return _$meResponseTypeEnum_CUSTOMER;
    case 'STAFF':
      return _$meResponseTypeEnum_STAFF;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<MeResponseTypeEnum> _$meResponseTypeEnumValues =
    BuiltSet<MeResponseTypeEnum>(const <MeResponseTypeEnum>[
  _$meResponseTypeEnum_CUSTOMER,
  _$meResponseTypeEnum_STAFF,
]);

Serializer<MeResponseTypeEnum> _$meResponseTypeEnumSerializer =
    _$MeResponseTypeEnumSerializer();

class _$MeResponseTypeEnumSerializer
    implements PrimitiveSerializer<MeResponseTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CUSTOMER': 'CUSTOMER',
    'STAFF': 'STAFF',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CUSTOMER': 'CUSTOMER',
    'STAFF': 'STAFF',
  };

  @override
  final Iterable<Type> types = const <Type>[MeResponseTypeEnum];
  @override
  final String wireName = 'MeResponseTypeEnum';

  @override
  Object serialize(Serializers serializers, MeResponseTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  MeResponseTypeEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      MeResponseTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$MeResponse extends MeResponse {
  @override
  final String? displayName;
  @override
  final String? email;
  @override
  final bool emailVerified;
  @override
  final String id;
  @override
  final String? msisdn;
  @override
  final bool msisdnVerified;
  @override
  final MeOrganization? organization;
  @override
  final BuiltList<String> permissions;
  @override
  final String preferredLanguage;
  @override
  final bool? profileComplete;
  @override
  final BuiltList<String> roles;
  @override
  final MeResponseTypeEnum type;

  factory _$MeResponse([void Function(MeResponseBuilder)? updates]) =>
      (MeResponseBuilder()..update(updates))._build();

  _$MeResponse._(
      {this.displayName,
      this.email,
      required this.emailVerified,
      required this.id,
      this.msisdn,
      required this.msisdnVerified,
      this.organization,
      required this.permissions,
      required this.preferredLanguage,
      this.profileComplete,
      required this.roles,
      required this.type})
      : super._();
  @override
  MeResponse rebuild(void Function(MeResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  MeResponseBuilder toBuilder() => MeResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is MeResponse &&
        displayName == other.displayName &&
        email == other.email &&
        emailVerified == other.emailVerified &&
        id == other.id &&
        msisdn == other.msisdn &&
        msisdnVerified == other.msisdnVerified &&
        organization == other.organization &&
        permissions == other.permissions &&
        preferredLanguage == other.preferredLanguage &&
        profileComplete == other.profileComplete &&
        roles == other.roles &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, emailVerified.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, msisdn.hashCode);
    _$hash = $jc(_$hash, msisdnVerified.hashCode);
    _$hash = $jc(_$hash, organization.hashCode);
    _$hash = $jc(_$hash, permissions.hashCode);
    _$hash = $jc(_$hash, preferredLanguage.hashCode);
    _$hash = $jc(_$hash, profileComplete.hashCode);
    _$hash = $jc(_$hash, roles.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'MeResponse')
          ..add('displayName', displayName)
          ..add('email', email)
          ..add('emailVerified', emailVerified)
          ..add('id', id)
          ..add('msisdn', msisdn)
          ..add('msisdnVerified', msisdnVerified)
          ..add('organization', organization)
          ..add('permissions', permissions)
          ..add('preferredLanguage', preferredLanguage)
          ..add('profileComplete', profileComplete)
          ..add('roles', roles)
          ..add('type', type))
        .toString();
  }
}

class MeResponseBuilder implements Builder<MeResponse, MeResponseBuilder> {
  _$MeResponse? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  bool? _emailVerified;
  bool? get emailVerified => _$this._emailVerified;
  set emailVerified(bool? emailVerified) =>
      _$this._emailVerified = emailVerified;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _msisdn;
  String? get msisdn => _$this._msisdn;
  set msisdn(String? msisdn) => _$this._msisdn = msisdn;

  bool? _msisdnVerified;
  bool? get msisdnVerified => _$this._msisdnVerified;
  set msisdnVerified(bool? msisdnVerified) =>
      _$this._msisdnVerified = msisdnVerified;

  MeOrganizationBuilder? _organization;
  MeOrganizationBuilder get organization =>
      _$this._organization ??= MeOrganizationBuilder();
  set organization(MeOrganizationBuilder? organization) =>
      _$this._organization = organization;

  ListBuilder<String>? _permissions;
  ListBuilder<String> get permissions =>
      _$this._permissions ??= ListBuilder<String>();
  set permissions(ListBuilder<String>? permissions) =>
      _$this._permissions = permissions;

  String? _preferredLanguage;
  String? get preferredLanguage => _$this._preferredLanguage;
  set preferredLanguage(String? preferredLanguage) =>
      _$this._preferredLanguage = preferredLanguage;

  bool? _profileComplete;
  bool? get profileComplete => _$this._profileComplete;
  set profileComplete(bool? profileComplete) =>
      _$this._profileComplete = profileComplete;

  ListBuilder<String>? _roles;
  ListBuilder<String> get roles => _$this._roles ??= ListBuilder<String>();
  set roles(ListBuilder<String>? roles) => _$this._roles = roles;

  MeResponseTypeEnum? _type;
  MeResponseTypeEnum? get type => _$this._type;
  set type(MeResponseTypeEnum? type) => _$this._type = type;

  MeResponseBuilder() {
    MeResponse._defaults(this);
  }

  MeResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _email = $v.email;
      _emailVerified = $v.emailVerified;
      _id = $v.id;
      _msisdn = $v.msisdn;
      _msisdnVerified = $v.msisdnVerified;
      _organization = $v.organization?.toBuilder();
      _permissions = $v.permissions.toBuilder();
      _preferredLanguage = $v.preferredLanguage;
      _profileComplete = $v.profileComplete;
      _roles = $v.roles.toBuilder();
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(MeResponse other) {
    _$v = other as _$MeResponse;
  }

  @override
  void update(void Function(MeResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  MeResponse build() => _build();

  _$MeResponse _build() {
    _$MeResponse _$result;
    try {
      _$result = _$v ??
          _$MeResponse._(
            displayName: displayName,
            email: email,
            emailVerified: BuiltValueNullFieldError.checkNotNull(
                emailVerified, r'MeResponse', 'emailVerified'),
            id: BuiltValueNullFieldError.checkNotNull(id, r'MeResponse', 'id'),
            msisdn: msisdn,
            msisdnVerified: BuiltValueNullFieldError.checkNotNull(
                msisdnVerified, r'MeResponse', 'msisdnVerified'),
            organization: _organization?.build(),
            permissions: permissions.build(),
            preferredLanguage: BuiltValueNullFieldError.checkNotNull(
                preferredLanguage, r'MeResponse', 'preferredLanguage'),
            profileComplete: profileComplete,
            roles: roles.build(),
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'MeResponse', 'type'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'organization';
        _organization?.build();
        _$failedField = 'permissions';
        permissions.build();

        _$failedField = 'roles';
        roles.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'MeResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
