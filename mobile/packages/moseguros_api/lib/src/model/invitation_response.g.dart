// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'invitation_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InvitationResponseStatusEnum _$invitationResponseStatusEnum_PENDING =
    const InvitationResponseStatusEnum._('PENDING');
const InvitationResponseStatusEnum _$invitationResponseStatusEnum_ACCEPTED =
    const InvitationResponseStatusEnum._('ACCEPTED');
const InvitationResponseStatusEnum _$invitationResponseStatusEnum_EXPIRED =
    const InvitationResponseStatusEnum._('EXPIRED');
const InvitationResponseStatusEnum _$invitationResponseStatusEnum_REVOKED =
    const InvitationResponseStatusEnum._('REVOKED');

InvitationResponseStatusEnum _$invitationResponseStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'PENDING':
      return _$invitationResponseStatusEnum_PENDING;
    case 'ACCEPTED':
      return _$invitationResponseStatusEnum_ACCEPTED;
    case 'EXPIRED':
      return _$invitationResponseStatusEnum_EXPIRED;
    case 'REVOKED':
      return _$invitationResponseStatusEnum_REVOKED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InvitationResponseStatusEnum>
    _$invitationResponseStatusEnumValues =
    BuiltSet<InvitationResponseStatusEnum>(const <InvitationResponseStatusEnum>[
  _$invitationResponseStatusEnum_PENDING,
  _$invitationResponseStatusEnum_ACCEPTED,
  _$invitationResponseStatusEnum_EXPIRED,
  _$invitationResponseStatusEnum_REVOKED,
]);

Serializer<InvitationResponseStatusEnum>
    _$invitationResponseStatusEnumSerializer =
    _$InvitationResponseStatusEnumSerializer();

class _$InvitationResponseStatusEnumSerializer
    implements PrimitiveSerializer<InvitationResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PENDING': 'PENDING',
    'ACCEPTED': 'ACCEPTED',
    'EXPIRED': 'EXPIRED',
    'REVOKED': 'REVOKED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PENDING': 'PENDING',
    'ACCEPTED': 'ACCEPTED',
    'EXPIRED': 'EXPIRED',
    'REVOKED': 'REVOKED',
  };

  @override
  final Iterable<Type> types = const <Type>[InvitationResponseStatusEnum];
  @override
  final String wireName = 'InvitationResponseStatusEnum';

  @override
  Object serialize(Serializers serializers, InvitationResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InvitationResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InvitationResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InvitationResponse extends InvitationResponse {
  @override
  final DateTime createdAt;
  @override
  final String email;
  @override
  final DateTime expiresAt;
  @override
  final String fullName;
  @override
  final String id;
  @override
  final String? organizationId;
  @override
  final BuiltList<String> roleCodes;
  @override
  final InvitationResponseStatusEnum status;

  factory _$InvitationResponse(
          [void Function(InvitationResponseBuilder)? updates]) =>
      (InvitationResponseBuilder()..update(updates))._build();

  _$InvitationResponse._(
      {required this.createdAt,
      required this.email,
      required this.expiresAt,
      required this.fullName,
      required this.id,
      this.organizationId,
      required this.roleCodes,
      required this.status})
      : super._();
  @override
  InvitationResponse rebuild(
          void Function(InvitationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InvitationResponseBuilder toBuilder() =>
      InvitationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InvitationResponse &&
        createdAt == other.createdAt &&
        email == other.email &&
        expiresAt == other.expiresAt &&
        fullName == other.fullName &&
        id == other.id &&
        organizationId == other.organizationId &&
        roleCodes == other.roleCodes &&
        status == other.status;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, organizationId.hashCode);
    _$hash = $jc(_$hash, roleCodes.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InvitationResponse')
          ..add('createdAt', createdAt)
          ..add('email', email)
          ..add('expiresAt', expiresAt)
          ..add('fullName', fullName)
          ..add('id', id)
          ..add('organizationId', organizationId)
          ..add('roleCodes', roleCodes)
          ..add('status', status))
        .toString();
  }
}

class InvitationResponseBuilder
    implements Builder<InvitationResponse, InvitationResponseBuilder> {
  _$InvitationResponse? _$v;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _organizationId;
  String? get organizationId => _$this._organizationId;
  set organizationId(String? organizationId) =>
      _$this._organizationId = organizationId;

  ListBuilder<String>? _roleCodes;
  ListBuilder<String> get roleCodes =>
      _$this._roleCodes ??= ListBuilder<String>();
  set roleCodes(ListBuilder<String>? roleCodes) =>
      _$this._roleCodes = roleCodes;

  InvitationResponseStatusEnum? _status;
  InvitationResponseStatusEnum? get status => _$this._status;
  set status(InvitationResponseStatusEnum? status) => _$this._status = status;

  InvitationResponseBuilder() {
    InvitationResponse._defaults(this);
  }

  InvitationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _createdAt = $v.createdAt;
      _email = $v.email;
      _expiresAt = $v.expiresAt;
      _fullName = $v.fullName;
      _id = $v.id;
      _organizationId = $v.organizationId;
      _roleCodes = $v.roleCodes.toBuilder();
      _status = $v.status;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InvitationResponse other) {
    _$v = other as _$InvitationResponse;
  }

  @override
  void update(void Function(InvitationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InvitationResponse build() => _build();

  _$InvitationResponse _build() {
    _$InvitationResponse _$result;
    try {
      _$result = _$v ??
          _$InvitationResponse._(
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'InvitationResponse', 'createdAt'),
            email: BuiltValueNullFieldError.checkNotNull(
                email, r'InvitationResponse', 'email'),
            expiresAt: BuiltValueNullFieldError.checkNotNull(
                expiresAt, r'InvitationResponse', 'expiresAt'),
            fullName: BuiltValueNullFieldError.checkNotNull(
                fullName, r'InvitationResponse', 'fullName'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InvitationResponse', 'id'),
            organizationId: organizationId,
            roleCodes: roleCodes.build(),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'InvitationResponse', 'status'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'roleCodes';
        roleCodes.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InvitationResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
