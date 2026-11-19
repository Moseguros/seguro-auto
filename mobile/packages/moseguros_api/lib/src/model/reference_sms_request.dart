//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reference_sms_request.g.dart';

/// ReferenceSmsRequest
///
/// Properties:
/// * [msisdn] - Telemóvel moçambicano em E.164
@BuiltValue()
abstract class ReferenceSmsRequest implements Built<ReferenceSmsRequest, ReferenceSmsRequestBuilder> {
  /// Telemóvel moçambicano em E.164
  @BuiltValueField(wireName: r'msisdn')
  String get msisdn;

  ReferenceSmsRequest._();

  factory ReferenceSmsRequest([void updates(ReferenceSmsRequestBuilder b)]) = _$ReferenceSmsRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ReferenceSmsRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ReferenceSmsRequest> get serializer => _$ReferenceSmsRequestSerializer();
}

class _$ReferenceSmsRequestSerializer implements PrimitiveSerializer<ReferenceSmsRequest> {
  @override
  final Iterable<Type> types = const [ReferenceSmsRequest, _$ReferenceSmsRequest];

  @override
  final String wireName = r'ReferenceSmsRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ReferenceSmsRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'msisdn';
    yield serializers.serialize(
      object.msisdn,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ReferenceSmsRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ReferenceSmsRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'msisdn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.msisdn = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ReferenceSmsRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ReferenceSmsRequestBuilder();
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

