//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_exception_candidate.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_exception_candidates.g.dart';

/// PaymentExceptionCandidates
///
/// Properties:
/// * [candidates] 
@BuiltValue()
abstract class PaymentExceptionCandidates implements Built<PaymentExceptionCandidates, PaymentExceptionCandidatesBuilder> {
  @BuiltValueField(wireName: r'candidates')
  BuiltList<PaymentExceptionCandidate> get candidates;

  PaymentExceptionCandidates._();

  factory PaymentExceptionCandidates([void updates(PaymentExceptionCandidatesBuilder b)]) = _$PaymentExceptionCandidates;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentExceptionCandidatesBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentExceptionCandidates> get serializer => _$PaymentExceptionCandidatesSerializer();
}

class _$PaymentExceptionCandidatesSerializer implements PrimitiveSerializer<PaymentExceptionCandidates> {
  @override
  final Iterable<Type> types = const [PaymentExceptionCandidates, _$PaymentExceptionCandidates];

  @override
  final String wireName = r'PaymentExceptionCandidates';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentExceptionCandidates object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'candidates';
    yield serializers.serialize(
      object.candidates,
      specifiedType: const FullType(BuiltList, [FullType(PaymentExceptionCandidate)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentExceptionCandidates object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentExceptionCandidatesBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'candidates':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PaymentExceptionCandidate)]),
          ) as BuiltList<PaymentExceptionCandidate>;
          result.candidates.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentExceptionCandidates deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentExceptionCandidatesBuilder();
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

