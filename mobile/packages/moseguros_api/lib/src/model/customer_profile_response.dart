//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'customer_profile_response.g.dart';

/// CustomerProfileResponse
///
/// Properties:
/// * [address] 
/// * [birthDate] 
/// * [complete] - Tem nome, data de nascimento, documento, NUIT e província
/// * [fullName] 
/// * [idDocNumberMasked] - Todos os caracteres menos os últimos 4 trocados por *
/// * [idDocType] 
/// * [nuitMasked] 
/// * [province] 
/// * [version] - Enviar na alteração
@BuiltValue()
abstract class CustomerProfileResponse implements Built<CustomerProfileResponse, CustomerProfileResponseBuilder> {
  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'birthDate')
  Date? get birthDate;

  /// Tem nome, data de nascimento, documento, NUIT e província
  @BuiltValueField(wireName: r'complete')
  bool get complete;

  @BuiltValueField(wireName: r'fullName')
  String? get fullName;

  /// Todos os caracteres menos os últimos 4 trocados por *
  @BuiltValueField(wireName: r'idDocNumberMasked')
  String? get idDocNumberMasked;

  @BuiltValueField(wireName: r'idDocType')
  CustomerProfileResponseIdDocTypeEnum? get idDocType;
  // enum idDocTypeEnum {  BI,  PASSPORT,  DIRE,  };

  @BuiltValueField(wireName: r'nuitMasked')
  String? get nuitMasked;

  @BuiltValueField(wireName: r'province')
  CustomerProfileResponseProvinceEnum? get province;
  // enum provinceEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  /// Enviar na alteração
  @BuiltValueField(wireName: r'version')
  int get version;

  CustomerProfileResponse._();

  factory CustomerProfileResponse([void updates(CustomerProfileResponseBuilder b)]) = _$CustomerProfileResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CustomerProfileResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CustomerProfileResponse> get serializer => _$CustomerProfileResponseSerializer();
}

class _$CustomerProfileResponseSerializer implements PrimitiveSerializer<CustomerProfileResponse> {
  @override
  final Iterable<Type> types = const [CustomerProfileResponse, _$CustomerProfileResponse];

  @override
  final String wireName = r'CustomerProfileResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CustomerProfileResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.address != null) {
      yield r'address';
      yield serializers.serialize(
        object.address,
        specifiedType: const FullType(String),
      );
    }
    if (object.birthDate != null) {
      yield r'birthDate';
      yield serializers.serialize(
        object.birthDate,
        specifiedType: const FullType(Date),
      );
    }
    yield r'complete';
    yield serializers.serialize(
      object.complete,
      specifiedType: const FullType(bool),
    );
    if (object.fullName != null) {
      yield r'fullName';
      yield serializers.serialize(
        object.fullName,
        specifiedType: const FullType(String),
      );
    }
    if (object.idDocNumberMasked != null) {
      yield r'idDocNumberMasked';
      yield serializers.serialize(
        object.idDocNumberMasked,
        specifiedType: const FullType(String),
      );
    }
    if (object.idDocType != null) {
      yield r'idDocType';
      yield serializers.serialize(
        object.idDocType,
        specifiedType: const FullType(CustomerProfileResponseIdDocTypeEnum),
      );
    }
    if (object.nuitMasked != null) {
      yield r'nuitMasked';
      yield serializers.serialize(
        object.nuitMasked,
        specifiedType: const FullType(String),
      );
    }
    if (object.province != null) {
      yield r'province';
      yield serializers.serialize(
        object.province,
        specifiedType: const FullType(CustomerProfileResponseProvinceEnum),
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
    CustomerProfileResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CustomerProfileResponseBuilder result,
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
        case r'complete':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.complete = valueDes;
          break;
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'idDocNumberMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.idDocNumberMasked = valueDes;
          break;
        case r'idDocType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CustomerProfileResponseIdDocTypeEnum),
          ) as CustomerProfileResponseIdDocTypeEnum;
          result.idDocType = valueDes;
          break;
        case r'nuitMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.nuitMasked = valueDes;
          break;
        case r'province':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CustomerProfileResponseProvinceEnum),
          ) as CustomerProfileResponseProvinceEnum;
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
  CustomerProfileResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CustomerProfileResponseBuilder();
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

class CustomerProfileResponseIdDocTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'BI')
  static const CustomerProfileResponseIdDocTypeEnum BI = _$customerProfileResponseIdDocTypeEnum_BI;
  @BuiltValueEnumConst(wireName: r'PASSPORT')
  static const CustomerProfileResponseIdDocTypeEnum PASSPORT = _$customerProfileResponseIdDocTypeEnum_PASSPORT;
  @BuiltValueEnumConst(wireName: r'DIRE')
  static const CustomerProfileResponseIdDocTypeEnum DIRE = _$customerProfileResponseIdDocTypeEnum_DIRE;

  static Serializer<CustomerProfileResponseIdDocTypeEnum> get serializer => _$customerProfileResponseIdDocTypeEnumSerializer;

  const CustomerProfileResponseIdDocTypeEnum._(String name): super(name);

  static BuiltSet<CustomerProfileResponseIdDocTypeEnum> get values => _$customerProfileResponseIdDocTypeEnumValues;
  static CustomerProfileResponseIdDocTypeEnum valueOf(String name) => _$customerProfileResponseIdDocTypeEnumValueOf(name);
}

class CustomerProfileResponseProvinceEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const CustomerProfileResponseProvinceEnum MAPUTO_CITY = _$customerProfileResponseProvinceEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const CustomerProfileResponseProvinceEnum MAPUTO = _$customerProfileResponseProvinceEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const CustomerProfileResponseProvinceEnum GAZA = _$customerProfileResponseProvinceEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const CustomerProfileResponseProvinceEnum INHAMBANE = _$customerProfileResponseProvinceEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const CustomerProfileResponseProvinceEnum SOFALA = _$customerProfileResponseProvinceEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const CustomerProfileResponseProvinceEnum MANICA = _$customerProfileResponseProvinceEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const CustomerProfileResponseProvinceEnum TETE = _$customerProfileResponseProvinceEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const CustomerProfileResponseProvinceEnum ZAMBEZIA = _$customerProfileResponseProvinceEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const CustomerProfileResponseProvinceEnum NAMPULA = _$customerProfileResponseProvinceEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const CustomerProfileResponseProvinceEnum CABO_DELGADO = _$customerProfileResponseProvinceEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const CustomerProfileResponseProvinceEnum NIASSA = _$customerProfileResponseProvinceEnum_NIASSA;

  static Serializer<CustomerProfileResponseProvinceEnum> get serializer => _$customerProfileResponseProvinceEnumSerializer;

  const CustomerProfileResponseProvinceEnum._(String name): super(name);

  static BuiltSet<CustomerProfileResponseProvinceEnum> get values => _$customerProfileResponseProvinceEnumValues;
  static CustomerProfileResponseProvinceEnum valueOf(String name) => _$customerProfileResponseProvinceEnumValueOf(name);
}

