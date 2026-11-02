//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/insurer_profile_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'organization_response.g.dart';

/// OrganizationResponse
///
/// Properties:
/// * [address] 
/// * [contactEmail] 
/// * [contactPhone] 
/// * [createdAt] 
/// * [displayName] - tradeName, se existir, senão legalName
/// * [id] 
/// * [insurerProfile] 
/// * [legalName] 
/// * [licenseNumber] 
/// * [nuit] - NUIT: 9 dígitos
/// * [province] 
/// * [status] 
/// * [tradeName] 
/// * [type] 
/// * [updatedAt] 
/// * [version] 
@BuiltValue()
abstract class OrganizationResponse implements Built<OrganizationResponse, OrganizationResponseBuilder> {
  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'contactEmail')
  String get contactEmail;

  @BuiltValueField(wireName: r'contactPhone')
  String? get contactPhone;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  /// tradeName, se existir, senão legalName
  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'insurerProfile')
  InsurerProfileResponse? get insurerProfile;

  @BuiltValueField(wireName: r'legalName')
  String get legalName;

  @BuiltValueField(wireName: r'licenseNumber')
  String? get licenseNumber;

  /// NUIT: 9 dígitos
  @BuiltValueField(wireName: r'nuit')
  String get nuit;

  @BuiltValueField(wireName: r'province')
  OrganizationResponseProvinceEnum? get province;
  // enum provinceEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  @BuiltValueField(wireName: r'status')
  OrganizationResponseStatusEnum get status;
  // enum statusEnum {  ACTIVE,  SUSPENDED,  };

  @BuiltValueField(wireName: r'tradeName')
  String? get tradeName;

  @BuiltValueField(wireName: r'type')
  OrganizationResponseTypeEnum get type;
  // enum typeEnum {  INSURER,  BROKER,  AGENT,  WORKSHOP,  ASSESSOR,  ASSISTANCE,  OMBUDSMAN,  REGULATOR,  POLICE,  CORPORATE_CLIENT,  };

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'version')
  int get version;

  OrganizationResponse._();

  factory OrganizationResponse([void updates(OrganizationResponseBuilder b)]) = _$OrganizationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OrganizationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OrganizationResponse> get serializer => _$OrganizationResponseSerializer();
}

class _$OrganizationResponseSerializer implements PrimitiveSerializer<OrganizationResponse> {
  @override
  final Iterable<Type> types = const [OrganizationResponse, _$OrganizationResponse];

  @override
  final String wireName = r'OrganizationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OrganizationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.address != null) {
      yield r'address';
      yield serializers.serialize(
        object.address,
        specifiedType: const FullType(String),
      );
    }
    yield r'contactEmail';
    yield serializers.serialize(
      object.contactEmail,
      specifiedType: const FullType(String),
    );
    if (object.contactPhone != null) {
      yield r'contactPhone';
      yield serializers.serialize(
        object.contactPhone,
        specifiedType: const FullType(String),
      );
    }
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
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
    if (object.insurerProfile != null) {
      yield r'insurerProfile';
      yield serializers.serialize(
        object.insurerProfile,
        specifiedType: const FullType(InsurerProfileResponse),
      );
    }
    yield r'legalName';
    yield serializers.serialize(
      object.legalName,
      specifiedType: const FullType(String),
    );
    if (object.licenseNumber != null) {
      yield r'licenseNumber';
      yield serializers.serialize(
        object.licenseNumber,
        specifiedType: const FullType(String),
      );
    }
    yield r'nuit';
    yield serializers.serialize(
      object.nuit,
      specifiedType: const FullType(String),
    );
    if (object.province != null) {
      yield r'province';
      yield serializers.serialize(
        object.province,
        specifiedType: const FullType(OrganizationResponseProvinceEnum),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(OrganizationResponseStatusEnum),
    );
    if (object.tradeName != null) {
      yield r'tradeName';
      yield serializers.serialize(
        object.tradeName,
        specifiedType: const FullType(String),
      );
    }
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(OrganizationResponseTypeEnum),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
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
    OrganizationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OrganizationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'contactEmail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.contactEmail = valueDes;
          break;
        case r'contactPhone':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.contactPhone = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
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
        case r'insurerProfile':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InsurerProfileResponse),
          ) as InsurerProfileResponse;
          result.insurerProfile.replace(valueDes);
          break;
        case r'legalName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.legalName = valueDes;
          break;
        case r'licenseNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.licenseNumber = valueDes;
          break;
        case r'nuit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.nuit = valueDes;
          break;
        case r'province':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OrganizationResponseProvinceEnum),
          ) as OrganizationResponseProvinceEnum;
          result.province = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OrganizationResponseStatusEnum),
          ) as OrganizationResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'tradeName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.tradeName = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OrganizationResponseTypeEnum),
          ) as OrganizationResponseTypeEnum;
          result.type = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
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
  OrganizationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OrganizationResponseBuilder();
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

class OrganizationResponseProvinceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const OrganizationResponseProvinceEnum MAPUTO_CITY = _$organizationResponseProvinceEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const OrganizationResponseProvinceEnum MAPUTO = _$organizationResponseProvinceEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const OrganizationResponseProvinceEnum GAZA = _$organizationResponseProvinceEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const OrganizationResponseProvinceEnum INHAMBANE = _$organizationResponseProvinceEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const OrganizationResponseProvinceEnum SOFALA = _$organizationResponseProvinceEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const OrganizationResponseProvinceEnum MANICA = _$organizationResponseProvinceEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const OrganizationResponseProvinceEnum TETE = _$organizationResponseProvinceEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const OrganizationResponseProvinceEnum ZAMBEZIA = _$organizationResponseProvinceEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const OrganizationResponseProvinceEnum NAMPULA = _$organizationResponseProvinceEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const OrganizationResponseProvinceEnum CABO_DELGADO = _$organizationResponseProvinceEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const OrganizationResponseProvinceEnum NIASSA = _$organizationResponseProvinceEnum_NIASSA;

  static Serializer<OrganizationResponseProvinceEnum> get serializer => _$organizationResponseProvinceEnumSerializer;

  const OrganizationResponseProvinceEnum._(String name): super(name);

  static BuiltSet<OrganizationResponseProvinceEnum> get values => _$organizationResponseProvinceEnumValues;
  static OrganizationResponseProvinceEnum valueOf(String name) => _$organizationResponseProvinceEnumValueOf(name);
}

class OrganizationResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ACTIVE')
  static const OrganizationResponseStatusEnum ACTIVE = _$organizationResponseStatusEnum_ACTIVE;
  @BuiltValueEnumConst(wireName: r'SUSPENDED')
  static const OrganizationResponseStatusEnum SUSPENDED = _$organizationResponseStatusEnum_SUSPENDED;

  static Serializer<OrganizationResponseStatusEnum> get serializer => _$organizationResponseStatusEnumSerializer;

  const OrganizationResponseStatusEnum._(String name): super(name);

  static BuiltSet<OrganizationResponseStatusEnum> get values => _$organizationResponseStatusEnumValues;
  static OrganizationResponseStatusEnum valueOf(String name) => _$organizationResponseStatusEnumValueOf(name);
}

class OrganizationResponseTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INSURER')
  static const OrganizationResponseTypeEnum INSURER = _$organizationResponseTypeEnum_INSURER;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const OrganizationResponseTypeEnum BROKER = _$organizationResponseTypeEnum_BROKER;
  @BuiltValueEnumConst(wireName: r'AGENT')
  static const OrganizationResponseTypeEnum AGENT = _$organizationResponseTypeEnum_AGENT;
  @BuiltValueEnumConst(wireName: r'WORKSHOP')
  static const OrganizationResponseTypeEnum WORKSHOP = _$organizationResponseTypeEnum_WORKSHOP;
  @BuiltValueEnumConst(wireName: r'ASSESSOR')
  static const OrganizationResponseTypeEnum ASSESSOR = _$organizationResponseTypeEnum_ASSESSOR;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const OrganizationResponseTypeEnum ASSISTANCE = _$organizationResponseTypeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'OMBUDSMAN')
  static const OrganizationResponseTypeEnum OMBUDSMAN = _$organizationResponseTypeEnum_OMBUDSMAN;
  @BuiltValueEnumConst(wireName: r'REGULATOR')
  static const OrganizationResponseTypeEnum REGULATOR = _$organizationResponseTypeEnum_REGULATOR;
  @BuiltValueEnumConst(wireName: r'POLICE')
  static const OrganizationResponseTypeEnum POLICE = _$organizationResponseTypeEnum_POLICE;
  @BuiltValueEnumConst(wireName: r'CORPORATE_CLIENT')
  static const OrganizationResponseTypeEnum CORPORATE_CLIENT = _$organizationResponseTypeEnum_CORPORATE_CLIENT;

  static Serializer<OrganizationResponseTypeEnum> get serializer => _$organizationResponseTypeEnumSerializer;

  const OrganizationResponseTypeEnum._(String name): super(name);

  static BuiltSet<OrganizationResponseTypeEnum> get values => _$organizationResponseTypeEnumValues;
  static OrganizationResponseTypeEnum valueOf(String name) => _$organizationResponseTypeEnumValueOf(name);
}

