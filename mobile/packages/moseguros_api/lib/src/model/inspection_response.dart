//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/inspection_status.dart';
import 'package:moseguros_api/src/model/inspection_photo_response.dart';
import 'package:moseguros_api/src/model/photo_slot.dart';
import 'package:moseguros_api/src/model/inspection_mode.dart';
import 'package:moseguros_api/src/model/inspection_review.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'inspection_response.g.dart';

/// InspectionResponse
///
/// Properties:
/// * [customerNotes] - Pedido presencial: local e horas preferidas
/// * [id] - Só com required = true
/// * [maxExtraPhotos] 
/// * [missingSlots] - Ângulos obrigatórios sem fotografia ([] em ON_SITE)
/// * [mode] 
/// * [odometerKm] 
/// * [photos] - Activas, pela ordem dos ângulos e depois do envio
/// * [required_] - Alguma cobertura OD_*
/// * [review] 
/// * [status] 
/// * [submittedAt] 
/// * [version] - Versão da vistoria, para approveInspection e rejectInspection
@BuiltValue()
abstract class InspectionResponse implements Built<InspectionResponse, InspectionResponseBuilder> {
  /// Pedido presencial: local e horas preferidas
  @BuiltValueField(wireName: r'customerNotes')
  String? get customerNotes;

  /// Só com required = true
  @BuiltValueField(wireName: r'id')
  String? get id;

  @BuiltValueField(wireName: r'maxExtraPhotos')
  int? get maxExtraPhotos;

  /// Ângulos obrigatórios sem fotografia ([] em ON_SITE)
  @BuiltValueField(wireName: r'missingSlots')
  BuiltList<PhotoSlot>? get missingSlots;

  @BuiltValueField(wireName: r'mode')
  InspectionMode? get mode;
  // enum modeEnum {  SELF_PHOTOS,  ON_SITE,  };

  @BuiltValueField(wireName: r'odometerKm')
  int? get odometerKm;

  /// Activas, pela ordem dos ângulos e depois do envio
  @BuiltValueField(wireName: r'photos')
  BuiltList<InspectionPhotoResponse>? get photos;

  /// Alguma cobertura OD_*
  @BuiltValueField(wireName: r'required')
  bool get required_;

  @BuiltValueField(wireName: r'review')
  InspectionReview? get review;

  @BuiltValueField(wireName: r'status')
  InspectionStatus? get status;
  // enum statusEnum {  PENDING,  SUBMITTED,  APPROVED,  REJECTED,  };

  @BuiltValueField(wireName: r'submittedAt')
  DateTime? get submittedAt;

  /// Versão da vistoria, para approveInspection e rejectInspection
  @BuiltValueField(wireName: r'version')
  int? get version;

  InspectionResponse._();

  factory InspectionResponse([void updates(InspectionResponseBuilder b)]) = _$InspectionResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InspectionResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InspectionResponse> get serializer => _$InspectionResponseSerializer();
}

class _$InspectionResponseSerializer implements PrimitiveSerializer<InspectionResponse> {
  @override
  final Iterable<Type> types = const [InspectionResponse, _$InspectionResponse];

  @override
  final String wireName = r'InspectionResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InspectionResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.customerNotes != null) {
      yield r'customerNotes';
      yield serializers.serialize(
        object.customerNotes,
        specifiedType: const FullType(String),
      );
    }
    if (object.id != null) {
      yield r'id';
      yield serializers.serialize(
        object.id,
        specifiedType: const FullType(String),
      );
    }
    if (object.maxExtraPhotos != null) {
      yield r'maxExtraPhotos';
      yield serializers.serialize(
        object.maxExtraPhotos,
        specifiedType: const FullType(int),
      );
    }
    if (object.missingSlots != null) {
      yield r'missingSlots';
      yield serializers.serialize(
        object.missingSlots,
        specifiedType: const FullType(BuiltList, [FullType(PhotoSlot)]),
      );
    }
    if (object.mode != null) {
      yield r'mode';
      yield serializers.serialize(
        object.mode,
        specifiedType: const FullType(InspectionMode),
      );
    }
    if (object.odometerKm != null) {
      yield r'odometerKm';
      yield serializers.serialize(
        object.odometerKm,
        specifiedType: const FullType(int),
      );
    }
    if (object.photos != null) {
      yield r'photos';
      yield serializers.serialize(
        object.photos,
        specifiedType: const FullType(BuiltList, [FullType(InspectionPhotoResponse)]),
      );
    }
    yield r'required';
    yield serializers.serialize(
      object.required_,
      specifiedType: const FullType(bool),
    );
    if (object.review != null) {
      yield r'review';
      yield serializers.serialize(
        object.review,
        specifiedType: const FullType(InspectionReview),
      );
    }
    if (object.status != null) {
      yield r'status';
      yield serializers.serialize(
        object.status,
        specifiedType: const FullType(InspectionStatus),
      );
    }
    if (object.submittedAt != null) {
      yield r'submittedAt';
      yield serializers.serialize(
        object.submittedAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.version != null) {
      yield r'version';
      yield serializers.serialize(
        object.version,
        specifiedType: const FullType(int),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    InspectionResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InspectionResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'customerNotes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerNotes = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'maxExtraPhotos':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxExtraPhotos = valueDes;
          break;
        case r'missingSlots':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PhotoSlot)]),
          ) as BuiltList<PhotoSlot>;
          result.missingSlots.replace(valueDes);
          break;
        case r'mode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionMode),
          ) as InspectionMode;
          result.mode = valueDes;
          break;
        case r'odometerKm':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.odometerKm = valueDes;
          break;
        case r'photos':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(InspectionPhotoResponse)]),
          ) as BuiltList<InspectionPhotoResponse>;
          result.photos.replace(valueDes);
          break;
        case r'required':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.required_ = valueDes;
          break;
        case r'review':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionReview),
          ) as InspectionReview;
          result.review.replace(valueDes);
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionStatus),
          ) as InspectionStatus;
          result.status = valueDes;
          break;
        case r'submittedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.submittedAt = valueDes;
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
  InspectionResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InspectionResponseBuilder();
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

