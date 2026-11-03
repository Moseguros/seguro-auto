//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_product_request.g.dart';

/// CreateProductRequest
///
/// Properties:
/// * [code] - ^[A-Z0-9][A-Z0-9_-]{1,29}$; único na seguradora; não muda depois de criado
/// * [description] 
/// * [name] 
/// * [type] 
@BuiltValue()
abstract class CreateProductRequest implements Built<CreateProductRequest, CreateProductRequestBuilder> {
  /// ^[A-Z0-9][A-Z0-9_-]{1,29}$; único na seguradora; não muda depois de criado
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'description')
  String? get description;

  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'type')
  CreateProductRequestTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  CreateProductRequest._();

  factory CreateProductRequest([void updates(CreateProductRequestBuilder b)]) = _$CreateProductRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateProductRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateProductRequest> get serializer => _$CreateProductRequestSerializer();
}

class _$CreateProductRequestSerializer implements PrimitiveSerializer<CreateProductRequest> {
  @override
  final Iterable<Type> types = const [CreateProductRequest, _$CreateProductRequest];

  @override
  final String wireName = r'CreateProductRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateProductRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    if (object.description != null) {
      yield r'description';
      yield serializers.serialize(
        object.description,
        specifiedType: const FullType(String),
      );
    }
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(CreateProductRequestTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateProductRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateProductRequestBuilder result,
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
        case r'description':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.description = valueDes;
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
            specifiedType: const FullType(CreateProductRequestTypeEnum),
          ) as CreateProductRequestTypeEnum;
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
  CreateProductRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateProductRequestBuilder();
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

class CreateProductRequestTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const CreateProductRequestTypeEnum MTPL_ONLY = _$createProductRequestTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const CreateProductRequestTypeEnum COMPREHENSIVE = _$createProductRequestTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const CreateProductRequestTypeEnum TEMPORARY = _$createProductRequestTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const CreateProductRequestTypeEnum BORDER = _$createProductRequestTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const CreateProductRequestTypeEnum FLEET = _$createProductRequestTypeEnum_FLEET;

  static Serializer<CreateProductRequestTypeEnum> get serializer => _$createProductRequestTypeEnumSerializer;

  const CreateProductRequestTypeEnum._(String name): super(name);

  static BuiltSet<CreateProductRequestTypeEnum> get values => _$createProductRequestTypeEnumValues;
  static CreateProductRequestTypeEnum valueOf(String name) => _$createProductRequestTypeEnumValueOf(name);
}

