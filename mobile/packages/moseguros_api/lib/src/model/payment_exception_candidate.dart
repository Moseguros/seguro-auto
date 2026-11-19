//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_status.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_exception_candidate.g.dart';

/// PaymentExceptionCandidate
///
/// Properties:
/// * [amount] 
/// * [bankReference] - Referência bancária (11 dígitos) da intenção
/// * [createdAt] 
/// * [id] 
/// * [matchesAmount] 
/// * [matchesReference] 
/// * [reference] 
/// * [status] 
/// * [targetLabel] 
@BuiltValue()
abstract class PaymentExceptionCandidate implements Built<PaymentExceptionCandidate, PaymentExceptionCandidateBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money get amount;

  /// Referência bancária (11 dígitos) da intenção
  @BuiltValueField(wireName: r'bankReference')
  String? get bankReference;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'matchesAmount')
  bool get matchesAmount;

  @BuiltValueField(wireName: r'matchesReference')
  bool get matchesReference;

  @BuiltValueField(wireName: r'reference')
  String get reference;

  @BuiltValueField(wireName: r'status')
  PaymentStatus get status;
  // enum statusEnum {  CREATED,  PENDING,  UNKNOWN,  SUCCEEDED,  FAILED,  EXPIRED,  CANCELLED,  REFUNDED,  };

  @BuiltValueField(wireName: r'targetLabel')
  String get targetLabel;

  PaymentExceptionCandidate._();

  factory PaymentExceptionCandidate([void updates(PaymentExceptionCandidateBuilder b)]) = _$PaymentExceptionCandidate;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentExceptionCandidateBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentExceptionCandidate> get serializer => _$PaymentExceptionCandidateSerializer();
}

class _$PaymentExceptionCandidateSerializer implements PrimitiveSerializer<PaymentExceptionCandidate> {
  @override
  final Iterable<Type> types = const [PaymentExceptionCandidate, _$PaymentExceptionCandidate];

  @override
  final String wireName = r'PaymentExceptionCandidate';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentExceptionCandidate object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'amount';
    yield serializers.serialize(
      object.amount,
      specifiedType: const FullType(Money),
    );
    if (object.bankReference != null) {
      yield r'bankReference';
      yield serializers.serialize(
        object.bankReference,
        specifiedType: const FullType(String),
      );
    }
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'matchesAmount';
    yield serializers.serialize(
      object.matchesAmount,
      specifiedType: const FullType(bool),
    );
    yield r'matchesReference';
    yield serializers.serialize(
      object.matchesReference,
      specifiedType: const FullType(bool),
    );
    yield r'reference';
    yield serializers.serialize(
      object.reference,
      specifiedType: const FullType(String),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PaymentStatus),
    );
    yield r'targetLabel';
    yield serializers.serialize(
      object.targetLabel,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentExceptionCandidate object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentExceptionCandidateBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'amount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.amount.replace(valueDes);
          break;
        case r'bankReference':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.bankReference = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'matchesAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.matchesAmount = valueDes;
          break;
        case r'matchesReference':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.matchesReference = valueDes;
          break;
        case r'reference':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.reference = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentStatus),
          ) as PaymentStatus;
          result.status = valueDes;
          break;
        case r'targetLabel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.targetLabel = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentExceptionCandidate deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentExceptionCandidateBuilder();
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

