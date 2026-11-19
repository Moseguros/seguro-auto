//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_file_source_insurer.g.dart';

/// PaymentFileSourceInsurer
///
/// Properties:
/// * [displayName] 
/// * [id] 
@BuiltValue()
abstract class PaymentFileSourceInsurer implements Built<PaymentFileSourceInsurer, PaymentFileSourceInsurerBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  PaymentFileSourceInsurer._();

  factory PaymentFileSourceInsurer([void updates(PaymentFileSourceInsurerBuilder b)]) = _$PaymentFileSourceInsurer;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentFileSourceInsurerBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentFileSourceInsurer> get serializer => _$PaymentFileSourceInsurerSerializer();
}

class _$PaymentFileSourceInsurerSerializer implements PrimitiveSerializer<PaymentFileSourceInsurer> {
  @override
  final Iterable<Type> types = const [PaymentFileSourceInsurer, _$PaymentFileSourceInsurer];

  @override
  final String wireName = r'PaymentFileSourceInsurer';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentFileSourceInsurer object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'displayName';
    yield serializers.serialize(
      object.displayName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentFileSourceInsurer object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentFileSourceInsurerBuilder result,
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
  PaymentFileSourceInsurer deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentFileSourceInsurerBuilder();
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

