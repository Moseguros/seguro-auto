// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'invitation_organization.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InvitationOrganizationTypeEnum _$invitationOrganizationTypeEnum_INSURER =
    const InvitationOrganizationTypeEnum._('INSURER');
const InvitationOrganizationTypeEnum _$invitationOrganizationTypeEnum_BROKER =
    const InvitationOrganizationTypeEnum._('BROKER');
const InvitationOrganizationTypeEnum _$invitationOrganizationTypeEnum_AGENT =
    const InvitationOrganizationTypeEnum._('AGENT');
const InvitationOrganizationTypeEnum _$invitationOrganizationTypeEnum_WORKSHOP =
    const InvitationOrganizationTypeEnum._('WORKSHOP');
const InvitationOrganizationTypeEnum _$invitationOrganizationTypeEnum_ASSESSOR =
    const InvitationOrganizationTypeEnum._('ASSESSOR');
const InvitationOrganizationTypeEnum
    _$invitationOrganizationTypeEnum_ASSISTANCE =
    const InvitationOrganizationTypeEnum._('ASSISTANCE');
const InvitationOrganizationTypeEnum
    _$invitationOrganizationTypeEnum_OMBUDSMAN =
    const InvitationOrganizationTypeEnum._('OMBUDSMAN');
const InvitationOrganizationTypeEnum
    _$invitationOrganizationTypeEnum_REGULATOR =
    const InvitationOrganizationTypeEnum._('REGULATOR');
const InvitationOrganizationTypeEnum _$invitationOrganizationTypeEnum_POLICE =
    const InvitationOrganizationTypeEnum._('POLICE');
const InvitationOrganizationTypeEnum
    _$invitationOrganizationTypeEnum_CORPORATE_CLIENT =
    const InvitationOrganizationTypeEnum._('CORPORATE_CLIENT');

InvitationOrganizationTypeEnum _$invitationOrganizationTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'INSURER':
      return _$invitationOrganizationTypeEnum_INSURER;
    case 'BROKER':
      return _$invitationOrganizationTypeEnum_BROKER;
    case 'AGENT':
      return _$invitationOrganizationTypeEnum_AGENT;
    case 'WORKSHOP':
      return _$invitationOrganizationTypeEnum_WORKSHOP;
    case 'ASSESSOR':
      return _$invitationOrganizationTypeEnum_ASSESSOR;
    case 'ASSISTANCE':
      return _$invitationOrganizationTypeEnum_ASSISTANCE;
    case 'OMBUDSMAN':
      return _$invitationOrganizationTypeEnum_OMBUDSMAN;
    case 'REGULATOR':
      return _$invitationOrganizationTypeEnum_REGULATOR;
    case 'POLICE':
      return _$invitationOrganizationTypeEnum_POLICE;
    case 'CORPORATE_CLIENT':
      return _$invitationOrganizationTypeEnum_CORPORATE_CLIENT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InvitationOrganizationTypeEnum>
    _$invitationOrganizationTypeEnumValues = BuiltSet<
        InvitationOrganizationTypeEnum>(const <InvitationOrganizationTypeEnum>[
  _$invitationOrganizationTypeEnum_INSURER,
  _$invitationOrganizationTypeEnum_BROKER,
  _$invitationOrganizationTypeEnum_AGENT,
  _$invitationOrganizationTypeEnum_WORKSHOP,
  _$invitationOrganizationTypeEnum_ASSESSOR,
  _$invitationOrganizationTypeEnum_ASSISTANCE,
  _$invitationOrganizationTypeEnum_OMBUDSMAN,
  _$invitationOrganizationTypeEnum_REGULATOR,
  _$invitationOrganizationTypeEnum_POLICE,
  _$invitationOrganizationTypeEnum_CORPORATE_CLIENT,
]);

Serializer<InvitationOrganizationTypeEnum>
    _$invitationOrganizationTypeEnumSerializer =
    _$InvitationOrganizationTypeEnumSerializer();

class _$InvitationOrganizationTypeEnumSerializer
    implements PrimitiveSerializer<InvitationOrganizationTypeEnum> {
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
  final Iterable<Type> types = const <Type>[InvitationOrganizationTypeEnum];
  @override
  final String wireName = 'InvitationOrganizationTypeEnum';

  @override
  Object serialize(
          Serializers serializers, InvitationOrganizationTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InvitationOrganizationTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InvitationOrganizationTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InvitationOrganization extends InvitationOrganization {
  @override
  final String displayName;
  @override
  final InvitationOrganizationTypeEnum type;

  factory _$InvitationOrganization(
          [void Function(InvitationOrganizationBuilder)? updates]) =>
      (InvitationOrganizationBuilder()..update(updates))._build();

  _$InvitationOrganization._({required this.displayName, required this.type})
      : super._();
  @override
  InvitationOrganization rebuild(
          void Function(InvitationOrganizationBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InvitationOrganizationBuilder toBuilder() =>
      InvitationOrganizationBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InvitationOrganization &&
        displayName == other.displayName &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InvitationOrganization')
          ..add('displayName', displayName)
          ..add('type', type))
        .toString();
  }
}

class InvitationOrganizationBuilder
    implements Builder<InvitationOrganization, InvitationOrganizationBuilder> {
  _$InvitationOrganization? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  InvitationOrganizationTypeEnum? _type;
  InvitationOrganizationTypeEnum? get type => _$this._type;
  set type(InvitationOrganizationTypeEnum? type) => _$this._type = type;

  InvitationOrganizationBuilder() {
    InvitationOrganization._defaults(this);
  }

  InvitationOrganizationBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InvitationOrganization other) {
    _$v = other as _$InvitationOrganization;
  }

  @override
  void update(void Function(InvitationOrganizationBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InvitationOrganization build() => _build();

  _$InvitationOrganization _build() {
    final _$result = _$v ??
        _$InvitationOrganization._(
          displayName: BuiltValueNullFieldError.checkNotNull(
              displayName, r'InvitationOrganization', 'displayName'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'InvitationOrganization', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
