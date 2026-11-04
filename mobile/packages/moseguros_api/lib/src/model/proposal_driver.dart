//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_driver.g.dart';

/// ProposalDriver
///
/// Properties:
/// * [fullName] 
/// * [id] 
/// * [licenceIssueDate] 
/// * [licenceNumberMasked] 
@BuiltValue()
abstract class ProposalDriver implements Built<ProposalDriver, ProposalDriverBuilder> {
  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'licenceIssueDate')
  Date get licenceIssueDate;

  @BuiltValueField(wireName: r'licenceNumberMasked')
  String get licenceNumberMasked;

  ProposalDriver._();

  factory ProposalDriver([void updates(ProposalDriverBuilder b)]) = _$ProposalDriver;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalDriverBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalDriver> get serializer => _$ProposalDriverSerializer();
}

class _$ProposalDriverSerializer implements PrimitiveSerializer<ProposalDriver> {
  @override
  final Iterable<Type> types = const [ProposalDriver, _$ProposalDriver];

  @override
  final String wireName = r'ProposalDriver';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalDriver object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'licenceIssueDate';
    yield serializers.serialize(
      object.licenceIssueDate,
      specifiedType: const FullType(Date),
    );
    yield r'licenceNumberMasked';
    yield serializers.serialize(
      object.licenceNumberMasked,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalDriver object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalDriverBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'licenceIssueDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.licenceIssueDate = valueDes;
          break;
        case r'licenceNumberMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.licenceNumberMasked = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalDriver deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalDriverBuilder();
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

