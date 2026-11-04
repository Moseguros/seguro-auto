//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_underwriting_rules_request.g.dart';

/// UpdateUnderwritingRulesRequest
///
/// Properties:
/// * [rules] - Regras de aceitação no formato underwriting-v1 (backend/src/main/resources/schemas/underwriting-v1.json): schemaVersion = 1 e autoAccept { enabled, simplifiedKycMaxPremium, liability { requireAllDeclarationsNegative }, ownDamage { maxVehicleValue, requireAllDeclarationsNegative } }. Dinheiro em centavos.
/// * [version] - version da versão do produto
@BuiltValue()
abstract class UpdateUnderwritingRulesRequest implements Built<UpdateUnderwritingRulesRequest, UpdateUnderwritingRulesRequestBuilder> {
  /// Regras de aceitação no formato underwriting-v1 (backend/src/main/resources/schemas/underwriting-v1.json): schemaVersion = 1 e autoAccept { enabled, simplifiedKycMaxPremium, liability { requireAllDeclarationsNegative }, ownDamage { maxVehicleValue, requireAllDeclarationsNegative } }. Dinheiro em centavos.
  @BuiltValueField(wireName: r'rules')
  JsonObject get rules;

  /// version da versão do produto
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateUnderwritingRulesRequest._();

  factory UpdateUnderwritingRulesRequest([void updates(UpdateUnderwritingRulesRequestBuilder b)]) = _$UpdateUnderwritingRulesRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateUnderwritingRulesRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateUnderwritingRulesRequest> get serializer => _$UpdateUnderwritingRulesRequestSerializer();
}

class _$UpdateUnderwritingRulesRequestSerializer implements PrimitiveSerializer<UpdateUnderwritingRulesRequest> {
  @override
  final Iterable<Type> types = const [UpdateUnderwritingRulesRequest, _$UpdateUnderwritingRulesRequest];

  @override
  final String wireName = r'UpdateUnderwritingRulesRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateUnderwritingRulesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    UpdateUnderwritingRulesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateUnderwritingRulesRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
  UpdateUnderwritingRulesRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateUnderwritingRulesRequestBuilder();
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

