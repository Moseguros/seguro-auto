//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_plate_format_request.g.dart';

/// CreatePlateFormatRequest
///
/// Properties:
/// * [code] - ^[A-Z0-9_]{2,20}$; não muda depois de criado
/// * [displayTemplate] - Grupos do padrão como {nome}, letras, dígitos, espaço e -
/// * [example] - Valor já formatado que o formato aceita
/// * [label] - 2–80 caracteres
/// * [pattern] - Expressão regular Java (≤ 200), com ^ e $ e pelo menos um grupo nomeado; aplica-se ao valor normalizado (maiúsculas, sem espaços nem hífens)
/// * [sortOrder] - 0–999: ordem de teste
@BuiltValue()
abstract class CreatePlateFormatRequest implements Built<CreatePlateFormatRequest, CreatePlateFormatRequestBuilder> {
  /// ^[A-Z0-9_]{2,20}$; não muda depois de criado
  @BuiltValueField(wireName: r'code')
  String get code;

  /// Grupos do padrão como {nome}, letras, dígitos, espaço e -
  @BuiltValueField(wireName: r'displayTemplate')
  String get displayTemplate;

  /// Valor já formatado que o formato aceita
  @BuiltValueField(wireName: r'example')
  String get example;

  /// 2–80 caracteres
  @BuiltValueField(wireName: r'label')
  String get label;

  /// Expressão regular Java (≤ 200), com ^ e $ e pelo menos um grupo nomeado; aplica-se ao valor normalizado (maiúsculas, sem espaços nem hífens)
  @BuiltValueField(wireName: r'pattern')
  String get pattern;

  /// 0–999: ordem de teste
  @BuiltValueField(wireName: r'sortOrder')
  int get sortOrder;

  CreatePlateFormatRequest._();

  factory CreatePlateFormatRequest([void updates(CreatePlateFormatRequestBuilder b)]) = _$CreatePlateFormatRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreatePlateFormatRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreatePlateFormatRequest> get serializer => _$CreatePlateFormatRequestSerializer();
}

class _$CreatePlateFormatRequestSerializer implements PrimitiveSerializer<CreatePlateFormatRequest> {
  @override
  final Iterable<Type> types = const [CreatePlateFormatRequest, _$CreatePlateFormatRequest];

  @override
  final String wireName = r'CreatePlateFormatRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreatePlateFormatRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'displayTemplate';
    yield serializers.serialize(
      object.displayTemplate,
      specifiedType: const FullType(String),
    );
    yield r'example';
    yield serializers.serialize(
      object.example,
      specifiedType: const FullType(String),
    );
    yield r'label';
    yield serializers.serialize(
      object.label,
      specifiedType: const FullType(String),
    );
    yield r'pattern';
    yield serializers.serialize(
      object.pattern,
      specifiedType: const FullType(String),
    );
    yield r'sortOrder';
    yield serializers.serialize(
      object.sortOrder,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreatePlateFormatRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreatePlateFormatRequestBuilder result,
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
        case r'displayTemplate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayTemplate = valueDes;
          break;
        case r'example':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.example = valueDes;
          break;
        case r'label':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.label = valueDes;
          break;
        case r'pattern':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.pattern = valueDes;
          break;
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreatePlateFormatRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreatePlateFormatRequestBuilder();
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

