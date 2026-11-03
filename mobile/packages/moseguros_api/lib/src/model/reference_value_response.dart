//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reference_value_response.g.dart';

/// ReferenceValueResponse
///
/// Properties:
/// * [active] 
/// * [code] 
/// * [domain] 
/// * [id] 
/// * [labelEn] 
/// * [labelPt] 
/// * [parentCode] 
/// * [sortOrder] 
/// * [updatedAt] 
/// * [version] 
@BuiltValue()
abstract class ReferenceValueResponse implements Built<ReferenceValueResponse, ReferenceValueResponseBuilder> {
  @BuiltValueField(wireName: r'active')
  bool get active;

  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'domain')
  ReferenceValueResponseDomainEnum get domain;
  // enum domainEnum {  VEHICLE_MAKE,  VEHICLE_MODEL,  LICENCE_CATEGORY,  };

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'labelEn')
  String? get labelEn;

  @BuiltValueField(wireName: r'labelPt')
  String get labelPt;

  @BuiltValueField(wireName: r'parentCode')
  String? get parentCode;

  @BuiltValueField(wireName: r'sortOrder')
  int get sortOrder;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'version')
  int get version;

  ReferenceValueResponse._();

  factory ReferenceValueResponse([void updates(ReferenceValueResponseBuilder b)]) = _$ReferenceValueResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ReferenceValueResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ReferenceValueResponse> get serializer => _$ReferenceValueResponseSerializer();
}

class _$ReferenceValueResponseSerializer implements PrimitiveSerializer<ReferenceValueResponse> {
  @override
  final Iterable<Type> types = const [ReferenceValueResponse, _$ReferenceValueResponse];

  @override
  final String wireName = r'ReferenceValueResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ReferenceValueResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'active';
    yield serializers.serialize(
      object.active,
      specifiedType: const FullType(bool),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'domain';
    yield serializers.serialize(
      object.domain,
      specifiedType: const FullType(ReferenceValueResponseDomainEnum),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.labelEn != null) {
      yield r'labelEn';
      yield serializers.serialize(
        object.labelEn,
        specifiedType: const FullType(String),
      );
    }
    yield r'labelPt';
    yield serializers.serialize(
      object.labelPt,
      specifiedType: const FullType(String),
    );
    if (object.parentCode != null) {
      yield r'parentCode';
      yield serializers.serialize(
        object.parentCode,
        specifiedType: const FullType(String),
      );
    }
    yield r'sortOrder';
    yield serializers.serialize(
      object.sortOrder,
      specifiedType: const FullType(int),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ReferenceValueResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ReferenceValueResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'active':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.active = valueDes;
          break;
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'domain':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ReferenceValueResponseDomainEnum),
          ) as ReferenceValueResponseDomainEnum;
          result.domain = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'labelEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelEn = valueDes;
          break;
        case r'labelPt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelPt = valueDes;
          break;
        case r'parentCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.parentCode = valueDes;
          break;
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ReferenceValueResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ReferenceValueResponseBuilder();
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

class ReferenceValueResponseDomainEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'VEHICLE_MAKE')
  static const ReferenceValueResponseDomainEnum VEHICLE_MAKE = _$referenceValueResponseDomainEnum_VEHICLE_MAKE;
  @BuiltValueEnumConst(wireName: r'VEHICLE_MODEL')
  static const ReferenceValueResponseDomainEnum VEHICLE_MODEL = _$referenceValueResponseDomainEnum_VEHICLE_MODEL;
  @BuiltValueEnumConst(wireName: r'LICENCE_CATEGORY')
  static const ReferenceValueResponseDomainEnum LICENCE_CATEGORY = _$referenceValueResponseDomainEnum_LICENCE_CATEGORY;

  static Serializer<ReferenceValueResponseDomainEnum> get serializer => _$referenceValueResponseDomainEnumSerializer;

  const ReferenceValueResponseDomainEnum._(String name): super(name);

  static BuiltSet<ReferenceValueResponseDomainEnum> get values => _$referenceValueResponseDomainEnumValues;
  static ReferenceValueResponseDomainEnum valueOf(String name) => _$referenceValueResponseDomainEnumValueOf(name);
}

