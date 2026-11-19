//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_status.dart';
import 'package:moseguros_api/src/model/payment_event_type.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_payment_event.g.dart';

/// InsurerPaymentEvent
///
/// Properties:
/// * [fromStatus] 
/// * [receivedAt] 
/// * [toStatus] 
/// * [type] 
@BuiltValue()
abstract class InsurerPaymentEvent implements Built<InsurerPaymentEvent, InsurerPaymentEventBuilder> {
  @BuiltValueField(wireName: r'fromStatus')
  PaymentStatus? get fromStatus;
  // enum fromStatusEnum {  CREATED,  PENDING,  UNKNOWN,  SUCCEEDED,  FAILED,  EXPIRED,  CANCELLED,  REFUNDED,  };

  @BuiltValueField(wireName: r'receivedAt')
  DateTime get receivedAt;

  @BuiltValueField(wireName: r'toStatus')
  PaymentStatus? get toStatus;
  // enum toStatusEnum {  CREATED,  PENDING,  UNKNOWN,  SUCCEEDED,  FAILED,  EXPIRED,  CANCELLED,  REFUNDED,  };

  @BuiltValueField(wireName: r'type')
  PaymentEventType get type;
  // enum typeEnum {  CREATED,  DISPATCHED,  PROVIDER_RESPONSE,  STATUS_QUERY,  STATUS_CHANGED,  REFERENCE_PAID,  MANUAL_RESOLUTION,  };

  InsurerPaymentEvent._();

  factory InsurerPaymentEvent([void updates(InsurerPaymentEventBuilder b)]) = _$InsurerPaymentEvent;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerPaymentEventBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerPaymentEvent> get serializer => _$InsurerPaymentEventSerializer();
}

class _$InsurerPaymentEventSerializer implements PrimitiveSerializer<InsurerPaymentEvent> {
  @override
  final Iterable<Type> types = const [InsurerPaymentEvent, _$InsurerPaymentEvent];

  @override
  final String wireName = r'InsurerPaymentEvent';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerPaymentEvent object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.fromStatus != null) {
      yield r'fromStatus';
      yield serializers.serialize(
        object.fromStatus,
        specifiedType: const FullType(PaymentStatus),
      );
    }
    yield r'receivedAt';
    yield serializers.serialize(
      object.receivedAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.toStatus != null) {
      yield r'toStatus';
      yield serializers.serialize(
        object.toStatus,
        specifiedType: const FullType(PaymentStatus),
      );
    }
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PaymentEventType),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerPaymentEvent object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerPaymentEventBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fromStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentStatus),
          ) as PaymentStatus;
          result.fromStatus = valueDes;
          break;
        case r'receivedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.receivedAt = valueDes;
          break;
        case r'toStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentStatus),
          ) as PaymentStatus;
          result.toStatus = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentEventType),
          ) as PaymentEventType;
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
  InsurerPaymentEvent deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerPaymentEventBuilder();
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

