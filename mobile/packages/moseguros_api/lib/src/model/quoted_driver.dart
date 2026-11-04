//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quoted_driver.g.dart';

/// QuotedDriver
///
/// Properties:
/// * [birthDate] 
/// * [driverId] 
/// * [licenceCategories] 
/// * [licenceIssueDate] 
@BuiltValue()
abstract class QuotedDriver implements Built<QuotedDriver, QuotedDriverBuilder> {
  @BuiltValueField(wireName: r'birthDate')
  Date? get birthDate;

  @BuiltValueField(wireName: r'driverId')
  String? get driverId;

  @BuiltValueField(wireName: r'licenceCategories')
  BuiltList<String> get licenceCategories;

  @BuiltValueField(wireName: r'licenceIssueDate')
  Date? get licenceIssueDate;

  QuotedDriver._();

  factory QuotedDriver([void updates(QuotedDriverBuilder b)]) = _$QuotedDriver;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuotedDriverBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuotedDriver> get serializer => _$QuotedDriverSerializer();
}

class _$QuotedDriverSerializer implements PrimitiveSerializer<QuotedDriver> {
  @override
  final Iterable<Type> types = const [QuotedDriver, _$QuotedDriver];

  @override
  final String wireName = r'QuotedDriver';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuotedDriver object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.birthDate != null) {
      yield r'birthDate';
      yield serializers.serialize(
        object.birthDate,
        specifiedType: const FullType.nullable(Date),
      );
    }
    if (object.driverId != null) {
      yield r'driverId';
      yield serializers.serialize(
        object.driverId,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'licenceCategories';
    yield serializers.serialize(
      object.licenceCategories,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    if (object.licenceIssueDate != null) {
      yield r'licenceIssueDate';
      yield serializers.serialize(
        object.licenceIssueDate,
        specifiedType: const FullType.nullable(Date),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    QuotedDriver object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuotedDriverBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'birthDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.birthDate = valueDes;
          break;
        case r'driverId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.driverId = valueDes;
          break;
        case r'licenceCategories':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.licenceCategories.replace(valueDes);
          break;
        case r'licenceIssueDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.licenceIssueDate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  QuotedDriver deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuotedDriverBuilder();
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

