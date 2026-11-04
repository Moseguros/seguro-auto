//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_policyholder.g.dart';

/// ProposalPolicyholder
///
/// Properties:
/// * [address] 
/// * [birthDate] 
/// * [emailMasked] 
/// * [fullName] 
/// * [idDocNumberMasked] 
/// * [idDocType] 
/// * [msisdnMasked] 
/// * [nuitMasked] 
/// * [province] 
@BuiltValue()
abstract class ProposalPolicyholder implements Built<ProposalPolicyholder, ProposalPolicyholderBuilder> {
  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  @BuiltValueField(wireName: r'emailMasked')
  String? get emailMasked;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'idDocNumberMasked')
  String get idDocNumberMasked;

  @BuiltValueField(wireName: r'idDocType')
  String get idDocType;

  @BuiltValueField(wireName: r'msisdnMasked')
  String? get msisdnMasked;

  @BuiltValueField(wireName: r'nuitMasked')
  String get nuitMasked;

  @BuiltValueField(wireName: r'province')
  String get province;

  ProposalPolicyholder._();

  factory ProposalPolicyholder([void updates(ProposalPolicyholderBuilder b)]) = _$ProposalPolicyholder;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalPolicyholderBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalPolicyholder> get serializer => _$ProposalPolicyholderSerializer();
}

class _$ProposalPolicyholderSerializer implements PrimitiveSerializer<ProposalPolicyholder> {
  @override
  final Iterable<Type> types = const [ProposalPolicyholder, _$ProposalPolicyholder];

  @override
  final String wireName = r'ProposalPolicyholder';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalPolicyholder object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.address != null) {
      yield r'address';
      yield serializers.serialize(
        object.address,
        specifiedType: const FullType(String),
      );
    }
    yield r'birthDate';
    yield serializers.serialize(
      object.birthDate,
      specifiedType: const FullType(Date),
    );
    if (object.emailMasked != null) {
      yield r'emailMasked';
      yield serializers.serialize(
        object.emailMasked,
        specifiedType: const FullType(String),
      );
    }
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
      specifiedType: const FullType(String),
    );
    yield r'idDocNumberMasked';
    yield serializers.serialize(
      object.idDocNumberMasked,
      specifiedType: const FullType(String),
    );
    yield r'idDocType';
    yield serializers.serialize(
      object.idDocType,
      specifiedType: const FullType(String),
    );
    if (object.msisdnMasked != null) {
      yield r'msisdnMasked';
      yield serializers.serialize(
        object.msisdnMasked,
        specifiedType: const FullType(String),
      );
    }
    yield r'nuitMasked';
    yield serializers.serialize(
      object.nuitMasked,
      specifiedType: const FullType(String),
    );
    yield r'province';
    yield serializers.serialize(
      object.province,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalPolicyholder object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalPolicyholderBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'birthDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.birthDate = valueDes;
          break;
        case r'emailMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.emailMasked = valueDes;
          break;
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'idDocNumberMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.idDocNumberMasked = valueDes;
          break;
        case r'idDocType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.idDocType = valueDes;
          break;
        case r'msisdnMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.msisdnMasked = valueDes;
          break;
        case r'nuitMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.nuitMasked = valueDes;
          break;
        case r'province':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.province = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalPolicyholder deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalPolicyholderBuilder();
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

