//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'broker_policyholder_request.g.dart';

/// Só com policyholderSource = BROKER (senão NOT_ALLOWED); omitido mantém o actual
///
/// Properties:
/// * [address] 
/// * [birthDate] - Pelo menos 18 anos, no máximo 120
/// * [fullName] - Letras, espaços, apóstrofo e hífen; pelo menos duas palavras
/// * [idDocNumber] - Maiúsculas e dígitos, 5–20 (espaços ignorados). Obrigatório na criação; no updateBrokerProposal, omitido mantém o actual (se o tipo não mudar).
/// * [idDocType] 
/// * [nuit] - 9 dígitos. Obrigatório na criação; no updateBrokerProposal, omitido mantém o actual.
/// * [province] 
@BuiltValue()
abstract class BrokerPolicyholderRequest implements Built<BrokerPolicyholderRequest, BrokerPolicyholderRequestBuilder> {
  @BuiltValueField(wireName: r'address')
  String? get address;

  /// Pelo menos 18 anos, no máximo 120
  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  /// Letras, espaços, apóstrofo e hífen; pelo menos duas palavras
  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  /// Maiúsculas e dígitos, 5–20 (espaços ignorados). Obrigatório na criação; no updateBrokerProposal, omitido mantém o actual (se o tipo não mudar).
  @BuiltValueField(wireName: r'idDocNumber')
  String? get idDocNumber;

  @BuiltValueField(wireName: r'idDocType')
  BrokerPolicyholderRequestIdDocTypeEnum get idDocType;
  // enum idDocTypeEnum {  BI,  PASSPORT,  DIRE,  };

  /// 9 dígitos. Obrigatório na criação; no updateBrokerProposal, omitido mantém o actual.
  @BuiltValueField(wireName: r'nuit')
  String? get nuit;

  @BuiltValueField(wireName: r'province')
  BrokerPolicyholderRequestProvinceEnum get province;
  // enum provinceEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  BrokerPolicyholderRequest._();

  factory BrokerPolicyholderRequest([void updates(BrokerPolicyholderRequestBuilder b)]) = _$BrokerPolicyholderRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(BrokerPolicyholderRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<BrokerPolicyholderRequest> get serializer => _$BrokerPolicyholderRequestSerializer();
}

class _$BrokerPolicyholderRequestSerializer implements PrimitiveSerializer<BrokerPolicyholderRequest> {
  @override
  final Iterable<Type> types = const [BrokerPolicyholderRequest, _$BrokerPolicyholderRequest];

  @override
  final String wireName = r'BrokerPolicyholderRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    BrokerPolicyholderRequest object, {
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
      specifiedType: const FullType(BrokerPolicyholderRequestIdDocTypeEnum),
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
      specifiedType: const FullType(BrokerPolicyholderRequestProvinceEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    BrokerPolicyholderRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required BrokerPolicyholderRequestBuilder result,
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
            specifiedType: const FullType(BrokerPolicyholderRequestIdDocTypeEnum),
          ) as BrokerPolicyholderRequestIdDocTypeEnum;
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
            specifiedType: const FullType(BrokerPolicyholderRequestProvinceEnum),
          ) as BrokerPolicyholderRequestProvinceEnum;
          result.province = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  BrokerPolicyholderRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = BrokerPolicyholderRequestBuilder();
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

class BrokerPolicyholderRequestIdDocTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'BI')
  static const BrokerPolicyholderRequestIdDocTypeEnum BI = _$brokerPolicyholderRequestIdDocTypeEnum_BI;
  @BuiltValueEnumConst(wireName: r'PASSPORT')
  static const BrokerPolicyholderRequestIdDocTypeEnum PASSPORT = _$brokerPolicyholderRequestIdDocTypeEnum_PASSPORT;
  @BuiltValueEnumConst(wireName: r'DIRE')
  static const BrokerPolicyholderRequestIdDocTypeEnum DIRE = _$brokerPolicyholderRequestIdDocTypeEnum_DIRE;

  static Serializer<BrokerPolicyholderRequestIdDocTypeEnum> get serializer => _$brokerPolicyholderRequestIdDocTypeEnumSerializer;

  const BrokerPolicyholderRequestIdDocTypeEnum._(String name): super(name);

  static BuiltSet<BrokerPolicyholderRequestIdDocTypeEnum> get values => _$brokerPolicyholderRequestIdDocTypeEnumValues;
  static BrokerPolicyholderRequestIdDocTypeEnum valueOf(String name) => _$brokerPolicyholderRequestIdDocTypeEnumValueOf(name);
}

class BrokerPolicyholderRequestProvinceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const BrokerPolicyholderRequestProvinceEnum MAPUTO_CITY = _$brokerPolicyholderRequestProvinceEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const BrokerPolicyholderRequestProvinceEnum MAPUTO = _$brokerPolicyholderRequestProvinceEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const BrokerPolicyholderRequestProvinceEnum GAZA = _$brokerPolicyholderRequestProvinceEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const BrokerPolicyholderRequestProvinceEnum INHAMBANE = _$brokerPolicyholderRequestProvinceEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const BrokerPolicyholderRequestProvinceEnum SOFALA = _$brokerPolicyholderRequestProvinceEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const BrokerPolicyholderRequestProvinceEnum MANICA = _$brokerPolicyholderRequestProvinceEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const BrokerPolicyholderRequestProvinceEnum TETE = _$brokerPolicyholderRequestProvinceEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const BrokerPolicyholderRequestProvinceEnum ZAMBEZIA = _$brokerPolicyholderRequestProvinceEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const BrokerPolicyholderRequestProvinceEnum NAMPULA = _$brokerPolicyholderRequestProvinceEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const BrokerPolicyholderRequestProvinceEnum CABO_DELGADO = _$brokerPolicyholderRequestProvinceEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const BrokerPolicyholderRequestProvinceEnum NIASSA = _$brokerPolicyholderRequestProvinceEnum_NIASSA;

  static Serializer<BrokerPolicyholderRequestProvinceEnum> get serializer => _$brokerPolicyholderRequestProvinceEnumSerializer;

  const BrokerPolicyholderRequestProvinceEnum._(String name): super(name);

  static BuiltSet<BrokerPolicyholderRequestProvinceEnum> get values => _$brokerPolicyholderRequestProvinceEnumValues;
  static BrokerPolicyholderRequestProvinceEnum valueOf(String name) => _$brokerPolicyholderRequestProvinceEnumValueOf(name);
}

