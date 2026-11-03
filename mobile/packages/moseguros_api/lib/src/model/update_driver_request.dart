//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_driver_request.g.dart';

/// UpdateDriverRequest
///
/// Properties:
/// * [birthDate] 
/// * [fullName] 
/// * [isOwner] 
/// * [licenceCategories] - 1–12 códigos activos em LICENCE_CATEGORY, sem repetidos
/// * [licenceIssueDate] 
/// * [licenceNumber] - Omitido ou null mantém o actual
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateDriverRequest implements Built<UpdateDriverRequest, UpdateDriverRequestBuilder> {
  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'isOwner')
  bool? get isOwner;

  /// 1–12 códigos activos em LICENCE_CATEGORY, sem repetidos
  @BuiltValueField(wireName: r'licenceCategories')
  BuiltList<String> get licenceCategories;

  @BuiltValueField(wireName: r'licenceIssueDate')
  Date get licenceIssueDate;

  /// Omitido ou null mantém o actual
  @BuiltValueField(wireName: r'licenceNumber')
  String? get licenceNumber;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateDriverRequest._();

  factory UpdateDriverRequest([void updates(UpdateDriverRequestBuilder b)]) = _$UpdateDriverRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateDriverRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateDriverRequest> get serializer => _$UpdateDriverRequestSerializer();
}

class _$UpdateDriverRequestSerializer implements PrimitiveSerializer<UpdateDriverRequest> {
  @override
  final Iterable<Type> types = const [UpdateDriverRequest, _$UpdateDriverRequest];

  @override
  final String wireName = r'UpdateDriverRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateDriverRequest object, {
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
    if (object.licenceNumber != null) {
      yield r'licenceNumber';
      yield serializers.serialize(
        object.licenceNumber,
        specifiedType: const FullType(String),
      );
    }
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateDriverRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateDriverRequestBuilder result,
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
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UpdateDriverRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateDriverRequestBuilder();
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

