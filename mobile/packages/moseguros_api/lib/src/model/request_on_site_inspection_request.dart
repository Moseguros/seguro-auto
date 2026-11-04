//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'request_on_site_inspection_request.g.dart';

/// RequestOnSiteInspectionRequest
///
/// Properties:
/// * [notes] - Local e horas preferidas (REQUIRED, TOO_SHORT, TOO_LONG)
/// * [version] - Versão da proposta; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class RequestOnSiteInspectionRequest implements Built<RequestOnSiteInspectionRequest, RequestOnSiteInspectionRequestBuilder> {
  /// Local e horas preferidas (REQUIRED, TOO_SHORT, TOO_LONG)
  @BuiltValueField(wireName: r'notes')
  String get notes;

  /// Versão da proposta; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  RequestOnSiteInspectionRequest._();

  factory RequestOnSiteInspectionRequest([void updates(RequestOnSiteInspectionRequestBuilder b)]) = _$RequestOnSiteInspectionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RequestOnSiteInspectionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RequestOnSiteInspectionRequest> get serializer => _$RequestOnSiteInspectionRequestSerializer();
}

class _$RequestOnSiteInspectionRequestSerializer implements PrimitiveSerializer<RequestOnSiteInspectionRequest> {
  @override
  final Iterable<Type> types = const [RequestOnSiteInspectionRequest, _$RequestOnSiteInspectionRequest];

  @override
  final String wireName = r'RequestOnSiteInspectionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RequestOnSiteInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'notes';
    yield serializers.serialize(
      object.notes,
      specifiedType: const FullType(String),
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
    RequestOnSiteInspectionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RequestOnSiteInspectionRequestBuilder result,
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
  RequestOnSiteInspectionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RequestOnSiteInspectionRequestBuilder();
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

