//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'verify_kyc_check_request.g.dart';

/// VerifyKycCheckRequest
///
/// Properties:
/// * [version] 
@BuiltValue()
abstract class VerifyKycCheckRequest implements Built<VerifyKycCheckRequest, VerifyKycCheckRequestBuilder> {
  @BuiltValueField(wireName: r'version')
  int get version;

  VerifyKycCheckRequest._();

  factory VerifyKycCheckRequest([void updates(VerifyKycCheckRequestBuilder b)]) = _$VerifyKycCheckRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VerifyKycCheckRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VerifyKycCheckRequest> get serializer => _$VerifyKycCheckRequestSerializer();
}

class _$VerifyKycCheckRequestSerializer implements PrimitiveSerializer<VerifyKycCheckRequest> {
  @override
  final Iterable<Type> types = const [VerifyKycCheckRequest, _$VerifyKycCheckRequest];

  @override
  final String wireName = r'VerifyKycCheckRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VerifyKycCheckRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    VerifyKycCheckRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VerifyKycCheckRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
  VerifyKycCheckRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VerifyKycCheckRequestBuilder();
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

