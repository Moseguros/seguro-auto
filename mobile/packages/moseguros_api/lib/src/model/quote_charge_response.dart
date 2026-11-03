//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_charge_response.g.dart';

/// QuoteChargeResponse
///
/// Properties:
/// * [amount] 
/// * [code] 
/// * [labelEn] 
/// * [labelPt] 
@BuiltValue()
abstract class QuoteChargeResponse implements Built<QuoteChargeResponse, QuoteChargeResponseBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money get amount;

  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'labelEn')
  String get labelEn;

  @BuiltValueField(wireName: r'labelPt')
  String get labelPt;

  QuoteChargeResponse._();

  factory QuoteChargeResponse([void updates(QuoteChargeResponseBuilder b)]) = _$QuoteChargeResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteChargeResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteChargeResponse> get serializer => _$QuoteChargeResponseSerializer();
}

class _$QuoteChargeResponseSerializer implements PrimitiveSerializer<QuoteChargeResponse> {
  @override
  final Iterable<Type> types = const [QuoteChargeResponse, _$QuoteChargeResponse];

  @override
  final String wireName = r'QuoteChargeResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteChargeResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'amount';
    yield serializers.serialize(
      object.amount,
      specifiedType: const FullType(Money),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'labelEn';
    yield serializers.serialize(
      object.labelEn,
      specifiedType: const FullType(String),
    );
    yield r'labelPt';
    yield serializers.serialize(
      object.labelPt,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteChargeResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteChargeResponseBuilder result,
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
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'labelEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelEn = valueDes;
          break;
        case r'labelPt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelPt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  QuoteChargeResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteChargeResponseBuilder();
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

