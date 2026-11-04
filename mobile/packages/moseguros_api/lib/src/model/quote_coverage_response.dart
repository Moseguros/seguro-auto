//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/quote_deductible.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_coverage_response.g.dart';

/// QuoteCoverageResponse
///
/// Properties:
/// * [capital] 
/// * [code] 
/// * [deductible] 
/// * [mandatory] 
/// * [premium] 
@BuiltValue()
abstract class QuoteCoverageResponse implements Built<QuoteCoverageResponse, QuoteCoverageResponseBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  @BuiltValueField(wireName: r'code')
  QuoteCoverageResponseCodeEnum get code;
  // enum codeEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  @BuiltValueField(wireName: r'deductible')
  QuoteDeductible get deductible;

  @BuiltValueField(wireName: r'mandatory')
  bool get mandatory;

  @BuiltValueField(wireName: r'premium')
  Money get premium;

  QuoteCoverageResponse._();

  factory QuoteCoverageResponse([void updates(QuoteCoverageResponseBuilder b)]) = _$QuoteCoverageResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteCoverageResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteCoverageResponse> get serializer => _$QuoteCoverageResponseSerializer();
}

class _$QuoteCoverageResponseSerializer implements PrimitiveSerializer<QuoteCoverageResponse> {
  @override
  final Iterable<Type> types = const [QuoteCoverageResponse, _$QuoteCoverageResponse];

  @override
  final String wireName = r'QuoteCoverageResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteCoverageResponse object, {
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
      specifiedType: const FullType(QuoteCoverageResponseCodeEnum),
    );
    yield r'deductible';
    yield serializers.serialize(
      object.deductible,
      specifiedType: const FullType(QuoteDeductible),
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
    QuoteCoverageResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteCoverageResponseBuilder result,
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
            specifiedType: const FullType(QuoteCoverageResponseCodeEnum),
          ) as QuoteCoverageResponseCodeEnum;
          result.code = valueDes;
          break;
        case r'deductible':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteDeductible),
          ) as QuoteDeductible;
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
  QuoteCoverageResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteCoverageResponseBuilder();
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

class QuoteCoverageResponseCodeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL')
  static const QuoteCoverageResponseCodeEnum MTPL = _$quoteCoverageResponseCodeEnum_MTPL;
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const QuoteCoverageResponseCodeEnum VTPL = _$quoteCoverageResponseCodeEnum_VTPL;
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const QuoteCoverageResponseCodeEnum OD_COLLISION = _$quoteCoverageResponseCodeEnum_OD_COLLISION;
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const QuoteCoverageResponseCodeEnum OD_GLASS = _$quoteCoverageResponseCodeEnum_OD_GLASS;
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const QuoteCoverageResponseCodeEnum OD_FIRE = _$quoteCoverageResponseCodeEnum_OD_FIRE;
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const QuoteCoverageResponseCodeEnum OD_NATURE = _$quoteCoverageResponseCodeEnum_OD_NATURE;
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const QuoteCoverageResponseCodeEnum OD_THEFT = _$quoteCoverageResponseCodeEnum_OD_THEFT;
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const QuoteCoverageResponseCodeEnum OCCUPANTS = _$quoteCoverageResponseCodeEnum_OCCUPANTS;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const QuoteCoverageResponseCodeEnum ASSISTANCE = _$quoteCoverageResponseCodeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const QuoteCoverageResponseCodeEnum LEGAL = _$quoteCoverageResponseCodeEnum_LEGAL;
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const QuoteCoverageResponseCodeEnum SRCC = _$quoteCoverageResponseCodeEnum_SRCC;
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const QuoteCoverageResponseCodeEnum CROSS_BORDER = _$quoteCoverageResponseCodeEnum_CROSS_BORDER;

  static Serializer<QuoteCoverageResponseCodeEnum> get serializer => _$quoteCoverageResponseCodeEnumSerializer;

  const QuoteCoverageResponseCodeEnum._(String name): super(name);

  static BuiltSet<QuoteCoverageResponseCodeEnum> get values => _$quoteCoverageResponseCodeEnumValues;
  static QuoteCoverageResponseCodeEnum valueOf(String name) => _$quoteCoverageResponseCodeEnumValueOf(name);
}

