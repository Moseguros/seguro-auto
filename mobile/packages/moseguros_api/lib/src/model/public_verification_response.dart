//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/certificate_check_status.dart';
import 'package:moseguros_api/src/model/coverage_type.dart';
import 'package:moseguros_api/src/model/verification_result.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'public_verification_response.g.dart';

/// Resultado da verificação pública; sem dados pessoais do tomador
///
/// Properties:
/// * [certificateStatus] 
/// * [checkedAt] 
/// * [coverageType] 
/// * [insurerName] 
/// * [plateMasked] - Matrícula com os dígitos trocados por *
/// * [status] 
/// * [validFrom] 
/// * [validTo] - Fim da cobertura (exclusivo)
@BuiltValue()
abstract class PublicVerificationResponse implements Built<PublicVerificationResponse, PublicVerificationResponseBuilder> {
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

  /// Matrícula com os dígitos trocados por *
  @BuiltValueField(wireName: r'plateMasked')
  String? get plateMasked;

  @BuiltValueField(wireName: r'status')
  VerificationResult get status;
  // enum statusEnum {  VALID,  NOT_YET_VALID,  EXPIRED,  SUSPENDED,  CANCELLED,  NOT_FOUND,  };

  @BuiltValueField(wireName: r'validFrom')
  DateTime? get validFrom;

  /// Fim da cobertura (exclusivo)
  @BuiltValueField(wireName: r'validTo')
  DateTime? get validTo;

  PublicVerificationResponse._();

  factory PublicVerificationResponse([void updates(PublicVerificationResponseBuilder b)]) = _$PublicVerificationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublicVerificationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublicVerificationResponse> get serializer => _$PublicVerificationResponseSerializer();
}

class _$PublicVerificationResponseSerializer implements PrimitiveSerializer<PublicVerificationResponse> {
  @override
  final Iterable<Type> types = const [PublicVerificationResponse, _$PublicVerificationResponse];

  @override
  final String wireName = r'PublicVerificationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublicVerificationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
  }

  @override
  Object serialize(
    Serializers serializers,
    PublicVerificationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublicVerificationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PublicVerificationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublicVerificationResponseBuilder();
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

