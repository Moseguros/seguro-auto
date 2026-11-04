//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_proposal_request.g.dart';

/// UpdateProposalRequest
///
/// Properties:
/// * [startDate] - Entre hoje e hoje + 60 dias (Africa/Maputo); fora → START_DATE_OUT_OF_RANGE
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateProposalRequest implements Built<UpdateProposalRequest, UpdateProposalRequestBuilder> {
  /// Entre hoje e hoje + 60 dias (Africa/Maputo); fora → START_DATE_OUT_OF_RANGE
  @BuiltValueField(wireName: r'startDate')
  Date get startDate;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateProposalRequest._();

  factory UpdateProposalRequest([void updates(UpdateProposalRequestBuilder b)]) = _$UpdateProposalRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateProposalRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateProposalRequest> get serializer => _$UpdateProposalRequestSerializer();
}

class _$UpdateProposalRequestSerializer implements PrimitiveSerializer<UpdateProposalRequest> {
  @override
  final Iterable<Type> types = const [UpdateProposalRequest, _$UpdateProposalRequest];

  @override
  final String wireName = r'UpdateProposalRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'startDate';
    yield serializers.serialize(
      object.startDate,
      specifiedType: const FullType(Date),
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
    UpdateProposalRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateProposalRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'startDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.startDate = valueDes;
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
  UpdateProposalRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateProposalRequestBuilder();
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

