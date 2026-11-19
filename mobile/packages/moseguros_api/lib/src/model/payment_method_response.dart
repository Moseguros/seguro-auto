//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/last_error_response.dart';
import 'package:moseguros_api/src/model/last_test_response.dart';
import 'package:built_value/json_object.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:moseguros_api/src/model/secret_status_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_method_response.g.dart';

/// PaymentMethodResponse
///
/// Properties:
/// * [configured] 
/// * [displayName] 
/// * [enabled] 
/// * [environment] 
/// * [intentValidityMinutes] 
/// * [lastError] 
/// * [lastTest] 
/// * [maxAmount] 
/// * [method] 
/// * [minAmount] 
/// * [secrets] - Campo do segredo (apiKey, publicKey, securityCredential, sftp.password, sftp.privateKey, sftp.passphrase) → estado
/// * [settings] - Dados não secretos da forma de pagamento, no formato do JSON Schema do método (backend/src/main/resources/schemas/payment-method-mpesa.json ou payment-method-bank_reference.json; devolvido em settingsSchema). Os segredos nunca estão aqui.
/// * [settingsSchema] - JSON Schema (draft 2020-12) dos settings do método; a web gera o formulário a partir dele.
/// * [sortOrder] 
/// * [updatedAt] 
/// * [version] 
@BuiltValue()
abstract class PaymentMethodResponse implements Built<PaymentMethodResponse, PaymentMethodResponseBuilder> {
  @BuiltValueField(wireName: r'configured')
  bool get configured;

  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'enabled')
  bool get enabled;

  @BuiltValueField(wireName: r'environment')
  PaymentMethodResponseEnvironmentEnum get environment;
  // enum environmentEnum {  SANDBOX,  PRODUCTION,  };

  @BuiltValueField(wireName: r'intentValidityMinutes')
  int get intentValidityMinutes;

  @BuiltValueField(wireName: r'lastError')
  LastErrorResponse? get lastError;

  @BuiltValueField(wireName: r'lastTest')
  LastTestResponse? get lastTest;

  @BuiltValueField(wireName: r'maxAmount')
  Money? get maxAmount;

  @BuiltValueField(wireName: r'method')
  PaymentMethodResponseMethodEnum get method;
  // enum methodEnum {  MPESA,  BANK_REFERENCE,  };

  @BuiltValueField(wireName: r'minAmount')
  Money? get minAmount;

  /// Campo do segredo (apiKey, publicKey, securityCredential, sftp.password, sftp.privateKey, sftp.passphrase) → estado
  @BuiltValueField(wireName: r'secrets')
  BuiltMap<String, SecretStatusResponse> get secrets;

  /// Dados não secretos da forma de pagamento, no formato do JSON Schema do método (backend/src/main/resources/schemas/payment-method-mpesa.json ou payment-method-bank_reference.json; devolvido em settingsSchema). Os segredos nunca estão aqui.
  @BuiltValueField(wireName: r'settings')
  JsonObject get settings;

  /// JSON Schema (draft 2020-12) dos settings do método; a web gera o formulário a partir dele.
  @BuiltValueField(wireName: r'settingsSchema')
  JsonObject get settingsSchema;

  @BuiltValueField(wireName: r'sortOrder')
  int get sortOrder;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime? get updatedAt;

  @BuiltValueField(wireName: r'version')
  int? get version;

  PaymentMethodResponse._();

  factory PaymentMethodResponse([void updates(PaymentMethodResponseBuilder b)]) = _$PaymentMethodResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentMethodResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentMethodResponse> get serializer => _$PaymentMethodResponseSerializer();
}

class _$PaymentMethodResponseSerializer implements PrimitiveSerializer<PaymentMethodResponse> {
  @override
  final Iterable<Type> types = const [PaymentMethodResponse, _$PaymentMethodResponse];

  @override
  final String wireName = r'PaymentMethodResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentMethodResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'configured';
    yield serializers.serialize(
      object.configured,
      specifiedType: const FullType(bool),
    );
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'enabled';
    yield serializers.serialize(
      object.enabled,
      specifiedType: const FullType(bool),
    );
    yield r'environment';
    yield serializers.serialize(
      object.environment,
      specifiedType: const FullType(PaymentMethodResponseEnvironmentEnum),
    );
    yield r'intentValidityMinutes';
    yield serializers.serialize(
      object.intentValidityMinutes,
      specifiedType: const FullType(int),
    );
    if (object.lastError != null) {
      yield r'lastError';
      yield serializers.serialize(
        object.lastError,
        specifiedType: const FullType.nullable(LastErrorResponse),
      );
    }
    if (object.lastTest != null) {
      yield r'lastTest';
      yield serializers.serialize(
        object.lastTest,
        specifiedType: const FullType.nullable(LastTestResponse),
      );
    }
    if (object.maxAmount != null) {
      yield r'maxAmount';
      yield serializers.serialize(
        object.maxAmount,
        specifiedType: const FullType(Money),
      );
    }
    yield r'method';
    yield serializers.serialize(
      object.method,
      specifiedType: const FullType(PaymentMethodResponseMethodEnum),
    );
    if (object.minAmount != null) {
      yield r'minAmount';
      yield serializers.serialize(
        object.minAmount,
        specifiedType: const FullType(Money),
      );
    }
    yield r'secrets';
    yield serializers.serialize(
      object.secrets,
      specifiedType: const FullType(BuiltMap, [FullType(String), FullType(SecretStatusResponse)]),
    );
    yield r'settings';
    yield serializers.serialize(
      object.settings,
      specifiedType: const FullType(JsonObject),
    );
    yield r'settingsSchema';
    yield serializers.serialize(
      object.settingsSchema,
      specifiedType: const FullType(JsonObject),
    );
    yield r'sortOrder';
    yield serializers.serialize(
      object.sortOrder,
      specifiedType: const FullType(int),
    );
    if (object.updatedAt != null) {
      yield r'updatedAt';
      yield serializers.serialize(
        object.updatedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.version != null) {
      yield r'version';
      yield serializers.serialize(
        object.version,
        specifiedType: const FullType.nullable(int),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentMethodResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentMethodResponseBuilder result,
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
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.displayName = valueDes;
          break;
        case r'enabled':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.enabled = valueDes;
          break;
        case r'environment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentMethodResponseEnvironmentEnum),
          ) as PaymentMethodResponseEnvironmentEnum;
          result.environment = valueDes;
          break;
        case r'intentValidityMinutes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.intentValidityMinutes = valueDes;
          break;
        case r'lastError':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(LastErrorResponse),
          ) as LastErrorResponse?;
          if (valueDes == null) continue;
          result.lastError.replace(valueDes);
          break;
        case r'lastTest':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(LastTestResponse),
          ) as LastTestResponse?;
          if (valueDes == null) continue;
          result.lastTest.replace(valueDes);
          break;
        case r'maxAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.maxAmount.replace(valueDes);
          break;
        case r'method':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentMethodResponseMethodEnum),
          ) as PaymentMethodResponseMethodEnum;
          result.method = valueDes;
          break;
        case r'minAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.minAmount.replace(valueDes);
          break;
        case r'secrets':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType(SecretStatusResponse)]),
          ) as BuiltMap<String, SecretStatusResponse>;
          result.secrets.replace(valueDes);
          break;
        case r'settings':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(JsonObject),
          ) as JsonObject;
          result.settings = valueDes;
          break;
        case r'settingsSchema':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(JsonObject),
          ) as JsonObject;
          result.settingsSchema = valueDes;
          break;
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.updatedAt = valueDes;
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
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
  PaymentMethodResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentMethodResponseBuilder();
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

class PaymentMethodResponseEnvironmentEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SANDBOX')
  static const PaymentMethodResponseEnvironmentEnum SANDBOX = _$paymentMethodResponseEnvironmentEnum_SANDBOX;
  @BuiltValueEnumConst(wireName: r'PRODUCTION')
  static const PaymentMethodResponseEnvironmentEnum PRODUCTION = _$paymentMethodResponseEnvironmentEnum_PRODUCTION;

  static Serializer<PaymentMethodResponseEnvironmentEnum> get serializer => _$paymentMethodResponseEnvironmentEnumSerializer;

  const PaymentMethodResponseEnvironmentEnum._(String name): super(name);

  static BuiltSet<PaymentMethodResponseEnvironmentEnum> get values => _$paymentMethodResponseEnvironmentEnumValues;
  static PaymentMethodResponseEnvironmentEnum valueOf(String name) => _$paymentMethodResponseEnvironmentEnumValueOf(name);
}

class PaymentMethodResponseMethodEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MPESA')
  static const PaymentMethodResponseMethodEnum MPESA = _$paymentMethodResponseMethodEnum_MPESA;
  @BuiltValueEnumConst(wireName: r'BANK_REFERENCE')
  static const PaymentMethodResponseMethodEnum BANK_REFERENCE = _$paymentMethodResponseMethodEnum_BANK_REFERENCE;

  static Serializer<PaymentMethodResponseMethodEnum> get serializer => _$paymentMethodResponseMethodEnumSerializer;

  const PaymentMethodResponseMethodEnum._(String name): super(name);

  static BuiltSet<PaymentMethodResponseMethodEnum> get values => _$paymentMethodResponseMethodEnumValues;
  static PaymentMethodResponseMethodEnum valueOf(String name) => _$paymentMethodResponseMethodEnumValueOf(name);
}

