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

part 'payment_exception_detail.g.dart';

/// PaymentExceptionDetail
///
/// Properties:
/// * [allowedResolutions] - Resoluções permitidas para o tipo; vazio depois de resolvida
/// * [amount] 
/// * [bankTransactionId] - Identificador da transacção no banco, se o ficheiro o trouxer
/// * [createdAt] 
/// * [detail] 
/// * [entity] 
/// * [fileName] 
/// * [id] 
/// * [intent] 
/// * [lineNo] 
/// * [linkedIntent] 
/// * [note] 
/// * [paidAt] 
/// * [reference] 
/// * [resolution] 
/// * [resolvedAt] 
/// * [resolvedBy] - Id do utilizador que resolveu
/// * [status] 
/// * [type] 
/// * [version] - Versão; enviada na resolução (diferente → 409 CONCURRENT_MODIFICATION)
@BuiltValue()
abstract class PaymentExceptionDetail implements Built<PaymentExceptionDetail, PaymentExceptionDetailBuilder> {
  /// Resoluções permitidas para o tipo; vazio depois de resolvida
  @BuiltValueField(wireName: r'allowedResolutions')
  BuiltList<PaymentResolution> get allowedResolutions;

  @BuiltValueField(wireName: r'amount')
  Money? get amount;

  /// Identificador da transacção no banco, se o ficheiro o trouxer
  @BuiltValueField(wireName: r'bankTransactionId')
  String? get bankTransactionId;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'detail')
  String? get detail;

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

  @BuiltValueField(wireName: r'linkedIntent')
  PaymentExceptionIntent? get linkedIntent;

  @BuiltValueField(wireName: r'note')
  String? get note;

  @BuiltValueField(wireName: r'paidAt')
  DateTime? get paidAt;

  @BuiltValueField(wireName: r'reference')
  String? get reference;

  @BuiltValueField(wireName: r'resolution')
  PaymentResolution? get resolution;
  // enum resolutionEnum {  LINKED,  REFUND,  IGNORED,  MARK_SUCCEEDED,  MARK_FAILED,  };

  @BuiltValueField(wireName: r'resolvedAt')
  DateTime? get resolvedAt;

  /// Id do utilizador que resolveu
  @BuiltValueField(wireName: r'resolvedBy')
  String? get resolvedBy;

  @BuiltValueField(wireName: r'status')
  PaymentExceptionDetailStatusEnum get status;
  // enum statusEnum {  OPEN,  RESOLVED,  };

  @BuiltValueField(wireName: r'type')
  PaymentExceptionType get type;
  // enum typeEnum {  AMOUNT_MISMATCH,  EXPIRED_REFERENCE,  ALREADY_PAID,  UNKNOWN_REFERENCE,  CANCELLED_REFERENCE,  TARGET_NOT_PAYABLE,  UNRESOLVED_STATUS,  };

  /// Versão; enviada na resolução (diferente → 409 CONCURRENT_MODIFICATION)
  @BuiltValueField(wireName: r'version')
  int get version;

  PaymentExceptionDetail._();

  factory PaymentExceptionDetail([void updates(PaymentExceptionDetailBuilder b)]) = _$PaymentExceptionDetail;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentExceptionDetailBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentExceptionDetail> get serializer => _$PaymentExceptionDetailSerializer();
}

class _$PaymentExceptionDetailSerializer implements PrimitiveSerializer<PaymentExceptionDetail> {
  @override
  final Iterable<Type> types = const [PaymentExceptionDetail, _$PaymentExceptionDetail];

  @override
  final String wireName = r'PaymentExceptionDetail';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentExceptionDetail object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'allowedResolutions';
    yield serializers.serialize(
      object.allowedResolutions,
      specifiedType: const FullType(BuiltList, [FullType(PaymentResolution)]),
    );
    if (object.amount != null) {
      yield r'amount';
      yield serializers.serialize(
        object.amount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.bankTransactionId != null) {
      yield r'bankTransactionId';
      yield serializers.serialize(
        object.bankTransactionId,
        specifiedType: const FullType(String),
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
    if (object.linkedIntent != null) {
      yield r'linkedIntent';
      yield serializers.serialize(
        object.linkedIntent,
        specifiedType: const FullType(PaymentExceptionIntent),
      );
    }
    if (object.note != null) {
      yield r'note';
      yield serializers.serialize(
        object.note,
        specifiedType: const FullType(String),
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
    if (object.resolvedAt != null) {
      yield r'resolvedAt';
      yield serializers.serialize(
        object.resolvedAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.resolvedBy != null) {
      yield r'resolvedBy';
      yield serializers.serialize(
        object.resolvedBy,
        specifiedType: const FullType(String),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PaymentExceptionDetailStatusEnum),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PaymentExceptionType),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentExceptionDetail object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentExceptionDetailBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'allowedResolutions':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PaymentResolution)]),
          ) as BuiltList<PaymentResolution>;
          result.allowedResolutions.replace(valueDes);
          break;
        case r'amount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.amount.replace(valueDes);
          break;
        case r'bankTransactionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.bankTransactionId = valueDes;
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
        case r'linkedIntent':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentExceptionIntent),
          ) as PaymentExceptionIntent;
          result.linkedIntent.replace(valueDes);
          break;
        case r'note':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.note = valueDes;
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
        case r'resolvedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.resolvedAt = valueDes;
          break;
        case r'resolvedBy':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.resolvedBy = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentExceptionDetailStatusEnum),
          ) as PaymentExceptionDetailStatusEnum;
          result.status = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentExceptionType),
          ) as PaymentExceptionType;
          result.type = valueDes;
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentExceptionDetail deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentExceptionDetailBuilder();
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

class PaymentExceptionDetailStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OPEN')
  static const PaymentExceptionDetailStatusEnum OPEN = _$paymentExceptionDetailStatusEnum_OPEN;
  @BuiltValueEnumConst(wireName: r'RESOLVED')
  static const PaymentExceptionDetailStatusEnum RESOLVED = _$paymentExceptionDetailStatusEnum_RESOLVED;

  static Serializer<PaymentExceptionDetailStatusEnum> get serializer => _$paymentExceptionDetailStatusEnumSerializer;

  const PaymentExceptionDetailStatusEnum._(String name): super(name);

  static BuiltSet<PaymentExceptionDetailStatusEnum> get values => _$paymentExceptionDetailStatusEnumValues;
  static PaymentExceptionDetailStatusEnum valueOf(String name) => _$paymentExceptionDetailStatusEnumValueOf(name);
}

