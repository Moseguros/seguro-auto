//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/public_reference_value_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'public_reference_values_response.g.dart';

/// PublicReferenceValuesResponse
///
/// Properties:
/// * [values] 
@BuiltValue()
abstract class PublicReferenceValuesResponse implements Built<PublicReferenceValuesResponse, PublicReferenceValuesResponseBuilder> {
  @BuiltValueField(wireName: r'values')
  BuiltList<PublicReferenceValueResponse> get values;

  PublicReferenceValuesResponse._();

  factory PublicReferenceValuesResponse([void updates(PublicReferenceValuesResponseBuilder b)]) = _$PublicReferenceValuesResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublicReferenceValuesResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublicReferenceValuesResponse> get serializer => _$PublicReferenceValuesResponseSerializer();
}

class _$PublicReferenceValuesResponseSerializer implements PrimitiveSerializer<PublicReferenceValuesResponse> {
  @override
  final Iterable<Type> types = const [PublicReferenceValuesResponse, _$PublicReferenceValuesResponse];

  @override
  final String wireName = r'PublicReferenceValuesResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublicReferenceValuesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'values';
    yield serializers.serialize(
      object.values,
      specifiedType: const FullType(BuiltList, [FullType(PublicReferenceValueResponse)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PublicReferenceValuesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublicReferenceValuesResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'values':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PublicReferenceValueResponse)]),
          ) as BuiltList<PublicReferenceValueResponse>;
          result.values.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PublicReferenceValuesResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublicReferenceValuesResponseBuilder();
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

