//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/photo_source.dart';
import 'package:moseguros_api/src/model/photo_slot.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'inspection_photo_response.g.dart';

/// InspectionPhotoResponse
///
/// Properties:
/// * [fileId] 
/// * [id] 
/// * [latitude] 
/// * [longitude] 
/// * [slot] 
/// * [source_] 
/// * [takenAt] - Hora da fotografia indicada pelo cliente
/// * [uploadedAt] 
@BuiltValue()
abstract class InspectionPhotoResponse implements Built<InspectionPhotoResponse, InspectionPhotoResponseBuilder> {
  @BuiltValueField(wireName: r'fileId')
  String get fileId;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'latitude')
  num? get latitude;

  @BuiltValueField(wireName: r'longitude')
  num? get longitude;

  @BuiltValueField(wireName: r'slot')
  PhotoSlot get slot;
  // enum slotEnum {  FRONT,  REAR,  LEFT,  RIGHT,  ODOMETER,  CHASSIS,  EXTRA,  };

  @BuiltValueField(wireName: r'source')
  PhotoSource get source_;
  // enum source_Enum {  CAMERA,  UPLOAD,  };

  /// Hora da fotografia indicada pelo cliente
  @BuiltValueField(wireName: r'takenAt')
  DateTime? get takenAt;

  @BuiltValueField(wireName: r'uploadedAt')
  DateTime get uploadedAt;

  InspectionPhotoResponse._();

  factory InspectionPhotoResponse([void updates(InspectionPhotoResponseBuilder b)]) = _$InspectionPhotoResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InspectionPhotoResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InspectionPhotoResponse> get serializer => _$InspectionPhotoResponseSerializer();
}

class _$InspectionPhotoResponseSerializer implements PrimitiveSerializer<InspectionPhotoResponse> {
  @override
  final Iterable<Type> types = const [InspectionPhotoResponse, _$InspectionPhotoResponse];

  @override
  final String wireName = r'InspectionPhotoResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InspectionPhotoResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'fileId';
    yield serializers.serialize(
      object.fileId,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.latitude != null) {
      yield r'latitude';
      yield serializers.serialize(
        object.latitude,
        specifiedType: const FullType(num),
      );
    }
    if (object.longitude != null) {
      yield r'longitude';
      yield serializers.serialize(
        object.longitude,
        specifiedType: const FullType(num),
      );
    }
    yield r'slot';
    yield serializers.serialize(
      object.slot,
      specifiedType: const FullType(PhotoSlot),
    );
    yield r'source';
    yield serializers.serialize(
      object.source_,
      specifiedType: const FullType(PhotoSource),
    );
    if (object.takenAt != null) {
      yield r'takenAt';
      yield serializers.serialize(
        object.takenAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'uploadedAt';
    yield serializers.serialize(
      object.uploadedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InspectionPhotoResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InspectionPhotoResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileId = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'latitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.latitude = valueDes;
          break;
        case r'longitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.longitude = valueDes;
          break;
        case r'slot':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PhotoSlot),
          ) as PhotoSlot;
          result.slot = valueDes;
          break;
        case r'source':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PhotoSource),
          ) as PhotoSource;
          result.source_ = valueDes;
          break;
        case r'takenAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.takenAt = valueDes;
          break;
        case r'uploadedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.uploadedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InspectionPhotoResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InspectionPhotoResponseBuilder();
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

