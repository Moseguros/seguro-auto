// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'broker_proposal_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$BrokerProposalSummaryResponse extends BrokerProposalSummaryResponse {
  @override
  final ProposalChecklist checklist;
  @override
  final DateTime createdAt;
  @override
  final String? customerMsisdnMasked;
  @override
  final String customerName;
  @override
  final DateTime? expiresAt;
  @override
  final String id;
  @override
  final ProposalOrganization? insurer;
  @override
  final String number;
  @override
  final String productName;
  @override
  final ProposalStatus status;
  @override
  final Money total;

  factory _$BrokerProposalSummaryResponse(
          [void Function(BrokerProposalSummaryResponseBuilder)? updates]) =>
      (BrokerProposalSummaryResponseBuilder()..update(updates))._build();

  _$BrokerProposalSummaryResponse._(
      {required this.checklist,
      required this.createdAt,
      this.customerMsisdnMasked,
      required this.customerName,
      this.expiresAt,
      required this.id,
      this.insurer,
      required this.number,
      required this.productName,
      required this.status,
      required this.total})
      : super._();
  @override
  BrokerProposalSummaryResponse rebuild(
          void Function(BrokerProposalSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  BrokerProposalSummaryResponseBuilder toBuilder() =>
      BrokerProposalSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is BrokerProposalSummaryResponse &&
        checklist == other.checklist &&
        createdAt == other.createdAt &&
        customerMsisdnMasked == other.customerMsisdnMasked &&
        customerName == other.customerName &&
        expiresAt == other.expiresAt &&
        id == other.id &&
        insurer == other.insurer &&
        number == other.number &&
        productName == other.productName &&
        status == other.status &&
        total == other.total;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, checklist.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, customerMsisdnMasked.hashCode);
    _$hash = $jc(_$hash, customerName.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, productName.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, total.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'BrokerProposalSummaryResponse')
          ..add('checklist', checklist)
          ..add('createdAt', createdAt)
          ..add('customerMsisdnMasked', customerMsisdnMasked)
          ..add('customerName', customerName)
          ..add('expiresAt', expiresAt)
          ..add('id', id)
          ..add('insurer', insurer)
          ..add('number', number)
          ..add('productName', productName)
          ..add('status', status)
          ..add('total', total))
        .toString();
  }
}

class BrokerProposalSummaryResponseBuilder
    implements
        Builder<BrokerProposalSummaryResponse,
            BrokerProposalSummaryResponseBuilder> {
  _$BrokerProposalSummaryResponse? _$v;

  ProposalChecklistBuilder? _checklist;
  ProposalChecklistBuilder get checklist =>
      _$this._checklist ??= ProposalChecklistBuilder();
  set checklist(ProposalChecklistBuilder? checklist) =>
      _$this._checklist = checklist;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _customerMsisdnMasked;
  String? get customerMsisdnMasked => _$this._customerMsisdnMasked;
  set customerMsisdnMasked(String? customerMsisdnMasked) =>
      _$this._customerMsisdnMasked = customerMsisdnMasked;

  String? _customerName;
  String? get customerName => _$this._customerName;
  set customerName(String? customerName) => _$this._customerName = customerName;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  ProposalOrganizationBuilder? _insurer;
  ProposalOrganizationBuilder get insurer =>
      _$this._insurer ??= ProposalOrganizationBuilder();
  set insurer(ProposalOrganizationBuilder? insurer) =>
      _$this._insurer = insurer;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  String? _productName;
  String? get productName => _$this._productName;
  set productName(String? productName) => _$this._productName = productName;

  ProposalStatus? _status;
  ProposalStatus? get status => _$this._status;
  set status(ProposalStatus? status) => _$this._status = status;

  MoneyBuilder? _total;
  MoneyBuilder get total => _$this._total ??= MoneyBuilder();
  set total(MoneyBuilder? total) => _$this._total = total;

  BrokerProposalSummaryResponseBuilder() {
    BrokerProposalSummaryResponse._defaults(this);
  }

  BrokerProposalSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _checklist = $v.checklist.toBuilder();
      _createdAt = $v.createdAt;
      _customerMsisdnMasked = $v.customerMsisdnMasked;
      _customerName = $v.customerName;
      _expiresAt = $v.expiresAt;
      _id = $v.id;
      _insurer = $v.insurer?.toBuilder();
      _number = $v.number;
      _productName = $v.productName;
      _status = $v.status;
      _total = $v.total.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(BrokerProposalSummaryResponse other) {
    _$v = other as _$BrokerProposalSummaryResponse;
  }

  @override
  void update(void Function(BrokerProposalSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  BrokerProposalSummaryResponse build() => _build();

  _$BrokerProposalSummaryResponse _build() {
    _$BrokerProposalSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$BrokerProposalSummaryResponse._(
            checklist: checklist.build(),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'BrokerProposalSummaryResponse', 'createdAt'),
            customerMsisdnMasked: customerMsisdnMasked,
            customerName: BuiltValueNullFieldError.checkNotNull(
                customerName, r'BrokerProposalSummaryResponse', 'customerName'),
            expiresAt: expiresAt,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'BrokerProposalSummaryResponse', 'id'),
            insurer: _insurer?.build(),
            number: BuiltValueNullFieldError.checkNotNull(
                number, r'BrokerProposalSummaryResponse', 'number'),
            productName: BuiltValueNullFieldError.checkNotNull(
                productName, r'BrokerProposalSummaryResponse', 'productName'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'BrokerProposalSummaryResponse', 'status'),
            total: total.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'checklist';
        checklist.build();

        _$failedField = 'insurer';
        _insurer?.build();

        _$failedField = 'total';
        total.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'BrokerProposalSummaryResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
