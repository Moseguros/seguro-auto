//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_plate_format_request.g.dart';

/// UpdatePlateFormatRequest
///
/// Properties:
/// * [displayTemplate] 
/// * [example] 
/// * [label] 
/// * [pattern] 
/// * [sortOrder] 
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdatePlateFormatRequest implements Built<UpdatePlateFormatRequest, UpdatePlateFormatRequestBuilder> {
  @BuiltValueField(wireName: r'displayTemplate')
  String get displayTemplate;

  @BuiltValueField(wireName: r'example')
  String get example;

  @BuiltValueField(wireName: r'label')
  String get label;

  @BuiltValueField(wireName: r'pattern')
  String get pattern;

  @BuiltValueField(wireName: r'sortOrder')
  int get sortOrder;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdatePlateFormatRequest._();

  factory UpdatePlateFormatRequest([void updates(UpdatePlateFormatRequestBuilder b)]) = _$UpdatePlateFormatRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdatePlateFormatRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdatePlateFormatRequest> get serializer => _$UpdatePlateFormatRequestSerializer();
}

class _$UpdatePlateFormatRequestSerializer implements PrimitiveSerializer<UpdatePlateFormatRequest> {
  @override
  final Iterable<Type> types = const [UpdatePlateFormatRequest, _$UpdatePlateFormatRequest];

  @override
  final String wireName = r'UpdatePlateFormatRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdatePlateFormatRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'displayTemplate';
    yield serializers.serialize(
      object.displayTemplate,
      specifiedType: const FullType(String),
    );
    yield r'example';
    yield serializers.serialize(
      object.example,
      specifiedType: const FullType(String),
    );
    yield r'label';
    yield serializers.serialize(
      object.label,
      specifiedType: const FullType(String),
    );
    yield r'pattern';
    yield serializers.serialize(
      object.pattern,
      specifiedType: const FullType(String),
    );
    yield r'sortOrder';
    yield serializers.serialize(
      object.sortOrder,
      specifiedType: const FullType(int),
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
    UpdatePlateFormatRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdatePlateFormatRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayTemplate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayTemplate = valueDes;
          break;
        case r'example':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.example = valueDes;
          break;
        case r'label':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.label = valueDes;
          break;
        case r'pattern':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.pattern = valueDes;
          break;
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
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
  UpdatePlateFormatRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdatePlateFormatRequestBuilder();
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

