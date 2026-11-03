//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/field_error.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'problem_detail.g.dart';

/// Erro RFC 7807 (application/problem+json)
///
/// Properties:
/// * [code] - Código do erro (ErrorCodes)
/// * [detail] 
/// * [errors] - Erros por campo, quando o pedido é inválido
/// * [instance] 
/// * [status] 
/// * [title] 
/// * [type] 
@BuiltValue()
abstract class ProblemDetail implements Built<ProblemDetail, ProblemDetailBuilder> {
  /// Código do erro (ErrorCodes)
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'detail')
  String? get detail;

  /// Erros por campo, quando o pedido é inválido
  @BuiltValueField(wireName: r'errors')
  BuiltList<FieldError>? get errors;

  @BuiltValueField(wireName: r'instance')
  String? get instance;

  @BuiltValueField(wireName: r'status')
  int get status;

  @BuiltValueField(wireName: r'title')
  String get title;

  @BuiltValueField(wireName: r'type')
  String get type;

  ProblemDetail._();

  factory ProblemDetail([void updates(ProblemDetailBuilder b)]) = _$ProblemDetail;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProblemDetailBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProblemDetail> get serializer => _$ProblemDetailSerializer();
}

class _$ProblemDetailSerializer implements PrimitiveSerializer<ProblemDetail> {
  @override
  final Iterable<Type> types = const [ProblemDetail, _$ProblemDetail];

  @override
  final String wireName = r'ProblemDetail';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProblemDetail object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    if (object.detail != null) {
      yield r'detail';
      yield serializers.serialize(
        object.detail,
        specifiedType: const FullType(String),
      );
    }
    if (object.errors != null) {
      yield r'errors';
      yield serializers.serialize(
        object.errors,
        specifiedType: const FullType(BuiltList, [FullType(FieldError)]),
      );
    }
    if (object.instance != null) {
      yield r'instance';
      yield serializers.serialize(
        object.instance,
        specifiedType: const FullType(String),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(int),
    );
    yield r'title';
    yield serializers.serialize(
      object.title,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProblemDetail object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProblemDetailBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'detail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.detail = valueDes;
          break;
        case r'errors':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(FieldError)]),
          ) as BuiltList<FieldError>;
          result.errors.replace(valueDes);
          break;
        case r'instance':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.instance = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.status = valueDes;
          break;
        case r'title':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.title = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.type = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProblemDetail deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProblemDetailBuilder();
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

