//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/policy_event_type.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_timeline_event.g.dart';

/// Só no detalhe da seguradora
///
/// Properties:
/// * [actorType] 
/// * [occurredAt] 
/// * [type] 
@BuiltValue()
abstract class PolicyTimelineEvent implements Built<PolicyTimelineEvent, PolicyTimelineEventBuilder> {
  @BuiltValueField(wireName: r'actorType')
  PolicyTimelineEventActorTypeEnum get actorType;
  // enum actorTypeEnum {  CUSTOMER,  STAFF,  SYSTEM,  };

  @BuiltValueField(wireName: r'occurredAt')
  DateTime get occurredAt;

  @BuiltValueField(wireName: r'type')
  PolicyEventType get type;
  // enum typeEnum {  ISSUED,  CERTIFICATE_ISSUED,  DOCUMENTS_READY,  };

  PolicyTimelineEvent._();

  factory PolicyTimelineEvent([void updates(PolicyTimelineEventBuilder b)]) = _$PolicyTimelineEvent;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PolicyTimelineEventBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PolicyTimelineEvent> get serializer => _$PolicyTimelineEventSerializer();
}

class _$PolicyTimelineEventSerializer implements PrimitiveSerializer<PolicyTimelineEvent> {
  @override
  final Iterable<Type> types = const [PolicyTimelineEvent, _$PolicyTimelineEvent];

  @override
  final String wireName = r'PolicyTimelineEvent';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PolicyTimelineEvent object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'actorType';
    yield serializers.serialize(
      object.actorType,
      specifiedType: const FullType(PolicyTimelineEventActorTypeEnum),
    );
    yield r'occurredAt';
    yield serializers.serialize(
      object.occurredAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PolicyEventType),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PolicyTimelineEvent object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PolicyTimelineEventBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'actorType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyTimelineEventActorTypeEnum),
          ) as PolicyTimelineEventActorTypeEnum;
          result.actorType = valueDes;
          break;
        case r'occurredAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.occurredAt = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyEventType),
          ) as PolicyEventType;
          result.type = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PolicyTimelineEvent deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PolicyTimelineEventBuilder();
    final serializedList = (serialized as Iterable<Object?>).toList();
    final unhandled = <Object?>[];
    _deserializeProperties(
      serializers,
      serialized,
      specifiedType: specifiedType,
      serializedList: serializedList,
      unhandled: unhandled,
      result: result,
    );
    return result.build();
  }
}

class PolicyTimelineEventActorTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CUSTOMER')
  static const PolicyTimelineEventActorTypeEnum CUSTOMER = _$policyTimelineEventActorTypeEnum_CUSTOMER;
  @BuiltValueEnumConst(wireName: r'STAFF')
  static const PolicyTimelineEventActorTypeEnum STAFF = _$policyTimelineEventActorTypeEnum_STAFF;
  @BuiltValueEnumConst(wireName: r'SYSTEM')
  static const PolicyTimelineEventActorTypeEnum SYSTEM = _$policyTimelineEventActorTypeEnum_SYSTEM;

  static Serializer<PolicyTimelineEventActorTypeEnum> get serializer => _$policyTimelineEventActorTypeEnumSerializer;

  const PolicyTimelineEventActorTypeEnum._(String name): super(name);

  static BuiltSet<PolicyTimelineEventActorTypeEnum> get values => _$policyTimelineEventActorTypeEnumValues;
  static PolicyTimelineEventActorTypeEnum valueOf(String name) => _$policyTimelineEventActorTypeEnumValueOf(name);
}

