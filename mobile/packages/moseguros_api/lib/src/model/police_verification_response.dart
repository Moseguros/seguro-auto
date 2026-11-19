//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/certificate_check_status.dart';
import 'package:moseguros_api/src/model/coverage_type.dart';
import 'package:moseguros_api/src/model/verification_result.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'police_verification_response.g.dart';

/// Resultado da verificação para um agente da polícia; sem dados pessoais do tomador
///
/// Properties:
/// * [certificateNumber] - Certificado do código ou, por matrícula, o certificado em vigor
/// * [certificateStatus] 
/// * [checkedAt] 
/// * [coverageType] 
/// * [insurerName] 
/// * [plateMasked] 
/// * [policyNumber] 
/// * [status] 
/// * [validFrom] 
/// * [validTo] - Fim da cobertura (exclusivo)
/// * [vehicleMake] 
/// * [vehicleModel] 
@BuiltValue()
abstract class PoliceVerificationResponse implements Built<PoliceVerificationResponse, PoliceVerificationResponseBuilder> {
  /// Certificado do código ou, por matrícula, o certificado em vigor
  @BuiltValueField(wireName: r'certificateNumber')
  String? get certificateNumber;

  @BuiltValueField(wireName: r'certificateStatus')
  CertificateCheckStatus? get certificateStatus;
  // enum certificateStatusEnum {  CURRENT,  REVOKED,  };

  @BuiltValueField(wireName: r'checkedAt')
  DateTime get checkedAt;

  @BuiltValueField(wireName: r'coverageType')
  CoverageType? get coverageType;
  // enum coverageTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  };

  @BuiltValueField(wireName: r'insurerName')
  String? get insurerName;

  @BuiltValueField(wireName: r'plateMasked')
  String? get plateMasked;

  @BuiltValueField(wireName: r'policyNumber')
  String? get policyNumber;

  @BuiltValueField(wireName: r'status')
  VerificationResult get status;
  // enum statusEnum {  VALID,  NOT_YET_VALID,  EXPIRED,  SUSPENDED,  CANCELLED,  NOT_FOUND,  };

  @BuiltValueField(wireName: r'validFrom')
  DateTime? get validFrom;

  /// Fim da cobertura (exclusivo)
  @BuiltValueField(wireName: r'validTo')
  DateTime? get validTo;

  @BuiltValueField(wireName: r'vehicleMake')
  String? get vehicleMake;

  @BuiltValueField(wireName: r'vehicleModel')
  String? get vehicleModel;

  PoliceVerificationResponse._();

  factory PoliceVerificationResponse([void updates(PoliceVerificationResponseBuilder b)]) = _$PoliceVerificationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PoliceVerificationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PoliceVerificationResponse> get serializer => _$PoliceVerificationResponseSerializer();
}

class _$PoliceVerificationResponseSerializer implements PrimitiveSerializer<PoliceVerificationResponse> {
  @override
  final Iterable<Type> types = const [PoliceVerificationResponse, _$PoliceVerificationResponse];

  @override
  final String wireName = r'PoliceVerificationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PoliceVerificationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.certificateNumber != null) {
      yield r'certificateNumber';
      yield serializers.serialize(
        object.certificateNumber,
        specifiedType: const FullType(String),
      );
    }
    if (object.certificateStatus != null) {
      yield r'certificateStatus';
      yield serializers.serialize(
        object.certificateStatus,
        specifiedType: const FullType(CertificateCheckStatus),
      );
    }
    yield r'checkedAt';
    yield serializers.serialize(
      object.checkedAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.coverageType != null) {
      yield r'coverageType';
      yield serializers.serialize(
        object.coverageType,
        specifiedType: const FullType(CoverageType),
      );
    }
    if (object.insurerName != null) {
      yield r'insurerName';
      yield serializers.serialize(
        object.insurerName,
        specifiedType: const FullType(String),
      );
    }
    if (object.plateMasked != null) {
      yield r'plateMasked';
      yield serializers.serialize(
        object.plateMasked,
        specifiedType: const FullType(String),
      );
    }
    if (object.policyNumber != null) {
      yield r'policyNumber';
      yield serializers.serialize(
        object.policyNumber,
        specifiedType: const FullType(String),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(VerificationResult),
    );
    if (object.validFrom != null) {
      yield r'validFrom';
      yield serializers.serialize(
        object.validFrom,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.validTo != null) {
      yield r'validTo';
      yield serializers.serialize(
        object.validTo,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.vehicleMake != null) {
      yield r'vehicleMake';
      yield serializers.serialize(
        object.vehicleMake,
        specifiedType: const FullType(String),
      );
    }
    if (object.vehicleModel != null) {
      yield r'vehicleModel';
      yield serializers.serialize(
        object.vehicleModel,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PoliceVerificationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PoliceVerificationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'certificateNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.certificateNumber = valueDes;
          break;
        case r'certificateStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CertificateCheckStatus),
          ) as CertificateCheckStatus;
          result.certificateStatus = valueDes;
          break;
        case r'checkedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.checkedAt = valueDes;
          break;
        case r'coverageType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverageType),
          ) as CoverageType;
          result.coverageType = valueDes;
          break;
        case r'insurerName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.insurerName = valueDes;
          break;
        case r'plateMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.plateMasked = valueDes;
          break;
        case r'policyNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.policyNumber = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(VerificationResult),
          ) as VerificationResult;
          result.status = valueDes;
          break;
        case r'validFrom':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.validFrom = valueDes;
          break;
        case r'validTo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.validTo = valueDes;
          break;
        case r'vehicleMake':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehicleMake = valueDes;
          break;
        case r'vehicleModel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehicleModel = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PoliceVerificationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PoliceVerificationResponseBuilder();
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

