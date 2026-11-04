//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/proposal_deductible.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_coverage.g.dart';

/// ProposalCoverage
///
/// Properties:
/// * [capital] 
/// * [code] 
/// * [deductible] 
/// * [mandatory] 
/// * [premium] 
@BuiltValue()
abstract class ProposalCoverage implements Built<ProposalCoverage, ProposalCoverageBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  @BuiltValueField(wireName: r'code')
  ProposalCoverageCodeEnum get code;
  // enum codeEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  @BuiltValueField(wireName: r'deductible')
  ProposalDeductible get deductible;

  @BuiltValueField(wireName: r'mandatory')
  bool get mandatory;

  @BuiltValueField(wireName: r'premium')
  Money get premium;

  ProposalCoverage._();

  factory ProposalCoverage([void updates(ProposalCoverageBuilder b)]) = _$ProposalCoverage;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalCoverageBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalCoverage> get serializer => _$ProposalCoverageSerializer();
}

class _$ProposalCoverageSerializer implements PrimitiveSerializer<ProposalCoverage> {
  @override
  final Iterable<Type> types = const [ProposalCoverage, _$ProposalCoverage];

  @override
  final String wireName = r'ProposalCoverage';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalCoverage object, {
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
      specifiedType: const FullType(ProposalCoverageCodeEnum),
    );
    yield r'deductible';
    yield serializers.serialize(
      object.deductible,
      specifiedType: const FullType(ProposalDeductible),
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
    ProposalCoverage object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalCoverageBuilder result,
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
            specifiedType: const FullType(ProposalCoverageCodeEnum),
          ) as ProposalCoverageCodeEnum;
          result.code = valueDes;
          break;
        case r'deductible':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalDeductible),
          ) as ProposalDeductible;
          result.deductible.replace(valueDes);
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
  ProposalCoverage deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalCoverageBuilder();
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

class ProposalCoverageCodeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL')
  static const ProposalCoverageCodeEnum MTPL = _$proposalCoverageCodeEnum_MTPL;
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const ProposalCoverageCodeEnum VTPL = _$proposalCoverageCodeEnum_VTPL;
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const ProposalCoverageCodeEnum OD_COLLISION = _$proposalCoverageCodeEnum_OD_COLLISION;
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const ProposalCoverageCodeEnum OD_GLASS = _$proposalCoverageCodeEnum_OD_GLASS;
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const ProposalCoverageCodeEnum OD_FIRE = _$proposalCoverageCodeEnum_OD_FIRE;
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const ProposalCoverageCodeEnum OD_NATURE = _$proposalCoverageCodeEnum_OD_NATURE;
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const ProposalCoverageCodeEnum OD_THEFT = _$proposalCoverageCodeEnum_OD_THEFT;
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const ProposalCoverageCodeEnum OCCUPANTS = _$proposalCoverageCodeEnum_OCCUPANTS;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const ProposalCoverageCodeEnum ASSISTANCE = _$proposalCoverageCodeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const ProposalCoverageCodeEnum LEGAL = _$proposalCoverageCodeEnum_LEGAL;
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const ProposalCoverageCodeEnum SRCC = _$proposalCoverageCodeEnum_SRCC;
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const ProposalCoverageCodeEnum CROSS_BORDER = _$proposalCoverageCodeEnum_CROSS_BORDER;

  static Serializer<ProposalCoverageCodeEnum> get serializer => _$proposalCoverageCodeEnumSerializer;

  const ProposalCoverageCodeEnum._(String name): super(name);

  static BuiltSet<ProposalCoverageCodeEnum> get values => _$proposalCoverageCodeEnumValues;
  static ProposalCoverageCodeEnum valueOf(String name) => _$proposalCoverageCodeEnumValueOf(name);
}

