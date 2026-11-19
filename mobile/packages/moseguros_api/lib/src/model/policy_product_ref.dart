//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_product_ref.g.dart';

/// PolicyProductRef
///
/// Properties:
/// * [name] 
/// * [type] 
/// * [versionId] 
@BuiltValue()
abstract class PolicyProductRef implements Built<PolicyProductRef, PolicyProductRefBuilder> {
  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'type')
  PolicyProductRefTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'versionId')
  String get versionId;

  PolicyProductRef._();

  factory PolicyProductRef([void updates(PolicyProductRefBuilder b)]) = _$PolicyProductRef;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PolicyProductRefBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PolicyProductRef> get serializer => _$PolicyProductRefSerializer();
}

class _$PolicyProductRefSerializer implements PrimitiveSerializer<PolicyProductRef> {
  @override
  final Iterable<Type> types = const [PolicyProductRef, _$PolicyProductRef];

  @override
  final String wireName = r'PolicyProductRef';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PolicyProductRef object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PolicyProductRefTypeEnum),
    );
    yield r'versionId';
    yield serializers.serialize(
      object.versionId,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PolicyProductRef object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PolicyProductRefBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'name':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.name = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyProductRefTypeEnum),
          ) as PolicyProductRefTypeEnum;
          result.type = valueDes;
          break;
        case r'versionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.versionId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PolicyProductRef deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PolicyProductRefBuilder();
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

class PolicyProductRefTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const PolicyProductRefTypeEnum MTPL_ONLY = _$policyProductRefTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const PolicyProductRefTypeEnum COMPREHENSIVE = _$policyProductRefTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const PolicyProductRefTypeEnum TEMPORARY = _$policyProductRefTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const PolicyProductRefTypeEnum BORDER = _$policyProductRefTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const PolicyProductRefTypeEnum FLEET = _$policyProductRefTypeEnum_FLEET;

  static Serializer<PolicyProductRefTypeEnum> get serializer => _$policyProductRefTypeEnumSerializer;

  const PolicyProductRefTypeEnum._(String name): super(name);

  static BuiltSet<PolicyProductRefTypeEnum> get values => _$policyProductRefTypeEnumValues;
  static PolicyProductRefTypeEnum valueOf(String name) => _$policyProductRefTypeEnumValueOf(name);
}

