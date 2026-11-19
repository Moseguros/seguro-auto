//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/subject_type.dart';
import 'package:moseguros_api/src/model/verification_result.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'police_check_response.g.dart';

/// PoliceCheckResponse
///
/// Properties:
/// * [checkedAt] 
/// * [id] 
/// * [plateMasked] - Vazio quando um código não correspondeu a nenhuma apólice
/// * [result] 
/// * [subjectType] 
@BuiltValue()
abstract class PoliceCheckResponse implements Built<PoliceCheckResponse, PoliceCheckResponseBuilder> {
  @BuiltValueField(wireName: r'checkedAt')
  DateTime get checkedAt;

  @BuiltValueField(wireName: r'id')
  String get id;

  /// Vazio quando um código não correspondeu a nenhuma apólice
  @BuiltValueField(wireName: r'plateMasked')
  String? get plateMasked;

  @BuiltValueField(wireName: r'result')
  VerificationResult get result;
  // enum resultEnum {  VALID,  NOT_YET_VALID,  EXPIRED,  SUSPENDED,  CANCELLED,  NOT_FOUND,  };

  @BuiltValueField(wireName: r'subjectType')
  SubjectType get subjectType;
  // enum subjectTypeEnum {  PLATE,  TOKEN,  };

  PoliceCheckResponse._();

  factory PoliceCheckResponse([void updates(PoliceCheckResponseBuilder b)]) = _$PoliceCheckResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PoliceCheckResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PoliceCheckResponse> get serializer => _$PoliceCheckResponseSerializer();
}

class _$PoliceCheckResponseSerializer implements PrimitiveSerializer<PoliceCheckResponse> {
  @override
  final Iterable<Type> types = const [PoliceCheckResponse, _$PoliceCheckResponse];

  @override
  final String wireName = r'PoliceCheckResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PoliceCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'checkedAt';
    yield serializers.serialize(
      object.checkedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.plateMasked != null) {
      yield r'plateMasked';
      yield serializers.serialize(
        object.plateMasked,
        specifiedType: const FullType(String),
      );
    }
    yield r'result';
    yield serializers.serialize(
      object.result,
      specifiedType: const FullType(VerificationResult),
    );
    yield r'subjectType';
    yield serializers.serialize(
      object.subjectType,
      specifiedType: const FullType(SubjectType),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PoliceCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PoliceCheckResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'checkedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.checkedAt = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'plateMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.plateMasked = valueDes;
          break;
        case r'result':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(VerificationResult),
          ) as VerificationResult;
          result.result = valueDes;
          break;
        case r'subjectType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SubjectType),
          ) as SubjectType;
          result.subjectType = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PoliceCheckResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PoliceCheckResponseBuilder();
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

