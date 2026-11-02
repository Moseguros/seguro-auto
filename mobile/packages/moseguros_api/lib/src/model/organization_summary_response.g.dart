// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'organization_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const OrganizationSummaryResponseStatusEnum
    _$organizationSummaryResponseStatusEnum_ACTIVE =
    const OrganizationSummaryResponseStatusEnum._('ACTIVE');
const OrganizationSummaryResponseStatusEnum
    _$organizationSummaryResponseStatusEnum_SUSPENDED =
    const OrganizationSummaryResponseStatusEnum._('SUSPENDED');

OrganizationSummaryResponseStatusEnum
    _$organizationSummaryResponseStatusEnumValueOf(String name) {
  switch (name) {
    case 'ACTIVE':
      return _$organizationSummaryResponseStatusEnum_ACTIVE;
    case 'SUSPENDED':
      return _$organizationSummaryResponseStatusEnum_SUSPENDED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OrganizationSummaryResponseStatusEnum>
    _$organizationSummaryResponseStatusEnumValues = BuiltSet<
        OrganizationSummaryResponseStatusEnum>(const <OrganizationSummaryResponseStatusEnum>[
  _$organizationSummaryResponseStatusEnum_ACTIVE,
  _$organizationSummaryResponseStatusEnum_SUSPENDED,
]);

const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_INSURER =
    const OrganizationSummaryResponseTypeEnum._('INSURER');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_BROKER =
    const OrganizationSummaryResponseTypeEnum._('BROKER');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_AGENT =
    const OrganizationSummaryResponseTypeEnum._('AGENT');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_WORKSHOP =
    const OrganizationSummaryResponseTypeEnum._('WORKSHOP');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_ASSESSOR =
    const OrganizationSummaryResponseTypeEnum._('ASSESSOR');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_ASSISTANCE =
    const OrganizationSummaryResponseTypeEnum._('ASSISTANCE');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_OMBUDSMAN =
    const OrganizationSummaryResponseTypeEnum._('OMBUDSMAN');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_REGULATOR =
    const OrganizationSummaryResponseTypeEnum._('REGULATOR');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_POLICE =
    const OrganizationSummaryResponseTypeEnum._('POLICE');
const OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnum_CORPORATE_CLIENT =
    const OrganizationSummaryResponseTypeEnum._('CORPORATE_CLIENT');

OrganizationSummaryResponseTypeEnum
    _$organizationSummaryResponseTypeEnumValueOf(String name) {
  switch (name) {
    case 'INSURER':
      return _$organizationSummaryResponseTypeEnum_INSURER;
    case 'BROKER':
      return _$organizationSummaryResponseTypeEnum_BROKER;
    case 'AGENT':
      return _$organizationSummaryResponseTypeEnum_AGENT;
    case 'WORKSHOP':
      return _$organizationSummaryResponseTypeEnum_WORKSHOP;
    case 'ASSESSOR':
      return _$organizationSummaryResponseTypeEnum_ASSESSOR;
    case 'ASSISTANCE':
      return _$organizationSummaryResponseTypeEnum_ASSISTANCE;
    case 'OMBUDSMAN':
      return _$organizationSummaryResponseTypeEnum_OMBUDSMAN;
    case 'REGULATOR':
      return _$organizationSummaryResponseTypeEnum_REGULATOR;
    case 'POLICE':
      return _$organizationSummaryResponseTypeEnum_POLICE;
    case 'CORPORATE_CLIENT':
      return _$organizationSummaryResponseTypeEnum_CORPORATE_CLIENT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OrganizationSummaryResponseTypeEnum>
    _$organizationSummaryResponseTypeEnumValues = BuiltSet<
        OrganizationSummaryResponseTypeEnum>(const <OrganizationSummaryResponseTypeEnum>[
  _$organizationSummaryResponseTypeEnum_INSURER,
  _$organizationSummaryResponseTypeEnum_BROKER,
  _$organizationSummaryResponseTypeEnum_AGENT,
  _$organizationSummaryResponseTypeEnum_WORKSHOP,
  _$organizationSummaryResponseTypeEnum_ASSESSOR,
  _$organizationSummaryResponseTypeEnum_ASSISTANCE,
  _$organizationSummaryResponseTypeEnum_OMBUDSMAN,
  _$organizationSummaryResponseTypeEnum_REGULATOR,
  _$organizationSummaryResponseTypeEnum_POLICE,
  _$organizationSummaryResponseTypeEnum_CORPORATE_CLIENT,
]);

Serializer<OrganizationSummaryResponseStatusEnum>
    _$organizationSummaryResponseStatusEnumSerializer =
    _$OrganizationSummaryResponseStatusEnumSerializer();
Serializer<OrganizationSummaryResponseTypeEnum>
    _$organizationSummaryResponseTypeEnumSerializer =
    _$OrganizationSummaryResponseTypeEnumSerializer();

class _$OrganizationSummaryResponseStatusEnumSerializer
    implements PrimitiveSerializer<OrganizationSummaryResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'ACTIVE': 'ACTIVE',
    'SUSPENDED': 'SUSPENDED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'ACTIVE': 'ACTIVE',
    'SUSPENDED': 'SUSPENDED',
  };

  @override
  final Iterable<Type> types = const <Type>[
    OrganizationSummaryResponseStatusEnum
  ];
  @override
  final String wireName = 'OrganizationSummaryResponseStatusEnum';

  @override
  Object serialize(
          Serializers serializers, OrganizationSummaryResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OrganizationSummaryResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OrganizationSummaryResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OrganizationSummaryResponseTypeEnumSerializer
    implements PrimitiveSerializer<OrganizationSummaryResponseTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'INSURER': 'INSURER',
    'BROKER': 'BROKER',
    'AGENT': 'AGENT',
    'WORKSHOP': 'WORKSHOP',
    'ASSESSOR': 'ASSESSOR',
    'ASSISTANCE': 'ASSISTANCE',
    'OMBUDSMAN': 'OMBUDSMAN',
    'REGULATOR': 'REGULATOR',
    'POLICE': 'POLICE',
    'CORPORATE_CLIENT': 'CORPORATE_CLIENT',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'INSURER': 'INSURER',
    'BROKER': 'BROKER',
    'AGENT': 'AGENT',
    'WORKSHOP': 'WORKSHOP',
    'ASSESSOR': 'ASSESSOR',
    'ASSISTANCE': 'ASSISTANCE',
    'OMBUDSMAN': 'OMBUDSMAN',
    'REGULATOR': 'REGULATOR',
    'POLICE': 'POLICE',
    'CORPORATE_CLIENT': 'CORPORATE_CLIENT',
  };

  @override
  final Iterable<Type> types = const <Type>[
    OrganizationSummaryResponseTypeEnum
  ];
  @override
  final String wireName = 'OrganizationSummaryResponseTypeEnum';

  @override
  Object serialize(
          Serializers serializers, OrganizationSummaryResponseTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OrganizationSummaryResponseTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OrganizationSummaryResponseTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OrganizationSummaryResponse extends OrganizationSummaryResponse {
  @override
  final DateTime createdAt;
  @override
  final String displayName;
  @override
  final String id;
  @override
  final String nuit;
  @override
  final OrganizationSummaryResponseStatusEnum status;
  @override
  final OrganizationSummaryResponseTypeEnum type;

  factory _$OrganizationSummaryResponse(
          [void Function(OrganizationSummaryResponseBuilder)? updates]) =>
      (OrganizationSummaryResponseBuilder()..update(updates))._build();

  _$OrganizationSummaryResponse._(
      {required this.createdAt,
      required this.displayName,
      required this.id,
      required this.nuit,
      required this.status,
      required this.type})
      : super._();
  @override
  OrganizationSummaryResponse rebuild(
          void Function(OrganizationSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OrganizationSummaryResponseBuilder toBuilder() =>
      OrganizationSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OrganizationSummaryResponse &&
        createdAt == other.createdAt &&
        displayName == other.displayName &&
        id == other.id &&
        nuit == other.nuit &&
        status == other.status &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, nuit.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OrganizationSummaryResponse')
          ..add('createdAt', createdAt)
          ..add('displayName', displayName)
          ..add('id', id)
          ..add('nuit', nuit)
          ..add('status', status)
          ..add('type', type))
        .toString();
  }
}

class OrganizationSummaryResponseBuilder
    implements
        Builder<OrganizationSummaryResponse,
            OrganizationSummaryResponseBuilder> {
  _$OrganizationSummaryResponse? _$v;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _nuit;
  String? get nuit => _$this._nuit;
  set nuit(String? nuit) => _$this._nuit = nuit;

  OrganizationSummaryResponseStatusEnum? _status;
  OrganizationSummaryResponseStatusEnum? get status => _$this._status;
  set status(OrganizationSummaryResponseStatusEnum? status) =>
      _$this._status = status;

  OrganizationSummaryResponseTypeEnum? _type;
  OrganizationSummaryResponseTypeEnum? get type => _$this._type;
  set type(OrganizationSummaryResponseTypeEnum? type) => _$this._type = type;

  OrganizationSummaryResponseBuilder() {
    OrganizationSummaryResponse._defaults(this);
  }

  OrganizationSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _createdAt = $v.createdAt;
      _displayName = $v.displayName;
      _id = $v.id;
      _nuit = $v.nuit;
      _status = $v.status;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OrganizationSummaryResponse other) {
    _$v = other as _$OrganizationSummaryResponse;
  }

  @override
  void update(void Function(OrganizationSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OrganizationSummaryResponse build() => _build();

  _$OrganizationSummaryResponse _build() {
    final _$result = _$v ??
        _$OrganizationSummaryResponse._(
          createdAt: BuiltValueNullFieldError.checkNotNull(
              createdAt, r'OrganizationSummaryResponse', 'createdAt'),
          displayName: BuiltValueNullFieldError.checkNotNull(
              displayName, r'OrganizationSummaryResponse', 'displayName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'OrganizationSummaryResponse', 'id'),
          nuit: BuiltValueNullFieldError.checkNotNull(
              nuit, r'OrganizationSummaryResponse', 'nuit'),
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'OrganizationSummaryResponse', 'status'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'OrganizationSummaryResponse', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
