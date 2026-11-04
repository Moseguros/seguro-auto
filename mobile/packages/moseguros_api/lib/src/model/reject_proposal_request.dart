//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reject_proposal_request.g.dart';

/// RejectProposalRequest
///
/// Properties:
/// * [reason] 
/// * [reasonCode] 
/// * [version] 
@BuiltValue()
abstract class RejectProposalRequest implements Built<RejectProposalRequest, RejectProposalRequestBuilder> {
  @BuiltValueField(wireName: r'reason')
  String get reason;

  @BuiltValueField(wireName: r'reasonCode')
  RejectProposalRequestReasonCodeEnum get reasonCode;
  // enum reasonCodeEnum {  RISK_NOT_ACCEPTED,  KYC_FAILED,  INSPECTION_FAILED,  FALSE_DECLARATION,  DOCUMENTS_INVALID,  OTHER,  };

  @BuiltValueField(wireName: r'version')
  int get version;

  RejectProposalRequest._();

  factory RejectProposalRequest([void updates(RejectProposalRequestBuilder b)]) = _$RejectProposalRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RejectProposalRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RejectProposalRequest> get serializer => _$RejectProposalRequestSerializer();
}

class _$RejectProposalRequestSerializer implements PrimitiveSerializer<RejectProposalRequest> {
  @override
  final Iterable<Type> types = const [RejectProposalRequest, _$RejectProposalRequest];

  @override
  final String wireName = r'RejectProposalRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RejectProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'reason';
    yield serializers.serialize(
      object.reason,
      specifiedType: const FullType(String),
    );
    yield r'reasonCode';
    yield serializers.serialize(
      object.reasonCode,
      specifiedType: const FullType(RejectProposalRequestReasonCodeEnum),
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
    RejectProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RejectProposalRequestBuilder result,
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
            specifiedType: const FullType(RejectProposalRequestReasonCodeEnum),
          ) as RejectProposalRequestReasonCodeEnum;
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
  RejectProposalRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RejectProposalRequestBuilder();
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

class RejectProposalRequestReasonCodeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'RISK_NOT_ACCEPTED')
  static const RejectProposalRequestReasonCodeEnum RISK_NOT_ACCEPTED = _$rejectProposalRequestReasonCodeEnum_RISK_NOT_ACCEPTED;
  @BuiltValueEnumConst(wireName: r'KYC_FAILED')
  static const RejectProposalRequestReasonCodeEnum KYC_FAILED = _$rejectProposalRequestReasonCodeEnum_KYC_FAILED;
  @BuiltValueEnumConst(wireName: r'INSPECTION_FAILED')
  static const RejectProposalRequestReasonCodeEnum INSPECTION_FAILED = _$rejectProposalRequestReasonCodeEnum_INSPECTION_FAILED;
  @BuiltValueEnumConst(wireName: r'FALSE_DECLARATION')
  static const RejectProposalRequestReasonCodeEnum FALSE_DECLARATION = _$rejectProposalRequestReasonCodeEnum_FALSE_DECLARATION;
  @BuiltValueEnumConst(wireName: r'DOCUMENTS_INVALID')
  static const RejectProposalRequestReasonCodeEnum DOCUMENTS_INVALID = _$rejectProposalRequestReasonCodeEnum_DOCUMENTS_INVALID;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const RejectProposalRequestReasonCodeEnum OTHER = _$rejectProposalRequestReasonCodeEnum_OTHER;

  static Serializer<RejectProposalRequestReasonCodeEnum> get serializer => _$rejectProposalRequestReasonCodeEnumSerializer;

  const RejectProposalRequestReasonCodeEnum._(String name): super(name);

  static BuiltSet<RejectProposalRequestReasonCodeEnum> get values => _$rejectProposalRequestReasonCodeEnumValues;
  static RejectProposalRequestReasonCodeEnum valueOf(String name) => _$rejectProposalRequestReasonCodeEnumValueOf(name);
}

