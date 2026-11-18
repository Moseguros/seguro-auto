// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_payment_detail.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InsurerPaymentDetailMethodEnum _$insurerPaymentDetailMethodEnum_MPESA =
    const InsurerPaymentDetailMethodEnum._('MPESA');
const InsurerPaymentDetailMethodEnum
    _$insurerPaymentDetailMethodEnum_BANK_REFERENCE =
    const InsurerPaymentDetailMethodEnum._('BANK_REFERENCE');

InsurerPaymentDetailMethodEnum _$insurerPaymentDetailMethodEnumValueOf(
    String name) {
  switch (name) {
    case 'MPESA':
      return _$insurerPaymentDetailMethodEnum_MPESA;
    case 'BANK_REFERENCE':
      return _$insurerPaymentDetailMethodEnum_BANK_REFERENCE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InsurerPaymentDetailMethodEnum>
    _$insurerPaymentDetailMethodEnumValues = BuiltSet<
        InsurerPaymentDetailMethodEnum>(const <InsurerPaymentDetailMethodEnum>[
  _$insurerPaymentDetailMethodEnum_MPESA,
  _$insurerPaymentDetailMethodEnum_BANK_REFERENCE,
]);

Serializer<InsurerPaymentDetailMethodEnum>
    _$insurerPaymentDetailMethodEnumSerializer =
    _$InsurerPaymentDetailMethodEnumSerializer();

class _$InsurerPaymentDetailMethodEnumSerializer
    implements PrimitiveSerializer<InsurerPaymentDetailMethodEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };

  @override
  final Iterable<Type> types = const <Type>[InsurerPaymentDetailMethodEnum];
  @override
  final String wireName = 'InsurerPaymentDetailMethodEnum';

  @override
  Object serialize(
          Serializers serializers, InsurerPaymentDetailMethodEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InsurerPaymentDetailMethodEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InsurerPaymentDetailMethodEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InsurerPaymentDetail extends InsurerPaymentDetail {
  @override
  final Money amount;
  @override
  final InsurerPaymentBankReference? bankReference;
  @override
  final PaymentConfirmedBy? confirmedBy;
  @override
  final DateTime createdAt;
  @override
  final BuiltList<InsurerPaymentEvent> events;
  @override
  final BuiltList<String> exceptionIds;
  @override
  final DateTime expiresAt;
  @override
  final FailureCode? failureCode;
  @override
  final String id;
  @override
  final InsurerPaymentDetailMethodEnum method;
  @override
  final String? payerMsisdnMasked;
  @override
  final String? providerTxId;
  @override
  final PaymentPurpose purpose;
  @override
  final String reference;
  @override
  final PaymentStatus status;
  @override
  final DateTime? succeededAt;
  @override
  final PaymentTargetRef target;
  @override
  final String targetLabel;

  factory _$InsurerPaymentDetail(
          [void Function(InsurerPaymentDetailBuilder)? updates]) =>
      (InsurerPaymentDetailBuilder()..update(updates))._build();

  _$InsurerPaymentDetail._(
      {required this.amount,
      this.bankReference,
      this.confirmedBy,
      required this.createdAt,
      required this.events,
      required this.exceptionIds,
      required this.expiresAt,
      this.failureCode,
      required this.id,
      required this.method,
      this.payerMsisdnMasked,
      this.providerTxId,
      required this.purpose,
      required this.reference,
      required this.status,
      this.succeededAt,
      required this.target,
      required this.targetLabel})
      : super._();
  @override
  InsurerPaymentDetail rebuild(
          void Function(InsurerPaymentDetailBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerPaymentDetailBuilder toBuilder() =>
      InsurerPaymentDetailBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerPaymentDetail &&
        amount == other.amount &&
        bankReference == other.bankReference &&
        confirmedBy == other.confirmedBy &&
        createdAt == other.createdAt &&
        events == other.events &&
        exceptionIds == other.exceptionIds &&
        expiresAt == other.expiresAt &&
        failureCode == other.failureCode &&
        id == other.id &&
        method == other.method &&
        payerMsisdnMasked == other.payerMsisdnMasked &&
        providerTxId == other.providerTxId &&
        purpose == other.purpose &&
        reference == other.reference &&
        status == other.status &&
        succeededAt == other.succeededAt &&
        target == other.target &&
        targetLabel == other.targetLabel;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, bankReference.hashCode);
    _$hash = $jc(_$hash, confirmedBy.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, events.hashCode);
    _$hash = $jc(_$hash, exceptionIds.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, failureCode.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, method.hashCode);
    _$hash = $jc(_$hash, payerMsisdnMasked.hashCode);
    _$hash = $jc(_$hash, providerTxId.hashCode);
    _$hash = $jc(_$hash, purpose.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, succeededAt.hashCode);
    _$hash = $jc(_$hash, target.hashCode);
    _$hash = $jc(_$hash, targetLabel.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerPaymentDetail')
          ..add('amount', amount)
          ..add('bankReference', bankReference)
          ..add('confirmedBy', confirmedBy)
          ..add('createdAt', createdAt)
          ..add('events', events)
          ..add('exceptionIds', exceptionIds)
          ..add('expiresAt', expiresAt)
          ..add('failureCode', failureCode)
          ..add('id', id)
          ..add('method', method)
          ..add('payerMsisdnMasked', payerMsisdnMasked)
          ..add('providerTxId', providerTxId)
          ..add('purpose', purpose)
          ..add('reference', reference)
          ..add('status', status)
          ..add('succeededAt', succeededAt)
          ..add('target', target)
          ..add('targetLabel', targetLabel))
        .toString();
  }
}

class InsurerPaymentDetailBuilder
    implements Builder<InsurerPaymentDetail, InsurerPaymentDetailBuilder> {
  _$InsurerPaymentDetail? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  InsurerPaymentBankReferenceBuilder? _bankReference;
  InsurerPaymentBankReferenceBuilder get bankReference =>
      _$this._bankReference ??= InsurerPaymentBankReferenceBuilder();
  set bankReference(InsurerPaymentBankReferenceBuilder? bankReference) =>
      _$this._bankReference = bankReference;

  PaymentConfirmedBy? _confirmedBy;
  PaymentConfirmedBy? get confirmedBy => _$this._confirmedBy;
  set confirmedBy(PaymentConfirmedBy? confirmedBy) =>
      _$this._confirmedBy = confirmedBy;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  ListBuilder<InsurerPaymentEvent>? _events;
  ListBuilder<InsurerPaymentEvent> get events =>
      _$this._events ??= ListBuilder<InsurerPaymentEvent>();
  set events(ListBuilder<InsurerPaymentEvent>? events) =>
      _$this._events = events;

  ListBuilder<String>? _exceptionIds;
  ListBuilder<String> get exceptionIds =>
      _$this._exceptionIds ??= ListBuilder<String>();
  set exceptionIds(ListBuilder<String>? exceptionIds) =>
      _$this._exceptionIds = exceptionIds;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  FailureCode? _failureCode;
  FailureCode? get failureCode => _$this._failureCode;
  set failureCode(FailureCode? failureCode) =>
      _$this._failureCode = failureCode;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  InsurerPaymentDetailMethodEnum? _method;
  InsurerPaymentDetailMethodEnum? get method => _$this._method;
  set method(InsurerPaymentDetailMethodEnum? method) => _$this._method = method;

  String? _payerMsisdnMasked;
  String? get payerMsisdnMasked => _$this._payerMsisdnMasked;
  set payerMsisdnMasked(String? payerMsisdnMasked) =>
      _$this._payerMsisdnMasked = payerMsisdnMasked;

  String? _providerTxId;
  String? get providerTxId => _$this._providerTxId;
  set providerTxId(String? providerTxId) => _$this._providerTxId = providerTxId;

  PaymentPurpose? _purpose;
  PaymentPurpose? get purpose => _$this._purpose;
  set purpose(PaymentPurpose? purpose) => _$this._purpose = purpose;

  String? _reference;
  String? get reference => _$this._reference;
  set reference(String? reference) => _$this._reference = reference;

  PaymentStatus? _status;
  PaymentStatus? get status => _$this._status;
  set status(PaymentStatus? status) => _$this._status = status;

  DateTime? _succeededAt;
  DateTime? get succeededAt => _$this._succeededAt;
  set succeededAt(DateTime? succeededAt) => _$this._succeededAt = succeededAt;

  PaymentTargetRefBuilder? _target;
  PaymentTargetRefBuilder get target =>
      _$this._target ??= PaymentTargetRefBuilder();
  set target(PaymentTargetRefBuilder? target) => _$this._target = target;

  String? _targetLabel;
  String? get targetLabel => _$this._targetLabel;
  set targetLabel(String? targetLabel) => _$this._targetLabel = targetLabel;

  InsurerPaymentDetailBuilder() {
    InsurerPaymentDetail._defaults(this);
  }

  InsurerPaymentDetailBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _bankReference = $v.bankReference?.toBuilder();
      _confirmedBy = $v.confirmedBy;
      _createdAt = $v.createdAt;
      _events = $v.events.toBuilder();
      _exceptionIds = $v.exceptionIds.toBuilder();
      _expiresAt = $v.expiresAt;
      _failureCode = $v.failureCode;
      _id = $v.id;
      _method = $v.method;
      _payerMsisdnMasked = $v.payerMsisdnMasked;
      _providerTxId = $v.providerTxId;
      _purpose = $v.purpose;
      _reference = $v.reference;
      _status = $v.status;
      _succeededAt = $v.succeededAt;
      _target = $v.target.toBuilder();
      _targetLabel = $v.targetLabel;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerPaymentDetail other) {
    _$v = other as _$InsurerPaymentDetail;
  }

  @override
  void update(void Function(InsurerPaymentDetailBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerPaymentDetail build() => _build();

  _$InsurerPaymentDetail _build() {
    _$InsurerPaymentDetail _$result;
    try {
      _$result = _$v ??
          _$InsurerPaymentDetail._(
            amount: amount.build(),
            bankReference: _bankReference?.build(),
            confirmedBy: confirmedBy,
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'InsurerPaymentDetail', 'createdAt'),
            events: events.build(),
            exceptionIds: exceptionIds.build(),
            expiresAt: BuiltValueNullFieldError.checkNotNull(
                expiresAt, r'InsurerPaymentDetail', 'expiresAt'),
            failureCode: failureCode,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerPaymentDetail', 'id'),
            method: BuiltValueNullFieldError.checkNotNull(
                method, r'InsurerPaymentDetail', 'method'),
            payerMsisdnMasked: payerMsisdnMasked,
            providerTxId: providerTxId,
            purpose: BuiltValueNullFieldError.checkNotNull(
                purpose, r'InsurerPaymentDetail', 'purpose'),
            reference: BuiltValueNullFieldError.checkNotNull(
                reference, r'InsurerPaymentDetail', 'reference'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'InsurerPaymentDetail', 'status'),
            succeededAt: succeededAt,
            target: target.build(),
            targetLabel: BuiltValueNullFieldError.checkNotNull(
                targetLabel, r'InsurerPaymentDetail', 'targetLabel'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();
        _$failedField = 'bankReference';
        _bankReference?.build();

        _$failedField = 'events';
        events.build();
        _$failedField = 'exceptionIds';
        exceptionIds.build();

        _$failedField = 'target';
        target.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerPaymentDetail', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
