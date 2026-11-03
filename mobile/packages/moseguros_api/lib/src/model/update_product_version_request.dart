//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/vehicle_category.dart';
import 'package:moseguros_api/src/model/vehicle_usage.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_product_version_request.g.dart';

/// UpdateProductVersionRequest
///
/// Properties:
/// * [changeNotes] - O que mudou nesta versão
/// * [usages] 
/// * [vehicleCategories] 
/// * [version] 
@BuiltValue()
abstract class UpdateProductVersionRequest implements Built<UpdateProductVersionRequest, UpdateProductVersionRequestBuilder> {
  /// O que mudou nesta versão
  @BuiltValueField(wireName: r'changeNotes')
  String? get changeNotes;

  @BuiltValueField(wireName: r'usages')
  BuiltList<VehicleUsage> get usages;

  @BuiltValueField(wireName: r'vehicleCategories')
  BuiltList<VehicleCategory> get vehicleCategories;

  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateProductVersionRequest._();

  factory UpdateProductVersionRequest([void updates(UpdateProductVersionRequestBuilder b)]) = _$UpdateProductVersionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateProductVersionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateProductVersionRequest> get serializer => _$UpdateProductVersionRequestSerializer();
}

class _$UpdateProductVersionRequestSerializer implements PrimitiveSerializer<UpdateProductVersionRequest> {
  @override
  final Iterable<Type> types = const [UpdateProductVersionRequest, _$UpdateProductVersionRequest];

  @override
  final String wireName = r'UpdateProductVersionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateProductVersionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.changeNotes != null) {
      yield r'changeNotes';
      yield serializers.serialize(
        object.changeNotes,
        specifiedType: const FullType(String),
      );
    }
    yield r'usages';
    yield serializers.serialize(
      object.usages,
      specifiedType: const FullType(BuiltList, [FullType(VehicleUsage)]),
    );
    yield r'vehicleCategories';
    yield serializers.serialize(
      object.vehicleCategories,
      specifiedType: const FullType(BuiltList, [FullType(VehicleCategory)]),
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
    UpdateProductVersionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateProductVersionRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'changeNotes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.changeNotes = valueDes;
          break;
        case r'usages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(VehicleUsage)]),
          ) as BuiltList<VehicleUsage>;
          result.usages.replace(valueDes);
          break;
        case r'vehicleCategories':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(VehicleCategory)]),
          ) as BuiltList<VehicleCategory>;
          result.vehicleCategories.replace(valueDes);
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
  UpdateProductVersionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateProductVersionRequestBuilder();
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

