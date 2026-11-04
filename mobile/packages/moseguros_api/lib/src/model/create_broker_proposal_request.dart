//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/broker_proposal_driver_request.dart';
import 'package:moseguros_api/src/model/broker_proposal_vehicle_request.dart';
import 'package:moseguros_api/src/model/broker_policyholder_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_broker_proposal_request.g.dart';

/// CreateBrokerProposalRequest
///
/// Properties:
/// * [driver] 
/// * [policyholder] 
/// * [quoteId] - Cotação OFFERED do mediador
/// * [vehicle] 
@BuiltValue()
abstract class CreateBrokerProposalRequest implements Built<CreateBrokerProposalRequest, CreateBrokerProposalRequestBuilder> {
  @BuiltValueField(wireName: r'driver')
  BrokerProposalDriverRequest get driver;

  @BuiltValueField(wireName: r'policyholder')
  BrokerPolicyholderRequest? get policyholder;

  /// Cotação OFFERED do mediador
  @BuiltValueField(wireName: r'quoteId')
  String get quoteId;

  @BuiltValueField(wireName: r'vehicle')
  BrokerProposalVehicleRequest get vehicle;

  CreateBrokerProposalRequest._();

  factory CreateBrokerProposalRequest([void updates(CreateBrokerProposalRequestBuilder b)]) = _$CreateBrokerProposalRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateBrokerProposalRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateBrokerProposalRequest> get serializer => _$CreateBrokerProposalRequestSerializer();
}

class _$CreateBrokerProposalRequestSerializer implements PrimitiveSerializer<CreateBrokerProposalRequest> {
  @override
  final Iterable<Type> types = const [CreateBrokerProposalRequest, _$CreateBrokerProposalRequest];

  @override
  final String wireName = r'CreateBrokerProposalRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateBrokerProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'driver';
    yield serializers.serialize(
      object.driver,
      specifiedType: const FullType(BrokerProposalDriverRequest),
    );
    if (object.policyholder != null) {
      yield r'policyholder';
      yield serializers.serialize(
        object.policyholder,
        specifiedType: const FullType(BrokerPolicyholderRequest),
      );
    }
    yield r'quoteId';
    yield serializers.serialize(
      object.quoteId,
      specifiedType: const FullType(String),
    );
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(BrokerProposalVehicleRequest),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateBrokerProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateBrokerProposalRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'driver':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BrokerProposalDriverRequest),
          ) as BrokerProposalDriverRequest;
          result.driver.replace(valueDes);
          break;
        case r'policyholder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BrokerPolicyholderRequest),
          ) as BrokerPolicyholderRequest;
          result.policyholder.replace(valueDes);
          break;
        case r'quoteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.quoteId = valueDes;
          break;
        case r'vehicle':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BrokerProposalVehicleRequest),
          ) as BrokerProposalVehicleRequest;
          result.vehicle.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateBrokerProposalRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateBrokerProposalRequestBuilder();
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

