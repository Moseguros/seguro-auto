// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_payment_summary.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InsurerPaymentSummaryMethodEnum _$insurerPaymentSummaryMethodEnum_MPESA =
    const InsurerPaymentSummaryMethodEnum._('MPESA');
const InsurerPaymentSummaryMethodEnum
    _$insurerPaymentSummaryMethodEnum_BANK_REFERENCE =
    const InsurerPaymentSummaryMethodEnum._('BANK_REFERENCE');

InsurerPaymentSummaryMethodEnum _$insurerPaymentSummaryMethodEnumValueOf(
    String name) {
  switch (name) {
    case 'MPESA':
      return _$insurerPaymentSummaryMethodEnum_MPESA;
    case 'BANK_REFERENCE':
      return _$insurerPaymentSummaryMethodEnum_BANK_REFERENCE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InsurerPaymentSummaryMethodEnum>
    _$insurerPaymentSummaryMethodEnumValues = BuiltSet<
        InsurerPaymentSummaryMethodEnum>(const <InsurerPaymentSummaryMethodEnum>[
  _$insurerPaymentSummaryMethodEnum_MPESA,
  _$insurerPaymentSummaryMethodEnum_BANK_REFERENCE,
]);

Serializer<InsurerPaymentSummaryMethodEnum>
    _$insurerPaymentSummaryMethodEnumSerializer =
    _$InsurerPaymentSummaryMethodEnumSerializer();

class _$InsurerPaymentSummaryMethodEnumSerializer
    implements PrimitiveSerializer<InsurerPaymentSummaryMethodEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };

  @override
  final Iterable<Type> types = const <Type>[InsurerPaymentSummaryMethodEnum];
  @override
  final String wireName = 'InsurerPaymentSummaryMethodEnum';

  @override
  Object serialize(
          Serializers serializers, InsurerPaymentSummaryMethodEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InsurerPaymentSummaryMethodEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InsurerPaymentSummaryMethodEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InsurerPaymentSummary extends InsurerPaymentSummary {
  @override
  final Money amount;
  @override
  final DateTime createdAt;
  @override
  final FailureCode? failureCode;
  @override
  final String id;
  @override
  final InsurerPaymentSummaryMethodEnum method;
  @override
  final String? providerTxId;
  @override
  final String reference;
  @override
  final PaymentStatus status;
  @override
  final DateTime? succeededAt;
  @override
  final String targetLabel;

  factory _$InsurerPaymentSummary(
          [void Function(InsurerPaymentSummaryBuilder)? updates]) =>
      (InsurerPaymentSummaryBuilder()..update(updates))._build();

  _$InsurerPaymentSummary._(
      {required this.amount,
      required this.createdAt,
      this.failureCode,
      required this.id,
      required this.method,
      this.providerTxId,
      required this.reference,
      required this.status,
      this.succeededAt,
      required this.targetLabel})
      : super._();
  @override
  InsurerPaymentSummary rebuild(
          void Function(InsurerPaymentSummaryBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerPaymentSummaryBuilder toBuilder() =>
      InsurerPaymentSummaryBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerPaymentSummary &&
        amount == other.amount &&
        createdAt == other.createdAt &&
        failureCode == other.failureCode &&
        id == other.id &&
        method == other.method &&
        providerTxId == other.providerTxId &&
        reference == other.reference &&
        status == other.status &&
        succeededAt == other.succeededAt &&
        targetLabel == other.targetLabel;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, failureCode.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, method.hashCode);
    _$hash = $jc(_$hash, providerTxId.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, succeededAt.hashCode);
    _$hash = $jc(_$hash, targetLabel.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerPaymentSummary')
          ..add('amount', amount)
          ..add('createdAt', createdAt)
          ..add('failureCode', failureCode)
          ..add('id', id)
          ..add('method', method)
          ..add('providerTxId', providerTxId)
          ..add('reference', reference)
          ..add('status', status)
          ..add('succeededAt', succeededAt)
          ..add('targetLabel', targetLabel))
        .toString();
  }
}

class InsurerPaymentSummaryBuilder
    implements Builder<InsurerPaymentSummary, InsurerPaymentSummaryBuilder> {
  _$InsurerPaymentSummary? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  FailureCode? _failureCode;
  FailureCode? get failureCode => _$this._failureCode;
  set failureCode(FailureCode? failureCode) =>
      _$this._failureCode = failureCode;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  InsurerPaymentSummaryMethodEnum? _method;
  InsurerPaymentSummaryMethodEnum? get method => _$this._method;
  set method(InsurerPaymentSummaryMethodEnum? method) =>
      _$this._method = method;

  String? _providerTxId;
  String? get providerTxId => _$this._providerTxId;
  set providerTxId(String? providerTxId) => _$this._providerTxId = providerTxId;

  String? _reference;
  String? get reference => _$this._reference;
  set reference(String? reference) => _$this._reference = reference;

  PaymentStatus? _status;
  PaymentStatus? get status => _$this._status;
  set status(PaymentStatus? status) => _$this._status = status;

  DateTime? _succeededAt;
  DateTime? get succeededAt => _$this._succeededAt;
  set succeededAt(DateTime? succeededAt) => _$this._succeededAt = succeededAt;

  String? _targetLabel;
  String? get targetLabel => _$this._targetLabel;
  set targetLabel(String? targetLabel) => _$this._targetLabel = targetLabel;

  InsurerPaymentSummaryBuilder() {
    InsurerPaymentSummary._defaults(this);
  }

  InsurerPaymentSummaryBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _createdAt = $v.createdAt;
      _failureCode = $v.failureCode;
      _id = $v.id;
      _method = $v.method;
      _providerTxId = $v.providerTxId;
      _reference = $v.reference;
      _status = $v.status;
      _succeededAt = $v.succeededAt;
      _targetLabel = $v.targetLabel;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerPaymentSummary other) {
    _$v = other as _$InsurerPaymentSummary;
  }

  @override
  void update(void Function(InsurerPaymentSummaryBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerPaymentSummary build() => _build();

  _$InsurerPaymentSummary _build() {
    _$InsurerPaymentSummary _$result;
    try {
      _$result = _$v ??
          _$InsurerPaymentSummary._(
            amount: amount.build(),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'InsurerPaymentSummary', 'createdAt'),
            failureCode: failureCode,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerPaymentSummary', 'id'),
            method: BuiltValueNullFieldError.checkNotNull(
                method, r'InsurerPaymentSummary', 'method'),
            providerTxId: providerTxId,
            reference: BuiltValueNullFieldError.checkNotNull(
                reference, r'InsurerPaymentSummary', 'reference'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'InsurerPaymentSummary', 'status'),
            succeededAt: succeededAt,
            targetLabel: BuiltValueNullFieldError.checkNotNull(
                targetLabel, r'InsurerPaymentSummary', 'targetLabel'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerPaymentSummary', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
