//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_checklist.g.dart';

/// ProposalChecklist
///
/// Properties:
/// * [declarations] 
/// * [inspection] 
/// * [signature] 
/// * [suitability] 
@BuiltValue()
abstract class ProposalChecklist implements Built<ProposalChecklist, ProposalChecklistBuilder> {
  @BuiltValueField(wireName: r'declarations')
  ProposalChecklistDeclarationsEnum get declarations;
  // enum declarationsEnum {  DONE,  MISSING,  };

  @BuiltValueField(wireName: r'inspection')
  ProposalChecklistInspectionEnum get inspection;
  // enum inspectionEnum {  NOT_REQUIRED,  MISSING,  DONE,  REJECTED,  };

  @BuiltValueField(wireName: r'signature')
  ProposalChecklistSignatureEnum get signature;
  // enum signatureEnum {  MISSING,  DONE,  };

  @BuiltValueField(wireName: r'suitability')
  ProposalChecklistSuitabilityEnum get suitability;
  // enum suitabilityEnum {  DONE,  MISSING,  ACK_REQUIRED,  };

  ProposalChecklist._();

  factory ProposalChecklist([void updates(ProposalChecklistBuilder b)]) = _$ProposalChecklist;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalChecklistBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalChecklist> get serializer => _$ProposalChecklistSerializer();
}

class _$ProposalChecklistSerializer implements PrimitiveSerializer<ProposalChecklist> {
  @override
  final Iterable<Type> types = const [ProposalChecklist, _$ProposalChecklist];

  @override
  final String wireName = r'ProposalChecklist';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalChecklist object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'declarations';
    yield serializers.serialize(
      object.declarations,
      specifiedType: const FullType(ProposalChecklistDeclarationsEnum),
    );
    yield r'inspection';
    yield serializers.serialize(
      object.inspection,
      specifiedType: const FullType(ProposalChecklistInspectionEnum),
    );
    yield r'signature';
    yield serializers.serialize(
      object.signature,
      specifiedType: const FullType(ProposalChecklistSignatureEnum),
    );
    yield r'suitability';
    yield serializers.serialize(
      object.suitability,
      specifiedType: const FullType(ProposalChecklistSuitabilityEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalChecklist object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalChecklistBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'declarations':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChecklistDeclarationsEnum),
          ) as ProposalChecklistDeclarationsEnum;
          result.declarations = valueDes;
          break;
        case r'inspection':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChecklistInspectionEnum),
          ) as ProposalChecklistInspectionEnum;
          result.inspection = valueDes;
          break;
        case r'signature':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChecklistSignatureEnum),
          ) as ProposalChecklistSignatureEnum;
          result.signature = valueDes;
          break;
        case r'suitability':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChecklistSuitabilityEnum),
          ) as ProposalChecklistSuitabilityEnum;
          result.suitability = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalChecklist deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalChecklistBuilder();
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

class ProposalChecklistDeclarationsEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DONE')
  static const ProposalChecklistDeclarationsEnum DONE = _$proposalChecklistDeclarationsEnum_DONE;
  @BuiltValueEnumConst(wireName: r'MISSING')
  static const ProposalChecklistDeclarationsEnum MISSING = _$proposalChecklistDeclarationsEnum_MISSING;

  static Serializer<ProposalChecklistDeclarationsEnum> get serializer => _$proposalChecklistDeclarationsEnumSerializer;

  const ProposalChecklistDeclarationsEnum._(String name): super(name);

  static BuiltSet<ProposalChecklistDeclarationsEnum> get values => _$proposalChecklistDeclarationsEnumValues;
  static ProposalChecklistDeclarationsEnum valueOf(String name) => _$proposalChecklistDeclarationsEnumValueOf(name);
}

class ProposalChecklistInspectionEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NOT_REQUIRED')
  static const ProposalChecklistInspectionEnum NOT_REQUIRED = _$proposalChecklistInspectionEnum_NOT_REQUIRED;
  @BuiltValueEnumConst(wireName: r'MISSING')
  static const ProposalChecklistInspectionEnum MISSING = _$proposalChecklistInspectionEnum_MISSING;
  @BuiltValueEnumConst(wireName: r'DONE')
  static const ProposalChecklistInspectionEnum DONE = _$proposalChecklistInspectionEnum_DONE;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const ProposalChecklistInspectionEnum REJECTED = _$proposalChecklistInspectionEnum_REJECTED;

  static Serializer<ProposalChecklistInspectionEnum> get serializer => _$proposalChecklistInspectionEnumSerializer;

  const ProposalChecklistInspectionEnum._(String name): super(name);

  static BuiltSet<ProposalChecklistInspectionEnum> get values => _$proposalChecklistInspectionEnumValues;
  static ProposalChecklistInspectionEnum valueOf(String name) => _$proposalChecklistInspectionEnumValueOf(name);
}

class ProposalChecklistSignatureEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MISSING')
  static const ProposalChecklistSignatureEnum MISSING = _$proposalChecklistSignatureEnum_MISSING;
  @BuiltValueEnumConst(wireName: r'DONE')
  static const ProposalChecklistSignatureEnum DONE = _$proposalChecklistSignatureEnum_DONE;

  static Serializer<ProposalChecklistSignatureEnum> get serializer => _$proposalChecklistSignatureEnumSerializer;

  const ProposalChecklistSignatureEnum._(String name): super(name);

  static BuiltSet<ProposalChecklistSignatureEnum> get values => _$proposalChecklistSignatureEnumValues;
  static ProposalChecklistSignatureEnum valueOf(String name) => _$proposalChecklistSignatureEnumValueOf(name);
}

class ProposalChecklistSuitabilityEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DONE')
  static const ProposalChecklistSuitabilityEnum DONE = _$proposalChecklistSuitabilityEnum_DONE;
  @BuiltValueEnumConst(wireName: r'MISSING')
  static const ProposalChecklistSuitabilityEnum MISSING = _$proposalChecklistSuitabilityEnum_MISSING;
  @BuiltValueEnumConst(wireName: r'ACK_REQUIRED')
  static const ProposalChecklistSuitabilityEnum ACK_REQUIRED = _$proposalChecklistSuitabilityEnum_ACK_REQUIRED;

  static Serializer<ProposalChecklistSuitabilityEnum> get serializer => _$proposalChecklistSuitabilityEnumSerializer;

  const ProposalChecklistSuitabilityEnum._(String name): super(name);

  static BuiltSet<ProposalChecklistSuitabilityEnum> get values => _$proposalChecklistSuitabilityEnumValues;
  static ProposalChecklistSuitabilityEnum valueOf(String name) => _$proposalChecklistSuitabilityEnumValueOf(name);
}

