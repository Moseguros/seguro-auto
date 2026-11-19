//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/admin_payment_file_source.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'admin_payment_file_sources.g.dart';

/// AdminPaymentFileSources
///
/// Properties:
/// * [sources] 
@BuiltValue()
abstract class AdminPaymentFileSources implements Built<AdminPaymentFileSources, AdminPaymentFileSourcesBuilder> {
  @BuiltValueField(wireName: r'sources')
  BuiltList<AdminPaymentFileSource> get sources;

  AdminPaymentFileSources._();

  factory AdminPaymentFileSources([void updates(AdminPaymentFileSourcesBuilder b)]) = _$AdminPaymentFileSources;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AdminPaymentFileSourcesBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AdminPaymentFileSources> get serializer => _$AdminPaymentFileSourcesSerializer();
}

class _$AdminPaymentFileSourcesSerializer implements PrimitiveSerializer<AdminPaymentFileSources> {
  @override
  final Iterable<Type> types = const [AdminPaymentFileSources, _$AdminPaymentFileSources];

  @override
  final String wireName = r'AdminPaymentFileSources';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AdminPaymentFileSources object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'sources';
    yield serializers.serialize(
      object.sources,
      specifiedType: const FullType(BuiltList, [FullType(AdminPaymentFileSource)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    AdminPaymentFileSources object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AdminPaymentFileSourcesBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'sources':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(AdminPaymentFileSource)]),
          ) as BuiltList<AdminPaymentFileSource>;
          result.sources.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AdminPaymentFileSources deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AdminPaymentFileSourcesBuilder();
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

