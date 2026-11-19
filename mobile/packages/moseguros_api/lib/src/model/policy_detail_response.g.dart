// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_detail_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PolicyDetailResponse extends PolicyDetailResponse {
  @override
  final PolicyCertificate? certificate;
  @override
  final CoverStatus coverStatus;
  @override
  final CoverageType coverageType;
  @override
  final BuiltList<PolicyCoverage> coverages;
  @override
  final DateTime endAt;
  @override
  final BuiltList<PolicyTimelineEvent>? events;
  @override
  final String id;
  @override
  final BuiltList<PolicyInstalment> instalments;
  @override
  final PolicyInsurerRef insurer;
  @override
  final DateTime issuedAt;
  @override
  final String number;
  @override
  final PaymentPlan paymentPlan;
  @override
  final String policyholderName;
  @override
  final PolicyProductRef product;
  @override
  final PolicyProposalRef? proposal;
  @override
  final RenewalType renewalType;
  @override
  final DateTime startAt;
  @override
  final PolicyStatus status;
  @override
  final Money totalPremium;
  @override
  final PolicyVehicleRef vehicle;

  factory _$PolicyDetailResponse(
          [void Function(PolicyDetailResponseBuilder)? updates]) =>
      (PolicyDetailResponseBuilder()..update(updates))._build();

  _$PolicyDetailResponse._(
      {this.certificate,
      required this.coverStatus,
      required this.coverageType,
      required this.coverages,
      required this.endAt,
      this.events,
      required this.id,
      required this.instalments,
      required this.insurer,
      required this.issuedAt,
      required this.number,
      required this.paymentPlan,
      required this.policyholderName,
      required this.product,
      this.proposal,
      required this.renewalType,
      required this.startAt,
      required this.status,
      required this.totalPremium,
      required this.vehicle})
      : super._();
  @override
  PolicyDetailResponse rebuild(
          void Function(PolicyDetailResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyDetailResponseBuilder toBuilder() =>
      PolicyDetailResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyDetailResponse &&
        certificate == other.certificate &&
        coverStatus == other.coverStatus &&
        coverageType == other.coverageType &&
        coverages == other.coverages &&
        endAt == other.endAt &&
        events == other.events &&
        id == other.id &&
        instalments == other.instalments &&
        insurer == other.insurer &&
        issuedAt == other.issuedAt &&
        number == other.number &&
        paymentPlan == other.paymentPlan &&
        policyholderName == other.policyholderName &&
        product == other.product &&
        proposal == other.proposal &&
        renewalType == other.renewalType &&
        startAt == other.startAt &&
        status == other.status &&
        totalPremium == other.totalPremium &&
        vehicle == other.vehicle;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, certificate.hashCode);
    _$hash = $jc(_$hash, coverStatus.hashCode);
    _$hash = $jc(_$hash, coverageType.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, endAt.hashCode);
    _$hash = $jc(_$hash, events.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, instalments.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, issuedAt.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, paymentPlan.hashCode);
    _$hash = $jc(_$hash, policyholderName.hashCode);
    _$hash = $jc(_$hash, product.hashCode);
    _$hash = $jc(_$hash, proposal.hashCode);
    _$hash = $jc(_$hash, renewalType.hashCode);
    _$hash = $jc(_$hash, startAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, totalPremium.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyDetailResponse')
          ..add('certificate', certificate)
          ..add('coverStatus', coverStatus)
          ..add('coverageType', coverageType)
          ..add('coverages', coverages)
          ..add('endAt', endAt)
          ..add('events', events)
          ..add('id', id)
          ..add('instalments', instalments)
          ..add('insurer', insurer)
          ..add('issuedAt', issuedAt)
          ..add('number', number)
          ..add('paymentPlan', paymentPlan)
          ..add('policyholderName', policyholderName)
          ..add('product', product)
          ..add('proposal', proposal)
          ..add('renewalType', renewalType)
          ..add('startAt', startAt)
          ..add('status', status)
          ..add('totalPremium', totalPremium)
          ..add('vehicle', vehicle))
        .toString();
  }
}

class PolicyDetailResponseBuilder
    implements Builder<PolicyDetailResponse, PolicyDetailResponseBuilder> {
  _$PolicyDetailResponse? _$v;

  PolicyCertificateBuilder? _certificate;
  PolicyCertificateBuilder get certificate =>
      _$this._certificate ??= PolicyCertificateBuilder();
  set certificate(PolicyCertificateBuilder? certificate) =>
      _$this._certificate = certificate;

  CoverStatus? _coverStatus;
  CoverStatus? get coverStatus => _$this._coverStatus;
  set coverStatus(CoverStatus? coverStatus) =>
      _$this._coverStatus = coverStatus;

  CoverageType? _coverageType;
  CoverageType? get coverageType => _$this._coverageType;
  set coverageType(CoverageType? coverageType) =>
      _$this._coverageType = coverageType;

  ListBuilder<PolicyCoverage>? _coverages;
  ListBuilder<PolicyCoverage> get coverages =>
      _$this._coverages ??= ListBuilder<PolicyCoverage>();
  set coverages(ListBuilder<PolicyCoverage>? coverages) =>
      _$this._coverages = coverages;

  DateTime? _endAt;
  DateTime? get endAt => _$this._endAt;
  set endAt(DateTime? endAt) => _$this._endAt = endAt;

  ListBuilder<PolicyTimelineEvent>? _events;
  ListBuilder<PolicyTimelineEvent> get events =>
      _$this._events ??= ListBuilder<PolicyTimelineEvent>();
  set events(ListBuilder<PolicyTimelineEvent>? events) =>
      _$this._events = events;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  ListBuilder<PolicyInstalment>? _instalments;
  ListBuilder<PolicyInstalment> get instalments =>
      _$this._instalments ??= ListBuilder<PolicyInstalment>();
  set instalments(ListBuilder<PolicyInstalment>? instalments) =>
      _$this._instalments = instalments;

  PolicyInsurerRefBuilder? _insurer;
  PolicyInsurerRefBuilder get insurer =>
      _$this._insurer ??= PolicyInsurerRefBuilder();
  set insurer(PolicyInsurerRefBuilder? insurer) => _$this._insurer = insurer;

  DateTime? _issuedAt;
  DateTime? get issuedAt => _$this._issuedAt;
  set issuedAt(DateTime? issuedAt) => _$this._issuedAt = issuedAt;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  PaymentPlan? _paymentPlan;
  PaymentPlan? get paymentPlan => _$this._paymentPlan;
  set paymentPlan(PaymentPlan? paymentPlan) =>
      _$this._paymentPlan = paymentPlan;

  String? _policyholderName;
  String? get policyholderName => _$this._policyholderName;
  set policyholderName(String? policyholderName) =>
      _$this._policyholderName = policyholderName;

  PolicyProductRefBuilder? _product;
  PolicyProductRefBuilder get product =>
      _$this._product ??= PolicyProductRefBuilder();
  set product(PolicyProductRefBuilder? product) => _$this._product = product;

  PolicyProposalRefBuilder? _proposal;
  PolicyProposalRefBuilder get proposal =>
      _$this._proposal ??= PolicyProposalRefBuilder();
  set proposal(PolicyProposalRefBuilder? proposal) =>
      _$this._proposal = proposal;

  RenewalType? _renewalType;
  RenewalType? get renewalType => _$this._renewalType;
  set renewalType(RenewalType? renewalType) =>
      _$this._renewalType = renewalType;

  DateTime? _startAt;
  DateTime? get startAt => _$this._startAt;
  set startAt(DateTime? startAt) => _$this._startAt = startAt;

  PolicyStatus? _status;
  PolicyStatus? get status => _$this._status;
  set status(PolicyStatus? status) => _$this._status = status;

  MoneyBuilder? _totalPremium;
  MoneyBuilder get totalPremium => _$this._totalPremium ??= MoneyBuilder();
  set totalPremium(MoneyBuilder? totalPremium) =>
      _$this._totalPremium = totalPremium;

  PolicyVehicleRefBuilder? _vehicle;
  PolicyVehicleRefBuilder get vehicle =>
      _$this._vehicle ??= PolicyVehicleRefBuilder();
  set vehicle(PolicyVehicleRefBuilder? vehicle) => _$this._vehicle = vehicle;

  PolicyDetailResponseBuilder() {
    PolicyDetailResponse._defaults(this);
  }

  PolicyDetailResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _certificate = $v.certificate?.toBuilder();
      _coverStatus = $v.coverStatus;
      _coverageType = $v.coverageType;
      _coverages = $v.coverages.toBuilder();
      _endAt = $v.endAt;
      _events = $v.events?.toBuilder();
      _id = $v.id;
      _instalments = $v.instalments.toBuilder();
      _insurer = $v.insurer.toBuilder();
      _issuedAt = $v.issuedAt;
      _number = $v.number;
      _paymentPlan = $v.paymentPlan;
      _policyholderName = $v.policyholderName;
      _product = $v.product.toBuilder();
      _proposal = $v.proposal?.toBuilder();
      _renewalType = $v.renewalType;
      _startAt = $v.startAt;
      _status = $v.status;
      _totalPremium = $v.totalPremium.toBuilder();
      _vehicle = $v.vehicle.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyDetailResponse other) {
    _$v = other as _$PolicyDetailResponse;
  }

  @override
  void update(void Function(PolicyDetailResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyDetailResponse build() => _build();

  _$PolicyDetailResponse _build() {
    _$PolicyDetailResponse _$result;
    try {
      _$result = _$v ??
          _$PolicyDetailResponse._(
            certificate: _certificate?.build(),
            coverStatus: BuiltValueNullFieldError.checkNotNull(
                coverStatus, r'PolicyDetailResponse', 'coverStatus'),
            coverageType: BuiltValueNullFieldError.checkNotNull(
                coverageType, r'PolicyDetailResponse', 'coverageType'),
            coverages: coverages.build(),
            endAt: BuiltValueNullFieldError.checkNotNull(
                endAt, r'PolicyDetailResponse', 'endAt'),
            events: _events?.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'PolicyDetailResponse', 'id'),
            instalments: instalments.build(),
            insurer: insurer.build(),
            issuedAt: BuiltValueNullFieldError.checkNotNull(
                issuedAt, r'PolicyDetailResponse', 'issuedAt'),
            number: BuiltValueNullFieldError.checkNotNull(
                number, r'PolicyDetailResponse', 'number'),
            paymentPlan: BuiltValueNullFieldError.checkNotNull(
                paymentPlan, r'PolicyDetailResponse', 'paymentPlan'),
            policyholderName: BuiltValueNullFieldError.checkNotNull(
                policyholderName, r'PolicyDetailResponse', 'policyholderName'),
            product: product.build(),
            proposal: _proposal?.build(),
            renewalType: BuiltValueNullFieldError.checkNotNull(
                renewalType, r'PolicyDetailResponse', 'renewalType'),
            startAt: BuiltValueNullFieldError.checkNotNull(
                startAt, r'PolicyDetailResponse', 'startAt'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'PolicyDetailResponse', 'status'),
            totalPremium: totalPremium.build(),
            vehicle: vehicle.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'certificate';
        _certificate?.build();

        _$failedField = 'coverages';
        coverages.build();

        _$failedField = 'events';
        _events?.build();

        _$failedField = 'instalments';
        instalments.build();
        _$failedField = 'insurer';
        insurer.build();

        _$failedField = 'product';
        product.build();
        _$failedField = 'proposal';
        _proposal?.build();

        _$failedField = 'totalPremium';
        totalPremium.build();
        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PolicyDetailResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
