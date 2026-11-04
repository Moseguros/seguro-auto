//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_files_response.g.dart';

/// KycFilesResponse
///
/// Properties:
/// * [docBackFileId] - Omitido no passaporte
/// * [docFrontFileId] 
/// * [selfieFileId] 
@BuiltValue()
abstract class KycFilesResponse implements Built<KycFilesResponse, KycFilesResponseBuilder> {
  /// Omitido no passaporte
  @BuiltValueField(wireName: r'docBackFileId')
  String? get docBackFileId;

  @BuiltValueField(wireName: r'docFrontFileId')
  String get docFrontFileId;

  @BuiltValueField(wireName: r'selfieFileId')
  String get selfieFileId;

  KycFilesResponse._();

  factory KycFilesResponse([void updates(KycFilesResponseBuilder b)]) = _$KycFilesResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(KycFilesResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<KycFilesResponse> get serializer => _$KycFilesResponseSerializer();
}

class _$KycFilesResponseSerializer implements PrimitiveSerializer<KycFilesResponse> {
  @override
  final Iterable<Type> types = const [KycFilesResponse, _$KycFilesResponse];

  @override
  final String wireName = r'KycFilesResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    KycFilesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.docBackFileId != null) {
      yield r'docBackFileId';
      yield serializers.serialize(
        object.docBackFileId,
        specifiedType: const FullType(String),
      );
    }
    yield r'docFrontFileId';
    yield serializers.serialize(
      object.docFrontFileId,
      specifiedType: const FullType(String),
    );
    yield r'selfieFileId';
    yield serializers.serialize(
      object.selfieFileId,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    KycFilesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required KycFilesResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'docBackFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.docBackFileId = valueDes;
          break;
        case r'docFrontFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.docFrontFileId = valueDes;
          break;
        case r'selfieFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.selfieFileId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  KycFilesResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = KycFilesResponseBuilder();
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

