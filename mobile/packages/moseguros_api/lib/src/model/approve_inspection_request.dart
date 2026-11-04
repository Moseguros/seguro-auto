//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/inspection_mode.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'approve_inspection_request.g.dart';

/// ApproveInspectionRequest
///
/// Properties:
/// * [mode] 
/// * [notes] 
/// * [version] - Versão da vistoria; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class ApproveInspectionRequest implements Built<ApproveInspectionRequest, ApproveInspectionRequestBuilder> {
  @BuiltValueField(wireName: r'mode')
  InspectionMode? get mode;
  // enum modeEnum {  SELF_PHOTOS,  ON_SITE,  };

  @BuiltValueField(wireName: r'notes')
  String? get notes;

  /// Versão da vistoria; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  ApproveInspectionRequest._();

  factory ApproveInspectionRequest([void updates(ApproveInspectionRequestBuilder b)]) = _$ApproveInspectionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ApproveInspectionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ApproveInspectionRequest> get serializer => _$ApproveInspectionRequestSerializer();
}

class _$ApproveInspectionRequestSerializer implements PrimitiveSerializer<ApproveInspectionRequest> {
  @override
  final Iterable<Type> types = const [ApproveInspectionRequest, _$ApproveInspectionRequest];

  @override
  final String wireName = r'ApproveInspectionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ApproveInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.mode != null) {
      yield r'mode';
      yield serializers.serialize(
        object.mode,
        specifiedType: const FullType(InspectionMode),
      );
    }
    if (object.notes != null) {
      yield r'notes';
      yield serializers.serialize(
        object.notes,
        specifiedType: const FullType(String),
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
    ApproveInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ApproveInspectionRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'mode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionMode),
          ) as InspectionMode;
          result.mode = valueDes;
          break;
        case r'notes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.notes = valueDes;
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
  ApproveInspectionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ApproveInspectionRequestBuilder();
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

