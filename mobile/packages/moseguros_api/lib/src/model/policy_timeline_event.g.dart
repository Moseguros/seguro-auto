// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_timeline_event.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyTimelineEventActorTypeEnum
    _$policyTimelineEventActorTypeEnum_CUSTOMER =
    const PolicyTimelineEventActorTypeEnum._('CUSTOMER');
const PolicyTimelineEventActorTypeEnum
    _$policyTimelineEventActorTypeEnum_STAFF =
    const PolicyTimelineEventActorTypeEnum._('STAFF');
const PolicyTimelineEventActorTypeEnum
    _$policyTimelineEventActorTypeEnum_SYSTEM =
    const PolicyTimelineEventActorTypeEnum._('SYSTEM');

PolicyTimelineEventActorTypeEnum _$policyTimelineEventActorTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'CUSTOMER':
      return _$policyTimelineEventActorTypeEnum_CUSTOMER;
    case 'STAFF':
      return _$policyTimelineEventActorTypeEnum_STAFF;
    case 'SYSTEM':
      return _$policyTimelineEventActorTypeEnum_SYSTEM;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyTimelineEventActorTypeEnum>
    _$policyTimelineEventActorTypeEnumValues = BuiltSet<
        PolicyTimelineEventActorTypeEnum>(const <PolicyTimelineEventActorTypeEnum>[
  _$policyTimelineEventActorTypeEnum_CUSTOMER,
  _$policyTimelineEventActorTypeEnum_STAFF,
  _$policyTimelineEventActorTypeEnum_SYSTEM,
]);

Serializer<PolicyTimelineEventActorTypeEnum>
    _$policyTimelineEventActorTypeEnumSerializer =
    _$PolicyTimelineEventActorTypeEnumSerializer();

class _$PolicyTimelineEventActorTypeEnumSerializer
    implements PrimitiveSerializer<PolicyTimelineEventActorTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CUSTOMER': 'CUSTOMER',
    'STAFF': 'STAFF',
    'SYSTEM': 'SYSTEM',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CUSTOMER': 'CUSTOMER',
    'STAFF': 'STAFF',
    'SYSTEM': 'SYSTEM',
  };

  @override
  final Iterable<Type> types = const <Type>[PolicyTimelineEventActorTypeEnum];
  @override
  final String wireName = 'PolicyTimelineEventActorTypeEnum';

  @override
  Object serialize(
          Serializers serializers, PolicyTimelineEventActorTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyTimelineEventActorTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyTimelineEventActorTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PolicyTimelineEvent extends PolicyTimelineEvent {
  @override
  final PolicyTimelineEventActorTypeEnum actorType;
  @override
  final DateTime occurredAt;
  @override
  final PolicyEventType type;

  factory _$PolicyTimelineEvent(
          [void Function(PolicyTimelineEventBuilder)? updates]) =>
      (PolicyTimelineEventBuilder()..update(updates))._build();

  _$PolicyTimelineEvent._(
      {required this.actorType, required this.occurredAt, required this.type})
      : super._();
  @override
  PolicyTimelineEvent rebuild(
          void Function(PolicyTimelineEventBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyTimelineEventBuilder toBuilder() =>
      PolicyTimelineEventBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyTimelineEvent &&
        actorType == other.actorType &&
        occurredAt == other.occurredAt &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, actorType.hashCode);
    _$hash = $jc(_$hash, occurredAt.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyTimelineEvent')
          ..add('actorType', actorType)
          ..add('occurredAt', occurredAt)
          ..add('type', type))
        .toString();
  }
}

class PolicyTimelineEventBuilder
    implements Builder<PolicyTimelineEvent, PolicyTimelineEventBuilder> {
  _$PolicyTimelineEvent? _$v;

  PolicyTimelineEventActorTypeEnum? _actorType;
  PolicyTimelineEventActorTypeEnum? get actorType => _$this._actorType;
  set actorType(PolicyTimelineEventActorTypeEnum? actorType) =>
      _$this._actorType = actorType;

  DateTime? _occurredAt;
  DateTime? get occurredAt => _$this._occurredAt;
  set occurredAt(DateTime? occurredAt) => _$this._occurredAt = occurredAt;

  PolicyEventType? _type;
  PolicyEventType? get type => _$this._type;
  set type(PolicyEventType? type) => _$this._type = type;

  PolicyTimelineEventBuilder() {
    PolicyTimelineEvent._defaults(this);
  }

  PolicyTimelineEventBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _actorType = $v.actorType;
      _occurredAt = $v.occurredAt;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyTimelineEvent other) {
    _$v = other as _$PolicyTimelineEvent;
  }

  @override
  void update(void Function(PolicyTimelineEventBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyTimelineEvent build() => _build();

  _$PolicyTimelineEvent _build() {
    final _$result = _$v ??
        _$PolicyTimelineEvent._(
          actorType: BuiltValueNullFieldError.checkNotNull(
              actorType, r'PolicyTimelineEvent', 'actorType'),
          occurredAt: BuiltValueNullFieldError.checkNotNull(
              occurredAt, r'PolicyTimelineEvent', 'occurredAt'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'PolicyTimelineEvent', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
