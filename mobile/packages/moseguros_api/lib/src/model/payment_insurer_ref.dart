//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_insurer_ref.g.dart';

/// PaymentInsurerRef
///
/// Properties:
/// * [displayName] 
/// * [id] 
@BuiltValue()
abstract class PaymentInsurerRef implements Built<PaymentInsurerRef, PaymentInsurerRefBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  PaymentInsurerRef._();

  factory PaymentInsurerRef([void updates(PaymentInsurerRefBuilder b)]) = _$PaymentInsurerRef;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentInsurerRefBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentInsurerRef> get serializer => _$PaymentInsurerRefSerializer();
}

class _$PaymentInsurerRefSerializer implements PrimitiveSerializer<PaymentInsurerRef> {
  @override
  final Iterable<Type> types = const [PaymentInsurerRef, _$PaymentInsurerRef];

  @override
  final String wireName = r'PaymentInsurerRef';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentInsurerRef object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType(String),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentInsurerRef object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentInsurerRefBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentInsurerRef deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentInsurerRefBuilder();
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

