//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/contact_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'contacts_response.g.dart';

/// ContactsResponse
///
/// Properties:
/// * [contacts] 
@BuiltValue()
abstract class ContactsResponse implements Built<ContactsResponse, ContactsResponseBuilder> {
  @BuiltValueField(wireName: r'contacts')
  BuiltList<ContactResponse> get contacts;

  ContactsResponse._();

  factory ContactsResponse([void updates(ContactsResponseBuilder b)]) = _$ContactsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ContactsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ContactsResponse> get serializer => _$ContactsResponseSerializer();
}

class _$ContactsResponseSerializer implements PrimitiveSerializer<ContactsResponse> {
  @override
  final Iterable<Type> types = const [ContactsResponse, _$ContactsResponse];

  @override
  final String wireName = r'ContactsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ContactsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'contacts';
    yield serializers.serialize(
      object.contacts,
      specifiedType: const FullType(BuiltList, [FullType(ContactResponse)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ContactsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ContactsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'contacts':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(ContactResponse)]),
          ) as BuiltList<ContactResponse>;
          result.contacts.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ContactsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ContactsResponseBuilder();
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

