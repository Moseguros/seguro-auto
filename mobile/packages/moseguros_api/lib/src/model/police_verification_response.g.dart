// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'police_verification_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PoliceVerificationResponse extends PoliceVerificationResponse {
  @override
  final String? certificateNumber;
  @override
  final CertificateCheckStatus? certificateStatus;
  @override
  final DateTime checkedAt;
  @override
  final CoverageType? coverageType;
  @override
  final String? insurerName;
  @override
  final String? plateMasked;
  @override
  final String? policyNumber;
  @override
  final VerificationResult status;
  @override
  final DateTime? validFrom;
  @override
  final DateTime? validTo;
  @override
  final String? vehicleMake;
  @override
  final String? vehicleModel;

  factory _$PoliceVerificationResponse(
          [void Function(PoliceVerificationResponseBuilder)? updates]) =>
      (PoliceVerificationResponseBuilder()..update(updates))._build();

  _$PoliceVerificationResponse._(
      {this.certificateNumber,
      this.certificateStatus,
      required this.checkedAt,
      this.coverageType,
      this.insurerName,
      this.plateMasked,
      this.policyNumber,
      required this.status,
      this.validFrom,
      this.validTo,
      this.vehicleMake,
      this.vehicleModel})
      : super._();
  @override
  PoliceVerificationResponse rebuild(
          void Function(PoliceVerificationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PoliceVerificationResponseBuilder toBuilder() =>
      PoliceVerificationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PoliceVerificationResponse &&
        certificateNumber == other.certificateNumber &&
        certificateStatus == other.certificateStatus &&
        checkedAt == other.checkedAt &&
        coverageType == other.coverageType &&
        insurerName == other.insurerName &&
        plateMasked == other.plateMasked &&
        policyNumber == other.policyNumber &&
        status == other.status &&
        validFrom == other.validFrom &&
        validTo == other.validTo &&
        vehicleMake == other.vehicleMake &&
        vehicleModel == other.vehicleModel;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, certificateNumber.hashCode);
    _$hash = $jc(_$hash, certificateStatus.hashCode);
    _$hash = $jc(_$hash, checkedAt.hashCode);
    _$hash = $jc(_$hash, coverageType.hashCode);
    _$hash = $jc(_$hash, insurerName.hashCode);
    _$hash = $jc(_$hash, plateMasked.hashCode);
    _$hash = $jc(_$hash, policyNumber.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, validFrom.hashCode);
    _$hash = $jc(_$hash, validTo.hashCode);
    _$hash = $jc(_$hash, vehicleMake.hashCode);
    _$hash = $jc(_$hash, vehicleModel.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PoliceVerificationResponse')
          ..add('certificateNumber', certificateNumber)
          ..add('certificateStatus', certificateStatus)
          ..add('checkedAt', checkedAt)
          ..add('coverageType', coverageType)
          ..add('insurerName', insurerName)
          ..add('plateMasked', plateMasked)
          ..add('policyNumber', policyNumber)
          ..add('status', status)
          ..add('validFrom', validFrom)
          ..add('validTo', validTo)
          ..add('vehicleMake', vehicleMake)
          ..add('vehicleModel', vehicleModel))
        .toString();
  }
}

class PoliceVerificationResponseBuilder
    implements
        Builder<PoliceVerificationResponse, PoliceVerificationResponseBuilder> {
  _$PoliceVerificationResponse? _$v;

  String? _certificateNumber;
  String? get certificateNumber => _$this._certificateNumber;
  set certificateNumber(String? certificateNumber) =>
      _$this._certificateNumber = certificateNumber;

  CertificateCheckStatus? _certificateStatus;
  CertificateCheckStatus? get certificateStatus => _$this._certificateStatus;
  set certificateStatus(CertificateCheckStatus? certificateStatus) =>
      _$this._certificateStatus = certificateStatus;

  DateTime? _checkedAt;
  DateTime? get checkedAt => _$this._checkedAt;
  set checkedAt(DateTime? checkedAt) => _$this._checkedAt = checkedAt;

  CoverageType? _coverageType;
  CoverageType? get coverageType => _$this._coverageType;
  set coverageType(CoverageType? coverageType) =>
      _$this._coverageType = coverageType;

  String? _insurerName;
  String? get insurerName => _$this._insurerName;
  set insurerName(String? insurerName) => _$this._insurerName = insurerName;

  String? _plateMasked;
  String? get plateMasked => _$this._plateMasked;
  set plateMasked(String? plateMasked) => _$this._plateMasked = plateMasked;

  String? _policyNumber;
  String? get policyNumber => _$this._policyNumber;
  set policyNumber(String? policyNumber) => _$this._policyNumber = policyNumber;

  VerificationResult? _status;
  VerificationResult? get status => _$this._status;
  set status(VerificationResult? status) => _$this._status = status;

  DateTime? _validFrom;
  DateTime? get validFrom => _$this._validFrom;
  set validFrom(DateTime? validFrom) => _$this._validFrom = validFrom;

  DateTime? _validTo;
  DateTime? get validTo => _$this._validTo;
  set validTo(DateTime? validTo) => _$this._validTo = validTo;

  String? _vehicleMake;
  String? get vehicleMake => _$this._vehicleMake;
  set vehicleMake(String? vehicleMake) => _$this._vehicleMake = vehicleMake;

  String? _vehicleModel;
  String? get vehicleModel => _$this._vehicleModel;
  set vehicleModel(String? vehicleModel) => _$this._vehicleModel = vehicleModel;

  PoliceVerificationResponseBuilder() {
    PoliceVerificationResponse._defaults(this);
  }

  PoliceVerificationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _certificateNumber = $v.certificateNumber;
      _certificateStatus = $v.certificateStatus;
      _checkedAt = $v.checkedAt;
      _coverageType = $v.coverageType;
      _insurerName = $v.insurerName;
      _plateMasked = $v.plateMasked;
      _policyNumber = $v.policyNumber;
      _status = $v.status;
      _validFrom = $v.validFrom;
      _validTo = $v.validTo;
      _vehicleMake = $v.vehicleMake;
      _vehicleModel = $v.vehicleModel;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PoliceVerificationResponse other) {
    _$v = other as _$PoliceVerificationResponse;
  }

  @override
  void update(void Function(PoliceVerificationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PoliceVerificationResponse build() => _build();

  _$PoliceVerificationResponse _build() {
    final _$result = _$v ??
        _$PoliceVerificationResponse._(
          certificateNumber: certificateNumber,
          certificateStatus: certificateStatus,
          checkedAt: BuiltValueNullFieldError.checkNotNull(
              checkedAt, r'PoliceVerificationResponse', 'checkedAt'),
          coverageType: coverageType,
          insurerName: insurerName,
          plateMasked: plateMasked,
          policyNumber: policyNumber,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'PoliceVerificationResponse', 'status'),
          validFrom: validFrom,
          validTo: validTo,
          vehicleMake: vehicleMake,
          vehicleModel: vehicleModel,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
