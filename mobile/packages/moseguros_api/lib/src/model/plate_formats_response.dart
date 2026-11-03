//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/plate_format_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'plate_formats_response.g.dart';

/// PlateFormatsResponse
///
/// Properties:
/// * [formats] 
@BuiltValue()
abstract class PlateFormatsResponse implements Built<PlateFormatsResponse, PlateFormatsResponseBuilder> {
  @BuiltValueField(wireName: r'formats')
  BuiltList<PlateFormatResponse> get formats;

  PlateFormatsResponse._();

  factory PlateFormatsResponse([void updates(PlateFormatsResponseBuilder b)]) = _$PlateFormatsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PlateFormatsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PlateFormatsResponse> get serializer => _$PlateFormatsResponseSerializer();
}

class _$PlateFormatsResponseSerializer implements PrimitiveSerializer<PlateFormatsResponse> {
  @override
  final Iterable<Type> types = const [PlateFormatsResponse, _$PlateFormatsResponse];

  @override
  final String wireName = r'PlateFormatsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PlateFormatsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'formats';
    yield serializers.serialize(
      object.formats,
      specifiedType: const FullType(BuiltList, [FullType(PlateFormatResponse)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PlateFormatsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PlateFormatsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'formats':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PlateFormatResponse)]),
          ) as BuiltList<PlateFormatResponse>;
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
  PlateFormatsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PlateFormatsResponseBuilder();
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

