//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'risk_driver_request.g.dart';

/// RiskDriverRequest
///
/// Properties:
/// * [birthDate] - No passado; idade até 100 anos
/// * [licenceCategories] - Opcional (0–12): códigos activos em LICENCE_CATEGORY
/// * [licenceIssueDate] - Opcional; entre o nascimento e hoje
@BuiltValue()
abstract class RiskDriverRequest implements Built<RiskDriverRequest, RiskDriverRequestBuilder> {
  /// No passado; idade até 100 anos
  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  /// Opcional (0–12): códigos activos em LICENCE_CATEGORY
  @BuiltValueField(wireName: r'licenceCategories')
  BuiltList<String>? get licenceCategories;

  /// Opcional; entre o nascimento e hoje
  @BuiltValueField(wireName: r'licenceIssueDate')
  Date? get licenceIssueDate;

  RiskDriverRequest._();

  factory RiskDriverRequest([void updates(RiskDriverRequestBuilder b)]) = _$RiskDriverRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RiskDriverRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RiskDriverRequest> get serializer => _$RiskDriverRequestSerializer();
}

class _$RiskDriverRequestSerializer implements PrimitiveSerializer<RiskDriverRequest> {
  @override
  final Iterable<Type> types = const [RiskDriverRequest, _$RiskDriverRequest];

  @override
  final String wireName = r'RiskDriverRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RiskDriverRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'birthDate';
    yield serializers.serialize(
      object.birthDate,
      specifiedType: const FullType(Date),
    );
    if (object.licenceCategories != null) {
      yield r'licenceCategories';
      yield serializers.serialize(
        object.licenceCategories,
        specifiedType: const FullType(BuiltList, [FullType(String)]),
      );
    }
    if (object.licenceIssueDate != null) {
      yield r'licenceIssueDate';
      yield serializers.serialize(
        object.licenceIssueDate,
        specifiedType: const FullType(Date),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    RiskDriverRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RiskDriverRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'birthDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.birthDate = valueDes;
          break;
        case r'licenceCategories':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.licenceCategories.replace(valueDes);
          break;
        case r'licenceIssueDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.licenceIssueDate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RiskDriverRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RiskDriverRequestBuilder();
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

