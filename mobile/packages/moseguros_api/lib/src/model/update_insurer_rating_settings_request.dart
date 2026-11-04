//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/charge.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_insurer_rating_settings_request.g.dart';

/// UpdateInsurerRatingSettingsRequest
///
/// Properties:
/// * [charges] 
/// * [version] 
@BuiltValue()
abstract class UpdateInsurerRatingSettingsRequest implements Built<UpdateInsurerRatingSettingsRequest, UpdateInsurerRatingSettingsRequestBuilder> {
  @BuiltValueField(wireName: r'charges')
  BuiltList<Charge> get charges;

  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateInsurerRatingSettingsRequest._();

  factory UpdateInsurerRatingSettingsRequest([void updates(UpdateInsurerRatingSettingsRequestBuilder b)]) = _$UpdateInsurerRatingSettingsRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateInsurerRatingSettingsRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateInsurerRatingSettingsRequest> get serializer => _$UpdateInsurerRatingSettingsRequestSerializer();
}

class _$UpdateInsurerRatingSettingsRequestSerializer implements PrimitiveSerializer<UpdateInsurerRatingSettingsRequest> {
  @override
  final Iterable<Type> types = const [UpdateInsurerRatingSettingsRequest, _$UpdateInsurerRatingSettingsRequest];

  @override
  final String wireName = r'UpdateInsurerRatingSettingsRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateInsurerRatingSettingsRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'charges';
    yield serializers.serialize(
      object.charges,
      specifiedType: const FullType(BuiltList, [FullType(Charge)]),
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
    UpdateInsurerRatingSettingsRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateInsurerRatingSettingsRequestBuilder result,
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
  UpdateInsurerRatingSettingsRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateInsurerRatingSettingsRequestBuilder();
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

