//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'me_organization.g.dart';

/// Organização do staff; omitida para clientes e staff da plataforma
///
/// Properties:
/// * [displayName] 
/// * [id] 
/// * [type] 
@BuiltValue()
abstract class MeOrganization implements Built<MeOrganization, MeOrganizationBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'type')
  MeOrganizationTypeEnum get type;
  // enum typeEnum {  INSURER,  BROKER,  AGENT,  WORKSHOP,  ASSESSOR,  ASSISTANCE,  OMBUDSMAN,  REGULATOR,  POLICE,  CORPORATE_CLIENT,  };

  MeOrganization._();

  factory MeOrganization([void updates(MeOrganizationBuilder b)]) = _$MeOrganization;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(MeOrganizationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<MeOrganization> get serializer => _$MeOrganizationSerializer();
}

class _$MeOrganizationSerializer implements PrimitiveSerializer<MeOrganization> {
  @override
  final Iterable<Type> types = const [MeOrganization, _$MeOrganization];

  @override
  final String wireName = r'MeOrganization';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    MeOrganization object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'displayName';
    yield serializers.serialize(
      object.displayName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(MeOrganizationTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    MeOrganization object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required MeOrganizationBuilder result,
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
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(MeOrganizationTypeEnum),
          ) as MeOrganizationTypeEnum;
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
  MeOrganization deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = MeOrganizationBuilder();
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

class MeOrganizationTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INSURER')
  static const MeOrganizationTypeEnum INSURER = _$meOrganizationTypeEnum_INSURER;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const MeOrganizationTypeEnum BROKER = _$meOrganizationTypeEnum_BROKER;
  @BuiltValueEnumConst(wireName: r'AGENT')
  static const MeOrganizationTypeEnum AGENT = _$meOrganizationTypeEnum_AGENT;
  @BuiltValueEnumConst(wireName: r'WORKSHOP')
  static const MeOrganizationTypeEnum WORKSHOP = _$meOrganizationTypeEnum_WORKSHOP;
  @BuiltValueEnumConst(wireName: r'ASSESSOR')
  static const MeOrganizationTypeEnum ASSESSOR = _$meOrganizationTypeEnum_ASSESSOR;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const MeOrganizationTypeEnum ASSISTANCE = _$meOrganizationTypeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'OMBUDSMAN')
  static const MeOrganizationTypeEnum OMBUDSMAN = _$meOrganizationTypeEnum_OMBUDSMAN;
  @BuiltValueEnumConst(wireName: r'REGULATOR')
  static const MeOrganizationTypeEnum REGULATOR = _$meOrganizationTypeEnum_REGULATOR;
  @BuiltValueEnumConst(wireName: r'POLICE')
  static const MeOrganizationTypeEnum POLICE = _$meOrganizationTypeEnum_POLICE;
  @BuiltValueEnumConst(wireName: r'CORPORATE_CLIENT')
  static const MeOrganizationTypeEnum CORPORATE_CLIENT = _$meOrganizationTypeEnum_CORPORATE_CLIENT;

  static Serializer<MeOrganizationTypeEnum> get serializer => _$meOrganizationTypeEnumSerializer;

  const MeOrganizationTypeEnum._(String name): super(name);

  static BuiltSet<MeOrganizationTypeEnum> get values => _$meOrganizationTypeEnumValues;
  static MeOrganizationTypeEnum valueOf(String name) => _$meOrganizationTypeEnumValueOf(name);
}

