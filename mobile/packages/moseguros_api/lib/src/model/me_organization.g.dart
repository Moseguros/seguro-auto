// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'me_organization.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const MeOrganizationTypeEnum _$meOrganizationTypeEnum_INSURER =
    const MeOrganizationTypeEnum._('INSURER');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_BROKER =
    const MeOrganizationTypeEnum._('BROKER');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_AGENT =
    const MeOrganizationTypeEnum._('AGENT');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_WORKSHOP =
    const MeOrganizationTypeEnum._('WORKSHOP');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_ASSESSOR =
    const MeOrganizationTypeEnum._('ASSESSOR');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_ASSISTANCE =
    const MeOrganizationTypeEnum._('ASSISTANCE');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_OMBUDSMAN =
    const MeOrganizationTypeEnum._('OMBUDSMAN');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_REGULATOR =
    const MeOrganizationTypeEnum._('REGULATOR');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_POLICE =
    const MeOrganizationTypeEnum._('POLICE');
const MeOrganizationTypeEnum _$meOrganizationTypeEnum_CORPORATE_CLIENT =
    const MeOrganizationTypeEnum._('CORPORATE_CLIENT');

MeOrganizationTypeEnum _$meOrganizationTypeEnumValueOf(String name) {
  switch (name) {
    case 'INSURER':
      return _$meOrganizationTypeEnum_INSURER;
    case 'BROKER':
      return _$meOrganizationTypeEnum_BROKER;
    case 'AGENT':
      return _$meOrganizationTypeEnum_AGENT;
    case 'WORKSHOP':
      return _$meOrganizationTypeEnum_WORKSHOP;
    case 'ASSESSOR':
      return _$meOrganizationTypeEnum_ASSESSOR;
    case 'ASSISTANCE':
      return _$meOrganizationTypeEnum_ASSISTANCE;
    case 'OMBUDSMAN':
      return _$meOrganizationTypeEnum_OMBUDSMAN;
    case 'REGULATOR':
      return _$meOrganizationTypeEnum_REGULATOR;
    case 'POLICE':
      return _$meOrganizationTypeEnum_POLICE;
    case 'CORPORATE_CLIENT':
      return _$meOrganizationTypeEnum_CORPORATE_CLIENT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<MeOrganizationTypeEnum> _$meOrganizationTypeEnumValues =
    BuiltSet<MeOrganizationTypeEnum>(const <MeOrganizationTypeEnum>[
  _$meOrganizationTypeEnum_INSURER,
  _$meOrganizationTypeEnum_BROKER,
  _$meOrganizationTypeEnum_AGENT,
  _$meOrganizationTypeEnum_WORKSHOP,
  _$meOrganizationTypeEnum_ASSESSOR,
  _$meOrganizationTypeEnum_ASSISTANCE,
  _$meOrganizationTypeEnum_OMBUDSMAN,
  _$meOrganizationTypeEnum_REGULATOR,
  _$meOrganizationTypeEnum_POLICE,
  _$meOrganizationTypeEnum_CORPORATE_CLIENT,
]);

Serializer<MeOrganizationTypeEnum> _$meOrganizationTypeEnumSerializer =
    _$MeOrganizationTypeEnumSerializer();

class _$MeOrganizationTypeEnumSerializer
    implements PrimitiveSerializer<MeOrganizationTypeEnum> {
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
  final Iterable<Type> types = const <Type>[MeOrganizationTypeEnum];
  @override
  final String wireName = 'MeOrganizationTypeEnum';

  @override
  Object serialize(Serializers serializers, MeOrganizationTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  MeOrganizationTypeEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      MeOrganizationTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$MeOrganization extends MeOrganization {
  @override
  final String displayName;
  @override
  final String id;
  @override
  final MeOrganizationTypeEnum type;

  factory _$MeOrganization([void Function(MeOrganizationBuilder)? updates]) =>
      (MeOrganizationBuilder()..update(updates))._build();

  _$MeOrganization._(
      {required this.displayName, required this.id, required this.type})
      : super._();
  @override
  MeOrganization rebuild(void Function(MeOrganizationBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  MeOrganizationBuilder toBuilder() => MeOrganizationBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is MeOrganization &&
        displayName == other.displayName &&
        id == other.id &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'MeOrganization')
          ..add('displayName', displayName)
          ..add('id', id)
          ..add('type', type))
        .toString();
  }
}

class MeOrganizationBuilder
    implements Builder<MeOrganization, MeOrganizationBuilder> {
  _$MeOrganization? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  MeOrganizationTypeEnum? _type;
  MeOrganizationTypeEnum? get type => _$this._type;
  set type(MeOrganizationTypeEnum? type) => _$this._type = type;

  MeOrganizationBuilder() {
    MeOrganization._defaults(this);
  }

  MeOrganizationBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _id = $v.id;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(MeOrganization other) {
    _$v = other as _$MeOrganization;
  }

  @override
  void update(void Function(MeOrganizationBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  MeOrganization build() => _build();

  _$MeOrganization _build() {
    final _$result = _$v ??
        _$MeOrganization._(
          displayName: BuiltValueNullFieldError.checkNotNull(
              displayName, r'MeOrganization', 'displayName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'MeOrganization', 'id'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'MeOrganization', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
