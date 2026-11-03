//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_insurer.g.dart';

/// QuoteInsurer
///
/// Properties:
/// * [displayName] 
/// * [id] 
/// * [logoFileId] 
/// * [shortCode] 
@BuiltValue()
abstract class QuoteInsurer implements Built<QuoteInsurer, QuoteInsurerBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'logoFileId')
  String? get logoFileId;

  @BuiltValueField(wireName: r'shortCode')
  String get shortCode;

  QuoteInsurer._();

  factory QuoteInsurer([void updates(QuoteInsurerBuilder b)]) = _$QuoteInsurer;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteInsurerBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteInsurer> get serializer => _$QuoteInsurerSerializer();
}

class _$QuoteInsurerSerializer implements PrimitiveSerializer<QuoteInsurer> {
  @override
  final Iterable<Type> types = const [QuoteInsurer, _$QuoteInsurer];

  @override
  final String wireName = r'QuoteInsurer';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteInsurer object, {
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
    if (object.logoFileId != null) {
      yield r'logoFileId';
      yield serializers.serialize(
        object.logoFileId,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'shortCode';
    yield serializers.serialize(
      object.shortCode,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteInsurer object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteInsurerBuilder result,
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
        case r'logoFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.logoFileId = valueDes;
          break;
        case r'shortCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.shortCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  QuoteInsurer deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteInsurerBuilder();
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

