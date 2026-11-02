// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'staff_user_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const StaffUserResponseStatusEnum
    _$staffUserResponseStatusEnum_PENDING_ACTIVATION =
    const StaffUserResponseStatusEnum._('PENDING_ACTIVATION');
const StaffUserResponseStatusEnum _$staffUserResponseStatusEnum_ACTIVE =
    const StaffUserResponseStatusEnum._('ACTIVE');
const StaffUserResponseStatusEnum _$staffUserResponseStatusEnum_LOCKED =
    const StaffUserResponseStatusEnum._('LOCKED');
const StaffUserResponseStatusEnum _$staffUserResponseStatusEnum_DISABLED =
    const StaffUserResponseStatusEnum._('DISABLED');

StaffUserResponseStatusEnum _$staffUserResponseStatusEnumValueOf(String name) {
  switch (name) {
    case 'PENDING_ACTIVATION':
      return _$staffUserResponseStatusEnum_PENDING_ACTIVATION;
    case 'ACTIVE':
      return _$staffUserResponseStatusEnum_ACTIVE;
    case 'LOCKED':
      return _$staffUserResponseStatusEnum_LOCKED;
    case 'DISABLED':
      return _$staffUserResponseStatusEnum_DISABLED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<StaffUserResponseStatusEnum>
    _$staffUserResponseStatusEnumValues =
    BuiltSet<StaffUserResponseStatusEnum>(const <StaffUserResponseStatusEnum>[
  _$staffUserResponseStatusEnum_PENDING_ACTIVATION,
  _$staffUserResponseStatusEnum_ACTIVE,
  _$staffUserResponseStatusEnum_LOCKED,
  _$staffUserResponseStatusEnum_DISABLED,
]);

Serializer<StaffUserResponseStatusEnum>
    _$staffUserResponseStatusEnumSerializer =
    _$StaffUserResponseStatusEnumSerializer();

class _$StaffUserResponseStatusEnumSerializer
    implements PrimitiveSerializer<StaffUserResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PENDING_ACTIVATION': 'PENDING_ACTIVATION',
    'ACTIVE': 'ACTIVE',
    'LOCKED': 'LOCKED',
    'DISABLED': 'DISABLED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PENDING_ACTIVATION': 'PENDING_ACTIVATION',
    'ACTIVE': 'ACTIVE',
    'LOCKED': 'LOCKED',
    'DISABLED': 'DISABLED',
  };

  @override
  final Iterable<Type> types = const <Type>[StaffUserResponseStatusEnum];
  @override
  final String wireName = 'StaffUserResponseStatusEnum';

  @override
  Object serialize(Serializers serializers, StaffUserResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  StaffUserResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      StaffUserResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$StaffUserResponse extends StaffUserResponse {
  @override
  final DateTime createdAt;
  @override
  final String? displayName;
  @override
  final String email;
  @override
  final String id;
  @override
  final DateTime? lastLoginAt;
  @override
  final BuiltList<String> roles;
  @override
  final StaffUserResponseStatusEnum status;
  @override
  final int version;

  factory _$StaffUserResponse(
          [void Function(StaffUserResponseBuilder)? updates]) =>
      (StaffUserResponseBuilder()..update(updates))._build();

  _$StaffUserResponse._(
      {required this.createdAt,
      this.displayName,
      required this.email,
      required this.id,
      this.lastLoginAt,
      required this.roles,
      required this.status,
      required this.version})
      : super._();
  @override
  StaffUserResponse rebuild(void Function(StaffUserResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  StaffUserResponseBuilder toBuilder() =>
      StaffUserResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is StaffUserResponse &&
        createdAt == other.createdAt &&
        displayName == other.displayName &&
        email == other.email &&
        id == other.id &&
        lastLoginAt == other.lastLoginAt &&
        roles == other.roles &&
        status == other.status &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, lastLoginAt.hashCode);
    _$hash = $jc(_$hash, roles.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'StaffUserResponse')
          ..add('createdAt', createdAt)
          ..add('displayName', displayName)
          ..add('email', email)
          ..add('id', id)
          ..add('lastLoginAt', lastLoginAt)
          ..add('roles', roles)
          ..add('status', status)
          ..add('version', version))
        .toString();
  }
}

class StaffUserResponseBuilder
    implements Builder<StaffUserResponse, StaffUserResponseBuilder> {
  _$StaffUserResponse? _$v;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  DateTime? _lastLoginAt;
  DateTime? get lastLoginAt => _$this._lastLoginAt;
  set lastLoginAt(DateTime? lastLoginAt) => _$this._lastLoginAt = lastLoginAt;

  ListBuilder<String>? _roles;
  ListBuilder<String> get roles => _$this._roles ??= ListBuilder<String>();
  set roles(ListBuilder<String>? roles) => _$this._roles = roles;

  StaffUserResponseStatusEnum? _status;
  StaffUserResponseStatusEnum? get status => _$this._status;
  set status(StaffUserResponseStatusEnum? status) => _$this._status = status;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  StaffUserResponseBuilder() {
    StaffUserResponse._defaults(this);
  }

  StaffUserResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _createdAt = $v.createdAt;
      _displayName = $v.displayName;
      _email = $v.email;
      _id = $v.id;
      _lastLoginAt = $v.lastLoginAt;
      _roles = $v.roles.toBuilder();
      _status = $v.status;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(StaffUserResponse other) {
    _$v = other as _$StaffUserResponse;
  }

  @override
  void update(void Function(StaffUserResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  StaffUserResponse build() => _build();

  _$StaffUserResponse _build() {
    _$StaffUserResponse _$result;
    try {
      _$result = _$v ??
          _$StaffUserResponse._(
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'StaffUserResponse', 'createdAt'),
            displayName: displayName,
            email: BuiltValueNullFieldError.checkNotNull(
                email, r'StaffUserResponse', 'email'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'StaffUserResponse', 'id'),
            lastLoginAt: lastLoginAt,
            roles: roles.build(),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'StaffUserResponse', 'status'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'StaffUserResponse', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'roles';
        roles.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'StaffUserResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
