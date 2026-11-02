//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/insurer_profile_request.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_organization_request.g.dart';

/// UpdateOrganizationRequest
///
/// Properties:
/// * [address] 
/// * [contactEmail] 
/// * [contactPhone] 
/// * [insurerProfile] 
/// * [legalName] 
/// * [licenseNumber] 
/// * [nuit] - NUIT: 9 dígitos
/// * [province] 
/// * [tradeName] 
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateOrganizationRequest implements Built<UpdateOrganizationRequest, UpdateOrganizationRequestBuilder> {
  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'contactEmail')
  String get contactEmail;

  @BuiltValueField(wireName: r'contactPhone')
  String? get contactPhone;

  @BuiltValueField(wireName: r'insurerProfile')
  InsurerProfileRequest? get insurerProfile;

  @BuiltValueField(wireName: r'legalName')
  String get legalName;

  @BuiltValueField(wireName: r'licenseNumber')
  String? get licenseNumber;

  /// NUIT: 9 dígitos
  @BuiltValueField(wireName: r'nuit')
  String get nuit;

  @BuiltValueField(wireName: r'province')
  UpdateOrganizationRequestProvinceEnum? get province;
  // enum provinceEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  @BuiltValueField(wireName: r'tradeName')
  String? get tradeName;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateOrganizationRequest._();

  factory UpdateOrganizationRequest([void updates(UpdateOrganizationRequestBuilder b)]) = _$UpdateOrganizationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateOrganizationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateOrganizationRequest> get serializer => _$UpdateOrganizationRequestSerializer();
}

class _$UpdateOrganizationRequestSerializer implements PrimitiveSerializer<UpdateOrganizationRequest> {
  @override
  final Iterable<Type> types = const [UpdateOrganizationRequest, _$UpdateOrganizationRequest];

  @override
  final String wireName = r'UpdateOrganizationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateOrganizationRequest object, {
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
        specifiedType: const FullType(UpdateOrganizationRequestProvinceEnum),
      );
    }
    if (object.tradeName != null) {
      yield r'tradeName';
      yield serializers.serialize(
        object.tradeName,
        specifiedType: const FullType(String),
      );
    }
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateOrganizationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateOrganizationRequestBuilder result,
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
            specifiedType: const FullType(UpdateOrganizationRequestProvinceEnum),
          ) as UpdateOrganizationRequestProvinceEnum;
          result.province = valueDes;
          break;
        case r'tradeName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.tradeName = valueDes;
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
  UpdateOrganizationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateOrganizationRequestBuilder();
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

class UpdateOrganizationRequestProvinceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const UpdateOrganizationRequestProvinceEnum MAPUTO_CITY = _$updateOrganizationRequestProvinceEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const UpdateOrganizationRequestProvinceEnum MAPUTO = _$updateOrganizationRequestProvinceEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const UpdateOrganizationRequestProvinceEnum GAZA = _$updateOrganizationRequestProvinceEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const UpdateOrganizationRequestProvinceEnum INHAMBANE = _$updateOrganizationRequestProvinceEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const UpdateOrganizationRequestProvinceEnum SOFALA = _$updateOrganizationRequestProvinceEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const UpdateOrganizationRequestProvinceEnum MANICA = _$updateOrganizationRequestProvinceEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const UpdateOrganizationRequestProvinceEnum TETE = _$updateOrganizationRequestProvinceEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const UpdateOrganizationRequestProvinceEnum ZAMBEZIA = _$updateOrganizationRequestProvinceEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const UpdateOrganizationRequestProvinceEnum NAMPULA = _$updateOrganizationRequestProvinceEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const UpdateOrganizationRequestProvinceEnum CABO_DELGADO = _$updateOrganizationRequestProvinceEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const UpdateOrganizationRequestProvinceEnum NIASSA = _$updateOrganizationRequestProvinceEnum_NIASSA;

  static Serializer<UpdateOrganizationRequestProvinceEnum> get serializer => _$updateOrganizationRequestProvinceEnumSerializer;

  const UpdateOrganizationRequestProvinceEnum._(String name): super(name);

  static BuiltSet<UpdateOrganizationRequestProvinceEnum> get values => _$updateOrganizationRequestProvinceEnumValues;
  static UpdateOrganizationRequestProvinceEnum valueOf(String name) => _$updateOrganizationRequestProvinceEnumValueOf(name);
}

