//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_proposal_driver.g.dart';

/// InsurerProposalDriver
///
/// Properties:
/// * [birthDate] 
/// * [fullName] 
/// * [id] 
/// * [licenceCategories] 
/// * [licenceIssueDate] 
/// * [licenceNumberMasked] 
@BuiltValue()
abstract class InsurerProposalDriver implements Built<InsurerProposalDriver, InsurerProposalDriverBuilder> {
  @BuiltValueField(wireName: r'birthDate')
  Date? get birthDate;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'licenceCategories')
  BuiltList<String> get licenceCategories;

  @BuiltValueField(wireName: r'licenceIssueDate')
  Date get licenceIssueDate;

  @BuiltValueField(wireName: r'licenceNumberMasked')
  String get licenceNumberMasked;

  InsurerProposalDriver._();

  factory InsurerProposalDriver([void updates(InsurerProposalDriverBuilder b)]) = _$InsurerProposalDriver;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerProposalDriverBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerProposalDriver> get serializer => _$InsurerProposalDriverSerializer();
}

class _$InsurerProposalDriverSerializer implements PrimitiveSerializer<InsurerProposalDriver> {
  @override
  final Iterable<Type> types = const [InsurerProposalDriver, _$InsurerProposalDriver];

  @override
  final String wireName = r'InsurerProposalDriver';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerProposalDriver object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.birthDate != null) {
      yield r'birthDate';
      yield serializers.serialize(
        object.birthDate,
        specifiedType: const FullType.nullable(Date),
      );
    }
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
    yield r'licenceCategories';
    yield serializers.serialize(
      object.licenceCategories,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
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
    InsurerProposalDriver object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerProposalDriverBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'birthDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(Date),
          ) as Date?;
          if (valueDes == null) continue;
          result.birthDate = valueDes;
          break;
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
        case r'licenceCategories':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.licenceCategories.replace(valueDes);
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
  InsurerProposalDriver deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerProposalDriverBuilder();
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

