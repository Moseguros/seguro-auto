//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_driver_request.g.dart';

/// CreateDriverRequest
///
/// Properties:
/// * [birthDate] - No passado; idade até 100 anos
/// * [fullName] - Letras, espaços, apóstrofo e hífen; pelo menos duas palavras
/// * [isOwner] - O condutor é o próprio cliente (defeito false); só um por cliente
/// * [licenceCategories] - 1–12 códigos activos em LICENCE_CATEGORY, sem repetidos
/// * [licenceIssueDate] - Entre o nascimento e hoje
/// * [licenceNumber] - 5–20 letras, dígitos, / ou - (espaços ignorados). Nunca é devolvido.
@BuiltValue()
abstract class CreateDriverRequest implements Built<CreateDriverRequest, CreateDriverRequestBuilder> {
  /// No passado; idade até 100 anos
  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  /// Letras, espaços, apóstrofo e hífen; pelo menos duas palavras
  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  /// O condutor é o próprio cliente (defeito false); só um por cliente
  @BuiltValueField(wireName: r'isOwner')
  bool? get isOwner;

  /// 1–12 códigos activos em LICENCE_CATEGORY, sem repetidos
  @BuiltValueField(wireName: r'licenceCategories')
  BuiltList<String> get licenceCategories;

  /// Entre o nascimento e hoje
  @BuiltValueField(wireName: r'licenceIssueDate')
  Date get licenceIssueDate;

  /// 5–20 letras, dígitos, / ou - (espaços ignorados). Nunca é devolvido.
  @BuiltValueField(wireName: r'licenceNumber')
  String get licenceNumber;

  CreateDriverRequest._();

  factory CreateDriverRequest([void updates(CreateDriverRequestBuilder b)]) = _$CreateDriverRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateDriverRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateDriverRequest> get serializer => _$CreateDriverRequestSerializer();
}

class _$CreateDriverRequestSerializer implements PrimitiveSerializer<CreateDriverRequest> {
  @override
  final Iterable<Type> types = const [CreateDriverRequest, _$CreateDriverRequest];

  @override
  final String wireName = r'CreateDriverRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateDriverRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'birthDate';
    yield serializers.serialize(
      object.birthDate,
      specifiedType: const FullType(Date),
    );
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
      specifiedType: const FullType(String),
    );
    if (object.isOwner != null) {
      yield r'isOwner';
      yield serializers.serialize(
        object.isOwner,
        specifiedType: const FullType(bool),
      );
    }
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
    yield r'licenceNumber';
    yield serializers.serialize(
      object.licenceNumber,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateDriverRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateDriverRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'birthDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.birthDate = valueDes;
          break;
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'isOwner':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.isOwner = valueDes;
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
        case r'licenceNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.licenceNumber = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateDriverRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateDriverRequestBuilder();
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

