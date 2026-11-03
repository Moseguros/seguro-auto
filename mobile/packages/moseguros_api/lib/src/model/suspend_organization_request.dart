//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'suspend_organization_request.g.dart';

/// SuspendOrganizationRequest
///
/// Properties:
/// * [reason] 
@BuiltValue()
abstract class SuspendOrganizationRequest implements Built<SuspendOrganizationRequest, SuspendOrganizationRequestBuilder> {
  @BuiltValueField(wireName: r'reason')
  String get reason;

  SuspendOrganizationRequest._();

  factory SuspendOrganizationRequest([void updates(SuspendOrganizationRequestBuilder b)]) = _$SuspendOrganizationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SuspendOrganizationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SuspendOrganizationRequest> get serializer => _$SuspendOrganizationRequestSerializer();
}

class _$SuspendOrganizationRequestSerializer implements PrimitiveSerializer<SuspendOrganizationRequest> {
  @override
  final Iterable<Type> types = const [SuspendOrganizationRequest, _$SuspendOrganizationRequest];

  @override
  final String wireName = r'SuspendOrganizationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SuspendOrganizationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'reason';
    yield serializers.serialize(
      object.reason,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SuspendOrganizationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SuspendOrganizationRequestBuilder result,
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SuspendOrganizationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SuspendOrganizationRequestBuilder();
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

