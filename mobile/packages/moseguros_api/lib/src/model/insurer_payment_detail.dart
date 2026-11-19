//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_purpose.dart';
import 'package:moseguros_api/src/model/insurer_payment_event.dart';
import 'package:moseguros_api/src/model/failure_code.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/payment_target_ref.dart';
import 'package:moseguros_api/src/model/payment_status.dart';
import 'package:moseguros_api/src/model/insurer_payment_bank_reference.dart';
import 'package:moseguros_api/src/model/payment_confirmed_by.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_payment_detail.g.dart';

/// InsurerPaymentDetail
///
/// Properties:
/// * [amount] 
/// * [bankReference] 
/// * [confirmedBy] 
/// * [createdAt] 
/// * [events] 
/// * [exceptionIds] 
/// * [expiresAt] 
/// * [failureCode] 
/// * [id] 
/// * [method] 
/// * [payerMsisdnMasked] 
/// * [providerTxId] 
/// * [purpose] 
/// * [reference] 
/// * [status] 
/// * [succeededAt] 
/// * [target] 
/// * [targetLabel] 
@BuiltValue()
abstract class InsurerPaymentDetail implements Built<InsurerPaymentDetail, InsurerPaymentDetailBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money get amount;

  @BuiltValueField(wireName: r'bankReference')
  InsurerPaymentBankReference? get bankReference;

  @BuiltValueField(wireName: r'confirmedBy')
  PaymentConfirmedBy? get confirmedBy;
  // enum confirmedByEnum {  PROVIDER,  STATUS_QUERY,  REFERENCE_FILE,  MANUAL,  };

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'events')
  BuiltList<InsurerPaymentEvent> get events;

  @BuiltValueField(wireName: r'exceptionIds')
  BuiltList<String> get exceptionIds;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  @BuiltValueField(wireName: r'failureCode')
  FailureCode? get failureCode;
  // enum failureCodeEnum {  CUSTOMER_CANCELLED,  INSUFFICIENT_BALANCE,  INVALID_MSISDN,  PROVIDER_REJECTED,  PROVIDER_CONFIG_ERROR,  PROVIDER_UNAVAILABLE,  TIMEOUT_EXPIRED,  EXPIRED,  SUPERSEDED,  MANUAL,  };

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'method')
  InsurerPaymentDetailMethodEnum get method;
  // enum methodEnum {  MPESA,  BANK_REFERENCE,  };

  @BuiltValueField(wireName: r'payerMsisdnMasked')
  String? get payerMsisdnMasked;

  @BuiltValueField(wireName: r'providerTxId')
  String? get providerTxId;

  @BuiltValueField(wireName: r'purpose')
  PaymentPurpose get purpose;
  // enum purposeEnum {  NEW_POLICY,  INSTALMENT,  RENEWAL,  ENDORSEMENT,  FLEET_INCLUSION,  };

  @BuiltValueField(wireName: r'reference')
  String get reference;

  @BuiltValueField(wireName: r'status')
  PaymentStatus get status;
  // enum statusEnum {  CREATED,  PENDING,  UNKNOWN,  SUCCEEDED,  FAILED,  EXPIRED,  CANCELLED,  REFUNDED,  };

  @BuiltValueField(wireName: r'succeededAt')
  DateTime? get succeededAt;

  @BuiltValueField(wireName: r'target')
  PaymentTargetRef get target;

  @BuiltValueField(wireName: r'targetLabel')
  String get targetLabel;

  InsurerPaymentDetail._();

  factory InsurerPaymentDetail([void updates(InsurerPaymentDetailBuilder b)]) = _$InsurerPaymentDetail;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerPaymentDetailBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerPaymentDetail> get serializer => _$InsurerPaymentDetailSerializer();
}

class _$InsurerPaymentDetailSerializer implements PrimitiveSerializer<InsurerPaymentDetail> {
  @override
  final Iterable<Type> types = const [InsurerPaymentDetail, _$InsurerPaymentDetail];

  @override
  final String wireName = r'InsurerPaymentDetail';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerPaymentDetail object, {
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
        specifiedType: const FullType(InsurerPaymentBankReference),
      );
    }
    if (object.confirmedBy != null) {
      yield r'confirmedBy';
      yield serializers.serialize(
        object.confirmedBy,
        specifiedType: const FullType(PaymentConfirmedBy),
      );
    }
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'events';
    yield serializers.serialize(
      object.events,
      specifiedType: const FullType(BuiltList, [FullType(InsurerPaymentEvent)]),
    );
    yield r'exceptionIds';
    yield serializers.serialize(
      object.exceptionIds,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'expiresAt';
    yield serializers.serialize(
      object.expiresAt,
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
      specifiedType: const FullType(InsurerPaymentDetailMethodEnum),
    );
    if (object.payerMsisdnMasked != null) {
      yield r'payerMsisdnMasked';
      yield serializers.serialize(
        object.payerMsisdnMasked,
        specifiedType: const FullType(String),
      );
    }
    if (object.providerTxId != null) {
      yield r'providerTxId';
      yield serializers.serialize(
        object.providerTxId,
        specifiedType: const FullType(String),
      );
    }
    yield r'purpose';
    yield serializers.serialize(
      object.purpose,
      specifiedType: const FullType(PaymentPurpose),
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
    if (object.succeededAt != null) {
      yield r'succeededAt';
      yield serializers.serialize(
        object.succeededAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'target';
    yield serializers.serialize(
      object.target,
      specifiedType: const FullType(PaymentTargetRef),
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
    InsurerPaymentDetail object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerPaymentDetailBuilder result,
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
            specifiedType: const FullType(InsurerPaymentBankReference),
          ) as InsurerPaymentBankReference;
          result.bankReference.replace(valueDes);
          break;
        case r'confirmedBy':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentConfirmedBy),
          ) as PaymentConfirmedBy;
          result.confirmedBy = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'events':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(InsurerPaymentEvent)]),
          ) as BuiltList<InsurerPaymentEvent>;
          result.events.replace(valueDes);
          break;
        case r'exceptionIds':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.exceptionIds.replace(valueDes);
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.expiresAt = valueDes;
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
            specifiedType: const FullType(InsurerPaymentDetailMethodEnum),
          ) as InsurerPaymentDetailMethodEnum;
          result.method = valueDes;
          break;
        case r'payerMsisdnMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.payerMsisdnMasked = valueDes;
          break;
        case r'providerTxId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.providerTxId = valueDes;
          break;
        case r'purpose':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentPurpose),
          ) as PaymentPurpose;
          result.purpose = valueDes;
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
        case r'target':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentTargetRef),
          ) as PaymentTargetRef;
          result.target.replace(valueDes);
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
  InsurerPaymentDetail deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerPaymentDetailBuilder();
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

class InsurerPaymentDetailMethodEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MPESA')
  static const InsurerPaymentDetailMethodEnum MPESA = _$insurerPaymentDetailMethodEnum_MPESA;
  @BuiltValueEnumConst(wireName: r'BANK_REFERENCE')
  static const InsurerPaymentDetailMethodEnum BANK_REFERENCE = _$insurerPaymentDetailMethodEnum_BANK_REFERENCE;

  static Serializer<InsurerPaymentDetailMethodEnum> get serializer => _$insurerPaymentDetailMethodEnumSerializer;

  const InsurerPaymentDetailMethodEnum._(String name): super(name);

  static BuiltSet<InsurerPaymentDetailMethodEnum> get values => _$insurerPaymentDetailMethodEnumValues;
  static InsurerPaymentDetailMethodEnum valueOf(String name) => _$insurerPaymentDetailMethodEnumValueOf(name);
}

