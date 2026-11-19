//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/policy_vehicle_ref.dart';
import 'package:moseguros_api/src/model/cover_status.dart';
import 'package:moseguros_api/src/model/policy_insurer_ref.dart';
import 'package:moseguros_api/src/model/coverage_type.dart';
import 'package:moseguros_api/src/model/policy_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'my_policy_summary_response.g.dart';

/// MyPolicySummaryResponse
///
/// Properties:
/// * [certificateReady] 
/// * [coverStatus] 
/// * [coverageType] 
/// * [endAt] 
/// * [id] 
/// * [insurer] 
/// * [number] 
/// * [productName] 
/// * [startAt] 
/// * [status] 
/// * [vehicle] 
@BuiltValue()
abstract class MyPolicySummaryResponse implements Built<MyPolicySummaryResponse, MyPolicySummaryResponseBuilder> {
  @BuiltValueField(wireName: r'certificateReady')
  bool get certificateReady;

  @BuiltValueField(wireName: r'coverStatus')
  CoverStatus get coverStatus;
  // enum coverStatusEnum {  COVERED,  NOT_STARTED,  ENDED,  SUSPENDED,  CANCELLED,  LAPSED,  };

  @BuiltValueField(wireName: r'coverageType')
  CoverageType get coverageType;
  // enum coverageTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  };

  @BuiltValueField(wireName: r'endAt')
  DateTime get endAt;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'insurer')
  PolicyInsurerRef get insurer;

  @BuiltValueField(wireName: r'number')
  String get number;

  @BuiltValueField(wireName: r'productName')
  String get productName;

  @BuiltValueField(wireName: r'startAt')
  DateTime get startAt;

  @BuiltValueField(wireName: r'status')
  PolicyStatus get status;
  // enum statusEnum {  ACTIVE,  SUSPENDED,  CANCELLED,  EXPIRED,  LAPSED,  };

  @BuiltValueField(wireName: r'vehicle')
  PolicyVehicleRef get vehicle;

  MyPolicySummaryResponse._();

  factory MyPolicySummaryResponse([void updates(MyPolicySummaryResponseBuilder b)]) = _$MyPolicySummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(MyPolicySummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<MyPolicySummaryResponse> get serializer => _$MyPolicySummaryResponseSerializer();
}

class _$MyPolicySummaryResponseSerializer implements PrimitiveSerializer<MyPolicySummaryResponse> {
  @override
  final Iterable<Type> types = const [MyPolicySummaryResponse, _$MyPolicySummaryResponse];

  @override
  final String wireName = r'MyPolicySummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    MyPolicySummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'certificateReady';
    yield serializers.serialize(
      object.certificateReady,
      specifiedType: const FullType(bool),
    );
    yield r'coverStatus';
    yield serializers.serialize(
      object.coverStatus,
      specifiedType: const FullType(CoverStatus),
    );
    yield r'coverageType';
    yield serializers.serialize(
      object.coverageType,
      specifiedType: const FullType(CoverageType),
    );
    yield r'endAt';
    yield serializers.serialize(
      object.endAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'insurer';
    yield serializers.serialize(
      object.insurer,
      specifiedType: const FullType(PolicyInsurerRef),
    );
    yield r'number';
    yield serializers.serialize(
      object.number,
      specifiedType: const FullType(String),
    );
    yield r'productName';
    yield serializers.serialize(
      object.productName,
      specifiedType: const FullType(String),
    );
    yield r'startAt';
    yield serializers.serialize(
      object.startAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PolicyStatus),
    );
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(PolicyVehicleRef),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    MyPolicySummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required MyPolicySummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'certificateReady':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.certificateReady = valueDes;
          break;
        case r'coverStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverStatus),
          ) as CoverStatus;
          result.coverStatus = valueDes;
          break;
        case r'coverageType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverageType),
          ) as CoverageType;
          result.coverageType = valueDes;
          break;
        case r'endAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.endAt = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyInsurerRef),
          ) as PolicyInsurerRef;
          result.insurer.replace(valueDes);
          break;
        case r'number':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.number = valueDes;
          break;
        case r'productName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productName = valueDes;
          break;
        case r'startAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.startAt = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyStatus),
          ) as PolicyStatus;
          result.status = valueDes;
          break;
        case r'vehicle':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyVehicleRef),
          ) as PolicyVehicleRef;
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
  MyPolicySummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = MyPolicySummaryResponseBuilder();
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

