//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'registry_lookup_response.g.dart';

/// RegistryLookupResponse
///
/// Properties:
/// * [category] 
/// * [chassisNumber] 
/// * [engineCc] 
/// * [grossWeightKg] 
/// * [make] 
/// * [model] 
/// * [plate] 
/// * [seats] 
/// * [source_] - Origem dos dados (MOCK até haver acesso ao INATRO)
/// * [year] 
@BuiltValue()
abstract class RegistryLookupResponse implements Built<RegistryLookupResponse, RegistryLookupResponseBuilder> {
  @BuiltValueField(wireName: r'category')
  RegistryLookupResponseCategoryEnum get category;
  // enum categoryEnum {  LIGHT_PASSENGER,  LIGHT_MIXED,  HEAVY,  MOTORCYCLE,  TRAILER,  BUS,  };

  @BuiltValueField(wireName: r'chassisNumber')
  String get chassisNumber;

  @BuiltValueField(wireName: r'engineCc')
  int? get engineCc;

  @BuiltValueField(wireName: r'grossWeightKg')
  int? get grossWeightKg;

  @BuiltValueField(wireName: r'make')
  String get make;

  @BuiltValueField(wireName: r'model')
  String get model;

  @BuiltValueField(wireName: r'plate')
  String get plate;

  @BuiltValueField(wireName: r'seats')
  int get seats;

  /// Origem dos dados (MOCK até haver acesso ao INATRO)
  @BuiltValueField(wireName: r'source')
  String get source_;

  @BuiltValueField(wireName: r'year')
  int get year;

  RegistryLookupResponse._();

  factory RegistryLookupResponse([void updates(RegistryLookupResponseBuilder b)]) = _$RegistryLookupResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RegistryLookupResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RegistryLookupResponse> get serializer => _$RegistryLookupResponseSerializer();
}

class _$RegistryLookupResponseSerializer implements PrimitiveSerializer<RegistryLookupResponse> {
  @override
  final Iterable<Type> types = const [RegistryLookupResponse, _$RegistryLookupResponse];

  @override
  final String wireName = r'RegistryLookupResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RegistryLookupResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'category';
    yield serializers.serialize(
      object.category,
      specifiedType: const FullType(RegistryLookupResponseCategoryEnum),
    );
    yield r'chassisNumber';
    yield serializers.serialize(
      object.chassisNumber,
      specifiedType: const FullType(String),
    );
    if (object.engineCc != null) {
      yield r'engineCc';
      yield serializers.serialize(
        object.engineCc,
        specifiedType: const FullType(int),
      );
    }
    if (object.grossWeightKg != null) {
      yield r'grossWeightKg';
      yield serializers.serialize(
        object.grossWeightKg,
        specifiedType: const FullType(int),
      );
    }
    yield r'make';
    yield serializers.serialize(
      object.make,
      specifiedType: const FullType(String),
    );
    yield r'model';
    yield serializers.serialize(
      object.model,
      specifiedType: const FullType(String),
    );
    yield r'plate';
    yield serializers.serialize(
      object.plate,
      specifiedType: const FullType(String),
    );
    yield r'seats';
    yield serializers.serialize(
      object.seats,
      specifiedType: const FullType(int),
    );
    yield r'source';
    yield serializers.serialize(
      object.source_,
      specifiedType: const FullType(String),
    );
    yield r'year';
    yield serializers.serialize(
      object.year,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RegistryLookupResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RegistryLookupResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'category':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RegistryLookupResponseCategoryEnum),
          ) as RegistryLookupResponseCategoryEnum;
          result.category = valueDes;
          break;
        case r'chassisNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.chassisNumber = valueDes;
          break;
        case r'engineCc':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.engineCc = valueDes;
          break;
        case r'grossWeightKg':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.grossWeightKg = valueDes;
          break;
        case r'make':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.make = valueDes;
          break;
        case r'model':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.model = valueDes;
          break;
        case r'plate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.plate = valueDes;
          break;
        case r'seats':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.seats = valueDes;
          break;
        case r'source':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.source_ = valueDes;
          break;
        case r'year':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.year = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RegistryLookupResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RegistryLookupResponseBuilder();
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

class RegistryLookupResponseCategoryEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'LIGHT_PASSENGER')
  static const RegistryLookupResponseCategoryEnum LIGHT_PASSENGER = _$registryLookupResponseCategoryEnum_LIGHT_PASSENGER;
  @BuiltValueEnumConst(wireName: r'LIGHT_MIXED')
  static const RegistryLookupResponseCategoryEnum LIGHT_MIXED = _$registryLookupResponseCategoryEnum_LIGHT_MIXED;
  @BuiltValueEnumConst(wireName: r'HEAVY')
  static const RegistryLookupResponseCategoryEnum HEAVY = _$registryLookupResponseCategoryEnum_HEAVY;
  @BuiltValueEnumConst(wireName: r'MOTORCYCLE')
  static const RegistryLookupResponseCategoryEnum MOTORCYCLE = _$registryLookupResponseCategoryEnum_MOTORCYCLE;
  @BuiltValueEnumConst(wireName: r'TRAILER')
  static const RegistryLookupResponseCategoryEnum TRAILER = _$registryLookupResponseCategoryEnum_TRAILER;
  @BuiltValueEnumConst(wireName: r'BUS')
  static const RegistryLookupResponseCategoryEnum BUS = _$registryLookupResponseCategoryEnum_BUS;

  static Serializer<RegistryLookupResponseCategoryEnum> get serializer => _$registryLookupResponseCategoryEnumSerializer;

  const RegistryLookupResponseCategoryEnum._(String name): super(name);

  static BuiltSet<RegistryLookupResponseCategoryEnum> get values => _$registryLookupResponseCategoryEnumValues;
  static RegistryLookupResponseCategoryEnum valueOf(String name) => _$registryLookupResponseCategoryEnumValueOf(name);
}

