//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_organization.g.dart';

/// Mediador (S4-06); null nas propostas do cliente
///
/// Properties:
/// * [displayName] 
/// * [id] 
@BuiltValue()
abstract class ProposalOrganization implements Built<ProposalOrganization, ProposalOrganizationBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  ProposalOrganization._();

  factory ProposalOrganization([void updates(ProposalOrganizationBuilder b)]) = _$ProposalOrganization;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalOrganizationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalOrganization> get serializer => _$ProposalOrganizationSerializer();
}

class _$ProposalOrganizationSerializer implements PrimitiveSerializer<ProposalOrganization> {
  @override
  final Iterable<Type> types = const [ProposalOrganization, _$ProposalOrganization];

  @override
  final String wireName = r'ProposalOrganization';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalOrganization object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'displayName';
    yield serializers.serialize(
      object.displayName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalOrganization object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalOrganizationBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalOrganization deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalOrganizationBuilder();
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

