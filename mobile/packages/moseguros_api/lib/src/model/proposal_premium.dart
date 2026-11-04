//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/proposal_charge.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_premium.g.dart';

/// ProposalPremium
///
/// Properties:
/// * [charges] 
/// * [commercial] 
/// * [roundingAdjustment] 
/// * [total] 
@BuiltValue()
abstract class ProposalPremium implements Built<ProposalPremium, ProposalPremiumBuilder> {
  @BuiltValueField(wireName: r'charges')
  BuiltList<ProposalCharge> get charges;

  @BuiltValueField(wireName: r'commercial')
  Money get commercial;

  @BuiltValueField(wireName: r'roundingAdjustment')
  Money get roundingAdjustment;

  @BuiltValueField(wireName: r'total')
  Money get total;

  ProposalPremium._();

  factory ProposalPremium([void updates(ProposalPremiumBuilder b)]) = _$ProposalPremium;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalPremiumBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalPremium> get serializer => _$ProposalPremiumSerializer();
}

class _$ProposalPremiumSerializer implements PrimitiveSerializer<ProposalPremium> {
  @override
  final Iterable<Type> types = const [ProposalPremium, _$ProposalPremium];

  @override
  final String wireName = r'ProposalPremium';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalPremium object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'charges';
    yield serializers.serialize(
      object.charges,
      specifiedType: const FullType(BuiltList, [FullType(ProposalCharge)]),
    );
    yield r'commercial';
    yield serializers.serialize(
      object.commercial,
      specifiedType: const FullType(Money),
    );
    yield r'roundingAdjustment';
    yield serializers.serialize(
      object.roundingAdjustment,
      specifiedType: const FullType(Money),
    );
    yield r'total';
    yield serializers.serialize(
      object.total,
      specifiedType: const FullType(Money),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalPremium object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalPremiumBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'charges':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(ProposalCharge)]),
          ) as BuiltList<ProposalCharge>;
          result.charges.replace(valueDes);
          break;
        case r'commercial':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.commercial.replace(valueDes);
          break;
        case r'roundingAdjustment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.roundingAdjustment.replace(valueDes);
          break;
        case r'total':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.total.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalPremium deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalPremiumBuilder();
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

