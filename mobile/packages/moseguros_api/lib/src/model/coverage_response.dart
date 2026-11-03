//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'coverage_response.g.dart';

/// CoverageResponse
///
/// Properties:
/// * [capital] 
/// * [capitalOptions] 
/// * [capitalType] 
/// * [code] 
/// * [deductibleAmount] 
/// * [deductibleMinAmount] 
/// * [deductiblePercent] 
/// * [deductibleType] 
/// * [description] 
/// * [exclusions] 
/// * [mandatory] 
/// * [requiresCodes] 
/// * [territorialScope] 
@BuiltValue()
abstract class CoverageResponse implements Built<CoverageResponse, CoverageResponseBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  @BuiltValueField(wireName: r'capitalOptions')
  BuiltList<Money>? get capitalOptions;

  @BuiltValueField(wireName: r'capitalType')
  CoverageResponseCapitalTypeEnum get capitalType;
  // enum capitalTypeEnum {  FIXED,  VEHICLE_VALUE,  OPTIONS,  PER_PERSON,  };

  @BuiltValueField(wireName: r'code')
  CoverageResponseCodeEnum get code;
  // enum codeEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  @BuiltValueField(wireName: r'deductibleAmount')
  Money? get deductibleAmount;

  @BuiltValueField(wireName: r'deductibleMinAmount')
  Money? get deductibleMinAmount;

  @BuiltValueField(wireName: r'deductiblePercent')
  num? get deductiblePercent;

  @BuiltValueField(wireName: r'deductibleType')
  CoverageResponseDeductibleTypeEnum get deductibleType;
  // enum deductibleTypeEnum {  NONE,  FIXED,  PERCENT,  };

  @BuiltValueField(wireName: r'description')
  String? get description;

  @BuiltValueField(wireName: r'exclusions')
  String? get exclusions;

  @BuiltValueField(wireName: r'mandatory')
  bool get mandatory;

  @BuiltValueField(wireName: r'requiresCodes')
  BuiltList<CoverageResponseRequiresCodesEnum>? get requiresCodes;
  // enum requiresCodesEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  @BuiltValueField(wireName: r'territorialScope')
  BuiltList<String>? get territorialScope;

  CoverageResponse._();

  factory CoverageResponse([void updates(CoverageResponseBuilder b)]) = _$CoverageResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CoverageResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CoverageResponse> get serializer => _$CoverageResponseSerializer();
}

class _$CoverageResponseSerializer implements PrimitiveSerializer<CoverageResponse> {
  @override
  final Iterable<Type> types = const [CoverageResponse, _$CoverageResponse];

  @override
  final String wireName = r'CoverageResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CoverageResponse object, {
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
        specifiedType: const FullType(BuiltList, [FullType(Money)]),
      );
    }
    yield r'capitalType';
    yield serializers.serialize(
      object.capitalType,
      specifiedType: const FullType(CoverageResponseCapitalTypeEnum),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(CoverageResponseCodeEnum),
    );
    if (object.deductibleAmount != null) {
      yield r'deductibleAmount';
      yield serializers.serialize(
        object.deductibleAmount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.deductibleMinAmount != null) {
      yield r'deductibleMinAmount';
      yield serializers.serialize(
        object.deductibleMinAmount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.deductiblePercent != null) {
      yield r'deductiblePercent';
      yield serializers.serialize(
        object.deductiblePercent,
        specifiedType: const FullType(num),
      );
    }
    yield r'deductibleType';
    yield serializers.serialize(
      object.deductibleType,
      specifiedType: const FullType(CoverageResponseDeductibleTypeEnum),
    );
    if (object.description != null) {
      yield r'description';
      yield serializers.serialize(
        object.description,
        specifiedType: const FullType(String),
      );
    }
    if (object.exclusions != null) {
      yield r'exclusions';
      yield serializers.serialize(
        object.exclusions,
        specifiedType: const FullType(String),
      );
    }
    yield r'mandatory';
    yield serializers.serialize(
      object.mandatory,
      specifiedType: const FullType(bool),
    );
    if (object.requiresCodes != null) {
      yield r'requiresCodes';
      yield serializers.serialize(
        object.requiresCodes,
        specifiedType: const FullType(BuiltList, [FullType(CoverageResponseRequiresCodesEnum)]),
      );
    }
    if (object.territorialScope != null) {
      yield r'territorialScope';
      yield serializers.serialize(
        object.territorialScope,
        specifiedType: const FullType(BuiltList, [FullType(String)]),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CoverageResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CoverageResponseBuilder result,
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
            specifiedType: const FullType(BuiltList, [FullType(Money)]),
          ) as BuiltList<Money>;
          result.capitalOptions.replace(valueDes);
          break;
        case r'capitalType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverageResponseCapitalTypeEnum),
          ) as CoverageResponseCapitalTypeEnum;
          result.capitalType = valueDes;
          break;
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverageResponseCodeEnum),
          ) as CoverageResponseCodeEnum;
          result.code = valueDes;
          break;
        case r'deductibleAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.deductibleAmount.replace(valueDes);
          break;
        case r'deductibleMinAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.deductibleMinAmount.replace(valueDes);
          break;
        case r'deductiblePercent':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.deductiblePercent = valueDes;
          break;
        case r'deductibleType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverageResponseDeductibleTypeEnum),
          ) as CoverageResponseDeductibleTypeEnum;
          result.deductibleType = valueDes;
          break;
        case r'description':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.description = valueDes;
          break;
        case r'exclusions':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.exclusions = valueDes;
          break;
        case r'mandatory':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.mandatory = valueDes;
          break;
        case r'requiresCodes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CoverageResponseRequiresCodesEnum)]),
          ) as BuiltList<CoverageResponseRequiresCodesEnum>;
          result.requiresCodes.replace(valueDes);
          break;
        case r'territorialScope':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.territorialScope.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CoverageResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CoverageResponseBuilder();
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

class CoverageResponseCapitalTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'FIXED')
  static const CoverageResponseCapitalTypeEnum FIXED = _$coverageResponseCapitalTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'VEHICLE_VALUE')
  static const CoverageResponseCapitalTypeEnum VEHICLE_VALUE = _$coverageResponseCapitalTypeEnum_VEHICLE_VALUE;
  @BuiltValueEnumConst(wireName: r'OPTIONS')
  static const CoverageResponseCapitalTypeEnum OPTIONS = _$coverageResponseCapitalTypeEnum_OPTIONS;
  @BuiltValueEnumConst(wireName: r'PER_PERSON')
  static const CoverageResponseCapitalTypeEnum PER_PERSON = _$coverageResponseCapitalTypeEnum_PER_PERSON;

  static Serializer<CoverageResponseCapitalTypeEnum> get serializer => _$coverageResponseCapitalTypeEnumSerializer;

  const CoverageResponseCapitalTypeEnum._(String name): super(name);

  static BuiltSet<CoverageResponseCapitalTypeEnum> get values => _$coverageResponseCapitalTypeEnumValues;
  static CoverageResponseCapitalTypeEnum valueOf(String name) => _$coverageResponseCapitalTypeEnumValueOf(name);
}

class CoverageResponseCodeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL')
  static const CoverageResponseCodeEnum MTPL = _$coverageResponseCodeEnum_MTPL;
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const CoverageResponseCodeEnum VTPL = _$coverageResponseCodeEnum_VTPL;
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const CoverageResponseCodeEnum OD_COLLISION = _$coverageResponseCodeEnum_OD_COLLISION;
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const CoverageResponseCodeEnum OD_GLASS = _$coverageResponseCodeEnum_OD_GLASS;
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const CoverageResponseCodeEnum OD_FIRE = _$coverageResponseCodeEnum_OD_FIRE;
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const CoverageResponseCodeEnum OD_NATURE = _$coverageResponseCodeEnum_OD_NATURE;
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const CoverageResponseCodeEnum OD_THEFT = _$coverageResponseCodeEnum_OD_THEFT;
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const CoverageResponseCodeEnum OCCUPANTS = _$coverageResponseCodeEnum_OCCUPANTS;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const CoverageResponseCodeEnum ASSISTANCE = _$coverageResponseCodeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const CoverageResponseCodeEnum LEGAL = _$coverageResponseCodeEnum_LEGAL;
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const CoverageResponseCodeEnum SRCC = _$coverageResponseCodeEnum_SRCC;
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const CoverageResponseCodeEnum CROSS_BORDER = _$coverageResponseCodeEnum_CROSS_BORDER;

  static Serializer<CoverageResponseCodeEnum> get serializer => _$coverageResponseCodeEnumSerializer;

  const CoverageResponseCodeEnum._(String name): super(name);

  static BuiltSet<CoverageResponseCodeEnum> get values => _$coverageResponseCodeEnumValues;
  static CoverageResponseCodeEnum valueOf(String name) => _$coverageResponseCodeEnumValueOf(name);
}

class CoverageResponseDeductibleTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NONE')
  static const CoverageResponseDeductibleTypeEnum NONE = _$coverageResponseDeductibleTypeEnum_NONE;
  @BuiltValueEnumConst(wireName: r'FIXED')
  static const CoverageResponseDeductibleTypeEnum FIXED = _$coverageResponseDeductibleTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'PERCENT')
  static const CoverageResponseDeductibleTypeEnum PERCENT = _$coverageResponseDeductibleTypeEnum_PERCENT;

  static Serializer<CoverageResponseDeductibleTypeEnum> get serializer => _$coverageResponseDeductibleTypeEnumSerializer;

  const CoverageResponseDeductibleTypeEnum._(String name): super(name);

  static BuiltSet<CoverageResponseDeductibleTypeEnum> get values => _$coverageResponseDeductibleTypeEnumValues;
  static CoverageResponseDeductibleTypeEnum valueOf(String name) => _$coverageResponseDeductibleTypeEnumValueOf(name);
}

class CoverageResponseRequiresCodesEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL')
  static const CoverageResponseRequiresCodesEnum MTPL = _$coverageResponseRequiresCodesEnum_MTPL;
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const CoverageResponseRequiresCodesEnum VTPL = _$coverageResponseRequiresCodesEnum_VTPL;
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const CoverageResponseRequiresCodesEnum OD_COLLISION = _$coverageResponseRequiresCodesEnum_OD_COLLISION;
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const CoverageResponseRequiresCodesEnum OD_GLASS = _$coverageResponseRequiresCodesEnum_OD_GLASS;
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const CoverageResponseRequiresCodesEnum OD_FIRE = _$coverageResponseRequiresCodesEnum_OD_FIRE;
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const CoverageResponseRequiresCodesEnum OD_NATURE = _$coverageResponseRequiresCodesEnum_OD_NATURE;
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const CoverageResponseRequiresCodesEnum OD_THEFT = _$coverageResponseRequiresCodesEnum_OD_THEFT;
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const CoverageResponseRequiresCodesEnum OCCUPANTS = _$coverageResponseRequiresCodesEnum_OCCUPANTS;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const CoverageResponseRequiresCodesEnum ASSISTANCE = _$coverageResponseRequiresCodesEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const CoverageResponseRequiresCodesEnum LEGAL = _$coverageResponseRequiresCodesEnum_LEGAL;
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const CoverageResponseRequiresCodesEnum SRCC = _$coverageResponseRequiresCodesEnum_SRCC;
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const CoverageResponseRequiresCodesEnum CROSS_BORDER = _$coverageResponseRequiresCodesEnum_CROSS_BORDER;

  static Serializer<CoverageResponseRequiresCodesEnum> get serializer => _$coverageResponseRequiresCodesEnumSerializer;

  const CoverageResponseRequiresCodesEnum._(String name): super(name);

  static BuiltSet<CoverageResponseRequiresCodesEnum> get values => _$coverageResponseRequiresCodesEnumValues;
  static CoverageResponseRequiresCodesEnum valueOf(String name) => _$coverageResponseRequiresCodesEnumValueOf(name);
}

