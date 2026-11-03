//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_profile_request.g.dart';

/// InsurerProfileRequest
///
/// Properties:
/// * [complaintsEmail] 
/// * [ombudsmanOrganizationId] - Provedor do cliente: organização existente do tipo OMBUDSMAN (400 OMBUDSMAN_INVALID)
/// * [shortCode] - Código curto único: 2 a 10 maiúsculas ou dígitos (409 SHORT_CODE_ALREADY_IN_USE)
@BuiltValue()
abstract class InsurerProfileRequest implements Built<InsurerProfileRequest, InsurerProfileRequestBuilder> {
  @BuiltValueField(wireName: r'complaintsEmail')
  String get complaintsEmail;

  /// Provedor do cliente: organização existente do tipo OMBUDSMAN (400 OMBUDSMAN_INVALID)
  @BuiltValueField(wireName: r'ombudsmanOrganizationId')
  String? get ombudsmanOrganizationId;

  /// Código curto único: 2 a 10 maiúsculas ou dígitos (409 SHORT_CODE_ALREADY_IN_USE)
  @BuiltValueField(wireName: r'shortCode')
  String get shortCode;

  InsurerProfileRequest._();

  factory InsurerProfileRequest([void updates(InsurerProfileRequestBuilder b)]) = _$InsurerProfileRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerProfileRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerProfileRequest> get serializer => _$InsurerProfileRequestSerializer();
}

class _$InsurerProfileRequestSerializer implements PrimitiveSerializer<InsurerProfileRequest> {
  @override
  final Iterable<Type> types = const [InsurerProfileRequest, _$InsurerProfileRequest];

  @override
  final String wireName = r'InsurerProfileRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerProfileRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'complaintsEmail';
    yield serializers.serialize(
      object.complaintsEmail,
      specifiedType: const FullType(String),
    );
    if (object.ombudsmanOrganizationId != null) {
      yield r'ombudsmanOrganizationId';
      yield serializers.serialize(
        object.ombudsmanOrganizationId,
        specifiedType: const FullType(String),
      );
    }
    yield r'shortCode';
    yield serializers.serialize(
      object.shortCode,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerProfileRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerProfileRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'complaintsEmail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.complaintsEmail = valueDes;
          break;
        case r'ombudsmanOrganizationId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.ombudsmanOrganizationId = valueDes;
          break;
        case r'shortCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.shortCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerProfileRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerProfileRequestBuilder();
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

