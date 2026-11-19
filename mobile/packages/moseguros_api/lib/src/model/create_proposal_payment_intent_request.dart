//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/proposal_payment_channel.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_proposal_payment_intent_request.g.dart';

/// CreateProposalPaymentIntentRequest
///
/// Properties:
/// * [channel] 
/// * [method] 
/// * [payerMsisdn] - Telemóvel moçambicano em E.164
@BuiltValue()
abstract class CreateProposalPaymentIntentRequest implements Built<CreateProposalPaymentIntentRequest, CreateProposalPaymentIntentRequestBuilder> {
  @BuiltValueField(wireName: r'channel')
  ProposalPaymentChannel get channel;
  // enum channelEnum {  WEB,  APP,  BROKER_LINK,  };

  @BuiltValueField(wireName: r'method')
  CreateProposalPaymentIntentRequestMethodEnum get method;
  // enum methodEnum {  MPESA,  BANK_REFERENCE,  };

  /// Telemóvel moçambicano em E.164
  @BuiltValueField(wireName: r'payerMsisdn')
  String? get payerMsisdn;

  CreateProposalPaymentIntentRequest._();

  factory CreateProposalPaymentIntentRequest([void updates(CreateProposalPaymentIntentRequestBuilder b)]) = _$CreateProposalPaymentIntentRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateProposalPaymentIntentRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateProposalPaymentIntentRequest> get serializer => _$CreateProposalPaymentIntentRequestSerializer();
}

class _$CreateProposalPaymentIntentRequestSerializer implements PrimitiveSerializer<CreateProposalPaymentIntentRequest> {
  @override
  final Iterable<Type> types = const [CreateProposalPaymentIntentRequest, _$CreateProposalPaymentIntentRequest];

  @override
  final String wireName = r'CreateProposalPaymentIntentRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateProposalPaymentIntentRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(ProposalPaymentChannel),
    );
    yield r'method';
    yield serializers.serialize(
      object.method,
      specifiedType: const FullType(CreateProposalPaymentIntentRequestMethodEnum),
    );
    if (object.payerMsisdn != null) {
      yield r'payerMsisdn';
      yield serializers.serialize(
        object.payerMsisdn,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateProposalPaymentIntentRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateProposalPaymentIntentRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalPaymentChannel),
          ) as ProposalPaymentChannel;
          result.channel = valueDes;
          break;
        case r'method':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateProposalPaymentIntentRequestMethodEnum),
          ) as CreateProposalPaymentIntentRequestMethodEnum;
          result.method = valueDes;
          break;
        case r'payerMsisdn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.payerMsisdn = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateProposalPaymentIntentRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateProposalPaymentIntentRequestBuilder();
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

class CreateProposalPaymentIntentRequestMethodEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MPESA')
  static const CreateProposalPaymentIntentRequestMethodEnum MPESA = _$createProposalPaymentIntentRequestMethodEnum_MPESA;
  @BuiltValueEnumConst(wireName: r'BANK_REFERENCE')
  static const CreateProposalPaymentIntentRequestMethodEnum BANK_REFERENCE = _$createProposalPaymentIntentRequestMethodEnum_BANK_REFERENCE;

  static Serializer<CreateProposalPaymentIntentRequestMethodEnum> get serializer => _$createProposalPaymentIntentRequestMethodEnumSerializer;

  const CreateProposalPaymentIntentRequestMethodEnum._(String name): super(name);

  static BuiltSet<CreateProposalPaymentIntentRequestMethodEnum> get values => _$createProposalPaymentIntentRequestMethodEnumValues;
  static CreateProposalPaymentIntentRequestMethodEnum valueOf(String name) => _$createProposalPaymentIntentRequestMethodEnumValueOf(name);
}

