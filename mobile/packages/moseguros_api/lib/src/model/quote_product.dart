//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_product.g.dart';

/// QuoteProduct
///
/// Properties:
/// * [code] 
/// * [generalConditionsFileId] 
/// * [id] 
/// * [name] 
/// * [preContractInfoFileId] 
/// * [type] 
/// * [versionId] 
/// * [versionNo] 
@BuiltValue()
abstract class QuoteProduct implements Built<QuoteProduct, QuoteProductBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'generalConditionsFileId')
  String? get generalConditionsFileId;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'preContractInfoFileId')
  String? get preContractInfoFileId;

  @BuiltValueField(wireName: r'type')
  QuoteProductTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'versionId')
  String get versionId;

  @BuiltValueField(wireName: r'versionNo')
  int get versionNo;

  QuoteProduct._();

  factory QuoteProduct([void updates(QuoteProductBuilder b)]) = _$QuoteProduct;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteProductBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteProduct> get serializer => _$QuoteProductSerializer();
}

class _$QuoteProductSerializer implements PrimitiveSerializer<QuoteProduct> {
  @override
  final Iterable<Type> types = const [QuoteProduct, _$QuoteProduct];

  @override
  final String wireName = r'QuoteProduct';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteProduct object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    if (object.generalConditionsFileId != null) {
      yield r'generalConditionsFileId';
      yield serializers.serialize(
        object.generalConditionsFileId,
        specifiedType: const FullType.nullable(String),
      );
    }
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
    if (object.preContractInfoFileId != null) {
      yield r'preContractInfoFileId';
      yield serializers.serialize(
        object.preContractInfoFileId,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(QuoteProductTypeEnum),
    );
    yield r'versionId';
    yield serializers.serialize(
      object.versionId,
      specifiedType: const FullType(String),
    );
    yield r'versionNo';
    yield serializers.serialize(
      object.versionNo,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteProduct object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteProductBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'generalConditionsFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.generalConditionsFileId = valueDes;
          break;
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
        case r'preContractInfoFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.preContractInfoFileId = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteProductTypeEnum),
          ) as QuoteProductTypeEnum;
          result.type = valueDes;
          break;
        case r'versionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.versionId = valueDes;
          break;
        case r'versionNo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.versionNo = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  QuoteProduct deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteProductBuilder();
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

class QuoteProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const QuoteProductTypeEnum MTPL_ONLY = _$quoteProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const QuoteProductTypeEnum COMPREHENSIVE = _$quoteProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const QuoteProductTypeEnum TEMPORARY = _$quoteProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const QuoteProductTypeEnum BORDER = _$quoteProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const QuoteProductTypeEnum FLEET = _$quoteProductTypeEnum_FLEET;

  static Serializer<QuoteProductTypeEnum> get serializer => _$quoteProductTypeEnumSerializer;

  const QuoteProductTypeEnum._(String name): super(name);

  static BuiltSet<QuoteProductTypeEnum> get values => _$quoteProductTypeEnumValues;
  static QuoteProductTypeEnum valueOf(String name) => _$quoteProductTypeEnumValueOf(name);
}

