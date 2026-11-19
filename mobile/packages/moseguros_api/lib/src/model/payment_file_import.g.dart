// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_file_import.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentFileImportFileFormatEnum _$paymentFileImportFileFormatEnum_MEPS =
    const PaymentFileImportFileFormatEnum._('MEPS');
const PaymentFileImportFileFormatEnum _$paymentFileImportFileFormatEnum_BMEPS =
    const PaymentFileImportFileFormatEnum._('BMEPS');

PaymentFileImportFileFormatEnum _$paymentFileImportFileFormatEnumValueOf(
    String name) {
  switch (name) {
    case 'MEPS':
      return _$paymentFileImportFileFormatEnum_MEPS;
    case 'BMEPS':
      return _$paymentFileImportFileFormatEnum_BMEPS;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentFileImportFileFormatEnum>
    _$paymentFileImportFileFormatEnumValues = BuiltSet<
        PaymentFileImportFileFormatEnum>(const <PaymentFileImportFileFormatEnum>[
  _$paymentFileImportFileFormatEnum_MEPS,
  _$paymentFileImportFileFormatEnum_BMEPS,
]);

const PaymentFileImportStatusEnum _$paymentFileImportStatusEnum_PROCESSED =
    const PaymentFileImportStatusEnum._('PROCESSED');
const PaymentFileImportStatusEnum _$paymentFileImportStatusEnum_REJECTED =
    const PaymentFileImportStatusEnum._('REJECTED');

PaymentFileImportStatusEnum _$paymentFileImportStatusEnumValueOf(String name) {
  switch (name) {
    case 'PROCESSED':
      return _$paymentFileImportStatusEnum_PROCESSED;
    case 'REJECTED':
      return _$paymentFileImportStatusEnum_REJECTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentFileImportStatusEnum>
    _$paymentFileImportStatusEnumValues =
    BuiltSet<PaymentFileImportStatusEnum>(const <PaymentFileImportStatusEnum>[
  _$paymentFileImportStatusEnum_PROCESSED,
  _$paymentFileImportStatusEnum_REJECTED,
]);

Serializer<PaymentFileImportFileFormatEnum>
    _$paymentFileImportFileFormatEnumSerializer =
    _$PaymentFileImportFileFormatEnumSerializer();
Serializer<PaymentFileImportStatusEnum>
    _$paymentFileImportStatusEnumSerializer =
    _$PaymentFileImportStatusEnumSerializer();

class _$PaymentFileImportFileFormatEnumSerializer
    implements PrimitiveSerializer<PaymentFileImportFileFormatEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MEPS': 'MEPS',
    'BMEPS': 'BMEPS',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MEPS': 'MEPS',
    'BMEPS': 'BMEPS',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentFileImportFileFormatEnum];
  @override
  final String wireName = 'PaymentFileImportFileFormatEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentFileImportFileFormatEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentFileImportFileFormatEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentFileImportFileFormatEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentFileImportStatusEnumSerializer
    implements PrimitiveSerializer<PaymentFileImportStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PROCESSED': 'PROCESSED',
    'REJECTED': 'REJECTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PROCESSED': 'PROCESSED',
    'REJECTED': 'REJECTED',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentFileImportStatusEnum];
  @override
  final String wireName = 'PaymentFileImportStatusEnum';

  @override
  Object serialize(Serializers serializers, PaymentFileImportStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentFileImportStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentFileImportStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentFileImport extends PaymentFileImport {
  @override
  final int confirmedCount;
  @override
  final String? detail;
  @override
  final int exceptionCount;
  @override
  final PaymentFileImportFileFormatEnum fileFormat;
  @override
  final String fileName;
  @override
  final String? formatFileId;
  @override
  final String id;
  @override
  final int? lineNo;
  @override
  final DateTime occurredAt;
  @override
  final String? reason;
  @override
  final int? recordCount;
  @override
  final String sha256;
  @override
  final PaymentFileImportStatusEnum status;
  @override
  final Money? totalAmount;
  @override
  final Money? totalFees;

  factory _$PaymentFileImport(
          [void Function(PaymentFileImportBuilder)? updates]) =>
      (PaymentFileImportBuilder()..update(updates))._build();

  _$PaymentFileImport._(
      {required this.confirmedCount,
      this.detail,
      required this.exceptionCount,
      required this.fileFormat,
      required this.fileName,
      this.formatFileId,
      required this.id,
      this.lineNo,
      required this.occurredAt,
      this.reason,
      this.recordCount,
      required this.sha256,
      required this.status,
      this.totalAmount,
      this.totalFees})
      : super._();
  @override
  PaymentFileImport rebuild(void Function(PaymentFileImportBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentFileImportBuilder toBuilder() =>
      PaymentFileImportBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentFileImport &&
        confirmedCount == other.confirmedCount &&
        detail == other.detail &&
        exceptionCount == other.exceptionCount &&
        fileFormat == other.fileFormat &&
        fileName == other.fileName &&
        formatFileId == other.formatFileId &&
        id == other.id &&
        lineNo == other.lineNo &&
        occurredAt == other.occurredAt &&
        reason == other.reason &&
        recordCount == other.recordCount &&
        sha256 == other.sha256 &&
        status == other.status &&
        totalAmount == other.totalAmount &&
        totalFees == other.totalFees;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, confirmedCount.hashCode);
    _$hash = $jc(_$hash, detail.hashCode);
    _$hash = $jc(_$hash, exceptionCount.hashCode);
    _$hash = $jc(_$hash, fileFormat.hashCode);
    _$hash = $jc(_$hash, fileName.hashCode);
    _$hash = $jc(_$hash, formatFileId.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, lineNo.hashCode);
    _$hash = $jc(_$hash, occurredAt.hashCode);
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jc(_$hash, recordCount.hashCode);
    _$hash = $jc(_$hash, sha256.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, totalAmount.hashCode);
    _$hash = $jc(_$hash, totalFees.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentFileImport')
          ..add('confirmedCount', confirmedCount)
          ..add('detail', detail)
          ..add('exceptionCount', exceptionCount)
          ..add('fileFormat', fileFormat)
          ..add('fileName', fileName)
          ..add('formatFileId', formatFileId)
          ..add('id', id)
          ..add('lineNo', lineNo)
          ..add('occurredAt', occurredAt)
          ..add('reason', reason)
          ..add('recordCount', recordCount)
          ..add('sha256', sha256)
          ..add('status', status)
          ..add('totalAmount', totalAmount)
          ..add('totalFees', totalFees))
        .toString();
  }
}

class PaymentFileImportBuilder
    implements Builder<PaymentFileImport, PaymentFileImportBuilder> {
  _$PaymentFileImport? _$v;

  int? _confirmedCount;
  int? get confirmedCount => _$this._confirmedCount;
  set confirmedCount(int? confirmedCount) =>
      _$this._confirmedCount = confirmedCount;

  String? _detail;
  String? get detail => _$this._detail;
  set detail(String? detail) => _$this._detail = detail;

  int? _exceptionCount;
  int? get exceptionCount => _$this._exceptionCount;
  set exceptionCount(int? exceptionCount) =>
      _$this._exceptionCount = exceptionCount;

  PaymentFileImportFileFormatEnum? _fileFormat;
  PaymentFileImportFileFormatEnum? get fileFormat => _$this._fileFormat;
  set fileFormat(PaymentFileImportFileFormatEnum? fileFormat) =>
      _$this._fileFormat = fileFormat;

  String? _fileName;
  String? get fileName => _$this._fileName;
  set fileName(String? fileName) => _$this._fileName = fileName;

  String? _formatFileId;
  String? get formatFileId => _$this._formatFileId;
  set formatFileId(String? formatFileId) => _$this._formatFileId = formatFileId;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  int? _lineNo;
  int? get lineNo => _$this._lineNo;
  set lineNo(int? lineNo) => _$this._lineNo = lineNo;

  DateTime? _occurredAt;
  DateTime? get occurredAt => _$this._occurredAt;
  set occurredAt(DateTime? occurredAt) => _$this._occurredAt = occurredAt;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  int? _recordCount;
  int? get recordCount => _$this._recordCount;
  set recordCount(int? recordCount) => _$this._recordCount = recordCount;

  String? _sha256;
  String? get sha256 => _$this._sha256;
  set sha256(String? sha256) => _$this._sha256 = sha256;

  PaymentFileImportStatusEnum? _status;
  PaymentFileImportStatusEnum? get status => _$this._status;
  set status(PaymentFileImportStatusEnum? status) => _$this._status = status;

  MoneyBuilder? _totalAmount;
  MoneyBuilder get totalAmount => _$this._totalAmount ??= MoneyBuilder();
  set totalAmount(MoneyBuilder? totalAmount) =>
      _$this._totalAmount = totalAmount;

  MoneyBuilder? _totalFees;
  MoneyBuilder get totalFees => _$this._totalFees ??= MoneyBuilder();
  set totalFees(MoneyBuilder? totalFees) => _$this._totalFees = totalFees;

  PaymentFileImportBuilder() {
    PaymentFileImport._defaults(this);
  }

  PaymentFileImportBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _confirmedCount = $v.confirmedCount;
      _detail = $v.detail;
      _exceptionCount = $v.exceptionCount;
      _fileFormat = $v.fileFormat;
      _fileName = $v.fileName;
      _formatFileId = $v.formatFileId;
      _id = $v.id;
      _lineNo = $v.lineNo;
      _occurredAt = $v.occurredAt;
      _reason = $v.reason;
      _recordCount = $v.recordCount;
      _sha256 = $v.sha256;
      _status = $v.status;
      _totalAmount = $v.totalAmount?.toBuilder();
      _totalFees = $v.totalFees?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentFileImport other) {
    _$v = other as _$PaymentFileImport;
  }

  @override
  void update(void Function(PaymentFileImportBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentFileImport build() => _build();

  _$PaymentFileImport _build() {
    _$PaymentFileImport _$result;
    try {
      _$result = _$v ??
          _$PaymentFileImport._(
            confirmedCount: BuiltValueNullFieldError.checkNotNull(
                confirmedCount, r'PaymentFileImport', 'confirmedCount'),
            detail: detail,
            exceptionCount: BuiltValueNullFieldError.checkNotNull(
                exceptionCount, r'PaymentFileImport', 'exceptionCount'),
            fileFormat: BuiltValueNullFieldError.checkNotNull(
                fileFormat, r'PaymentFileImport', 'fileFormat'),
            fileName: BuiltValueNullFieldError.checkNotNull(
                fileName, r'PaymentFileImport', 'fileName'),
            formatFileId: formatFileId,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'PaymentFileImport', 'id'),
            lineNo: lineNo,
            occurredAt: BuiltValueNullFieldError.checkNotNull(
                occurredAt, r'PaymentFileImport', 'occurredAt'),
            reason: reason,
            recordCount: recordCount,
            sha256: BuiltValueNullFieldError.checkNotNull(
                sha256, r'PaymentFileImport', 'sha256'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'PaymentFileImport', 'status'),
            totalAmount: _totalAmount?.build(),
            totalFees: _totalFees?.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'totalAmount';
        _totalAmount?.build();
        _$failedField = 'totalFees';
        _totalFees?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentFileImport', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
