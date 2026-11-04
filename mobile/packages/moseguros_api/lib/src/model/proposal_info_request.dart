//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/info_request_item.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_info_request.g.dart';

/// Pedido de informação em curso (INFO_REQUESTED)
///
/// Properties:
/// * [items] 
/// * [message] 
/// * [requestedAt] 
@BuiltValue()
abstract class ProposalInfoRequest implements Built<ProposalInfoRequest, ProposalInfoRequestBuilder> {
  @BuiltValueField(wireName: r'items')
  BuiltList<InfoRequestItem> get items;

  @BuiltValueField(wireName: r'message')
  String get message;

  @BuiltValueField(wireName: r'requestedAt')
  DateTime get requestedAt;

  ProposalInfoRequest._();

  factory ProposalInfoRequest([void updates(ProposalInfoRequestBuilder b)]) = _$ProposalInfoRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalInfoRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalInfoRequest> get serializer => _$ProposalInfoRequestSerializer();
}

class _$ProposalInfoRequestSerializer implements PrimitiveSerializer<ProposalInfoRequest> {
  @override
  final Iterable<Type> types = const [ProposalInfoRequest, _$ProposalInfoRequest];

  @override
  final String wireName = r'ProposalInfoRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalInfoRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'items';
    yield serializers.serialize(
      object.items,
      specifiedType: const FullType(BuiltList, [FullType(InfoRequestItem)]),
    );
    yield r'message';
    yield serializers.serialize(
      object.message,
      specifiedType: const FullType(String),
    );
    yield r'requestedAt';
    yield serializers.serialize(
      object.requestedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalInfoRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalInfoRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'items':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(InfoRequestItem)]),
          ) as BuiltList<InfoRequestItem>;
          result.items.replace(valueDes);
          break;
        case r'message':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.message = valueDes;
          break;
        case r'requestedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.requestedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalInfoRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalInfoRequestBuilder();
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

