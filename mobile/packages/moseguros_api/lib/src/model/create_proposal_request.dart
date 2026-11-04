//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/proposal_channel.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_proposal_request.g.dart';

/// CreateProposalRequest
///
/// Properties:
/// * [channel] 
/// * [quoteId] - Cotação OFFERED do próprio cliente
@BuiltValue()
abstract class CreateProposalRequest implements Built<CreateProposalRequest, CreateProposalRequestBuilder> {
  @BuiltValueField(wireName: r'channel')
  ProposalChannel get channel;
  // enum channelEnum {  APP,  WEB,  BROKER,  };

  /// Cotação OFFERED do próprio cliente
  @BuiltValueField(wireName: r'quoteId')
  String get quoteId;

  CreateProposalRequest._();

  factory CreateProposalRequest([void updates(CreateProposalRequestBuilder b)]) = _$CreateProposalRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateProposalRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateProposalRequest> get serializer => _$CreateProposalRequestSerializer();
}

class _$CreateProposalRequestSerializer implements PrimitiveSerializer<CreateProposalRequest> {
  @override
  final Iterable<Type> types = const [CreateProposalRequest, _$CreateProposalRequest];

  @override
  final String wireName = r'CreateProposalRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(ProposalChannel),
    );
    yield r'quoteId';
    yield serializers.serialize(
      object.quoteId,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateProposalRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChannel),
          ) as ProposalChannel;
          result.channel = valueDes;
          break;
        case r'quoteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.quoteId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateProposalRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateProposalRequestBuilder();
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

