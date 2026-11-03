//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'driver_response.g.dart';

/// DriverResponse
///
/// Properties:
/// * [birthDate] 
/// * [createdAt] 
/// * [fullName] 
/// * [id] 
/// * [isOwner] 
/// * [licenceCategories] 
/// * [licenceIssueDate] 
/// * [licenceNumberMasked] - Quatro * fixos e os últimos 4 caracteres
/// * [updatedAt] 
/// * [vehicleIds] - Veículos activos a que está associado
/// * [version] 
@BuiltValue()
abstract class DriverResponse implements Built<DriverResponse, DriverResponseBuilder> {
  @BuiltValueField(wireName: r'birthDate')
  Date get birthDate;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'isOwner')
  bool get isOwner;

  @BuiltValueField(wireName: r'licenceCategories')
  BuiltList<String> get licenceCategories;

  @BuiltValueField(wireName: r'licenceIssueDate')
  Date get licenceIssueDate;

  /// Quatro * fixos e os últimos 4 caracteres
  @BuiltValueField(wireName: r'licenceNumberMasked')
  String get licenceNumberMasked;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  /// Veículos activos a que está associado
  @BuiltValueField(wireName: r'vehicleIds')
  BuiltList<String> get vehicleIds;

  @BuiltValueField(wireName: r'version')
  int get version;

  DriverResponse._();

  factory DriverResponse([void updates(DriverResponseBuilder b)]) = _$DriverResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(DriverResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<DriverResponse> get serializer => _$DriverResponseSerializer();
}

class _$DriverResponseSerializer implements PrimitiveSerializer<DriverResponse> {
  @override
  final Iterable<Type> types = const [DriverResponse, _$DriverResponse];

  @override
  final String wireName = r'DriverResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    DriverResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'birthDate';
    yield serializers.serialize(
      object.birthDate,
      specifiedType: const FullType(Date),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
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
    yield r'isOwner';
    yield serializers.serialize(
      object.isOwner,
      specifiedType: const FullType(bool),
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
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'vehicleIds';
    yield serializers.serialize(
      object.vehicleIds,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    DriverResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required DriverResponseBuilder result,
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
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
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
        case r'licenceNumberMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.licenceNumberMasked = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'vehicleIds':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.vehicleIds.replace(valueDes);
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
  DriverResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = DriverResponseBuilder();
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

