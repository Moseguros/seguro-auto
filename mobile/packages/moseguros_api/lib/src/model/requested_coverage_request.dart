//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'requested_coverage_request.g.dart';

/// RequestedCoverageRequest
///
/// Properties:
/// * [capital] 
/// * [code] 
@BuiltValue()
abstract class RequestedCoverageRequest implements Built<RequestedCoverageRequest, RequestedCoverageRequestBuilder> {
  @BuiltValueField(wireName: r'capital')
  Money? get capital;

  @BuiltValueField(wireName: r'code')
  RequestedCoverageRequestCodeEnum get code;
  // enum codeEnum {  MTPL,  VTPL,  OD_COLLISION,  OD_GLASS,  OD_FIRE,  OD_NATURE,  OD_THEFT,  OCCUPANTS,  ASSISTANCE,  LEGAL,  SRCC,  CROSS_BORDER,  };

  RequestedCoverageRequest._();

  factory RequestedCoverageRequest([void updates(RequestedCoverageRequestBuilder b)]) = _$RequestedCoverageRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RequestedCoverageRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RequestedCoverageRequest> get serializer => _$RequestedCoverageRequestSerializer();
}

class _$RequestedCoverageRequestSerializer implements PrimitiveSerializer<RequestedCoverageRequest> {
  @override
  final Iterable<Type> types = const [RequestedCoverageRequest, _$RequestedCoverageRequest];

  @override
  final String wireName = r'RequestedCoverageRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RequestedCoverageRequest object, {
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
      specifiedType: const FullType(RequestedCoverageRequestCodeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RequestedCoverageRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RequestedCoverageRequestBuilder result,
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
            specifiedType: const FullType(RequestedCoverageRequestCodeEnum),
          ) as RequestedCoverageRequestCodeEnum;
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
  RequestedCoverageRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RequestedCoverageRequestBuilder();
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

class RequestedCoverageRequestCodeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL')
  static const RequestedCoverageRequestCodeEnum MTPL = _$requestedCoverageRequestCodeEnum_MTPL;
  @BuiltValueEnumConst(wireName: r'VTPL')
  static const RequestedCoverageRequestCodeEnum VTPL = _$requestedCoverageRequestCodeEnum_VTPL;
  @BuiltValueEnumConst(wireName: r'OD_COLLISION')
  static const RequestedCoverageRequestCodeEnum OD_COLLISION = _$requestedCoverageRequestCodeEnum_OD_COLLISION;
  @BuiltValueEnumConst(wireName: r'OD_GLASS')
  static const RequestedCoverageRequestCodeEnum OD_GLASS = _$requestedCoverageRequestCodeEnum_OD_GLASS;
  @BuiltValueEnumConst(wireName: r'OD_FIRE')
  static const RequestedCoverageRequestCodeEnum OD_FIRE = _$requestedCoverageRequestCodeEnum_OD_FIRE;
  @BuiltValueEnumConst(wireName: r'OD_NATURE')
  static const RequestedCoverageRequestCodeEnum OD_NATURE = _$requestedCoverageRequestCodeEnum_OD_NATURE;
  @BuiltValueEnumConst(wireName: r'OD_THEFT')
  static const RequestedCoverageRequestCodeEnum OD_THEFT = _$requestedCoverageRequestCodeEnum_OD_THEFT;
  @BuiltValueEnumConst(wireName: r'OCCUPANTS')
  static const RequestedCoverageRequestCodeEnum OCCUPANTS = _$requestedCoverageRequestCodeEnum_OCCUPANTS;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const RequestedCoverageRequestCodeEnum ASSISTANCE = _$requestedCoverageRequestCodeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const RequestedCoverageRequestCodeEnum LEGAL = _$requestedCoverageRequestCodeEnum_LEGAL;
  @BuiltValueEnumConst(wireName: r'SRCC')
  static const RequestedCoverageRequestCodeEnum SRCC = _$requestedCoverageRequestCodeEnum_SRCC;
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER')
  static const RequestedCoverageRequestCodeEnum CROSS_BORDER = _$requestedCoverageRequestCodeEnum_CROSS_BORDER;

  static Serializer<RequestedCoverageRequestCodeEnum> get serializer => _$requestedCoverageRequestCodeEnumSerializer;

  const RequestedCoverageRequestCodeEnum._(String name): super(name);

  static BuiltSet<RequestedCoverageRequestCodeEnum> get values => _$requestedCoverageRequestCodeEnumValues;
  static RequestedCoverageRequestCodeEnum valueOf(String name) => _$requestedCoverageRequestCodeEnumValueOf(name);
}

