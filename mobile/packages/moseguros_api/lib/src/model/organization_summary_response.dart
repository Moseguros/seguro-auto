//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'organization_summary_response.g.dart';

/// OrganizationSummaryResponse
///
/// Properties:
/// * [createdAt] 
/// * [displayName] 
/// * [id] 
/// * [nuit] - NUIT: 9 dígitos
/// * [status] 
/// * [type] 
@BuiltValue()
abstract class OrganizationSummaryResponse implements Built<OrganizationSummaryResponse, OrganizationSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'displayName')
  String get displayName;

  @BuiltValueField(wireName: r'id')
  String get id;

  /// NUIT: 9 dígitos
  @BuiltValueField(wireName: r'nuit')
  String get nuit;

  @BuiltValueField(wireName: r'status')
  OrganizationSummaryResponseStatusEnum get status;
  // enum statusEnum {  ACTIVE,  SUSPENDED,  };

  @BuiltValueField(wireName: r'type')
  OrganizationSummaryResponseTypeEnum get type;
  // enum typeEnum {  INSURER,  BROKER,  AGENT,  WORKSHOP,  ASSESSOR,  ASSISTANCE,  OMBUDSMAN,  REGULATOR,  POLICE,  CORPORATE_CLIENT,  };

  OrganizationSummaryResponse._();

  factory OrganizationSummaryResponse([void updates(OrganizationSummaryResponseBuilder b)]) = _$OrganizationSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OrganizationSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OrganizationSummaryResponse> get serializer => _$OrganizationSummaryResponseSerializer();
}

class _$OrganizationSummaryResponseSerializer implements PrimitiveSerializer<OrganizationSummaryResponse> {
  @override
  final Iterable<Type> types = const [OrganizationSummaryResponse, _$OrganizationSummaryResponse];

  @override
  final String wireName = r'OrganizationSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OrganizationSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
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
    yield r'nuit';
    yield serializers.serialize(
      object.nuit,
      specifiedType: const FullType(String),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(OrganizationSummaryResponseStatusEnum),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(OrganizationSummaryResponseTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OrganizationSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OrganizationSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
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
        case r'nuit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.nuit = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OrganizationSummaryResponseStatusEnum),
          ) as OrganizationSummaryResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OrganizationSummaryResponseTypeEnum),
          ) as OrganizationSummaryResponseTypeEnum;
          result.type = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OrganizationSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OrganizationSummaryResponseBuilder();
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

class OrganizationSummaryResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ACTIVE')
  static const OrganizationSummaryResponseStatusEnum ACTIVE = _$organizationSummaryResponseStatusEnum_ACTIVE;
  @BuiltValueEnumConst(wireName: r'SUSPENDED')
  static const OrganizationSummaryResponseStatusEnum SUSPENDED = _$organizationSummaryResponseStatusEnum_SUSPENDED;

  static Serializer<OrganizationSummaryResponseStatusEnum> get serializer => _$organizationSummaryResponseStatusEnumSerializer;

  const OrganizationSummaryResponseStatusEnum._(String name): super(name);

  static BuiltSet<OrganizationSummaryResponseStatusEnum> get values => _$organizationSummaryResponseStatusEnumValues;
  static OrganizationSummaryResponseStatusEnum valueOf(String name) => _$organizationSummaryResponseStatusEnumValueOf(name);
}

class OrganizationSummaryResponseTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INSURER')
  static const OrganizationSummaryResponseTypeEnum INSURER = _$organizationSummaryResponseTypeEnum_INSURER;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const OrganizationSummaryResponseTypeEnum BROKER = _$organizationSummaryResponseTypeEnum_BROKER;
  @BuiltValueEnumConst(wireName: r'AGENT')
  static const OrganizationSummaryResponseTypeEnum AGENT = _$organizationSummaryResponseTypeEnum_AGENT;
  @BuiltValueEnumConst(wireName: r'WORKSHOP')
  static const OrganizationSummaryResponseTypeEnum WORKSHOP = _$organizationSummaryResponseTypeEnum_WORKSHOP;
  @BuiltValueEnumConst(wireName: r'ASSESSOR')
  static const OrganizationSummaryResponseTypeEnum ASSESSOR = _$organizationSummaryResponseTypeEnum_ASSESSOR;
  @BuiltValueEnumConst(wireName: r'ASSISTANCE')
  static const OrganizationSummaryResponseTypeEnum ASSISTANCE = _$organizationSummaryResponseTypeEnum_ASSISTANCE;
  @BuiltValueEnumConst(wireName: r'OMBUDSMAN')
  static const OrganizationSummaryResponseTypeEnum OMBUDSMAN = _$organizationSummaryResponseTypeEnum_OMBUDSMAN;
  @BuiltValueEnumConst(wireName: r'REGULATOR')
  static const OrganizationSummaryResponseTypeEnum REGULATOR = _$organizationSummaryResponseTypeEnum_REGULATOR;
  @BuiltValueEnumConst(wireName: r'POLICE')
  static const OrganizationSummaryResponseTypeEnum POLICE = _$organizationSummaryResponseTypeEnum_POLICE;
  @BuiltValueEnumConst(wireName: r'CORPORATE_CLIENT')
  static const OrganizationSummaryResponseTypeEnum CORPORATE_CLIENT = _$organizationSummaryResponseTypeEnum_CORPORATE_CLIENT;

  static Serializer<OrganizationSummaryResponseTypeEnum> get serializer => _$organizationSummaryResponseTypeEnumSerializer;

  const OrganizationSummaryResponseTypeEnum._(String name): super(name);

  static BuiltSet<OrganizationSummaryResponseTypeEnum> get values => _$organizationSummaryResponseTypeEnumValues;
  static OrganizationSummaryResponseTypeEnum valueOf(String name) => _$organizationSummaryResponseTypeEnumValueOf(name);
}

