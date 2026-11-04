//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/kyc_check_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_check_summary_response.g.dart';

/// KycCheckSummaryResponse
///
/// Properties:
/// * [fullName] - Cópia do perfil no momento do envio
/// * [id] 
/// * [idDocType] 
/// * [reviewedAt] 
/// * [status] 
/// * [submittedAt] 
@BuiltValue()
abstract class KycCheckSummaryResponse implements Built<KycCheckSummaryResponse, KycCheckSummaryResponseBuilder> {
  /// Cópia do perfil no momento do envio
  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'idDocType')
  KycCheckSummaryResponseIdDocTypeEnum get idDocType;
  // enum idDocTypeEnum {  BI,  PASSPORT,  DIRE,  };

  @BuiltValueField(wireName: r'reviewedAt')
  DateTime? get reviewedAt;

  @BuiltValueField(wireName: r'status')
  KycCheckStatus get status;
  // enum statusEnum {  PENDING,  VERIFIED,  REJECTED,  };

  @BuiltValueField(wireName: r'submittedAt')
  DateTime get submittedAt;

  KycCheckSummaryResponse._();

  factory KycCheckSummaryResponse([void updates(KycCheckSummaryResponseBuilder b)]) = _$KycCheckSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(KycCheckSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<KycCheckSummaryResponse> get serializer => _$KycCheckSummaryResponseSerializer();
}

class _$KycCheckSummaryResponseSerializer implements PrimitiveSerializer<KycCheckSummaryResponse> {
  @override
  final Iterable<Type> types = const [KycCheckSummaryResponse, _$KycCheckSummaryResponse];

  @override
  final String wireName = r'KycCheckSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    KycCheckSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'idDocType';
    yield serializers.serialize(
      object.idDocType,
      specifiedType: const FullType(KycCheckSummaryResponseIdDocTypeEnum),
    );
    if (object.reviewedAt != null) {
      yield r'reviewedAt';
      yield serializers.serialize(
        object.reviewedAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(KycCheckStatus),
    );
    yield r'submittedAt';
    yield serializers.serialize(
      object.submittedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    KycCheckSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required KycCheckSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'idDocType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycCheckSummaryResponseIdDocTypeEnum),
          ) as KycCheckSummaryResponseIdDocTypeEnum;
          result.idDocType = valueDes;
          break;
        case r'reviewedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.reviewedAt = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycCheckStatus),
          ) as KycCheckStatus;
          result.status = valueDes;
          break;
        case r'submittedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.submittedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  KycCheckSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = KycCheckSummaryResponseBuilder();
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

class KycCheckSummaryResponseIdDocTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'BI')
  static const KycCheckSummaryResponseIdDocTypeEnum BI = _$kycCheckSummaryResponseIdDocTypeEnum_BI;
  @BuiltValueEnumConst(wireName: r'PASSPORT')
  static const KycCheckSummaryResponseIdDocTypeEnum PASSPORT = _$kycCheckSummaryResponseIdDocTypeEnum_PASSPORT;
  @BuiltValueEnumConst(wireName: r'DIRE')
  static const KycCheckSummaryResponseIdDocTypeEnum DIRE = _$kycCheckSummaryResponseIdDocTypeEnum_DIRE;

  static Serializer<KycCheckSummaryResponseIdDocTypeEnum> get serializer => _$kycCheckSummaryResponseIdDocTypeEnumSerializer;

  const KycCheckSummaryResponseIdDocTypeEnum._(String name): super(name);

  static BuiltSet<KycCheckSummaryResponseIdDocTypeEnum> get values => _$kycCheckSummaryResponseIdDocTypeEnumValues;
  static KycCheckSummaryResponseIdDocTypeEnum valueOf(String name) => _$kycCheckSummaryResponseIdDocTypeEnumValueOf(name);
}

