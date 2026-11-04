//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/kyc_state.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_proposal_kyc.g.dart';

/// InsurerProposalKyc
///
/// Properties:
/// * [checkId] 
/// * [requiredForAutomaticAcceptance] 
/// * [state] 
/// * [verifiedAt] - Só em VERIFIED e OUTDATED
/// * [verifiedByThisInsurer] 
@BuiltValue()
abstract class InsurerProposalKyc implements Built<InsurerProposalKyc, InsurerProposalKycBuilder> {
  @BuiltValueField(wireName: r'checkId')
  String? get checkId;

  @BuiltValueField(wireName: r'requiredForAutomaticAcceptance')
  bool get requiredForAutomaticAcceptance;

  @BuiltValueField(wireName: r'state')
  KycState get state;
  // enum stateEnum {  NONE,  PENDING,  VERIFIED,  REJECTED,  OUTDATED,  };

  /// Só em VERIFIED e OUTDATED
  @BuiltValueField(wireName: r'verifiedAt')
  DateTime? get verifiedAt;

  @BuiltValueField(wireName: r'verifiedByThisInsurer')
  bool get verifiedByThisInsurer;

  InsurerProposalKyc._();

  factory InsurerProposalKyc([void updates(InsurerProposalKycBuilder b)]) = _$InsurerProposalKyc;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerProposalKycBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerProposalKyc> get serializer => _$InsurerProposalKycSerializer();
}

class _$InsurerProposalKycSerializer implements PrimitiveSerializer<InsurerProposalKyc> {
  @override
  final Iterable<Type> types = const [InsurerProposalKyc, _$InsurerProposalKyc];

  @override
  final String wireName = r'InsurerProposalKyc';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerProposalKyc object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.checkId != null) {
      yield r'checkId';
      yield serializers.serialize(
        object.checkId,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'requiredForAutomaticAcceptance';
    yield serializers.serialize(
      object.requiredForAutomaticAcceptance,
      specifiedType: const FullType(bool),
    );
    yield r'state';
    yield serializers.serialize(
      object.state,
      specifiedType: const FullType(KycState),
    );
    if (object.verifiedAt != null) {
      yield r'verifiedAt';
      yield serializers.serialize(
        object.verifiedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'verifiedByThisInsurer';
    yield serializers.serialize(
      object.verifiedByThisInsurer,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerProposalKyc object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerProposalKycBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'checkId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.checkId = valueDes;
          break;
        case r'requiredForAutomaticAcceptance':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.requiredForAutomaticAcceptance = valueDes;
          break;
        case r'state':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycState),
          ) as KycState;
          result.state = valueDes;
          break;
        case r'verifiedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.verifiedAt = valueDes;
          break;
        case r'verifiedByThisInsurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.verifiedByThisInsurer = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerProposalKyc deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerProposalKycBuilder();
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

