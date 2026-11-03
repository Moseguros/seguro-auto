//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'registration_doc_response.g.dart';

/// RegistrationDocResponse
///
/// Properties:
/// * [contentType] 
/// * [fileId] - Descarregar com GET /api/v1/files/{fileId} (downloadFile)
/// * [sizeBytes] 
/// * [uploadedAt] 
@BuiltValue()
abstract class RegistrationDocResponse implements Built<RegistrationDocResponse, RegistrationDocResponseBuilder> {
  @BuiltValueField(wireName: r'contentType')
  String get contentType;

  /// Descarregar com GET /api/v1/files/{fileId} (downloadFile)
  @BuiltValueField(wireName: r'fileId')
  String get fileId;

  @BuiltValueField(wireName: r'sizeBytes')
  int get sizeBytes;

  @BuiltValueField(wireName: r'uploadedAt')
  DateTime get uploadedAt;

  RegistrationDocResponse._();

  factory RegistrationDocResponse([void updates(RegistrationDocResponseBuilder b)]) = _$RegistrationDocResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RegistrationDocResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RegistrationDocResponse> get serializer => _$RegistrationDocResponseSerializer();
}

class _$RegistrationDocResponseSerializer implements PrimitiveSerializer<RegistrationDocResponse> {
  @override
  final Iterable<Type> types = const [RegistrationDocResponse, _$RegistrationDocResponse];

  @override
  final String wireName = r'RegistrationDocResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RegistrationDocResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'contentType';
    yield serializers.serialize(
      object.contentType,
      specifiedType: const FullType(String),
    );
    yield r'fileId';
    yield serializers.serialize(
      object.fileId,
      specifiedType: const FullType(String),
    );
    yield r'sizeBytes';
    yield serializers.serialize(
      object.sizeBytes,
      specifiedType: const FullType(int),
    );
    yield r'uploadedAt';
    yield serializers.serialize(
      object.uploadedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RegistrationDocResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RegistrationDocResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'contentType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.contentType = valueDes;
          break;
        case r'fileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileId = valueDes;
          break;
        case r'sizeBytes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sizeBytes = valueDes;
          break;
        case r'uploadedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.uploadedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RegistrationDocResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RegistrationDocResponseBuilder();
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

