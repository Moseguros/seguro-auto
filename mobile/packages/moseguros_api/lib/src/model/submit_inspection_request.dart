//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'submit_inspection_request.g.dart';

/// SubmitInspectionRequest
///
/// Properties:
/// * [odometerKm] - Quilometragem; fora de 0–2 000 000 → OUT_OF_RANGE
/// * [version] - Versão da proposta; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class SubmitInspectionRequest implements Built<SubmitInspectionRequest, SubmitInspectionRequestBuilder> {
  /// Quilometragem; fora de 0–2 000 000 → OUT_OF_RANGE
  @BuiltValueField(wireName: r'odometerKm')
  int get odometerKm;

  /// Versão da proposta; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  SubmitInspectionRequest._();

  factory SubmitInspectionRequest([void updates(SubmitInspectionRequestBuilder b)]) = _$SubmitInspectionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SubmitInspectionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SubmitInspectionRequest> get serializer => _$SubmitInspectionRequestSerializer();
}

class _$SubmitInspectionRequestSerializer implements PrimitiveSerializer<SubmitInspectionRequest> {
  @override
  final Iterable<Type> types = const [SubmitInspectionRequest, _$SubmitInspectionRequest];

  @override
  final String wireName = r'SubmitInspectionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SubmitInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'odometerKm';
    yield serializers.serialize(
      object.odometerKm,
      specifiedType: const FullType(int),
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
    SubmitInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SubmitInspectionRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'odometerKm':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.odometerKm = valueDes;
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
  SubmitInspectionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SubmitInspectionRequestBuilder();
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

