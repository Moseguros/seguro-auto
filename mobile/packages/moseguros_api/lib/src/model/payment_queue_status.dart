//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_queue_status.g.dart';

/// Vazio se o broker não responder
///
/// Properties:
/// * [consumers] - -1 se a fila não existir
/// * [messages] - -1 se a fila não existir
/// * [name] 
@BuiltValue()
abstract class PaymentQueueStatus implements Built<PaymentQueueStatus, PaymentQueueStatusBuilder> {
  /// -1 se a fila não existir
  @BuiltValueField(wireName: r'consumers')
  int get consumers;

  /// -1 se a fila não existir
  @BuiltValueField(wireName: r'messages')
  int get messages;

  @BuiltValueField(wireName: r'name')
  String get name;

  PaymentQueueStatus._();

  factory PaymentQueueStatus([void updates(PaymentQueueStatusBuilder b)]) = _$PaymentQueueStatus;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentQueueStatusBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentQueueStatus> get serializer => _$PaymentQueueStatusSerializer();
}

class _$PaymentQueueStatusSerializer implements PrimitiveSerializer<PaymentQueueStatus> {
  @override
  final Iterable<Type> types = const [PaymentQueueStatus, _$PaymentQueueStatus];

  @override
  final String wireName = r'PaymentQueueStatus';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentQueueStatus object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'consumers';
    yield serializers.serialize(
      object.consumers,
      specifiedType: const FullType(int),
    );
    yield r'messages';
    yield serializers.serialize(
      object.messages,
      specifiedType: const FullType(int),
    );
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentQueueStatus object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentQueueStatusBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'consumers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.consumers = valueDes;
          break;
        case r'messages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.messages = valueDes;
          break;
        case r'name':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.name = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentQueueStatus deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentQueueStatusBuilder();
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

