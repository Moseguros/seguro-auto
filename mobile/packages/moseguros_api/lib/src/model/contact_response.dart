//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'contact_response.g.dart';

/// ContactResponse
///
/// Properties:
/// * [type] 
/// * [value] - Telemóvel em E.164 ou e-mail
/// * [verified] 
@BuiltValue()
abstract class ContactResponse implements Built<ContactResponse, ContactResponseBuilder> {
  @BuiltValueField(wireName: r'type')
  ContactResponseTypeEnum get type;
  // enum typeEnum {  MSISDN,  EMAIL,  };

  /// Telemóvel em E.164 ou e-mail
  @BuiltValueField(wireName: r'value')
  String get value;

  @BuiltValueField(wireName: r'verified')
  bool get verified;

  ContactResponse._();

  factory ContactResponse([void updates(ContactResponseBuilder b)]) = _$ContactResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ContactResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ContactResponse> get serializer => _$ContactResponseSerializer();
}

class _$ContactResponseSerializer implements PrimitiveSerializer<ContactResponse> {
  @override
  final Iterable<Type> types = const [ContactResponse, _$ContactResponse];

  @override
  final String wireName = r'ContactResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ContactResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ContactResponseTypeEnum),
    );
    yield r'value';
    yield serializers.serialize(
      object.value,
      specifiedType: const FullType(String),
    );
    yield r'verified';
    yield serializers.serialize(
      object.verified,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ContactResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ContactResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ContactResponseTypeEnum),
          ) as ContactResponseTypeEnum;
          result.type = valueDes;
          break;
        case r'value':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.value = valueDes;
          break;
        case r'verified':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.verified = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ContactResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ContactResponseBuilder();
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

class ContactResponseTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MSISDN')
  static const ContactResponseTypeEnum MSISDN = _$contactResponseTypeEnum_MSISDN;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const ContactResponseTypeEnum EMAIL = _$contactResponseTypeEnum_EMAIL;

  static Serializer<ContactResponseTypeEnum> get serializer => _$contactResponseTypeEnumSerializer;

  const ContactResponseTypeEnum._(String name): super(name);

  static BuiltSet<ContactResponseTypeEnum> get values => _$contactResponseTypeEnumValues;
  static ContactResponseTypeEnum valueOf(String name) => _$contactResponseTypeEnumValueOf(name);
}

