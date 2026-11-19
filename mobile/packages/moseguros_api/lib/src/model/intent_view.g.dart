// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'intent_view.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const IntentViewMethodEnum _$intentViewMethodEnum_MPESA =
    const IntentViewMethodEnum._('MPESA');
const IntentViewMethodEnum _$intentViewMethodEnum_BANK_REFERENCE =
    const IntentViewMethodEnum._('BANK_REFERENCE');

IntentViewMethodEnum _$intentViewMethodEnumValueOf(String name) {
  switch (name) {
    case 'MPESA':
      return _$intentViewMethodEnum_MPESA;
    case 'BANK_REFERENCE':
      return _$intentViewMethodEnum_BANK_REFERENCE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<IntentViewMethodEnum> _$intentViewMethodEnumValues =
    BuiltSet<IntentViewMethodEnum>(const <IntentViewMethodEnum>[
  _$intentViewMethodEnum_MPESA,
  _$intentViewMethodEnum_BANK_REFERENCE,
]);

Serializer<IntentViewMethodEnum> _$intentViewMethodEnumSerializer =
    _$IntentViewMethodEnumSerializer();

class _$IntentViewMethodEnumSerializer
    implements PrimitiveSerializer<IntentViewMethodEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };

  @override
  final Iterable<Type> types = const <Type>[IntentViewMethodEnum];
  @override
  final String wireName = 'IntentViewMethodEnum';

  @override
  Object serialize(Serializers serializers, IntentViewMethodEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  IntentViewMethodEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      IntentViewMethodEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$IntentView extends IntentView {
  @override
  final Money amount;
  @override
  final DateTime createdAt;
  @override
  final DateTime expiresAt;
  @override
  final FailureCode? failureCode;
  @override
  final String id;
  @override
  final PaymentInsurerRef insurer;
  @override
  final IntentViewMethodEnum method;
  @override
  final PaymentNextAction nextAction;
  @override
  final String? payerMsisdnMasked;
  @override
  final String payerUserId;
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

  factory _$IntentView([void Function(IntentViewBuilder)? updates]) =>
      (IntentViewBuilder()..update(updates))._build();

  _$IntentView._(
      {required this.amount,
      required this.createdAt,
      required this.expiresAt,
      this.failureCode,
      required this.id,
      required this.insurer,
      required this.method,
      required this.nextAction,
      this.payerMsisdnMasked,
      required this.payerUserId,
      required this.purpose,
      required this.reference,
      required this.status,
      this.succeededAt,
      required this.target})
      : super._();
  @override
  IntentView rebuild(void Function(IntentViewBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  IntentViewBuilder toBuilder() => IntentViewBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is IntentView &&
        amount == other.amount &&
        createdAt == other.createdAt &&
        expiresAt == other.expiresAt &&
        failureCode == other.failureCode &&
        id == other.id &&
        insurer == other.insurer &&
        method == other.method &&
        nextAction == other.nextAction &&
        payerMsisdnMasked == other.payerMsisdnMasked &&
        payerUserId == other.payerUserId &&
        purpose == other.purpose &&
        reference == other.reference &&
        status == other.status &&
        succeededAt == other.succeededAt &&
        target == other.target;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, failureCode.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, method.hashCode);
    _$hash = $jc(_$hash, nextAction.hashCode);
    _$hash = $jc(_$hash, payerMsisdnMasked.hashCode);
    _$hash = $jc(_$hash, payerUserId.hashCode);
    _$hash = $jc(_$hash, purpose.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, succeededAt.hashCode);
    _$hash = $jc(_$hash, target.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'IntentView')
          ..add('amount', amount)
          ..add('createdAt', createdAt)
          ..add('expiresAt', expiresAt)
          ..add('failureCode', failureCode)
          ..add('id', id)
          ..add('insurer', insurer)
          ..add('method', method)
          ..add('nextAction', nextAction)
          ..add('payerMsisdnMasked', payerMsisdnMasked)
          ..add('payerUserId', payerUserId)
          ..add('purpose', purpose)
          ..add('reference', reference)
          ..add('status', status)
          ..add('succeededAt', succeededAt)
          ..add('target', target))
        .toString();
  }
}

class IntentViewBuilder implements Builder<IntentView, IntentViewBuilder> {
  _$IntentView? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

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

  PaymentInsurerRefBuilder? _insurer;
  PaymentInsurerRefBuilder get insurer =>
      _$this._insurer ??= PaymentInsurerRefBuilder();
  set insurer(PaymentInsurerRefBuilder? insurer) => _$this._insurer = insurer;

  IntentViewMethodEnum? _method;
  IntentViewMethodEnum? get method => _$this._method;
  set method(IntentViewMethodEnum? method) => _$this._method = method;

  PaymentNextActionBuilder? _nextAction;
  PaymentNextActionBuilder get nextAction =>
      _$this._nextAction ??= PaymentNextActionBuilder();
  set nextAction(PaymentNextActionBuilder? nextAction) =>
      _$this._nextAction = nextAction;

  String? _payerMsisdnMasked;
  String? get payerMsisdnMasked => _$this._payerMsisdnMasked;
  set payerMsisdnMasked(String? payerMsisdnMasked) =>
      _$this._payerMsisdnMasked = payerMsisdnMasked;

  String? _payerUserId;
  String? get payerUserId => _$this._payerUserId;
  set payerUserId(String? payerUserId) => _$this._payerUserId = payerUserId;

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

  IntentViewBuilder() {
    IntentView._defaults(this);
  }

  IntentViewBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _createdAt = $v.createdAt;
      _expiresAt = $v.expiresAt;
      _failureCode = $v.failureCode;
      _id = $v.id;
      _insurer = $v.insurer.toBuilder();
      _method = $v.method;
      _nextAction = $v.nextAction.toBuilder();
      _payerMsisdnMasked = $v.payerMsisdnMasked;
      _payerUserId = $v.payerUserId;
      _purpose = $v.purpose;
      _reference = $v.reference;
      _status = $v.status;
      _succeededAt = $v.succeededAt;
      _target = $v.target.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(IntentView other) {
    _$v = other as _$IntentView;
  }

  @override
  void update(void Function(IntentViewBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  IntentView build() => _build();

  _$IntentView _build() {
    _$IntentView _$result;
    try {
      _$result = _$v ??
          _$IntentView._(
            amount: amount.build(),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'IntentView', 'createdAt'),
            expiresAt: BuiltValueNullFieldError.checkNotNull(
                expiresAt, r'IntentView', 'expiresAt'),
            failureCode: failureCode,
            id: BuiltValueNullFieldError.checkNotNull(id, r'IntentView', 'id'),
            insurer: insurer.build(),
            method: BuiltValueNullFieldError.checkNotNull(
                method, r'IntentView', 'method'),
            nextAction: nextAction.build(),
            payerMsisdnMasked: payerMsisdnMasked,
            payerUserId: BuiltValueNullFieldError.checkNotNull(
                payerUserId, r'IntentView', 'payerUserId'),
            purpose: BuiltValueNullFieldError.checkNotNull(
                purpose, r'IntentView', 'purpose'),
            reference: BuiltValueNullFieldError.checkNotNull(
                reference, r'IntentView', 'reference'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'IntentView', 'status'),
            succeededAt: succeededAt,
            target: target.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();

        _$failedField = 'insurer';
        insurer.build();

        _$failedField = 'nextAction';
        nextAction.build();

        _$failedField = 'target';
        target.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'IntentView', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
