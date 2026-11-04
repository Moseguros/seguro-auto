//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/risk_driver_request.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/charge.dart';
import 'package:moseguros_api/src/model/simulation_vehicle_request.dart';
import 'package:moseguros_api/src/model/requested_coverage_request.dart';
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'simulate_rating_request.g.dart';

/// SimulateRatingRequest
///
/// Properties:
/// * [charges] 
/// * [coverages] - Coberturas pedidas além das obrigatórias (pode ser vazia)
/// * [driver] 
/// * [durationDays] 
/// * [rating] - Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
/// * [startDate] - Qualquer data
/// * [vehicle] 
@BuiltValue()
abstract class SimulateRatingRequest implements Built<SimulateRatingRequest, SimulateRatingRequestBuilder> {
  @BuiltValueField(wireName: r'charges')
  BuiltList<Charge>? get charges;

  /// Coberturas pedidas além das obrigatórias (pode ser vazia)
  @BuiltValueField(wireName: r'coverages')
  BuiltList<RequestedCoverageRequest>? get coverages;

  @BuiltValueField(wireName: r'driver')
  RiskDriverRequest get driver;

  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  /// Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
  @BuiltValueField(wireName: r'rating')
  JsonObject? get rating;

  /// Qualquer data
  @BuiltValueField(wireName: r'startDate')
  Date get startDate;

  @BuiltValueField(wireName: r'vehicle')
  SimulationVehicleRequest get vehicle;

  SimulateRatingRequest._();

  factory SimulateRatingRequest([void updates(SimulateRatingRequestBuilder b)]) = _$SimulateRatingRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SimulateRatingRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SimulateRatingRequest> get serializer => _$SimulateRatingRequestSerializer();
}

class _$SimulateRatingRequestSerializer implements PrimitiveSerializer<SimulateRatingRequest> {
  @override
  final Iterable<Type> types = const [SimulateRatingRequest, _$SimulateRatingRequest];

  @override
  final String wireName = r'SimulateRatingRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SimulateRatingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.charges != null) {
      yield r'charges';
      yield serializers.serialize(
        object.charges,
        specifiedType: const FullType.nullable(BuiltList, [FullType(Charge)]),
      );
    }
    if (object.coverages != null) {
      yield r'coverages';
      yield serializers.serialize(
        object.coverages,
        specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageRequest)]),
      );
    }
    yield r'driver';
    yield serializers.serialize(
      object.driver,
      specifiedType: const FullType(RiskDriverRequest),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
    if (object.rating != null) {
      yield r'rating';
      yield serializers.serialize(
        object.rating,
        specifiedType: const FullType(JsonObject),
      );
    }
    yield r'startDate';
    yield serializers.serialize(
      object.startDate,
      specifiedType: const FullType(Date),
    );
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(SimulationVehicleRequest),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SimulateRatingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SimulateRatingRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'charges':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(BuiltList, [FullType(Charge)]),
          ) as BuiltList<Charge>?;
          if (valueDes == null) continue;
          result.charges.replace(valueDes);
          break;
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageRequest)]),
          ) as BuiltList<RequestedCoverageRequest>;
          result.coverages.replace(valueDes);
          break;
        case r'driver':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RiskDriverRequest),
          ) as RiskDriverRequest;
          result.driver.replace(valueDes);
          break;
        case r'durationDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationDays = valueDes;
          break;
        case r'rating':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(JsonObject),
          ) as JsonObject;
          result.rating = valueDes;
          break;
        case r'startDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.startDate = valueDes;
          break;
        case r'vehicle':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SimulationVehicleRequest),
          ) as SimulationVehicleRequest;
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
  SimulateRatingRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SimulateRatingRequestBuilder();
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

