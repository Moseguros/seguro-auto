//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_reference_value_request.g.dart';

/// UpdateReferenceValueRequest
///
/// Properties:
/// * [labelEn] 
/// * [labelPt] 
/// * [sortOrder] 
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateReferenceValueRequest implements Built<UpdateReferenceValueRequest, UpdateReferenceValueRequestBuilder> {
  @BuiltValueField(wireName: r'labelEn')
  String? get labelEn;

  @BuiltValueField(wireName: r'labelPt')
  String get labelPt;

  @BuiltValueField(wireName: r'sortOrder')
  int? get sortOrder;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateReferenceValueRequest._();

  factory UpdateReferenceValueRequest([void updates(UpdateReferenceValueRequestBuilder b)]) = _$UpdateReferenceValueRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateReferenceValueRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateReferenceValueRequest> get serializer => _$UpdateReferenceValueRequestSerializer();
}

class _$UpdateReferenceValueRequestSerializer implements PrimitiveSerializer<UpdateReferenceValueRequest> {
  @override
  final Iterable<Type> types = const [UpdateReferenceValueRequest, _$UpdateReferenceValueRequest];

  @override
  final String wireName = r'UpdateReferenceValueRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateReferenceValueRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    if (object.sortOrder != null) {
      yield r'sortOrder';
      yield serializers.serialize(
        object.sortOrder,
        specifiedType: const FullType(int),
      );
    }
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateReferenceValueRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateReferenceValueRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
  UpdateReferenceValueRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateReferenceValueRequestBuilder();
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

