//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_me_request.g.dart';

/// UpdateMeRequest
///
/// Properties:
/// * [displayName] - Só staff, 2–120 caracteres; omitido mantém o actual. Ignorado para clientes (o nome está no perfil).
/// * [preferredLanguage] 
@BuiltValue()
abstract class UpdateMeRequest implements Built<UpdateMeRequest, UpdateMeRequestBuilder> {
  /// Só staff, 2–120 caracteres; omitido mantém o actual. Ignorado para clientes (o nome está no perfil).
  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'preferredLanguage')
  UpdateMeRequestPreferredLanguageEnum get preferredLanguage;
  // enum preferredLanguageEnum {  pt,  en,  };

  UpdateMeRequest._();

  factory UpdateMeRequest([void updates(UpdateMeRequestBuilder b)]) = _$UpdateMeRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateMeRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateMeRequest> get serializer => _$UpdateMeRequestSerializer();
}

class _$UpdateMeRequestSerializer implements PrimitiveSerializer<UpdateMeRequest> {
  @override
  final Iterable<Type> types = const [UpdateMeRequest, _$UpdateMeRequest];

  @override
  final String wireName = r'UpdateMeRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateMeRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType(String),
      );
    }
    yield r'preferredLanguage';
    yield serializers.serialize(
      object.preferredLanguage,
      specifiedType: const FullType(UpdateMeRequestPreferredLanguageEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateMeRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateMeRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayName = valueDes;
          break;
        case r'preferredLanguage':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UpdateMeRequestPreferredLanguageEnum),
          ) as UpdateMeRequestPreferredLanguageEnum;
          result.preferredLanguage = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UpdateMeRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateMeRequestBuilder();
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

class UpdateMeRequestPreferredLanguageEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'pt')
  static const UpdateMeRequestPreferredLanguageEnum pt = _$updateMeRequestPreferredLanguageEnum_pt;
  @BuiltValueEnumConst(wireName: r'en')
  static const UpdateMeRequestPreferredLanguageEnum en = _$updateMeRequestPreferredLanguageEnum_en;

  static Serializer<UpdateMeRequestPreferredLanguageEnum> get serializer => _$updateMeRequestPreferredLanguageEnumSerializer;

  const UpdateMeRequestPreferredLanguageEnum._(String name): super(name);

  static BuiltSet<UpdateMeRequestPreferredLanguageEnum> get values => _$updateMeRequestPreferredLanguageEnumValues;
  static UpdateMeRequestPreferredLanguageEnum valueOf(String name) => _$updateMeRequestPreferredLanguageEnumValueOf(name);
}

