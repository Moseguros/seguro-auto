//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/kyc_state.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_kyc.g.dart';

/// ProposalKyc
///
/// Properties:
/// * [requiredForAutomaticAcceptance] - S4-05; null nesta fase
/// * [state] 
@BuiltValue()
abstract class ProposalKyc implements Built<ProposalKyc, ProposalKycBuilder> {
  /// S4-05; null nesta fase
  @BuiltValueField(wireName: r'requiredForAutomaticAcceptance')
  bool? get requiredForAutomaticAcceptance;

  @BuiltValueField(wireName: r'state')
  KycState get state;
  // enum stateEnum {  NONE,  PENDING,  VERIFIED,  REJECTED,  OUTDATED,  };

  ProposalKyc._();

  factory ProposalKyc([void updates(ProposalKycBuilder b)]) = _$ProposalKyc;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalKycBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalKyc> get serializer => _$ProposalKycSerializer();
}

class _$ProposalKycSerializer implements PrimitiveSerializer<ProposalKyc> {
  @override
  final Iterable<Type> types = const [ProposalKyc, _$ProposalKyc];

  @override
  final String wireName = r'ProposalKyc';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalKyc object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.requiredForAutomaticAcceptance != null) {
      yield r'requiredForAutomaticAcceptance';
      yield serializers.serialize(
        object.requiredForAutomaticAcceptance,
        specifiedType: const FullType.nullable(bool),
      );
    }
    yield r'state';
    yield serializers.serialize(
      object.state,
      specifiedType: const FullType(KycState),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalKyc object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalKycBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'requiredForAutomaticAcceptance':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(bool),
          ) as bool?;
          if (valueDes == null) continue;
          result.requiredForAutomaticAcceptance = valueDes;
          break;
        case r'state':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycState),
          ) as KycState;
          result.state = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalKyc deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalKycBuilder();
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

