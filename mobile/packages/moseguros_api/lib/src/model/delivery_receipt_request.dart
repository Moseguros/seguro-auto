//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'delivery_receipt_request.g.dart';

/// DeliveryReceiptRequest
///
/// Properties:
/// * [providerMessageId] - id devolvido pelo fornecedor no envio
/// * [reason] - Motivo indicado pelo fornecedor (sem dados pessoais)
/// * [status] 
@BuiltValue()
abstract class DeliveryReceiptRequest implements Built<DeliveryReceiptRequest, DeliveryReceiptRequestBuilder> {
  /// id devolvido pelo fornecedor no envio
  @BuiltValueField(wireName: r'providerMessageId')
  String get providerMessageId;

  /// Motivo indicado pelo fornecedor (sem dados pessoais)
  @BuiltValueField(wireName: r'reason')
  String? get reason;

  @BuiltValueField(wireName: r'status')
  DeliveryReceiptRequestStatusEnum get status;
  // enum statusEnum {  DELIVERED,  FAILED,  };

  DeliveryReceiptRequest._();

  factory DeliveryReceiptRequest([void updates(DeliveryReceiptRequestBuilder b)]) = _$DeliveryReceiptRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(DeliveryReceiptRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<DeliveryReceiptRequest> get serializer => _$DeliveryReceiptRequestSerializer();
}

class _$DeliveryReceiptRequestSerializer implements PrimitiveSerializer<DeliveryReceiptRequest> {
  @override
  final Iterable<Type> types = const [DeliveryReceiptRequest, _$DeliveryReceiptRequest];

  @override
  final String wireName = r'DeliveryReceiptRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    DeliveryReceiptRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'providerMessageId';
    yield serializers.serialize(
      object.providerMessageId,
      specifiedType: const FullType(String),
    );
    if (object.reason != null) {
      yield r'reason';
      yield serializers.serialize(
        object.reason,
        specifiedType: const FullType(String),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(DeliveryReceiptRequestStatusEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    DeliveryReceiptRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required DeliveryReceiptRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'providerMessageId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.providerMessageId = valueDes;
          break;
        case r'reason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.reason = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DeliveryReceiptRequestStatusEnum),
          ) as DeliveryReceiptRequestStatusEnum;
          result.status = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  DeliveryReceiptRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = DeliveryReceiptRequestBuilder();
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

class DeliveryReceiptRequestStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DELIVERED')
  static const DeliveryReceiptRequestStatusEnum DELIVERED = _$deliveryReceiptRequestStatusEnum_DELIVERED;
  @BuiltValueEnumConst(wireName: r'FAILED')
  static const DeliveryReceiptRequestStatusEnum FAILED = _$deliveryReceiptRequestStatusEnum_FAILED;

  static Serializer<DeliveryReceiptRequestStatusEnum> get serializer => _$deliveryReceiptRequestStatusEnumSerializer;

  const DeliveryReceiptRequestStatusEnum._(String name): super(name);

  static BuiltSet<DeliveryReceiptRequestStatusEnum> get values => _$deliveryReceiptRequestStatusEnumValues;
  static DeliveryReceiptRequestStatusEnum valueOf(String name) => _$deliveryReceiptRequestStatusEnumValueOf(name);
}

