//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/kyc_rejection_reason.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/kyc_check_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_check_response.g.dart';

/// KycCheckResponse
///
/// Properties:
/// * [id] 
/// * [idDocNumberMasked] - Todos os caracteres menos os últimos 4 trocados por *
/// * [idDocType] 
/// * [insurerId] - Seguradora a quem foi enviada
/// * [rejectionReason] - Texto livre do subscritor (só em REJECTED, se o escreveu)
/// * [rejectionReasonCode] 
/// * [reviewedAt] - Omitido enquanto PENDING
/// * [status] 
/// * [submittedAt] 
@BuiltValue()
abstract class KycCheckResponse implements Built<KycCheckResponse, KycCheckResponseBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  /// Todos os caracteres menos os últimos 4 trocados por *
  @BuiltValueField(wireName: r'idDocNumberMasked')
  String get idDocNumberMasked;

  @BuiltValueField(wireName: r'idDocType')
  KycCheckResponseIdDocTypeEnum get idDocType;
  // enum idDocTypeEnum {  BI,  PASSPORT,  DIRE,  };

  /// Seguradora a quem foi enviada
  @BuiltValueField(wireName: r'insurerId')
  String get insurerId;

  /// Texto livre do subscritor (só em REJECTED, se o escreveu)
  @BuiltValueField(wireName: r'rejectionReason')
  String? get rejectionReason;

  @BuiltValueField(wireName: r'rejectionReasonCode')
  KycRejectionReason? get rejectionReasonCode;
  // enum rejectionReasonCodeEnum {  DOCUMENT_UNREADABLE,  DOCUMENT_EXPIRED,  DATA_MISMATCH,  SELFIE_MISMATCH,  OTHER,  };

  /// Omitido enquanto PENDING
  @BuiltValueField(wireName: r'reviewedAt')
  DateTime? get reviewedAt;

  @BuiltValueField(wireName: r'status')
  KycCheckStatus get status;
  // enum statusEnum {  PENDING,  VERIFIED,  REJECTED,  };

  @BuiltValueField(wireName: r'submittedAt')
  DateTime get submittedAt;

  KycCheckResponse._();

  factory KycCheckResponse([void updates(KycCheckResponseBuilder b)]) = _$KycCheckResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(KycCheckResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<KycCheckResponse> get serializer => _$KycCheckResponseSerializer();
}

class _$KycCheckResponseSerializer implements PrimitiveSerializer<KycCheckResponse> {
  @override
  final Iterable<Type> types = const [KycCheckResponse, _$KycCheckResponse];

  @override
  final String wireName = r'KycCheckResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    KycCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'idDocNumberMasked';
    yield serializers.serialize(
      object.idDocNumberMasked,
      specifiedType: const FullType(String),
    );
    yield r'idDocType';
    yield serializers.serialize(
      object.idDocType,
      specifiedType: const FullType(KycCheckResponseIdDocTypeEnum),
    );
    yield r'insurerId';
    yield serializers.serialize(
      object.insurerId,
      specifiedType: const FullType(String),
    );
    if (object.rejectionReason != null) {
      yield r'rejectionReason';
      yield serializers.serialize(
        object.rejectionReason,
        specifiedType: const FullType(String),
      );
    }
    if (object.rejectionReasonCode != null) {
      yield r'rejectionReasonCode';
      yield serializers.serialize(
        object.rejectionReasonCode,
        specifiedType: const FullType(KycRejectionReason),
      );
    }
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
    KycCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required KycCheckResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
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
            specifiedType: const FullType(KycCheckResponseIdDocTypeEnum),
          ) as KycCheckResponseIdDocTypeEnum;
          result.idDocType = valueDes;
          break;
        case r'insurerId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.insurerId = valueDes;
          break;
        case r'rejectionReason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.rejectionReason = valueDes;
          break;
        case r'rejectionReasonCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycRejectionReason),
          ) as KycRejectionReason;
          result.rejectionReasonCode = valueDes;
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
  KycCheckResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = KycCheckResponseBuilder();
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

class KycCheckResponseIdDocTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'BI')
  static const KycCheckResponseIdDocTypeEnum BI = _$kycCheckResponseIdDocTypeEnum_BI;
  @BuiltValueEnumConst(wireName: r'PASSPORT')
  static const KycCheckResponseIdDocTypeEnum PASSPORT = _$kycCheckResponseIdDocTypeEnum_PASSPORT;
  @BuiltValueEnumConst(wireName: r'DIRE')
  static const KycCheckResponseIdDocTypeEnum DIRE = _$kycCheckResponseIdDocTypeEnum_DIRE;

  static Serializer<KycCheckResponseIdDocTypeEnum> get serializer => _$kycCheckResponseIdDocTypeEnumSerializer;

  const KycCheckResponseIdDocTypeEnum._(String name): super(name);

  static BuiltSet<KycCheckResponseIdDocTypeEnum> get values => _$kycCheckResponseIdDocTypeEnumValues;
  static KycCheckResponseIdDocTypeEnum valueOf(String name) => _$kycCheckResponseIdDocTypeEnumValueOf(name);
}

