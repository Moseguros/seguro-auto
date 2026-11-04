//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_customer_response.g.dart';

/// KycCustomerResponse
///
/// Properties:
/// * [birthDate] 
/// * [fullName] 
/// * [idDocNumber] - Em claro, para comparar com a fotografia
/// * [idDocType] 
/// * [msisdnMasked] - Omitido se a conta só tem e-mail
/// * [userId] 
@BuiltValue()
abstract class KycCustomerResponse implements Built<KycCustomerResponse, KycCustomerResponseBuilder> {
  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  /// Em claro, para comparar com a fotografia
  @BuiltValueField(wireName: r'idDocNumber')
  String get idDocNumber;

  @BuiltValueField(wireName: r'idDocType')
  KycCustomerResponseIdDocTypeEnum get idDocType;
  // enum idDocTypeEnum {  BI,  PASSPORT,  DIRE,  };

  /// Omitido se a conta só tem e-mail
  @BuiltValueField(wireName: r'msisdnMasked')
  String? get msisdnMasked;

  @BuiltValueField(wireName: r'userId')
  String get userId;

  KycCustomerResponse._();

  factory KycCustomerResponse([void updates(KycCustomerResponseBuilder b)]) = _$KycCustomerResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(KycCustomerResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<KycCustomerResponse> get serializer => _$KycCustomerResponseSerializer();
}

class _$KycCustomerResponseSerializer implements PrimitiveSerializer<KycCustomerResponse> {
  @override
  final Iterable<Type> types = const [KycCustomerResponse, _$KycCustomerResponse];

  @override
  final String wireName = r'KycCustomerResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    KycCustomerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    yield r'idDocNumber';
    yield serializers.serialize(
      object.idDocNumber,
      specifiedType: const FullType(String),
    );
    yield r'idDocType';
    yield serializers.serialize(
      object.idDocType,
      specifiedType: const FullType(KycCustomerResponseIdDocTypeEnum),
    );
    if (object.msisdnMasked != null) {
      yield r'msisdnMasked';
      yield serializers.serialize(
        object.msisdnMasked,
        specifiedType: const FullType(String),
      );
    }
    yield r'userId';
    yield serializers.serialize(
      object.userId,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    KycCustomerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required KycCustomerResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
            specifiedType: const FullType(KycCustomerResponseIdDocTypeEnum),
          ) as KycCustomerResponseIdDocTypeEnum;
          result.idDocType = valueDes;
          break;
        case r'msisdnMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.msisdnMasked = valueDes;
          break;
        case r'userId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.userId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  KycCustomerResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = KycCustomerResponseBuilder();
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

class KycCustomerResponseIdDocTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'BI')
  static const KycCustomerResponseIdDocTypeEnum BI = _$kycCustomerResponseIdDocTypeEnum_BI;
  @BuiltValueEnumConst(wireName: r'PASSPORT')
  static const KycCustomerResponseIdDocTypeEnum PASSPORT = _$kycCustomerResponseIdDocTypeEnum_PASSPORT;
  @BuiltValueEnumConst(wireName: r'DIRE')
  static const KycCustomerResponseIdDocTypeEnum DIRE = _$kycCustomerResponseIdDocTypeEnum_DIRE;

  static Serializer<KycCustomerResponseIdDocTypeEnum> get serializer => _$kycCustomerResponseIdDocTypeEnumSerializer;

  const KycCustomerResponseIdDocTypeEnum._(String name): super(name);

  static BuiltSet<KycCustomerResponseIdDocTypeEnum> get values => _$kycCustomerResponseIdDocTypeEnumValues;
  static KycCustomerResponseIdDocTypeEnum valueOf(String name) => _$kycCustomerResponseIdDocTypeEnumValueOf(name);
}

