//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_product_document.g.dart';

/// Condições Gerais e Informação Pré-contratual da versão, na língua do tomador (ou pt); descarregam-se em GET /api/v1/files/{fileId}
///
/// Properties:
/// * [fileId] 
/// * [type] 
@BuiltValue()
abstract class ProposalProductDocument implements Built<ProposalProductDocument, ProposalProductDocumentBuilder> {
  @BuiltValueField(wireName: r'fileId')
  String get fileId;

  @BuiltValueField(wireName: r'type')
  ProposalProductDocumentTypeEnum get type;
  // enum typeEnum {  GENERAL_CONDITIONS,  PRE_CONTRACT_INFO,  SPECIAL_CONDITIONS,  OTHER,  };

  ProposalProductDocument._();

  factory ProposalProductDocument([void updates(ProposalProductDocumentBuilder b)]) = _$ProposalProductDocument;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalProductDocumentBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalProductDocument> get serializer => _$ProposalProductDocumentSerializer();
}

class _$ProposalProductDocumentSerializer implements PrimitiveSerializer<ProposalProductDocument> {
  @override
  final Iterable<Type> types = const [ProposalProductDocument, _$ProposalProductDocument];

  @override
  final String wireName = r'ProposalProductDocument';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalProductDocument object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'fileId';
    yield serializers.serialize(
      object.fileId,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ProposalProductDocumentTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalProductDocument object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalProductDocumentBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileId = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalProductDocumentTypeEnum),
          ) as ProposalProductDocumentTypeEnum;
          result.type = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalProductDocument deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalProductDocumentBuilder();
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

class ProposalProductDocumentTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'GENERAL_CONDITIONS')
  static const ProposalProductDocumentTypeEnum GENERAL_CONDITIONS = _$proposalProductDocumentTypeEnum_GENERAL_CONDITIONS;
  @BuiltValueEnumConst(wireName: r'PRE_CONTRACT_INFO')
  static const ProposalProductDocumentTypeEnum PRE_CONTRACT_INFO = _$proposalProductDocumentTypeEnum_PRE_CONTRACT_INFO;
  @BuiltValueEnumConst(wireName: r'SPECIAL_CONDITIONS')
  static const ProposalProductDocumentTypeEnum SPECIAL_CONDITIONS = _$proposalProductDocumentTypeEnum_SPECIAL_CONDITIONS;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const ProposalProductDocumentTypeEnum OTHER = _$proposalProductDocumentTypeEnum_OTHER;

  static Serializer<ProposalProductDocumentTypeEnum> get serializer => _$proposalProductDocumentTypeEnumSerializer;

  const ProposalProductDocumentTypeEnum._(String name): super(name);

  static BuiltSet<ProposalProductDocumentTypeEnum> get values => _$proposalProductDocumentTypeEnumValues;
  static ProposalProductDocumentTypeEnum valueOf(String name) => _$proposalProductDocumentTypeEnumValueOf(name);
}

