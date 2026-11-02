//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_profile_response.g.dart';

/// Só nas seguradoras
///
/// Properties:
/// * [complaintsEmail] 
/// * [ombudsmanOrganizationId] 
/// * [shortCode] 
@BuiltValue()
abstract class InsurerProfileResponse implements Built<InsurerProfileResponse, InsurerProfileResponseBuilder> {
  @BuiltValueField(wireName: r'complaintsEmail')
  String get complaintsEmail;

  @BuiltValueField(wireName: r'ombudsmanOrganizationId')
  String? get ombudsmanOrganizationId;

  @BuiltValueField(wireName: r'shortCode')
  String get shortCode;

  InsurerProfileResponse._();

  factory InsurerProfileResponse([void updates(InsurerProfileResponseBuilder b)]) = _$InsurerProfileResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerProfileResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerProfileResponse> get serializer => _$InsurerProfileResponseSerializer();
}

class _$InsurerProfileResponseSerializer implements PrimitiveSerializer<InsurerProfileResponse> {
  @override
  final Iterable<Type> types = const [InsurerProfileResponse, _$InsurerProfileResponse];

  @override
  final String wireName = r'InsurerProfileResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerProfileResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'complaintsEmail';
    yield serializers.serialize(
      object.complaintsEmail,
      specifiedType: const FullType(String),
    );
    if (object.ombudsmanOrganizationId != null) {
      yield r'ombudsmanOrganizationId';
      yield serializers.serialize(
        object.ombudsmanOrganizationId,
        specifiedType: const FullType(String),
      );
    }
    yield r'shortCode';
    yield serializers.serialize(
      object.shortCode,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerProfileResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerProfileResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'complaintsEmail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.complaintsEmail = valueDes;
          break;
        case r'ombudsmanOrganizationId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.ombudsmanOrganizationId = valueDes;
          break;
        case r'shortCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.shortCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerProfileResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerProfileResponseBuilder();
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

