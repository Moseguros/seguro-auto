//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'coverage_request.g.dart';

/// CoverageRequest
///
/// Properties:
/// * [capital] 
/// * [capitalOptions] - Obrigatório em OPTIONS, proibido nos outros; 2 a 10 valores > 0 em ordem estritamente crescente (CAPITAL_OPTIONS_INVALID)
/// * [capitalType] 
/// * [code] - Sem repetidos na lista (DUPLICATED)
/// * [deductibleAmount] 
/// * [deductibleMinAmount] 
/// * [deductiblePercent] - Obrigatório em PERCENT, proibido nos outros; > 0 e <= 100, até 2 casas decimais
/// * [deductibleType] 
/// * [description] 
/// * [exclusions] - Exclusões resumidas; o texto legal está nas condições gerais
/// * [mandatory] - O cliente não pode retirar esta cobertura
/// * [requiresCodes] - Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
/// * [territorialScope] - 1 a 20 códigos ISO 3166-1 alfa-2 sem repetidos; inclui MZ
@BuiltValue()
abstract class CoverageRequest implements Built<CoverageRequest, CoverageRequestBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  /// Obrigatório em OPTIONS, proibido nos outros; 2 a 10 valores > 0 em ordem estritamente crescente (CAPITAL_OPTIONS_INVALID)
  @BuiltValueField(wireName: r'capitalOptions')
  BuiltList<Money>? get capitalOptions;

  @BuiltValueField(wireName: r'capitalType')
  CoverageRequestCapitalTypeEnum get capitalType;
  // enum capitalTypeEnum {  FIXED,  VEHICLE_VALUE,  OPTIONS,  PER_PERSON,  };

  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueField(wireName: r'code')
  CoverageRequestCodeEnum get code;
  // enum codeEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  @BuiltValueField(wireName: r'deductibleAmount')
  Money? get deductibleAmount;

  @BuiltValueField(wireName: r'deductibleMinAmount')
  Money? get deductibleMinAmount;

  /// Obrigatório em PERCENT, proibido nos outros; > 0 e <= 100, até 2 casas decimais
  @BuiltValueField(wireName: r'deductiblePercent')
  num? get deductiblePercent;

  @BuiltValueField(wireName: r'deductibleType')
  CoverageRequestDeductibleTypeEnum get deductibleType;
  // enum deductibleTypeEnum {  NONE,  FIXED,  PERCENT,  };

  @BuiltValueField(wireName: r'description')
  String? get description;

  /// Exclusões resumidas; o texto legal está nas condições gerais
  @BuiltValueField(wireName: r'exclusions')
  String? get exclusions;

  /// O cliente não pode retirar esta cobertura
  @BuiltValueField(wireName: r'mandatory')
  bool get mandatory;

  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueField(wireName: r'requiresCodes')
  BuiltList<CoverageRequestRequiresCodesEnum>? get requiresCodes;
  // enum requiresCodesEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  /// 1 a 20 códigos ISO 3166-1 alfa-2 sem repetidos; inclui MZ
  @BuiltValueField(wireName: r'territorialScope')
  BuiltList<String>? get territorialScope;

  CoverageRequest._();

  factory CoverageRequest([void updates(CoverageRequestBuilder b)]) = _$CoverageRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CoverageRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CoverageRequest> get serializer => _$CoverageRequestSerializer();
}

class _$CoverageRequestSerializer implements PrimitiveSerializer<CoverageRequest> {
  @override
  final Iterable<Type> types = const [CoverageRequest, _$CoverageRequest];

  @override
  final String wireName = r'CoverageRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CoverageRequest object, {
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
      specifiedType: const FullType(CoverageRequestCapitalTypeEnum),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(CoverageRequestCodeEnum),
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
      specifiedType: const FullType(CoverageRequestDeductibleTypeEnum),
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
        specifiedType: const FullType(BuiltList, [FullType(CoverageRequestRequiresCodesEnum)]),
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
    CoverageRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CoverageRequestBuilder result,
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
            specifiedType: const FullType(CoverageRequestCapitalTypeEnum),
          ) as CoverageRequestCapitalTypeEnum;
          result.capitalType = valueDes;
          break;
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverageRequestCodeEnum),
          ) as CoverageRequestCodeEnum;
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
            specifiedType: const FullType(CoverageRequestDeductibleTypeEnum),
          ) as CoverageRequestDeductibleTypeEnum;
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
            specifiedType: const FullType(BuiltList, [FullType(CoverageRequestRequiresCodesEnum)]),
          ) as BuiltList<CoverageRequestRequiresCodesEnum>;
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
  CoverageRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CoverageRequestBuilder();
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

class CoverageRequestCapitalTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'FIXED')
  static const CoverageRequestCapitalTypeEnum FIXED = _$coverageRequestCapitalTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'VEHICLE_VALUE')
  static const CoverageRequestCapitalTypeEnum VEHICLE_VALUE = _$coverageRequestCapitalTypeEnum_VEHICLE_VALUE;
  @BuiltValueEnumConst(wireName: r'OPTIONS')
  static const CoverageRequestCapitalTypeEnum OPTIONS = _$coverageRequestCapitalTypeEnum_OPTIONS;
  @BuiltValueEnumConst(wireName: r'PER_PERSON')
  static const CoverageRequestCapitalTypeEnum PER_PERSON = _$coverageRequestCapitalTypeEnum_PER_PERSON;

  static Serializer<CoverageRequestCapitalTypeEnum> get serializer => _$coverageRequestCapitalTypeEnumSerializer;

  const CoverageRequestCapitalTypeEnum._(String name): super(name);

  static BuiltSet<CoverageRequestCapitalTypeEnum> get values => _$coverageRequestCapitalTypeEnumValues;
  static CoverageRequestCapitalTypeEnum valueOf(String name) => _$coverageRequestCapitalTypeEnumValueOf(name);
}

class CoverageRequestCodeEnum extends EnumClass {

  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'MTPL')
  static const CoverageRequestCodeEnum MTPL = _$coverageRequestCodeEnum_MTPL;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const CoverageRequestCodeEnum VTPL = _$coverageRequestCodeEnum_VTPL;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const CoverageRequestCodeEnum OD_COLLISION = _$coverageRequestCodeEnum_OD_COLLISION;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const CoverageRequestCodeEnum OD_GLASS = _$coverageRequestCodeEnum_OD_GLASS;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const CoverageRequestCodeEnum OD_FIRE = _$coverageRequestCodeEnum_OD_FIRE;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const CoverageRequestCodeEnum OD_NATURE = _$coverageRequestCodeEnum_OD_NATURE;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const CoverageRequestCodeEnum OD_THEFT = _$coverageRequestCodeEnum_OD_THEFT;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const CoverageRequestCodeEnum OCCUPANTS = _$coverageRequestCodeEnum_OCCUPANTS;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const CoverageRequestCodeEnum ASSISTANCE = _$coverageRequestCodeEnum_ASSISTANCE;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const CoverageRequestCodeEnum LEGAL = _$coverageRequestCodeEnum_LEGAL;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const CoverageRequestCodeEnum SRCC = _$coverageRequestCodeEnum_SRCC;
  /// Sem repetidos na lista (DUPLICATED)
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const CoverageRequestCodeEnum CROSS_BORDER = _$coverageRequestCodeEnum_CROSS_BORDER;

  static Serializer<CoverageRequestCodeEnum> get serializer => _$coverageRequestCodeEnumSerializer;

  const CoverageRequestCodeEnum._(String name): super(name);

  static BuiltSet<CoverageRequestCodeEnum> get values => _$coverageRequestCodeEnumValues;
  static CoverageRequestCodeEnum valueOf(String name) => _$coverageRequestCodeEnumValueOf(name);
}

class CoverageRequestDeductibleTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NONE')
  static const CoverageRequestDeductibleTypeEnum NONE = _$coverageRequestDeductibleTypeEnum_NONE;
  @BuiltValueEnumConst(wireName: r'FIXED')
  static const CoverageRequestDeductibleTypeEnum FIXED = _$coverageRequestDeductibleTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'PERCENT')
  static const CoverageRequestDeductibleTypeEnum PERCENT = _$coverageRequestDeductibleTypeEnum_PERCENT;

  static Serializer<CoverageRequestDeductibleTypeEnum> get serializer => _$coverageRequestDeductibleTypeEnumSerializer;

  const CoverageRequestDeductibleTypeEnum._(String name): super(name);

  static BuiltSet<CoverageRequestDeductibleTypeEnum> get values => _$coverageRequestDeductibleTypeEnumValues;
  static CoverageRequestDeductibleTypeEnum valueOf(String name) => _$coverageRequestDeductibleTypeEnumValueOf(name);
}

class CoverageRequestRequiresCodesEnum extends EnumClass {

  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'MTPL')
  static const CoverageRequestRequiresCodesEnum MTPL = _$coverageRequestRequiresCodesEnum_MTPL;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const CoverageRequestRequiresCodesEnum VTPL = _$coverageRequestRequiresCodesEnum_VTPL;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const CoverageRequestRequiresCodesEnum OD_COLLISION = _$coverageRequestRequiresCodesEnum_OD_COLLISION;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const CoverageRequestRequiresCodesEnum OD_GLASS = _$coverageRequestRequiresCodesEnum_OD_GLASS;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const CoverageRequestRequiresCodesEnum OD_FIRE = _$coverageRequestRequiresCodesEnum_OD_FIRE;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const CoverageRequestRequiresCodesEnum OD_NATURE = _$coverageRequestRequiresCodesEnum_OD_NATURE;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const CoverageRequestRequiresCodesEnum OD_THEFT = _$coverageRequestRequiresCodesEnum_OD_THEFT;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const CoverageRequestRequiresCodesEnum OCCUPANTS = _$coverageRequestRequiresCodesEnum_OCCUPANTS;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const CoverageRequestRequiresCodesEnum ASSISTANCE = _$coverageRequestRequiresCodesEnum_ASSISTANCE;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const CoverageRequestRequiresCodesEnum LEGAL = _$coverageRequestRequiresCodesEnum_LEGAL;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const CoverageRequestRequiresCodesEnum SRCC = _$coverageRequestRequiresCodesEnum_SRCC;
  /// Coberturas que têm de existir e ser escolhidas com esta; sem repetidos nem o próprio código (INVALID_REQUIREMENT)
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const CoverageRequestRequiresCodesEnum CROSS_BORDER = _$coverageRequestRequiresCodesEnum_CROSS_BORDER;

  static Serializer<CoverageRequestRequiresCodesEnum> get serializer => _$coverageRequestRequiresCodesEnumSerializer;

  const CoverageRequestRequiresCodesEnum._(String name): super(name);

  static BuiltSet<CoverageRequestRequiresCodesEnum> get values => _$coverageRequestRequiresCodesEnumValues;
  static CoverageRequestRequiresCodesEnum valueOf(String name) => _$coverageRequestRequiresCodesEnumValueOf(name);
}

