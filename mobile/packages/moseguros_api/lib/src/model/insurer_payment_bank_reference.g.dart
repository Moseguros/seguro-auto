// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_payment_bank_reference.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerPaymentBankReference extends InsurerPaymentBankReference {
  @override
  final String? bankTransactionId;
  @override
  final String entity;
  @override
  final DateTime? paidAt;
  @override
  final String reference;
  @override
  final ReferenceStatus status;

  factory _$InsurerPaymentBankReference(
          [void Function(InsurerPaymentBankReferenceBuilder)? updates]) =>
      (InsurerPaymentBankReferenceBuilder()..update(updates))._build();

  _$InsurerPaymentBankReference._(
      {this.bankTransactionId,
      required this.entity,
      this.paidAt,
      required this.reference,
      required this.status})
      : super._();
  @override
  InsurerPaymentBankReference rebuild(
          void Function(InsurerPaymentBankReferenceBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerPaymentBankReferenceBuilder toBuilder() =>
      InsurerPaymentBankReferenceBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerPaymentBankReference &&
        bankTransactionId == other.bankTransactionId &&
        entity == other.entity &&
        paidAt == other.paidAt &&
        reference == other.reference &&
        status == other.status;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, bankTransactionId.hashCode);
    _$hash = $jc(_$hash, entity.hashCode);
    _$hash = $jc(_$hash, paidAt.hashCode);
    _$hash = $jc(_$hash, reference.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerPaymentBankReference')
          ..add('bankTransactionId', bankTransactionId)
          ..add('entity', entity)
          ..add('paidAt', paidAt)
          ..add('reference', reference)
          ..add('status', status))
        .toString();
  }
}

class InsurerPaymentBankReferenceBuilder
    implements
        Builder<InsurerPaymentBankReference,
            InsurerPaymentBankReferenceBuilder> {
  _$InsurerPaymentBankReference? _$v;

  String? _bankTransactionId;
  String? get bankTransactionId => _$this._bankTransactionId;
  set bankTransactionId(String? bankTransactionId) =>
      _$this._bankTransactionId = bankTransactionId;

  String? _entity;
  String? get entity => _$this._entity;
  set entity(String? entity) => _$this._entity = entity;

  DateTime? _paidAt;
  DateTime? get paidAt => _$this._paidAt;
  set paidAt(DateTime? paidAt) => _$this._paidAt = paidAt;

  String? _reference;
  String? get reference => _$this._reference;
  set reference(String? reference) => _$this._reference = reference;

  ReferenceStatus? _status;
  ReferenceStatus? get status => _$this._status;
  set status(ReferenceStatus? status) => _$this._status = status;

  InsurerPaymentBankReferenceBuilder() {
    InsurerPaymentBankReference._defaults(this);
  }

  InsurerPaymentBankReferenceBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _bankTransactionId = $v.bankTransactionId;
      _entity = $v.entity;
      _paidAt = $v.paidAt;
      _reference = $v.reference;
      _status = $v.status;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerPaymentBankReference other) {
    _$v = other as _$InsurerPaymentBankReference;
  }

  @override
  void update(void Function(InsurerPaymentBankReferenceBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerPaymentBankReference build() => _build();

  _$InsurerPaymentBankReference _build() {
    final _$result = _$v ??
        _$InsurerPaymentBankReference._(
          bankTransactionId: bankTransactionId,
          entity: BuiltValueNullFieldError.checkNotNull(
              entity, r'InsurerPaymentBankReference', 'entity'),
          paidAt: paidAt,
          reference: BuiltValueNullFieldError.checkNotNull(
              reference, r'InsurerPaymentBankReference', 'reference'),
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'InsurerPaymentBankReference', 'status'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
