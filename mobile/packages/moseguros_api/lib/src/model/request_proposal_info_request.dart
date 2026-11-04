//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'request_proposal_info_request.g.dart';

/// RequestProposalInfoRequest
///
/// Properties:
/// * [items] - 1 ou mais, sem repetidos: DECLARATIONS, SUITABILITY, INSPECTION, KYC, POLICYHOLDER, OTHER
/// * [message] 
/// * [version] 
@BuiltValue()
abstract class RequestProposalInfoRequest implements Built<RequestProposalInfoRequest, RequestProposalInfoRequestBuilder> {
  /// 1 ou mais, sem repetidos: DECLARATIONS, SUITABILITY, INSPECTION, KYC, POLICYHOLDER, OTHER
  @BuiltValueField(wireName: r'items')
  BuiltList<String> get items;

  @BuiltValueField(wireName: r'message')
  String get message;

  @BuiltValueField(wireName: r'version')
  int get version;

  RequestProposalInfoRequest._();

  factory RequestProposalInfoRequest([void updates(RequestProposalInfoRequestBuilder b)]) = _$RequestProposalInfoRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RequestProposalInfoRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RequestProposalInfoRequest> get serializer => _$RequestProposalInfoRequestSerializer();
}

class _$RequestProposalInfoRequestSerializer implements PrimitiveSerializer<RequestProposalInfoRequest> {
  @override
  final Iterable<Type> types = const [RequestProposalInfoRequest, _$RequestProposalInfoRequest];

  @override
  final String wireName = r'RequestProposalInfoRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RequestProposalInfoRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'items';
    yield serializers.serialize(
      object.items,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'message';
    yield serializers.serialize(
      object.message,
      specifiedType: const FullType(String),
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
    RequestProposalInfoRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RequestProposalInfoRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'items':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.items.replace(valueDes);
          break;
        case r'message':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.message = valueDes;
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
  RequestProposalInfoRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RequestProposalInfoRequestBuilder();
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

