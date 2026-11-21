//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/policy_document_type.dart';
import 'package:moseguros_api/src/model/policy_document_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_document.g.dart';

/// Documento da apólice; PENDING (sem fileId) enquanto não foi gerado
///
/// Properties:
/// * [fileId] 
/// * [fileName] 
/// * [status] 
/// * [type] 
@BuiltValue()
abstract class PolicyDocument implements Built<PolicyDocument, PolicyDocumentBuilder> {
  @BuiltValueField(wireName: r'fileId')
  String? get fileId;

  @BuiltValueField(wireName: r'fileName')
  String? get fileName;

  @BuiltValueField(wireName: r'status')
  PolicyDocumentStatus get status;
  // enum statusEnum {  READY,  PENDING,  };

  @BuiltValueField(wireName: r'type')
  PolicyDocumentType get type;
  // enum typeEnum {  CERTIFICATE,  SCHEDULE,  GENERAL_CONDITIONS,  PRE_CONTRACT_INFO,  PROPOSAL,  };

  PolicyDocument._();

  factory PolicyDocument([void updates(PolicyDocumentBuilder b)]) = _$PolicyDocument;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PolicyDocumentBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PolicyDocument> get serializer => _$PolicyDocumentSerializer();
}

class _$PolicyDocumentSerializer implements PrimitiveSerializer<PolicyDocument> {
  @override
  final Iterable<Type> types = const [PolicyDocument, _$PolicyDocument];

  @override
  final String wireName = r'PolicyDocument';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PolicyDocument object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.fileId != null) {
      yield r'fileId';
      yield serializers.serialize(
        object.fileId,
        specifiedType: const FullType(String),
      );
    }
    if (object.fileName != null) {
      yield r'fileName';
      yield serializers.serialize(
        object.fileName,
        specifiedType: const FullType(String),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PolicyDocumentStatus),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PolicyDocumentType),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PolicyDocument object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PolicyDocumentBuilder result,
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
        case r'fileName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileName = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyDocumentStatus),
          ) as PolicyDocumentStatus;
          result.status = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyDocumentType),
          ) as PolicyDocumentType;
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
  PolicyDocument deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PolicyDocumentBuilder();
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

