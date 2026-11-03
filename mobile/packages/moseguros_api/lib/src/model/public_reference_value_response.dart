//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'public_reference_value_response.g.dart';

/// PublicReferenceValueResponse
///
/// Properties:
/// * [code] 
/// * [labelEn] 
/// * [labelPt] 
/// * [parentCode] 
@BuiltValue()
abstract class PublicReferenceValueResponse implements Built<PublicReferenceValueResponse, PublicReferenceValueResponseBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'labelEn')
  String? get labelEn;

  @BuiltValueField(wireName: r'labelPt')
  String get labelPt;

  @BuiltValueField(wireName: r'parentCode')
  String? get parentCode;

  PublicReferenceValueResponse._();

  factory PublicReferenceValueResponse([void updates(PublicReferenceValueResponseBuilder b)]) = _$PublicReferenceValueResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublicReferenceValueResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublicReferenceValueResponse> get serializer => _$PublicReferenceValueResponseSerializer();
}

class _$PublicReferenceValueResponseSerializer implements PrimitiveSerializer<PublicReferenceValueResponse> {
  @override
  final Iterable<Type> types = const [PublicReferenceValueResponse, _$PublicReferenceValueResponse];

  @override
  final String wireName = r'PublicReferenceValueResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublicReferenceValueResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    if (object.labelEn != null) {
      yield r'labelEn';
      yield serializers.serialize(
        object.labelEn,
        specifiedType: const FullType(String),
      );
    }
    yield r'labelPt';
    yield serializers.serialize(
      object.labelPt,
      specifiedType: const FullType(String),
    );
    if (object.parentCode != null) {
      yield r'parentCode';
      yield serializers.serialize(
        object.parentCode,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PublicReferenceValueResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublicReferenceValueResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'labelEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelEn = valueDes;
          break;
        case r'labelPt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelPt = valueDes;
          break;
        case r'parentCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.parentCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PublicReferenceValueResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublicReferenceValueResponseBuilder();
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

