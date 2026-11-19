// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_exception_summary.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentExceptionSummaryStatusEnum
    _$paymentExceptionSummaryStatusEnum_OPEN =
    const PaymentExceptionSummaryStatusEnum._('OPEN');
const PaymentExceptionSummaryStatusEnum
    _$paymentExceptionSummaryStatusEnum_RESOLVED =
    const PaymentExceptionSummaryStatusEnum._('RESOLVED');

PaymentExceptionSummaryStatusEnum _$paymentExceptionSummaryStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'OPEN':
      return _$paymentExceptionSummaryStatusEnum_OPEN;
    case 'RESOLVED':
      return _$paymentExceptionSummaryStatusEnum_RESOLVED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentExceptionSummaryStatusEnum>
    _$paymentExceptionSummaryStatusEnumValues = BuiltSet<
        PaymentExceptionSummaryStatusEnum>(const <PaymentExceptionSummaryStatusEnum>[
  _$paymentExceptionSummaryStatusEnum_OPEN,
  _$paymentExceptionSummaryStatusEnum_RESOLVED,
]);

Serializer<PaymentExceptionSummaryStatusEnum>
    _$paymentExceptionSummaryStatusEnumSerializer =
    _$PaymentExceptionSummaryStatusEnumSerializer();

class _$PaymentExceptionSummaryStatusEnumSerializer
    implements PrimitiveSerializer<PaymentExceptionSummaryStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'OPEN': 'OPEN',
    'RESOLVED': 'RESOLVED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'OPEN': 'OPEN',
    'RESOLVED': 'RESOLVED',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentExceptionSummaryStatusEnum];
  @override
  final String wireName = 'PaymentExceptionSummaryStatusEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentExceptionSummaryStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentExceptionSummaryStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentExceptionSummaryStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentExceptionSummary extends PaymentExceptionSummary {
  @override
  final Money? amount;
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
  final DateTime? paidAt;
  @override
  final String? reference;
  @override
  final PaymentResolution? resolution;
  @override
  final PaymentExceptionSummaryStatusEnum status;
  @override
  final PaymentExceptionType type;

  factory _$PaymentExceptionSummary(
          [void Function(PaymentExceptionSummaryBuilder)? updates]) =>
      (PaymentExceptionSummaryBuilder()..update(updates))._build();

  _$PaymentExceptionSummary._(
      {this.amount,
      required this.createdAt,
      this.detail,
      this.entity,
      this.fileName,
      required this.id,
      this.intent,
      this.lineNo,
      this.paidAt,
      this.reference,
      this.resolution,
      required this.status,
      required this.type})
      : super._();
  @override
  PaymentExceptionSummary rebuild(
          void Function(PaymentExceptionSummaryBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentExceptionSummaryBuilder toBuilder() =>
      PaymentExceptionSummaryBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentExceptionSummary &&
        amount == other.amount &&
        createdAt == other.createdAt &&
        detail == other.detail &&
        entity == other.entity &&
        fileName == other.fileName &&
        id == other.id &&
        intent == other.intent &&
        lineNo == other.lineNo &&
        paidAt == other.paidAt &&
        reference == other.reference &&
        resolution == other.resolution &&
        status == other.status &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, detail.hashCode);
    _$hash = $jc(_$hash, entity.hashCode);
    _$hash = $jc(_$hash, fileName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, intent.hashCode);
    _$hash = $jc(_$hash, lineNo.hashCode);
    _$hash = $jc(_$hash, paidAt.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, resolution.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentExceptionSummary')
          ..add('amount', amount)
          ..add('createdAt', createdAt)
          ..add('detail', detail)
          ..add('entity', entity)
          ..add('fileName', fileName)
          ..add('id', id)
          ..add('intent', intent)
          ..add('lineNo', lineNo)
          ..add('paidAt', paidAt)
          ..add('reference', reference)
          ..add('resolution', resolution)
          ..add('status', status)
          ..add('type', type))
        .toString();
  }
}

class PaymentExceptionSummaryBuilder
    implements
        Builder<PaymentExceptionSummary, PaymentExceptionSummaryBuilder> {
  _$PaymentExceptionSummary? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

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

  PaymentExceptionSummaryStatusEnum? _status;
  PaymentExceptionSummaryStatusEnum? get status => _$this._status;
  set status(PaymentExceptionSummaryStatusEnum? status) =>
      _$this._status = status;

  PaymentExceptionType? _type;
  PaymentExceptionType? get type => _$this._type;
  set type(PaymentExceptionType? type) => _$this._type = type;

  PaymentExceptionSummaryBuilder() {
    PaymentExceptionSummary._defaults(this);
  }

  PaymentExceptionSummaryBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount?.toBuilder();
      _createdAt = $v.createdAt;
      _detail = $v.detail;
      _entity = $v.entity;
      _fileName = $v.fileName;
      _id = $v.id;
      _intent = $v.intent?.toBuilder();
      _lineNo = $v.lineNo;
      _paidAt = $v.paidAt;
      _reference = $v.reference;
      _resolution = $v.resolution;
      _status = $v.status;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentExceptionSummary other) {
    _$v = other as _$PaymentExceptionSummary;
  }

  @override
  void update(void Function(PaymentExceptionSummaryBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentExceptionSummary build() => _build();

  _$PaymentExceptionSummary _build() {
    _$PaymentExceptionSummary _$result;
    try {
      _$result = _$v ??
          _$PaymentExceptionSummary._(
            amount: _amount?.build(),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'PaymentExceptionSummary', 'createdAt'),
            detail: detail,
            entity: entity,
            fileName: fileName,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'PaymentExceptionSummary', 'id'),
            intent: _intent?.build(),
            lineNo: lineNo,
            paidAt: paidAt,
            reference: reference,
            resolution: resolution,
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'PaymentExceptionSummary', 'status'),
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'PaymentExceptionSummary', 'type'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        _amount?.build();

        _$failedField = 'intent';
        _intent?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentExceptionSummary', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
