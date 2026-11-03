//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/coverage_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'replace_coverages_request.g.dart';

/// ReplaceCoveragesRequest
///
/// Properties:
/// * [coverages] 
/// * [version] 
@BuiltValue()
abstract class ReplaceCoveragesRequest implements Built<ReplaceCoveragesRequest, ReplaceCoveragesRequestBuilder> {
  @BuiltValueField(wireName: r'coverages')
  BuiltList<CoverageRequest> get coverages;

  @BuiltValueField(wireName: r'version')
  int get version;

  ReplaceCoveragesRequest._();

  factory ReplaceCoveragesRequest([void updates(ReplaceCoveragesRequestBuilder b)]) = _$ReplaceCoveragesRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ReplaceCoveragesRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ReplaceCoveragesRequest> get serializer => _$ReplaceCoveragesRequestSerializer();
}

class _$ReplaceCoveragesRequestSerializer implements PrimitiveSerializer<ReplaceCoveragesRequest> {
  @override
  final Iterable<Type> types = const [ReplaceCoveragesRequest, _$ReplaceCoveragesRequest];

  @override
  final String wireName = r'ReplaceCoveragesRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ReplaceCoveragesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'coverages';
    yield serializers.serialize(
      object.coverages,
      specifiedType: const FullType(BuiltList, [FullType(CoverageRequest)]),
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
    ReplaceCoveragesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ReplaceCoveragesRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CoverageRequest)]),
          ) as BuiltList<CoverageRequest>;
          result.coverages.replace(valueDes);
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
  ReplaceCoveragesRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ReplaceCoveragesRequestBuilder();
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

