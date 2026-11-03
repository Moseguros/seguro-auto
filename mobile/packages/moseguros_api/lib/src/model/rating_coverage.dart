//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rating_coverage.g.dart';

/// RatingCoverage
///
/// Properties:
/// * [capital] 
/// * [capitalOptions] 
/// * [capitalType] 
/// * [code] 
/// * [mandatory] 
@BuiltValue()
abstract class RatingCoverage implements Built<RatingCoverage, RatingCoverageBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  @BuiltValueField(wireName: r'capitalOptions')
  BuiltList<Money>? get capitalOptions;

  @BuiltValueField(wireName: r'capitalType')
  RatingCoverageCapitalTypeEnum get capitalType;
  // enum capitalTypeEnum {  FIXED,  VEHICLE_VALUE,  OPTIONS,  PER_PERSON,  };

  @BuiltValueField(wireName: r'code')
  RatingCoverageCodeEnum get code;
  // enum codeEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  @BuiltValueField(wireName: r'mandatory')
  bool get mandatory;

  RatingCoverage._();

  factory RatingCoverage([void updates(RatingCoverageBuilder b)]) = _$RatingCoverage;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RatingCoverageBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RatingCoverage> get serializer => _$RatingCoverageSerializer();
}

class _$RatingCoverageSerializer implements PrimitiveSerializer<RatingCoverage> {
  @override
  final Iterable<Type> types = const [RatingCoverage, _$RatingCoverage];

  @override
  final String wireName = r'RatingCoverage';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RatingCoverage object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.capital != null) {
      yield r'capital';
      yield serializers.serialize(
        object.capital,
        specifiedType: const FullType(Money),
      );
    }
    if (object.capitalOptions != null) {
      yield r'capitalOptions';
      yield serializers.serialize(
        object.capitalOptions,
        specifiedType: const FullType.nullable(BuiltList, [FullType(Money)]),
      );
    }
    yield r'capitalType';
    yield serializers.serialize(
      object.capitalType,
      specifiedType: const FullType(RatingCoverageCapitalTypeEnum),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(RatingCoverageCodeEnum),
    );
    yield r'mandatory';
    yield serializers.serialize(
      object.mandatory,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RatingCoverage object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RatingCoverageBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'capital':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.capital.replace(valueDes);
          break;
        case r'capitalOptions':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(BuiltList, [FullType(Money)]),
          ) as BuiltList<Money>?;
          if (valueDes == null) continue;
          result.capitalOptions.replace(valueDes);
          break;
        case r'capitalType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RatingCoverageCapitalTypeEnum),
          ) as RatingCoverageCapitalTypeEnum;
          result.capitalType = valueDes;
          break;
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RatingCoverageCodeEnum),
          ) as RatingCoverageCodeEnum;
          result.code = valueDes;
          break;
        case r'mandatory':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.mandatory = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RatingCoverage deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RatingCoverageBuilder();
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

class RatingCoverageCapitalTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'FIXED')
  static const RatingCoverageCapitalTypeEnum FIXED = _$ratingCoverageCapitalTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'VEHICLE_VALUE')
  static const RatingCoverageCapitalTypeEnum VEHICLE_VALUE = _$ratingCoverageCapitalTypeEnum_VEHICLE_VALUE;
  @BuiltValueEnumConst(wireName: r'OPTIONS')
  static const RatingCoverageCapitalTypeEnum OPTIONS = _$ratingCoverageCapitalTypeEnum_OPTIONS;
  @BuiltValueEnumConst(wireName: r'PER_PERSON')
  static const RatingCoverageCapitalTypeEnum PER_PERSON = _$ratingCoverageCapitalTypeEnum_PER_PERSON;

  static Serializer<RatingCoverageCapitalTypeEnum> get serializer => _$ratingCoverageCapitalTypeEnumSerializer;

  const RatingCoverageCapitalTypeEnum._(String name): super(name);

  static BuiltSet<RatingCoverageCapitalTypeEnum> get values => _$ratingCoverageCapitalTypeEnumValues;
  static RatingCoverageCapitalTypeEnum valueOf(String name) => _$ratingCoverageCapitalTypeEnumValueOf(name);
}

class RatingCoverageCodeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL')
  static const RatingCoverageCodeEnum MTPL = _$ratingCoverageCodeEnum_MTPL;
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const RatingCoverageCodeEnum VTPL = _$ratingCoverageCodeEnum_VTPL;
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const RatingCoverageCodeEnum OD_COLLISION = _$ratingCoverageCodeEnum_OD_COLLISION;
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const RatingCoverageCodeEnum OD_GLASS = _$ratingCoverageCodeEnum_OD_GLASS;
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const RatingCoverageCodeEnum OD_FIRE = _$ratingCoverageCodeEnum_OD_FIRE;
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const RatingCoverageCodeEnum OD_NATURE = _$ratingCoverageCodeEnum_OD_NATURE;
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const RatingCoverageCodeEnum OD_THEFT = _$ratingCoverageCodeEnum_OD_THEFT;
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const RatingCoverageCodeEnum OCCUPANTS = _$ratingCoverageCodeEnum_OCCUPANTS;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const RatingCoverageCodeEnum ASSISTANCE = _$ratingCoverageCodeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const RatingCoverageCodeEnum LEGAL = _$ratingCoverageCodeEnum_LEGAL;
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const RatingCoverageCodeEnum SRCC = _$ratingCoverageCodeEnum_SRCC;
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const RatingCoverageCodeEnum CROSS_BORDER = _$ratingCoverageCodeEnum_CROSS_BORDER;

  static Serializer<RatingCoverageCodeEnum> get serializer => _$ratingCoverageCodeEnumSerializer;

  const RatingCoverageCodeEnum._(String name): super(name);

  static BuiltSet<RatingCoverageCodeEnum> get values => _$ratingCoverageCodeEnumValues;
  static RatingCoverageCodeEnum valueOf(String name) => _$ratingCoverageCodeEnumValueOf(name);
}

