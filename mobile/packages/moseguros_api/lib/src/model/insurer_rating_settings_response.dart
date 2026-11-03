//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/charge.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_rating_settings_response.g.dart';

/// InsurerRatingSettingsResponse
///
/// Properties:
/// * [charges] 
/// * [ratingMode] 
/// * [version] 
@BuiltValue()
abstract class InsurerRatingSettingsResponse implements Built<InsurerRatingSettingsResponse, InsurerRatingSettingsResponseBuilder> {
  @BuiltValueField(wireName: r'charges')
  BuiltList<Charge> get charges;

  @BuiltValueField(wireName: r'ratingMode')
  InsurerRatingSettingsResponseRatingModeEnum get ratingMode;
  // enum ratingModeEnum {  INTERNAL,  EXTERNAL,  };

  @BuiltValueField(wireName: r'version')
  int get version;

  InsurerRatingSettingsResponse._();

  factory InsurerRatingSettingsResponse([void updates(InsurerRatingSettingsResponseBuilder b)]) = _$InsurerRatingSettingsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerRatingSettingsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerRatingSettingsResponse> get serializer => _$InsurerRatingSettingsResponseSerializer();
}

class _$InsurerRatingSettingsResponseSerializer implements PrimitiveSerializer<InsurerRatingSettingsResponse> {
  @override
  final Iterable<Type> types = const [InsurerRatingSettingsResponse, _$InsurerRatingSettingsResponse];

  @override
  final String wireName = r'InsurerRatingSettingsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerRatingSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'charges';
    yield serializers.serialize(
      object.charges,
      specifiedType: const FullType(BuiltList, [FullType(Charge)]),
    );
    yield r'ratingMode';
    yield serializers.serialize(
      object.ratingMode,
      specifiedType: const FullType(InsurerRatingSettingsResponseRatingModeEnum),
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
    InsurerRatingSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerRatingSettingsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'charges':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(Charge)]),
          ) as BuiltList<Charge>;
          result.charges.replace(valueDes);
          break;
        case r'ratingMode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InsurerRatingSettingsResponseRatingModeEnum),
          ) as InsurerRatingSettingsResponseRatingModeEnum;
          result.ratingMode = valueDes;
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
  InsurerRatingSettingsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerRatingSettingsResponseBuilder();
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

class InsurerRatingSettingsResponseRatingModeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INTERNAL')
  static const InsurerRatingSettingsResponseRatingModeEnum INTERNAL = _$insurerRatingSettingsResponseRatingModeEnum_INTERNAL;
  @BuiltValueEnumConst(wireName: r'EXTERNAL')
  static const InsurerRatingSettingsResponseRatingModeEnum EXTERNAL = _$insurerRatingSettingsResponseRatingModeEnum_EXTERNAL;

  static Serializer<InsurerRatingSettingsResponseRatingModeEnum> get serializer => _$insurerRatingSettingsResponseRatingModeEnumSerializer;

  const InsurerRatingSettingsResponseRatingModeEnum._(String name): super(name);

  static BuiltSet<InsurerRatingSettingsResponseRatingModeEnum> get values => _$insurerRatingSettingsResponseRatingModeEnumValues;
  static InsurerRatingSettingsResponseRatingModeEnum valueOf(String name) => _$insurerRatingSettingsResponseRatingModeEnumValueOf(name);
}

