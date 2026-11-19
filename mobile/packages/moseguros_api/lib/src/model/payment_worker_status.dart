//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/payment_queue_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_worker_status.g.dart';

/// PaymentWorkerStatus
///
/// Properties:
/// * [brokerReachable] 
/// * [lastMessageAt] - Última mensagem recebida do worker
/// * [queues] - Vazio se o broker não responder
@BuiltValue()
abstract class PaymentWorkerStatus implements Built<PaymentWorkerStatus, PaymentWorkerStatusBuilder> {
  @BuiltValueField(wireName: r'brokerReachable')
  bool get brokerReachable;

  /// Última mensagem recebida do worker
  @BuiltValueField(wireName: r'lastMessageAt')
  DateTime? get lastMessageAt;

  /// Vazio se o broker não responder
  @BuiltValueField(wireName: r'queues')
  BuiltList<PaymentQueueStatus> get queues;

  PaymentWorkerStatus._();

  factory PaymentWorkerStatus([void updates(PaymentWorkerStatusBuilder b)]) = _$PaymentWorkerStatus;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentWorkerStatusBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentWorkerStatus> get serializer => _$PaymentWorkerStatusSerializer();
}

class _$PaymentWorkerStatusSerializer implements PrimitiveSerializer<PaymentWorkerStatus> {
  @override
  final Iterable<Type> types = const [PaymentWorkerStatus, _$PaymentWorkerStatus];

  @override
  final String wireName = r'PaymentWorkerStatus';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentWorkerStatus object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'brokerReachable';
    yield serializers.serialize(
      object.brokerReachable,
      specifiedType: const FullType(bool),
    );
    if (object.lastMessageAt != null) {
      yield r'lastMessageAt';
      yield serializers.serialize(
        object.lastMessageAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'queues';
    yield serializers.serialize(
      object.queues,
      specifiedType: const FullType(BuiltList, [FullType(PaymentQueueStatus)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentWorkerStatus object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentWorkerStatusBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'brokerReachable':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.brokerReachable = valueDes;
          break;
        case r'lastMessageAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.lastMessageAt = valueDes;
          break;
        case r'queues':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PaymentQueueStatus)]),
          ) as BuiltList<PaymentQueueStatus>;
          result.queues.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentWorkerStatus deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentWorkerStatusBuilder();
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

