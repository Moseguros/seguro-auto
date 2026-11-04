//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'requested_coverage_response.g.dart';

/// RequestedCoverageResponse
///
/// Properties:
/// * [capital] 
/// * [code] 
@BuiltValue()
abstract class RequestedCoverageResponse implements Built<RequestedCoverageResponse, RequestedCoverageResponseBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  @BuiltValueField(wireName: r'code')
  RequestedCoverageResponseCodeEnum get code;
  // enum codeEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  RequestedCoverageResponse._();

  factory RequestedCoverageResponse([void updates(RequestedCoverageResponseBuilder b)]) = _$RequestedCoverageResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RequestedCoverageResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RequestedCoverageResponse> get serializer => _$RequestedCoverageResponseSerializer();
}

class _$RequestedCoverageResponseSerializer implements PrimitiveSerializer<RequestedCoverageResponse> {
  @override
  final Iterable<Type> types = const [RequestedCoverageResponse, _$RequestedCoverageResponse];

  @override
  final String wireName = r'RequestedCoverageResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RequestedCoverageResponse object, {
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
      specifiedType: const FullType(RequestedCoverageResponseCodeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RequestedCoverageResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RequestedCoverageResponseBuilder result,
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
            specifiedType: const FullType(RequestedCoverageResponseCodeEnum),
          ) as RequestedCoverageResponseCodeEnum;
          result.code = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RequestedCoverageResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RequestedCoverageResponseBuilder();
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

class RequestedCoverageResponseCodeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL')
  static const RequestedCoverageResponseCodeEnum MTPL = _$requestedCoverageResponseCodeEnum_MTPL;
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const RequestedCoverageResponseCodeEnum VTPL = _$requestedCoverageResponseCodeEnum_VTPL;
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const RequestedCoverageResponseCodeEnum OD_COLLISION = _$requestedCoverageResponseCodeEnum_OD_COLLISION;
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const RequestedCoverageResponseCodeEnum OD_GLASS = _$requestedCoverageResponseCodeEnum_OD_GLASS;
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const RequestedCoverageResponseCodeEnum OD_FIRE = _$requestedCoverageResponseCodeEnum_OD_FIRE;
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const RequestedCoverageResponseCodeEnum OD_NATURE = _$requestedCoverageResponseCodeEnum_OD_NATURE;
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const RequestedCoverageResponseCodeEnum OD_THEFT = _$requestedCoverageResponseCodeEnum_OD_THEFT;
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const RequestedCoverageResponseCodeEnum OCCUPANTS = _$requestedCoverageResponseCodeEnum_OCCUPANTS;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const RequestedCoverageResponseCodeEnum ASSISTANCE = _$requestedCoverageResponseCodeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const RequestedCoverageResponseCodeEnum LEGAL = _$requestedCoverageResponseCodeEnum_LEGAL;
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const RequestedCoverageResponseCodeEnum SRCC = _$requestedCoverageResponseCodeEnum_SRCC;
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const RequestedCoverageResponseCodeEnum CROSS_BORDER = _$requestedCoverageResponseCodeEnum_CROSS_BORDER;

  static Serializer<RequestedCoverageResponseCodeEnum> get serializer => _$requestedCoverageResponseCodeEnumSerializer;

  const RequestedCoverageResponseCodeEnum._(String name): super(name);

  static BuiltSet<RequestedCoverageResponseCodeEnum> get values => _$requestedCoverageResponseCodeEnumValues;
  static RequestedCoverageResponseCodeEnum valueOf(String name) => _$requestedCoverageResponseCodeEnumValueOf(name);
}

