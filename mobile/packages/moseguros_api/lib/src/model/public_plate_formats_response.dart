//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/public_plate_format_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'public_plate_formats_response.g.dart';

/// PublicPlateFormatsResponse
///
/// Properties:
/// * [formats] 
@BuiltValue()
abstract class PublicPlateFormatsResponse implements Built<PublicPlateFormatsResponse, PublicPlateFormatsResponseBuilder> {
  @BuiltValueField(wireName: r'formats')
  BuiltList<PublicPlateFormatResponse> get formats;

  PublicPlateFormatsResponse._();

  factory PublicPlateFormatsResponse([void updates(PublicPlateFormatsResponseBuilder b)]) = _$PublicPlateFormatsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublicPlateFormatsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublicPlateFormatsResponse> get serializer => _$PublicPlateFormatsResponseSerializer();
}

class _$PublicPlateFormatsResponseSerializer implements PrimitiveSerializer<PublicPlateFormatsResponse> {
  @override
  final Iterable<Type> types = const [PublicPlateFormatsResponse, _$PublicPlateFormatsResponse];

  @override
  final String wireName = r'PublicPlateFormatsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublicPlateFormatsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'formats';
    yield serializers.serialize(
      object.formats,
      specifiedType: const FullType(BuiltList, [FullType(PublicPlateFormatResponse)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PublicPlateFormatsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublicPlateFormatsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'formats':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PublicPlateFormatResponse)]),
          ) as BuiltList<PublicPlateFormatResponse>;
          result.formats.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PublicPlateFormatsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublicPlateFormatsResponseBuilder();
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

