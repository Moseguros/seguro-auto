//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_inspection.g.dart';

/// ProposalInspection
///
/// Properties:
/// * [required_] 
@BuiltValue()
abstract class ProposalInspection implements Built<ProposalInspection, ProposalInspectionBuilder> {
  @BuiltValueField(wireName: r'required')
  bool get required_;

  ProposalInspection._();

  factory ProposalInspection([void updates(ProposalInspectionBuilder b)]) = _$ProposalInspection;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalInspectionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalInspection> get serializer => _$ProposalInspectionSerializer();
}

class _$ProposalInspectionSerializer implements PrimitiveSerializer<ProposalInspection> {
  @override
  final Iterable<Type> types = const [ProposalInspection, _$ProposalInspection];

  @override
  final String wireName = r'ProposalInspection';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalInspection object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'required';
    yield serializers.serialize(
      object.required_,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalInspection object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalInspectionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'required':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.required_ = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalInspection deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalInspectionBuilder();
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

