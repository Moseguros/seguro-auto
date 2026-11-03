//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'invitation_organization.g.dart';

/// Omitida nos convites da plataforma
///
/// Properties:
/// * [displayName] 
/// * [type] 
@BuiltValue()
abstract class InvitationOrganization implements Built<InvitationOrganization, InvitationOrganizationBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'type')
  InvitationOrganizationTypeEnum get type;
  // enum typeEnum {  INSURER,  BROKER,  AGENT,  WORKSHOP,  ASSESSOR,  ASSISTANCE,  OMBUDSMAN,  REGULATOR,  POLICE,  CORPORATE_CLIENT,  };

  InvitationOrganization._();

  factory InvitationOrganization([void updates(InvitationOrganizationBuilder b)]) = _$InvitationOrganization;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InvitationOrganizationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InvitationOrganization> get serializer => _$InvitationOrganizationSerializer();
}

class _$InvitationOrganizationSerializer implements PrimitiveSerializer<InvitationOrganization> {
  @override
  final Iterable<Type> types = const [InvitationOrganization, _$InvitationOrganization];

  @override
  final String wireName = r'InvitationOrganization';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InvitationOrganization object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'displayName';
    yield serializers.serialize(
      object.displayName,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(InvitationOrganizationTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InvitationOrganization object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InvitationOrganizationBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayName = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InvitationOrganizationTypeEnum),
          ) as InvitationOrganizationTypeEnum;
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
  InvitationOrganization deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InvitationOrganizationBuilder();
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

class InvitationOrganizationTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INSURER')
  static const InvitationOrganizationTypeEnum INSURER = _$invitationOrganizationTypeEnum_INSURER;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const InvitationOrganizationTypeEnum BROKER = _$invitationOrganizationTypeEnum_BROKER;
  @BuiltValueEnumConst(wireName: r'AGENT')
  static const InvitationOrganizationTypeEnum AGENT = _$invitationOrganizationTypeEnum_AGENT;
  @BuiltValueEnumConst(wireName: r'WORKSHOP')
  static const InvitationOrganizationTypeEnum WORKSHOP = _$invitationOrganizationTypeEnum_WORKSHOP;
  @BuiltValueEnumConst(wireName: r'ASSESSOR')
  static const InvitationOrganizationTypeEnum ASSESSOR = _$invitationOrganizationTypeEnum_ASSESSOR;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const InvitationOrganizationTypeEnum ASSISTANCE = _$invitationOrganizationTypeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'OMBUDSMAN')
  static const InvitationOrganizationTypeEnum OMBUDSMAN = _$invitationOrganizationTypeEnum_OMBUDSMAN;
  @BuiltValueEnumConst(wireName: r'REGULATOR')
  static const InvitationOrganizationTypeEnum REGULATOR = _$invitationOrganizationTypeEnum_REGULATOR;
  @BuiltValueEnumConst(wireName: r'POLICE')
  static const InvitationOrganizationTypeEnum POLICE = _$invitationOrganizationTypeEnum_POLICE;
  @BuiltValueEnumConst(wireName: r'CORPORATE_CLIENT')
  static const InvitationOrganizationTypeEnum CORPORATE_CLIENT = _$invitationOrganizationTypeEnum_CORPORATE_CLIENT;

  static Serializer<InvitationOrganizationTypeEnum> get serializer => _$invitationOrganizationTypeEnumSerializer;

  const InvitationOrganizationTypeEnum._(String name): super(name);

  static BuiltSet<InvitationOrganizationTypeEnum> get values => _$invitationOrganizationTypeEnumValues;
  static InvitationOrganizationTypeEnum valueOf(String name) => _$invitationOrganizationTypeEnumValueOf(name);
}

