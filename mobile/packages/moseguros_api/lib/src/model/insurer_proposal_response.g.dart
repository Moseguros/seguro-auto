// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_proposal_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerProposalResponse extends InsurerProposalResponse {
  @override
  final ProposalOrganization? broker;
  @override
  final ProposalChannel channel;
  @override
  final ProposalChecklist checklist;
  @override
  final BuiltList<ProposalCoverage> coverages;
  @override
  final DateTime createdAt;
  @override
  final ProposalDecision? decision;
  @override
  final ProposalDeclarations? declarations;
  @override
  final InsurerProposalDriver driver;
  @override
  final int durationDays;
  @override
  final DateTime endAt;
  @override
  final Date endDate;
  @override
  final DateTime? expiresAt;
  @override
  final String id;
  @override
  final InspectionResponse inspection;
  @override
  final ProposalOrganization? insurer;
  @override
  final InsurerProposalKyc kyc;
  @override
  final String number;
  @override
  final ProposalPolicyholder policyholder;
  @override
  final ProposalPremium premium;
  @override
  final ProposalProduct product;
  @override
  final String quoteId;
  @override
  final ProposalSignature? signature;
  @override
  final BuiltList<InsurerProposalSignature> signatures;
  @override
  final InsurerProposalSla sla;
  @override
  final DateTime startAt;
  @override
  final ProposalStatus status;
  @override
  final DateTime submittedAt;
  @override
  final ProposalSuitability? suitability;
  @override
  final UnderwritingResult? underwriting;
  @override
  final DateTime updatedAt;
  @override
  final InsurerProposalVehicle vehicle;
  @override
  final int version;

  factory _$InsurerProposalResponse(
          [void Function(InsurerProposalResponseBuilder)? updates]) =>
      (InsurerProposalResponseBuilder()..update(updates))._build();

  _$InsurerProposalResponse._(
      {this.broker,
      required this.channel,
      required this.checklist,
      required this.coverages,
      required this.createdAt,
      this.decision,
      this.declarations,
      required this.driver,
      required this.durationDays,
      required this.endAt,
      required this.endDate,
      this.expiresAt,
      required this.id,
      required this.inspection,
      this.insurer,
      required this.kyc,
      required this.number,
      required this.policyholder,
      required this.premium,
      required this.product,
      required this.quoteId,
      this.signature,
      required this.signatures,
      required this.sla,
      required this.startAt,
      required this.status,
      required this.submittedAt,
      this.suitability,
      this.underwriting,
      required this.updatedAt,
      required this.vehicle,
      required this.version})
      : super._();
  @override
  InsurerProposalResponse rebuild(
          void Function(InsurerProposalResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProposalResponseBuilder toBuilder() =>
      InsurerProposalResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProposalResponse &&
        broker == other.broker &&
        channel == other.channel &&
        checklist == other.checklist &&
        coverages == other.coverages &&
        createdAt == other.createdAt &&
        decision == other.decision &&
        declarations == other.declarations &&
        driver == other.driver &&
        durationDays == other.durationDays &&
        endAt == other.endAt &&
        endDate == other.endDate &&
        expiresAt == other.expiresAt &&
        id == other.id &&
        inspection == other.inspection &&
        insurer == other.insurer &&
        kyc == other.kyc &&
        number == other.number &&
        policyholder == other.policyholder &&
        premium == other.premium &&
        product == other.product &&
        quoteId == other.quoteId &&
        signature == other.signature &&
        signatures == other.signatures &&
        sla == other.sla &&
        startAt == other.startAt &&
        status == other.status &&
        submittedAt == other.submittedAt &&
        suitability == other.suitability &&
        underwriting == other.underwriting &&
        updatedAt == other.updatedAt &&
        vehicle == other.vehicle &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, broker.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, checklist.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, decision.hashCode);
    _$hash = $jc(_$hash, declarations.hashCode);
    _$hash = $jc(_$hash, driver.hashCode);
    _$hash = $jc(_$hash, durationDays.hashCode);
    _$hash = $jc(_$hash, endAt.hashCode);
    _$hash = $jc(_$hash, endDate.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, inspection.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, kyc.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, policyholder.hashCode);
    _$hash = $jc(_$hash, premium.hashCode);
    _$hash = $jc(_$hash, product.hashCode);
    _$hash = $jc(_$hash, quoteId.hashCode);
    _$hash = $jc(_$hash, signature.hashCode);
    _$hash = $jc(_$hash, signatures.hashCode);
    _$hash = $jc(_$hash, sla.hashCode);
    _$hash = $jc(_$hash, startAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jc(_$hash, suitability.hashCode);
    _$hash = $jc(_$hash, underwriting.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProposalResponse')
          ..add('broker', broker)
          ..add('channel', channel)
          ..add('checklist', checklist)
          ..add('coverages', coverages)
          ..add('createdAt', createdAt)
          ..add('decision', decision)
          ..add('declarations', declarations)
          ..add('driver', driver)
          ..add('durationDays', durationDays)
          ..add('endAt', endAt)
          ..add('endDate', endDate)
          ..add('expiresAt', expiresAt)
          ..add('id', id)
          ..add('inspection', inspection)
          ..add('insurer', insurer)
          ..add('kyc', kyc)
          ..add('number', number)
          ..add('policyholder', policyholder)
          ..add('premium', premium)
          ..add('product', product)
          ..add('quoteId', quoteId)
          ..add('signature', signature)
          ..add('signatures', signatures)
          ..add('sla', sla)
          ..add('startAt', startAt)
          ..add('status', status)
          ..add('submittedAt', submittedAt)
          ..add('suitability', suitability)
          ..add('underwriting', underwriting)
          ..add('updatedAt', updatedAt)
          ..add('vehicle', vehicle)
          ..add('version', version))
        .toString();
  }
}

class InsurerProposalResponseBuilder
    implements
        Builder<InsurerProposalResponse, InsurerProposalResponseBuilder> {
  _$InsurerProposalResponse? _$v;

  ProposalOrganizationBuilder? _broker;
  ProposalOrganizationBuilder get broker =>
      _$this._broker ??= ProposalOrganizationBuilder();
  set broker(ProposalOrganizationBuilder? broker) => _$this._broker = broker;

  ProposalChannel? _channel;
  ProposalChannel? get channel => _$this._channel;
  set channel(ProposalChannel? channel) => _$this._channel = channel;

  ProposalChecklistBuilder? _checklist;
  ProposalChecklistBuilder get checklist =>
      _$this._checklist ??= ProposalChecklistBuilder();
  set checklist(ProposalChecklistBuilder? checklist) =>
      _$this._checklist = checklist;

  ListBuilder<ProposalCoverage>? _coverages;
  ListBuilder<ProposalCoverage> get coverages =>
      _$this._coverages ??= ListBuilder<ProposalCoverage>();
  set coverages(ListBuilder<ProposalCoverage>? coverages) =>
      _$this._coverages = coverages;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  ProposalDecisionBuilder? _decision;
  ProposalDecisionBuilder get decision =>
      _$this._decision ??= ProposalDecisionBuilder();
  set decision(ProposalDecisionBuilder? decision) =>
      _$this._decision = decision;

  ProposalDeclarationsBuilder? _declarations;
  ProposalDeclarationsBuilder get declarations =>
      _$this._declarations ??= ProposalDeclarationsBuilder();
  set declarations(ProposalDeclarationsBuilder? declarations) =>
      _$this._declarations = declarations;

  InsurerProposalDriverBuilder? _driver;
  InsurerProposalDriverBuilder get driver =>
      _$this._driver ??= InsurerProposalDriverBuilder();
  set driver(InsurerProposalDriverBuilder? driver) => _$this._driver = driver;

  int? _durationDays;
  int? get durationDays => _$this._durationDays;
  set durationDays(int? durationDays) => _$this._durationDays = durationDays;

  DateTime? _endAt;
  DateTime? get endAt => _$this._endAt;
  set endAt(DateTime? endAt) => _$this._endAt = endAt;

  Date? _endDate;
  Date? get endDate => _$this._endDate;
  set endDate(Date? endDate) => _$this._endDate = endDate;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  InspectionResponseBuilder? _inspection;
  InspectionResponseBuilder get inspection =>
      _$this._inspection ??= InspectionResponseBuilder();
  set inspection(InspectionResponseBuilder? inspection) =>
      _$this._inspection = inspection;

  ProposalOrganizationBuilder? _insurer;
  ProposalOrganizationBuilder get insurer =>
      _$this._insurer ??= ProposalOrganizationBuilder();
  set insurer(ProposalOrganizationBuilder? insurer) =>
      _$this._insurer = insurer;

  InsurerProposalKycBuilder? _kyc;
  InsurerProposalKycBuilder get kyc =>
      _$this._kyc ??= InsurerProposalKycBuilder();
  set kyc(InsurerProposalKycBuilder? kyc) => _$this._kyc = kyc;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  ProposalPolicyholderBuilder? _policyholder;
  ProposalPolicyholderBuilder get policyholder =>
      _$this._policyholder ??= ProposalPolicyholderBuilder();
  set policyholder(ProposalPolicyholderBuilder? policyholder) =>
      _$this._policyholder = policyholder;

  ProposalPremiumBuilder? _premium;
  ProposalPremiumBuilder get premium =>
      _$this._premium ??= ProposalPremiumBuilder();
  set premium(ProposalPremiumBuilder? premium) => _$this._premium = premium;

  ProposalProductBuilder? _product;
  ProposalProductBuilder get product =>
      _$this._product ??= ProposalProductBuilder();
  set product(ProposalProductBuilder? product) => _$this._product = product;

  String? _quoteId;
  String? get quoteId => _$this._quoteId;
  set quoteId(String? quoteId) => _$this._quoteId = quoteId;

  ProposalSignatureBuilder? _signature;
  ProposalSignatureBuilder get signature =>
      _$this._signature ??= ProposalSignatureBuilder();
  set signature(ProposalSignatureBuilder? signature) =>
      _$this._signature = signature;

  ListBuilder<InsurerProposalSignature>? _signatures;
  ListBuilder<InsurerProposalSignature> get signatures =>
      _$this._signatures ??= ListBuilder<InsurerProposalSignature>();
  set signatures(ListBuilder<InsurerProposalSignature>? signatures) =>
      _$this._signatures = signatures;

  InsurerProposalSlaBuilder? _sla;
  InsurerProposalSlaBuilder get sla =>
      _$this._sla ??= InsurerProposalSlaBuilder();
  set sla(InsurerProposalSlaBuilder? sla) => _$this._sla = sla;

  DateTime? _startAt;
  DateTime? get startAt => _$this._startAt;
  set startAt(DateTime? startAt) => _$this._startAt = startAt;

  ProposalStatus? _status;
  ProposalStatus? get status => _$this._status;
  set status(ProposalStatus? status) => _$this._status = status;

  DateTime? _submittedAt;
  DateTime? get submittedAt => _$this._submittedAt;
  set submittedAt(DateTime? submittedAt) => _$this._submittedAt = submittedAt;

  ProposalSuitabilityBuilder? _suitability;
  ProposalSuitabilityBuilder get suitability =>
      _$this._suitability ??= ProposalSuitabilityBuilder();
  set suitability(ProposalSuitabilityBuilder? suitability) =>
      _$this._suitability = suitability;

  UnderwritingResultBuilder? _underwriting;
  UnderwritingResultBuilder get underwriting =>
      _$this._underwriting ??= UnderwritingResultBuilder();
  set underwriting(UnderwritingResultBuilder? underwriting) =>
      _$this._underwriting = underwriting;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  InsurerProposalVehicleBuilder? _vehicle;
  InsurerProposalVehicleBuilder get vehicle =>
      _$this._vehicle ??= InsurerProposalVehicleBuilder();
  set vehicle(InsurerProposalVehicleBuilder? vehicle) =>
      _$this._vehicle = vehicle;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  InsurerProposalResponseBuilder() {
    InsurerProposalResponse._defaults(this);
  }

  InsurerProposalResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _broker = $v.broker?.toBuilder();
      _channel = $v.channel;
      _checklist = $v.checklist.toBuilder();
      _coverages = $v.coverages.toBuilder();
      _createdAt = $v.createdAt;
      _decision = $v.decision?.toBuilder();
      _declarations = $v.declarations?.toBuilder();
      _driver = $v.driver.toBuilder();
      _durationDays = $v.durationDays;
      _endAt = $v.endAt;
      _endDate = $v.endDate;
      _expiresAt = $v.expiresAt;
      _id = $v.id;
      _inspection = $v.inspection.toBuilder();
      _insurer = $v.insurer?.toBuilder();
      _kyc = $v.kyc.toBuilder();
      _number = $v.number;
      _policyholder = $v.policyholder.toBuilder();
      _premium = $v.premium.toBuilder();
      _product = $v.product.toBuilder();
      _quoteId = $v.quoteId;
      _signature = $v.signature?.toBuilder();
      _signatures = $v.signatures.toBuilder();
      _sla = $v.sla.toBuilder();
      _startAt = $v.startAt;
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _suitability = $v.suitability?.toBuilder();
      _underwriting = $v.underwriting?.toBuilder();
      _updatedAt = $v.updatedAt;
      _vehicle = $v.vehicle.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProposalResponse other) {
    _$v = other as _$InsurerProposalResponse;
  }

  @override
  void update(void Function(InsurerProposalResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProposalResponse build() => _build();

  _$InsurerProposalResponse _build() {
    _$InsurerProposalResponse _$result;
    try {
      _$result = _$v ??
          _$InsurerProposalResponse._(
            broker: _broker?.build(),
            channel: BuiltValueNullFieldError.checkNotNull(
                channel, r'InsurerProposalResponse', 'channel'),
            checklist: checklist.build(),
            coverages: coverages.build(),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'InsurerProposalResponse', 'createdAt'),
            decision: _decision?.build(),
            declarations: _declarations?.build(),
            driver: driver.build(),
            durationDays: BuiltValueNullFieldError.checkNotNull(
                durationDays, r'InsurerProposalResponse', 'durationDays'),
            endAt: BuiltValueNullFieldError.checkNotNull(
                endAt, r'InsurerProposalResponse', 'endAt'),
            endDate: BuiltValueNullFieldError.checkNotNull(
                endDate, r'InsurerProposalResponse', 'endDate'),
            expiresAt: expiresAt,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerProposalResponse', 'id'),
            inspection: inspection.build(),
            insurer: _insurer?.build(),
            kyc: kyc.build(),
            number: BuiltValueNullFieldError.checkNotNull(
                number, r'InsurerProposalResponse', 'number'),
            policyholder: policyholder.build(),
            premium: premium.build(),
            product: product.build(),
            quoteId: BuiltValueNullFieldError.checkNotNull(
                quoteId, r'InsurerProposalResponse', 'quoteId'),
            signature: _signature?.build(),
            signatures: signatures.build(),
            sla: sla.build(),
            startAt: BuiltValueNullFieldError.checkNotNull(
                startAt, r'InsurerProposalResponse', 'startAt'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'InsurerProposalResponse', 'status'),
            submittedAt: BuiltValueNullFieldError.checkNotNull(
                submittedAt, r'InsurerProposalResponse', 'submittedAt'),
            suitability: _suitability?.build(),
            underwriting: _underwriting?.build(),
            updatedAt: BuiltValueNullFieldError.checkNotNull(
                updatedAt, r'InsurerProposalResponse', 'updatedAt'),
            vehicle: vehicle.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'InsurerProposalResponse', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'broker';
        _broker?.build();

        _$failedField = 'checklist';
        checklist.build();
        _$failedField = 'coverages';
        coverages.build();

        _$failedField = 'decision';
        _decision?.build();
        _$failedField = 'declarations';
        _declarations?.build();
        _$failedField = 'driver';
        driver.build();

        _$failedField = 'inspection';
        inspection.build();
        _$failedField = 'insurer';
        _insurer?.build();
        _$failedField = 'kyc';
        kyc.build();

        _$failedField = 'policyholder';
        policyholder.build();
        _$failedField = 'premium';
        premium.build();
        _$failedField = 'product';
        product.build();

        _$failedField = 'signature';
        _signature?.build();
        _$failedField = 'signatures';
        signatures.build();
        _$failedField = 'sla';
        sla.build();

        _$failedField = 'suitability';
        _suitability?.build();
        _$failedField = 'underwriting';
        _underwriting?.build();

        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerProposalResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
