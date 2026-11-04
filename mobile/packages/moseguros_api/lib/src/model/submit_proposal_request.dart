//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'submit_proposal_request.g.dart';

/// SubmitProposalRequest
///
/// Properties:
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class SubmitProposalRequest implements Built<SubmitProposalRequest, SubmitProposalRequestBuilder> {
  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  SubmitProposalRequest._();

  factory SubmitProposalRequest([void updates(SubmitProposalRequestBuilder b)]) = _$SubmitProposalRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SubmitProposalRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SubmitProposalRequest> get serializer => _$SubmitProposalRequestSerializer();
}

class _$SubmitProposalRequestSerializer implements PrimitiveSerializer<SubmitProposalRequest> {
  @override
  final Iterable<Type> types = const [SubmitProposalRequest, _$SubmitProposalRequest];

  @override
  final String wireName = r'SubmitProposalRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SubmitProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SubmitProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SubmitProposalRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
  SubmitProposalRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SubmitProposalRequestBuilder();
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

