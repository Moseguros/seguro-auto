//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_product.g.dart';

/// ProposalProduct
///
/// Properties:
/// * [id] 
/// * [name] 
/// * [type] 
/// * [versionId] 
@BuiltValue()
abstract class ProposalProduct implements Built<ProposalProduct, ProposalProductBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'type')
  ProposalProductTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'versionId')
  String get versionId;

  ProposalProduct._();

  factory ProposalProduct([void updates(ProposalProductBuilder b)]) = _$ProposalProduct;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalProductBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalProduct> get serializer => _$ProposalProductSerializer();
}

class _$ProposalProductSerializer implements PrimitiveSerializer<ProposalProduct> {
  @override
  final Iterable<Type> types = const [ProposalProduct, _$ProposalProduct];

  @override
  final String wireName = r'ProposalProduct';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalProduct object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ProposalProductTypeEnum),
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
    ProposalProduct object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalProductBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
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
            specifiedType: const FullType(ProposalProductTypeEnum),
          ) as ProposalProductTypeEnum;
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
  ProposalProduct deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalProductBuilder();
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

class ProposalProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const ProposalProductTypeEnum MTPL_ONLY = _$proposalProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const ProposalProductTypeEnum COMPREHENSIVE = _$proposalProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const ProposalProductTypeEnum TEMPORARY = _$proposalProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const ProposalProductTypeEnum BORDER = _$proposalProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const ProposalProductTypeEnum FLEET = _$proposalProductTypeEnum_FLEET;

  static Serializer<ProposalProductTypeEnum> get serializer => _$proposalProductTypeEnumSerializer;

  const ProposalProductTypeEnum._(String name): super(name);

  static BuiltSet<ProposalProductTypeEnum> get values => _$proposalProductTypeEnumValues;
  static ProposalProductTypeEnum valueOf(String name) => _$proposalProductTypeEnumValueOf(name);
}

