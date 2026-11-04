//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/proposal_status.dart';
import 'package:moseguros_api/src/model/proposal_policyholder.dart';
import 'package:moseguros_api/src/model/proposal_kyc.dart';
import 'package:moseguros_api/src/model/proposal_channel.dart';
import 'package:moseguros_api/src/model/proposal_inspection.dart';
import 'package:moseguros_api/src/model/proposal_product.dart';
import 'package:moseguros_api/src/model/proposal_suitability.dart';
import 'package:moseguros_api/src/model/proposal_checklist.dart';
import 'package:moseguros_api/src/model/proposal_coverage.dart';
import 'package:moseguros_api/src/model/proposal_organization.dart';
import 'package:moseguros_api/src/model/proposal_signature.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/proposal_driver.dart';
import 'package:moseguros_api/src/model/proposal_vehicle.dart';
import 'package:built_value/json_object.dart';
import 'package:moseguros_api/src/model/proposal_declarations.dart';
import 'package:moseguros_api/src/model/proposal_premium.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_response.g.dart';

/// ProposalResponse
///
/// Properties:
/// * [broker] 
/// * [channel] 
/// * [checklist] 
/// * [coverages] 
/// * [createdAt] 
/// * [decision] - Decisão da seguradora (S4-05); null antes de existir.
/// * [declarations] 
/// * [driver] 
/// * [durationDays] 
/// * [endAt] - Exclusivo: 00:00 (Africa/Maputo) do dia seguinte ao último dia coberto
/// * [endDate] - Último dia coberto (Africa/Maputo)
/// * [expiresAt] - Fim do prazo sem acção do cliente; null em UNDER_REVIEW e nos finais
/// * [id] 
/// * [inspection] 
/// * [insurer] 
/// * [kyc] 
/// * [number] 
/// * [policyholder] 
/// * [premium] 
/// * [product] 
/// * [quoteId] 
/// * [signature] 
/// * [startAt] - 00:00 do primeiro dia em Africa/Maputo
/// * [status] 
/// * [submittedAt] - Primeira submissão (S4-05)
/// * [suitability] 
/// * [updatedAt] 
/// * [vehicle] 
/// * [version] - Versão para as escritas; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class ProposalResponse implements Built<ProposalResponse, ProposalResponseBuilder> {
  @BuiltValueField(wireName: r'broker')
  ProposalOrganization? get broker;

  @BuiltValueField(wireName: r'channel')
  ProposalChannel get channel;
  // enum channelEnum {  APP,  WEB,  BROKER,  };

  @BuiltValueField(wireName: r'checklist')
  ProposalChecklist get checklist;

  @BuiltValueField(wireName: r'coverages')
  BuiltList<ProposalCoverage> get coverages;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  /// Decisão da seguradora (S4-05); null antes de existir.
  @BuiltValueField(wireName: r'decision')
  BuiltMap<String, JsonObject?>? get decision;

  @BuiltValueField(wireName: r'declarations')
  ProposalDeclarations? get declarations;

  @BuiltValueField(wireName: r'driver')
  ProposalDriver get driver;

  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  /// Exclusivo: 00:00 (Africa/Maputo) do dia seguinte ao último dia coberto
  @BuiltValueField(wireName: r'endAt')
  DateTime get endAt;

  /// Último dia coberto (Africa/Maputo)
  @BuiltValueField(wireName: r'endDate')
  Date get endDate;

  /// Fim do prazo sem acção do cliente; null em UNDER_REVIEW e nos finais
  @BuiltValueField(wireName: r'expiresAt')
  DateTime? get expiresAt;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'inspection')
  ProposalInspection get inspection;

  @BuiltValueField(wireName: r'insurer')
  ProposalOrganization? get insurer;

  @BuiltValueField(wireName: r'kyc')
  ProposalKyc get kyc;

  @BuiltValueField(wireName: r'number')
  String get number;

  @BuiltValueField(wireName: r'policyholder')
  ProposalPolicyholder get policyholder;

  @BuiltValueField(wireName: r'premium')
  ProposalPremium get premium;

  @BuiltValueField(wireName: r'product')
  ProposalProduct get product;

  @BuiltValueField(wireName: r'quoteId')
  String get quoteId;

  @BuiltValueField(wireName: r'signature')
  ProposalSignature? get signature;

  /// 00:00 do primeiro dia em Africa/Maputo
  @BuiltValueField(wireName: r'startAt')
  DateTime get startAt;

  @BuiltValueField(wireName: r'status')
  ProposalStatus get status;
  // enum statusEnum {  DRAFT,  UNDER_REVIEW,  INFO_REQUESTED,  ACCEPTED,  REJECTED,  EXPIRED,  PAID,  CONVERTED,  };

  /// Primeira submissão (S4-05)
  @BuiltValueField(wireName: r'submittedAt')
  DateTime? get submittedAt;

  @BuiltValueField(wireName: r'suitability')
  ProposalSuitability? get suitability;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'vehicle')
  ProposalVehicle get vehicle;

  /// Versão para as escritas; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  ProposalResponse._();

  factory ProposalResponse([void updates(ProposalResponseBuilder b)]) = _$ProposalResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalResponse> get serializer => _$ProposalResponseSerializer();
}

class _$ProposalResponseSerializer implements PrimitiveSerializer<ProposalResponse> {
  @override
  final Iterable<Type> types = const [ProposalResponse, _$ProposalResponse];

  @override
  final String wireName = r'ProposalResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.broker != null) {
      yield r'broker';
      yield serializers.serialize(
        object.broker,
        specifiedType: const FullType.nullable(ProposalOrganization),
      );
    }
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(ProposalChannel),
    );
    yield r'checklist';
    yield serializers.serialize(
      object.checklist,
      specifiedType: const FullType(ProposalChecklist),
    );
    yield r'coverages';
    yield serializers.serialize(
      object.coverages,
      specifiedType: const FullType(BuiltList, [FullType(ProposalCoverage)]),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.decision != null) {
      yield r'decision';
      yield serializers.serialize(
        object.decision,
        specifiedType: const FullType(BuiltMap, [FullType(String), FullType.nullable(JsonObject)]),
      );
    }
    if (object.declarations != null) {
      yield r'declarations';
      yield serializers.serialize(
        object.declarations,
        specifiedType: const FullType.nullable(ProposalDeclarations),
      );
    }
    yield r'driver';
    yield serializers.serialize(
      object.driver,
      specifiedType: const FullType(ProposalDriver),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
    yield r'endAt';
    yield serializers.serialize(
      object.endAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'endDate';
    yield serializers.serialize(
      object.endDate,
      specifiedType: const FullType(Date),
    );
    if (object.expiresAt != null) {
      yield r'expiresAt';
      yield serializers.serialize(
        object.expiresAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'inspection';
    yield serializers.serialize(
      object.inspection,
      specifiedType: const FullType(ProposalInspection),
    );
    yield r'insurer';
    yield object.insurer == null ? null : serializers.serialize(
      object.insurer,
      specifiedType: const FullType.nullable(ProposalOrganization),
    );
    yield r'kyc';
    yield serializers.serialize(
      object.kyc,
      specifiedType: const FullType(ProposalKyc),
    );
    yield r'number';
    yield serializers.serialize(
      object.number,
      specifiedType: const FullType(String),
    );
    yield r'policyholder';
    yield serializers.serialize(
      object.policyholder,
      specifiedType: const FullType(ProposalPolicyholder),
    );
    yield r'premium';
    yield serializers.serialize(
      object.premium,
      specifiedType: const FullType(ProposalPremium),
    );
    yield r'product';
    yield serializers.serialize(
      object.product,
      specifiedType: const FullType(ProposalProduct),
    );
    yield r'quoteId';
    yield serializers.serialize(
      object.quoteId,
      specifiedType: const FullType(String),
    );
    if (object.signature != null) {
      yield r'signature';
      yield serializers.serialize(
        object.signature,
        specifiedType: const FullType.nullable(ProposalSignature),
      );
    }
    yield r'startAt';
    yield serializers.serialize(
      object.startAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(ProposalStatus),
    );
    if (object.submittedAt != null) {
      yield r'submittedAt';
      yield serializers.serialize(
        object.submittedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.suitability != null) {
      yield r'suitability';
      yield serializers.serialize(
        object.suitability,
        specifiedType: const FullType.nullable(ProposalSuitability),
      );
    }
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(ProposalVehicle),
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
    ProposalResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'broker':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(ProposalOrganization),
          ) as ProposalOrganization?;
          if (valueDes == null) continue;
          result.broker.replace(valueDes);
          break;
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChannel),
          ) as ProposalChannel;
          result.channel = valueDes;
          break;
        case r'checklist':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChecklist),
          ) as ProposalChecklist;
          result.checklist.replace(valueDes);
          break;
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(ProposalCoverage)]),
          ) as BuiltList<ProposalCoverage>;
          result.coverages.replace(valueDes);
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'decision':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType.nullable(JsonObject)]),
          ) as BuiltMap<String, JsonObject?>;
          result.decision.replace(valueDes);
          break;
        case r'declarations':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(ProposalDeclarations),
          ) as ProposalDeclarations?;
          if (valueDes == null) continue;
          result.declarations.replace(valueDes);
          break;
        case r'driver':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalDriver),
          ) as ProposalDriver;
          result.driver.replace(valueDes);
          break;
        case r'durationDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationDays = valueDes;
          break;
        case r'endAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.endAt = valueDes;
          break;
        case r'endDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.endDate = valueDes;
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.expiresAt = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'inspection':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalInspection),
          ) as ProposalInspection;
          result.inspection.replace(valueDes);
          break;
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(ProposalOrganization),
          ) as ProposalOrganization?;
          if (valueDes == null) continue;
          result.insurer.replace(valueDes);
          break;
        case r'kyc':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalKyc),
          ) as ProposalKyc;
          result.kyc.replace(valueDes);
          break;
        case r'number':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.number = valueDes;
          break;
        case r'policyholder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalPolicyholder),
          ) as ProposalPolicyholder;
          result.policyholder.replace(valueDes);
          break;
        case r'premium':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalPremium),
          ) as ProposalPremium;
          result.premium.replace(valueDes);
          break;
        case r'product':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalProduct),
          ) as ProposalProduct;
          result.product.replace(valueDes);
          break;
        case r'quoteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.quoteId = valueDes;
          break;
        case r'signature':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(ProposalSignature),
          ) as ProposalSignature?;
          if (valueDes == null) continue;
          result.signature.replace(valueDes);
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
            specifiedType: const FullType(ProposalStatus),
          ) as ProposalStatus;
          result.status = valueDes;
          break;
        case r'submittedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.submittedAt = valueDes;
          break;
        case r'suitability':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(ProposalSuitability),
          ) as ProposalSuitability?;
          if (valueDes == null) continue;
          result.suitability.replace(valueDes);
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'vehicle':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalVehicle),
          ) as ProposalVehicle;
          result.vehicle.replace(valueDes);
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
  ProposalResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalResponseBuilder();
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

