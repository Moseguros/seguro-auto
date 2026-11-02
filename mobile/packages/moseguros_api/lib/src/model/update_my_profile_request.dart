//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_my_profile_request.g.dart';

/// UpdateMyProfileRequest
///
/// Properties:
/// * [address] 
/// * [birthDate] - Pelo menos 18 anos, no máximo 120
/// * [fullName] - Letras, espaços, apóstrofo e hífen; pelo menos duas palavras
/// * [idDocNumber] - Maiúsculas e dígitos, 5–20 (espaços ignorados). Obrigatório se ainda não existir ou se idDocType mudar; omitido mantém o actual.
/// * [idDocType] 
/// * [nuit] - 9 dígitos. Obrigatório se ainda não existir; omitido mantém o actual.
/// * [province] 
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateMyProfileRequest implements Built<UpdateMyProfileRequest, UpdateMyProfileRequestBuilder> {
  @BuiltValueField(wireName: r'address')
  String? get address;

  /// Pelo menos 18 anos, no máximo 120
  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  /// Letras, espaços, apóstrofo e hífen; pelo menos duas palavras
  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  /// Maiúsculas e dígitos, 5–20 (espaços ignorados). Obrigatório se ainda não existir ou se idDocType mudar; omitido mantém o actual.
  @BuiltValueField(wireName: r'idDocNumber')
  String? get idDocNumber;

  @BuiltValueField(wireName: r'idDocType')
  UpdateMyProfileRequestIdDocTypeEnum get idDocType;
  // enum idDocTypeEnum {  BI,  PASSPORT,  DIRE,  };

  /// 9 dígitos. Obrigatório se ainda não existir; omitido mantém o actual.
  @BuiltValueField(wireName: r'nuit')
  String? get nuit;

  @BuiltValueField(wireName: r'province')
  UpdateMyProfileRequestProvinceEnum get province;
  // enum provinceEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateMyProfileRequest._();

  factory UpdateMyProfileRequest([void updates(UpdateMyProfileRequestBuilder b)]) = _$UpdateMyProfileRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateMyProfileRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateMyProfileRequest> get serializer => _$UpdateMyProfileRequestSerializer();
}

class _$UpdateMyProfileRequestSerializer implements PrimitiveSerializer<UpdateMyProfileRequest> {
  @override
  final Iterable<Type> types = const [UpdateMyProfileRequest, _$UpdateMyProfileRequest];

  @override
  final String wireName = r'UpdateMyProfileRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateMyProfileRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.address != null) {
      yield r'address';
      yield serializers.serialize(
        object.address,
        specifiedType: const FullType(String),
      );
    }
    yield r'birthDate';
    yield serializers.serialize(
      object.birthDate,
      specifiedType: const FullType(Date),
    );
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
      specifiedType: const FullType(String),
    );
    if (object.idDocNumber != null) {
      yield r'idDocNumber';
      yield serializers.serialize(
        object.idDocNumber,
        specifiedType: const FullType(String),
      );
    }
    yield r'idDocType';
    yield serializers.serialize(
      object.idDocType,
      specifiedType: const FullType(UpdateMyProfileRequestIdDocTypeEnum),
    );
    if (object.nuit != null) {
      yield r'nuit';
      yield serializers.serialize(
        object.nuit,
        specifiedType: const FullType(String),
      );
    }
    yield r'province';
    yield serializers.serialize(
      object.province,
      specifiedType: const FullType(UpdateMyProfileRequestProvinceEnum),
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
    UpdateMyProfileRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateMyProfileRequestBuilder result,
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
        case r'birthDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.birthDate = valueDes;
          break;
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'idDocNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.idDocNumber = valueDes;
          break;
        case r'idDocType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UpdateMyProfileRequestIdDocTypeEnum),
          ) as UpdateMyProfileRequestIdDocTypeEnum;
          result.idDocType = valueDes;
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
            specifiedType: const FullType(UpdateMyProfileRequestProvinceEnum),
          ) as UpdateMyProfileRequestProvinceEnum;
          result.province = valueDes;
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
  UpdateMyProfileRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateMyProfileRequestBuilder();
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

class UpdateMyProfileRequestIdDocTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'BI')
  static const UpdateMyProfileRequestIdDocTypeEnum BI = _$updateMyProfileRequestIdDocTypeEnum_BI;
  @BuiltValueEnumConst(wireName: r'PASSPORT')
  static const UpdateMyProfileRequestIdDocTypeEnum PASSPORT = _$updateMyProfileRequestIdDocTypeEnum_PASSPORT;
  @BuiltValueEnumConst(wireName: r'DIRE')
  static const UpdateMyProfileRequestIdDocTypeEnum DIRE = _$updateMyProfileRequestIdDocTypeEnum_DIRE;

  static Serializer<UpdateMyProfileRequestIdDocTypeEnum> get serializer => _$updateMyProfileRequestIdDocTypeEnumSerializer;

  const UpdateMyProfileRequestIdDocTypeEnum._(String name): super(name);

  static BuiltSet<UpdateMyProfileRequestIdDocTypeEnum> get values => _$updateMyProfileRequestIdDocTypeEnumValues;
  static UpdateMyProfileRequestIdDocTypeEnum valueOf(String name) => _$updateMyProfileRequestIdDocTypeEnumValueOf(name);
}

class UpdateMyProfileRequestProvinceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const UpdateMyProfileRequestProvinceEnum MAPUTO_CITY = _$updateMyProfileRequestProvinceEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const UpdateMyProfileRequestProvinceEnum MAPUTO = _$updateMyProfileRequestProvinceEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const UpdateMyProfileRequestProvinceEnum GAZA = _$updateMyProfileRequestProvinceEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const UpdateMyProfileRequestProvinceEnum INHAMBANE = _$updateMyProfileRequestProvinceEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const UpdateMyProfileRequestProvinceEnum SOFALA = _$updateMyProfileRequestProvinceEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const UpdateMyProfileRequestProvinceEnum MANICA = _$updateMyProfileRequestProvinceEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const UpdateMyProfileRequestProvinceEnum TETE = _$updateMyProfileRequestProvinceEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const UpdateMyProfileRequestProvinceEnum ZAMBEZIA = _$updateMyProfileRequestProvinceEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const UpdateMyProfileRequestProvinceEnum NAMPULA = _$updateMyProfileRequestProvinceEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const UpdateMyProfileRequestProvinceEnum CABO_DELGADO = _$updateMyProfileRequestProvinceEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const UpdateMyProfileRequestProvinceEnum NIASSA = _$updateMyProfileRequestProvinceEnum_NIASSA;

  static Serializer<UpdateMyProfileRequestProvinceEnum> get serializer => _$updateMyProfileRequestProvinceEnumSerializer;

  const UpdateMyProfileRequestProvinceEnum._(String name): super(name);

  static BuiltSet<UpdateMyProfileRequestProvinceEnum> get values => _$updateMyProfileRequestProvinceEnumValues;
  static UpdateMyProfileRequestProvinceEnum valueOf(String name) => _$updateMyProfileRequestProvinceEnumValueOf(name);
}

