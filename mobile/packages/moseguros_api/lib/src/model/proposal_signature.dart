//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_signature.g.dart';

/// Assinatura válida (S4-04); null sem ela ou depois de uma alteração
///
/// Properties:
/// * [assisted] - Assinatura com a ajuda de um mediador (S4-06)
/// * [channel] 
/// * [destinationMasked] 
/// * [documentFileId] 
/// * [documentHash] - SHA-256 do PDF, hexadecimal minúsculo
/// * [signedAt] 
@BuiltValue()
abstract class ProposalSignature implements Built<ProposalSignature, ProposalSignatureBuilder> {
  /// Assinatura com a ajuda de um mediador (S4-06)
  @BuiltValueField(wireName: r'assisted')
  bool get assisted;

  @BuiltValueField(wireName: r'channel')
  ProposalSignatureChannelEnum get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'destinationMasked')
  String get destinationMasked;

  @BuiltValueField(wireName: r'documentFileId')
  String get documentFileId;

  /// SHA-256 do PDF, hexadecimal minúsculo
  @BuiltValueField(wireName: r'documentHash')
  String get documentHash;

  @BuiltValueField(wireName: r'signedAt')
  DateTime get signedAt;

  ProposalSignature._();

  factory ProposalSignature([void updates(ProposalSignatureBuilder b)]) = _$ProposalSignature;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalSignatureBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalSignature> get serializer => _$ProposalSignatureSerializer();
}

class _$ProposalSignatureSerializer implements PrimitiveSerializer<ProposalSignature> {
  @override
  final Iterable<Type> types = const [ProposalSignature, _$ProposalSignature];

  @override
  final String wireName = r'ProposalSignature';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalSignature object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'assisted';
    yield serializers.serialize(
      object.assisted,
      specifiedType: const FullType(bool),
    );
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(ProposalSignatureChannelEnum),
    );
    yield r'destinationMasked';
    yield serializers.serialize(
      object.destinationMasked,
      specifiedType: const FullType(String),
    );
    yield r'documentFileId';
    yield serializers.serialize(
      object.documentFileId,
      specifiedType: const FullType(String),
    );
    yield r'documentHash';
    yield serializers.serialize(
      object.documentHash,
      specifiedType: const FullType(String),
    );
    yield r'signedAt';
    yield serializers.serialize(
      object.signedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalSignature object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalSignatureBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'assisted':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.assisted = valueDes;
          break;
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalSignatureChannelEnum),
          ) as ProposalSignatureChannelEnum;
          result.channel = valueDes;
          break;
        case r'destinationMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.destinationMasked = valueDes;
          break;
        case r'documentFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.documentFileId = valueDes;
          break;
        case r'documentHash':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.documentHash = valueDes;
          break;
        case r'signedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.signedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalSignature deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalSignatureBuilder();
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

class ProposalSignatureChannelEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const ProposalSignatureChannelEnum SMS = _$proposalSignatureChannelEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const ProposalSignatureChannelEnum WHATSAPP = _$proposalSignatureChannelEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const ProposalSignatureChannelEnum EMAIL = _$proposalSignatureChannelEnum_EMAIL;

  static Serializer<ProposalSignatureChannelEnum> get serializer => _$proposalSignatureChannelEnumSerializer;

  const ProposalSignatureChannelEnum._(String name): super(name);

  static BuiltSet<ProposalSignatureChannelEnum> get values => _$proposalSignatureChannelEnumValues;
  static ProposalSignatureChannelEnum valueOf(String name) => _$proposalSignatureChannelEnumValueOf(name);
}

