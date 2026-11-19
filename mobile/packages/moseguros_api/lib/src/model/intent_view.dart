//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_next_action.dart';
import 'package:moseguros_api/src/model/payment_purpose.dart';
import 'package:moseguros_api/src/model/payment_insurer_ref.dart';
import 'package:moseguros_api/src/model/failure_code.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/payment_target_ref.dart';
import 'package:moseguros_api/src/model/payment_status.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'intent_view.g.dart';

/// IntentView
///
/// Properties:
/// * [amount] 
/// * [createdAt] 
/// * [expiresAt] 
/// * [failureCode] 
/// * [id] 
/// * [insurer] 
/// * [method] 
/// * [nextAction] 
/// * [payerMsisdnMasked] 
/// * [payerUserId] 
/// * [purpose] 
/// * [reference] 
/// * [status] 
/// * [succeededAt] 
/// * [target] 
@BuiltValue()
abstract class IntentView implements Built<IntentView, IntentViewBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money get amount;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  @BuiltValueField(wireName: r'failureCode')
  FailureCode? get failureCode;
  // enum failureCodeEnum {  CUSTOMER_CANCELLED,  INSUFFICIENT_BALANCE,  INVALID_MSISDN,  PROVIDER_REJECTED,  PROVIDER_CONFIG_ERROR,  PROVIDER_UNAVAILABLE,  TIMEOUT_EXPIRED,  EXPIRED,  SUPERSEDED,  MANUAL,  };

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'insurer')
  PaymentInsurerRef get insurer;

  @BuiltValueField(wireName: r'method')
  IntentViewMethodEnum get method;
  // enum methodEnum {  MPESA,  BANK_REFERENCE,  };

  @BuiltValueField(wireName: r'nextAction')
  PaymentNextAction get nextAction;

  @BuiltValueField(wireName: r'payerMsisdnMasked')
  String? get payerMsisdnMasked;

  @BuiltValueField(wireName: r'payerUserId')
  String get payerUserId;

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

  IntentView._();

  factory IntentView([void updates(IntentViewBuilder b)]) = _$IntentView;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(IntentViewBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<IntentView> get serializer => _$IntentViewSerializer();
}

class _$IntentViewSerializer implements PrimitiveSerializer<IntentView> {
  @override
  final Iterable<Type> types = const [IntentView, _$IntentView];

  @override
  final String wireName = r'IntentView';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    IntentView object, {
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
    yield r'insurer';
    yield serializers.serialize(
      object.insurer,
      specifiedType: const FullType(PaymentInsurerRef),
    );
    yield r'method';
    yield serializers.serialize(
      object.method,
      specifiedType: const FullType(IntentViewMethodEnum),
    );
    yield r'nextAction';
    yield serializers.serialize(
      object.nextAction,
      specifiedType: const FullType(PaymentNextAction),
    );
    if (object.payerMsisdnMasked != null) {
      yield r'payerMsisdnMasked';
      yield serializers.serialize(
        object.payerMsisdnMasked,
        specifiedType: const FullType(String),
      );
    }
    yield r'payerUserId';
    yield serializers.serialize(
      object.payerUserId,
      specifiedType: const FullType(String),
    );
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
  }

  @override
  Object serialize(
    Serializers serializers,
    IntentView object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required IntentViewBuilder result,
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
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentInsurerRef),
          ) as PaymentInsurerRef;
          result.insurer.replace(valueDes);
          break;
        case r'method':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(IntentViewMethodEnum),
          ) as IntentViewMethodEnum;
          result.method = valueDes;
          break;
        case r'nextAction':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentNextAction),
          ) as PaymentNextAction;
          result.nextAction.replace(valueDes);
          break;
        case r'payerMsisdnMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.payerMsisdnMasked = valueDes;
          break;
        case r'payerUserId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.payerUserId = valueDes;
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  IntentView deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = IntentViewBuilder();
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

class IntentViewMethodEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MPESA')
  static const IntentViewMethodEnum MPESA = _$intentViewMethodEnum_MPESA;
  @BuiltValueEnumConst(wireName: r'BANK_REFERENCE')
  static const IntentViewMethodEnum BANK_REFERENCE = _$intentViewMethodEnum_BANK_REFERENCE;

  static Serializer<IntentViewMethodEnum> get serializer => _$intentViewMethodEnumSerializer;

  const IntentViewMethodEnum._(String name): super(name);

  static BuiltSet<IntentViewMethodEnum> get values => _$intentViewMethodEnumValues;
  static IntentViewMethodEnum valueOf(String name) => _$intentViewMethodEnumValueOf(name);
}

