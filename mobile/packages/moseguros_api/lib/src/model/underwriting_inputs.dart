//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/declaration_question.dart';
import 'package:moseguros_api/src/model/inspection_status.dart';
import 'package:moseguros_api/src/model/kyc_state.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'underwriting_inputs.g.dart';

/// UnderwritingInputs
///
/// Properties:
/// * [hasOwnDamage] 
/// * [inspectionStatus] 
/// * [kycState] 
/// * [maxVehicleValue] 
/// * [positiveDeclarations] 
/// * [simplifiedKycMaxPremium] 
/// * [totalAmount] 
/// * [vehicleValue] 
@BuiltValue()
abstract class UnderwritingInputs implements Built<UnderwritingInputs, UnderwritingInputsBuilder> {
  @BuiltValueField(wireName: r'hasOwnDamage')
  bool get hasOwnDamage;

  @BuiltValueField(wireName: r'inspectionStatus')
  InspectionStatus? get inspectionStatus;
  // enum inspectionStatusEnum {  PENDING,  SUBMITTED,  APPROVED,  REJECTED,  };

  @BuiltValueField(wireName: r'kycState')
  KycState get kycState;
  // enum kycStateEnum {  NONE,  PENDING,  VERIFIED,  REJECTED,  OUTDATED,  };

  @BuiltValueField(wireName: r'maxVehicleValue')
  int get maxVehicleValue;

  @BuiltValueField(wireName: r'positiveDeclarations')
  BuiltList<DeclarationQuestion> get positiveDeclarations;

  @BuiltValueField(wireName: r'simplifiedKycMaxPremium')
  int get simplifiedKycMaxPremium;

  @BuiltValueField(wireName: r'totalAmount')
  int get totalAmount;

  @BuiltValueField(wireName: r'vehicleValue')
  int? get vehicleValue;

  UnderwritingInputs._();

  factory UnderwritingInputs([void updates(UnderwritingInputsBuilder b)]) = _$UnderwritingInputs;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UnderwritingInputsBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UnderwritingInputs> get serializer => _$UnderwritingInputsSerializer();
}

class _$UnderwritingInputsSerializer implements PrimitiveSerializer<UnderwritingInputs> {
  @override
  final Iterable<Type> types = const [UnderwritingInputs, _$UnderwritingInputs];

  @override
  final String wireName = r'UnderwritingInputs';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UnderwritingInputs object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'hasOwnDamage';
    yield serializers.serialize(
      object.hasOwnDamage,
      specifiedType: const FullType(bool),
    );
    if (object.inspectionStatus != null) {
      yield r'inspectionStatus';
      yield serializers.serialize(
        object.inspectionStatus,
        specifiedType: const FullType(InspectionStatus),
      );
    }
    yield r'kycState';
    yield serializers.serialize(
      object.kycState,
      specifiedType: const FullType(KycState),
    );
    yield r'maxVehicleValue';
    yield serializers.serialize(
      object.maxVehicleValue,
      specifiedType: const FullType(int),
    );
    yield r'positiveDeclarations';
    yield serializers.serialize(
      object.positiveDeclarations,
      specifiedType: const FullType(BuiltList, [FullType(DeclarationQuestion)]),
    );
    yield r'simplifiedKycMaxPremium';
    yield serializers.serialize(
      object.simplifiedKycMaxPremium,
      specifiedType: const FullType(int),
    );
    yield r'totalAmount';
    yield serializers.serialize(
      object.totalAmount,
      specifiedType: const FullType(int),
    );
    if (object.vehicleValue != null) {
      yield r'vehicleValue';
      yield serializers.serialize(
        object.vehicleValue,
        specifiedType: const FullType.nullable(int),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    UnderwritingInputs object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UnderwritingInputsBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'hasOwnDamage':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.hasOwnDamage = valueDes;
          break;
        case r'inspectionStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionStatus),
          ) as InspectionStatus;
          result.inspectionStatus = valueDes;
          break;
        case r'kycState':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycState),
          ) as KycState;
          result.kycState = valueDes;
          break;
        case r'maxVehicleValue':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxVehicleValue = valueDes;
          break;
        case r'positiveDeclarations':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(DeclarationQuestion)]),
          ) as BuiltList<DeclarationQuestion>;
          result.positiveDeclarations.replace(valueDes);
          break;
        case r'simplifiedKycMaxPremium':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.simplifiedKycMaxPremium = valueDes;
          break;
        case r'totalAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.totalAmount = valueDes;
          break;
        case r'vehicleValue':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.vehicleValue = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UnderwritingInputs deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UnderwritingInputsBuilder();
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

