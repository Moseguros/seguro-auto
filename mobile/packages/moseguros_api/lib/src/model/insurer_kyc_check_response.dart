//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/kyc_customer_response.dart';
import 'package:moseguros_api/src/model/kyc_previous_check_response.dart';
import 'package:moseguros_api/src/model/kyc_rejection_reason.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/kyc_files_response.dart';
import 'package:moseguros_api/src/model/kyc_check_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_kyc_check_response.g.dart';

/// InsurerKycCheckResponse
///
/// Properties:
/// * [customer] 
/// * [files] 
/// * [id] 
/// * [previousChecks] - Outras verificações do mesmo cliente enviadas a esta seguradora, da mais recente para a mais antiga
/// * [rejectionReason] 
/// * [rejectionReasonCode] 
/// * [reviewedAt] 
/// * [status] 
/// * [submittedAt] 
/// * [version] - Enviar em verify/reject
@BuiltValue()
abstract class InsurerKycCheckResponse implements Built<InsurerKycCheckResponse, InsurerKycCheckResponseBuilder> {
  @BuiltValueField(wireName: r'customer')
  KycCustomerResponse get customer;

  @BuiltValueField(wireName: r'files')
  KycFilesResponse get files;

  @BuiltValueField(wireName: r'id')
  String get id;

  /// Outras verificações do mesmo cliente enviadas a esta seguradora, da mais recente para a mais antiga
  @BuiltValueField(wireName: r'previousChecks')
  BuiltList<KycPreviousCheckResponse> get previousChecks;

  @BuiltValueField(wireName: r'rejectionReason')
  String? get rejectionReason;

  @BuiltValueField(wireName: r'rejectionReasonCode')
  KycRejectionReason? get rejectionReasonCode;
  // enum rejectionReasonCodeEnum {  DOCUMENT_UNREADABLE,  DOCUMENT_EXPIRED,  DATA_MISMATCH,  SELFIE_MISMATCH,  OTHER,  };

  @BuiltValueField(wireName: r'reviewedAt')
  DateTime? get reviewedAt;

  @BuiltValueField(wireName: r'status')
  KycCheckStatus get status;
  // enum statusEnum {  PENDING,  VERIFIED,  REJECTED,  };

  @BuiltValueField(wireName: r'submittedAt')
  DateTime get submittedAt;

  /// Enviar em verify/reject
  @BuiltValueField(wireName: r'version')
  int get version;

  InsurerKycCheckResponse._();

  factory InsurerKycCheckResponse([void updates(InsurerKycCheckResponseBuilder b)]) = _$InsurerKycCheckResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerKycCheckResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerKycCheckResponse> get serializer => _$InsurerKycCheckResponseSerializer();
}

class _$InsurerKycCheckResponseSerializer implements PrimitiveSerializer<InsurerKycCheckResponse> {
  @override
  final Iterable<Type> types = const [InsurerKycCheckResponse, _$InsurerKycCheckResponse];

  @override
  final String wireName = r'InsurerKycCheckResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerKycCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'customer';
    yield serializers.serialize(
      object.customer,
      specifiedType: const FullType(KycCustomerResponse),
    );
    yield r'files';
    yield serializers.serialize(
      object.files,
      specifiedType: const FullType(KycFilesResponse),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'previousChecks';
    yield serializers.serialize(
      object.previousChecks,
      specifiedType: const FullType(BuiltList, [FullType(KycPreviousCheckResponse)]),
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
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerKycCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerKycCheckResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'customer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycCustomerResponse),
          ) as KycCustomerResponse;
          result.customer.replace(valueDes);
          break;
        case r'files':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycFilesResponse),
          ) as KycFilesResponse;
          result.files.replace(valueDes);
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'previousChecks':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(KycPreviousCheckResponse)]),
          ) as BuiltList<KycPreviousCheckResponse>;
          result.previousChecks.replace(valueDes);
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
  InsurerKycCheckResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerKycCheckResponseBuilder();
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

