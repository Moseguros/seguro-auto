//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_resolution.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/payment_exception_intent.dart';
import 'package:moseguros_api/src/model/payment_exception_type.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_exception_summary.g.dart';

/// PaymentExceptionSummary
///
/// Properties:
/// * [amount] 
/// * [createdAt] 
/// * [detail] - Ex.: INVALID_CHECK_DIGITS, NOT_ISSUED (UNKNOWN_REFERENCE)
/// * [entity] - Entidade (5 dígitos)
/// * [fileName] 
/// * [id] 
/// * [intent] 
/// * [lineNo] 
/// * [paidAt] 
/// * [reference] - Referência (11 dígitos)
/// * [resolution] 
/// * [status] 
/// * [type] 
@BuiltValue()
abstract class PaymentExceptionSummary implements Built<PaymentExceptionSummary, PaymentExceptionSummaryBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money? get amount;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  /// Ex.: INVALID_CHECK_DIGITS, NOT_ISSUED (UNKNOWN_REFERENCE)
  @BuiltValueField(wireName: r'detail')
  String? get detail;

  /// Entidade (5 dígitos)
  @BuiltValueField(wireName: r'entity')
  String? get entity;

  @BuiltValueField(wireName: r'fileName')
  String? get fileName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'intent')
  PaymentExceptionIntent? get intent;

  @BuiltValueField(wireName: r'lineNo')
  int? get lineNo;

  @BuiltValueField(wireName: r'paidAt')
  DateTime? get paidAt;

  /// Referência (11 dígitos)
  @BuiltValueField(wireName: r'reference')
  String? get reference;

  @BuiltValueField(wireName: r'resolution')
  PaymentResolution? get resolution;
  // enum resolutionEnum {  LINKED,  REFUND,  IGNORED,  MARK_SUCCEEDED,  MARK_FAILED,  };

  @BuiltValueField(wireName: r'status')
  PaymentExceptionSummaryStatusEnum get status;
  // enum statusEnum {  OPEN,  RESOLVED,  };

  @BuiltValueField(wireName: r'type')
  PaymentExceptionType get type;
  // enum typeEnum {  AMOUNT_MISMATCH,  EXPIRED_REFERENCE,  ALREADY_PAID,  UNKNOWN_REFERENCE,  CANCELLED_REFERENCE,  TARGET_NOT_PAYABLE,  UNRESOLVED_STATUS,  };

  PaymentExceptionSummary._();

  factory PaymentExceptionSummary([void updates(PaymentExceptionSummaryBuilder b)]) = _$PaymentExceptionSummary;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentExceptionSummaryBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentExceptionSummary> get serializer => _$PaymentExceptionSummarySerializer();
}

class _$PaymentExceptionSummarySerializer implements PrimitiveSerializer<PaymentExceptionSummary> {
  @override
  final Iterable<Type> types = const [PaymentExceptionSummary, _$PaymentExceptionSummary];

  @override
  final String wireName = r'PaymentExceptionSummary';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentExceptionSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.amount != null) {
      yield r'amount';
      yield serializers.serialize(
        object.amount,
        specifiedType: const FullType(Money),
      );
    }
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.detail != null) {
      yield r'detail';
      yield serializers.serialize(
        object.detail,
        specifiedType: const FullType(String),
      );
    }
    if (object.entity != null) {
      yield r'entity';
      yield serializers.serialize(
        object.entity,
        specifiedType: const FullType(String),
      );
    }
    if (object.fileName != null) {
      yield r'fileName';
      yield serializers.serialize(
        object.fileName,
        specifiedType: const FullType(String),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.intent != null) {
      yield r'intent';
      yield serializers.serialize(
        object.intent,
        specifiedType: const FullType(PaymentExceptionIntent),
      );
    }
    if (object.lineNo != null) {
      yield r'lineNo';
      yield serializers.serialize(
        object.lineNo,
        specifiedType: const FullType(int),
      );
    }
    if (object.paidAt != null) {
      yield r'paidAt';
      yield serializers.serialize(
        object.paidAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.reference != null) {
      yield r'reference';
      yield serializers.serialize(
        object.reference,
        specifiedType: const FullType(String),
      );
    }
    if (object.resolution != null) {
      yield r'resolution';
      yield serializers.serialize(
        object.resolution,
        specifiedType: const FullType(PaymentResolution),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PaymentExceptionSummaryStatusEnum),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PaymentExceptionType),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentExceptionSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentExceptionSummaryBuilder result,
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
        case r'detail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.detail = valueDes;
          break;
        case r'entity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.entity = valueDes;
          break;
        case r'fileName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'intent':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentExceptionIntent),
          ) as PaymentExceptionIntent;
          result.intent.replace(valueDes);
          break;
        case r'lineNo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.lineNo = valueDes;
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
        case r'resolution':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentResolution),
          ) as PaymentResolution;
          result.resolution = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentExceptionSummaryStatusEnum),
          ) as PaymentExceptionSummaryStatusEnum;
          result.status = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentExceptionType),
          ) as PaymentExceptionType;
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
  PaymentExceptionSummary deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentExceptionSummaryBuilder();
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

class PaymentExceptionSummaryStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN')
  static const PaymentExceptionSummaryStatusEnum OPEN = _$paymentExceptionSummaryStatusEnum_OPEN;
  @BuiltValueEnumConst(wireName: r'RESOLVED')
  static const PaymentExceptionSummaryStatusEnum RESOLVED = _$paymentExceptionSummaryStatusEnum_RESOLVED;

  static Serializer<PaymentExceptionSummaryStatusEnum> get serializer => _$paymentExceptionSummaryStatusEnumSerializer;

  const PaymentExceptionSummaryStatusEnum._(String name): super(name);

  static BuiltSet<PaymentExceptionSummaryStatusEnum> get values => _$paymentExceptionSummaryStatusEnumValues;
  static PaymentExceptionSummaryStatusEnum valueOf(String name) => _$paymentExceptionSummaryStatusEnumValueOf(name);
}

