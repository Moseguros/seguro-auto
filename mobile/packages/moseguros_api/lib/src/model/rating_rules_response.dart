//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/rating_coverage.dart';
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rating_rules_response.g.dart';

/// RatingRulesResponse
///
/// Properties:
/// * [configured] 
/// * [coverages] 
/// * [editable] 
/// * [productType] 
/// * [productVersionId] 
/// * [rating] - Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
/// * [version] 
@BuiltValue()
abstract class RatingRulesResponse implements Built<RatingRulesResponse, RatingRulesResponseBuilder> {
  @BuiltValueField(wireName: r'configured')
  bool get configured;

  @BuiltValueField(wireName: r'coverages')
  BuiltList<RatingCoverage> get coverages;

  @BuiltValueField(wireName: r'editable')
  bool get editable;

  @BuiltValueField(wireName: r'productType')
  RatingRulesResponseProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'productVersionId')
  String get productVersionId;

  /// Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
  @BuiltValueField(wireName: r'rating')
  JsonObject? get rating;

  @BuiltValueField(wireName: r'version')
  int get version;

  RatingRulesResponse._();

  factory RatingRulesResponse([void updates(RatingRulesResponseBuilder b)]) = _$RatingRulesResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RatingRulesResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RatingRulesResponse> get serializer => _$RatingRulesResponseSerializer();
}

class _$RatingRulesResponseSerializer implements PrimitiveSerializer<RatingRulesResponse> {
  @override
  final Iterable<Type> types = const [RatingRulesResponse, _$RatingRulesResponse];

  @override
  final String wireName = r'RatingRulesResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RatingRulesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'configured';
    yield serializers.serialize(
      object.configured,
      specifiedType: const FullType(bool),
    );
    yield r'coverages';
    yield serializers.serialize(
      object.coverages,
      specifiedType: const FullType(BuiltList, [FullType(RatingCoverage)]),
    );
    yield r'editable';
    yield serializers.serialize(
      object.editable,
      specifiedType: const FullType(bool),
    );
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(RatingRulesResponseProductTypeEnum),
    );
    yield r'productVersionId';
    yield serializers.serialize(
      object.productVersionId,
      specifiedType: const FullType(String),
    );
    if (object.rating != null) {
      yield r'rating';
      yield serializers.serialize(
        object.rating,
        specifiedType: const FullType(JsonObject),
      );
    }
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RatingRulesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RatingRulesResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'configured':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.configured = valueDes;
          break;
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RatingCoverage)]),
          ) as BuiltList<RatingCoverage>;
          result.coverages.replace(valueDes);
          break;
        case r'editable':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.editable = valueDes;
          break;
        case r'productType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RatingRulesResponseProductTypeEnum),
          ) as RatingRulesResponseProductTypeEnum;
          result.productType = valueDes;
          break;
        case r'productVersionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productVersionId = valueDes;
          break;
        case r'rating':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(JsonObject),
          ) as JsonObject;
          result.rating = valueDes;
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
  RatingRulesResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RatingRulesResponseBuilder();
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

class RatingRulesResponseProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const RatingRulesResponseProductTypeEnum MTPL_ONLY = _$ratingRulesResponseProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const RatingRulesResponseProductTypeEnum COMPREHENSIVE = _$ratingRulesResponseProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const RatingRulesResponseProductTypeEnum TEMPORARY = _$ratingRulesResponseProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const RatingRulesResponseProductTypeEnum BORDER = _$ratingRulesResponseProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const RatingRulesResponseProductTypeEnum FLEET = _$ratingRulesResponseProductTypeEnum_FLEET;

  static Serializer<RatingRulesResponseProductTypeEnum> get serializer => _$ratingRulesResponseProductTypeEnumSerializer;

  const RatingRulesResponseProductTypeEnum._(String name): super(name);

  static BuiltSet<RatingRulesResponseProductTypeEnum> get values => _$ratingRulesResponseProductTypeEnumValues;
  static RatingRulesResponseProductTypeEnum valueOf(String name) => _$ratingRulesResponseProductTypeEnumValueOf(name);
}

