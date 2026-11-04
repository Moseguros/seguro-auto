//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/kyc_rejection_reason.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reject_kyc_check_request.g.dart';

/// RejectKycCheckRequest
///
/// Properties:
/// * [reason] 
/// * [reasonCode] 
/// * [version] 
@BuiltValue()
abstract class RejectKycCheckRequest implements Built<RejectKycCheckRequest, RejectKycCheckRequestBuilder> {
  @BuiltValueField(wireName: r'reason')
  String? get reason;

  @BuiltValueField(wireName: r'reasonCode')
  KycRejectionReason get reasonCode;
  // enum reasonCodeEnum {  DOCUMENT_UNREADABLE,  DOCUMENT_EXPIRED,  DATA_MISMATCH,  SELFIE_MISMATCH,  OTHER,  };

  @BuiltValueField(wireName: r'version')
  int get version;

  RejectKycCheckRequest._();

  factory RejectKycCheckRequest([void updates(RejectKycCheckRequestBuilder b)]) = _$RejectKycCheckRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RejectKycCheckRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RejectKycCheckRequest> get serializer => _$RejectKycCheckRequestSerializer();
}

class _$RejectKycCheckRequestSerializer implements PrimitiveSerializer<RejectKycCheckRequest> {
  @override
  final Iterable<Type> types = const [RejectKycCheckRequest, _$RejectKycCheckRequest];

  @override
  final String wireName = r'RejectKycCheckRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RejectKycCheckRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.reason != null) {
      yield r'reason';
      yield serializers.serialize(
        object.reason,
        specifiedType: const FullType(String),
      );
    }
    yield r'reasonCode';
    yield serializers.serialize(
      object.reasonCode,
      specifiedType: const FullType(KycRejectionReason),
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
    RejectKycCheckRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RejectKycCheckRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'reason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.reason = valueDes;
          break;
        case r'reasonCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycRejectionReason),
          ) as KycRejectionReason;
          result.reasonCode = valueDes;
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
  RejectKycCheckRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RejectKycCheckRequestBuilder();
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

