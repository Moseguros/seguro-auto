//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'accept_proposal_request.g.dart';

/// AcceptProposalRequest
///
/// Properties:
/// * [note] - Nota opcional (até 1000 caracteres)
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class AcceptProposalRequest implements Built<AcceptProposalRequest, AcceptProposalRequestBuilder> {
  /// Nota opcional (até 1000 caracteres)
  @BuiltValueField(wireName: r'note')
  String? get note;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  AcceptProposalRequest._();

  factory AcceptProposalRequest([void updates(AcceptProposalRequestBuilder b)]) = _$AcceptProposalRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AcceptProposalRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AcceptProposalRequest> get serializer => _$AcceptProposalRequestSerializer();
}

class _$AcceptProposalRequestSerializer implements PrimitiveSerializer<AcceptProposalRequest> {
  @override
  final Iterable<Type> types = const [AcceptProposalRequest, _$AcceptProposalRequest];

  @override
  final String wireName = r'AcceptProposalRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AcceptProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.note != null) {
      yield r'note';
      yield serializers.serialize(
        object.note,
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
    AcceptProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AcceptProposalRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'note':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.note = valueDes;
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
  AcceptProposalRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AcceptProposalRequestBuilder();
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

