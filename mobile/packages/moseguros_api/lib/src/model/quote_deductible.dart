//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_deductible.g.dart';

/// QuoteDeductible
///
/// Properties:
/// * [amount] 
/// * [minAmount] 
/// * [percent] 
/// * [type] 
@BuiltValue()
abstract class QuoteDeductible implements Built<QuoteDeductible, QuoteDeductibleBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money? get amount;

  @BuiltValueField(wireName: r'minAmount')
  Money? get minAmount;

  @BuiltValueField(wireName: r'percent')
  String? get percent;

  @BuiltValueField(wireName: r'type')
  QuoteDeductibleTypeEnum get type;
  // enum typeEnum {  NONE,  FIXED,  PERCENT,  };

  QuoteDeductible._();

  factory QuoteDeductible([void updates(QuoteDeductibleBuilder b)]) = _$QuoteDeductible;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteDeductibleBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteDeductible> get serializer => _$QuoteDeductibleSerializer();
}

class _$QuoteDeductibleSerializer implements PrimitiveSerializer<QuoteDeductible> {
  @override
  final Iterable<Type> types = const [QuoteDeductible, _$QuoteDeductible];

  @override
  final String wireName = r'QuoteDeductible';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteDeductible object, {
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
      specifiedType: const FullType(QuoteDeductibleTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteDeductible object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteDeductibleBuilder result,
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
            specifiedType: const FullType(QuoteDeductibleTypeEnum),
          ) as QuoteDeductibleTypeEnum;
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
  QuoteDeductible deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteDeductibleBuilder();
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

class QuoteDeductibleTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NONE')
  static const QuoteDeductibleTypeEnum NONE = _$quoteDeductibleTypeEnum_NONE;
  @BuiltValueEnumConst(wireName: r'FIXED')
  static const QuoteDeductibleTypeEnum FIXED = _$quoteDeductibleTypeEnum_FIXED;
  @BuiltValueEnumConst(wireName: r'PERCENT')
  static const QuoteDeductibleTypeEnum PERCENT = _$quoteDeductibleTypeEnum_PERCENT;

  static Serializer<QuoteDeductibleTypeEnum> get serializer => _$quoteDeductibleTypeEnumSerializer;

  const QuoteDeductibleTypeEnum._(String name): super(name);

  static BuiltSet<QuoteDeductibleTypeEnum> get values => _$quoteDeductibleTypeEnumValues;
  static QuoteDeductibleTypeEnum valueOf(String name) => _$quoteDeductibleTypeEnumValueOf(name);
}

