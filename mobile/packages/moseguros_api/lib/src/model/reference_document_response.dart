//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reference_document_response.g.dart';

/// ReferenceDocumentResponse
///
/// Properties:
/// * [fileId] 
@BuiltValue()
abstract class ReferenceDocumentResponse implements Built<ReferenceDocumentResponse, ReferenceDocumentResponseBuilder> {
  @BuiltValueField(wireName: r'fileId')
  String get fileId;

  ReferenceDocumentResponse._();

  factory ReferenceDocumentResponse([void updates(ReferenceDocumentResponseBuilder b)]) = _$ReferenceDocumentResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ReferenceDocumentResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ReferenceDocumentResponse> get serializer => _$ReferenceDocumentResponseSerializer();
}

class _$ReferenceDocumentResponseSerializer implements PrimitiveSerializer<ReferenceDocumentResponse> {
  @override
  final Iterable<Type> types = const [ReferenceDocumentResponse, _$ReferenceDocumentResponse];

  @override
  final String wireName = r'ReferenceDocumentResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ReferenceDocumentResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'fileId';
    yield serializers.serialize(
      object.fileId,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ReferenceDocumentResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ReferenceDocumentResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ReferenceDocumentResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ReferenceDocumentResponseBuilder();
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

