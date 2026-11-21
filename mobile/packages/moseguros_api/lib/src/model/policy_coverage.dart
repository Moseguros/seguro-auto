//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_coverage.g.dart';

/// PolicyCoverage
///
/// Properties:
/// * [capital] 
/// * [code] 
/// * [deductibleAmount] 
/// * [deductiblePercent] 
/// * [deductibleType] 
/// * [mandatory] 
/// * [premium] 
@BuiltValue()
abstract class PolicyCoverage implements Built<PolicyCoverage, PolicyCoverageBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'deductibleAmount')
  Money? get deductibleAmount;

  @BuiltValueField(wireName: r'deductiblePercent')
  num? get deductiblePercent;

  @BuiltValueField(wireName: r'deductibleType')
  PolicyCoverageDeductibleTypeEnum get deductibleType;
  // enum deductibleTypeEnum {  NONE,  FIXED,  PERCENT,  };

  @BuiltValueField(wireName: r'mandatory')
  bool get mandatory;

  @BuiltValueField(wireName: r'premium')
  Money get premium;

  PolicyCoverage._();

  factory PolicyCoverage([void updates(PolicyCoverageBuilder b)]) = _$PolicyCoverage;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PolicyCoverageBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PolicyCoverage> get serializer => _$PolicyCoverageSerializer();
}

class _$PolicyCoverageSerializer implements PrimitiveSerializer<PolicyCoverage> {
  @override
  final Iterable<Type> types = const [PolicyCoverage, _$PolicyCoverage];

  @override
  final String wireName = r'PolicyCoverage';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PolicyCoverage object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.capital != null) {
      yield r'capital';
      yield serializers.serialize(
        object.capital,
        specifiedType: const FullType(Money),
      );
    }
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    if (object.deductibleAmount != null) {
      yield r'deductibleAmount';
      yield serializers.serialize(
        object.deductibleAmount,
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
      specifiedType: const FullType(PolicyCoverageDeductibleTypeEnum),
    );
    yield r'mandatory';
    yield serializers.serialize(
      object.mandatory,
      specifiedType: const FullType(bool),
    );
    yield r'premium';
    yield serializers.serialize(
      object.premium,
      specifiedType: const FullType(Money),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PolicyCoverage object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PolicyCoverageBuilder result,
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
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'deductibleAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.deductibleAmount.replace(valueDes);
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
            specifiedType: const FullType(PolicyCoverageDeductibleTypeEnum),
          ) as PolicyCoverageDeductibleTypeEnum;
          result.deductibleType = valueDes;
          break;
        case r'mandatory':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.mandatory = valueDes;
          break;
        case r'premium':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.premium.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PolicyCoverage deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PolicyCoverageBuilder();
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

class PolicyCoverageDeductibleTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NONE')
  static const PolicyCoverageDeductibleTypeEnum NONE = _$policyCoverageDeductibleTypeEnum_NONE;
  @BuiltValueEnumConst(wireName: r'FIXED')
  static const PolicyCoverageDeductibleTypeEnum FIXED = _$policyCoverageDeductibleTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'PERCENT')
  static const PolicyCoverageDeductibleTypeEnum PERCENT = _$policyCoverageDeductibleTypeEnum_PERCENT;

  static Serializer<PolicyCoverageDeductibleTypeEnum> get serializer => _$policyCoverageDeductibleTypeEnumSerializer;

  const PolicyCoverageDeductibleTypeEnum._(String name): super(name);

  static BuiltSet<PolicyCoverageDeductibleTypeEnum> get values => _$policyCoverageDeductibleTypeEnumValues;
  static PolicyCoverageDeductibleTypeEnum valueOf(String name) => _$policyCoverageDeductibleTypeEnumValueOf(name);
}

