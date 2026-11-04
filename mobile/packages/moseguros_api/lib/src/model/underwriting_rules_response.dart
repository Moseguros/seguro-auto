//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'underwriting_rules_response.g.dart';

/// UnderwritingRulesResponse
///
/// Properties:
/// * [isDefault] - true: a versão não tem regras próprias e valem as por defeito
/// * [rules] - Regras de aceitação no formato underwriting-v1 (backend/src/main/resources/schemas/underwriting-v1.json): schemaVersion = 1 e autoAccept { enabled, simplifiedKycMaxPremium, liability { requireAllDeclarationsNegative }, ownDamage { maxVehicleValue, requireAllDeclarationsNegative } }. Dinheiro em centavos.
/// * [version] - version da versão do produto
@BuiltValue()
abstract class UnderwritingRulesResponse implements Built<UnderwritingRulesResponse, UnderwritingRulesResponseBuilder> {
  /// true: a versão não tem regras próprias e valem as por defeito
  @BuiltValueField(wireName: r'isDefault')
  bool get isDefault;

  /// Regras de aceitação no formato underwriting-v1 (backend/src/main/resources/schemas/underwriting-v1.json): schemaVersion = 1 e autoAccept { enabled, simplifiedKycMaxPremium, liability { requireAllDeclarationsNegative }, ownDamage { maxVehicleValue, requireAllDeclarationsNegative } }. Dinheiro em centavos.
  @BuiltValueField(wireName: r'rules')
  JsonObject get rules;

  /// version da versão do produto
  @BuiltValueField(wireName: r'version')
  int get version;

  UnderwritingRulesResponse._();

  factory UnderwritingRulesResponse([void updates(UnderwritingRulesResponseBuilder b)]) = _$UnderwritingRulesResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UnderwritingRulesResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UnderwritingRulesResponse> get serializer => _$UnderwritingRulesResponseSerializer();
}

class _$UnderwritingRulesResponseSerializer implements PrimitiveSerializer<UnderwritingRulesResponse> {
  @override
  final Iterable<Type> types = const [UnderwritingRulesResponse, _$UnderwritingRulesResponse];

  @override
  final String wireName = r'UnderwritingRulesResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UnderwritingRulesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'isDefault';
    yield serializers.serialize(
      object.isDefault,
      specifiedType: const FullType(bool),
    );
    yield r'rules';
    yield serializers.serialize(
      object.rules,
      specifiedType: const FullType(JsonObject),
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
    UnderwritingRulesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UnderwritingRulesResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'isDefault':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.isDefault = valueDes;
          break;
        case r'rules':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(JsonObject),
          ) as JsonObject;
          result.rules = valueDes;
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
  UnderwritingRulesResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UnderwritingRulesResponseBuilder();
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

