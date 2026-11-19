// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_exception_detail.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentExceptionDetailStatusEnum _$paymentExceptionDetailStatusEnum_OPEN =
    const PaymentExceptionDetailStatusEnum._('OPEN');
const PaymentExceptionDetailStatusEnum
    _$paymentExceptionDetailStatusEnum_RESOLVED =
    const PaymentExceptionDetailStatusEnum._('RESOLVED');

PaymentExceptionDetailStatusEnum _$paymentExceptionDetailStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'OPEN':
      return _$paymentExceptionDetailStatusEnum_OPEN;
    case 'RESOLVED':
      return _$paymentExceptionDetailStatusEnum_RESOLVED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentExceptionDetailStatusEnum>
    _$paymentExceptionDetailStatusEnumValues = BuiltSet<
        PaymentExceptionDetailStatusEnum>(const <PaymentExceptionDetailStatusEnum>[
  _$paymentExceptionDetailStatusEnum_OPEN,
  _$paymentExceptionDetailStatusEnum_RESOLVED,
]);

Serializer<PaymentExceptionDetailStatusEnum>
    _$paymentExceptionDetailStatusEnumSerializer =
    _$PaymentExceptionDetailStatusEnumSerializer();

class _$PaymentExceptionDetailStatusEnumSerializer
    implements PrimitiveSerializer<PaymentExceptionDetailStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'OPEN': 'OPEN',
    'RESOLVED': 'RESOLVED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'OPEN': 'OPEN',
    'RESOLVED': 'RESOLVED',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentExceptionDetailStatusEnum];
  @override
  final String wireName = 'PaymentExceptionDetailStatusEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentExceptionDetailStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentExceptionDetailStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentExceptionDetailStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentExceptionDetail extends PaymentExceptionDetail {
  @override
  final BuiltList<PaymentResolution> allowedResolutions;
  @override
  final Money? amount;
  @override
  final String? bankTransactionId;
  @override
  final DateTime createdAt;
  @override
  final String? detail;
  @override
  final String? entity;
  @override
  final String? fileName;
  @override
  final String id;
  @override
  final PaymentExceptionIntent? intent;
  @override
  final int? lineNo;
  @override
  final PaymentExceptionIntent? linkedIntent;
  @override
  final String? note;
  @override
  final DateTime? paidAt;
  @override
  final String? reference;
  @override
  final PaymentResolution? resolution;
  @override
  final DateTime? resolvedAt;
  @override
  final String? resolvedBy;
  @override
  final PaymentExceptionDetailStatusEnum status;
  @override
  final PaymentExceptionType type;
  @override
  final int version;

  factory _$PaymentExceptionDetail(
          [void Function(PaymentExceptionDetailBuilder)? updates]) =>
      (PaymentExceptionDetailBuilder()..update(updates))._build();

  _$PaymentExceptionDetail._(
      {required this.allowedResolutions,
      this.amount,
      this.bankTransactionId,
      required this.createdAt,
      this.detail,
      this.entity,
      this.fileName,
      required this.id,
      this.intent,
      this.lineNo,
      this.linkedIntent,
      this.note,
      this.paidAt,
      this.reference,
      this.resolution,
      this.resolvedAt,
      this.resolvedBy,
      required this.status,
      required this.type,
      required this.version})
      : super._();
  @override
  PaymentExceptionDetail rebuild(
          void Function(PaymentExceptionDetailBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentExceptionDetailBuilder toBuilder() =>
      PaymentExceptionDetailBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentExceptionDetail &&
        allowedResolutions == other.allowedResolutions &&
        amount == other.amount &&
        bankTransactionId == other.bankTransactionId &&
        createdAt == other.createdAt &&
        detail == other.detail &&
        entity == other.entity &&
        fileName == other.fileName &&
        id == other.id &&
        intent == other.intent &&
        lineNo == other.lineNo &&
        linkedIntent == other.linkedIntent &&
        note == other.note &&
        paidAt == other.paidAt &&
        reference == other.reference &&
        resolution == other.resolution &&
        resolvedAt == other.resolvedAt &&
        resolvedBy == other.resolvedBy &&
        status == other.status &&
        type == other.type &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, allowedResolutions.hashCode);
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, bankTransactionId.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, detail.hashCode);
    _$hash = $jc(_$hash, entity.hashCode);
    _$hash = $jc(_$hash, fileName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, intent.hashCode);
    _$hash = $jc(_$hash, lineNo.hashCode);
    _$hash = $jc(_$hash, linkedIntent.hashCode);
    _$hash = $jc(_$hash, note.hashCode);
    _$hash = $jc(_$hash, paidAt.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, resolution.hashCode);
    _$hash = $jc(_$hash, resolvedAt.hashCode);
    _$hash = $jc(_$hash, resolvedBy.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentExceptionDetail')
          ..add('allowedResolutions', allowedResolutions)
          ..add('amount', amount)
          ..add('bankTransactionId', bankTransactionId)
          ..add('createdAt', createdAt)
          ..add('detail', detail)
          ..add('entity', entity)
          ..add('fileName', fileName)
          ..add('id', id)
          ..add('intent', intent)
          ..add('lineNo', lineNo)
          ..add('linkedIntent', linkedIntent)
          ..add('note', note)
          ..add('paidAt', paidAt)
          ..add('reference', reference)
          ..add('resolution', resolution)
          ..add('resolvedAt', resolvedAt)
          ..add('resolvedBy', resolvedBy)
          ..add('status', status)
          ..add('type', type)
          ..add('version', version))
        .toString();
  }
}

class PaymentExceptionDetailBuilder
    implements Builder<PaymentExceptionDetail, PaymentExceptionDetailBuilder> {
  _$PaymentExceptionDetail? _$v;

  ListBuilder<PaymentResolution>? _allowedResolutions;
  ListBuilder<PaymentResolution> get allowedResolutions =>
      _$this._allowedResolutions ??= ListBuilder<PaymentResolution>();
  set allowedResolutions(ListBuilder<PaymentResolution>? allowedResolutions) =>
      _$this._allowedResolutions = allowedResolutions;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  String? _bankTransactionId;
  String? get bankTransactionId => _$this._bankTransactionId;
  set bankTransactionId(String? bankTransactionId) =>
      _$this._bankTransactionId = bankTransactionId;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _detail;
  String? get detail => _$this._detail;
  set detail(String? detail) => _$this._detail = detail;

  String? _entity;
  String? get entity => _$this._entity;
  set entity(String? entity) => _$this._entity = entity;

  String? _fileName;
  String? get fileName => _$this._fileName;
  set fileName(String? fileName) => _$this._fileName = fileName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  PaymentExceptionIntentBuilder? _intent;
  PaymentExceptionIntentBuilder get intent =>
      _$this._intent ??= PaymentExceptionIntentBuilder();
  set intent(PaymentExceptionIntentBuilder? intent) => _$this._intent = intent;

  int? _lineNo;
  int? get lineNo => _$this._lineNo;
  set lineNo(int? lineNo) => _$this._lineNo = lineNo;

  PaymentExceptionIntentBuilder? _linkedIntent;
  PaymentExceptionIntentBuilder get linkedIntent =>
      _$this._linkedIntent ??= PaymentExceptionIntentBuilder();
  set linkedIntent(PaymentExceptionIntentBuilder? linkedIntent) =>
      _$this._linkedIntent = linkedIntent;

  String? _note;
  String? get note => _$this._note;
  set note(String? note) => _$this._note = note;

  DateTime? _paidAt;
  DateTime? get paidAt => _$this._paidAt;
  set paidAt(DateTime? paidAt) => _$this._paidAt = paidAt;

  String? _reference;
  String? get reference => _$this._reference;
  set reference(String? reference) => _$this._reference = reference;

  PaymentResolution? _resolution;
  PaymentResolution? get resolution => _$this._resolution;
  set resolution(PaymentResolution? resolution) =>
      _$this._resolution = resolution;

  DateTime? _resolvedAt;
  DateTime? get resolvedAt => _$this._resolvedAt;
  set resolvedAt(DateTime? resolvedAt) => _$this._resolvedAt = resolvedAt;

  String? _resolvedBy;
  String? get resolvedBy => _$this._resolvedBy;
  set resolvedBy(String? resolvedBy) => _$this._resolvedBy = resolvedBy;

  PaymentExceptionDetailStatusEnum? _status;
  PaymentExceptionDetailStatusEnum? get status => _$this._status;
  set status(PaymentExceptionDetailStatusEnum? status) =>
      _$this._status = status;

  PaymentExceptionType? _type;
  PaymentExceptionType? get type => _$this._type;
  set type(PaymentExceptionType? type) => _$this._type = type;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  PaymentExceptionDetailBuilder() {
    PaymentExceptionDetail._defaults(this);
  }

  PaymentExceptionDetailBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _allowedResolutions = $v.allowedResolutions.toBuilder();
      _amount = $v.amount?.toBuilder();
      _bankTransactionId = $v.bankTransactionId;
      _createdAt = $v.createdAt;
      _detail = $v.detail;
      _entity = $v.entity;
      _fileName = $v.fileName;
      _id = $v.id;
      _intent = $v.intent?.toBuilder();
      _lineNo = $v.lineNo;
      _linkedIntent = $v.linkedIntent?.toBuilder();
      _note = $v.note;
      _paidAt = $v.paidAt;
      _reference = $v.reference;
      _resolution = $v.resolution;
      _resolvedAt = $v.resolvedAt;
      _resolvedBy = $v.resolvedBy;
      _status = $v.status;
      _type = $v.type;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentExceptionDetail other) {
    _$v = other as _$PaymentExceptionDetail;
  }

  @override
  void update(void Function(PaymentExceptionDetailBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentExceptionDetail build() => _build();

  _$PaymentExceptionDetail _build() {
    _$PaymentExceptionDetail _$result;
    try {
      _$result = _$v ??
          _$PaymentExceptionDetail._(
            allowedResolutions: allowedResolutions.build(),
            amount: _amount?.build(),
            bankTransactionId: bankTransactionId,
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'PaymentExceptionDetail', 'createdAt'),
            detail: detail,
            entity: entity,
            fileName: fileName,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'PaymentExceptionDetail', 'id'),
            intent: _intent?.build(),
            lineNo: lineNo,
            linkedIntent: _linkedIntent?.build(),
            note: note,
            paidAt: paidAt,
            reference: reference,
            resolution: resolution,
            resolvedAt: resolvedAt,
            resolvedBy: resolvedBy,
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'PaymentExceptionDetail', 'status'),
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'PaymentExceptionDetail', 'type'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'PaymentExceptionDetail', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'allowedResolutions';
        allowedResolutions.build();
        _$failedField = 'amount';
        _amount?.build();

        _$failedField = 'intent';
        _intent?.build();

        _$failedField = 'linkedIntent';
        _linkedIntent?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentExceptionDetail', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
