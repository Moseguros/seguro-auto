//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_deductible.g.dart';

/// ProposalDeductible
///
/// Properties:
/// * [amount] 
/// * [minAmount] 
/// * [percent] 
/// * [type] 
@BuiltValue()
abstract class ProposalDeductible implements Built<ProposalDeductible, ProposalDeductibleBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money? get amount;

  @BuiltValueField(wireName: r'minAmount')
  Money? get minAmount;

  @BuiltValueField(wireName: r'percent')
  String? get percent;

  @BuiltValueField(wireName: r'type')
  ProposalDeductibleTypeEnum get type;
  // enum typeEnum {  NONE,  FIXED,  PERCENT,  };

  ProposalDeductible._();

  factory ProposalDeductible([void updates(ProposalDeductibleBuilder b)]) = _$ProposalDeductible;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalDeductibleBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalDeductible> get serializer => _$ProposalDeductibleSerializer();
}

class _$ProposalDeductibleSerializer implements PrimitiveSerializer<ProposalDeductible> {
  @override
  final Iterable<Type> types = const [ProposalDeductible, _$ProposalDeductible];

  @override
  final String wireName = r'ProposalDeductible';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalDeductible object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.amount != null) {
      yield r'amount';
      yield serializers.serialize(
        object.amount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.minAmount != null) {
      yield r'minAmount';
      yield serializers.serialize(
        object.minAmount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.percent != null) {
      yield r'percent';
      yield serializers.serialize(
        object.percent,
        specifiedType: const FullType(String),
      );
    }
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ProposalDeductibleTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalDeductible object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalDeductibleBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'amount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.amount.replace(valueDes);
          break;
        case r'minAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.minAmount.replace(valueDes);
          break;
        case r'percent':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.percent = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalDeductibleTypeEnum),
          ) as ProposalDeductibleTypeEnum;
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
  ProposalDeductible deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalDeductibleBuilder();
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

class ProposalDeductibleTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NONE')
  static const ProposalDeductibleTypeEnum NONE = _$proposalDeductibleTypeEnum_NONE;
  @BuiltValueEnumConst(wireName: r'FIXED')
  static const ProposalDeductibleTypeEnum FIXED = _$proposalDeductibleTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'PERCENT')
  static const ProposalDeductibleTypeEnum PERCENT = _$proposalDeductibleTypeEnum_PERCENT;

  static Serializer<ProposalDeductibleTypeEnum> get serializer => _$proposalDeductibleTypeEnumSerializer;

  const ProposalDeductibleTypeEnum._(String name): super(name);

  static BuiltSet<ProposalDeductibleTypeEnum> get values => _$proposalDeductibleTypeEnumValues;
  static ProposalDeductibleTypeEnum valueOf(String name) => _$proposalDeductibleTypeEnumValueOf(name);
}

