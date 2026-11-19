//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/failure_code.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/payment_status.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_payment_summary.g.dart';

/// InsurerPaymentSummary
///
/// Properties:
/// * [amount] 
/// * [createdAt] 
/// * [failureCode] 
/// * [id] 
/// * [method] 
/// * [providerTxId] 
/// * [reference] 
/// * [status] 
/// * [succeededAt] 
/// * [targetLabel] 
@BuiltValue()
abstract class InsurerPaymentSummary implements Built<InsurerPaymentSummary, InsurerPaymentSummaryBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money get amount;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'failureCode')
  FailureCode? get failureCode;
  // enum failureCodeEnum {  CUSTOMER_CANCELLED,  INSUFFICIENT_BALANCE,  INVALID_MSISDN,  PROVIDER_REJECTED,  PROVIDER_CONFIG_ERROR,  PROVIDER_UNAVAILABLE,  TIMEOUT_EXPIRED,  EXPIRED,  SUPERSEDED,  MANUAL,  };

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'method')
  InsurerPaymentSummaryMethodEnum get method;
  // enum methodEnum {  MPESA,  BANK_REFERENCE,  };

  @BuiltValueField(wireName: r'providerTxId')
  String? get providerTxId;

  @BuiltValueField(wireName: r'reference')
  String get reference;

  @BuiltValueField(wireName: r'status')
  PaymentStatus get status;
  // enum statusEnum {  CREATED,  PENDING,  UNKNOWN,  SUCCEEDED,  FAILED,  EXPIRED,  CANCELLED,  REFUNDED,  };

  @BuiltValueField(wireName: r'succeededAt')
  DateTime? get succeededAt;

  @BuiltValueField(wireName: r'targetLabel')
  String get targetLabel;

  InsurerPaymentSummary._();

  factory InsurerPaymentSummary([void updates(InsurerPaymentSummaryBuilder b)]) = _$InsurerPaymentSummary;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerPaymentSummaryBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerPaymentSummary> get serializer => _$InsurerPaymentSummarySerializer();
}

class _$InsurerPaymentSummarySerializer implements PrimitiveSerializer<InsurerPaymentSummary> {
  @override
  final Iterable<Type> types = const [InsurerPaymentSummary, _$InsurerPaymentSummary];

  @override
  final String wireName = r'InsurerPaymentSummary';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerPaymentSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'amount';
    yield serializers.serialize(
      object.amount,
      specifiedType: const FullType(Money),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.failureCode != null) {
      yield r'failureCode';
      yield serializers.serialize(
        object.failureCode,
        specifiedType: const FullType(FailureCode),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'method';
    yield serializers.serialize(
      object.method,
      specifiedType: const FullType(InsurerPaymentSummaryMethodEnum),
    );
    if (object.providerTxId != null) {
      yield r'providerTxId';
      yield serializers.serialize(
        object.providerTxId,
        specifiedType: const FullType(String),
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
      specifiedType: const FullType(PaymentStatus),
    );
    if (object.succeededAt != null) {
      yield r'succeededAt';
      yield serializers.serialize(
        object.succeededAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'targetLabel';
    yield serializers.serialize(
      object.targetLabel,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerPaymentSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerPaymentSummaryBuilder result,
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
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'failureCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(FailureCode),
          ) as FailureCode;
          result.failureCode = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'method':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InsurerPaymentSummaryMethodEnum),
          ) as InsurerPaymentSummaryMethodEnum;
          result.method = valueDes;
          break;
        case r'providerTxId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.providerTxId = valueDes;
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
        case r'succeededAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.succeededAt = valueDes;
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
  InsurerPaymentSummary deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerPaymentSummaryBuilder();
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

class InsurerPaymentSummaryMethodEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MPESA')
  static const InsurerPaymentSummaryMethodEnum MPESA = _$insurerPaymentSummaryMethodEnum_MPESA;
  @BuiltValueEnumConst(wireName: r'BANK_REFERENCE')
  static const InsurerPaymentSummaryMethodEnum BANK_REFERENCE = _$insurerPaymentSummaryMethodEnum_BANK_REFERENCE;

  static Serializer<InsurerPaymentSummaryMethodEnum> get serializer => _$insurerPaymentSummaryMethodEnumSerializer;

  const InsurerPaymentSummaryMethodEnum._(String name): super(name);

  static BuiltSet<InsurerPaymentSummaryMethodEnum> get values => _$insurerPaymentSummaryMethodEnumValues;
  static InsurerPaymentSummaryMethodEnum valueOf(String name) => _$insurerPaymentSummaryMethodEnumValueOf(name);
}

