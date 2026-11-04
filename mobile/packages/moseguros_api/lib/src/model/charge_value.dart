//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'dart:core';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';
import 'package:one_of/one_of.dart';

part 'charge_value.g.dart';

/// PERCENT: percentagem em texto (\"2.5\" = 2,5 %); FIXED: centavos (inteiro)
@BuiltValue()
abstract class ChargeValue implements Built<ChargeValue, ChargeValueBuilder> {
  /// One Of [String], [int]
  OneOf get oneOf;

  ChargeValue._();

  factory ChargeValue([void updates(ChargeValueBuilder b)]) = _$ChargeValue;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ChargeValueBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ChargeValue> get serializer => _$ChargeValueSerializer();
}

class _$ChargeValueSerializer implements PrimitiveSerializer<ChargeValue> {
  @override
  final Iterable<Type> types = const [ChargeValue, _$ChargeValue];

  @override
  final String wireName = r'ChargeValue';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ChargeValue object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
  }

  @override
  Object serialize(
    Serializers serializers,
    ChargeValue object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final oneOf = object.oneOf;
    return serializers.serialize(oneOf.value, specifiedType: FullType(oneOf.valueType))!;
  }

  @override
  ChargeValue deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ChargeValueBuilder();
    Object? oneOfDataSrc;
    final targetType = const FullType(OneOf, [FullType(String), FullType(int), ]);
    oneOfDataSrc = serialized;
    result.oneOf = serializers.deserialize(oneOfDataSrc, specifiedType: targetType) as OneOf;
    return result.build();
  }
}

