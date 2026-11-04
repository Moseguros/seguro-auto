//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/photo_slot.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reject_inspection_request.g.dart';

/// RejectInspectionRequest
///
/// Properties:
/// * [reason] 
/// * [retakeSlots] - Ângulos a repetir, sem repetidos (INVALID_SLOT, DUPLICATED)
/// * [version] - Versão da vistoria; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class RejectInspectionRequest implements Built<RejectInspectionRequest, RejectInspectionRequestBuilder> {
  @BuiltValueField(wireName: r'reason')
  String get reason;

  /// Ângulos a repetir, sem repetidos (INVALID_SLOT, DUPLICATED)
  @BuiltValueField(wireName: r'retakeSlots')
  BuiltList<PhotoSlot>? get retakeSlots;

  /// Versão da vistoria; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  RejectInspectionRequest._();

  factory RejectInspectionRequest([void updates(RejectInspectionRequestBuilder b)]) = _$RejectInspectionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RejectInspectionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RejectInspectionRequest> get serializer => _$RejectInspectionRequestSerializer();
}

class _$RejectInspectionRequestSerializer implements PrimitiveSerializer<RejectInspectionRequest> {
  @override
  final Iterable<Type> types = const [RejectInspectionRequest, _$RejectInspectionRequest];

  @override
  final String wireName = r'RejectInspectionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RejectInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'reason';
    yield serializers.serialize(
      object.reason,
      specifiedType: const FullType(String),
    );
    if (object.retakeSlots != null) {
      yield r'retakeSlots';
      yield serializers.serialize(
        object.retakeSlots,
        specifiedType: const FullType(BuiltList, [FullType(PhotoSlot)]),
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
    RejectInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RejectInspectionRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'reason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.reason = valueDes;
          break;
        case r'retakeSlots':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PhotoSlot)]),
          ) as BuiltList<PhotoSlot>;
          result.retakeSlots.replace(valueDes);
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
  RejectInspectionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RejectInspectionRequestBuilder();
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

