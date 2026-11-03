//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_branding_response.g.dart';

/// InsurerBrandingResponse
///
/// Properties:
/// * [logoFileId] 
@BuiltValue()
abstract class InsurerBrandingResponse implements Built<InsurerBrandingResponse, InsurerBrandingResponseBuilder> {
  @BuiltValueField(wireName: r'logoFileId')
  String? get logoFileId;

  InsurerBrandingResponse._();

  factory InsurerBrandingResponse([void updates(InsurerBrandingResponseBuilder b)]) = _$InsurerBrandingResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerBrandingResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerBrandingResponse> get serializer => _$InsurerBrandingResponseSerializer();
}

class _$InsurerBrandingResponseSerializer implements PrimitiveSerializer<InsurerBrandingResponse> {
  @override
  final Iterable<Type> types = const [InsurerBrandingResponse, _$InsurerBrandingResponse];

  @override
  final String wireName = r'InsurerBrandingResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerBrandingResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'logoFileId';
    yield object.logoFileId == null ? null : serializers.serialize(
      object.logoFileId,
      specifiedType: const FullType.nullable(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerBrandingResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerBrandingResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'logoFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.logoFileId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerBrandingResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerBrandingResponseBuilder();
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

