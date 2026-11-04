//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/underwriting_outcome.dart';
import 'package:moseguros_api/src/model/underwriting_failure.dart';
import 'package:moseguros_api/src/model/underwriting_inputs.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'underwriting_result.g.dart';

/// Última avaliação das regras de aceitação (underwriting_result)
///
/// Properties:
/// * [evaluatedAt] 
/// * [failures] 
/// * [inputs] 
/// * [outcome] 
/// * [rulesSchemaVersion] 
/// * [rulesSource] 
@BuiltValue()
abstract class UnderwritingResult implements Built<UnderwritingResult, UnderwritingResultBuilder> {
  @BuiltValueField(wireName: r'evaluatedAt')
  DateTime get evaluatedAt;

  @BuiltValueField(wireName: r'failures')
  BuiltList<UnderwritingFailure> get failures;

  @BuiltValueField(wireName: r'inputs')
  UnderwritingInputs get inputs;

  @BuiltValueField(wireName: r'outcome')
  UnderwritingOutcome get outcome;
  // enum outcomeEnum {  AUTOMATIC,  MANUAL_REVIEW,  };

  @BuiltValueField(wireName: r'rulesSchemaVersion')
  int get rulesSchemaVersion;

  @BuiltValueField(wireName: r'rulesSource')
  UnderwritingResultRulesSourceEnum get rulesSource;
  // enum rulesSourceEnum {  PRODUCT,  DEFAULT,  };

  UnderwritingResult._();

  factory UnderwritingResult([void updates(UnderwritingResultBuilder b)]) = _$UnderwritingResult;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UnderwritingResultBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UnderwritingResult> get serializer => _$UnderwritingResultSerializer();
}

class _$UnderwritingResultSerializer implements PrimitiveSerializer<UnderwritingResult> {
  @override
  final Iterable<Type> types = const [UnderwritingResult, _$UnderwritingResult];

  @override
  final String wireName = r'UnderwritingResult';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UnderwritingResult object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'evaluatedAt';
    yield serializers.serialize(
      object.evaluatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'failures';
    yield serializers.serialize(
      object.failures,
      specifiedType: const FullType(BuiltList, [FullType(UnderwritingFailure)]),
    );
    yield r'inputs';
    yield serializers.serialize(
      object.inputs,
      specifiedType: const FullType(UnderwritingInputs),
    );
    yield r'outcome';
    yield serializers.serialize(
      object.outcome,
      specifiedType: const FullType(UnderwritingOutcome),
    );
    yield r'rulesSchemaVersion';
    yield serializers.serialize(
      object.rulesSchemaVersion,
      specifiedType: const FullType(int),
    );
    yield r'rulesSource';
    yield serializers.serialize(
      object.rulesSource,
      specifiedType: const FullType(UnderwritingResultRulesSourceEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UnderwritingResult object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UnderwritingResultBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'evaluatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.evaluatedAt = valueDes;
          break;
        case r'failures':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(UnderwritingFailure)]),
          ) as BuiltList<UnderwritingFailure>;
          result.failures.replace(valueDes);
          break;
        case r'inputs':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UnderwritingInputs),
          ) as UnderwritingInputs;
          result.inputs.replace(valueDes);
          break;
        case r'outcome':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UnderwritingOutcome),
          ) as UnderwritingOutcome;
          result.outcome = valueDes;
          break;
        case r'rulesSchemaVersion':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.rulesSchemaVersion = valueDes;
          break;
        case r'rulesSource':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UnderwritingResultRulesSourceEnum),
          ) as UnderwritingResultRulesSourceEnum;
          result.rulesSource = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UnderwritingResult deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UnderwritingResultBuilder();
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

class UnderwritingResultRulesSourceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PRODUCT')
  static const UnderwritingResultRulesSourceEnum PRODUCT = _$underwritingResultRulesSourceEnum_PRODUCT;
  @BuiltValueEnumConst(wireName: r'DEFAULT')
  static const UnderwritingResultRulesSourceEnum DEFAULT = _$underwritingResultRulesSourceEnum_DEFAULT;

  static Serializer<UnderwritingResultRulesSourceEnum> get serializer => _$underwritingResultRulesSourceEnumSerializer;

  const UnderwritingResultRulesSourceEnum._(String name): super(name);

  static BuiltSet<UnderwritingResultRulesSourceEnum> get values => _$underwritingResultRulesSourceEnumValues;
  static UnderwritingResultRulesSourceEnum valueOf(String name) => _$underwritingResultRulesSourceEnumValueOf(name);
}

