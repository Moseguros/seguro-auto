//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_file_import.g.dart';

/// PaymentFileImport
///
/// Properties:
/// * [confirmedCount] - Pagamentos confirmados
/// * [detail] 
/// * [exceptionCount] - Pagamentos que geraram excepções
/// * [fileFormat] 
/// * [fileName] 
/// * [formatFileId] - Identificador do ficheiro no formato (MEPS AAAAMMDDS; BMEPS empresa-perfil-ID)
/// * [id] 
/// * [lineNo] 
/// * [occurredAt] 
/// * [reason] - Só REJECTED: FileRejectionReason (i18n fileRejectionReason.*)
/// * [recordCount] - Só PROCESSED
/// * [sha256] 
/// * [status] 
/// * [totalAmount] 
/// * [totalFees] 
@BuiltValue()
abstract class PaymentFileImport implements Built<PaymentFileImport, PaymentFileImportBuilder> {
  /// Pagamentos confirmados
  @BuiltValueField(wireName: r'confirmedCount')
  int get confirmedCount;

  @BuiltValueField(wireName: r'detail')
  String? get detail;

  /// Pagamentos que geraram excepções
  @BuiltValueField(wireName: r'exceptionCount')
  int get exceptionCount;

  @BuiltValueField(wireName: r'fileFormat')
  PaymentFileImportFileFormatEnum get fileFormat;
  // enum fileFormatEnum {  MEPS,  BMEPS,  };

  @BuiltValueField(wireName: r'fileName')
  String get fileName;

  /// Identificador do ficheiro no formato (MEPS AAAAMMDDS; BMEPS empresa-perfil-ID)
  @BuiltValueField(wireName: r'formatFileId')
  String? get formatFileId;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'lineNo')
  int? get lineNo;

  @BuiltValueField(wireName: r'occurredAt')
  DateTime get occurredAt;

  /// Só REJECTED: FileRejectionReason (i18n fileRejectionReason.*)
  @BuiltValueField(wireName: r'reason')
  String? get reason;

  /// Só PROCESSED
  @BuiltValueField(wireName: r'recordCount')
  int? get recordCount;

  @BuiltValueField(wireName: r'sha256')
  String get sha256;

  @BuiltValueField(wireName: r'status')
  PaymentFileImportStatusEnum get status;
  // enum statusEnum {  PROCESSED,  REJECTED,  };

  @BuiltValueField(wireName: r'totalAmount')
  Money? get totalAmount;

  @BuiltValueField(wireName: r'totalFees')
  Money? get totalFees;

  PaymentFileImport._();

  factory PaymentFileImport([void updates(PaymentFileImportBuilder b)]) = _$PaymentFileImport;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentFileImportBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentFileImport> get serializer => _$PaymentFileImportSerializer();
}

class _$PaymentFileImportSerializer implements PrimitiveSerializer<PaymentFileImport> {
  @override
  final Iterable<Type> types = const [PaymentFileImport, _$PaymentFileImport];

  @override
  final String wireName = r'PaymentFileImport';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentFileImport object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'confirmedCount';
    yield serializers.serialize(
      object.confirmedCount,
      specifiedType: const FullType(int),
    );
    if (object.detail != null) {
      yield r'detail';
      yield serializers.serialize(
        object.detail,
        specifiedType: const FullType(String),
      );
    }
    yield r'exceptionCount';
    yield serializers.serialize(
      object.exceptionCount,
      specifiedType: const FullType(int),
    );
    yield r'fileFormat';
    yield serializers.serialize(
      object.fileFormat,
      specifiedType: const FullType(PaymentFileImportFileFormatEnum),
    );
    yield r'fileName';
    yield serializers.serialize(
      object.fileName,
      specifiedType: const FullType(String),
    );
    if (object.formatFileId != null) {
      yield r'formatFileId';
      yield serializers.serialize(
        object.formatFileId,
        specifiedType: const FullType(String),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.lineNo != null) {
      yield r'lineNo';
      yield serializers.serialize(
        object.lineNo,
        specifiedType: const FullType(int),
      );
    }
    yield r'occurredAt';
    yield serializers.serialize(
      object.occurredAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.reason != null) {
      yield r'reason';
      yield serializers.serialize(
        object.reason,
        specifiedType: const FullType(String),
      );
    }
    if (object.recordCount != null) {
      yield r'recordCount';
      yield serializers.serialize(
        object.recordCount,
        specifiedType: const FullType(int),
      );
    }
    yield r'sha256';
    yield serializers.serialize(
      object.sha256,
      specifiedType: const FullType(String),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PaymentFileImportStatusEnum),
    );
    if (object.totalAmount != null) {
      yield r'totalAmount';
      yield serializers.serialize(
        object.totalAmount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.totalFees != null) {
      yield r'totalFees';
      yield serializers.serialize(
        object.totalFees,
        specifiedType: const FullType(Money),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentFileImport object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentFileImportBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'confirmedCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.confirmedCount = valueDes;
          break;
        case r'detail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.detail = valueDes;
          break;
        case r'exceptionCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.exceptionCount = valueDes;
          break;
        case r'fileFormat':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentFileImportFileFormatEnum),
          ) as PaymentFileImportFileFormatEnum;
          result.fileFormat = valueDes;
          break;
        case r'fileName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileName = valueDes;
          break;
        case r'formatFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.formatFileId = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'lineNo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.lineNo = valueDes;
          break;
        case r'occurredAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.occurredAt = valueDes;
          break;
        case r'reason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.reason = valueDes;
          break;
        case r'recordCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.recordCount = valueDes;
          break;
        case r'sha256':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.sha256 = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentFileImportStatusEnum),
          ) as PaymentFileImportStatusEnum;
          result.status = valueDes;
          break;
        case r'totalAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.totalAmount.replace(valueDes);
          break;
        case r'totalFees':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.totalFees.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentFileImport deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentFileImportBuilder();
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

class PaymentFileImportFileFormatEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MEPS')
  static const PaymentFileImportFileFormatEnum MEPS = _$paymentFileImportFileFormatEnum_MEPS;
  @BuiltValueEnumConst(wireName: r'BMEPS')
  static const PaymentFileImportFileFormatEnum BMEPS = _$paymentFileImportFileFormatEnum_BMEPS;

  static Serializer<PaymentFileImportFileFormatEnum> get serializer => _$paymentFileImportFileFormatEnumSerializer;

  const PaymentFileImportFileFormatEnum._(String name): super(name);

  static BuiltSet<PaymentFileImportFileFormatEnum> get values => _$paymentFileImportFileFormatEnumValues;
  static PaymentFileImportFileFormatEnum valueOf(String name) => _$paymentFileImportFileFormatEnumValueOf(name);
}

class PaymentFileImportStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PROCESSED')
  static const PaymentFileImportStatusEnum PROCESSED = _$paymentFileImportStatusEnum_PROCESSED;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const PaymentFileImportStatusEnum REJECTED = _$paymentFileImportStatusEnum_REJECTED;

  static Serializer<PaymentFileImportStatusEnum> get serializer => _$paymentFileImportStatusEnumSerializer;

  const PaymentFileImportStatusEnum._(String name): super(name);

  static BuiltSet<PaymentFileImportStatusEnum> get values => _$paymentFileImportStatusEnumValues;
  static PaymentFileImportStatusEnum valueOf(String name) => _$paymentFileImportStatusEnumValueOf(name);
}

