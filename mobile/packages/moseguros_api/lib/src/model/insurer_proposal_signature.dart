//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_proposal_signature.g.dart';

/// Todas as assinaturas (válida e invalidadas), as mais recentes primeiro
///
/// Properties:
/// * [assisted] 
/// * [channel] 
/// * [destinationMasked] 
/// * [documentFileId] 
/// * [documentHash] 
/// * [invalidatedAt] 
/// * [invalidatedReason] 
/// * [signedAt] 
@BuiltValue()
abstract class InsurerProposalSignature implements Built<InsurerProposalSignature, InsurerProposalSignatureBuilder> {
  @BuiltValueField(wireName: r'assisted')
  bool get assisted;

  @BuiltValueField(wireName: r'channel')
  InsurerProposalSignatureChannelEnum get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'destinationMasked')
  String get destinationMasked;

  @BuiltValueField(wireName: r'documentFileId')
  String get documentFileId;

  @BuiltValueField(wireName: r'documentHash')
  String get documentHash;

  @BuiltValueField(wireName: r'invalidatedAt')
  DateTime? get invalidatedAt;

  @BuiltValueField(wireName: r'invalidatedReason')
  InsurerProposalSignatureInvalidatedReasonEnum? get invalidatedReason;
  // enum invalidatedReasonEnum {  CONTENT_CHANGED,  INFO_REQUESTED,  };

  @BuiltValueField(wireName: r'signedAt')
  DateTime get signedAt;

  InsurerProposalSignature._();

  factory InsurerProposalSignature([void updates(InsurerProposalSignatureBuilder b)]) = _$InsurerProposalSignature;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerProposalSignatureBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerProposalSignature> get serializer => _$InsurerProposalSignatureSerializer();
}

class _$InsurerProposalSignatureSerializer implements PrimitiveSerializer<InsurerProposalSignature> {
  @override
  final Iterable<Type> types = const [InsurerProposalSignature, _$InsurerProposalSignature];

  @override
  final String wireName = r'InsurerProposalSignature';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerProposalSignature object, {
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
      specifiedType: const FullType(InsurerProposalSignatureChannelEnum),
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
    if (object.invalidatedAt != null) {
      yield r'invalidatedAt';
      yield serializers.serialize(
        object.invalidatedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.invalidatedReason != null) {
      yield r'invalidatedReason';
      yield serializers.serialize(
        object.invalidatedReason,
        specifiedType: const FullType.nullable(InsurerProposalSignatureInvalidatedReasonEnum),
      );
    }
    yield r'signedAt';
    yield serializers.serialize(
      object.signedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerProposalSignature object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerProposalSignatureBuilder result,
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
            specifiedType: const FullType(InsurerProposalSignatureChannelEnum),
          ) as InsurerProposalSignatureChannelEnum;
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
        case r'invalidatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.invalidatedAt = valueDes;
          break;
        case r'invalidatedReason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(InsurerProposalSignatureInvalidatedReasonEnum),
          ) as InsurerProposalSignatureInvalidatedReasonEnum?;
          if (valueDes == null) continue;
          result.invalidatedReason = valueDes;
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
  InsurerProposalSignature deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerProposalSignatureBuilder();
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

class InsurerProposalSignatureChannelEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const InsurerProposalSignatureChannelEnum SMS = _$insurerProposalSignatureChannelEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const InsurerProposalSignatureChannelEnum WHATSAPP = _$insurerProposalSignatureChannelEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const InsurerProposalSignatureChannelEnum EMAIL = _$insurerProposalSignatureChannelEnum_EMAIL;

  static Serializer<InsurerProposalSignatureChannelEnum> get serializer => _$insurerProposalSignatureChannelEnumSerializer;

  const InsurerProposalSignatureChannelEnum._(String name): super(name);

  static BuiltSet<InsurerProposalSignatureChannelEnum> get values => _$insurerProposalSignatureChannelEnumValues;
  static InsurerProposalSignatureChannelEnum valueOf(String name) => _$insurerProposalSignatureChannelEnumValueOf(name);
}

class InsurerProposalSignatureInvalidatedReasonEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CONTENT_CHANGED')
  static const InsurerProposalSignatureInvalidatedReasonEnum CONTENT_CHANGED = _$insurerProposalSignatureInvalidatedReasonEnum_CONTENT_CHANGED;
  @BuiltValueEnumConst(wireName: r'INFO_REQUESTED')
  static const InsurerProposalSignatureInvalidatedReasonEnum INFO_REQUESTED = _$insurerProposalSignatureInvalidatedReasonEnum_INFO_REQUESTED;

  static Serializer<InsurerProposalSignatureInvalidatedReasonEnum> get serializer => _$insurerProposalSignatureInvalidatedReasonEnumSerializer;

  const InsurerProposalSignatureInvalidatedReasonEnum._(String name): super(name);

  static BuiltSet<InsurerProposalSignatureInvalidatedReasonEnum> get values => _$insurerProposalSignatureInvalidatedReasonEnumValues;
  static InsurerProposalSignatureInvalidatedReasonEnum valueOf(String name) => _$insurerProposalSignatureInvalidatedReasonEnumValueOf(name);
}

