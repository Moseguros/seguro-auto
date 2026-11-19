//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'resolve_payment_exception_request.g.dart';

/// ResolvePaymentExceptionRequest
///
/// Properties:
/// * [intentId] - Só LINKED (obrigatório): intenção BANK_REFERENCE desta seguradora em PENDING, EXPIRED ou CANCELLED
/// * [note] 
/// * [providerTxId] - Só MARK_SUCCEEDED: id da transacção no portal do fornecedor
/// * [resolution] - REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma
/// * [version] 
@BuiltValue()
abstract class ResolvePaymentExceptionRequest implements Built<ResolvePaymentExceptionRequest, ResolvePaymentExceptionRequestBuilder> {
  /// Só LINKED (obrigatório): intenção BANK_REFERENCE desta seguradora em PENDING, EXPIRED ou CANCELLED
  @BuiltValueField(wireName: r'intentId')
  String? get intentId;

  @BuiltValueField(wireName: r'note')
  String get note;

  /// Só MARK_SUCCEEDED: id da transacção no portal do fornecedor
  @BuiltValueField(wireName: r'providerTxId')
  String? get providerTxId;

  /// REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma
  @BuiltValueField(wireName: r'resolution')
  ResolvePaymentExceptionRequestResolutionEnum get resolution;
  // enum resolutionEnum {  LINKED,  REFUND,  IGNORED,  MARK_SUCCEEDED,  MARK_FAILED,  };

  @BuiltValueField(wireName: r'version')
  int get version;

  ResolvePaymentExceptionRequest._();

  factory ResolvePaymentExceptionRequest([void updates(ResolvePaymentExceptionRequestBuilder b)]) = _$ResolvePaymentExceptionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ResolvePaymentExceptionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ResolvePaymentExceptionRequest> get serializer => _$ResolvePaymentExceptionRequestSerializer();
}

class _$ResolvePaymentExceptionRequestSerializer implements PrimitiveSerializer<ResolvePaymentExceptionRequest> {
  @override
  final Iterable<Type> types = const [ResolvePaymentExceptionRequest, _$ResolvePaymentExceptionRequest];

  @override
  final String wireName = r'ResolvePaymentExceptionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ResolvePaymentExceptionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.intentId != null) {
      yield r'intentId';
      yield serializers.serialize(
        object.intentId,
        specifiedType: const FullType(String),
      );
    }
    yield r'note';
    yield serializers.serialize(
      object.note,
      specifiedType: const FullType(String),
    );
    if (object.providerTxId != null) {
      yield r'providerTxId';
      yield serializers.serialize(
        object.providerTxId,
        specifiedType: const FullType(String),
      );
    }
    yield r'resolution';
    yield serializers.serialize(
      object.resolution,
      specifiedType: const FullType(ResolvePaymentExceptionRequestResolutionEnum),
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
    ResolvePaymentExceptionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ResolvePaymentExceptionRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'intentId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.intentId = valueDes;
          break;
        case r'note':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.note = valueDes;
          break;
        case r'providerTxId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.providerTxId = valueDes;
          break;
        case r'resolution':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ResolvePaymentExceptionRequestResolutionEnum),
          ) as ResolvePaymentExceptionRequestResolutionEnum;
          result.resolution = valueDes;
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
  ResolvePaymentExceptionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ResolvePaymentExceptionRequestBuilder();
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

class ResolvePaymentExceptionRequestResolutionEnum extends EnumClass {

  /// REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma
  @BuiltValueEnumConst(wireName: r'LINKED')
  static const ResolvePaymentExceptionRequestResolutionEnum LINKED = _$resolvePaymentExceptionRequestResolutionEnum_LINKED;
  /// REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma
  @BuiltValueEnumConst(wireName: r'REFUND')
  static const ResolvePaymentExceptionRequestResolutionEnum REFUND = _$resolvePaymentExceptionRequestResolutionEnum_REFUND;
  /// REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma
  @BuiltValueEnumConst(wireName: r'IGNORED')
  static const ResolvePaymentExceptionRequestResolutionEnum IGNORED = _$resolvePaymentExceptionRequestResolutionEnum_IGNORED;
  /// REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma
  @BuiltValueEnumConst(wireName: r'MARK_SUCCEEDED')
  static const ResolvePaymentExceptionRequestResolutionEnum MARK_SUCCEEDED = _$resolvePaymentExceptionRequestResolutionEnum_MARK_SUCCEEDED;
  /// REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma
  @BuiltValueEnumConst(wireName: r'MARK_FAILED')
  static const ResolvePaymentExceptionRequestResolutionEnum MARK_FAILED = _$resolvePaymentExceptionRequestResolutionEnum_MARK_FAILED;

  static Serializer<ResolvePaymentExceptionRequestResolutionEnum> get serializer => _$resolvePaymentExceptionRequestResolutionEnumSerializer;

  const ResolvePaymentExceptionRequestResolutionEnum._(String name): super(name);

  static BuiltSet<ResolvePaymentExceptionRequestResolutionEnum> get values => _$resolvePaymentExceptionRequestResolutionEnumValues;
  static ResolvePaymentExceptionRequestResolutionEnum valueOf(String name) => _$resolvePaymentExceptionRequestResolutionEnumValueOf(name);
}

