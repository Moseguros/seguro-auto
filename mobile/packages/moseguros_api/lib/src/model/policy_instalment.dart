//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/instalment_status.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_instalment.g.dart';

/// PolicyInstalment
///
/// Properties:
/// * [amount] 
/// * [dueDate] 
/// * [paidAt] 
/// * [seq] 
/// * [status] 
@BuiltValue()
abstract class PolicyInstalment implements Built<PolicyInstalment, PolicyInstalmentBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money get amount;

  @BuiltValueField(wireName: r'dueDate')
  Date get dueDate;

  @BuiltValueField(wireName: r'paidAt')
  DateTime? get paidAt;

  @BuiltValueField(wireName: r'seq')
  int get seq;

  @BuiltValueField(wireName: r'status')
  InstalmentStatus get status;
  // enum statusEnum {  DUE,  PAID,  OVERDUE,  CANCELLED,  };

  PolicyInstalment._();

  factory PolicyInstalment([void updates(PolicyInstalmentBuilder b)]) = _$PolicyInstalment;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PolicyInstalmentBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PolicyInstalment> get serializer => _$PolicyInstalmentSerializer();
}

class _$PolicyInstalmentSerializer implements PrimitiveSerializer<PolicyInstalment> {
  @override
  final Iterable<Type> types = const [PolicyInstalment, _$PolicyInstalment];

  @override
  final String wireName = r'PolicyInstalment';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PolicyInstalment object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'amount';
    yield serializers.serialize(
      object.amount,
      specifiedType: const FullType(Money),
    );
    yield r'dueDate';
    yield serializers.serialize(
      object.dueDate,
      specifiedType: const FullType(Date),
    );
    if (object.paidAt != null) {
      yield r'paidAt';
      yield serializers.serialize(
        object.paidAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'seq';
    yield serializers.serialize(
      object.seq,
      specifiedType: const FullType(int),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(InstalmentStatus),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PolicyInstalment object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PolicyInstalmentBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'amount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.amount.replace(valueDes);
          break;
        case r'dueDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.dueDate = valueDes;
          break;
        case r'paidAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.paidAt = valueDes;
          break;
        case r'seq':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.seq = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InstalmentStatus),
          ) as InstalmentStatus;
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
  PolicyInstalment deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PolicyInstalmentBuilder();
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

