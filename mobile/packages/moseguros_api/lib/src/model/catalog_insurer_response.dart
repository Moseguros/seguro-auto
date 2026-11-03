//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'catalog_insurer_response.g.dart';

/// CatalogInsurerResponse
///
/// Properties:
/// * [displayName] 
/// * [id] 
@BuiltValue()
abstract class CatalogInsurerResponse implements Built<CatalogInsurerResponse, CatalogInsurerResponseBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  CatalogInsurerResponse._();

  factory CatalogInsurerResponse([void updates(CatalogInsurerResponseBuilder b)]) = _$CatalogInsurerResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CatalogInsurerResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CatalogInsurerResponse> get serializer => _$CatalogInsurerResponseSerializer();
}

class _$CatalogInsurerResponseSerializer implements PrimitiveSerializer<CatalogInsurerResponse> {
  @override
  final Iterable<Type> types = const [CatalogInsurerResponse, _$CatalogInsurerResponse];

  @override
  final String wireName = r'CatalogInsurerResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CatalogInsurerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'displayName';
    yield serializers.serialize(
      object.displayName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CatalogInsurerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CatalogInsurerResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CatalogInsurerResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CatalogInsurerResponseBuilder();
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

