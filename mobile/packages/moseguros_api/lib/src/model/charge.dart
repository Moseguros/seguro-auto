//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/charge_value.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'charge.g.dart';

/// Encargo (imposto ou taxa) que a seguradora acrescenta ao prémio comercial
///
/// Properties:
/// * [code] 
/// * [labelEn] 
/// * [labelPt] 
/// * [type] 
/// * [value] 
@BuiltValue()
abstract class Charge implements Built<Charge, ChargeBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'labelEn')
  String get labelEn;

  @BuiltValueField(wireName: r'labelPt')
  String get labelPt;

  @BuiltValueField(wireName: r'type')
  ChargeTypeEnum get type;
  // enum typeEnum {  PERCENT,  FIXED,  };

  @BuiltValueField(wireName: r'value')
  ChargeValue get value;

  Charge._();

  factory Charge([void updates(ChargeBuilder b)]) = _$Charge;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ChargeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<Charge> get serializer => _$ChargeSerializer();
}

class _$ChargeSerializer implements PrimitiveSerializer<Charge> {
  @override
  final Iterable<Type> types = const [Charge, _$Charge];

  @override
  final String wireName = r'Charge';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    Charge object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'labelEn';
    yield serializers.serialize(
      object.labelEn,
      specifiedType: const FullType(String),
    );
    yield r'labelPt';
    yield serializers.serialize(
      object.labelPt,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ChargeTypeEnum),
    );
    yield r'value';
    yield serializers.serialize(
      object.value,
      specifiedType: const FullType(ChargeValue),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    Charge object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ChargeBuilder result,
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
        case r'labelEn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelEn = valueDes;
          break;
        case r'labelPt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.labelPt = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ChargeTypeEnum),
          ) as ChargeTypeEnum;
          result.type = valueDes;
          break;
        case r'value':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ChargeValue),
          ) as ChargeValue;
          result.value.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  Charge deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ChargeBuilder();
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

class ChargeTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PERCENT')
  static const ChargeTypeEnum PERCENT = _$chargeTypeEnum_PERCENT;
  @BuiltValueEnumConst(wireName: r'FIXED')
  static const ChargeTypeEnum FIXED = _$chargeTypeEnum_FIXED;

  static Serializer<ChargeTypeEnum> get serializer => _$chargeTypeEnumSerializer;

  const ChargeTypeEnum._(String name): super(name);

  static BuiltSet<ChargeTypeEnum> get values => _$chargeTypeEnumValues;
  static ChargeTypeEnum valueOf(String name) => _$chargeTypeEnumValueOf(name);
}

