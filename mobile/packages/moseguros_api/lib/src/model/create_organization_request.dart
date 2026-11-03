//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/insurer_profile_request.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_organization_request.g.dart';

/// CreateOrganizationRequest
///
/// Properties:
/// * [address] 
/// * [contactEmail] 
/// * [contactPhone] - Só dígitos, com + opcional, sem espaços
/// * [insurerProfile] 
/// * [legalName] 
/// * [licenseNumber] - Número de autorização da ASFPM; obrigatório para INSURER, BROKER e AGENT (400 LICENSE_NUMBER_REQUIRED)
/// * [nuit] - NUIT: 9 dígitos
/// * [province] 
/// * [tradeName] 
/// * [type] 
@BuiltValue()
abstract class CreateOrganizationRequest implements Built<CreateOrganizationRequest, CreateOrganizationRequestBuilder> {
  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'contactEmail')
  String get contactEmail;

  /// Só dígitos, com + opcional, sem espaços
  @BuiltValueField(wireName: r'contactPhone')
  String? get contactPhone;

  @BuiltValueField(wireName: r'insurerProfile')
  InsurerProfileRequest? get insurerProfile;

  @BuiltValueField(wireName: r'legalName')
  String get legalName;

  /// Número de autorização da ASFPM; obrigatório para INSURER, BROKER e AGENT (400 LICENSE_NUMBER_REQUIRED)
  @BuiltValueField(wireName: r'licenseNumber')
  String? get licenseNumber;

  /// NUIT: 9 dígitos
  @BuiltValueField(wireName: r'nuit')
  String get nuit;

  @BuiltValueField(wireName: r'province')
  CreateOrganizationRequestProvinceEnum? get province;
  // enum provinceEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  @BuiltValueField(wireName: r'tradeName')
  String? get tradeName;

  @BuiltValueField(wireName: r'type')
  CreateOrganizationRequestTypeEnum get type;
  // enum typeEnum {  INSURER,  BROKER,  AGENT,  WORKSHOP,  ASSESSOR,  ASSISTANCE,  OMBUDSMAN,  REGULATOR,  POLICE,  CORPORATE_CLIENT,  };

  CreateOrganizationRequest._();

  factory CreateOrganizationRequest([void updates(CreateOrganizationRequestBuilder b)]) = _$CreateOrganizationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateOrganizationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateOrganizationRequest> get serializer => _$CreateOrganizationRequestSerializer();
}

class _$CreateOrganizationRequestSerializer implements PrimitiveSerializer<CreateOrganizationRequest> {
  @override
  final Iterable<Type> types = const [CreateOrganizationRequest, _$CreateOrganizationRequest];

  @override
  final String wireName = r'CreateOrganizationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateOrganizationRequest object, {
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
    if (object.insurerProfile != null) {
      yield r'insurerProfile';
      yield serializers.serialize(
        object.insurerProfile,
        specifiedType: const FullType(InsurerProfileRequest),
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
        specifiedType: const FullType(CreateOrganizationRequestProvinceEnum),
      );
    }
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
      specifiedType: const FullType(CreateOrganizationRequestTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateOrganizationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateOrganizationRequestBuilder result,
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
        case r'insurerProfile':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InsurerProfileRequest),
          ) as InsurerProfileRequest;
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
            specifiedType: const FullType(CreateOrganizationRequestProvinceEnum),
          ) as CreateOrganizationRequestProvinceEnum;
          result.province = valueDes;
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
            specifiedType: const FullType(CreateOrganizationRequestTypeEnum),
          ) as CreateOrganizationRequestTypeEnum;
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
  CreateOrganizationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateOrganizationRequestBuilder();
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

class CreateOrganizationRequestProvinceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const CreateOrganizationRequestProvinceEnum MAPUTO_CITY = _$createOrganizationRequestProvinceEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const CreateOrganizationRequestProvinceEnum MAPUTO = _$createOrganizationRequestProvinceEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const CreateOrganizationRequestProvinceEnum GAZA = _$createOrganizationRequestProvinceEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const CreateOrganizationRequestProvinceEnum INHAMBANE = _$createOrganizationRequestProvinceEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const CreateOrganizationRequestProvinceEnum SOFALA = _$createOrganizationRequestProvinceEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const CreateOrganizationRequestProvinceEnum MANICA = _$createOrganizationRequestProvinceEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const CreateOrganizationRequestProvinceEnum TETE = _$createOrganizationRequestProvinceEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const CreateOrganizationRequestProvinceEnum ZAMBEZIA = _$createOrganizationRequestProvinceEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const CreateOrganizationRequestProvinceEnum NAMPULA = _$createOrganizationRequestProvinceEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const CreateOrganizationRequestProvinceEnum CABO_DELGADO = _$createOrganizationRequestProvinceEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const CreateOrganizationRequestProvinceEnum NIASSA = _$createOrganizationRequestProvinceEnum_NIASSA;

  static Serializer<CreateOrganizationRequestProvinceEnum> get serializer => _$createOrganizationRequestProvinceEnumSerializer;

  const CreateOrganizationRequestProvinceEnum._(String name): super(name);

  static BuiltSet<CreateOrganizationRequestProvinceEnum> get values => _$createOrganizationRequestProvinceEnumValues;
  static CreateOrganizationRequestProvinceEnum valueOf(String name) => _$createOrganizationRequestProvinceEnumValueOf(name);
}

class CreateOrganizationRequestTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INSURER')
  static const CreateOrganizationRequestTypeEnum INSURER = _$createOrganizationRequestTypeEnum_INSURER;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const CreateOrganizationRequestTypeEnum BROKER = _$createOrganizationRequestTypeEnum_BROKER;
  @BuiltValueEnumConst(wireName: r'AGENT')
  static const CreateOrganizationRequestTypeEnum AGENT = _$createOrganizationRequestTypeEnum_AGENT;
  @BuiltValueEnumConst(wireName: r'WORKSHOP')
  static const CreateOrganizationRequestTypeEnum WORKSHOP = _$createOrganizationRequestTypeEnum_WORKSHOP;
  @BuiltValueEnumConst(wireName: r'ASSESSOR')
  static const CreateOrganizationRequestTypeEnum ASSESSOR = _$createOrganizationRequestTypeEnum_ASSESSOR;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const CreateOrganizationRequestTypeEnum ASSISTANCE = _$createOrganizationRequestTypeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'OMBUDSMAN')
  static const CreateOrganizationRequestTypeEnum OMBUDSMAN = _$createOrganizationRequestTypeEnum_OMBUDSMAN;
  @BuiltValueEnumConst(wireName: r'REGULATOR')
  static const CreateOrganizationRequestTypeEnum REGULATOR = _$createOrganizationRequestTypeEnum_REGULATOR;
  @BuiltValueEnumConst(wireName: r'POLICE')
  static const CreateOrganizationRequestTypeEnum POLICE = _$createOrganizationRequestTypeEnum_POLICE;
  @BuiltValueEnumConst(wireName: r'CORPORATE_CLIENT')
  static const CreateOrganizationRequestTypeEnum CORPORATE_CLIENT = _$createOrganizationRequestTypeEnum_CORPORATE_CLIENT;

  static Serializer<CreateOrganizationRequestTypeEnum> get serializer => _$createOrganizationRequestTypeEnumSerializer;

  const CreateOrganizationRequestTypeEnum._(String name): super(name);

  static BuiltSet<CreateOrganizationRequestTypeEnum> get values => _$createOrganizationRequestTypeEnumValues;
  static CreateOrganizationRequestTypeEnum valueOf(String name) => _$createOrganizationRequestTypeEnumValueOf(name);
}

