//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_file_source_insurer.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'admin_payment_file_source.g.dart';

/// AdminPaymentFileSource
///
/// Properties:
/// * [enabled] 
/// * [entity] 
/// * [fileFormat] 
/// * [filesLast24h] 
/// * [insurer] 
/// * [lastPollAt] 
/// * [lastPollError] - PollErrorCode (i18n pollErrorCode.*)
/// * [lastPollOutcome] 
/// * [lastSuccessAt] 
/// * [rejectedLast24h] 
/// * [sourceId] 
/// * [staleSince] 
@BuiltValue()
abstract class AdminPaymentFileSource implements Built<AdminPaymentFileSource, AdminPaymentFileSourceBuilder> {
  @BuiltValueField(wireName: r'enabled')
  bool get enabled;

  @BuiltValueField(wireName: r'entity')
  String? get entity;

  @BuiltValueField(wireName: r'fileFormat')
  AdminPaymentFileSourceFileFormatEnum? get fileFormat;
  // enum fileFormatEnum {  MEPS,  BMEPS,  };

  @BuiltValueField(wireName: r'filesLast24h')
  int get filesLast24h;

  @BuiltValueField(wireName: r'insurer')
  PaymentFileSourceInsurer get insurer;

  @BuiltValueField(wireName: r'lastPollAt')
  DateTime? get lastPollAt;

  /// PollErrorCode (i18n pollErrorCode.*)
  @BuiltValueField(wireName: r'lastPollError')
  String? get lastPollError;

  @BuiltValueField(wireName: r'lastPollOutcome')
  AdminPaymentFileSourceLastPollOutcomeEnum? get lastPollOutcome;
  // enum lastPollOutcomeEnum {  OK,  ERROR,  };

  @BuiltValueField(wireName: r'lastSuccessAt')
  DateTime? get lastSuccessAt;

  @BuiltValueField(wireName: r'rejectedLast24h')
  int get rejectedLast24h;

  @BuiltValueField(wireName: r'sourceId')
  String get sourceId;

  @BuiltValueField(wireName: r'staleSince')
  DateTime? get staleSince;

  AdminPaymentFileSource._();

  factory AdminPaymentFileSource([void updates(AdminPaymentFileSourceBuilder b)]) = _$AdminPaymentFileSource;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AdminPaymentFileSourceBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AdminPaymentFileSource> get serializer => _$AdminPaymentFileSourceSerializer();
}

class _$AdminPaymentFileSourceSerializer implements PrimitiveSerializer<AdminPaymentFileSource> {
  @override
  final Iterable<Type> types = const [AdminPaymentFileSource, _$AdminPaymentFileSource];

  @override
  final String wireName = r'AdminPaymentFileSource';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AdminPaymentFileSource object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'enabled';
    yield serializers.serialize(
      object.enabled,
      specifiedType: const FullType(bool),
    );
    if (object.entity != null) {
      yield r'entity';
      yield serializers.serialize(
        object.entity,
        specifiedType: const FullType(String),
      );
    }
    if (object.fileFormat != null) {
      yield r'fileFormat';
      yield serializers.serialize(
        object.fileFormat,
        specifiedType: const FullType(AdminPaymentFileSourceFileFormatEnum),
      );
    }
    yield r'filesLast24h';
    yield serializers.serialize(
      object.filesLast24h,
      specifiedType: const FullType(int),
    );
    yield r'insurer';
    yield serializers.serialize(
      object.insurer,
      specifiedType: const FullType(PaymentFileSourceInsurer),
    );
    if (object.lastPollAt != null) {
      yield r'lastPollAt';
      yield serializers.serialize(
        object.lastPollAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.lastPollError != null) {
      yield r'lastPollError';
      yield serializers.serialize(
        object.lastPollError,
        specifiedType: const FullType(String),
      );
    }
    if (object.lastPollOutcome != null) {
      yield r'lastPollOutcome';
      yield serializers.serialize(
        object.lastPollOutcome,
        specifiedType: const FullType(AdminPaymentFileSourceLastPollOutcomeEnum),
      );
    }
    if (object.lastSuccessAt != null) {
      yield r'lastSuccessAt';
      yield serializers.serialize(
        object.lastSuccessAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'rejectedLast24h';
    yield serializers.serialize(
      object.rejectedLast24h,
      specifiedType: const FullType(int),
    );
    yield r'sourceId';
    yield serializers.serialize(
      object.sourceId,
      specifiedType: const FullType(String),
    );
    if (object.staleSince != null) {
      yield r'staleSince';
      yield serializers.serialize(
        object.staleSince,
        specifiedType: const FullType(DateTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    AdminPaymentFileSource object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AdminPaymentFileSourceBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'enabled':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.enabled = valueDes;
          break;
        case r'entity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.entity = valueDes;
          break;
        case r'fileFormat':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(AdminPaymentFileSourceFileFormatEnum),
          ) as AdminPaymentFileSourceFileFormatEnum;
          result.fileFormat = valueDes;
          break;
        case r'filesLast24h':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.filesLast24h = valueDes;
          break;
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentFileSourceInsurer),
          ) as PaymentFileSourceInsurer;
          result.insurer.replace(valueDes);
          break;
        case r'lastPollAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.lastPollAt = valueDes;
          break;
        case r'lastPollError':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.lastPollError = valueDes;
          break;
        case r'lastPollOutcome':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(AdminPaymentFileSourceLastPollOutcomeEnum),
          ) as AdminPaymentFileSourceLastPollOutcomeEnum;
          result.lastPollOutcome = valueDes;
          break;
        case r'lastSuccessAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.lastSuccessAt = valueDes;
          break;
        case r'rejectedLast24h':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.rejectedLast24h = valueDes;
          break;
        case r'sourceId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.sourceId = valueDes;
          break;
        case r'staleSince':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.staleSince = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AdminPaymentFileSource deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AdminPaymentFileSourceBuilder();
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

class AdminPaymentFileSourceFileFormatEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MEPS')
  static const AdminPaymentFileSourceFileFormatEnum MEPS = _$adminPaymentFileSourceFileFormatEnum_MEPS;
  @BuiltValueEnumConst(wireName: r'BMEPS')
  static const AdminPaymentFileSourceFileFormatEnum BMEPS = _$adminPaymentFileSourceFileFormatEnum_BMEPS;

  static Serializer<AdminPaymentFileSourceFileFormatEnum> get serializer => _$adminPaymentFileSourceFileFormatEnumSerializer;

  const AdminPaymentFileSourceFileFormatEnum._(String name): super(name);

  static BuiltSet<AdminPaymentFileSourceFileFormatEnum> get values => _$adminPaymentFileSourceFileFormatEnumValues;
  static AdminPaymentFileSourceFileFormatEnum valueOf(String name) => _$adminPaymentFileSourceFileFormatEnumValueOf(name);
}

class AdminPaymentFileSourceLastPollOutcomeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OK')
  static const AdminPaymentFileSourceLastPollOutcomeEnum OK = _$adminPaymentFileSourceLastPollOutcomeEnum_OK;
  @BuiltValueEnumConst(wireName: r'ERROR')
  static const AdminPaymentFileSourceLastPollOutcomeEnum ERROR = _$adminPaymentFileSourceLastPollOutcomeEnum_ERROR;

  static Serializer<AdminPaymentFileSourceLastPollOutcomeEnum> get serializer => _$adminPaymentFileSourceLastPollOutcomeEnumSerializer;

  const AdminPaymentFileSourceLastPollOutcomeEnum._(String name): super(name);

  static BuiltSet<AdminPaymentFileSourceLastPollOutcomeEnum> get values => _$adminPaymentFileSourceLastPollOutcomeEnumValues;
  static AdminPaymentFileSourceLastPollOutcomeEnum valueOf(String name) => _$adminPaymentFileSourceLastPollOutcomeEnumValueOf(name);
}

