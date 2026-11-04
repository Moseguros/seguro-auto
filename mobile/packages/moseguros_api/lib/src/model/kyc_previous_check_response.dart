//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/kyc_rejection_reason.dart';
import 'package:moseguros_api/src/model/kyc_check_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_previous_check_response.g.dart';

/// Outras verificações do mesmo cliente enviadas a esta seguradora, da mais recente para a mais antiga
///
/// Properties:
/// * [id] 
/// * [rejectionReasonCode] 
/// * [status] 
/// * [submittedAt] 
@BuiltValue()
abstract class KycPreviousCheckResponse implements Built<KycPreviousCheckResponse, KycPreviousCheckResponseBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'rejectionReasonCode')
  KycRejectionReason? get rejectionReasonCode;
  // enum rejectionReasonCodeEnum {  DOCUMENT_UNREADABLE,  DOCUMENT_EXPIRED,  DATA_MISMATCH,  SELFIE_MISMATCH,  OTHER,  };

  @BuiltValueField(wireName: r'status')
  KycCheckStatus get status;
  // enum statusEnum {  PENDING,  VERIFIED,  REJECTED,  };

  @BuiltValueField(wireName: r'submittedAt')
  DateTime get submittedAt;

  KycPreviousCheckResponse._();

  factory KycPreviousCheckResponse([void updates(KycPreviousCheckResponseBuilder b)]) = _$KycPreviousCheckResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(KycPreviousCheckResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<KycPreviousCheckResponse> get serializer => _$KycPreviousCheckResponseSerializer();
}

class _$KycPreviousCheckResponseSerializer implements PrimitiveSerializer<KycPreviousCheckResponse> {
  @override
  final Iterable<Type> types = const [KycPreviousCheckResponse, _$KycPreviousCheckResponse];

  @override
  final String wireName = r'KycPreviousCheckResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    KycPreviousCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.rejectionReasonCode != null) {
      yield r'rejectionReasonCode';
      yield serializers.serialize(
        object.rejectionReasonCode,
        specifiedType: const FullType(KycRejectionReason),
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
    KycPreviousCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required KycPreviousCheckResponseBuilder result,
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
        case r'rejectionReasonCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycRejectionReason),
          ) as KycRejectionReason;
          result.rejectionReasonCode = valueDes;
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
  KycPreviousCheckResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = KycPreviousCheckResponseBuilder();
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

