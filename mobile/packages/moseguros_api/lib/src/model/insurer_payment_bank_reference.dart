//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/reference_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_payment_bank_reference.g.dart';

/// InsurerPaymentBankReference
///
/// Properties:
/// * [bankTransactionId] 
/// * [entity] 
/// * [paidAt] 
/// * [reference] 
/// * [status] 
@BuiltValue()
abstract class InsurerPaymentBankReference implements Built<InsurerPaymentBankReference, InsurerPaymentBankReferenceBuilder> {
  @BuiltValueField(wireName: r'bankTransactionId')
  String? get bankTransactionId;

  @BuiltValueField(wireName: r'entity')
  String get entity;

  @BuiltValueField(wireName: r'paidAt')
  DateTime? get paidAt;

  @BuiltValueField(wireName: r'reference')
  String get reference;

  @BuiltValueField(wireName: r'status')
  ReferenceStatus get status;
  // enum statusEnum {  ACTIVE,  PAID,  EXPIRED,  CANCELLED,  };

  InsurerPaymentBankReference._();

  factory InsurerPaymentBankReference([void updates(InsurerPaymentBankReferenceBuilder b)]) = _$InsurerPaymentBankReference;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerPaymentBankReferenceBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerPaymentBankReference> get serializer => _$InsurerPaymentBankReferenceSerializer();
}

class _$InsurerPaymentBankReferenceSerializer implements PrimitiveSerializer<InsurerPaymentBankReference> {
  @override
  final Iterable<Type> types = const [InsurerPaymentBankReference, _$InsurerPaymentBankReference];

  @override
  final String wireName = r'InsurerPaymentBankReference';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerPaymentBankReference object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.bankTransactionId != null) {
      yield r'bankTransactionId';
      yield serializers.serialize(
        object.bankTransactionId,
        specifiedType: const FullType(String),
      );
    }
    yield r'entity';
    yield serializers.serialize(
      object.entity,
      specifiedType: const FullType(String),
    );
    if (object.paidAt != null) {
      yield r'paidAt';
      yield serializers.serialize(
        object.paidAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'reference';
    yield serializers.serialize(
      object.reference,
      specifiedType: const FullType(String),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(ReferenceStatus),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerPaymentBankReference object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerPaymentBankReferenceBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'bankTransactionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.bankTransactionId = valueDes;
          break;
        case r'entity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.entity = valueDes;
          break;
        case r'paidAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.paidAt = valueDes;
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
            specifiedType: const FullType(ReferenceStatus),
          ) as ReferenceStatus;
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
  InsurerPaymentBankReference deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerPaymentBankReferenceBuilder();
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

