//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/inspection_status.dart';
import 'package:moseguros_api/src/model/inspection_mode.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_inspection_summary_response.g.dart';

/// InsurerInspectionSummaryResponse
///
/// Properties:
/// * [customerName] 
/// * [id] 
/// * [mode] 
/// * [photoCount] - Fotografias activas
/// * [proposalId] 
/// * [proposalNumber] 
/// * [status] 
/// * [submittedAt] 
/// * [vehiclePlate] 
@BuiltValue()
abstract class InsurerInspectionSummaryResponse implements Built<InsurerInspectionSummaryResponse, InsurerInspectionSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'customerName')
  String get customerName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'mode')
  InspectionMode get mode;
  // enum modeEnum {  SELF_PHOTOS,  ON_SITE,  };

  /// Fotografias activas
  @BuiltValueField(wireName: r'photoCount')
  int get photoCount;

  @BuiltValueField(wireName: r'proposalId')
  String get proposalId;

  @BuiltValueField(wireName: r'proposalNumber')
  String get proposalNumber;

  @BuiltValueField(wireName: r'status')
  InspectionStatus get status;
  // enum statusEnum {  PENDING,  SUBMITTED,  APPROVED,  REJECTED,  };

  @BuiltValueField(wireName: r'submittedAt')
  DateTime? get submittedAt;

  @BuiltValueField(wireName: r'vehiclePlate')
  String get vehiclePlate;

  InsurerInspectionSummaryResponse._();

  factory InsurerInspectionSummaryResponse([void updates(InsurerInspectionSummaryResponseBuilder b)]) = _$InsurerInspectionSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerInspectionSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerInspectionSummaryResponse> get serializer => _$InsurerInspectionSummaryResponseSerializer();
}

class _$InsurerInspectionSummaryResponseSerializer implements PrimitiveSerializer<InsurerInspectionSummaryResponse> {
  @override
  final Iterable<Type> types = const [InsurerInspectionSummaryResponse, _$InsurerInspectionSummaryResponse];

  @override
  final String wireName = r'InsurerInspectionSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerInspectionSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'customerName';
    yield serializers.serialize(
      object.customerName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'mode';
    yield serializers.serialize(
      object.mode,
      specifiedType: const FullType(InspectionMode),
    );
    yield r'photoCount';
    yield serializers.serialize(
      object.photoCount,
      specifiedType: const FullType(int),
    );
    yield r'proposalId';
    yield serializers.serialize(
      object.proposalId,
      specifiedType: const FullType(String),
    );
    yield r'proposalNumber';
    yield serializers.serialize(
      object.proposalNumber,
      specifiedType: const FullType(String),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(InspectionStatus),
    );
    if (object.submittedAt != null) {
      yield r'submittedAt';
      yield serializers.serialize(
        object.submittedAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'vehiclePlate';
    yield serializers.serialize(
      object.vehiclePlate,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerInspectionSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerInspectionSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'customerName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'mode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionMode),
          ) as InspectionMode;
          result.mode = valueDes;
          break;
        case r'photoCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.photoCount = valueDes;
          break;
        case r'proposalId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.proposalId = valueDes;
          break;
        case r'proposalNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.proposalNumber = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionStatus),
          ) as InspectionStatus;
          result.status = valueDes;
          break;
        case r'submittedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.submittedAt = valueDes;
          break;
        case r'vehiclePlate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehiclePlate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerInspectionSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerInspectionSummaryResponseBuilder();
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

