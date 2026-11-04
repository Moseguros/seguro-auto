//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_summary_vehicle.g.dart';

/// QuoteSummaryVehicle
///
/// Properties:
/// * [make] 
/// * [model] 
/// * [plate] 
@BuiltValue()
abstract class QuoteSummaryVehicle implements Built<QuoteSummaryVehicle, QuoteSummaryVehicleBuilder> {
  @BuiltValueField(wireName: r'make')
  String? get make;

  @BuiltValueField(wireName: r'model')
  String? get model;

  @BuiltValueField(wireName: r'plate')
  String? get plate;

  QuoteSummaryVehicle._();

  factory QuoteSummaryVehicle([void updates(QuoteSummaryVehicleBuilder b)]) = _$QuoteSummaryVehicle;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteSummaryVehicleBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteSummaryVehicle> get serializer => _$QuoteSummaryVehicleSerializer();
}

class _$QuoteSummaryVehicleSerializer implements PrimitiveSerializer<QuoteSummaryVehicle> {
  @override
  final Iterable<Type> types = const [QuoteSummaryVehicle, _$QuoteSummaryVehicle];

  @override
  final String wireName = r'QuoteSummaryVehicle';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteSummaryVehicle object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.make != null) {
      yield r'make';
      yield serializers.serialize(
        object.make,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.model != null) {
      yield r'model';
      yield serializers.serialize(
        object.model,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.plate != null) {
      yield r'plate';
      yield serializers.serialize(
        object.plate,
        specifiedType: const FullType.nullable(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteSummaryVehicle object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteSummaryVehicleBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'make':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.make = valueDes;
          break;
        case r'model':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.model = valueDes;
          break;
        case r'plate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.plate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  QuoteSummaryVehicle deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteSummaryVehicleBuilder();
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

