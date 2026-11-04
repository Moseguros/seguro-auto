//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/broker_policyholder_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_broker_proposal_request.g.dart';

/// UpdateBrokerProposalRequest
///
/// Properties:
/// * [policyholder] 
/// * [startDate] - Nova data de início (hoje a hoje + 60 dias, Africa/Maputo); omitida mantém a actual
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateBrokerProposalRequest implements Built<UpdateBrokerProposalRequest, UpdateBrokerProposalRequestBuilder> {
  @BuiltValueField(wireName: r'policyholder')
  BrokerPolicyholderRequest? get policyholder;

  /// Nova data de início (hoje a hoje + 60 dias, Africa/Maputo); omitida mantém a actual
  @BuiltValueField(wireName: r'startDate')
  Date? get startDate;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateBrokerProposalRequest._();

  factory UpdateBrokerProposalRequest([void updates(UpdateBrokerProposalRequestBuilder b)]) = _$UpdateBrokerProposalRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateBrokerProposalRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateBrokerProposalRequest> get serializer => _$UpdateBrokerProposalRequestSerializer();
}

class _$UpdateBrokerProposalRequestSerializer implements PrimitiveSerializer<UpdateBrokerProposalRequest> {
  @override
  final Iterable<Type> types = const [UpdateBrokerProposalRequest, _$UpdateBrokerProposalRequest];

  @override
  final String wireName = r'UpdateBrokerProposalRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateBrokerProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.policyholder != null) {
      yield r'policyholder';
      yield serializers.serialize(
        object.policyholder,
        specifiedType: const FullType(BrokerPolicyholderRequest),
      );
    }
    if (object.startDate != null) {
      yield r'startDate';
      yield serializers.serialize(
        object.startDate,
        specifiedType: const FullType(Date),
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
    UpdateBrokerProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateBrokerProposalRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'policyholder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BrokerPolicyholderRequest),
          ) as BrokerPolicyholderRequest;
          result.policyholder.replace(valueDes);
          break;
        case r'startDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.startDate = valueDes;
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
  UpdateBrokerProposalRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateBrokerProposalRequestBuilder();
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

