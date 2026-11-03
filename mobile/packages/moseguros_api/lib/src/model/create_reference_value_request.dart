//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_reference_value_request.g.dart';

/// CreateReferenceValueRequest
///
/// Properties:
/// * [code] - ^[A-Z0-9_]{1,40}$; não muda depois de criado
/// * [labelEn] 
/// * [labelPt] 
/// * [parentCode] 
/// * [sortOrder] 
@BuiltValue()
abstract class CreateReferenceValueRequest implements Built<CreateReferenceValueRequest, CreateReferenceValueRequestBuilder> {
  /// ^[A-Z0-9_]{1,40}$; não muda depois de criado
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'labelEn')
  String? get labelEn;

  @BuiltValueField(wireName: r'labelPt')
  String get labelPt;

  @BuiltValueField(wireName: r'parentCode')
  String? get parentCode;

  @BuiltValueField(wireName: r'sortOrder')
  int? get sortOrder;

  CreateReferenceValueRequest._();

  factory CreateReferenceValueRequest([void updates(CreateReferenceValueRequestBuilder b)]) = _$CreateReferenceValueRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateReferenceValueRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateReferenceValueRequest> get serializer => _$CreateReferenceValueRequestSerializer();
}

class _$CreateReferenceValueRequestSerializer implements PrimitiveSerializer<CreateReferenceValueRequest> {
  @override
  final Iterable<Type> types = const [CreateReferenceValueRequest, _$CreateReferenceValueRequest];

  @override
  final String wireName = r'CreateReferenceValueRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateReferenceValueRequest object, {
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
    if (object.sortOrder != null) {
      yield r'sortOrder';
      yield serializers.serialize(
        object.sortOrder,
        specifiedType: const FullType(int),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateReferenceValueRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateReferenceValueRequestBuilder result,
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
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateReferenceValueRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateReferenceValueRequestBuilder();
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

