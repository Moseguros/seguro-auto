//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_proposal_ref.g.dart';

/// Só no detalhe da seguradora
///
/// Properties:
/// * [id] 
/// * [number] 
@BuiltValue()
abstract class PolicyProposalRef implements Built<PolicyProposalRef, PolicyProposalRefBuilder> {
  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'number')
  String? get number;

  PolicyProposalRef._();

  factory PolicyProposalRef([void updates(PolicyProposalRefBuilder b)]) = _$PolicyProposalRef;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PolicyProposalRefBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PolicyProposalRef> get serializer => _$PolicyProposalRefSerializer();
}

class _$PolicyProposalRefSerializer implements PrimitiveSerializer<PolicyProposalRef> {
  @override
  final Iterable<Type> types = const [PolicyProposalRef, _$PolicyProposalRef];

  @override
  final String wireName = r'PolicyProposalRef';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PolicyProposalRef object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.number != null) {
      yield r'number';
      yield serializers.serialize(
        object.number,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PolicyProposalRef object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PolicyProposalRefBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'number':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.number = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PolicyProposalRef deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PolicyProposalRefBuilder();
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

