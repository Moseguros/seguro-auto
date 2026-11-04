//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/sla_status.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_proposal_sla.g.dart';

/// InsurerProposalSla
///
/// Properties:
/// * [alertAt] 
/// * [alertedAt] 
/// * [dueAt] 
/// * [reviewStartedAt] - Entrada em UNDER_REVIEW (reinicia em cada submissão)
/// * [status] 
@BuiltValue()
abstract class InsurerProposalSla implements Built<InsurerProposalSla, InsurerProposalSlaBuilder> {
  @BuiltValueField(wireName: r'alertAt')
  DateTime? get alertAt;

  @BuiltValueField(wireName: r'alertedAt')
  DateTime? get alertedAt;

  @BuiltValueField(wireName: r'dueAt')
  DateTime? get dueAt;

  /// Entrada em UNDER_REVIEW (reinicia em cada submissão)
  @BuiltValueField(wireName: r'reviewStartedAt')
  DateTime? get reviewStartedAt;

  @BuiltValueField(wireName: r'status')
  SlaStatus? get status;
  // enum statusEnum {  ON_TIME,  ALERT,  OVERDUE,  };

  InsurerProposalSla._();

  factory InsurerProposalSla([void updates(InsurerProposalSlaBuilder b)]) = _$InsurerProposalSla;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerProposalSlaBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerProposalSla> get serializer => _$InsurerProposalSlaSerializer();
}

class _$InsurerProposalSlaSerializer implements PrimitiveSerializer<InsurerProposalSla> {
  @override
  final Iterable<Type> types = const [InsurerProposalSla, _$InsurerProposalSla];

  @override
  final String wireName = r'InsurerProposalSla';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerProposalSla object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.alertAt != null) {
      yield r'alertAt';
      yield serializers.serialize(
        object.alertAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.alertedAt != null) {
      yield r'alertedAt';
      yield serializers.serialize(
        object.alertedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.dueAt != null) {
      yield r'dueAt';
      yield serializers.serialize(
        object.dueAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.reviewStartedAt != null) {
      yield r'reviewStartedAt';
      yield serializers.serialize(
        object.reviewStartedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.status != null) {
      yield r'status';
      yield serializers.serialize(
        object.status,
        specifiedType: const FullType.nullable(SlaStatus),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerProposalSla object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerProposalSlaBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'alertAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.alertAt = valueDes;
          break;
        case r'alertedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.alertedAt = valueDes;
          break;
        case r'dueAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.dueAt = valueDes;
          break;
        case r'reviewStartedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.reviewStartedAt = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(SlaStatus),
          ) as SlaStatus?;
          if (valueDes == null) continue;
          result.status = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerProposalSla deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerProposalSlaBuilder();
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

