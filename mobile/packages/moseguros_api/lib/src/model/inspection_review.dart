//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/photo_slot.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'inspection_review.g.dart';

/// Ausente até haver revisão
///
/// Properties:
/// * [notes] - Notas da aprovação
/// * [rejectionReason] - Só em REJECTED
/// * [retakeSlots] - Ângulos a repetir pedidos na recusa
/// * [reviewedAt] 
@BuiltValue()
abstract class InspectionReview implements Built<InspectionReview, InspectionReviewBuilder> {
  /// Notas da aprovação
  @BuiltValueField(wireName: r'notes')
  String? get notes;

  /// Só em REJECTED
  @BuiltValueField(wireName: r'rejectionReason')
  String? get rejectionReason;

  /// Ângulos a repetir pedidos na recusa
  @BuiltValueField(wireName: r'retakeSlots')
  BuiltList<PhotoSlot> get retakeSlots;

  @BuiltValueField(wireName: r'reviewedAt')
  DateTime get reviewedAt;

  InspectionReview._();

  factory InspectionReview([void updates(InspectionReviewBuilder b)]) = _$InspectionReview;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InspectionReviewBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InspectionReview> get serializer => _$InspectionReviewSerializer();
}

class _$InspectionReviewSerializer implements PrimitiveSerializer<InspectionReview> {
  @override
  final Iterable<Type> types = const [InspectionReview, _$InspectionReview];

  @override
  final String wireName = r'InspectionReview';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InspectionReview object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.notes != null) {
      yield r'notes';
      yield serializers.serialize(
        object.notes,
        specifiedType: const FullType(String),
      );
    }
    if (object.rejectionReason != null) {
      yield r'rejectionReason';
      yield serializers.serialize(
        object.rejectionReason,
        specifiedType: const FullType(String),
      );
    }
    yield r'retakeSlots';
    yield serializers.serialize(
      object.retakeSlots,
      specifiedType: const FullType(BuiltList, [FullType(PhotoSlot)]),
    );
    yield r'reviewedAt';
    yield serializers.serialize(
      object.reviewedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InspectionReview object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InspectionReviewBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'notes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.notes = valueDes;
          break;
        case r'rejectionReason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.rejectionReason = valueDes;
          break;
        case r'retakeSlots':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PhotoSlot)]),
          ) as BuiltList<PhotoSlot>;
          result.retakeSlots.replace(valueDes);
          break;
        case r'reviewedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.reviewedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InspectionReview deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InspectionReviewBuilder();
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

