//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_target_type.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_target_ref.g.dart';

/// PaymentTargetRef
///
/// Properties:
/// * [id] 
/// * [label] 
/// * [type] 
@BuiltValue()
abstract class PaymentTargetRef implements Built<PaymentTargetRef, PaymentTargetRefBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'label')
  String get label;

  @BuiltValueField(wireName: r'type')
  PaymentTargetType get type;
  // enum typeEnum {  PROPOSAL,  INSTALMENT,  RENEWAL,  ENDORSEMENT,  FLEET_INCLUSION,  };

  PaymentTargetRef._();

  factory PaymentTargetRef([void updates(PaymentTargetRefBuilder b)]) = _$PaymentTargetRef;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentTargetRefBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentTargetRef> get serializer => _$PaymentTargetRefSerializer();
}

class _$PaymentTargetRefSerializer implements PrimitiveSerializer<PaymentTargetRef> {
  @override
  final Iterable<Type> types = const [PaymentTargetRef, _$PaymentTargetRef];

  @override
  final String wireName = r'PaymentTargetRef';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentTargetRef object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'label';
    yield serializers.serialize(
      object.label,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PaymentTargetType),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentTargetRef object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentTargetRefBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'label':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.label = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentTargetType),
          ) as PaymentTargetType;
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
  PaymentTargetRef deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentTargetRefBuilder();
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

