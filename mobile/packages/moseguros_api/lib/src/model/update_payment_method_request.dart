//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/json_object.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_payment_method_request.g.dart';

/// UpdatePaymentMethodRequest
///
/// Properties:
/// * [displayName] 
/// * [environment] 
/// * [intentValidityMinutes] - Validade das intenções M-Pesa ainda não enviadas (ignorado em BANK_REFERENCE)
/// * [maxAmount] 
/// * [minAmount] 
/// * [secrets] - Campo → valor do segredo (até 8000 caracteres); escrito só no cofre de segredos
/// * [settings] - Dados não secretos da forma de pagamento, no formato do JSON Schema do método (backend/src/main/resources/schemas/payment-method-mpesa.json ou payment-method-bank_reference.json; devolvido em settingsSchema). Os segredos nunca estão aqui.
/// * [sortOrder] 
/// * [version] 
@BuiltValue()
abstract class UpdatePaymentMethodRequest implements Built<UpdatePaymentMethodRequest, UpdatePaymentMethodRequestBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'environment')
  UpdatePaymentMethodRequestEnvironmentEnum get environment;
  // enum environmentEnum {  SANDBOX,  PRODUCTION,  };

  /// Validade das intenções M-Pesa ainda não enviadas (ignorado em BANK_REFERENCE)
  @BuiltValueField(wireName: r'intentValidityMinutes')
  int get intentValidityMinutes;

  @BuiltValueField(wireName: r'maxAmount')
  Money? get maxAmount;

  @BuiltValueField(wireName: r'minAmount')
  Money? get minAmount;

  /// Campo → valor do segredo (até 8000 caracteres); escrito só no cofre de segredos
  @BuiltValueField(wireName: r'secrets')
  BuiltMap<String, String?>? get secrets;

  /// Dados não secretos da forma de pagamento, no formato do JSON Schema do método (backend/src/main/resources/schemas/payment-method-mpesa.json ou payment-method-bank_reference.json; devolvido em settingsSchema). Os segredos nunca estão aqui.
  @BuiltValueField(wireName: r'settings')
  JsonObject get settings;

  @BuiltValueField(wireName: r'sortOrder')
  int get sortOrder;

  @BuiltValueField(wireName: r'version')
  int? get version;

  UpdatePaymentMethodRequest._();

  factory UpdatePaymentMethodRequest([void updates(UpdatePaymentMethodRequestBuilder b)]) = _$UpdatePaymentMethodRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdatePaymentMethodRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdatePaymentMethodRequest> get serializer => _$UpdatePaymentMethodRequestSerializer();
}

class _$UpdatePaymentMethodRequestSerializer implements PrimitiveSerializer<UpdatePaymentMethodRequest> {
  @override
  final Iterable<Type> types = const [UpdatePaymentMethodRequest, _$UpdatePaymentMethodRequest];

  @override
  final String wireName = r'UpdatePaymentMethodRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdatePaymentMethodRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'environment';
    yield serializers.serialize(
      object.environment,
      specifiedType: const FullType(UpdatePaymentMethodRequestEnvironmentEnum),
    );
    yield r'intentValidityMinutes';
    yield serializers.serialize(
      object.intentValidityMinutes,
      specifiedType: const FullType(int),
    );
    if (object.maxAmount != null) {
      yield r'maxAmount';
      yield serializers.serialize(
        object.maxAmount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.minAmount != null) {
      yield r'minAmount';
      yield serializers.serialize(
        object.minAmount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.secrets != null) {
      yield r'secrets';
      yield serializers.serialize(
        object.secrets,
        specifiedType: const FullType.nullable(BuiltMap, [FullType(String), FullType.nullable(String)]),
      );
    }
    yield r'settings';
    yield serializers.serialize(
      object.settings,
      specifiedType: const FullType(JsonObject),
    );
    yield r'sortOrder';
    yield serializers.serialize(
      object.sortOrder,
      specifiedType: const FullType(int),
    );
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
    UpdatePaymentMethodRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdatePaymentMethodRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.displayName = valueDes;
          break;
        case r'environment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UpdatePaymentMethodRequestEnvironmentEnum),
          ) as UpdatePaymentMethodRequestEnvironmentEnum;
          result.environment = valueDes;
          break;
        case r'intentValidityMinutes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.intentValidityMinutes = valueDes;
          break;
        case r'maxAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.maxAmount.replace(valueDes);
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
            specifiedType: const FullType.nullable(BuiltMap, [FullType(String), FullType.nullable(String)]),
          ) as BuiltMap<String, String?>?;
          if (valueDes == null) continue;
          result.secrets.replace(valueDes);
          break;
        case r'settings':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(JsonObject),
          ) as JsonObject;
          result.settings = valueDes;
          break;
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
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
  UpdatePaymentMethodRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdatePaymentMethodRequestBuilder();
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

class UpdatePaymentMethodRequestEnvironmentEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SANDBOX')
  static const UpdatePaymentMethodRequestEnvironmentEnum SANDBOX = _$updatePaymentMethodRequestEnvironmentEnum_SANDBOX;
  @BuiltValueEnumConst(wireName: r'PRODUCTION')
  static const UpdatePaymentMethodRequestEnvironmentEnum PRODUCTION = _$updatePaymentMethodRequestEnvironmentEnum_PRODUCTION;

  static Serializer<UpdatePaymentMethodRequestEnvironmentEnum> get serializer => _$updatePaymentMethodRequestEnvironmentEnumSerializer;

  const UpdatePaymentMethodRequestEnvironmentEnum._(String name): super(name);

  static BuiltSet<UpdatePaymentMethodRequestEnvironmentEnum> get values => _$updatePaymentMethodRequestEnvironmentEnumValues;
  static UpdatePaymentMethodRequestEnvironmentEnum valueOf(String name) => _$updatePaymentMethodRequestEnvironmentEnumValueOf(name);
}

