//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_file_source_status.g.dart';

/// PaymentFileSourceStatus
///
/// Properties:
/// * [configured] - false sem forma de pagamento BANK_REFERENCE (os outros campos não vêm)
/// * [enabled] 
/// * [entity] 
/// * [fileFormat] 
/// * [lastPollAt] 
/// * [lastPollError] - PollErrorCode (i18n pollErrorCode.*)
/// * [lastPollOutcome] 
/// * [lastSuccessAt] 
/// * [staleAfterMinutes] - Minutos sem leitura com sucesso a partir dos quais a fonte está parada
/// * [staleSince] 
@BuiltValue()
abstract class PaymentFileSourceStatus implements Built<PaymentFileSourceStatus, PaymentFileSourceStatusBuilder> {
  /// false sem forma de pagamento BANK_REFERENCE (os outros campos não vêm)
  @BuiltValueField(wireName: r'configured')
  bool get configured;

  @BuiltValueField(wireName: r'enabled')
  bool? get enabled;

  @BuiltValueField(wireName: r'entity')
  String? get entity;

  @BuiltValueField(wireName: r'fileFormat')
  PaymentFileSourceStatusFileFormatEnum? get fileFormat;
  // enum fileFormatEnum {  MEPS,  BMEPS,  };

  @BuiltValueField(wireName: r'lastPollAt')
  DateTime? get lastPollAt;

  /// PollErrorCode (i18n pollErrorCode.*)
  @BuiltValueField(wireName: r'lastPollError')
  String? get lastPollError;

  @BuiltValueField(wireName: r'lastPollOutcome')
  PaymentFileSourceStatusLastPollOutcomeEnum? get lastPollOutcome;
  // enum lastPollOutcomeEnum {  OK,  ERROR,  };

  @BuiltValueField(wireName: r'lastSuccessAt')
  DateTime? get lastSuccessAt;

  /// Minutos sem leitura com sucesso a partir dos quais a fonte está parada
  @BuiltValueField(wireName: r'staleAfterMinutes')
  int? get staleAfterMinutes;

  @BuiltValueField(wireName: r'staleSince')
  DateTime? get staleSince;

  PaymentFileSourceStatus._();

  factory PaymentFileSourceStatus([void updates(PaymentFileSourceStatusBuilder b)]) = _$PaymentFileSourceStatus;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentFileSourceStatusBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentFileSourceStatus> get serializer => _$PaymentFileSourceStatusSerializer();
}

class _$PaymentFileSourceStatusSerializer implements PrimitiveSerializer<PaymentFileSourceStatus> {
  @override
  final Iterable<Type> types = const [PaymentFileSourceStatus, _$PaymentFileSourceStatus];

  @override
  final String wireName = r'PaymentFileSourceStatus';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentFileSourceStatus object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'configured';
    yield serializers.serialize(
      object.configured,
      specifiedType: const FullType(bool),
    );
    if (object.enabled != null) {
      yield r'enabled';
      yield serializers.serialize(
        object.enabled,
        specifiedType: const FullType(bool),
      );
    }
    if (object.entity != null) {
      yield r'entity';
      yield serializers.serialize(
        object.entity,
        specifiedType: const FullType(String),
      );
    }
    if (object.fileFormat != null) {
      yield r'fileFormat';
      yield serializers.serialize(
        object.fileFormat,
        specifiedType: const FullType(PaymentFileSourceStatusFileFormatEnum),
      );
    }
    if (object.lastPollAt != null) {
      yield r'lastPollAt';
      yield serializers.serialize(
        object.lastPollAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.lastPollError != null) {
      yield r'lastPollError';
      yield serializers.serialize(
        object.lastPollError,
        specifiedType: const FullType(String),
      );
    }
    if (object.lastPollOutcome != null) {
      yield r'lastPollOutcome';
      yield serializers.serialize(
        object.lastPollOutcome,
        specifiedType: const FullType(PaymentFileSourceStatusLastPollOutcomeEnum),
      );
    }
    if (object.lastSuccessAt != null) {
      yield r'lastSuccessAt';
      yield serializers.serialize(
        object.lastSuccessAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.staleAfterMinutes != null) {
      yield r'staleAfterMinutes';
      yield serializers.serialize(
        object.staleAfterMinutes,
        specifiedType: const FullType(int),
      );
    }
    if (object.staleSince != null) {
      yield r'staleSince';
      yield serializers.serialize(
        object.staleSince,
        specifiedType: const FullType(DateTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentFileSourceStatus object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentFileSourceStatusBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'configured':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.configured = valueDes;
          break;
        case r'enabled':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.enabled = valueDes;
          break;
        case r'entity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.entity = valueDes;
          break;
        case r'fileFormat':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentFileSourceStatusFileFormatEnum),
          ) as PaymentFileSourceStatusFileFormatEnum;
          result.fileFormat = valueDes;
          break;
        case r'lastPollAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.lastPollAt = valueDes;
          break;
        case r'lastPollError':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.lastPollError = valueDes;
          break;
        case r'lastPollOutcome':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentFileSourceStatusLastPollOutcomeEnum),
          ) as PaymentFileSourceStatusLastPollOutcomeEnum;
          result.lastPollOutcome = valueDes;
          break;
        case r'lastSuccessAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.lastSuccessAt = valueDes;
          break;
        case r'staleAfterMinutes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.staleAfterMinutes = valueDes;
          break;
        case r'staleSince':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.staleSince = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentFileSourceStatus deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentFileSourceStatusBuilder();
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

class PaymentFileSourceStatusFileFormatEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MEPS')
  static const PaymentFileSourceStatusFileFormatEnum MEPS = _$paymentFileSourceStatusFileFormatEnum_MEPS;
  @BuiltValueEnumConst(wireName: r'BMEPS')
  static const PaymentFileSourceStatusFileFormatEnum BMEPS = _$paymentFileSourceStatusFileFormatEnum_BMEPS;

  static Serializer<PaymentFileSourceStatusFileFormatEnum> get serializer => _$paymentFileSourceStatusFileFormatEnumSerializer;

  const PaymentFileSourceStatusFileFormatEnum._(String name): super(name);

  static BuiltSet<PaymentFileSourceStatusFileFormatEnum> get values => _$paymentFileSourceStatusFileFormatEnumValues;
  static PaymentFileSourceStatusFileFormatEnum valueOf(String name) => _$paymentFileSourceStatusFileFormatEnumValueOf(name);
}

class PaymentFileSourceStatusLastPollOutcomeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OK')
  static const PaymentFileSourceStatusLastPollOutcomeEnum OK = _$paymentFileSourceStatusLastPollOutcomeEnum_OK;
  @BuiltValueEnumConst(wireName: r'ERROR')
  static const PaymentFileSourceStatusLastPollOutcomeEnum ERROR = _$paymentFileSourceStatusLastPollOutcomeEnum_ERROR;

  static Serializer<PaymentFileSourceStatusLastPollOutcomeEnum> get serializer => _$paymentFileSourceStatusLastPollOutcomeEnumSerializer;

  const PaymentFileSourceStatusLastPollOutcomeEnum._(String name): super(name);

  static BuiltSet<PaymentFileSourceStatusLastPollOutcomeEnum> get values => _$paymentFileSourceStatusLastPollOutcomeEnumValues;
  static PaymentFileSourceStatusLastPollOutcomeEnum valueOf(String name) => _$paymentFileSourceStatusLastPollOutcomeEnumValueOf(name);
}

