//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'request_signature_request.g.dart';

/// RequestSignatureRequest
///
/// Properties:
/// * [channel] - Canal escolhido (se as definições o permitirem); omitido = o canal por defeito
/// * [documentsAcknowledged] - O tomador leu as Condições Gerais e a Informação Pré-contratual (tem de ser true)
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class RequestSignatureRequest implements Built<RequestSignatureRequest, RequestSignatureRequestBuilder> {
  /// Canal escolhido (se as definições o permitirem); omitido = o canal por defeito
  @BuiltValueField(wireName: r'channel')
  RequestSignatureRequestChannelEnum? get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  /// O tomador leu as Condições Gerais e a Informação Pré-contratual (tem de ser true)
  @BuiltValueField(wireName: r'documentsAcknowledged')
  bool get documentsAcknowledged;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  RequestSignatureRequest._();

  factory RequestSignatureRequest([void updates(RequestSignatureRequestBuilder b)]) = _$RequestSignatureRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RequestSignatureRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RequestSignatureRequest> get serializer => _$RequestSignatureRequestSerializer();
}

class _$RequestSignatureRequestSerializer implements PrimitiveSerializer<RequestSignatureRequest> {
  @override
  final Iterable<Type> types = const [RequestSignatureRequest, _$RequestSignatureRequest];

  @override
  final String wireName = r'RequestSignatureRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RequestSignatureRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.channel != null) {
      yield r'channel';
      yield serializers.serialize(
        object.channel,
        specifiedType: const FullType(RequestSignatureRequestChannelEnum),
      );
    }
    yield r'documentsAcknowledged';
    yield serializers.serialize(
      object.documentsAcknowledged,
      specifiedType: const FullType(bool),
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
    RequestSignatureRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RequestSignatureRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RequestSignatureRequestChannelEnum),
          ) as RequestSignatureRequestChannelEnum;
          result.channel = valueDes;
          break;
        case r'documentsAcknowledged':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.documentsAcknowledged = valueDes;
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
  RequestSignatureRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RequestSignatureRequestBuilder();
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

class RequestSignatureRequestChannelEnum extends EnumClass {

  /// Canal escolhido (se as definições o permitirem); omitido = o canal por defeito
  @BuiltValueEnumConst(wireName: r'SMS')
  static const RequestSignatureRequestChannelEnum SMS = _$requestSignatureRequestChannelEnum_SMS;
  /// Canal escolhido (se as definições o permitirem); omitido = o canal por defeito
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const RequestSignatureRequestChannelEnum WHATSAPP = _$requestSignatureRequestChannelEnum_WHATSAPP;
  /// Canal escolhido (se as definições o permitirem); omitido = o canal por defeito
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const RequestSignatureRequestChannelEnum EMAIL = _$requestSignatureRequestChannelEnum_EMAIL;

  static Serializer<RequestSignatureRequestChannelEnum> get serializer => _$requestSignatureRequestChannelEnumSerializer;

  const RequestSignatureRequestChannelEnum._(String name): super(name);

  static BuiltSet<RequestSignatureRequestChannelEnum> get values => _$requestSignatureRequestChannelEnumValues;
  static RequestSignatureRequestChannelEnum valueOf(String name) => _$requestSignatureRequestChannelEnumValueOf(name);
}

